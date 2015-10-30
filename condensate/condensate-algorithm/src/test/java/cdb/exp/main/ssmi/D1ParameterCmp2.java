package cdb.exp.main.ssmi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.util.StopWatch;

import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.LoggerUtil;
import cdb.common.lang.MatrixFileUtil;
import cdb.common.lang.SerializeUtil;
import cdb.dal.vo.DenseMatrix;
import cdb.service.dataset.DatasetProc;
import cdb.service.dataset.SSMIFileDtProc;

/**
 * 
 * @author Chao Chen
 * @version $Id: D1ParameterCmp2.java, v 0.1 Oct 12, 2015 2:47:08 PM chench Exp $
 */
public class D1ParameterCmp2 extends AbstractArcticAnalysis {
    /** frequency identity*/
    protected final static String FREQNCY_ID = "n19v";

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        case1();
        case2();
        stopWatch.stop();
        LoggerUtil.info(logger, "OVERALL TIME SPENDED: " + stopWatch.getTotalTimeMillis() / 1000.0);
    }

    public static void case1() {

        // make task queues
        int yearStart = 1993;
        int yearEnd = 2014;
        String[] years = new String[yearEnd - yearStart + 1];
        for (int year = yearStart; year <= yearEnd; year++) {
            years[year - yearStart] = String.valueOf(year);
        }
        String[] seasons = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
                             "12" };
        Queue<String> taskIds = new LinkedList<String>();
        for (String season : seasons) {
            taskIds.add(season);
        }

        // run bussiness
        StopWatch stopWatch = new StopWatch();
        try {
            stopWatch.start();
            CmpThread.task = taskIds;
            CmpThread.years = years;
            ExecutorService exec = Executors.newCachedThreadPool();
            exec.execute(new CmpThread());
            exec.execute(new CmpThread());
            exec.execute(new CmpThread());
            exec.execute(new CmpThread());
            exec.shutdown();
            exec.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
            stopWatch.stop();
        } catch (InterruptedException e) {
            ExceptionUtil.caught(e, "ExecutorService await crush! ");
        } finally {
            LoggerUtil.info(logger,
                "Task completed, time consuming: " + stopWatch.getTotalTimeSeconds());
        }

    }

    public static void case2() {
        String[] seasons = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
                             "12" };
        // make regular files
        HashMap<String, DenseMatrix> meanRep = new HashMap<String, DenseMatrix>();
        HashMap<String, DenseMatrix> sdRep = new HashMap<String, DenseMatrix>();
        int seasonNum = seasons.length;
        for (int seasonIndx = 0; seasonIndx < seasonNum; seasonIndx++) {
            String meanFile = ROOT_DIR + "Condensate/mean_" + seasons[seasonIndx] + "_" + FREQNCY_ID
                              + ".OBJ";

            DenseMatrix mean = (DenseMatrix) SerializeUtil.readObject(meanFile);
            meanRep.put(seasons[seasonIndx], mean);

            String sdFile = ROOT_DIR + "Condensate/sd_" + seasons[seasonIndx] + "_" + FREQNCY_ID
                            + ".OBJ";
            DenseMatrix sd = (DenseMatrix) SerializeUtil.readObject(sdFile);
            sdRep.put(seasons[seasonIndx], sd);
        }
        SerializeUtil.writeObject(meanRep, ROOT_DIR + "Condensate/mean_" + FREQNCY_ID + ".OBJ");
        SerializeUtil.writeObject(sdRep, ROOT_DIR + "Condensate/sd_" + FREQNCY_ID + ".OBJ");
    }

    public static class CmpThread extends Thread {

        public static Queue<String> task;
        public static String[]      years;

        public static synchronized String task() {
            return task.poll();
        }

        /** 
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            DatasetProc dProc = new SSMIFileDtProc();

            String taskId = null;
            while ((taskId = task()) != null) {

                int[] dimsns = dProc.dimensions(FREQNCY_ID);
                DenseMatrix means = new DenseMatrix(dimsns[0], dimsns[1]);
                DenseMatrix meanSquares = new DenseMatrix(dimsns[0], dimsns[1]);
                DenseMatrix counts = new DenseMatrix(dimsns[0], dimsns[1]);

                // accomplish the file name with regular pattern
                for (String yearStr : years) {
                    int year = Integer.valueOf(yearStr);
                    int timeRange = year * 100 + Integer.valueOf(taskId);
                    String fileRE = ROOT_DIR + year + "/";
                    if (timeRange < 199201) {
                        fileRE += "tb_f08_" + timeRange + "\\d{2}_v2_" + FREQNCY_ID + ".bin";
                    } else if (timeRange < 199601) {
                        fileRE += "tb_f11_" + timeRange + "\\d{2}_v2_" + FREQNCY_ID + ".bin";
                    } else if (timeRange < 200901) {
                        fileRE += "tb_f13_" + timeRange + "\\d{2}_v2_" + FREQNCY_ID + ".bin";
                    } else if (timeRange < 201001) {
                        fileRE += "tb_f13_" + timeRange + "\\d{2}_v3_" + FREQNCY_ID + ".bin";
                    } else {
                        fileRE += "tb_f17_" + timeRange + "\\d{2}_v4_" + FREQNCY_ID + ".bin";
                    }

                    // load data set
                    List<DenseMatrix> partialData = new ArrayList<DenseMatrix>();
                    MatrixFileUtil.read(fileRE, partialData, dProc, 1.0d);
                    if (partialData.isEmpty()) {
                        LoggerUtil.warn(logger, timeRange + " is empty.\t\t[*]");
                        continue;
                    } else {
                        for (DenseMatrix denseMatrix : partialData) {
                            for (int row = 0; row < dimsns[0]; row++) {
                                for (int col = 0; col < dimsns[1]; col++) {
                                    double val = denseMatrix.getVal(row, col);
                                    if (Double.isNaN(val)) {
                                        // no observation
                                        continue;
                                    }

                                    means.add(row, col, val);
                                    meanSquares.add(row, col, val * val);
                                    counts.add(row, col, 1);
                                }
                            }
                        }
                    }
                }

                // compute parameters
                for (int row = 0; row < dimsns[0]; row++) {
                    for (int col = 0; col < dimsns[1]; col++) {
                        double count = counts.getVal(row, col);
                        if (Double.isNaN(count) | count == 0.0d) {
                            // no observation
                            continue;
                        }

                        double mean = means.getVal(row, col) / count;
                        double meanSquare = meanSquares.getVal(row, col) / (count - 1);

                        means.setVal(row, col, mean);
                        meanSquares.setVal(row, col, Math.sqrt(meanSquare - mean * mean));
                    }
                }

                // computer the parameters
                String meanFileName = meanSerialNameConvntn(taskId, FREQNCY_ID);
                SerializeUtil.writeObject(means, meanFileName);

                String sdFileName = sdSerialNameConvntn(taskId, FREQNCY_ID);
                SerializeUtil.writeObject(meanSquares, sdFileName);
                LoggerUtil.info(logger, taskId + " completed");
            }
        }

    }
}
