package cdb.ml.qc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

import cdb.common.lang.ClusterHelper;
import cdb.common.lang.DistanceUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.FileUtil;
import cdb.common.lang.LoggerUtil;
import cdb.common.lang.StringUtil;
import cdb.common.model.Cluster;
import cdb.common.model.Point;
import cdb.common.model.RegionAnomalyInfoVO;
import cdb.common.model.RegionInfoVO;
import cdb.common.model.Samples;
import cdb.ml.clustering.ExpectationMaximumUtil;
import cdb.ml.clustering.KMeansPlusPlusUtil;

/**
 * 
 * @author Chao Chen
 * @version $Id: DefaultQualityControllThread.java, v 0.1 Oct 27, 2015 10:22:58
 *          AM chench Exp $
 */
public class DefaultQualityControllThread extends AbstractQualityControllThread {

	/** mutex object */
	protected static Object ANOMALY_MUTEX = new Object();
	/** the buffer of the Region anomaly object */
	protected static List<RegionAnomalyInfoVO> raInfoBuffer = new ArrayList<RegionAnomalyInfoVO>();

	protected static void save(List<RegionAnomalyInfoVO> raArr, String resultFile) {
		synchronized (ANOMALY_MUTEX) {
			raInfoBuffer.addAll(raArr);

			if (raInfoBuffer.size() >= 10) {//1000*1000
				StringBuilder strBuffer = new StringBuilder();
				for (RegionAnomalyInfoVO one : raInfoBuffer) {
					strBuffer.append(one.toString()).append('\n');
				}
				raInfoBuffer.clear();
				FileUtil.writeAsAppendWithDirCheck(resultFile, strBuffer.toString());
			}
		}
	}

	public static void flush(String resultFile) {
		synchronized (ANOMALY_MUTEX) {
			StringBuilder strBuffer = new StringBuilder();
			for (RegionAnomalyInfoVO one : raInfoBuffer) {
				strBuffer.append(one.toString()).append('\n');
			}
			raInfoBuffer.clear();
			FileUtil.writeAsAppendWithDirCheck(resultFile, strBuffer.toString());
		}
	}

	/**
	 * @param alpha
	 *            the tolerance of the merging process
	 * @param maxIter
	 *            the maximum number of iterations in clustering process
	 * @param maxClusterNum
	 *            the maximum number of the resulting clusters
	 * @param potentialMaliciousRatio
	 *            the potential ratio of the malicious data
	 * @param regionHeight
	 *            the row numbers of every region
	 * @param regionWeight
	 *            the column numbers of every region
	 */
	public DefaultQualityControllThread(double alpha, int maxIter, int maxClusterNum, double potentialMaliciousRatio,
			int regionHeight, int regionWeight, boolean needSaveData) {
		super(alpha, maxIter, maxClusterNum, potentialMaliciousRatio, regionHeight, regionWeight, needSaveData);
	}

	/**
	 * @param configFileName
	 *            the file path of the configuration file
	 */
	public DefaultQualityControllThread(String configFileName) {
		super(configFileName);

	}

	/**
	 * @see cdb.ml.qc.AbstractQualityControllThread#run()
	 */
	@Override
	public void run() {// configuration file zconfigqc.properties
		Entry<String, List<String>> dEntry = null;
		while ((dEntry = task()) != null) {
			String resultFile = dEntry.getKey();// result
			List<String> fileNames = dEntry.getValue();// source
			List<RegionAnomalyInfoVO> raArr = new ArrayList<RegionAnomalyInfoVO>();
			for (String fileName : fileNames) {
				raArr.addAll(innerDectectoin(fileName));// detect anomaly
			}
			save(raArr, resultFile);
		}
	}

	/**
	 * pattern detection function
	 * 
	 * @param fileName
	 *            the file to store the data
	 * @return
	 */
	protected List<RegionAnomalyInfoVO> innerDectectoin(String fileName) {
		List<RegionAnomalyInfoVO> resultArr = new ArrayList<RegionAnomalyInfoVO>();
		try {
			// load features for every sample (region observations)
			Queue<RegionInfoVO> regnList = readRegionInfoStep(fileName);
			if (regnList.isEmpty()) {
				return new ArrayList<RegionAnomalyInfoVO>();
			} else if (regnList.size() < maxClusterNum * 50) {
				LoggerUtil.warn(logger,
						"Lack of data : " + regnList.size() + "\t" + fileName.substring(fileName.lastIndexOf('/')));
				return new ArrayList<RegionAnomalyInfoVO>();
			}

			// making clustering samples
			RegionInfoVO pivot = regnList.peek();
			// int pDimen = 6 + pivot.getDistribution().dimension() +
			// pivot.getGradCol().dimension()
			// + pivot.getGradRow().dimension() +
			// pivot.gettGradCon().dimension()
			// + pivot.getsCorrCon().dimension() +
			// pivot.getsDiffCon().dimension();
			int pDimen = 12;
			int rRIndx = pivot.getrIndx();
			int cRIndx = pivot.getcIndx();
			Samples dataSample = new Samples(regnList.size(), pDimen);
			List<String> regnDateStr = new ArrayList<String>();
			QualityControllHelper.normalizeFeatures(dataSample, regnList, regnDateStr, 
					filterCategory);// regnDateStr, date attribute, one to one
			if (needSaveData) {// normalized data save
				persistFeatureStep(fileName, dataSample, regnDateStr);
			}

			resultArr = discoverPatternStep(dataSample, fileName, regnDateStr, rRIndx, cRIndx);
		} catch (ParseException e) {
			ExceptionUtil.caught(e, "Date format parsing error.");
		}

		// rare pattern detection
		return resultArr;
	}

