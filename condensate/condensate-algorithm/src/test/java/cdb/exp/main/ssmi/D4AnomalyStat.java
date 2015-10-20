package cdb.exp.main.ssmi;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cdb.common.lang.DateUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.LoggerUtil;
import cdb.common.lang.SerializeUtil;
import cdb.common.lang.StatisticParamUtil;
import cdb.common.lang.VisualizationUtil;
import cdb.dal.vo.DenseMatrix;
import cdb.dal.vo.SparseMatrix;
import cdb.service.dataset.DatasetProc;
import cdb.service.dataset.SSMIFileDtProc;

/**
 * 
 * @author Chao Chen
 * @version $Id: D4AnomalyStat.java, v 0.1 Oct 19, 2015 1:39:02 PM chench Exp $
 */
public class D4AnomalyStat extends AbstractArcticAnalysis {

    /** frequency identity*/
    protected final static String FREQNCY_ID = "s85v";

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        case2();
    }

    public static void case2() {
        // make task lists
        List<String> taskIds = null;
        try {
            Date sDate = DateUtil.parse("19930101", DateUtil.SHORT_FORMAT);
            Date eDate = DateUtil.parse("19940101", DateUtil.SHORT_FORMAT);
            taskIds = workingSetGen(sDate, eDate);
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "Check date format.");
        }

        // computer parameter
        List<DenseMatrix> srlData = new ArrayList<DenseMatrix>();
        DatasetProc dataProc = new SSMIFileDtProc();
        for (String fileAnml : taskIds) {
            DenseMatrix cMatrix = dataProc.read(fileAnml);
            if (cMatrix == null) {
                continue;
            }

            srlData.add(StatisticParamUtil.distribution(cMatrix, 0, 500, 5, 1.0));
        }

        DenseMatrix mean = StatisticParamUtil.mean(srlData);
        DenseMatrix sd = StatisticParamUtil.sd(srlData, mean);
        LoggerUtil.info(logger, mean);
        LoggerUtil.info(logger, sd);
    }

    public static void case1() {
        // make task lists
        List<String> taskIds = null;
        try {
            Date sDate = DateUtil.parse("19900701", DateUtil.SHORT_FORMAT);
            Date eDate = DateUtil.parse("20140831", DateUtil.SHORT_FORMAT);
            taskIds = workingSetGen(sDate, eDate);
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "Check date format.");
        }

        int[] dimsn = dimensions(FREQNCY_ID);
        DenseMatrix cMatrix = new DenseMatrix(dimsn[0], dimsn[1]);
        for (String fileAnml : taskIds) {
            SparseMatrix sMatrix = (SparseMatrix) SerializeUtil.readObject(fileAnml);
            if (sMatrix == null) {
                continue;
            }

            int[] len = sMatrix.length();
            for (int row = 0; row < len[0]; row++) {
                int[] cList = sMatrix.getRowRef(row).indexList();
                if (cList == null) {
                    continue;
                }

                for (int col : cList) {
                    cMatrix.add(row, col, 1);
                }
            }
        }

        VisualizationUtil.gnuHeatmap(cMatrix, ROOT_DIR + 1);

        int nullCount = 0;
        for (int row = 0; row < dimsn[0]; row++) {
            for (int col = 0; col < dimsn[1]; col++) {
                if (cMatrix.getVal(row, col) == 0.0d) {
                    nullCount++;
                }
            }
        }

        LoggerUtil.info(logger, "No Anomaly rate: " + nullCount * 1.0 / (dimsn[0] * dimsn[1]));
    }

    public static List<String> workingSetGen(Date sDate, Date eDate) {
        List<String> testSet = new ArrayList<String>();

        Date curDate = sDate;
        while (curDate.before(eDate)) {
            String fileAnml = ROOT_DIR + "StatisticAnomaly/"
                              + DateUtil.format(curDate, DateUtil.SHORT_FORMAT) + '_' + FREQNCY_ID
                              + ".OBJ";
            testSet.add(fileAnml);

            //move to next day
            curDate.setTime(curDate.getTime() + 24 * 60 * 60 * 1000);
        }
        return testSet;
    }
}
