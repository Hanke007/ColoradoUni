package cdb.exp.qc.analysis;

import cdb.common.lang.FileUtil;
import cdb.common.model.Point;
import cdb.common.model.RegionAnomalyInfoVO;

/**
 * 
 * @author Chao Chen
 * @version $Id: RegionMatlabPartner.java, v 0.1 Oct 30, 2015 3:21:02 PM chench Exp $
 */
public class RegionMatlabPartner {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        case1();
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
