package cdb.ml.anomaly;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import cdb.ml.clustering.Point;

/**
 * Statistics-based method
 * 
 * @author Chao Chen
 * @version $Id: SimpleAnomalyDetecion.java, v 0.1 Sep 29, 2015 10:05:52 AM chench Exp $
 */
public class SimpleAnomalyDetecion implements AnomalyDetection {
    /** the mean value of the observation*/
    private double meanVal;
    /** the standard deviation value of the observation*/
    private double sdVal;
    /** the number of the standard deviations*/
    private double sdNum;

    /**
     * Construction
     */
    public SimpleAnomalyDetecion() {
        super();
    }

    /**
     * Construction
     * 
     * @param meanVal   the mean value of the observation
     * @param sdVal     the standard deviation value of the observation
     * @param sdNum     the number of the standard deviations
     */
    public SimpleAnomalyDetecion(double meanVal, double sdVal, double sdNum) {
        super();
        this.meanVal = meanVal;
        this.sdVal = sdVal;
        this.sdNum = sdNum;
    }

    /** 
     * @see cdb.ml.anomaly.AnomalyDetection#detect(cdb.ml.clustering.Point[], int, int)
     */
    @Override
    public int[] detect(Point[] domains, int neighNum, int anomalyNum) {
        if (domains == null | domains.length == 0) {
            throw new RuntimeException("The Point[] variable is null or blank.");
        }

        List<Integer> anomalies = new ArrayList<Integer>();
        for (int p = 0; p < anomalies.size(); p++) {
            if (domains[p] == null | Double.isNaN(domains[p].getValue(0))) {
                continue;
            } else if (Math.abs(domains[p].getValue(0) - meanVal) > sdNum * sdVal) {
                anomalies.add(p);
            }
        }

        if (anomalies.isEmpty()) {
            return null;
        } else {
            int[] reslt = ArrayUtils.toPrimitive(anomalies.toArray(new Integer[anomalies.size()]));
            return reslt;
        }

    }

    /**
     * Getter method for property <tt>meanVal</tt>.
     * 
     * @return property value of meanVal
     */
    public double getMeanVal() {
        return meanVal;
    }

    /**
     * Setter method for property <tt>meanVal</tt>.
     * 
     * @param meanVal value to be assigned to property meanVal
     */
    public void setMeanVal(double meanVal) {
        this.meanVal = meanVal;
    }

    /**
     * Getter method for property <tt>sdNum</tt>.
     * 
     * @return property value of sdNum
     */
    public double getSdNum() {
        return sdNum;
    }

    /**
     * Setter method for property <tt>sdNum</tt>.
     * 
     * @param sdNum value to be assigned to property sdNum
     */
    public void setSdNum(double sdNum) {
        this.sdNum = sdNum;
    }

}
