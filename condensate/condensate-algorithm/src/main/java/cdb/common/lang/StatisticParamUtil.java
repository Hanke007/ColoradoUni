package cdb.common.lang;

import java.util.List;

import cdb.dal.vo.DenseMatrix;
import cdb.ml.clustering.Cluster;

/**
 * 
 * @author Chao Chen
 * @version $Id: StatisticParamUtil.java, v 0.1 Sep 15, 2015 12:04:15 PM chench Exp $
 */
public final class StatisticParamUtil {

    private StatisticParamUtil() {

    }

    /**
     * compute the mean of every matrix in the list
     * 
     * @param seralData     the list of the overall data
     * @return
     */
    public static double[] meanSeqTimeseries(List<DenseMatrix> seralData) {
        double[] means = new double[seralData.size()];

        int indx = 0;
        for (DenseMatrix oneMatrix : seralData) {
            means[indx++] = oneMatrix.average();
        }
        return means;
    }

    /**
     * compute the mean parameter 
     * 
     * @param seralData     the list of the overall data
     * @return              the centroid matrix with mean value
     */
    public static DenseMatrix mean(List<DenseMatrix> seralData) {
        int rowNum = seralData.get(0).getRowNum();
        int colNum = seralData.get(0).getColNum();
        DenseMatrix centroid = new DenseMatrix(rowNum, colNum);
        DenseMatrix count = new DenseMatrix(rowNum, colNum);
        for (DenseMatrix denseMatrix : seralData) {
            for (int row = 0; row < rowNum; row++) {
                for (int col = 0; col < colNum; col++) {
                    double val = denseMatrix.getVal(row, col);
                    if (Double.isNaN(val)) {
                        // no observation
                        continue;
                    }

                    centroid.add(row, col, val);
                    count.add(row, col, 1);
                }
            }
        }

        for (int row = 0; row < rowNum; row++) {
            for (int col = 0; col < colNum; col++) {
                double val = centroid.getVal(row, col);
                double cnt = count.getVal(row, col);
                if (cnt == 0) {
                    // no observation
                    continue;
                }

                centroid.setVal(row, col, val / cnt);
            }
        }
        return centroid;
    }

    /**
     * compute the mean parameter in the given cluster
     * 
     * @param seralData     the list of the overall data
     * @param cluster       the given cluster
     * @return              the centroid matrix with mean value
     */
    public static DenseMatrix meanInOneCluster(List<DenseMatrix> seralData, Cluster cluster) {
        int rowNum = seralData.get(0).getRowNum();
        int colNum = seralData.get(0).getColNum();
        DenseMatrix centroid = new DenseMatrix(rowNum, colNum);
        DenseMatrix count = new DenseMatrix(rowNum, colNum);
        for (int index : cluster) {
            for (int row = 0; row < rowNum; row++) {
                for (int col = 0; col < colNum; col++) {
                    double val = seralData.get(index).getVal(row, col);
                    if (Double.isNaN(val)) {
                        // no observation
                        continue;
                    }

                    centroid.add(row, col, val);
                    count.add(row, col, 1);
                }
            }
        }

        for (int row = 0; row < rowNum; row++) {
            for (int col = 0; col < colNum; col++) {
                double val = centroid.getVal(row, col);
                double cnt = count.getVal(row, col);
                if (cnt == 0) {
                    // no observation
                    continue;
                }

                centroid.setVal(row, col, val / cnt);
            }
        }
        return centroid;
    }

    /**
     * compute the standard deviation across time
     * 
     * @param seralData     the list of the overall data
     * @return
     */
    public static double[] sdSeqTimeseries(List<DenseMatrix> seralData) {
        double[] sd = new double[seralData.size()];

        int indx = 0;
        for (DenseMatrix oneMatrix : seralData) {
            sd[indx++] = oneMatrix.sd();
        }
        return sd;
    }

