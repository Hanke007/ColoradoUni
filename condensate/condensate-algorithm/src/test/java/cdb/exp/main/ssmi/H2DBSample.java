package cdb.exp.main.ssmi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Date;

import org.springframework.util.StopWatch;

import cdb.common.lang.DateUtil;

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
        Date scur = DateUtil.parse("19940101", DateUtil.SHORT_FORMAT);
        long sdayFor1970 = scur.getTime() / (24 * 60 * 60 * 1000);
        System.out.println(sdayFor1970);

        Date ecur = DateUtil.parse("19950101", DateUtil.SHORT_FORMAT);
        long edayFor1970 = ecur.getTime() / (24 * 60 * 60 * 1000);
        System.out.println(edayFor1970);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        queryH2_37V(sdayFor1970, edayFor1970);
        stopWatch.stop();
        System.out.println("OVERALL TIME SPENDED: " + stopWatch.getTotalTimeMillis() / 1000.0);
    }

    protected static void queryH2_19H(long sdayFor1970, long edayFor1970) throws Exception {
        //        DatasetProc dProc = new SSMIFileDtProc();
        //        DenseMatrix tMatrix = dProc
        //            .read("C:/Users/chench/Desktop/SIDS/2000/tb_f13_20000811_v2_s19v.bin");

        Class.forName("org.h2.Driver");
        Connection conn = DriverManager.getConnection("jdbc:h2:~/ssmi19h19902014", "", "");
        Statement stmt = conn.createStatement();

        String query = "SELECT loc.ROW, loc.COL " + "FROM SSMI19H19902014.VECTORS As vec "
                       + "JOIN SSMI19H19902014.LOCATIONS   AS loc  ON loc.ID = vec.LOCATIONID "
                       + "JOIN SSMI19H19902014.TIMESTAMPS As tp   ON tp.ID = vec.TIMESTAMPID "
                       + "WHERE tp.TIMESTAMP >  " + sdayFor1970 + " AND tp.TIMESTAMP < "
                       + edayFor1970
                       + " AND loc.ROW > 100 AND loc.ROW < 200 AND loc.COL > 100 AND loc.COL < 200 ";
        stmt.executeQuery(query);
        //        ResultSet rs = stmt.executeQuery(query);
        //        SparseMatrix sMatrix = new SparseMatrix(tMatrix.getRowNum(), tMatrix.getColNum());
        //        while (rs.next()) {
        //            int row = rs.getInt("ROW");
        //            int col = rs.getInt("COL");
        //            sMatrix.setValue(row, col, tMatrix.getVal(row, col));
        //        }
        //        ImageWUtil.plotRGBImageWithMask(tMatrix, "C:/Users/chench/Desktop/SIDS/Anomaly/2000_/1.jpg",
        //            sMatrix, ImageWUtil.JPG_FORMMAT);
    }

    protected static void queryH2_22V(long sdayFor1970, long edayFor1970) throws Exception {
        //        DatasetProc dProc = new SSMIFileDtProc();
        //        DenseMatrix tMatrix = dProc
        //            .read("C:/Users/chench/Desktop/SIDS/2000/tb_f13_20000811_v2_s19v.bin");

        Class.forName("org.h2.Driver");
        Connection conn = DriverManager.getConnection("jdbc:h2:~/ssmi22v19902014", "", "");
        Statement stmt = conn.createStatement();

        String query = "SELECT loc.ROW, loc.COL " + "FROM SSMI22V19902014.VECTORS As vec "
                       + "JOIN SSMI22V19902014.LOCATIONS   AS loc  ON loc.ID = vec.LOCATIONID "
                       + "JOIN SSMI22V19902014.TIMESTAMPS As tp   ON tp.ID = vec.TIMESTAMPID "
                       + "WHERE tp.TIMESTAMP >  " + sdayFor1970 + " AND tp.TIMESTAMP < "
                       + edayFor1970
                       + " AND loc.ROW > 100 AND loc.ROW < 200 AND loc.COL > 100 AND loc.COL < 200 ";
        stmt.executeQuery(query);
        //        ResultSet rs = stmt.executeQuery(query);
        //        SparseMatrix sMatrix = new SparseMatrix(tMatrix.getRowNum(), tMatrix.getColNum());
        //        while (rs.next()) {
        //            int row = rs.getInt("ROW");
        //            int col = rs.getInt("COL");
        //            sMatrix.setValue(row, col, tMatrix.getVal(row, col));
        //        }
        //        ImageWUtil.plotRGBImageWithMask(tMatrix, "C:/Users/chench/Desktop/SIDS/Anomaly/2000_/1.jpg",
        //            sMatrix, ImageWUtil.JPG_FORMMAT);
    }

    protected static void queryH2_37V(long sdayFor1970, long edayFor1970) throws Exception {
        //        DatasetProc dProc = new SSMIFileDtProc();
        //        DenseMatrix tMatrix = dProc
        //            .read("C:/Users/chench/Desktop/SIDS/2000/tb_f13_20000811_v2_s19v.bin");

        Class.forName("org.h2.Driver");
        Connection conn = DriverManager.getConnection("jdbc:h2:~/ssmi37v19902014", "", "");
        Statement stmt = conn.createStatement();

        String query = "SELECT loc.ROW, loc.COL " + "FROM SSMI37V19902014.VECTORS As vec "
                       + " JOIN SSMI37V19902014.LOCATIONS   AS loc  ON loc.ID = vec.LOCATIONID "
                       + " JOIN SSMI37V19902014.TIMESTAMPS As tp   ON tp.ID = vec.TIMESTAMPID "
                       + "WHERE tp.TIMESTAMP >  " + sdayFor1970 + " AND tp.TIMESTAMP < "
                       + edayFor1970
                       + " AND loc.ROW > 100 AND loc.ROW < 200 AND loc.COL > 100 AND loc.COL < 200 ";
        stmt.executeQuery(query);
        //        ResultSet rs = stmt.executeQuery(query);
        //        SparseMatrix sMatrix = new SparseMatrix(tMatrix.getRowNum(), tMatrix.getColNum());
        //        while (rs.next()) {
        //            int row = rs.getInt("ROW");
        //            int col = rs.getInt("COL");
        //            sMatrix.setValue(row, col, tMatrix.getVal(row, col));
        //        }
        //        ImageWUtil.plotRGBImageWithMask(tMatrix, "C:/Users/chench/Desktop/SIDS/Anomaly/2000_/1.jpg",
        //            sMatrix, ImageWUtil.JPG_FORMMAT);
    }

    protected static void queryH2_85V(long sdayFor1970, long edayFor1970) throws Exception {
        //        DatasetProc dProc = new SSMIFileDtProc();
        //        DenseMatrix tMatrix = dProc
        //            .read("C:/Users/chench/Desktop/SIDS/2000/tb_f13_20000811_v2_s19v.bin");

        Class.forName("org.h2.Driver");
        Connection conn = DriverManager.getConnection("jdbc:h2:~/ssmi85v19902014", "", "");
        Statement stmt = conn.createStatement();

        String query = "SELECT loc.ROW, loc.COL " + "FROM SSMI85V19902014.VECTORS As vec "
                       + " JOIN SSMI85V19902014.LOCATIONS   AS loc  ON loc.ID = vec.LOCATIONID "
                       + " JOIN SSMI85V19902014.TIMESTAMPS As tp   ON tp.ID = vec.TIMESTAMPID "
                       + "WHERE tp.TIMESTAMP >  " + sdayFor1970 + " AND tp.TIMESTAMP < "
                       + edayFor1970
                       + " AND loc.ROW > 100 AND loc.ROW < 200 AND loc.COL > 100 AND loc.COL < 200 ";
        stmt.executeQuery(query);
        //        ResultSet rs = stmt.executeQuery(query);
        //        SparseMatrix sMatrix = new SparseMatrix(tMatrix.getRowNum(), tMatrix.getColNum());
        //        while (rs.next()) {
        //            int row = rs.getInt("ROW");
        //            int col = rs.getInt("COL");
        //            sMatrix.setValue(row, col, tMatrix.getVal(row, col));
        //        }
        //        ImageWUtil.plotRGBImageWithMask(tMatrix, "C:/Users/chench/Desktop/SIDS/Anomaly/2000_/1.jpg",
        //            sMatrix, ImageWUtil.JPG_FORMMAT);
    }

}
