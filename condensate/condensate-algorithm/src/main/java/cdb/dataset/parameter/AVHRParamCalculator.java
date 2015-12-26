package cdb.dataset.parameter;

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

import org.springframework.util.StopWatch;

import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.FileUtil;
import cdb.common.lang.LoggerUtil;
import cdb.common.lang.SerializeUtil;
import cdb.common.model.DenseMatrix;
import cdb.dal.file.DatasetProc;

/**
 * 
 * @author Chao Chen
 * @version $Id: AVHRParamCalculator.java, v 0.1 Dec 23, 2015 12:07:27 PM chench Exp $
 */
public class AVHRParamCalculator extends AbstractParamCalculator {
    /** the year to start*/
    private int sYear;
    /** the year to end*/
    private int eYear;

    /**
     * @param rootDir           the root directory of the dataset
     * @param regionHeight      the number of rows in sub-regions
     * @param regionWeight      the number of columns in sub-regions
     * @param freqId            the frequency id
     * @param dProc             file dataset parser
     */
    public AVHRParamCalculator(String rootDir, int sYear, int eYear, int regionHeight,
                               int regionWeight, String freqId, DatasetProc dProc) {
        super(rootDir, regionHeight, regionWeight, freqId, dProc);
        this.sYear = sYear;
        this.eYear = eYear;
    }

    /** 
     * @see cdb.dataset.parameter.AbstractParamCalculator#calculate(java.util.Map, java.util.Map)
     */
    @Override
    public void calculate(Map<String, DenseMatrix> meanRep, Map<String, DenseMatrix> sdRep) {
        if (hasExistedAndRead(meanRep, sdRep)) {
            return;
        }

        if (hasExistedAndRead(meanRep, sdRep)) {
            return;
        }

        String[] seasons = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
                             "12" };

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
                String fileRE = rootDir;
                if (timeRange < 198501) {
                    fileRE += "a07_s005_" + timeRange + "\\d{2}_" + freqId + ".v2";
                } else if (timeRange < 199001) {
                    fileRE += "a09_s005_" + timeRange + "\\d{2}_" + freqId + ".v2";
                } else if (timeRange < 199501) {
                    fileRE += "a11_s005_" + timeRange + "\\d{2}_" + freqId + ".v2";
                } else {
                    fileRE += "a14_s005_" + timeRange + "\\d{2}_" + freqId + ".v2";
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
            exec.execute(new DefaultParamThread(regionHeight, regionWeight, freqId, dProc));
            exec.execute(new DefaultParamThread(regionHeight, regionWeight, freqId, dProc));
            exec.execute(new DefaultParamThread(regionHeight, regionWeight, freqId, dProc));
            exec.execute(new DefaultParamThread(regionHeight, regionWeight, freqId, dProc));
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

    /**
     * if the serialized files exists, then parse them into object;
     * 
     * @param meanRep       the repository of mean variables
     * @param sdRep         the repository of standard deviation variables
     * @return              true if the serialized process succeed, and false otherwise.
     */
    @SuppressWarnings("unchecked")
    protected boolean hasExistedAndRead(Map<String, DenseMatrix> meanRep,
                                        Map<String, DenseMatrix> sdRep) {
        String meanFileSeril = rootDir + "Condensate/" + regionHeight + '_' + regionWeight
                               + "_mean_" + freqId + ".OBJ";
        String sdFileSeril = rootDir + "Condensate/" + regionHeight + '_' + regionWeight + "_sd_"
                             + freqId + ".OBJ";

        if (FileUtil.exists(meanFileSeril) & FileUtil.exists(sdFileSeril)) {
            meanRep.putAll((HashMap<String, DenseMatrix>) SerializeUtil.readObject(meanFileSeril));
            sdRep.putAll((HashMap<String, DenseMatrix>) SerializeUtil.readObject(sdFileSeril));
            return true;
        } else {
            return false;
        }
    }
}
