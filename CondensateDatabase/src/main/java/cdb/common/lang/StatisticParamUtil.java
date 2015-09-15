package cdb.common.lang;

import java.util.List;

import cdb.dal.vo.DenseIntMatrix;
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
     * compute the mean parameter 
     * 
     * @param seralData     the list of the overall data
     * @return              the centroid matrix with mean value
     */
    public static DenseIntMatrix meanInOneCluster(List<DenseIntMatrix> seralData) {
        int rowNum = seralData.get(0).getRowNum();
        int colNum = seralData.get(0).getColNum();
        DenseIntMatrix centroid = new DenseIntMatrix(rowNum, colNum);
        DenseIntMatrix count = new DenseIntMatrix(rowNum, colNum);
        for (DenseIntMatrix denseMatrix : seralData) {
            for (int row = 0; row < rowNum; row++) {
                for (int col = 0; col < colNum; col++) {
                    int val = denseMatrix.getVal(row, col);
                    if (val == 0) {
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
                int val = centroid.getVal(row, col);
                int cnt = count.getVal(row, col);
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
    public static DenseIntMatrix meanInOneCluster(List<DenseIntMatrix> seralData, Cluster cluster) {
        int rowNum = seralData.get(0).getRowNum();
        int colNum = seralData.get(0).getColNum();
        DenseIntMatrix centroid = new DenseIntMatrix(rowNum, colNum);
        DenseIntMatrix count = new DenseIntMatrix(rowNum, colNum);
        for (int index : cluster) {
            for (int row = 0; row < rowNum; row++) {
                for (int col = 0; col < colNum; col++) {
                    int val = seralData.get(index).getVal(row, col);
                    if (val == 0) {
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
                int val = centroid.getVal(row, col);
                int cnt = count.getVal(row, col);
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
     * compute the standard deviation
     * 
     * @param seralData     the list of the overall data
     * @param centroid      the centroid matrix with mean parameters
     * @return              the matrix with standard deviation parameter
     */
    public static DenseIntMatrix sd(List<DenseIntMatrix> seralData, DenseIntMatrix centroid) {
        // compute the mean of the squared values EXX
        int rowNum = seralData.get(0).getRowNum();
        int colNum = seralData.get(0).getColNum();
        DenseIntMatrix sd = new DenseIntMatrix(rowNum, colNum);
        DenseIntMatrix count = new DenseIntMatrix(rowNum, colNum);
        for (DenseIntMatrix denseMatrix : seralData) {
            for (int row = 0; row < rowNum; row++) {
                for (int col = 0; col < colNum; col++) {
                    int val = denseMatrix.getVal(row, col);
                    if (val == 0) {
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
                int val = sd.getVal(row, col);
                int cnt = count.getVal(row, col);
                if (cnt == 0) {
                    // no observation
                    continue;
                }

                double squaredMean = Math.pow(centroid.getVal(row, col), 2.0d);
                double dVal = val * 1.0 / cnt - squaredMean; // DX = EXX - EX*EX
                sd.setVal(row, col, (int) Math.sqrt(dVal));
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
    public static DenseIntMatrix sdInOneCluster(List<DenseIntMatrix> seralData, Cluster cluster,
                                                DenseIntMatrix centroid) {
        // compute the mean of the squared values EXX
        int rowNum = seralData.get(0).getRowNum();
        int colNum = seralData.get(0).getColNum();
        DenseIntMatrix sd = new DenseIntMatrix(rowNum, colNum);
        DenseIntMatrix count = new DenseIntMatrix(rowNum, colNum);
        for (int index : cluster) {
            for (int row = 0; row < rowNum; row++) {
                for (int col = 0; col < colNum; col++) {
                    int val = seralData.get(index).getVal(row, col);
                    if (val == 0) {
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
                int val = sd.getVal(row, col);
                int cnt = count.getVal(row, col);
                if (cnt == 0) {
                    // no observation
                    continue;
                }

                double squaredMean = Math.pow(centroid.getVal(row, col), 2.0d);
                double dVal = val * 1.0 / cnt - squaredMean; // DX = EXX - EX*EX
                sd.setVal(row, col, (int) Math.sqrt(dVal));
            }
        }
        return sd;
    }

}
