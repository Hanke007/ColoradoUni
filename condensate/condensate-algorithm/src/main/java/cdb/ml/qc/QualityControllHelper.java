package cdb.ml.qc;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.math3.stat.StatUtils;

import cdb.common.datastructure.ListInMap;
import cdb.common.lang.DateUtil;
import cdb.common.model.Point;
import cdb.common.model.RegionInfoVO;
import cdb.common.model.Samples;

/**
 * 
 * @author Chao Chen
 * @version $Id: RegionInfoVOHelper.java, v 0.1 Nov 3, 2015 11:42:48 AM chench
 *          Exp $
 */
public final class QualityControllHelper {

	/**
	 * forbidden construction
	 */
	private QualityControllHelper() {

	}

	/**
	 * feature pick-out converter
	 * 
	 * @param one
	 *            resource object
	 * @return
	 */
	public static Point make12Features(RegionInfoVO one) {
		Point p = new Point(12);
		int pSeq = 0;

		double gradRowSum = 0;
		if (one.getGradRow() != null)
			for (double gradRowVal : one.getGradRow()) {
				gradRowSum += gradRowVal;
				// dataSample.setValue(dSeq, pSeq++, gradRowVal);
			}
		p.setValue(pSeq++, gradRowSum);

		// gradient along column
		double gradColSum = 0;
		if (one.getGradCol() != null)
			for (double gradColVal : one.getGradCol()) {
				gradColSum += gradColVal;
				// dataSample.setValue(dSeq, pSeq++, gradColVal);
			}
		p.setValue(pSeq++, gradColSum);

		// Contextual: temporal gradients
		// for (double tGradConVal : one.gettGradCon()) {
		// dataSample.setValue(dSeq, pSeq++, tGradConVal);
		// }

		p.setValue(pSeq++, 0.0d);
		if (one.gettGradCon() != null) {
			p.setValue(pSeq++, one.gettGradCon().getValue(2)); // dffMean[-1]
			// p.setValue(pSeq++, one.gettGradCon().getValue(3)); // dffMean[+1]

			p.setValue(pSeq++, 0.0d);
			p.setValue(pSeq++, one.gettGradCon().getValue(4)); // dffSd[-1]
			// p.setValue(pSeq++, one.gettGradCon().getValue(5)); // dffSd[+1]
		}

		// Contextual: spatial correlations
		if (one.getsCorrCon() != null) {
			double sCorrSum = 0;
			for (double sCorrConVal : one.getsCorrCon()) {
				if (Double.isNaN(sCorrConVal)) {
					continue;
				}
				sCorrSum += sCorrConVal;
				// dataSample.setValue(dSeq, pSeq++, sCorrConVal);
			}
			p.setValue(pSeq++, sCorrSum);
		}

		// Contextual: spatial differences
		if (one.getsDiffCon() != null) {
			double sDiffSum = 0;
			for (double sDiffConVal : one.getsDiffCon()) {
				sDiffSum += sDiffConVal;
				// dataSample.setValue(dSeq, pSeq++, sDiffConVal);
			}
			p.setValue(pSeq++, sDiffSum);
		}

		// dataSample.setValue(dSeq, pSeq++, one.getEntropy());
		// dataSample.setValue(dSeq, pSeq++, one.getGradMean());
		p.setValue(pSeq++, one.getMean());
		p.setValue(pSeq++, one.getSd());

		p.setValue(pSeq++, 0.0d);
		// p.setValue(pSeq++, one.getrIndx());
		p.setValue(pSeq++, 0.0d);
		// p.setValue(pSeq++, one.getcIndx());

		return p;
	}

	public static void normalizeFeatures(Samples dataSample, Queue<RegionInfoVO> regnList, List<String> regnDateStr,
			String filterCategory) throws ParseException {
		// assign the data to its categories bundle
		Calendar cal = Calendar.getInstance();
		ListInMap<Integer, RegionInfoVO> dRep = new ListInMap<Integer, RegionInfoVO>();
		while (!regnList.isEmpty()) {
			RegionInfoVO one = regnList.poll();

			int key = -1;
			cal.setTime(DateUtil.parse(one.getDateStr(), DateUtil.SHORT_FORMAT));
			switch (filterCategory) {
			case "DAILY":
				key = cal.get(Calendar.DAY_OF_YEAR);
				break;
			case "MONTHLY":
				key = cal.get(Calendar.MONTH);
				break;
			case "SEASONLY":
				key = cal.get(Calendar.MONTH) / 4;
				break;
			default:
				break;
			}
			dRep.put(key, one);
		}

		// normalize the data in every bundle
		int dsSeq = 0;
		Set<Integer> keys = dRep.keySet();
		for (Integer key : keys) {
			// normalize data
			List<RegionInfoVO> data = dRep.get(key);
			List<Point> rFeatures = new ArrayList<Point>();
			for (RegionInfoVO regnInfoVO : data) {
				rFeatures.add(QualityControllHelper.make12Features(regnInfoVO));//
				regnDateStr.add(regnInfoVO.getDateStr());
			}
			//normalization(rFeatures);

			// fill in Samples
			for (Point feature : rFeatures) {
				dataSample.setPoint(dsSeq, feature);
				dsSeq++;
			}
		}
	}

	public static void normalization(List<Point> rFeatures) {
		if (rFeatures == null || rFeatures.isEmpty()) {
			return;
		}

		int fNum = rFeatures.get(0).dimension();
		for (int fIndx = 0; fIndx < fNum; fIndx++) {
			int pNum = rFeatures.size();
			double[] fVals = new double[pNum];
			for (int pIndx = 0; pIndx < pNum; pIndx++) {
				fVals[pIndx] = rFeatures.get(pIndx).getValue(fIndx);
			}

			if (StatUtils.variance(fVals) == 0.0d) {
				// check whether the data is constant
				continue;
			}
			double[] nVals = StatUtils.normalize(fVals);
			for (int pIndx = 0; pIndx < pNum; pIndx++) {
				rFeatures.get(pIndx).setValue(fIndx, nVals[pIndx]);
			}
		}
	}

}
