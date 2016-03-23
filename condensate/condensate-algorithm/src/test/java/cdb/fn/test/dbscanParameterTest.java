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
import cdb.common.lang.LoggerUtil;

import org.apache.commons.io.IOUtils;
import org.springframework.util.StopWatch;

import cdb.common.lang.ClusterHelper;
import cdb.common.lang.DistanceUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.FileUtil;
import cdb.common.model.Cluster;
import cdb.common.model.Point;
import cdb.common.model.RegionInfoVO;
import cdb.common.model.Samples;
import cdb.exp.qc.AbstractDetecting;
import cdb.ml.clustering.DBSCANBasic;
import cdb.ml.clustering.KMeansPlusPlusUtil;
import cdb.ml.qc.QualityControllHelper;

public class dbscanParameterTest extends AbstractDetecting {

	public static void main(String[] args) {

		final int type = DistanceUtil.SQUARE_EUCLIDEAN_DISTANCE;
		String elbowFile = "C:/Dataset/SSMI/elbowTest/elbow";
		
		try {
			// load features for every sample (region observations)
			//String fileName = "C:/Dataset/SSMI/ClassificationDataset/n19v_2_2_ORG/159_81";
			String fileName = "/Users/mira67/Documents/101_125";//101_125,0_105

			Queue<RegionInfoVO> regnList = readRegionInfoStep(fileName);
			// making clustering samples
			int pDimen = 12;
			Samples dataSample = new Samples(regnList.size(), pDimen);
			List<String> regnDateStr = new ArrayList<String>();
			
			QualityControllHelper.normalizeFeatures(dataSample, regnList, regnDateStr, "MONTHLY");

			final double eps = 17;
			final int minPts = 6;
			StopWatch stopWatch = null;
			
			LoggerUtil.info(logger, "DBSCAN Test: Working");
			//reduce dimension
			final int len = dataSample.length()[0];            
			int[] feaId = {0,1,3,5,6,7,8,9};//non-zero features
			for (int i = 0; i < len; i++){
				Point tempP = new Point(8);
				for (int j = 0; j < 8; j++) {
					tempP.setValue(j, dataSample.getPoint(i).getValue(feaId[j]));
				}
				dataSample.setPoint(i,tempP);
			}
			dataSample.setDimension(8);

			List<Cluster> clusters = DBSCANBasic.cluster(dataSample, eps, minPts, type);
			
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
