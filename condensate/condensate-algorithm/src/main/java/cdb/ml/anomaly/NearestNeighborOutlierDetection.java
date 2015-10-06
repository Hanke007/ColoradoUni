package cdb.ml.anomaly;

import java.util.Arrays;
import cdb.ml.clustering.Point;

/**
 * 
 * 
 * @author Chao Chen
 * @version $Id: SimpleAnomalyDetection.java, v 0.1 Sep 22, 2015 1:24:09 PM chench Exp $
 */
public class NearestNeighborOutlierDetection implements AnomalyDetection {

    /**
     * @see cdb.ml.anomaly.AnomalyDetection#detect(cdb.ml.clustering.Point[], int, int)
     */
    @Override
    public int[] detect(Point[] domains, int neighNum, int anomalyNum) {
        double[] maxDist = new double[domains.length];
        for (int i = 0; i < domains.length; i++) {
            Point a = domains[i];

            if (a == null) {
                continue;
            } else if (a.getValue(0) == 0.0d && a.getValue(1) == 0.0d) {
                maxDist[i] = Double.MAX_VALUE;
            } else {
                double[] allDist = new double[domains.length];
                for (int j = 0; j < domains.length; j++) {
                    Point b = domains[j];
                    if (b == null) {
                        continue;
                    } else if (b.getValue(0) == 0.0d && b.getValue(1) == 0.0d) {
                        continue;
                    }

                    //                double distance = (KL_UniNormal(a, b) + KL_UniNormal(b, a)) / 2.0d;
                    //                    double distance = KL_UniNormal(b, a);
                    double distance = Euclidean_distance(b, a);

                    allDist[j] = distance;
                }

                Arrays.sort(allDist);
                maxDist[i] = allDist[domains.length - neighNum];
            }
        }

        int[] resltIndx = new int[anomalyNum];
        for (int k = 0; k < anomalyNum; k++) {
            int indx = findMaximum(maxDist);
            if (maxDist[indx] == 0) {
                int[] newIndx = new int[k];
                for (int j = 0; j < k; j++) {
                    newIndx[j] = resltIndx[j];
                }
                resltIndx = newIndx;
                break;
            }

            resltIndx[k] = indx;
            maxDist[indx] = Double.NEGATIVE_INFINITY;
        }
        return resltIndx;
    }

    /**
     * distance measures based on KL-Divergence
     * 
     * @param a     data point a
     * @param b     data point b
     * @return      the distance between two points
     */
    protected double KL_UniNormal(final Point a, final Point b) {
        double mean1 = a.getValue(0);
        double sigma1 = a.getValue(1);

        double mean2 = b.getValue(0);
        double sigma2 = b.getValue(1);

        if (sigma2 == 0.0d & sigma1 == 0.0d & mean1 == mean2) {
            return 0.0d;
        } else if (sigma1 == 0.0d) {
            return Double.MAX_VALUE;
        } else if (sigma2 == 0.0d) {
            return 0.0d;
        }

        return Math.log(sigma2 / sigma1)
               + (sigma1 * sigma1 + Math.pow(mean1 - mean2, 2.0d)) / (2 * sigma2 * sigma2) - 1 / 2;
    }

    /**
     * distance measures based on KL-Divergence
     * 
     * @param a     data point a
     * @param b     data point b
     * @return      the distance between two points
     */
    protected double Euclidean_distance(final Point a, final Point b) {

        int dimsn = a.dimension();
        double sum = 0.0d;
        for (int i = 0; i < dimsn; i++) {
            sum += Math.pow(a.getValue(i) - b.getValue(i), 2.0d);
        }

        return Math.sqrt(sum);
    }

    /**
     * find the index of the data with the maximum anomaly scores
     *      
     * @param allDist   the array of distance
     * @return          the index of the data  with the maximum anomaly scores
     */
    protected int findMaximum(double[] allDist) {
        double max = Double.NEGATIVE_INFINITY;
        int pivot = -1;
        // ignoring side effects
        for (int i = 0; i < allDist.length; i++) {
            double val = allDist[i];

            if (max < val) {
                max = val;
                pivot = i;
            }
        }
        return pivot;
    }
}
