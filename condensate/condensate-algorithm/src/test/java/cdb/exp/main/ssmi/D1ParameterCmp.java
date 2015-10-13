package cdb.exp.main.ssmi;

import java.util.ArrayList;
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
import cdb.common.lang.StatisticParamUtil;
import cdb.dal.vo.DenseMatrix;
import cdb.service.dataset.SSMIFileDtProc;

/**
 * 
 * @author Chao Chen
 * @version $Id: D1ParameterCmp.java, v 0.1 Sep 28, 2015 9:48:58 AM chench Exp $
 */
public class D1ParameterCmp extends AbstractArcticAnalysis {

    /** frequency identity*/
    protected final static String FREQNCY_ID = "s19v";

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        case1();
    }

    public static void case1() {

        // make task queues
        int yearStart = 1987;
        int yearEnd = 2014;
        String[] years = new String[yearEnd - yearStart + 1];
        for (int year = yearStart; year <= yearEnd; year++) {
            years[year - yearStart] = String.valueOf(year);
        }
        String[] seasons = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
                             "12" };
        Queue<String> taskIds = new LinkedList<String>();
        for (String year : years) {
            for (String season : seasons) {
                taskIds.add(year + season);
            }
        }

        // run bussiness
        StopWatch stopWatch = new StopWatch();
        try {
            stopWatch.start();
            CmpThread.task = taskIds;
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

    public static class CmpThread extends Thread {

        public static Queue<String> task;

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

                // accomplish the file name with regular pattern

                int timeRange = Integer.valueOf(taskId);
                int year = timeRange / 100;
                String fileRE = ROOT_DIR + year + "/";
                if (timeRange < 199201) {
                    fileRE += "tb_f08_" + taskId + "\\d{2}_v2_" + FREQNCY_ID + ".bin";
                } else if (timeRange < 199601) {
                    fileRE += "tb_f11_" + taskId + "\\d{2}_v2_" + FREQNCY_ID + ".bin";
                } else if (timeRange < 200901) {
                    fileRE += "tb_f13_" + taskId + "\\d{2}_v2_" + FREQNCY_ID + ".bin";
                } else if (timeRange < 201001) {
                    fileRE += "tb_f13_" + taskId + "\\d{2}_v3_" + FREQNCY_ID + ".bin";
                } else {
                    fileRE += "tb_f17_" + taskId + "\\d{2}_v4_" + FREQNCY_ID + ".bin";
                }

                // load data set
                List<DenseMatrix> seralData = new ArrayList<DenseMatrix>();
                MatrixFileUtil.read(fileRE, seralData, new SSMIFileDtProc(), 1.0d);
                if (seralData.isEmpty()) {
                    LoggerUtil.warn(logger, taskId + " is empty.\t\t[*]");
                    continue;
                }

                // computer the parameters
                String meanFileName = meanSerialNameConvntn(taskId, FREQNCY_ID);
                DenseMatrix means = StatisticParamUtil.mean(seralData);
                SerializeUtil.writeObject(means, meanFileName);

                String sdFileName = sdSerialNameConvntn(taskId, FREQNCY_ID);
                DenseMatrix sds = StatisticParamUtil.sd(seralData, means);
                SerializeUtil.writeObject(sds, sdFileName);
                LoggerUtil.info(logger, taskId + " completed");
            }
        }

    }
}
