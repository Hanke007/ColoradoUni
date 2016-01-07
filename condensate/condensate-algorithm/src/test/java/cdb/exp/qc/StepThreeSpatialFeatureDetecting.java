package cdb.exp.qc;

import java.text.ParseException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.util.StopWatch;

import cdb.common.lang.ConfigureUtil;
import cdb.common.lang.DateUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.LoggerUtil;
import cdb.dal.file.DatasetProc;
import cdb.dal.file.SSMIFileDtProc;
import cdb.dataset.util.BinFileConvntnUtil;
import cdb.ml.qc.SpatialFeatureQualityControllThread;

/**
 * 
 * @author Chao Chen
 * @version $Id: StepThreeSpatialFeatureDetecting.java, v 0.1 Dec 2, 2015 11:07:18 AM chench Exp $
 */
public class StepThreeSpatialFeatureDetecting extends AbstractDetecting {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        SSMI("src/test/resources/zConfigQC.properties", new SSMIFileDtProc());
    }

    public static void SSMI(String configFileName, DatasetProc dProc) {
        Properties properties = ConfigureUtil.read(configFileName);
        String freqId = properties.getProperty("FREQ_ID");
        String rootDir = properties.getProperty("DATA_ROOT_DIR");
        String beginDate = properties.getProperty("BEGIN_DATE");
        String endDate = properties.getProperty("END_DATE");

        LoggerUtil.info(logger, "1. make multiple thread tasks.");
        configureMultiThreadJobs(rootDir, freqId, beginDate, endDate, 1, 1);

        // detect anomaly
        LoggerUtil.info(logger, "2. detect potential errors.");
        StopWatch stopWatch = null;
        try {
            stopWatch = new StopWatch();
            stopWatch.start();
            ExecutorService exec = Executors.newCachedThreadPool();
            exec.execute(new SpatialFeatureQualityControllThread(configFileName, dProc));
            exec.execute(new SpatialFeatureQualityControllThread(configFileName, dProc));
            exec.execute(new SpatialFeatureQualityControllThread(configFileName, dProc));
            exec.execute(new SpatialFeatureQualityControllThread(configFileName, dProc));
            exec.shutdown();
            exec.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
            stopWatch.stop();
        } catch (InterruptedException e) {
            ExceptionUtil.caught(e, "ExecutorService await crush! ");
        } finally {
            SpatialFeatureQualityControllThread
                .flush(rootDir + "Anomaly/COLD_" + freqId + '_' + 1 + '_' + 1);
            LoggerUtil.info(logger,
                "OVERALL TIME SPENDED: " + stopWatch.getTotalTimeMillis() / 1000.0);
        }

    }

    protected static void configureMultiThreadJobs(String rootDir, String freqId, String beginDate,
                                                   String endDate, int regionHeight,
                                                   int regionWeight) {
        try {
            Date bDate = DateUtil.parse(beginDate, DateUtil.SHORT_FORMAT);
            Date eDate = DateUtil.parse(endDate, DateUtil.SHORT_FORMAT);
            String resultFile = rootDir + "Anomaly/COLD_" + freqId + '_' + regionHeight + '_'
                                + regionWeight;

            // Entry: resultFile - sourceFile
            Queue<Entry<String, List<String>>> multiThreadTasks = new LinkedList<Entry<String, List<String>>>();

            Date curDate = bDate;
            while (curDate.before(eDate)) {
                String key = resultFile;
                List<String> val = new ArrayList<String>();
                val.add(BinFileConvntnUtil.fileAVHR(rootDir,
                    DateUtil.format(curDate, DateUtil.SHORT_FORMAT), freqId));
                //move to next day
                curDate.setTime(curDate.getTime() + 24 * 60 * 60 * 1000);

                Entry<String, List<String>> newOne = new AbstractMap.SimpleEntry<String, List<String>>(
                    key, val);
                multiThreadTasks.add(newOne);
            }

            SpatialFeatureQualityControllThread.tasks = multiThreadTasks;
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "Check date format.");
        }

    }
}
