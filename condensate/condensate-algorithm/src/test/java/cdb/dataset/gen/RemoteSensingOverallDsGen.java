package cdb.dataset.gen;

import cdb.dataset.gen.support.AVHRSourceDumpImpl;
import cdb.dataset.gen.support.AnomalyInfoVOTransformerImpl;
import cdb.dataset.gen.support.ImageInfoVOTransformerImpl;
import cdb.dataset.gen.support.RemoteSensingGen;
import cdb.dataset.gen.support.SSMISourceDumpImpl;
import cdb.dataset.gen.ui.DsGenFrame;
import cdb.service.dataset.AVHRFileDtProc;
import cdb.service.dataset.SSMIFileDtProc;

/**
 * 
 * @author Chao Chen
 * @version $Id: ImageSSMIDsGen.java, v 0.1 Oct 22, 2015 4:09:10 PM chench Exp $
 */
public class RemoteSensingOverallDsGen {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        imageSSMI();
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
}
