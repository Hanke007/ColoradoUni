package cdb.exp.main.ssmi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.math3.stat.StatUtils;
import org.springframework.util.StopWatch;

import cdb.common.lang.ExceptionUtil;
import cdb.common.model.Point;
import cdb.dal.util.DatabaseFactory;

/**
 * 
 * @author Chao Chen
 * @version $Id: TemplCluasss.java, v 0.1 Oct 12, 2015 1:15:49 PM chench Exp $
 */
public class H2DBSample {

    /** select the anomalies occurred in certain region within certain period*/
    protected final static String ANOMALY_IN_CERTAIN_TEMPORAL_SPATIAL = "SELECT loc.LON, loc.LAT, tp.TIMESTAMP "
                                                                        + "FROM (SELECT * FROM VECTORS "
                                                                        + "WHERE TIMESTAMPID IN ( SELECT ID "
                                                                        + "FROM TIMESTAMPS "
                                                                        + "WHERE TIMESTAMP >= ? AND TIMESTAMP <= ? "
                                                                        + ")) As vec "
                                                                        + "INNER JOIN LOCATIONS  AS loc  ON loc.ID = vec.LOCATIONID  "
                                                                        + "INNER JOIN TIMESTAMPS As tp   ON tp.ID = vec.TIMESTAMPID "
                                                                        + "WHERE ROW >= ? AND ROW <= ? AND COL >= ? AND COL <= ? ";

    protected final static String AGGREGATED_ANOMALY_IN_CERTAIN_TEMPORAL_SPATIAL = "SELECT loc.LON, loc.LAT, COUNT(vec.ID), AVG(vec.VALUE) "
                                                                                   + "FROM (SELECT * FROM VECTORS "
                                                                                   + "WHERE TIMESTAMPID IN ( SELECT ID "
                                                                                   + "FROM TIMESTAMPS "
                                                                                   + "WHERE TIMESTAMP >= ? AND TIMESTAMP <= ? "
                                                                                   + ")) As vec "
                                                                                   + "INNER JOIN LOCATIONS  AS loc  ON loc.ID = vec.LOCATIONID  "
                                                                                   + "INNER JOIN TIMESTAMPS As tp   ON tp.ID = vec.TIMESTAMPID "
                                                                                   + "WHERE ROW >= ? AND ROW <= ? AND COL >= ? AND COL <= ? "
                                                                                   + "GROUP BY loc.ID";

    /**
     * 
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        long duration = 365 * 3;
        String dbId = "H2_SSMI_s19v";

        double[] ticks = new double[40];
        for (int i = 0; i < ticks.length; i++) {
            // imitated parameters
            long sdayFor1970 = Double
                .valueOf(7305.0 + (16435.0 - duration - 7305.0) * Math.random()).longValue();
            long edayFor1970 = sdayFor1970 + duration;

            double smallX = 80 + Math.random() * 120;
            double smallY = 80 + Math.random() * 120;
            Point boxLoc = new Point(4);
            boxLoc.setValue(0, smallX);
            boxLoc.setValue(1, smallX + 100);
            boxLoc.setValue(2, smallY);
            boxLoc.setValue(3, smallY + 100);

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            testH2(dbId, sdayFor1970, edayFor1970, boxLoc);
            stopWatch.stop();
            System.out.println(i + ": " + stopWatch.getTotalTimeMillis() / 1000.0);
            ticks[i] = stopWatch.getTotalTimeMillis() / 1000.0;
        }
        System.out.println("OVERALL TIME SPENDED: " + StatUtils.mean(ticks) + "\tSD: "
                           + Math.sqrt(StatUtils.variance(ticks)));
    }

    protected static void testH2(String dbId, long sdayFor1970, long edayFor1970, Point boxLoc) {
        try {
            Connection conn = DatabaseFactory.getConnection(dbId);
            PreparedStatement stmt = conn
                .prepareStatement(AGGREGATED_ANOMALY_IN_CERTAIN_TEMPORAL_SPATIAL);
            stmt.setLong(1, sdayFor1970);
            stmt.setLong(2, edayFor1970);
            stmt.setInt(3, (int) boxLoc.getValue(0));
            stmt.setInt(4, (int) boxLoc.getValue(1));
            stmt.setInt(5, (int) boxLoc.getValue(2));
            stmt.setInt(6, (int) boxLoc.getValue(3));
            ResultSet rs = stmt.executeQuery();

            int rsNum = 0;
            while (rs.next()) {
                rsNum++;
            }
            rs.close();
            stmt.close();
            conn.close();
            System.out.println(rsNum + ", " + sdayFor1970 + ", " + edayFor1970 + ", " + boxLoc);
        } catch (ClassNotFoundException e) {
            ExceptionUtil.caught(e, "");
        } catch (SQLException e) {
            ExceptionUtil.caught(e, "");
        }

    }

}
