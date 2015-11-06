package cdb.ml.qc;

import cdb.common.model.Point;
import cdb.common.model.RegionInfoVO;

/**
 * 
 * @author Chao Chen
 * @version $Id: RegionInfoVOHelper.java, v 0.1 Nov 3, 2015 11:42:48 AM chench Exp $
 */
public final class RegionInfoVOHelper {

    /**
     * forbidden construction
     */
    private RegionInfoVOHelper() {

    }

    public static Point make12Features(RegionInfoVO one) {
        Point p = new Point(12);
        int pSeq = 0;

        double gradRowSum = 0;
        for (double gradRowVal : one.getGradRow()) {
            gradRowSum += gradRowVal;
            //                dataSample.setValue(dSeq, pSeq++, gradRowVal);
        }
        p.setValue(pSeq++, gradRowSum);

        // gradient along column
        double gradColSum = 0;
        for (double gradColVal : one.getGradCol()) {
            gradColSum += gradColVal;
            //                dataSample.setValue(dSeq, pSeq++, gradColVal);
        }
        p.setValue(pSeq++, gradColSum);

        // Contextual: temporal gradients
        //            for (double tGradConVal : one.gettGradCon()) {
        //                dataSample.setValue(dSeq, pSeq++, tGradConVal);
        //            }

        p.setValue(pSeq++, 0.0d);
        //        p.setValue(pSeq++, one.gettGradCon().getValue(2)); // dffMean[-1]
        p.setValue(pSeq++, one.gettGradCon().getValue(3)); // dffMean[+1]

        p.setValue(pSeq++, 0.0d);
        //        p.setValue(pSeq++, one.gettGradCon().getValue(4)); // dffSd[-1]
        p.setValue(pSeq++, one.gettGradCon().getValue(5)); // dffSd[+1]

        // Contextual: spatial correlations
        double sCorrSum = 0;
        for (double sCorrConVal : one.getsCorrCon()) {
            sCorrSum += sCorrConVal;
            //                dataSample.setValue(dSeq, pSeq++, sCorrConVal);
        }
        p.setValue(pSeq++, sCorrSum);

        // Contextual: spatial differences
        double sDiffSum = 0;
        for (double sDiffConVal : one.getsDiffCon()) {
            sDiffSum += sDiffConVal;
            //                dataSample.setValue(dSeq, pSeq++, sDiffConVal);
        }
        p.setValue(pSeq++, sDiffSum);

        //            dataSample.setValue(dSeq, pSeq++, one.getEntropy());
        //            dataSample.setValue(dSeq, pSeq++, one.getGradMean());
        p.setValue(pSeq++, one.getMean());
        p.setValue(pSeq++, one.getSd());

        p.setValue(pSeq++, 0.0d);
        //                p.setValue(pSeq++, one.getrIndx());
        p.setValue(pSeq++, 0.0d);
        //                p.setValue(pSeq++, one.getcIndx());

        return p;
    }
}
