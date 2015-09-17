package cdb.exp.main.greenland;

import java.util.ArrayList;
import java.util.List;

import cdb.common.lang.ClusterLocHelper;
import cdb.common.lang.MatrixFileUtil;
import cdb.common.lang.StatisticParamUtil;
import cdb.common.lang.VisualizationUtil;
import cdb.dal.vo.DenseMatrix;
import cdb.dal.vo.Location;
import cdb.service.dataset.NetCDFDtProc;

/**
 * 
 * @author Chao Chen
 * @version $Id: S2TimeseriesCluster.java, v 0.1 Sep 17, 2015 9:57:00 AM chench Exp $
 */
public class S2TimeseriesCluster {

    public final static String ROOT_DIR = "C:/Users/chench/Desktop/SIDS/";

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        case1();
    }

    public static void case1() {
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
        List<Location> oneCluster = loadingClusterResulting(clstFile, 1);

        cmpingParamWithCluster(seralData, oneCluster);

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

    public static List<Location> loadingClusterResulting(String clstFile, int oneSeq) {
        List<List<Location>> locSet = new ArrayList<List<Location>>();
        ClusterLocHelper.readLoc(clstFile, locSet);
        return locSet.get(oneSeq);
    }

    public static void cmpingParamWithCluster(List<DenseMatrix> seralData, List<Location> oneCluster) {
        // save data in specific cluster
        int mNum = seralData.size();
        for (int i = 0; i < mNum; i++) {
            DenseMatrix curMatrix = seralData.get(i);
            DenseMatrix newMatrix = new DenseMatrix(curMatrix.getRowNum(), curMatrix.getColNum());

            for (Location loc : oneCluster) {
                newMatrix.setVal(loc.x(), loc.y(), curMatrix.getVal(loc.x(), loc.y()));
            }
            seralData.set(i, newMatrix);
        }

        // computer statistical parameters
        double[] means = StatisticParamUtil.meanSeqTimeseries(seralData);
        VisualizationUtil.gnuLinepoint(means, 0, ROOT_DIR + "Statistcs/Trends/List");

        //        double[] sds = StatisticParamUtil.sdSeqTimeseries(seralData);
        //        VisualizationUtil.gnuLinepoint(sds, 0, ROOT_DIR + "Statistcs/Trends/List");
    }
}
