package cdb.exp.main.greenland;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cdb.common.lang.ClusterLocHelper;
import cdb.common.lang.FileUtil;
import cdb.common.lang.LoggerUtil;
import cdb.common.lang.MatrixFileUtil;
import cdb.common.lang.SerializeUtil;
import cdb.common.lang.StatisticParamUtil;
import cdb.common.lang.log4j.LoggerDefineConstant;
import cdb.dal.vo.DenseMatrix;
import cdb.ml.clustering.Cluster;
import cdb.ml.clustering.KMeansPlusPlusUtil;
import cdb.ml.clustering.Samples;
import cdb.service.dataset.DatasetProc;
import cdb.service.dataset.NetCDFDtProc;

/**
 * 
 * @author Chao Chen
 * @version $Id: S1ParameterCmp.java, v 0.1 Sep 15, 2015 12:02:03 PM chench Exp $
 */
public class S1ParameterCmp {

    public final static String  ROOT_DIR = "C:/Users/chench/Desktop/SIDS/";

    /** logger */
    private final static Logger logger   = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        //        case2();

        case3(69,10);

        //        DenseMatrix sd = (DenseMatrix) SerializeUtil.readObject(ROOT_DIR + "Serial/SD.OBJ");
        //        clusterinng(sd, 3, 45);
    }

    public static void case1() {
        // loading dataset
        String[] filePatternSets = {
                "C:/Users/chench/Desktop/SIDS/2000/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2002/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2004/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2006/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2008/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2010/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2012/GLSMD25E2_\\d{8}_v01r01.nc" };

        LoggerUtil.info(logger, "1. load dataset.");
        List<DenseMatrix> seralData = new ArrayList<DenseMatrix>();
        List<String> fileAssigmnt = new ArrayList<String>();
        loadingDatasetStep(filePatternSets, seralData, fileAssigmnt, new NetCDFDtProc(), 1.0);

        LoggerUtil.info(logger, "2. compute statistical parameter.");
        DenseMatrix centroid = StatisticParamUtil.mean(seralData);
        MatrixFileUtil.gnuHeatmap(centroid, "C:\\Users\\chench\\Desktop\\mean");
        DenseMatrix sd = StatisticParamUtil.sd(seralData, centroid);
        MatrixFileUtil.gnuHeatmap(sd, "C:\\Users\\chench\\Desktop\\sd");
    }

    public static void case2() {
        // loading dataset
        String[] filePatternSets = {
                "C:/Users/chench/Desktop/SIDS/2000/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2002/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2004/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2006/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2008/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2010/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2012/GLSMD25E2_\\d{8}_v01r01.nc" };

        LoggerUtil.info(logger, "1. load dataset.");
        List<DenseMatrix> seralData = new ArrayList<DenseMatrix>();
        List<String> fileAssigmnt = new ArrayList<String>();
        loadingDatasetStep(filePatternSets, seralData, fileAssigmnt, new NetCDFDtProc(), 1.0);

        LoggerUtil.info(logger, "2. compute statistical parameter.");
        DenseMatrix centroid = StatisticParamUtil.mean(seralData);
        SerializeUtil.writeObject(centroid, ROOT_DIR + "Serial/MEAN.OBJ");
        DenseMatrix sd = StatisticParamUtil.sd(seralData, centroid);
        SerializeUtil.writeObject(sd, ROOT_DIR + "Serial/SD.OBJ");
    }

    public static void case3(int x, int y) {
        // loading dataset
        String[] filePatternSets = {
                "C:/Users/chench/Desktop/SIDS/2000/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2002/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2004/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2006/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2008/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2010/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2012/GLSMD25E2_\\d{8}_v01r01.nc" };

        LoggerUtil.info(logger, "1. load dataset.");
        List<DenseMatrix> seralData = new ArrayList<DenseMatrix>();
        List<String> fileAssigmnt = new ArrayList<String>();

        plotWRTOnePoint(filePatternSets, seralData, fileAssigmnt, new NetCDFDtProc(), 1.0, x, y);
    }

    /**
     * load data
     * 
     * @param filePatternSets
     * @param seralData
     * @param fileAssigmnt
     * @param dProc
     */
    public static void loadingDatasetStep(String[] filePatternSets, List<DenseMatrix> seralData,
                                          List<String> fileAssigmnt, DatasetProc dProc,
                                          double samplingParam) {
        int[] rowIncluded = new int[100];
        int[] colIncluded = new int[100];
        for (int i = 0; i < rowIncluded.length; i++) {
            rowIncluded[i] = 370 + i;
            colIncluded[i] = 260 + i;
        }

        for (String filePattern : filePatternSets) {
            File[] dFiles = FileUtil.parserFilesByPattern(filePattern);
            for (File file : dFiles) {
                if (Math.random() > samplingParam) {
                    continue;
                }

                seralData.add(dProc.read(file.getAbsolutePath(), rowIncluded, colIncluded));
                fileAssigmnt.add(file.getName());

                //                String fileN = file.getName();
                //                ImageWUtil.plotImageForMEASURE(
                //                    dProc.read(file.getAbsolutePath(), rowIncluded, colIncluded),
                //                    "C:\\Users\\chench\\Desktop\\" + fileN.substring(0, fileN.indexOf('.'))
                //                            + ".png", ImageWUtil.PNG_FORMMAT);
            }
        }
    }

    public static void plotWRTOnePoint(String[] filePatternSets, List<DenseMatrix> seralData,
                                       List<String> fileAssigmnt, DatasetProc dProc,
                                       double samplingParam, int x, int y) {
        int[] rowIncluded = new int[100];
        int[] colIncluded = new int[100];
        for (int i = 0; i < rowIncluded.length; i++) {
            rowIncluded[i] = 370 + i;
            colIncluded[i] = 260 + i;
        }

        StringBuilder sp = new StringBuilder();
        int iCount = 1;
        for (String filePattern : filePatternSets) {
            File[] dFiles = FileUtil.parserFilesByPattern(filePattern);
            for (File file : dFiles) {
                if (Math.random() > samplingParam) {
                    continue;
                }

                LoggerUtil.info(logger, "reads File: " + file.getName());
                sp.append(iCount++)
                    .append("\t")
                    .append(
                        dProc.read(file.getAbsolutePath(), rowIncluded, colIncluded).getVal(x, y))
                    .append('\n');
            }
        }

        FileUtil.write("C:/Users/chench/Desktop/SIDS/Statistcs/Trends/List", sp.toString());
    }

    public static Cluster[] clusterinng(DenseMatrix diMatrix, int k, int maxIteration) {
        int rowNum = diMatrix.getRowNum();
        int colNum = diMatrix.getColNum();

        // transfer data to Samples formulation
        Samples dataSample = new Samples(rowNum * colNum, 1);
        for (int row = 0; row < rowNum; row++) {
            for (int col = 0; col < colNum; col++) {
                int index = row * colNum + col;
                dataSample.setValue(index, 0, diMatrix.getVal(row, col));
            }
        }

        // clustering the data points
        Cluster[] clusters = KMeansPlusPlusUtil.cluster(dataSample, k, maxIteration,
            KMeansPlusPlusUtil.SQUARE_EUCLIDEAN_DISTANCE);
        ClusterLocHelper.saveLoc(clusters, ROOT_DIR + "Clustering/kmean_" + k, rowNum, colNum);

        // save the clustering information
        return clusters;
    }
}
