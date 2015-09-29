package cdb.web.service;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cdb.common.lang.DateUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.FileUtil;
import cdb.common.lang.SerializeUtil;
import cdb.common.lang.StatisticParamUtil;
import cdb.dal.vo.DenseMatrix;
import cdb.dal.vo.Location;
import cdb.ml.anomaly.AnomalyDetection;
import cdb.ml.anomaly.SimpleAnomalyDetecion;
import cdb.ml.clustering.Point;
import cdb.web.vo.AnomalyVO;

/**
 * Detect anomalies in SSIM-data set
 * See <a href="http://nsidc.org/data/docs/daac/nsidc0001_ssmi_tbs.gd.html">Daily Polar Gridded Brightness Temperatures Document</a>
 * 
 * @author Chao Chen
 * @version $Id: AnomalyDetectionService.java, v 0.1 Sep 28, 2015 6:12:09 PM chench Exp $
 */
@Service
public class SSMIAnmlDtcnImpl extends AbstractAnmlDtcnService {

    /** the root directory of SSMI dataset */
    @Value("#{configProperties['ROOT_DIR']}")
    private String SSMI_ROOT_DIR;

    /**
     * detect anomalies in SSIM-dataset given start and end dates
     * 
     * @param sDate     start date
     * @param eDate     end date
     * @param locals    the target locations to check
     * @return          the list of the anomalies
     */
    @Override
    public List<AnomalyVO> retrvAnomaly(Date sDate, Date eDate, Location[] locals, String freqId) {
        // group test data-set by month
        Map<String, List<String>> testSets = testsetGroupByMonth(sDate, eDate);
        String[] seasons = testSets.keySet().toArray(new String[testSets.keySet().size()]);

        // computer statistical parameters
        Map<String, DenseMatrix> meanRep = new HashMap<String, DenseMatrix>();
        Map<String, DenseMatrix> sdRep = new HashMap<String, DenseMatrix>();
        cmpParams(seasons, meanRep, sdRep, freqId);

        // detect anomalies
        try {
            return detectAnomalies(testSets, meanRep, sdRep, locals, freqId);
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "Date parse exception");
        }
        return null;
    }

    protected Map<String, List<String>> testsetGroupByMonth(Date sDate, Date eDate) {
        String[] seasons = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
                             "12" };
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

    protected void cmpParams(String[] seasons, Map<String, DenseMatrix> meanRep,
                             Map<String, DenseMatrix> sdRep, String freqId) {
        // make regular files
        int seasonNum = seasons.length;
        String[] meanFiles = new String[seasonNum];
        String[] sdFiles = new String[seasonNum];
        for (int seasonIndx = 0; seasonIndx < seasonNum; seasonIndx++) {
            meanFiles[seasonIndx] = SSMI_ROOT_DIR + "Condensate/mean_\\d{4}" + seasons[seasonIndx]
                                    + "_" + freqId + ".OBJ";
            sdFiles[seasonIndx] = SSMI_ROOT_DIR + "Condensate/sd_\\d{4}" + seasons[seasonIndx] + "_"
                                  + freqId + ".OBJ";
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

    protected List<AnomalyVO> detectAnomalies(Map<String, List<String>> testSets,
                                              Map<String, DenseMatrix> meanRep,
                                              Map<String, DenseMatrix> sdRep, Location[] locs,
                                              String freqId) throws ParseException {
        List<AnomalyVO> result = new ArrayList<AnomalyVO>();

        for (String season : testSets.keySet()) {
            List<String> taskIds = testSets.get(season);
            int taskIdSize = taskIds.size();
            int locSize = locs.length;

            Point[][] domains = new Point[locSize][taskIdSize];
            for (int tIndx = 0; tIndx < taskIdSize; tIndx++) {
                for (int locIndx = 0; locIndx < locSize; locIndx++) {
                    String fileName = binFileConvntn(taskIds.get(tIndx), freqId);
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
            for (int locIndx = 0; locIndx < locSize; locIndx++) {
                Location loc = locs[locIndx];

                AnomalyDetection detector = new SimpleAnomalyDetecion(
                    meanRep.get(season).getVal(loc.x(), loc.y()),
                    sdRep.get(season).getVal(loc.x(), loc.y()), 2.0);
                int[] anmlyIndx = detector.detect(domains[locIndx], 0, 0);
                if (anmlyIndx == null) {
                    continue;
                }

                for (int tIndx : anmlyIndx) {
                    Date tDate = DateUtil.parse(taskIds.get(tIndx), DateUtil.SHORT_FORMAT);
                    double val = domains[locIndx][tIndx].getValue(0);
                    double longi = loc.x();
                    double lati = loc.y();
                    result.add(new AnomalyVO(tDate, val, longi, lati));
                }
            }
        }

        return result;
    }

    /**
     * the conventional way to name the binary files
     * 
     * @param taskId    the task identify   yyyymmdd
     * @param freqId    the frequency identify
     * @return          file name
     */
    protected String binFileConvntn(String taskId, String freqId) {
        int timeRange = Integer.valueOf(taskId) / 100;
        int year = timeRange / 10000;
        String fileName = SSMI_ROOT_DIR + year + "/";
        if (timeRange < 199201) {
            fileName += "tb_f08_" + taskId + "_v2_" + freqId + ".bin";
        } else if (timeRange < 199601) {
            fileName += "tb_f11_" + taskId + "_v2_" + freqId + ".bin";
        } else if (timeRange < 200901) {
            fileName += "tb_f13_" + taskId + "_v2_" + freqId + ".bin";
        } else if (timeRange < 201001) {
            fileName += "tb_f13_" + taskId + "_v3_" + freqId + ".bin";
        } else {
            fileName += "tb_f17_" + taskId + "_v4_" + freqId + ".bin";
        }

        return fileName;
    }

}
