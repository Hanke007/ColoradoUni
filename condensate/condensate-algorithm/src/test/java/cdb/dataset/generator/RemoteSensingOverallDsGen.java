package cdb.dataset.generator;

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

import org.apache.log4j.Logger;
import org.springframework.util.StopWatch;

import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.FileUtil;
import cdb.common.lang.LoggerUtil;
import cdb.common.lang.SerializeUtil;
import cdb.common.lang.log4j.LoggerDefineConstant;
import cdb.dal.vo.DenseMatrix;
import cdb.dataset.generator.AVHRSourceDumpImpl;
import cdb.dataset.generator.AnomalyInfoVOTransformerImpl;
import cdb.dataset.generator.ImageInfoVOTransformerImpl;
import cdb.dataset.generator.RemoteSensingGen;
import cdb.dataset.generator.SSMISourceDumpImpl;
import cdb.dataset.generator.ui.DsGenFrame;
import cdb.dataset.parameter.DefaultParamThread;
import cdb.service.dataset.AVHRFileDtProc;
import cdb.service.dataset.SSMIFileDtProc;

/**
 * 
 * @author Chao Chen
 * @version $Id: ImageSSMIDsGen.java, v 0.1 Oct 22, 2015 4:09:10 PM chench Exp $
 */
public class RemoteSensingOverallDsGen {

