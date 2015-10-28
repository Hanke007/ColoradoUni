package cdb.exp.qc.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cdb.common.lang.FileUtil;
import cdb.common.lang.ImageWUtil;
import cdb.dal.vo.Location;
import cdb.dal.vo.RegionAnomalyInfoVO;

/**
 * 
 * @author Chao Chen
 * @version $Id: RegionInfoAnalysis.java, v 0.1 Oct 27, 2015 3:55:09 PM chench Exp $
 */
public class RegionInfoAnalysis {
    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        SSMI();
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

            String orgnImag = rootDir + "Anomaly/1990to1995/" + dateStr + '_' + freqId + ".jpg";
            String targtImag = rootDir + "Anomaly/Malicious/" + dateStr + '_' + freqId + ".jpg";

            ImageWUtil.drawRects(orgnImag, targtImag, rects, regionWeight, regionWeight,
                ImageWUtil.JPG_FORMMAT);
        }
    }
}
