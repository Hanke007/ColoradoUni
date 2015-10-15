package cdb.dataset.ssmi;

import cdb.dataset.ssmi.ui.DsGenFrame;

/**
 * 
 * @author Chao Chen
 * @version $Id: MannualGUI.java, v 0.1 Oct 14, 2015 5:35:09 PM chench Exp $
 */
public class SSMIDsGenGUI {

    /** root director where the new data-set is! OLD: RAWDATA_V1, NEW: RAWDATA_V2*/
    private final static String rootDataDir  = "C:/Users/chench/Desktop/SIDS/ClassificationDataset/";
    /** root director where the new images are*/
    private final static String rootImageDir = "C:/Users/chench/Desktop/SIDS/Anomaly/2000LowFreq/";

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        DsGenFrame frame = new DsGenFrame(rootDataDir, rootImageDir);

        frame.pack();
        frame.setLocation(300, 20);
        frame.setSize(670, 560);
        frame.setVisible(true);
    }

}
