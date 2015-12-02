package cdb.exp.qc.analysis.ext;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import cdb.common.lang.ConfigureUtil;
import cdb.common.lang.DateUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.FileUtil;
import cdb.common.lang.LoggerUtil;
import cdb.common.model.DenseMatrix;
import cdb.common.model.Point;
import cdb.common.model.RegionAnomalyInfoVO;
import cdb.dal.file.DatasetProc;
import cdb.dal.file.SSMIFileDtProc;
import cdb.dal.util.DBUtil;
import cdb.dataset.util.BinFileConvntnUtil;
import cdb.exp.qc.analysis.AbstractQcAnalysis;

/**
 * 
 * @author Chao Chen
 * @version $Id: RegionMatlabPartner.java, v 0.1 Oct 30, 2015 3:21:02 PM chench Exp $
 */
public class RegionMatlabPartner extends AbstractQcAnalysis {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        convert2MeltRelatedDataset();
        //        case1();
        //        case2(160, 24);
        //        case3(160, 24);
    }

    protected static void convert2MeltRelatedDataset() {
        String rootDir = "C:/Users/chench/Desktop/SIDS/SSMI/";

        Properties properties = ConfigureUtil.read("src/test/resources/sqlDump.properties");
        String sql = properties.getProperty("DUMP");

        Map<String, List<RegionAnomalyInfoVO>> regnAnmlRep = new HashMap<String, List<RegionAnomalyInfoVO>>();
        List<RegionAnomalyInfoVO> dbSet = DBUtil.excuteSQLWithReturnList(sql);
        if (dbSet != null) {
            for (RegionAnomalyInfoVO one : dbSet) {
                String dateStr = one.getDateStr();

                List<RegionAnomalyInfoVO> anmArr = regnAnmlRep.get(dateStr);
                if (anmArr == null) {
                    anmArr = new ArrayList<RegionAnomalyInfoVO>();
                    regnAnmlRep.put(dateStr, anmArr);
                }
                anmArr.add(one);
            }
        }

        DatasetProc dProc = new SSMIFileDtProc();
        StringBuilder conBuff = new StringBuilder();
        for (String taskId : regnAnmlRep.keySet()) {
            DenseMatrix m19h = dProc.read(BinFileConvntnUtil.fileSSMI(rootDir, taskId, "n19h"));
            DenseMatrix m37v = dProc.read(BinFileConvntnUtil.fileSSMI(rootDir, taskId, "n37v"));

            List<RegionAnomalyInfoVO> regnAnmlArr = regnAnmlRep.get(taskId);
            for (RegionAnomalyInfoVO one : regnAnmlArr) {
                int rowBegin = one.getX();
                int rowEnd = rowBegin + one.getHeight();
                int colBegin = one.getY();
                int colEnd = colBegin + one.getWidth();

                int rSeq = 0;
                double[] valArr = new double[one.getHeight() * one.getWidth()];
                for (int row = rowBegin; row < rowEnd; row++) {
                    for (int col = colBegin; col < colEnd; col++) {
                        double v19h = m19h.getVal(row, col);
                        double v37v = m37v.getVal(row, col);

                        valArr[rSeq++] = (v19h - v37v) / (v19h + v37v);
                    }
                }

                double iceIndx = StatUtils.max(valArr);
                if (Double.isNaN(iceIndx)) {
                    continue;

                } else if (iceIndx < -0.0158) {
                    double ran = Math.random();
                    if (ran > 0.50) {
                        continue;
                    }
                }

                conBuff.append(one.getdPoint()).append("# " + taskId).append("# " + iceIndx)
                    .append('\n');
            }
        }

        FileUtil.write(rootDir + "Anomaly/REG_MAT", conBuff.toString());
    }

    protected static void case2(int rowIndx, int colIndx) {
        // thread setting
        int regionHeight = 8;
        int regionWeight = 8;

        // read objects
        String rootDir = "C:/Users/chench/Desktop/SIDS/SSMI/";
        String freqId = "n19v";

        DatasetProc dProc = new SSMIFileDtProc();

        try {
            Date sDate = DateUtil.parse("19920101", DateUtil.SHORT_FORMAT);
            Date mDate = DateUtil.parse("20100101", DateUtil.SHORT_FORMAT);
            Date eDate = DateUtil.parse("20150101", DateUtil.SHORT_FORMAT);

            Calendar cal = Calendar.getInstance();
            DescriptiveStatistics[] stats = new DescriptiveStatistics[367];

            StringBuilder strBuilder = new StringBuilder();
            Date cDate = sDate;
            while (!cDate.after(eDate)) {
                String taskId = DateUtil.format(cDate, DateUtil.SHORT_FORMAT);
                String fileName = BinFileConvntnUtil.fileSSMI(rootDir, taskId, freqId);
                DenseMatrix dMatrix = dProc.read(fileName);

                cDate.setTime(cDate.getTime() + 24 * 60 * 60 * 1000);
                if (dMatrix == null) {
                    continue;
                } else if (cDate.after(mDate)) {
                    LoggerUtil.info(logger, taskId);
                    List<Double> vals = new ArrayList<Double>();
                    for (int row = rowIndx; row < rowIndx + regionHeight; row++) {
                        for (int col = colIndx; col < colIndx + regionWeight; col++) {
                            if (Double.isNaN(dMatrix.getVal(row, col))) {
                                continue;
                            }
                            vals.add(dMatrix.getVal(row, col));
                        }
                    }

                    if (!vals.isEmpty()) {
                        cal.setTime(cDate);
                        int dayInYear = cal.get(Calendar.DAY_OF_YEAR);

                        strBuilder.append(dayInYear).append(',').append(taskId).append(',')
                            .append(StatUtils.mean(
                                ArrayUtils.toPrimitive(vals.toArray(new Double[vals.size()]))))
                            .append(',').append(stats[dayInYear].getMean()).append(',')
                            .append(stats[dayInYear].getStandardDeviation()).append('\n');
                    }
                } else {
                    cal.setTime(cDate);
                    int dayInYear = cal.get(Calendar.DAY_OF_YEAR);
                    if (stats[dayInYear] == null) {
                        stats[dayInYear] = new DescriptiveStatistics();
                    }

                    List<Double> vals = new ArrayList<Double>();
                    for (int row = rowIndx; row < rowIndx + regionHeight; row++) {
                        for (int col = colIndx; col < colIndx + regionWeight; col++) {
                            if (Double.isNaN(dMatrix.getVal(row, col))) {
                                continue;
                            }
                            vals.add(dMatrix.getVal(row, col));
                        }
                    }
                    if (!vals.isEmpty()) {
                        stats[dayInYear].addValue(StatUtils
                            .mean(ArrayUtils.toPrimitive(vals.toArray(new Double[vals.size()]))));
                    }
                }

            }

            FileUtil.write(rootDir + "Anomaly/MAT", strBuilder.toString());
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "");
        }

    }

    protected static void case3(int rowIndx, int colIndx) {
        // thread setting
        int regionHeight = 8;
        int regionWeight = 8;

        // read objects
        String rootDir = "C:/Users/chench/Desktop/SIDS/SSMI/";
        String freqId = "n19v";

        try {
            Date sDate = DateUtil.parse("19920101", DateUtil.SHORT_FORMAT);
            Date eDate = DateUtil.parse("20100101", DateUtil.SHORT_FORMAT);

            // compute parameter
            DescriptiveStatistics[] stats = new DescriptiveStatistics[367];
            cmpMean(stats, sDate, eDate, rootDir, freqId, regionHeight, regionWeight, rowIndx,
                colIndx);

            // draw 
            Calendar cal = Calendar.getInstance();
            DatasetProc dProc = new SSMIFileDtProc();

            StringBuilder strBuilder = new StringBuilder();
            Date cDate = new Date(sDate.getTime());
            while (!cDate.after(eDate)) {
                String taskId = DateUtil.format(cDate, DateUtil.SHORT_FORMAT);
                String fileName = BinFileConvntnUtil.fileSSMI(rootDir, taskId, freqId);
                DenseMatrix dMatrix = dProc.read(fileName);

                cDate.setTime(cDate.getTime() + 24 * 60 * 60 * 1000);
                if (dMatrix == null) {
                    continue;
                } else {
                    LoggerUtil.info(logger, taskId);
                    List<Double> vals = new ArrayList<Double>();
                    for (int row = rowIndx; row < rowIndx + regionHeight; row++) {
                        for (int col = colIndx; col < colIndx + regionWeight; col++) {
                            if (Double.isNaN(dMatrix.getVal(row, col))) {
                                continue;
                            }
                            vals.add(dMatrix.getVal(row, col));
                        }
                    }

                    if (!vals.isEmpty()) {
                        cal.setTime(cDate);
                        int dayInYear = cal.get(Calendar.DAY_OF_YEAR);

                        strBuilder.append(dayInYear).append(',').append(taskId).append(',')
                            .append(StatUtils.mean(
                                ArrayUtils.toPrimitive(vals.toArray(new Double[vals.size()]))))
                            .append(',').append(stats[dayInYear].getMean()).append(',')
                            .append(stats[dayInYear].getStandardDeviation()).append('\n');
                    }
                }
            }
            FileUtil.write(rootDir + "Anomaly/MAT2", strBuilder.toString());
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "");
        }

    }

    protected static void cmpMean(DescriptiveStatistics[] stats, final Date sDate, final Date eDate,
                                  String rootDir, String freqId, int regionHeight, int regionWeight,
                                  int rowIndx, int colIndx) {
        DatasetProc dProc = new SSMIFileDtProc();
        Calendar cal = Calendar.getInstance();

        Date cDate = new Date(sDate.getTime());
        while (!cDate.after(eDate)) {
            String taskId = DateUtil.format(cDate, DateUtil.SHORT_FORMAT);
            String fileName = BinFileConvntnUtil.fileSSMI(rootDir, taskId, freqId);
            DenseMatrix dMatrix = dProc.read(fileName);

            cDate.setTime(cDate.getTime() + 24 * 60 * 60 * 1000);
            if (dMatrix == null) {
                continue;
            } else {
                cal.setTime(cDate);
                int dayInYear = cal.get(Calendar.DAY_OF_YEAR);
                if (stats[dayInYear] == null) {
                    stats[dayInYear] = new DescriptiveStatistics();
                }

                List<Double> vals = new ArrayList<Double>();
                for (int row = rowIndx; row < rowIndx + regionHeight; row++) {
                    for (int col = colIndx; col < colIndx + regionWeight; col++) {
                        if (Double.isNaN(dMatrix.getVal(row, col))) {
                            continue;
                        }
                        vals.add(dMatrix.getVal(row, col));
                    }
                }
                if (!vals.isEmpty()) {
                    stats[dayInYear].addValue(StatUtils
                        .mean(ArrayUtils.toPrimitive(vals.toArray(new Double[vals.size()]))));
                }
            }

        }
    }

    protected static void case1() {
        // thread setting
        int regionHeight = 8;
        int regionWeight = 8;

        // read objects
        String rootDir = "C:/Users/chench/Desktop/SIDS/SSMI/";
        String freqId = "n19v";

        RegionAnomalyInfoVO regAnmVO = new RegionAnomalyInfoVO();
        regAnmVO.setX(312 / 8);
        regAnmVO.setY(152 / 8);
        regAnmVO.setDateStr("20120712");
        analSpecificRegnAndField(regionHeight, regionWeight, rootDir, freqId, regAnmVO);
    }

    protected static void analSpecificRegnAndField(int regionHeight, int regionWeight,
                                                   String rootDir, String freqId,
                                                   RegionAnomalyInfoVO regAnmVO) {
        String regnLoc = "" + regAnmVO.getX() + '_' + regAnmVO.getY();
        String[] lines = FileUtil
            .readLines(rootDir + "ClassificationDataset/" + freqId + '_' + regionHeight + '_'
                       + regionWeight + "_New/" + regnLoc);

        int dateVal = Integer.valueOf(regAnmVO.getDateStr());
        Point point = null;
        for (String line : lines) {
            point = Point.parseOf(line);
            if (point.getValue(16) == dateVal) {
                break;
            }
        }
        System.out.println(regnLoc + "\n" + point.toString() + "\n\n\n");

        int fIndx = 1;
        StringBuilder strBuild = new StringBuilder();
        for (Double val : point) {
            if (fIndx >= 15) {
                break;
            } else if (Math.abs(val) >= 3.0d) {
                strBuild.append(fIndx).append('\t').append(val).append('\n');
            }

            fIndx++;
        }

        System.out.println(strBuild);
    }
}
