package cdb.exp.main.greenland;

import java.util.ArrayList;
import java.util.List;

import cdb.common.lang.ClusterHelper;
import cdb.common.lang.MatrixFileUtil;
import cdb.common.lang.StatisticParamUtil;
import cdb.common.lang.VisualizationUtil;
import cdb.dal.vo.DenseMatrix;
import cdb.dal.vo.Location;
import cdb.ml.clustering.Cluster;
import cdb.ml.clustering.HierarchicalClustering;
import cdb.ml.clustering.Samples;
import cdb.service.dataset.NetCDFDtProc;

/**
 * 
 * @author Chao Chen
 * @version $Id: S2TimeseriesCluster.java, v 0.1 Sep 17, 2015 9:57:00 AM chench Exp $
 */
public class S2TimeseriesCluster extends AbstractGreenLandAnalysis {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        case1();
        //        case2();
    }

    public static void case1() {
        String[] filePatternSets = {
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

        List<DenseMatrix> seralData = new ArrayList<DenseMatrix>();
        List<String> fileAssigmnt = new ArrayList<String>();
        loadingdataset(filePatternSets, seralData, fileAssigmnt);

        String clstFile = ROOT_DIR + "Clustering/kmean_5";
        List<Location> oneCluster = loadingSpatialClusterResulting(clstFile, 1);

        cmpingParamWithCluster(seralData, oneCluster);

    }

    public static void case2() {
        String[] filePatternSets = {
                "C:/Users/chench/Desktop/SIDS/2000/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2002/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2004/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2006/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2008/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2010/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2012/GLSMD25E2_\\d{8}_v01r01.nc" };

        List<DenseMatrix> seralData = new ArrayList<DenseMatrix>();
        List<String> fileAssigmnt = new ArrayList<String>();
        loadingdataset(filePatternSets, seralData, fileAssigmnt);

        String clstFile = ROOT_DIR + "Clustering/kmean_5";
        List<Location> oneCluster = loadingSpatialClusterResulting(clstFile, 1);

        Cluster[] resultSet = clusterTimeseries(seralData, oneCluster, 25);
        ClusterHelper.saveLoc(resultSet, ROOT_DIR + "Clustering/Hierarchy_5", seralData.size(),
            1);

        // computer statistical parameters
        double[] means = StatisticParamUtil.meanSeqTimeseries(seralData);

        VisualizationUtil
            .gnuLPWithMultipleFile(means, resultSet, ROOT_DIR + "Statistcs/Automatic/");
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
        ClusterHelper.readLoc(clstFile, locSet);
        return locSet.get(oneSeq);
    }

    public static void cmpingParamWithCluster(List<DenseMatrix> seralData, List<Location> oneCluster) {
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

        // computer statistical parameters
        double[] means = StatisticParamUtil.meanSeqTimeseries(seralData);
        VisualizationUtil.gnuLinepoint(means, 0, ROOT_DIR + "Statistcs/Trends/means");

        double[] sds = StatisticParamUtil.sdSeqTimeseries(seralData);
        VisualizationUtil.gnuLinepoint(sds, 0, ROOT_DIR + "Statistcs/Trends/sds");
    }

    public static Cluster[] clusterTimeseries(List<DenseMatrix> seralData,
                                              List<Location> oneCluster, int maxK) {
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

        // computer statistical parameters
        double[] means = StatisticParamUtil.meanSeqTimeseries(seralData);
        double[] sds = StatisticParamUtil.sdSeqTimeseries(seralData);

        // transfer data to Samples formulation
        Samples dataSample = new Samples(means.length, 2);
        for (int indxCount = 0; indxCount < means.length; indxCount++) {
            dataSample.setValue(indxCount, 0, means[indxCount]);
            dataSample.setValue(indxCount, 1, sds[indxCount]);
        }

        return HierarchicalClustering.cluster(dataSample, maxK,
            HierarchicalClustering.SQUARE_EUCLIDEAN_DISTANCE);
    }
}
