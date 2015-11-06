package cdb.exp.qc.analysis;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.math3.stat.StatUtils;

import cdb.common.lang.DateUtil;
import cdb.common.lang.FileUtil;
import cdb.common.lang.ImageWUtil;
import cdb.common.model.Location;
import cdb.common.model.RegionAnomalyInfoVO;
import cdb.common.model.RegionInfoVO;
import cdb.common.model.Samples;
import cdb.exp.qc.ui.RegionJFrame;

/**
 * 
 * @author Chao Chen
 * @version $Id: RegionInfoAnalysis.java, v 0.1 Oct 27, 2015 3:55:09 PM chench Exp $
 */
public class RegionInfoAnalysis extends AbstractQcAnalysis {

    /**
     * 
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        gui();
        //        SSMI();
        //        trackValCheckByDate(312, 152);
        //        e2();
    }

    public static void gui() {
        String imgRootDir = "C:/Users/chench/Desktop/SIDS/SSMI/Anomaly/1990to1995/";
        String regnInfoRootDir = "C:/Users/chench/Desktop/SIDS/SSMI/ClassificationDataset/n19v_8_8/";
        String regnAnmInfoFile = "C:/Users/chench/Desktop/SIDS/SSMI/Anomaly/REG_n19v_8_8";
        String freqId = "n19v";

        RegionJFrame frame = new RegionJFrame(imgRootDir, regnInfoRootDir, regnAnmInfoFile, freqId,
            3, 2010);
        frame.pack();
        frame.setLocation(300, 20);
        frame.setSize(520, 700);
        frame.setVisible(true);
    }

    public static void e1() throws ParseException {
        long daySeq = 4694;
        Date firstDate = DateUtil.parse("19980101", DateUtil.SHORT_FORMAT);
        firstDate.setTime(firstDate.getTime() + (daySeq - 1) * 24 * 60 * 60 * 1000);
        System.out.println(DateUtil.format(firstDate, DateUtil.SHORT_FORMAT));
    }

    public static void e2() {
        Location[] locs = { new Location(312, 152) };
        ImageWUtil.drawRects(
            "C:/Users/chench/Desktop/SIDS/SSMI/Anomaly/1990to1995/19980101_n19v.bmp",
            "C:/Users/chench/Desktop/SIDS/SSMI/1.bmp", locs, 8, 8, ImageWUtil.BMP_FORMAT);
    }

    public static void SSMI() {
        String rootDir = "C:/Users/chench/Desktop/SIDS/SSMI/";
        String freqId = "n19v";
        int regionHeight = 8;
        int regionWeight = 8;

        String regnResultFile = rootDir + "Anomaly/REG_" + freqId + '_' + regionHeight + '_'
                                + regionWeight;
        String[] lines = FileUtil.readLines(regnResultFile);
        Map<String, List<RegionAnomalyInfoVO>> regnRep = new HashMap<String, List<RegionAnomalyInfoVO>>();
        for (String line : lines) {
            RegionAnomalyInfoVO regnAnmlVO = RegionAnomalyInfoVO.parseOf(line);

            String key = regnAnmlVO.getDateStr();
            List<RegionAnomalyInfoVO> regnAnmlArr = regnRep.get(key);
            if (regnAnmlArr == null) {
                regnAnmlArr = new ArrayList<RegionAnomalyInfoVO>();
                regnRep.put(key, regnAnmlArr);
            }
            regnAnmlArr.add(regnAnmlVO);
        }

        for (String dateStr : regnRep.keySet()) {
            List<RegionAnomalyInfoVO> regnAnmlArr = regnRep.get(dateStr);

            int regnNum = regnAnmlArr.size();
            Location[] rects = new Location[regnNum];
            for (int indx = 0; indx < regnNum; indx++) {
                RegionAnomalyInfoVO regn = regnAnmlArr.get(indx);
                rects[indx] = new Location(regn.getX(), regn.getY());
            }

            String orgnImag = rootDir + "Anomaly/1990to1995/" + dateStr + '_' + freqId + ".bmp";
            String targtImag = rootDir + "Anomaly/Malicious/" + dateStr + '_' + freqId + ".bmp";

            ImageWUtil.drawRects(orgnImag, targtImag, rects, regionWeight, regionWeight,
                ImageWUtil.BMP_FORMAT);
        }
    }

    public static void trackValCheckByDate(int x, int y) {
        // thread setting
        int regionHeight = 8;
        int regionWeight = 8;

        // read objects
        String rootDir = "C:/Users/chench/Desktop/SIDS/SSMI/";
        String freqId = "n19v";

        Queue<RegionInfoVO> regnList = new LinkedList<RegionInfoVO>();
        List<String> regnLocs = new ArrayList<String>();
        int rIndx = x / regionHeight;
        int cIndx = y / regionWeight;
        regnLocs.add("" + rIndx + '_' + cIndx);

        for (String regnLoc : regnLocs) {
            String[] lines = FileUtil
                .readLines(rootDir + "ClassificationDataset/" + freqId + '_' + regionHeight + '_'
                           + regionWeight + '/' + regnLoc);
            for (String line : lines) {
                RegionInfoVO regnVO = RegionInfoVO.parseOf(line);
                regnList.add(regnVO);
            }

            int dataNum = regnList.size();
            Samples dataSample = new Samples(dataNum, 6 + 10);
            List<String> dateStrArr = new ArrayList<String>();
            tranformRegionVO(regnList, dataSample, dateStrArr);
            normalization(dataSample);

            StringBuilder strBuilder = new StringBuilder();
            for (int i = 0; i < dataNum; i++) {
                strBuilder.append(dataSample.getPoint(i)).append("# " + dateStrArr.get(i))
                    .append('\n');
            }
            FileUtil.write("C:/Users/chench/Desktop/SIDS/SSMI/Anomaly/MATLAB",
                strBuilder.toString());
        }

    }

    protected static void tranformRegionVO(Queue<RegionInfoVO> regnList, Samples dataSample,
                                           List<String> regnDateStr) {
        int dSeq = 0;
        RegionInfoVO one = null;
        while ((one = regnList.poll()) != null) {
            int pSeq = 0;

            // distribution information
            //            for (double distrbtnVal : one.getDistribution()) {
            //                dataSample.setValue(dSeq, pSeq++, distrbtnVal);
            //            }

            // gradient along row
            double gradRowSum = 0;
            for (double gradRowVal : one.getGradRow()) {
                gradRowSum += gradRowVal;
                //                dataSample.setValue(dSeq, pSeq++, gradRowVal);
            }
            dataSample.setValue(dSeq, pSeq++, gradRowSum);

            // gradient along column
            double gradColSum = 0;
            for (double gradColVal : one.getGradCol()) {
                gradColSum += gradColVal;
                //                dataSample.setValue(dSeq, pSeq++, gradColVal);
            }
            dataSample.setValue(dSeq, pSeq++, gradColSum);

            // Contextual: temporal gradients
            for (double tGradConVal : one.gettGradCon()) {
                dataSample.setValue(dSeq, pSeq++, tGradConVal);
            }

            // Contextual: spatial correlations
            double sCorrSum = 0;
            for (double sCorrConVal : one.getsCorrCon()) {
                sCorrSum += sCorrConVal;
                //                dataSample.setValue(dSeq, pSeq++, sCorrConVal);
            }
            dataSample.setValue(dSeq, pSeq++, sCorrSum);

            // Contextual: spatial differences
            double sDiffSum = 0;
            for (double sDiffConVal : one.getsDiffCon()) {
                sDiffSum += sDiffConVal;
                //                dataSample.setValue(dSeq, pSeq++, sDiffConVal);
            }
            dataSample.setValue(dSeq, pSeq++, sDiffSum);

            dataSample.setValue(dSeq, pSeq++, one.getEntropy());
            dataSample.setValue(dSeq, pSeq++, one.getGradMean());
            dataSample.setValue(dSeq, pSeq++, one.getMean());
            dataSample.setValue(dSeq, pSeq++, one.getSd());
            dataSample.setValue(dSeq, pSeq++, one.getrIndx());
            dataSample.setValue(dSeq, pSeq++, one.getcIndx());

            regnDateStr.add(one.getDateStr());
            dSeq++;
        }
    }

    protected static void normalization(Samples dataSample) {
        int[] dimens = dataSample.length();

        for (int pIndx = 0; pIndx < dimens[1]; pIndx++) {

            // read datas
            double[] vals = new double[dimens[0]];
            for (int sIndx = 0; sIndx < dimens[0]; sIndx++) {
                vals[sIndx] = dataSample.getValue(sIndx, pIndx);
            }

            // normalization
            double sd = StatUtils.variance(vals);
            if (sd != 0) {
                double[] valNorm = StatUtils.normalize(vals);
                for (int sIndx = 0; sIndx < dimens[0]; sIndx++) {
                    dataSample.setValue(sIndx, pIndx, valNorm[sIndx]);
                }
            }
        }
    }
}
