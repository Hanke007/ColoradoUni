package cdb.exp.main.greenland;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import cdb.common.lang.ClusterLocHelper;
import cdb.common.lang.LoggerUtil;
import cdb.common.lang.MatrixFileUtil;
import cdb.common.lang.StatisticParamUtil;
import cdb.common.lang.VisualizationUtil;
import cdb.common.lang.log4j.LoggerDefineConstant;
import cdb.dal.vo.DenseMatrix;
import cdb.dal.vo.Location;
import cdb.ml.clustering.Cluster;
import cdb.ml.clustering.HierarchicalClustering;
import cdb.ml.clustering.Point;
import cdb.ml.clustering.Samples;
import cdb.service.dataset.NetCDFDtProc;

/**
 * 
 * @author Chao Chen
 * @version $Id: S3ParameterAnomaly.java, v 0.1 Sep 18, 2015 2:48:53 PM chench Exp $
 */
public class S3ParameterAnomaly {

    public final static String    ROOT_DIR = "C:/Users/chench/Desktop/SIDS/";

    /** logger */
    protected final static Logger logger   = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        case2();
    }

    public static void case2() {
        String[] filePatternSets = {
                "C:/Users/chench/Desktop/SIDS/1990/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/1991/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/1992/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/1993/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/1994/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/1995/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/1996/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/1997/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/1998/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/1999/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2000/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2001/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2002/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2003/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2004/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2005/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2006/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2007/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2008/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2009/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2010/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2011/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2012/GLSMD25E2_\\d{8}_v01r01.nc" };

        LoggerUtil.info(logger, "1. loading dataset.");
        List<DenseMatrix> seralData = new ArrayList<DenseMatrix>();
        List<String> fileAssigmnt = new ArrayList<String>();
        loadingdataset(filePatternSets, seralData, fileAssigmnt);

        LoggerUtil.info(logger, "2. loading spatial clustering.");
        String clstFile = ROOT_DIR + "Clustering/kmean_5";
        List<Location> oneCluster = loadingSpatialClusterResulting(clstFile, 1);

        clusterTimeseries(seralData, oneCluster, 50, fileAssigmnt);
    }

    public static void loadingdataset(String[] filePatternSets, List<DenseMatrix> seralData,
                                      List<String> fileAssigmnt) {
        // tailor the data
        int[] rowIncluded = new int[100];
        int[] colIncluded = new int[100];
        for (int i = 0; i < rowIncluded.length; i++) {
            rowIncluded[i] = 370 + i;
            colIncluded[i] = 260 + i;
        }

        MatrixFileUtil.read(filePatternSets, seralData, fileAssigmnt, new NetCDFDtProc(),
            rowIncluded, colIncluded, 1.0d);
    }

    public static List<Location> loadingSpatialClusterResulting(String clstFile, int oneSeq) {
        List<List<Location>> locSet = new ArrayList<List<Location>>();
        ClusterLocHelper.readLoc(clstFile, locSet);
        return locSet.get(oneSeq);
    }

    public static void clusterTimeseries(List<DenseMatrix> seralData, List<Location> oneCluster,
                                         int maxK, List<String> fileAssigmnt) {
        LoggerUtil.info(logger, "3. compute statistical parameters.");
        // save data in specific cluster
        int mNum = seralData.size();
        for (int i = 0; i < mNum; i++) {
            DenseMatrix curMatrix = seralData.get(i);
            DenseMatrix newMatrix = new DenseMatrix(curMatrix.getRowNum(), curMatrix.getColNum(),
                Double.NaN);

            for (Location loc : oneCluster) {
                newMatrix.setVal(loc.x(), loc.y(), curMatrix.getVal(loc.x(), loc.y()));
            }
            seralData.set(i, newMatrix);
        }

        // compute statistical parameters
        double[] means = StatisticParamUtil.meanSeqTimeseries(seralData);
        double[] sds = StatisticParamUtil.sdSeqTimeseries(seralData);

        LoggerUtil.info(logger, "4. clustering time-series data.");
        // transfer data to Samples formulation
        Samples dataSample = new Samples(means.length, 2);
        for (int indxCount = 0; indxCount < means.length; indxCount++) {
            dataSample.setValue(indxCount, 0, means[indxCount]);
            dataSample.setValue(indxCount, 1, sds[indxCount]);
        }
        Cluster[] resultSet = HierarchicalClustering.cluster(dataSample, maxK,
            HierarchicalClustering.SQUARE_EUCLIDEAN_DISTANCE);
        VisualizationUtil
            .gnuLPWithMultipleFile(means, resultSet, ROOT_DIR + "Statistcs/Automatic/");

        // out-lier detection 
        LoggerUtil.info(logger, "5. detecting outliers.");
        Point[] centers = new Point[resultSet.length];
        for (int indx = 0; indx < resultSet.length; indx++) {
            centers[indx] = resultSet[indx].centroid(dataSample);
        }

        double[] maxDist = new double[resultSet.length];
        for (int i = 0; i < resultSet.length; i++) {

            double[] allDist = new double[resultSet.length];
            for (int j = 0; j < resultSet.length; j++) {
                Point a = centers[i];
                Point b = centers[j];
                double distance = (KL_UniNormal(a, b) + KL_UniNormal(b, a)) / 2.0d;

                allDist[j] = distance;
            }

            Arrays.sort(allDist);
            maxDist[i] = allDist[resultSet.length - 4];
        }

        for (int k = 0; k < 3; k++) {
            int indx = findMaximum(maxDist);
            maxDist[indx] = Double.MIN_VALUE;

            StringBuilder context = new StringBuilder();
            context.append("ClusterId: " + indx + "\n");
            Collections.sort(resultSet[indx].getList());

            int firstIndx = resultSet[indx].getList().get(0);
            int lasttIndx = resultSet[indx].getList().get(resultSet[indx].getList().size() - 1);
            context.append(fileAssigmnt.get(firstIndx)).append('\t')
                .append(fileAssigmnt.get(lasttIndx)).append('.');
            LoggerUtil.info(logger, context.toString());
        }

    }

    protected static double KL_UniNormal(final Point a, final Point b) {
        double mean1 = a.getValue(0);
        double sigma1 = a.getValue(1);

        double mean2 = b.getValue(0);
        double sigma2 = b.getValue(1);

        if (sigma2 == 0.0d) {
            return 0.0d;
        } else if (Double.isNaN(sigma2 / sigma1)) {
            return 0.0d;
        }

        return Math.log(sigma2 / sigma1) + (sigma1 * sigma1 + Math.pow(mean1 - mean2, 2.0d))
               / (2 * sigma2 * sigma2) - 1 / 2;
    }

    protected static int findMaximum(double[] allDist) {
        double max = Double.MIN_VALUE;
        int pivot = -1;
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
