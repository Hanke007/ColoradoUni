package cdb.exp.analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cdb.common.lang.FileUtil;
import cdb.common.lang.VisualizationUtil;
import cdb.common.model.DenseMatrix;
import cdb.dal.file.DatasetProc;
import cdb.dal.file.SSMIFileDtProc;

/**
 * 
 * @author Chao Chen
 * @version $Id: StatisticAnalysis.java, v 0.1 Jul 24, 2015 10:28:05 AM chench Exp $
 */
public class StatisticAnalysis {

    public final static String filePattern = "C:/Users/chench/Desktop/SIDS/2014/tb_f17_201401\\d{2}_v4_s19h.bin";

    //    public final static String filePattern = "C:/Users/chench/Desktop/SIDS/2014/tb_f17_20141108_v4_s19h.bin";

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        cmpDistrHierarchy();
    }

    protected void cmpStat() {
        // load data
        File[] dFiles = FileUtil.parserFilesByPattern(filePattern);
        DatasetProc dProc = new SSMIFileDtProc();
        List<DenseMatrix> seralData = new ArrayList<DenseMatrix>();
        for (File file : dFiles) {
            seralData.add(dProc.read(file.getAbsolutePath()));
        }

        // compute statistics
        DenseMatrix EX = new DenseMatrix(seralData.get(0).getRowNum(),
            seralData.get(0).getColNum());
        cmpEX(seralData, EX);

        DenseMatrix EXX = new DenseMatrix(seralData.get(0).getRowNum(),
            seralData.get(0).getColNum());
        cmpEXX(seralData, EXX);

        DenseMatrix SD = new DenseMatrix(seralData.get(0).getRowNum(),
            seralData.get(0).getColNum());
        cmpSD(EX, EXX, SD);

        VisualizationUtil.gnuHeatmap(EX, "C:/Users/chench/Desktop/SIDS/mean_201412");
        VisualizationUtil.gnuHeatmap(SD, "C:/Users/chench/Desktop/SIDS/sd_201412");
    }

    public static void cmpDistrHierarchy() {
        // load data
        File[] dFiles = FileUtil.parserFilesByPattern(filePattern);
        DatasetProc dProc = new SSMIFileDtProc();
        List<DenseMatrix> seralData = new ArrayList<DenseMatrix>();
        for (File file : dFiles) {
            seralData.add(dProc.read(file.getAbsolutePath()));
        }

        // compute statistics
        DenseMatrix EX = new DenseMatrix(seralData.get(0).getRowNum(),
            seralData.get(0).getColNum());
        cmpEX(seralData, EX);

        //compute the hierarchical information 
        int stepSize = 100;
        int max = 3000;
        double[] numInStep = new double[max / stepSize];
        cmpHierarchy(EX, stepSize, max, numInStep);

        StringBuilder content = new StringBuilder();
        for (int i = 0; i < numInStep.length; i++) {
            content.append((i + 1) * stepSize).append('\t').append(numInStep[i]).append('\n');
        }
        FileUtil.write("C:/Users/chench/Desktop/SIDS/hmp", content.toString());
    }

    /**
     * Compute the mean of each random variable, e.g. EX
     * 
     * @param seralData     the samples to compute squared mean
     * @param result        the resulting EX
     */
    protected static void cmpEX(List<DenseMatrix> seralData, DenseMatrix result) {
        // aggregate
        int rowNum = seralData.get(0).getRowNum();
        int colNum = seralData.get(0).getColNum();
        for (DenseMatrix one : seralData) {
            for (int row = 0; row < rowNum; row++) {
                for (int col = 0; col < colNum; col++) {
                    result.add(row, col, one.getVal(row, col));
                }
            }
        }

        // compute mean
        result.scale(seralData.size());
    }

    /**
     * Compute the mean of squared value of each random variable, e.g. EXX
     * 
     * @param seralData     the samples to compute squared mean
     * @param result        the resulting EXX
     */
    protected static void cmpEXX(List<DenseMatrix> seralData, DenseMatrix result) {
        // aggregate
        int rowNum = seralData.get(0).getRowNum();
        int colNum = seralData.get(0).getColNum();
        for (DenseMatrix one : seralData) {
            for (int row = 0; row < rowNum; row++) {
                for (int col = 0; col < colNum; col++) {
                    int addOne = (int) Math.pow(one.getVal(row, col), 2.0);
                    result.add(row, col, addOne);
                }
            }
        }

        // compute mean
        result.scale(seralData.size());
    }

    /**
     * Compute standard deviation of each random variable
     * 
     * @param EX        mean
     * @param EXX       squared mean
     * @param SD        standard deviation
     */
    protected static void cmpSD(DenseMatrix EX, DenseMatrix EXX, DenseMatrix SD) {
        int rowNum = EX.getRowNum();
        int colNum = EX.getColNum();
        for (int row = 0; row < rowNum; row++) {
            for (int col = 0; col < colNum; col++) {
                double DxVal = EXX.getVal(row, col) - Math.pow(EX.getVal(row, col), 2.0d);
                SD.add(row, col, (int) Math.sqrt(DxVal));
            }
        }
    }

    /**
     * 
     * 
     * @param speciData
     * @param stepSize
     * @param max
     * @param numInStep
     */
    protected static void cmpHierarchy(DenseMatrix speciData, int stepSize, int max,
                                       double[] numInStep) {
        int rowNum = speciData.getRowNum();
        int colNum = speciData.getColNum();
        for (int row = 0; row < rowNum; row++) {
            for (int col = 0; col < colNum; col++) {
                double val = speciData.getVal(row, col);
                int position = (int) (val / stepSize);
                numInStep[position]++;
            }
        }

        int total = rowNum * colNum;
        for (int i = 0; i < numInStep.length; i++) {
            numInStep[i] /= total;
        }
    }
}
