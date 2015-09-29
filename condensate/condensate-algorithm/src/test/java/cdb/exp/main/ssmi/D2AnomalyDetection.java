package cdb.exp.main.ssmi;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cdb.common.lang.DateUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.FileUtil;
import cdb.common.lang.LoggerUtil;
import cdb.common.lang.SerializeUtil;
import cdb.common.lang.StatisticParamUtil;
import cdb.dal.vo.DenseMatrix;
import cdb.dal.vo.Location;
import cdb.ml.anomaly.AnomalyDetection;
import cdb.ml.anomaly.SimpleAnomalyDetecion;
import cdb.ml.clustering.Point;
import cdb.service.dataset.DatasetProc;
import cdb.service.dataset.SSMIFileDtProc;

/**
 * 
 * @author Chao Chen
 * @version $Id: D2AnomalyDetection.java, v 0.1 Sep 29, 2015 10:41:04 AM chench Exp $
 */
public class D2AnomalyDetection extends AbstractArcticAnalysis {

    /** frequency identity*/
    protected final static String FREQNCY_ID = "s19h";

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        case1();
    }

    public static void case1() {
        // fetch related mean and sd files
        LoggerUtil.info(logger, "1. compute statistical parameter.");
        String[] seasons = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
                             "12" };
        Map<String, DenseMatrix> meanRep = new HashMap<String, DenseMatrix>();
        Map<String, DenseMatrix> sdRep = new HashMap<String, DenseMatrix>();
        cmpParams(seasons, meanRep, sdRep);

        // detect anomalies
        try {
            LoggerUtil.info(logger, "2. detect anomalies.");
            Date sDate = DateUtil.parse("20080112", DateUtil.SHORT_FORMAT);
            Date eDate = DateUtil.parse("20100112", DateUtil.SHORT_FORMAT);
            Location[] locals = { new Location(100, 100) };
            detectAnomalies(meanRep, sdRep, seasons, sDate, eDate, locals, new SSMIFileDtProc());
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "Check date format.");
        }

    }

    public static void cmpParams(String[] seasons, Map<String, DenseMatrix> meanRep,
                                 Map<String, DenseMatrix> sdRep) {
        // make regular files
        int seasonNum = seasons.length;
        String[] meanFiles = new String[seasonNum];
        String[] sdFiles = new String[seasonNum];
        for (int seasonIndx = 0; seasonIndx < seasonNum; seasonIndx++) {
            meanFiles[seasonIndx] = ROOT_DIR + "Condensate/mean_\\d{4}" + seasons[seasonIndx] + "_"
                                    + FREQNCY_ID + ".OBJ";
            sdFiles[seasonIndx] = ROOT_DIR + "Condensate/sd_\\d{4}" + seasons[seasonIndx] + "_"
                                  + FREQNCY_ID + ".OBJ";
        }

        // mean parameters
        for (int seasonIndx = 0; seasonIndx < seasonNum; seasonIndx++) {
            // read partial mean
            List<DenseMatrix> meanPartial = new ArrayList<DenseMatrix>();
            File[] files = FileUtil.parserFilesByPattern(meanFiles[seasonIndx]);
            for (File file : files) {
                meanPartial.add((DenseMatrix) SerializeUtil.readObject(file.getAbsolutePath()));
            }

            // compute mean
            meanRep.put(seasons[seasonIndx], StatisticParamUtil.mean(meanPartial));
        }

        // standard deviation parameters
        for (int seasonIndx = 0; seasonIndx < seasonNum; seasonIndx++) {
            // read partial mean
            List<DenseMatrix> sdPartial = new ArrayList<DenseMatrix>();
            File[] files = FileUtil.parserFilesByPattern(sdFiles[seasonIndx]);
            for (File file : files) {
                sdPartial.add((DenseMatrix) SerializeUtil.readObject(file.getAbsolutePath()));
            }

            // compute mean
            sdRep.put(seasons[seasonIndx], StatisticParamUtil.mean(sdPartial));
        }
    }

    public static void detectAnomalies(Map<String, DenseMatrix> meanRep,
                                       Map<String, DenseMatrix> sdRep, String[] seasons, Date sDate,
                                       Date eDate, Location[] locs, DatasetProc dProc) {
        LoggerUtil.info(logger, "\ta) group test dataset by month.");
        Map<String, List<String>> testSets = testsetGroupByMonth(seasons, sDate, eDate);

        LoggerUtil.info(logger, "\tb) group test dataset by month.");
        for (String season : testSets.keySet()) {
            List<String> taskIds = testSets.get(season);
            int taskIdSize = taskIds.size();
            int locSize = locs.length;

            Point[][] domains = new Point[locSize][taskIdSize];
            for (int tIndx = 0; tIndx < taskIdSize; tIndx++) {
                for (int locIndx = 0; locIndx < locSize; locIndx++) {
                    String fileName = binFileConvntn(taskIds.get(tIndx), FREQNCY_ID);
                    DenseMatrix tMatrix = dProc.read(fileName);
                    if (tMatrix == null) {
                        domains[locIndx][tIndx] = null;
                        continue;
                    }

                    Location loc = locs[locIndx];
                    domains[locIndx][tIndx] = new Point(tMatrix.getVal(loc.x(), loc.y()));
                }
            }

            // anomaly detection
            StringBuilder logMsg = new StringBuilder("\tc) process " + season + ": ");
            for (int locIndx = 0; locIndx < locSize; locIndx++) {
                Location loc = locs[locIndx];

                AnomalyDetection detector = new SimpleAnomalyDetecion(
                    meanRep.get(season).getVal(loc.x(), loc.y()),
                    sdRep.get(season).getVal(loc.x(), loc.y()), 2.0);
                int[] anmlyIndx = detector.detect(domains[locIndx], 0, 0);
                if (anmlyIndx == null) {
                    continue;
                }

                for (int indx : anmlyIndx) {
                    logMsg.append(taskIds.get(indx)).append(',');
                }
            }
            LoggerUtil.info(logger, logMsg.toString());
        }
    }

    public static Map<String, List<String>> testsetGroupByMonth(String[] seasons, Date sDate,
                                                                Date eDate) {
        Map<String, List<String>> result = new HashMap<String, List<String>>();

        Calendar cal = Calendar.getInstance();
        Date curDate = sDate;
        while (curDate.before(eDate)) {
            cal.setTime(curDate);
            int month = cal.get(Calendar.MONTH);

            String key = seasons[month];
            List<String> testSet = result.get(key);
            if (testSet == null) {
                testSet = new ArrayList<String>();
                result.put(key, testSet);
            }
            testSet.add(DateUtil.format(curDate, DateUtil.SHORT_FORMAT));

            //move to next day
            curDate.setTime(curDate.getTime() + 24 * 60 * 60 * 1000);
        }
        return result;
    }
}
