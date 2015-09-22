package cdb.ml.clustering;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cdb.common.lang.LoggerUtil;
import cdb.common.lang.log4j.LoggerDefineConstant;

/**
 * 
 * @author Chao Chen
 * @version $Id: HierarchicalClustering.java, v 0.1 Sep 17, 2015 5:36:28 PM chench Exp $
 */
public class HierarchicalClustering {

    /** square error*/
    public final static int     SQUARE_EUCLIDEAN_DISTANCE    = 202;
    /** pearson correlation*/
    public final static int     PEARSON_CORRELATION_DISTANCE = 203;
    /** KL divergence*/
    public final static int     KL_DISTANCE                  = 204;

    /** logger */
    private final static Logger logger                       = Logger
                                                                 .getLogger(LoggerDefineConstant.SERVICE_THREAD);

    /**
     * forbidden construction
     */
    private HierarchicalClustering() {

    }

    /**
     * divide the samples into max-K classes
     * 
     * @param points        the sample to be clustered, which every row is a sample
     * @param K             the maximum number of classes
     * @param maxIteration  the maximum number of iterations
     * @param type          the type of distance involved
     * @return
     */
    public static Cluster[] cluster(final Samples points, final int K, final int type) {
        final int pointCount = points.length()[0];
        if (pointCount < K) {
            throw new RuntimeException("Number of samples is less than the number of classes.");
        }

        //create the initial clusters
        LoggerUtil.info(logger, "0. Create the initial clusters.");
        List<Cluster> resultSet = new ArrayList<Cluster>();
        List<Point> centroids = new ArrayList<Point>();
        List<Double> adjErrorTable = new ArrayList<Double>();
        initialize(points, resultSet, centroids, adjErrorTable, type);

        List<Cluster> oldReSet = null;
        double oldErr = Double.MAX_VALUE;
        double curErr = 0.0d;
        int round = 0;
        while (resultSet.size() > K) {
            // find two adjacent clusters with the least distance
            int[] invlvdIndices = findAdjacentClusters(resultSet, centroids, adjErrorTable);

            // update center of existing clustering
            updateCenters(points, resultSet, centroids, invlvdIndices);

            // update errors between adjacent clusters
            curErr = updateAdjacentError(points, resultSet, centroids, adjErrorTable,
                invlvdIndices, type);

            // check stopping condition
            round++;
            if (curErr > oldErr && resultSet.size() <= K) {
                resultSet = oldReSet;
                LoggerUtil.info(logger, round + "\t" + curErr + ">" + oldErr);
                break;
            } else {
                oldErr = curErr;

                // deep clone
                oldReSet = new ArrayList<Cluster>();
                for (Cluster one : resultSet) {
                    oldReSet.add((Cluster) one.deepClone());
                }
                LoggerUtil.info(logger, round + "\t" + curErr);
            }

        }
        Cluster[] returnSet = new Cluster[resultSet.size()];
        resultSet.toArray(returnSet);

        // record centers
        int cenIndex = 0;
        StringBuilder centdInfo = new StringBuilder();
        for (Cluster cluster : returnSet) {
            centdInfo.append('\n').append(cenIndex).append(": ").append(cluster.centroid(points))
                .append("\tSize: ").append(returnSet[cenIndex].getList().size());
            cenIndex++;
        }
        LoggerUtil.info(logger, centdInfo.toString());
        return returnSet;
    }

    protected static void initialize(Samples points, List<Cluster> resultSet,
                                     List<Point> centroids, List<Double> adjErrorTable,
                                     final int type) {
        // every point is a cluster
        int pIndex = 0;
        for (Point point : points) {
            Cluster one = new Cluster();
            one.add(pIndex++);
            resultSet.add(one);

            centroids.add(point);
        }

        // compute distance between the adjacent points
        int cenNum = resultSet.size();
        for (int i = 0; i < cenNum - 1; i++) {
            Point a = resultSet.get(i).centroid(points);
            Point b = resultSet.get(i + 1).centroid(points);

            adjErrorTable.add(distance(a, b, type));
        }
    }

