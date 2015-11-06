package cdb.exp.main;

import java.io.File;

import cdb.common.lang.ImageWUtil;
import cdb.common.model.DenseMatrix;
import cdb.dal.file.AVHRFileDtProc;
import cdb.dal.file.DMSPFileDtProc;
import cdb.dal.file.DatasetProc;
import cdb.dal.file.NetCDFDtProc;
import cdb.dal.file.SSMIFileDtProc;

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
        filteringAVHRFileDtProc();
    }

    public static void SSMIFileDtProc() {
        String fileName = "C:\\Users\\chench\\Desktop\\SIDS\\2006\\tb_f13_20061201_v2_s85v.bin";
        DatasetProc dProc = new SSMIFileDtProc();
        DenseMatrix matrix = dProc.read(fileName);

        File file = new File(fileName);
        String fileN = file.getName();
        ImageWUtil.plotGrayImage(matrix,
            "C:\\Users\\chench\\Desktop\\" + fileN.substring(0, fileN.indexOf('.')) + ".png",
            ImageWUtil.PNG_FORMMAT);
    }

    public static void DMSPFileDtProc() {
        String fileName = "C:\\Users\\chench\\Desktop\\SIDS\\2015\\nt_20150101_f17_nrt_n.bin";
        DatasetProc dProc = new DMSPFileDtProc();
        DenseMatrix matrix = dProc.read(fileName);

        File file = new File(fileName);
        String fileN = file.getName();
        ImageWUtil.plotGrayImage(matrix,
            "C:\\Users\\chench\\Desktop\\" + fileN.substring(0, fileN.indexOf('.')) + ".png",
            ImageWUtil.PNG_FORMMAT);
    }

    public static void NetCDFileDtProc() {
        String fileName = "C:\\Users\\chench\\Desktop\\SIDS\\2012\\GLSMD25E2_20120101_v01r01.nc";
        DatasetProc dProc = new NetCDFDtProc();
        DenseMatrix matrix = dProc.read(fileName);

        File file = new File(fileName);
        String fileN = file.getName();
        ImageWUtil.plotImageForMEASURE(matrix,
            "C:\\Users\\chench\\Desktop\\" + fileN.substring(0, fileN.indexOf('.')) + ".png",
            ImageWUtil.PNG_FORMMAT);
    }

    public static void filteringNetCDFileDtProc() {
        String fileName = "C:\\Users\\chench\\Desktop\\SIDS\\1990\\GLSMD25E2_19900205_v01r01.nc";
        int[] rowIncluded = new int[100];
        int[] colIncluded = new int[100];
        for (int i = 0; i < rowIncluded.length; i++) {
            rowIncluded[i] = 370 + i;
            colIncluded[i] = 260 + i;
        }

        DatasetProc dProc = new NetCDFDtProc();
        DenseMatrix matrix = dProc.read(fileName, rowIncluded, colIncluded);

        File file = new File(fileName);
        String fileN = file.getName();
        ImageWUtil.plotImageForMEASURE(matrix,
            "C:\\Users\\chench\\Desktop\\" + fileN.substring(0, fileN.indexOf('.')) + ".png",
            ImageWUtil.PNG_FORMMAT);
    }

    public static void filteringAVHRFileDtProc() {
        String fileName = "C:\\Users\\chench\\Desktop\\SIDS\\AVHR\\2000PROC\\a14_n005_2000002_0400_chn3.v3";
        DatasetProc dProc = new AVHRFileDtProc();
        DenseMatrix matrix = dProc.read(fileName);

        File file = new File(fileName);
        String fileN = file.getName();
        ImageWUtil.plotGrayImage(matrix,
            "C:\\Users\\chench\\Desktop\\" + fileN.substring(0, fileN.indexOf('.')) + ".png",
            ImageWUtil.PNG_FORMMAT);
    }
}
