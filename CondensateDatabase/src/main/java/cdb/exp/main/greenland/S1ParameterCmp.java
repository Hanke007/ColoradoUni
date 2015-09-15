package cdb.exp.main.greenland;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cdb.common.lang.FileUtil;
import cdb.common.lang.LoggerUtil;
import cdb.common.lang.MatrixFileUtil;
import cdb.common.lang.StatisticParamUtil;
import cdb.common.lang.log4j.LoggerDefineConstant;
import cdb.dal.vo.DenseIntMatrix;
import cdb.service.dataset.DatasetProc;
import cdb.service.dataset.NetCDFDtProc;

/**
 * 
 * @author Chao Chen
 * @version $Id: S1ParameterCmp.java, v 0.1 Sep 15, 2015 12:02:03 PM chench Exp $
 */
public class S1ParameterCmp {

    /** logger */
    private final static Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
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
        List<DenseIntMatrix> seralData = new ArrayList<DenseIntMatrix>();
        List<String> fileAssigmnt = new ArrayList<String>();
        loadingDatasetStep(filePatternSets, seralData, fileAssigmnt, new NetCDFDtProc(), 1.0);

        LoggerUtil.info(logger, "2. compute statistical parameter.");
        DenseIntMatrix centroid = StatisticParamUtil.mean(seralData);
        MatrixFileUtil.gnuHeatmap(centroid, "C:\\Users\\chench\\Desktop\\mean");
        DenseIntMatrix sd = StatisticParamUtil.sd(seralData, centroid);
        MatrixFileUtil.gnuHeatmap(sd, "C:\\Users\\chench\\Desktop\\sd");
    }

    /**
     * load data
     * 
     * @param filePatternSets
     * @param seralData
     * @param fileAssigmnt
     * @param dProc
     */
    public static void loadingDatasetStep(String[] filePatternSets, List<DenseIntMatrix> seralData,
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
            }
        }
    }

    public static void plotWRTOnePoint(String[] filePatternSets, List<DenseIntMatrix> seralData,
                                       List<String> fileAssigmnt, DatasetProc dProc,
                                       double samplingParam) {
        StringBuilder sp = new StringBuilder();
        int iCount = 1;
        for (String filePattern : filePatternSets) {
            File[] dFiles = FileUtil.parserFilesByPattern(filePattern);
            for (File file : dFiles) {
                if (Math.random() > samplingParam) {
                    continue;
                }

                LoggerUtil.info(logger, "reads File: " + file.getName());
                //                seralData.add(dProc.read(file.getAbsolutePath()));
                //                fileAssigmnt.add(file.getName());
                sp.append(iCount++).append("\t")
                    .append(dProc.read(file.getAbsolutePath()).getVal(443, 304)).append('\n');
            }
        }

        FileUtil.write("C:/Users/chench/Desktop/SIDS/List", sp.toString());
    }
}
