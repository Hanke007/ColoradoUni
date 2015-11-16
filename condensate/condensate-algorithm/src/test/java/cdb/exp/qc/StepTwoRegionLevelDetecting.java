package cdb.exp.qc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.springframework.util.StopWatch;

import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.FileUtil;
import cdb.common.lang.LoggerUtil;
import cdb.common.model.RegionInfoVO;
import cdb.dal.file.SSMIFileDtProc;
import cdb.ml.qc.DefaultQualityControllThread;

/**
 * 
 * @author Chao Chen
 * @version $Id: StepTwoRegionLevelDetecting.java, v 0.1 Oct 23, 2015 4:47:20 PM chench Exp $
 */
public class StepTwoRegionLevelDetecting extends AbstractDetecting {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        SSMI(false);
    }

    public static void SSMI(boolean needSaveData) {
        // thread setting
        double alpha = 2.0;
        int maxIter = 5;
        int maxClusterNum = 20;
        double potentialMaliciousRatio = 0.10;
        int regionHeight = 8;
        int regionWeight = 8;

        // read objects
        String freqId = "n19v";
        String rootDir = "C:/Users/chench/Desktop/SIDS/SSMI/";
        String regnInfoDir = rootDir + "ClassificationDataset/" + freqId + '_' + regionHeight + '_'
                             + regionWeight + "_ORG/";

        LoggerUtil.info(logger, "1. check and read region value-object.");
        checkAndReadRegionInfoVO(rootDir, regnInfoDir, freqId, regionHeight, regionWeight);
        LoggerUtil.info(logger, "2. make multiple thread tasks.");
        configureMultiThreadJobs(rootDir, regnInfoDir, freqId, regionHeight, regionWeight);

        // detect anomaly
        LoggerUtil.info(logger, "3. detect potential errors.");
        StopWatch stopWatch = null;
        try {
            stopWatch = new StopWatch();
            stopWatch.start();
            ExecutorService exec = Executors.newCachedThreadPool();
            exec.execute(new DefaultQualityControllThread(alpha, maxIter, maxClusterNum,
                potentialMaliciousRatio, regionHeight, regionWeight, needSaveData));
            exec.execute(new DefaultQualityControllThread(alpha, maxIter, maxClusterNum,
                potentialMaliciousRatio, regionHeight, regionWeight, needSaveData));
            exec.execute(new DefaultQualityControllThread(alpha, maxIter, maxClusterNum,
                potentialMaliciousRatio, regionHeight, regionWeight, needSaveData));
            exec.execute(new DefaultQualityControllThread(alpha, maxIter, maxClusterNum,
                potentialMaliciousRatio, regionHeight, regionWeight, needSaveData));
            exec.shutdown();
            exec.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
            stopWatch.stop();
        } catch (InterruptedException e) {
            ExceptionUtil.caught(e, "ExecutorService await crush! ");
        } finally {
            DefaultQualityControllThread
                .flush(rootDir + "Anomaly/REG_" + freqId + '_' + regionHeight + '_' + regionWeight);
            LoggerUtil.info(logger,
                "OVERALL TIME SPENDED: " + stopWatch.getTotalTimeMillis() / 1000.0);
        }
    }

    protected static void configureMultiThreadJobs(String rootDir, String regnInfoDir,
                                                   String freqId, int regionHeight,
                                                   int regionWeight) {
        int[] dimens = (new SSMIFileDtProc()).dimensions(freqId);
        String resultFile = rootDir + "Anomaly/REG_" + freqId + '_' + regionHeight + '_'
                            + regionWeight;

        // Entry: resultFile - sourceFile
        Queue<Entry<String, List<String>>> multiThreadTasks = new LinkedList<Entry<String, List<String>>>();
        for (int rRIndx = 0; rRIndx < dimens[0] / regionHeight; rRIndx++) {
            for (int cRIndx = 0; cRIndx < dimens[1] / regionWeight; cRIndx++) {
                String key = resultFile;
                List<String> val = new ArrayList<String>();
                val.add(regnInfoDir + rRIndx + '_' + cRIndx);

                Entry<String, List<String>> newOne = new AbstractMap.SimpleEntry<String, List<String>>(
                    key, val);
                multiThreadTasks.add(newOne);
            }
        }
        DefaultQualityControllThread.tasks = multiThreadTasks;
    }

    protected static void checkAndReadRegionInfoVO(String rootDir, String regnInfoDir,
                                                   String freqId, int regionHeight,
                                                   int regionWeight) {
        if (!FileUtil.exists(regnInfoDir)) {
            String regnSourceFile = rootDir + "ClassificationDataset/REG_" + freqId + '_'
                                    + regionHeight + '_' + regionWeight;
            readAndDistributeRegionInfoVO(regnInfoDir, regnSourceFile);
        }
    }

    protected static void readAndDistributeRegionInfoVO(String regnTargetDir,
                                                        String regnSourceFile) {
        Map<String, StringBuilder> regnDistbtBuffer = new HashMap<String, StringBuilder>();

        BufferedReader reader = null;
        try {

            int bufferSize = 0;
            reader = new BufferedReader(new FileReader(regnSourceFile));
            String line = null;
            while ((line = reader.readLine()) != null) {
                RegionInfoVO regnVO = RegionInfoVO.parseOf(line);

                String fileName = regnTargetDir + regnVO.getrIndx() + '_' + regnVO.getcIndx();
                StringBuilder regnBuffer = regnDistbtBuffer.get(fileName);
                if (regnBuffer == null) {
                    regnBuffer = new StringBuilder();
                    regnDistbtBuffer.put(fileName, regnBuffer);
                }
                regnBuffer.append(regnVO.toString()).append('\n');

                bufferSize++;
                if (bufferSize > 300 * 1000) {
                    for (Entry<String, StringBuilder> entry : regnDistbtBuffer.entrySet()) {
                        FileUtil.writeAsAppendWithDirCheck(entry.getKey(),
                            entry.getValue().toString());
                    }

                    regnDistbtBuffer.clear();
                    bufferSize = 0;
                }
            }

            if (!regnDistbtBuffer.isEmpty()) {
                for (Entry<String, StringBuilder> entry : regnDistbtBuffer.entrySet()) {
                    FileUtil.writeAsAppendWithDirCheck(entry.getKey(), entry.getValue().toString());
                }
            }

        } catch (FileNotFoundException e) {
            ExceptionUtil.caught(e, "File Not Found " + regnSourceFile);
        } catch (IOException e) {
            ExceptionUtil.caught(e, "Reading Error");
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

}
