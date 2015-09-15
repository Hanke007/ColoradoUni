package cdb.exp.main;

import java.io.File;

import cdb.common.lang.ImageWUtil;
import cdb.dal.vo.DenseIntMatrix;
import cdb.service.dataset.DMSPFileDtProc;
import cdb.service.dataset.DatasetProc;
import cdb.service.dataset.SSMIFileDtProc;

/**
 * 
 * @author Chao Chen
 * @version $Id: TestMain.java, v 0.1 Jul 22, 2015 4:49:40 PM chench Exp $
 */
public class TestMain {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        DMSPFileDtProc();
    }

    public static void SSMIFileDtProc() {
        String fileName = "C:\\Users\\chench\\Desktop\\SIDS\\2006\\tb_f13_20061201_v2_s85v.bin";
        DatasetProc dProc = new SSMIFileDtProc();
        DenseIntMatrix matrix = dProc.read(fileName);

        File file = new File(fileName);
        String fileN = file.getName();
        ImageWUtil.plotGrayImage(matrix,
            "C:\\Users\\chench\\Desktop\\" + fileN.substring(0, fileN.indexOf('.')) + ".png",
            ImageWUtil.PNG_FORMMAT);
    }

    public static void DMSPFileDtProc() {
        String fileName = "C:\\Users\\chench\\Desktop\\SIDS\\2015\\nt_20150101_f17_nrt_n.bin";
        DatasetProc dProc = new DMSPFileDtProc();
        DenseIntMatrix matrix = dProc.read(fileName);

        File file = new File(fileName);
        String fileN = file.getName();
        ImageWUtil.plotGrayImage(matrix,
            "C:\\Users\\chench\\Desktop\\" + fileN.substring(0, fileN.indexOf('.')) + ".png",
            ImageWUtil.PNG_FORMMAT);
    }
}
