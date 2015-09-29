package cdb.ml.anomaly;

import cdb.ml.clustering.Point;

/**
 * 
 * @author Chao Chen
 * @version $Id: AnomalyDetection.java, v 0.1 Sep 29, 2015 10:20:15 AM chench Exp $
 */
public interface AnomalyDetection {

    /**
     * detect the anomalous data in the given domains
     * 
     * @param domains       the data domain includes anomaly
     * @param neighNum      the number of the neighbor to count the anomaly scores
     * @param anomalyNum    the number of anomalies to return
     * @return              the indices of the anomalies
     */
    public int[] detect(Point[] domains, int neighNum, int anomalyNum);
}