    /** logger */
    protected final static Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        regionSSMI();
    }

    public static void imageSSMI() {
        String rootDir = "C:/Users/chench/Desktop/SIDS/SSMI/";
        String freqId = "n22v";
        String sDateStr = "19900101";
        String eDateStr = "20150101";

        double sampleRatio = 0.8;
        double minVal = 0.0d;
        double maxVal = 500.0d;
        int k = 5;

        RemoteSensingGen rsGen = new RemoteSensingGen(rootDir, sDateStr, eDateStr, freqId);
        rsGen.setDataProc(new SSMIFileDtProc());
        rsGen.setSourceDumper(new SSMISourceDumpImpl());
        rsGen.setDataTransformer(new ImageInfoVOTransformerImpl(sampleRatio, minVal, maxVal, k));
        rsGen.run();
    }

    public static void imageAVHR() {
        String rootDir = "C:/Users/chench/Desktop/SIDS/AVHR/";
        String freqId = "1400_chn4";
        String sDateStr = "20000101";
        String eDateStr = "20010101";

        double sampleRatio = 0.8;
        double minVal = 0.0d;
        double maxVal = 500.0d;
        int k = 5;

        RemoteSensingGen rsGen = new RemoteSensingGen(rootDir, sDateStr, eDateStr, freqId);
        rsGen.setDataProc(new AVHRFileDtProc());
        rsGen.setSourceDumper(new AVHRSourceDumpImpl());
        rsGen.setDataTransformer(new ImageInfoVOTransformerImpl(sampleRatio, minVal, maxVal, k));
        rsGen.run();
    }

    public static void anamlySSMI() {
        String rootDir = "C:/Users/chench/Desktop/SIDS/SSMI/";
        String freqId = "s22v";
        String sDateStr = "19920110";
        String eDateStr = "19920111";

        int k = 50;
        double alpha = 2.0d;
        int maxIter = 20;

        RemoteSensingGen rsGen = new RemoteSensingGen(rootDir, sDateStr, eDateStr, freqId);
        rsGen.setDataProc(new SSMIFileDtProc());
        rsGen.setSourceDumper(new SSMISourceDumpImpl());
        rsGen.setDataTransformer(new AnomalyInfoVOTransformerImpl(k, alpha, maxIter));
        rsGen.run();

        String rootDataDir = "C:/Users/chench/Desktop/SIDS/SSMI/ClassificationDataset/";
        String rootImageDir = "C:/Users/chench/Desktop/SIDS/SSMI/Anomaly/2000LowFreq/";
        DsGenFrame frame = new DsGenFrame(rootDataDir, rootImageDir);
        frame.pack();
        frame.setLocation(300, 20);
        frame.setSize(670, 560);
        frame.setVisible(true);
    }

    public static void regionSSMI() {
        String rootDir = "C:/Users/chench/Desktop/SIDS/SSMI/";
        int regionHeight = 8;
        int regionWeight = 8;
        int minVal = 0;
        int maxVal = 400;
        int k = 5;
        String freqId = "n19v";
        String sDateStr = "20100101";
        String eDateStr = "20140101";

        LoggerUtil.info(logger, "1. Achieve parameters.");
        Map<String, DenseMatrix> meanRep = new HashMap<String, DenseMatrix>();
        Map<String, DenseMatrix> sdRep = new HashMap<String, DenseMatrix>();
        achieveParam(rootDir, regionHeight, regionWeight, meanRep, sdRep, freqId);

        LoggerUtil.info(logger, "2. Making region objects.");
        RemoteSensingGen rsGen = new RemoteSensingGen(rootDir, sDateStr, eDateStr, freqId);
        rsGen.setDataProc(new SSMIFileDtProc());
        rsGen.setSourceDumper(new SSMISourceDumpImpl());
        rsGen.setDataTransformer(new RegionIfnoVOTransformerImpl(regionHeight, regionWeight, minVal,
            maxVal, k, meanRep, sdRep));
        rsGen.run();
    }

    protected static void achieveParam(String rootDir, int regionHeight, int regionWeight,
                                       Map<String, DenseMatrix> meanRep,
                                       Map<String, DenseMatrix> sdRep, String freqId) {
        paramRead(rootDir, regionHeight, regionWeight, meanRep, sdRep, freqId);

        // if no such information, then compute it
        if (meanRep.isEmpty() & sdRep.isEmpty()) {
            paramCmpSSMI(rootDir, regionHeight, regionWeight, meanRep, sdRep, freqId);
        }
    }

    protected static void paramCmpSSMI(String rootDir, int regionHeight, int regionWeight,
                                       Map<String, DenseMatrix> meanRep,
                                       Map<String, DenseMatrix> sdRep, String freqId) {
        String[] seasons = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
                             "12" };
        int sYear = 1992;
        int eYear = 2014;

        Queue<Entry<String, List<String>>> multiThreadTasks = new LinkedList<Entry<String, List<String>>>();
        for (String season : seasons) {
            // regionHeight_regionWeight_season_frequencyId
            String dirIdSerial = rootDir + "Condensate/" + regionHeight + '_' + regionWeight + '_'
                                 + season + '_' + freqId + '_';
            List<String> fileRes = new ArrayList<String>();
            Entry<String, List<String>> newOne = new AbstractMap.SimpleEntry<String, List<String>>(
                dirIdSerial, fileRes);
            multiThreadTasks.add(newOne);

            for (int year = sYear; year <= eYear; year++) {
                int timeRange = year * 100 + Integer.valueOf(season);
                String fileRE = rootDir + year + "/";
                if (timeRange < 199201) {
                    fileRE += "tb_f08_" + timeRange + "\\d{2}_v2_" + freqId + ".bin";
                } else if (timeRange < 199601) {
                    fileRE += "tb_f11_" + timeRange + "\\d{2}_v2_" + freqId + ".bin";
                } else if (timeRange < 200901) {
                    fileRE += "tb_f13_" + timeRange + "\\d{2}_v2_" + freqId + ".bin";
                } else if (timeRange < 201001) {
                    fileRE += "tb_f13_" + timeRange + "\\d{2}_v3_" + freqId + ".bin";
                } else {
                    fileRE += "tb_f17_" + timeRange + "\\d{2}_v4_" + freqId + ".bin";
                }

                fileRes.add(fileRE);
            }
        }

        // multiple thread module
        StopWatch stopWatch = new StopWatch();
        try {
            stopWatch.start();
            DefaultParamThread.tasks = multiThreadTasks;
            ExecutorService exec = Executors.newCachedThreadPool();
            exec.execute(
                new DefaultParamThread(regionHeight, regionWeight, freqId, new SSMIFileDtProc()));
            exec.execute(
                new DefaultParamThread(regionHeight, regionWeight, freqId, new SSMIFileDtProc()));
            exec.execute(
                new DefaultParamThread(regionHeight, regionWeight, freqId, new SSMIFileDtProc()));
            exec.execute(
                new DefaultParamThread(regionHeight, regionWeight, freqId, new SSMIFileDtProc()));
            exec.shutdown();
            exec.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
            stopWatch.stop();
        } catch (InterruptedException e) {
            ExceptionUtil.caught(e, "ExecutorService await crush! ");
        } finally {
            LoggerUtil.info(logger,
                "Task completed, time consuming: " + stopWatch.getTotalTimeSeconds());
        }

        // read and save parameters
        for (String season : seasons) {
            String meanFile = rootDir + "Condensate/" + regionHeight + '_' + regionWeight + '_'
                              + season + '_' + freqId + "_mean.OBJ";
            DenseMatrix mean = (DenseMatrix) SerializeUtil.readObject(meanFile);
            meanRep.put(season, mean);

            String sdFile = rootDir + "Condensate/" + regionHeight + '_' + regionWeight + '_'
                            + season + '_' + freqId + "_sd.OBJ";
            DenseMatrix sd = (DenseMatrix) SerializeUtil.readObject(sdFile);
            sdRep.put(season, sd);
        }
        SerializeUtil.writeObject(meanRep, rootDir + "Condensate/" + regionHeight + '_'
                                           + regionWeight + "_mean_" + freqId + ".OBJ");

        SerializeUtil.writeObject(sdRep,
            rootDir + "Condensate/" + regionHeight + '_' + regionWeight + "_sd_" + freqId + ".OBJ");
    }

    @SuppressWarnings("unchecked")
    protected static void paramRead(String rootDir, int regionHeight, int regionWeight,
                                    Map<String, DenseMatrix> meanRep,
                                    Map<String, DenseMatrix> sdRep, String freqId) {
        String meanFileSeril = rootDir + "Condensate/" + regionHeight + '_' + regionWeight
                               + "_mean_" + freqId + ".OBJ";
        String sdFileSeril = rootDir + "Condensate/" + regionHeight + '_' + regionWeight + "_sd_"
                             + freqId + ".OBJ";

        if (FileUtil.exists(meanFileSeril) & FileUtil.exists(sdFileSeril)) {
            meanRep.putAll((HashMap<String, DenseMatrix>) SerializeUtil.readObject(meanFileSeril));
            sdRep.putAll((HashMap<String, DenseMatrix>) SerializeUtil.readObject(sdFileSeril));
        }
    }
}
