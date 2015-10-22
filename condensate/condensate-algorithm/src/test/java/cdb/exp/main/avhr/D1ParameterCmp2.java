package cdb.exp.main.avhr;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.util.StopWatch;

import cdb.common.lang.DateUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.LoggerUtil;
import cdb.common.lang.MatrixFileUtil;
import cdb.common.lang.SerializeUtil;
import cdb.dal.vo.DenseMatrix;
import cdb.service.dataset.AVHRFileDtProc;

/**
 * 
 * @author Chao Chen
 * @version $Id: D1ParameterCmp2.java, v 0.1 Oct 12, 2015 2:47:08 PM chench Exp $
 */
public class D1ParameterCmp2 extends AVHRAnalysis {
    /** frequency identity*/
    protected final static String FREQNCY_ID = "1400_chn4";

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
        Map<String, List<String>> taskContext = null;
        try {
            LoggerUtil.info(logger, "2. making working set.");
            Date sDate = DateUtil.parse("20000101", DateUtil.SHORT_FORMAT);
            Date eDate = DateUtil.parse("20010101", DateUtil.SHORT_FORMAT);
            taskContext = imgWorkingSetGen(sDate, eDate, FREQNCY_ID);
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "Check date format.");
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
            CmpThread.taskContext = taskContext;
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

        public static Queue<String>             task;
        public static Map<String, List<String>> taskContext;

        public static synchronized String task() {
            return task.poll();
        }

        /** 
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            String taskId = null;
            while ((taskId = task()) != null) {

                int[] dimsns = dimensions(FREQNCY_ID);
                DenseMatrix means = new DenseMatrix(dimsns[0], dimsns[1]);
                DenseMatrix meanSquares = new DenseMatrix(dimsns[0], dimsns[1]);
                DenseMatrix counts = new DenseMatrix(dimsns[0], dimsns[1]);

                // accomplish the file name with regular pattern
                List<String> fileRES = taskContext.get(taskId);
                for (String fileRE : fileRES) {

                    // load data set
                    List<DenseMatrix> partialData = new ArrayList<DenseMatrix>();
                    MatrixFileUtil.read(fileRE, partialData, new AVHRFileDtProc(), 1.0d);
                    if (partialData.isEmpty()) {
                        LoggerUtil.warn(logger, fileRE + " is empty.\t\t[*]");
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
