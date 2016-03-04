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

import cdb.common.lang.DistanceUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.FileUtil;
import cdb.common.lang.LoggerUtil;
import cdb.common.model.Cluster;
import cdb.common.model.RegionAnomalyInfoVO;
import cdb.common.model.RegionInfoVO;
import cdb.common.model.Samples;
import cdb.ml.clustering.KMeansPlusPlusUtil;
import cdb.ml.qc.QualityControllHelper;

public class elbowTest {

	public static void main(String[] args) {

		final int type = DistanceUtil.SQUARE_EUCLIDEAN_DISTANCE;
		String elbowFile = "C:/Dataset/SSMI/elbowTest/elbow";
		try {
			// load features for every sample (region observations)
			String fileName = "C:/Dataset/SSMI/ClassificationDataset/n19v_2_2_ORG/159_81";
			Queue<RegionInfoVO> regnList = readRegionInfoStep(fileName);
			// making clustering samples
			RegionInfoVO pivot = regnList.peek();
			int pDimen = 12;
			Samples dataSample = new Samples(regnList.size(), pDimen);
			List<String> regnDateStr = new ArrayList<String>();
			QualityControllHelper.normalizeFeatures(dataSample, regnList, regnDateStr, "MONTHLY");//z-score
			
			final int maxClusterNum = 50;
			List<Double> costError = new ArrayList<Double>();
			
			for (int i = 0; i < maxClusterNum; i++){
				costError.add(i,(double) 0);
			}
			
			double dist = 0;//
			int m = 0;//index if error array
		    for (int i = 2; i<maxClusterNum+1;i++)
		    {
				Cluster[] roughClusters = KMeansPlusPlusUtil.cluster(dataSample, i, 20,
						DistanceUtil.SQUARE_EUCLIDEAN_DISTANCE);
				//compute cost function of K-Means results
				
				for (Cluster cl:roughClusters) {
					for (int j = 0; j < cl.getList().size(); j++){
						dist = DistanceUtil.distance(dataSample.getPointRef(cl.getList().get(j)),cl.centroid(dataSample), type);
						costError.set(m, dist+costError.get(m));//accumulate total errors
					
					}
                }//end of K clusters loop
				m++;
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
