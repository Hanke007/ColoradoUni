package cdb.dal.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.h2.jdbcx.JdbcConnectionPool;

import cdb.common.lang.ExceptionUtil;
import cdb.common.model.Point;

/**
 * 
 * @author Chao Chen
 * @version $Id: GEOInfoFixer.java, v 0.1 Nov 11, 2015 11:22:39 AM chench Exp $
 */
public class GEOInfoFixer {

    private final static String SELECT_ALL = "SELECT ID, LON, LAT FROM LOCATIONS";

    private final static String UPDATE_LOC = "UPDATE LOCATIONS " + "SET LON = ?, LAT = ? "
                                             + "WHERE ID = ? ";

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        List<Point> locs = new ArrayList<Point>();
        try {
            // achieve data
            JdbcConnectionPool connPoolH2 = JdbcConnectionPool.create(
                "jdbc:h2:tcp://localhost/~/ssmi37v19902014;SCHEMA=ssmi37v19902014;AUTO_SERVER=true;MULTI_THREADED=1",
                "", "");
            Connection conn = connPoolH2.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SELECT_ALL);

            while (rs.next()) {
                //ID, LON, LAT
                Point point = new Point(rs.getInt(1), rs.getDouble(2), rs.getDouble(3));
                locs.add(point);
            }
            conn.close();
            connPoolH2.dispose();

            // write data

        } catch (SQLException e) {
            ExceptionUtil.caught(e, "");
        }

        try {
            // achieve data
            JdbcConnectionPool connPoolH2 = JdbcConnectionPool.create(
                "jdbc:h2:tcp://localhost/~/ssmi22v19902014a2;SCHEMA=ssmi22v19902014a2;AUTO_SERVER=true;CACHE_SIZE=1048576;MULTI_THREADED=1",
                "", "");
            Connection conn = connPoolH2.getConnection();
            for (Point point : locs) {
                PreparedStatement stmt = conn.prepareStatement(UPDATE_LOC);
                stmt.setDouble(1, point.getValue(1));
                stmt.setDouble(2, point.getValue(2));
                stmt.setInt(3, (int) point.getValue(0));
                stmt.execute();
            }
            conn.close();
            connPoolH2.dispose();

            // write data

        } catch (SQLException e) {
            ExceptionUtil.caught(e, "");
        }

    }

}
