package cdb.fn.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.io.IOUtils;

import cdb.common.lang.ClusterHelper;
import cdb.common.lang.DistanceUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.FileUtil;
import cdb.common.model.Cluster;
import cdb.common.model.RegionInfoVO;
import cdb.common.model.Samples;
import cdb.ml.clustering.KMeansPlusPlusUtil;
import cdb.ml.qc.QualityControllHelper;

/**
 * Author: Qi LIU
 * Elbow, Alpha boundary testing code
 * */

public class elbowTest {

	public static void main(String[] args) {

		final int type = DistanceUtil.SQUARE_EUCLIDEAN_DISTANCE;
		String elbowFile = "C:/Dataset/SSMI/elbowTest/elbow";
		String feaFile = "C:/Dataset/SSMI/elbowTest/visFea";
		String alpFile = "C:/Dataset/SSMI/elbowTest/alpha";
		String rawfeaFile = "C:/Dataset/SSMI/elbowTest/rawfea";
		try {
			// load features for every sample (region observations)
			String fileName = "C:/Dataset/SSMI/ClassificationDataset/n19v_2_2_ORG/159_81";
			Queue<RegionInfoVO> regnList = readRegionInfoStep(fileName);
			// making clustering samples
			int pDimen = 12;
			Samples dataSample = new Samples(regnList.size(), pDimen);
			Samples alpresult = new Samples(regnList.size(), 8);//1 id + 5 labels at different alpha
			List<String> regnDateStr = new ArrayList<String>();
			QualityControllHelper.normalizeFeatures(dataSample, regnList, regnDateStr, "MONTHLY");//z-score, add a not normalized option
			
			//re-write samples to a file for visual analysis, not-normalized
			for (int pt = 0; pt < dataSample.length()[0]; pt++){
				FileUtil.writeAsAppendWithDirCheck(rawfeaFile, dataSample.getPoint(pt).toStringSimple()+"\n");
			}
			
			final int maxClusterNum = 50;
			final float maxAlpha = 4.5f;
			final int maxIter  =5;
			
			List<Double> costError = new ArrayList<Double>();
			
			for (int i = 0; i < maxClusterNum; i++){
				costError.add(i,(double) 0);
			}
			
			double dist = 0;//
			int m = 0;//index if error array
		    for (int i = 2; i<maxClusterNum+1;i++)//at least two clusters
		    {
//				Cluster[] roughClusters = KMeansPlusPlusUtil.cluster(dataSample, i, 20,
//						DistanceUtil.SQUARE_EUCLIDEAN_DISTANCE);
				
				//compute cost function of K-Means results
//				for (Cluster cl:roughClusters) {
//					for (int j = 0; j < cl.getList().size(); j++){
//						dist = DistanceUtil.distance(dataSample.getPointRef(cl.getList().get(j)),cl.centroid(dataSample), type);
//						costError.set(m, dist+costError.get(m));//accumulate total errors
//					
//					}
//                }//end of K clusters loop
//				m++;
				
				//Testing of different alpha during merging
				int alpID = 1;
				for (float alp = 1; alp < maxAlpha; alp = alp + 0.5f) {
					
						Cluster[] roughClusters = KMeansPlusPlusUtil.cluster(dataSample, i, 20,
							DistanceUtil.SQUARE_EUCLIDEAN_DISTANCE);
						Cluster[] newClusters = ClusterHelper.mergeAdjacentCluster(dataSample, roughClusters,
						DistanceUtil.SQUARE_EUCLIDEAN_DISTANCE, alp, maxIter);
						
						int k = 0;// from 0 to number of clusters
						for (Cluster ncluster : newClusters) {
							for (int idx : ncluster.getList()) {
								System.out.println(idx);
								alpresult.setValue(idx, alpID, k);
								alpresult.setValue(idx, 0, idx);
							}
							k++;// update cluster index
						}
						alpID++;
				}
				
				for (int pt = 0; pt < dataSample.length()[0]; pt++){
					FileUtil.writeAsAppendWithDirCheck(alpFile, alpresult.getPoint(pt).toStringSimple()+"\n");
				}
				
				alpID = 10;
				
		    }//end of compute cost error
		    
		    //write cost error to file
		    FileUtil.writeAsAppendWithDirCheck(elbowFile, costError.toString());
		    
		} catch (ParseException e) {
			ExceptionUtil.caught(e, "Date format parsing error.");
		}
	}
	
	protected static Queue<RegionInfoVO> readRegionInfoStep(String fileName) {
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

}
