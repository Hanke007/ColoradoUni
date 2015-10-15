package cdb.dataset.ssmi.ui;

import cdb.dal.vo.AnomalyInfoVO;

/**
 * 
 * @author Chao Chen
 * @version $Id: Freq2ImageFileUtil.java, v 0.1 Oct 15, 2015 1:29:09 PM chench Exp $
 */
public final class Freq2ImageFileUtil {

    private Freq2ImageFileUtil() {

    }

    public static String freq2ImageFile(String rootImageDir, AnomalyInfoVO anomalyVO,
                                        String freqId) {
        return rootImageDir + anomalyVO.getDateStr() + '_' + freqId + ".JPG";
    }
}
