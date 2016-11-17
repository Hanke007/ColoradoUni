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
import java.util.Properties;
import java.util.Queue;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.springframework.util.StopWatch;

import cdb.common.lang.ConfigureUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.FileUtil;
import cdb.common.lang.LoggerUtil;
import cdb.common.model.RegionInfoVO;
import cdb.dal.file.AVHRFileDtProc;
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
        SSMI("src/test/resources/zConfigQC.properties");
    }

    public static void SSMI(String configFileName) {
        Properties properties = ConfigureUtil.read(configFileName);
        int regionHeight = Integer.valueOf(properties.getProperty("REGION_HEIGHT"));
        int regionWeight = Integer.valueOf(properties.getProperty("REGION_WEIGHT"));
        String freqId = properties.getProperty("FREQ_ID");
        String rootDir = properties.getProperty("DATA_ROOT_DIR");
        String regnInfoDir = rootDir + "ClassificationDataset/" + freqId + '_' + regionHeight + '_'
                             + regionWeight + "_ORG/";

        //----------------------------
        //  business logics
        //----------------------------
        //LoggerUtil.info(logger, "1. check and read region value-object.");
        checkAndReadRegionInfoVO(rootDir, regnInfoDir, freqId, regionHeight, regionWeight);//if individual file exists, skip, if not, create one file for one object
        //LoggerUtil.info(logger, "2. make multiple thread tasks.");
        configureMultiThreadJobs(rootDir, regnInfoDir, freqId, regionHeight, regionWeight);//bing fa shu ju chuli

        // detect anomaly
        //LoggerUtil.info(logger, "3. detect potential errors.");
        StopWatch stopWatch = null;
        try {
            stopWatch = new StopWatch();
            stopWatch.start();
            ExecutorService exec = Executors.newCachedThreadPool();
            exec.execute(new DefaultQualityControllThread(configFileName));
            //exec.execute(new DefaultQualityControllThread(configFileName));
            //exec.execute(new DefaultQualityControllThread(configFileName));
            //exec.execute(new DefaultQualityControllThread(configFileName));
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
        int[] dimens = (new SSMIFileDtProc()).dimensions(freqId);//SSMI -> AVHR
        String resultFile = rootDir + "Anomaly/REG_" + freqId + '_' + regionHeight + '_'
                            + regionWeight;// result file

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
        DefaultQualityControllThread.tasks = multiThreadTasks;//quality control
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

                String fileName = regnTargetDir + regnVO.getrIndx() + '_' + regnVO.getcIndx();//mingming guize
                StringBuilder regnBuffer = regnDistbtBuffer.get(fileName);
                if (regnBuffer == null) {
                    regnBuffer = new StringBuilder();
                    regnDistbtBuffer.put(fileName, regnBuffer);
                }
                regnBuffer.append(regnVO.toString()).append('\n');

                bufferSize++;
                if (bufferSize > 1 * 1000 * 1 * 1000) {
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