    /**
     * compute the standard deviation
     * 
     * @param seralData     the list of the overall data
     * @param centroid      the centroid matrix with mean parameters
     * @return              the matrix with standard deviation parameter
     */
    public static DenseMatrix sd(List<DenseMatrix> seralData, DenseMatrix centroid) {
        // compute the mean of the squared values EXX
        int rowNum = seralData.get(0).getRowNum();
        int colNum = seralData.get(0).getColNum();
        DenseMatrix sd = new DenseMatrix(rowNum, colNum);
        DenseMatrix count = new DenseMatrix(rowNum, colNum);
        for (DenseMatrix denseMatrix : seralData) {
            for (int row = 0; row < rowNum; row++) {
                for (int col = 0; col < colNum; col++) {
                    double val = denseMatrix.getVal(row, col);
                    if (Double.isNaN(val)) {
                        // no observation
                        continue;
                    }

                    sd.add(row, col, val * val);
                    count.add(row, col, 1);
                }
            }
        }

        // compute standard deviation
        for (int row = 0; row < rowNum; row++) {
            for (int col = 0; col < colNum; col++) {
                double val = sd.getVal(row, col);
                double cnt = count.getVal(row, col);
                if (cnt == 0) {
                    // no observation
                    continue;
                }

                double squaredMean = Math.pow(centroid.getVal(row, col), 2.0d);
                double dVal = val * 1.0 / cnt - squaredMean; // DX = EXX - EX*EX
                sd.setVal(row, col, Math.sqrt(dVal));
            }
        }
        return sd;
    }

    /**
     * compute the standard deviation in the given cluster
     * 
     * @param seralData     the list of the overall data
     * @param cluster       the given cluster
     * @param centroid      the centroid matrix with mean parameters
     * @return              the matrix with standard deviation parameter
     */
    public static DenseMatrix sdInOneCluster(List<DenseMatrix> seralData, Cluster cluster,
                                             DenseMatrix centroid) {
        // compute the mean of the squared values EXX
        int rowNum = seralData.get(0).getRowNum();
        int colNum = seralData.get(0).getColNum();
        DenseMatrix sd = new DenseMatrix(rowNum, colNum);
        DenseMatrix count = new DenseMatrix(rowNum, colNum);
        for (int index : cluster) {
            for (int row = 0; row < rowNum; row++) {
                for (int col = 0; col < colNum; col++) {
                    double val = seralData.get(index).getVal(row, col);
                    if (Double.isNaN(val)) {
                        // no observation
                        continue;
                    }

                    sd.add(row, col, val * val);
                    count.add(row, col, 1);
                }
            }
        }

        // compute standard deviation
        for (int row = 0; row < rowNum; row++) {
            for (int col = 0; col < colNum; col++) {
                double val = sd.getVal(row, col);
                double cnt = count.getVal(row, col);
                if (cnt == 0) {
                    // no observation
                    continue;
                }

                double squaredMean = Math.pow(centroid.getVal(row, col), 2.0d);
                double dVal = val * 1.0 / cnt - squaredMean; // DX = EXX - EX*EX
                sd.setVal(row, col, Math.sqrt(dVal));
            }
        }
        return sd;
    }

    /**
     * compute the distribution given a data set
     * 
     * @param dMatrix
     * @param minVal
     * @param maxVal
     * @param k
     * @return
     */
    public static DenseMatrix distribution(DenseMatrix dMatrix, double minVal, double maxVal, int k,
                                           double sampleRate) {
        double stepVal = (maxVal - minVal) / k;
        int rowNum = dMatrix.getRowNum();
        int colNum = dMatrix.getColNum();

        int count = 0;
        double[] distributions = new double[k];
        for (int row = 0; row < rowNum; row++) {
            for (int col = 0; col < colNum; col++) {
                double val = dMatrix.getVal(row, col);
                if (Double.isNaN(val)) {
                    continue;
                } else if (Math.random() > sampleRate) {
                    continue;
                }

                int indx = (int) ((val - minVal) / stepVal);
                distributions[indx]++;
                count++;
            }
        }

        DenseMatrix reslt = new DenseMatrix(1, k);
        for (int dIndx = 0; dIndx < k; dIndx++) {
            reslt.setVal(0, dIndx, distributions[dIndx] / count);
        }
        return reslt;
    }
}
