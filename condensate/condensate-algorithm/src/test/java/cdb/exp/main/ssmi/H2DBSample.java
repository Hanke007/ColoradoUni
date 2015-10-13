package cdb.exp.main.ssmi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import cdb.common.lang.DateUtil;
import cdb.common.lang.ImageWUtil;
import cdb.dal.vo.DenseMatrix;
import cdb.dal.vo.SparseMatrix;
import cdb.service.dataset.DatasetProc;
import cdb.service.dataset.SSMIFileDtProc;

/**
 * 
 * @author Chao Chen
 * @version $Id: TemplCluasss.java, v 0.1 Oct 12, 2015 1:15:49 PM chench Exp $
 */
public class H2DBSample {

    /**
     * 
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        Date cur = DateUtil.parse("20000811", DateUtil.SHORT_FORMAT);
        long dayFor1970 = cur.getTime() / (24 * 60 * 60 * 1000);
        System.out.println(dayFor1970);

        DatasetProc dProc = new SSMIFileDtProc();
        DenseMatrix tMatrix = dProc
            .read("C:/Users/chench/Desktop/SIDS/2000/tb_f13_20000811_v2_s19v.bin");

        Class.forName("org.h2.Driver");
        Connection conn = DriverManager.getConnection("jdbc:h2:~/ssmi19v19902014", "", "");
        Statement stmt = conn.createStatement();

        String query = "SELECT loc.ROW, loc.COL " + "FROM SSMI19H19902014.VECTORS As vec "
                       + "JOIN SSMI19H19902014.LOCATIONS   AS loc  ON loc.ID = vec.LOCATIONID "
                       + "JOIN SSMI19H19902014.TIMESTAMPS As tp   ON tp.ID = vec.TIMESTAMPID "
                       + "WHERE tp.TIMESTAMP =  " + dayFor1970;
        ResultSet rs = stmt.executeQuery(query);
        SparseMatrix sMatrix = new SparseMatrix(tMatrix.getRowNum(), tMatrix.getColNum());
        while (rs.next()) {
            int row = rs.getInt("ROW");
            int col = rs.getInt("COL");
            sMatrix.setValue(row, col, tMatrix.getVal(row, col));
        }
        ImageWUtil.plotRGBImageWithMask(tMatrix, "C:/Users/chench/Desktop/SIDS/Anomaly/2000_/1.jpg",
            sMatrix, ImageWUtil.JPG_FORMMAT);
    }

}