	protected void persistFeatureStep(String fileName, Samples dataSample, List<String> regnDateStr) {
		StringBuilder stringBuilder = new StringBuilder();
		int dSize = dataSample.length()[0];
		for (int dIndx = 0; dIndx < dSize; dIndx++) {
			Point p = dataSample.getPointRef(dIndx);
			String dataStr = regnDateStr.get(dIndx);
			stringBuilder.append(p.toString()).append("# ").append(dataStr).append('\n');
		}

		// replace freqId_rWidth_rHeight_ORG to freqId_rWidth_rHeight
		int lSlant = fileName.lastIndexOf('/');
		int lSecSlant = fileName.substring(0, lSlant).lastIndexOf('/');
		String lDirName = fileName.substring(lSecSlant + 1, lSlant);
		String sFileName = StringUtil.replace(fileName, lDirName, lDirName.substring(0, lDirName.length() - 4));
		FileUtil.existDirAndMakeDir(sFileName);
		FileUtil.write(sFileName, stringBuilder.toString());
	}

	protected Queue<RegionInfoVO> readRegionInfoStep(String fileName) {
		Queue<RegionInfoVO> regnList = new LinkedList<RegionInfoVO>();
		if (!FileUtil.exists(fileName)) {
			return regnList;
		}

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(fileName));

