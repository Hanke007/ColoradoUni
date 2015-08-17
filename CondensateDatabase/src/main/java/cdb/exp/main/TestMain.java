package cdb.exp.main;

import cdb.common.lang.ImageWUtil;
import cdb.dal.vo.DenseIntMatrix;
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
        String fileName = "C:\\Users\\chench\\Desktop\\SIDS\\2006\\tb_f13_20061201_v2_s85v.bin";
        //        String fileName = "C:\\Users\\chench\\Desktop\\SIDS\\1991\\tb_f08_19910104_v2_n19h.bin";

        DatasetProc dProc = new SSMIFileDtProc();
        DenseIntMatrix matrix = dProc.read(fileName);
        ImageWUtil.plotGrayImage(matrix, "C:\\Users\\chench\\Desktop\\12v.png",
            ImageWUtil.PNG_FORMMAT);
    }

}
