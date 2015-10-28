/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package cdb.ml.clustering;

import org.apache.log4j.Logger;

import cdb.common.lang.DistanceUtil;
import cdb.common.lang.LoggerUtil;
import cdb.common.lang.log4j.LoggerDefineConstant;

/**
 * This is a class implementing K-means++.
 * Technical detail of the algorithm can be found in
 * Arthur, David, and Sergei Vassilvitskii, K-means++: The Advantages of Careful Seeding.
 * Proceedings of the eighteenth annual ACM-SIAM symposium on Discrete algorithms, 2007.
 * 
 * @author Chao Chen
 * @version $Id: KMeansUtil.java, v 0.1 2014-10-14 上午11:19:24 chench Exp $
 */
public class KMeansPlusPlusUtil {

    /** logger */
    private final static Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_THREAD);

    /**
     * forbid construction
     */
    private KMeansPlusPlusUtil() {

    }

    /**
     * divide the samples into K classes
     * 
     * @param points        the sample to be clustered, which every row is a sample
     * @param K             the number of classes
     * @param maxIteration  the maximum number of iterations
     * @param type          the type of distance involved
     * @return
     */
    public static Cluster[] cluster(final Samples points, final int K, final int maxIteration,
                                    final int type) {
        final int pointCount = points.length()[0];
        if (pointCount < K) {
            throw new RuntimeException("Number of samples is less than the number of classes.");
        }

        //Create the initial clusters
        LoggerUtil.debug(logger, "0. Create the initial clusters.");
        Point[] centroids = new Point[K];
        chooseInitialCenters(points, centroids, K, type);

        //Converge to a minimun
        LoggerUtil.debug(logger, "1. Locally search optimal solution.");
        Cluster[] resultSet = new Cluster[K];
        int[] assigmnt = new int[pointCount];
        double oldErr = Double.MAX_VALUE;
        double curErr = 0.0d;
        int round = 0;
        while (round < maxIteration) {
            //Create new set of elements
            Cluster[] newSet = new Cluster[K];
            for (int i = 0; i < K; i++) {
                newSet[i] = new Cluster();
            }

            //Greedy Strategy
            int changes = 0;
            curErr = 0.0d;
            for (int i = 0; i < pointCount; i++) {
                Point a = points.getPointRef(i);

                //choose optimal centroid
                int pivot = -1;
                double min = Double.MAX_VALUE;
                for (int k = 0; k < K; k++) {
                    double distnce = DistanceUtil.distance(a, centroids[k], type);

                    if (min > distnce) {
                        min = distnce;
                        pivot = k;
                    }
                }
                curErr += min;

                if (pivot == -1) {
                    throw new RuntimeException("pivot equals -1. a:\n" + a);
                }
                //check change
                if (pivot != assigmnt[i]) {
                    changes++;
                }

                assigmnt[i] = pivot;
                newSet[pivot].add(i);
            }

            //Convex Optimization
            for (int k = 0; k < K; k++) {
                centroids[k] = newSet[k].centroid(points);
            }

            //if no change, then exist
            round++;
            if (changes == 0) {
                LoggerUtil.debug(logger, round + "\tNo Changes");
                break;
            } else if (curErr > oldErr) {
                LoggerUtil.debug(logger, round + "\t" + curErr + ">" + oldErr);
                break;
            } else {
                oldErr = curErr;
                resultSet = newSet;
                LoggerUtil.debug(logger, round + "\t" + curErr);
            }

        }

        int cenIndex = 1;
        StringBuilder centdInfo = new StringBuilder();
        for (Point centroid : centroids) {
            centdInfo.append('\n').append(cenIndex).append(": ").append(centroid).append("\tSize: ")
                .append(resultSet[cenIndex - 1].getList().size());
            cenIndex++;
        }
        LoggerUtil.debug(logger, centdInfo.toString());

        return resultSet;
    }

    /**
     * make a initial division, and make sure no empty class
     * 
     * @param clusters
     * @param assigmnt
     * @param K
     */
    public static void chooseInitialCenters(final Samples points, final Point[] centroids,
                                            final int K, final int type) {
        final int pointCount = points.length()[0];
        boolean[] taken = new boolean[pointCount];

        //Choose an initial center uniformly at random

        int indxOfFirstCentroid = (int) (Math.random() * pointCount);
        centroids[0] = points.getPoint(indxOfFirstCentroid);
        taken[indxOfFirstCentroid] = true;

        //Choose the rest centers
        int curCentroid = 1;
        while (curCentroid < K) {

            //Compute shortest distance from a data point to the closest center
            double[] D = new double[pointCount];
            double sum = 0.0d;
            for (int i = 0; i < pointCount; i++) {
                if (taken[i]) {
                    continue;
                }

                Point point = points.getPointRef(i);
                double min = Double.MAX_VALUE;
                for (int j = 0; j < curCentroid; j++) {
                    double distance = DistanceUtil.distance(point, centroids[j], type);

                    if (min > distance) {
                        min = distance;
                    }
                }
                D[i] = min;
                sum += min;
            }

            //Choose next center x with probability D(x) / /Sigma D(x_i)
            double roullete = Math.random();
            int pivot = -1;
            while (pivot == -1) {
                for (int i = 0; i < pointCount; i++) {
                    if (taken[i]) {
                        continue;
                    }

                    roullete -= D[i] / sum;
                    if (roullete < 0.0d) {
                        pivot = i;
                        break;
                    }
                }
            }

            //Update centroid information
            centroids[curCentroid] = points.getPoint(pivot);
            taken[pivot] = true;
            curCentroid++;
        }
    }

}