    protected static int[] findAdjacentClusters(List<Cluster> resultSet, List<Point> centroids,
                                                List<Double> adjErrorTable) {
        int pivot = -1;
        double min = Double.MAX_VALUE;
        for (int i = 0; i < adjErrorTable.size(); i++) {
            double distance = adjErrorTable.get(i);

            if (min > distance) {
                pivot = i;
                min = distance;
            }
        }

        int[] invlvdIndices = new int[2];
        invlvdIndices[0] = pivot;
        invlvdIndices[1] = pivot + 1;
        return invlvdIndices;
    }

    protected static void updateCenters(Samples points, List<Cluster> resultSet,
                                        List<Point> centroids, int[] invlvdIndices) {
        // update clusters
        Cluster a = resultSet.get(invlvdIndices[0]);
        Cluster b = resultSet.get(invlvdIndices[1]);
        for (int pIndex : b) {
            a.add(pIndex);
        }
        resultSet.remove(invlvdIndices[1]);

        // update centers
        centroids.set(invlvdIndices[0], a.centroid(points));
        centroids.remove(invlvdIndices[1]);

    }

    protected static double updateAdjacentError(Samples points, List<Cluster> resultSet,
                                                List<Point> centroids, List<Double> adjErrorTable,
                                                int[] invlvdIndices, final int type) {
        if (invlvdIndices[0] != resultSet.size() - 1) {
            Point a = resultSet.get(invlvdIndices[0]).centroid(points);
            Point b = resultSet.get(invlvdIndices[1]).centroid(points);

            // if invlvdIndices[0] is not the last node
            // then remove the old one
            double newDistnce = distance(a, b, type);
            adjErrorTable.set(invlvdIndices[0], newDistnce);
            adjErrorTable.remove(invlvdIndices[1]);
        } else {
            // if invlvdIndices[0] is the last node
            Point a = resultSet.get(invlvdIndices[0] - 1).centroid(points);
            Point b = resultSet.get(invlvdIndices[0]).centroid(points);
            double newDistnce = distance(a, b, type);
            adjErrorTable.set(invlvdIndices[0] - 1, newDistnce);
            adjErrorTable.remove(invlvdIndices[0]);
        }

        // computer the overall errors
        double avgError = 0.0d;
        int modelCount = 0;
        for (int clstIndex = 0; clstIndex < resultSet.size(); clstIndex++) {
            Cluster cluster = resultSet.get(clstIndex);
            Point center = centroids.get(clstIndex);

            double sum = 0.0d;
            for (int eleIndex : cluster) {
                Point elem = points.getPoint(eleIndex);
                sum += distance(elem, center, type);
            }
            sum /= cluster.getList().size();

            if (Double.isInfinite(sum)) {
                continue;
            }
            avgError += Math.sqrt(sum);
            modelCount++;
        }
        return Math.sqrt(avgError / modelCount);
    }

    /**
     * calculate the distance between two vectors
     *  
     * @param a     given vector
     * @param b     given vector
     * @param type  the distance to compute
     * @return
     */
    public static double distance(final Point a, final Point centroid, final int type) {
        //check vector with all zeros
        if (type != SQUARE_EUCLIDEAN_DISTANCE && (a.norm() == 0 || centroid.norm() == 0)) {
            return 0.0;
        }

        switch (type) {
            case SQUARE_EUCLIDEAN_DISTANCE:
                Point c = a.minus(centroid);
                return c.innerProduct(c); // |a-b|
            case PEARSON_CORRELATION_DISTANCE:
                a.sub(a.average());
                centroid.sub(centroid.average());
                return a.innerProduct(centroid) / (a.norm() * centroid.norm());
            case KL_DISTANCE:
                double mean1 = a.getValue(0);
                double sigma1 = a.getValue(1);

                double mean2 = centroid.getValue(0);
                double sigma2 = centroid.getValue(1);

                if (sigma2 == 0.0d & sigma1 == 0.0d & mean1 == mean2) {
                    return 0.0d;
                } else if (sigma2 == 0.0d | sigma1 == 0.0d) {
                    return Double.POSITIVE_INFINITY;
                }

                return Math.log(sigma2 / sigma1)
                       + (sigma1 * sigma1 + Math.pow(mean1 - mean2, 2.0d)) / (2 * sigma2 * sigma2)
                       - 1 / 2;
            default:
                throw new RuntimeException("Wrong Distance Type! ");
        }
    }

    protected static double KLUniNormal(final Point a, final Point centroid) {

        return 0.0d;
    }

}