			String line = null;
			while ((line = reader.readLine()) != null) {
				RegionInfoVO regnVO = RegionInfoVO.parseOf(line);
				regnList.add(regnVO);
			}
		} catch (FileNotFoundException e) {
			ExceptionUtil.caught(e, "无法找到对应的加载文件: " + fileName);
		} catch (IOException e) {
			ExceptionUtil.caught(e, "读取文件发生异常，校验文件格式");
		} finally {
			IOUtils.closeQuietly(reader);
		}

		return regnList;
	}

	protected List<RegionAnomalyInfoVO> discoverPatternStep(Samples dataSample, String fileName,
			List<String> regnDateStr, int rRIndx, int cRIndx) {

		// for mid results analysis
		final int len = dataSample.length()[0];// number of samples
		final int resDim = 1 + 1 + 1 + 1 + 1; // + dataSample.length()[1];//date(0)
											// + cluster label(1) + merge
											// label(2) + after boundary optimization (3)+ outlier label(4)
		Samples midresult = new Samples(len, resDim);
		final String midresultDir = "C:/Dataset/SSMI/midresults/";
		/*
		 * mid result analysis: step 0: initialize with sample value of dim and
		 * grab datestr
		 */
		final int dateID = 0, clusterLabelID = 1, clusterMergeID = 2, clusterBoundaryOptID = 3, outlierID = 4;
		int k = 0;
		for (String dt : regnDateStr) {
			midresult.setValue(k, dateID, Integer.parseInt(dt));
			k++;
		}

		// clustering with k-means
//		Cluster[] roughClusters = KMeansPlusPlusUtil.cluster(dataSample, maxClusterNum, 20,
//				DistanceUtil.SQUARE_EUCLIDEAN_DISTANCE);
		
		// clustering with EM
		// remove unused feature columns to ensure positive definite cov-matrix
		int[] feaId = {0,1,3,5,6,7,8,9};//non-zero features
		for (int i = 0; i < len; i++){
			Point tempP = new Point(8);
			for (int j = 0; j < 8; j++) {
				tempP.setValue(j, dataSample.getPoint(i).getValue(feaId[j]));
			}
			dataSample.setPoint(i,tempP);
		}
		dataSample.setDimension(8);
		
		// set feature length
		Cluster[] roughClusters = ExpectationMaximumUtil.cluster(dataSample, maxClusterNum, maxIter);
		List<Integer> validClusterId = new ArrayList<Integer>();
		int m = 0, n = 0;
		for (Cluster rcluster : roughClusters) {
			if (rcluster.getList().size() > 0) {
				validClusterId.add(m, n);
				m++;
			}
			n++;//update cluster id
		}
		
		int validLength = validClusterId.size();//how many valid clusters generated from EM
		Cluster[] emClusters = new Cluster[validLength];
        for (int i = 0; i < validLength; i++) {
            emClusters[i] = roughClusters[validClusterId.get(i)];
        }
		
		/*
		 * mid result analysis: step 1: grab samples cluster label, use
		 * [1:number of clusters]
		 */
		k = 0;// from 0 to number of clusters
		for (Cluster rcluster : emClusters) {
			for (int idx : rcluster.getList()) {
				midresult.setValue(idx, clusterLabelID, k);
			}
			k++;// update cluster index
		}

		// merge step - for EM, need update method
		Cluster[] newClusters = ClusterHelper.mergeAdjacentCluster(dataSample, emClusters,
				DistanceUtil.SQUARE_EUCLIDEAN_DISTANCE, alpha, 5);//maxIter
		
		/*
		 * mid result analysis: step 2: grab samples merge label, [1:number of
		 * clusters]
		 */
		k = 0;// from 0 to number of clusters
		for (Cluster ncluster : newClusters) {
			for (int idx : ncluster.getList()) {
				midresult.setValue(idx, clusterMergeID, k);
			}
			k++;// update cluster index
		}
		
		//Cluster[] optClusters = ClusterHelper.boundaryOptimizeCluster(dataSample, newClusters);
		
		/*
		 * mid result analysis: step 2: grab samples merge label, [1:number of
		 * clusters]
		 */
//		k = 0;// from 0 to number of clusters
//		for (Cluster optcluster : optClusters) {
//			for (int idx : optcluster.getList()) {
//				midresult.setValue(idx, clusterBoundaryOptID, k);
//			}
//			k++;// update cluster index
//		}

		// identification
		int clusterNum = newClusters.length;
		double[] sizeTable = new double[clusterNum];
		for (int i = 0; i < clusterNum; i++) {
			sizeTable[i] = newClusters[i].getList().size();
		}
		LoggerUtil.info(logger,
				fileName.substring(fileName.lastIndexOf('/')) + " resulting clusters: " + Arrays.toString(sizeTable));

		// total number * 1%, 10%, total number: total observations
		int curNum = 0;
		int totalNum = regnDateStr.size();
		int newClusterNum = newClusters.length;
		List<RegionAnomalyInfoVO> raArr = new ArrayList<RegionAnomalyInfoVO>();
		for (int i = 0; i < newClusterNum; i++) {
			Cluster cluster = newClusters[findMinimum(sizeTable)];

			curNum += cluster.getList().size();
			if (curNum > totalNum * potentialMaliciousRatio) {
				break;
			}

			// result presentation - write to files
			for (int dIndx : cluster.getList()) {
				RegionAnomalyInfoVO raVO = new RegionAnomalyInfoVO();
				raVO.setDateStr(regnDateStr.get(dIndx));
				raVO.setHeight(regionHeight);
				raVO.setWidth(regionWeight);
				raVO.setX(rRIndx * regionHeight);
				raVO.setY(cRIndx * regionWeight);
				raVO.setdPoint(dataSample.getPointRef(dIndx));
				raArr.add(raVO);

				/*
				 * mid result analysis: step 3: grab samples outlier label:
				 * 1-outlier, 0-normal
				 */
				midresult.setValue(dIndx, outlierID, 1);
				LoggerUtil.debug(logger,"Outlier: "+rRIndx+','+cRIndx+':'+dIndx);// check
				
			}
		}

		/* record midresult to file, one onject one file */

		// writeAsAppendWithDirCheck(String file, String context)
		String midrfile = midresultDir + rRIndx + '_' + cRIndx;
		StringBuilder midout = new StringBuilder();
		for (int i = 0; i < len; i++) {
			midout.setLength(0);//reset stringbuilder
			for (int j = 0; j < resDim; j++){
				if (j>0){
					midout.append(",");
				}
				midout.append(midresult.getValue(i, j));
			}
			midout.append("\n");
			FileUtil.writeAsAppendWithDirCheck(midrfile, midout.toString());
		}
		return raArr;
	}

	/**
	 * find the index of the data with the minimum value
	 * 
	 * @param sizeTable
	 *            the array of distance
	 * @return the index of the data with the maximum anomaly scores
	 */
	protected int findMinimum(double[] sizeTable) {
		double min = Double.MAX_VALUE;
		int pivot = -1;
		// ignoring side effects
		for (int i = 0; i < sizeTable.length; i++) {
			double val = sizeTable[i];

			if (min > val) {
				min = val;
				pivot = i;
			}
		}

		sizeTable[pivot] = Double.MAX_VALUE;
		return pivot;
	}

}