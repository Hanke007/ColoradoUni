package cdb.web.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.LoggerUtil;
import cdb.dal.util.DatabaseFactory;
import cdb.web.bean.Location2D;
import cdb.web.envelope.AnomalyEnvelope;
import cdb.web.vo.AnomalyVO;

/**
 * 
 * @author Chao Chen
 * @version $Id: H2BasedAnomalyInfoWDAOImpl.java, v 0.1 Nov 9, 2015 2:56:26 PM chench Exp $
 */
@Repository
public class H2BasedAnomalyInfoWDAOImpl implements AnomalyInfoWDAO {
    /** the default value of the right down corner */
    private Location2D defaultRightDownVal;

    /** 
     * @see cdb.web.dao.AnomalyInfoWDAO#selectNearestLeftUp(double, double, cdb.web.envelope.AnomalyEnvelope)
     */
    @Override
    public Location2D selectNearestLeftUp(double longi, double lati, AnomalyEnvelope reqContext) {
        Location2D result = null;
        Connection conn = null;

        String dbId = convertDBID(reqContext);
        try {
            conn = DatabaseFactory.getConnection(dbId);
            PreparedStatement stmt = conn.prepareStatement(NEAREST_APPROXIMATED);
            stmt.setDouble(1, longi);
            stmt.setDouble(2, longi);
            stmt.setDouble(3, lati);
            stmt.setDouble(4, lati);

            double minLon = Math.floor(longi) - 1;
            stmt.setDouble(5, minLon);
            double maxLon = Math.ceil(longi);
            stmt.setDouble(6, maxLon);

            double minLat = Math.floor(lati) - 1;
            stmt.setDouble(7, minLat);
            double maxLat = Math.ceil(lati);
            stmt.setDouble(8, maxLat);

            ResultSet rs = stmt.executeQuery();
            result = rs.next() ? new Location2D(rs.getInt(1), rs.getInt(2)) : new Location2D(0, 0);

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            ExceptionUtil.caught(e, reqContext);
            DatabaseFactory.removeConnectionCache(dbId);
        } catch (ClassNotFoundException e) {
            ExceptionUtil.caught(e, "DB Driver Not Found");
        } finally {
            LoggerUtil.info(logger,
                "Finished Nearest Approximation: " + (result == null ? "NULL" : result.toString()));
        }

        return result;
    }

    /** 
     * @see cdb.web.dao.AnomalyInfoWDAO#selectNearestRightDown(double, double, cdb.web.envelope.AnomalyEnvelope)
     */
    @Override
    public Location2D selectNearestRightDown(double longi, double lati,
                                             AnomalyEnvelope reqContext) {
        Location2D result = null;
        Connection conn = null;

        String dbId = convertDBID(reqContext);
        try {
            conn = DatabaseFactory.getConnection(dbId);
            PreparedStatement stmt = conn.prepareStatement(NEAREST_APPROXIMATED);
            stmt.setDouble(1, longi);
            stmt.setDouble(2, longi);
            stmt.setDouble(3, lati);
            stmt.setDouble(4, lati);

            double minLon = Math.floor(longi) - 1;
            stmt.setDouble(5, minLon);
            double maxLon = Math.ceil(longi);
            stmt.setDouble(6, maxLon);

            double minLat = Math.floor(lati) - 1;
            stmt.setDouble(7, minLat);
            double maxLat = Math.ceil(lati);
            stmt.setDouble(8, maxLat);

            ResultSet rs = stmt.executeQuery();
            result = rs.next() ? new Location2D(rs.getInt(1), rs.getInt(2))
                : selectDefaultRightDown(reqContext);

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            ExceptionUtil.caught(e, reqContext);
            DatabaseFactory.removeConnectionCache(dbId);
        } catch (ClassNotFoundException e) {
            ExceptionUtil.caught(e, "DB Driver Not Found");
        } finally {
            LoggerUtil.info(logger,
                "Finished Nearest Approximation: " + (result == null ? "NULL" : result.toString()));
        }

        if (conn != null) {
            try {

                conn.close();

            } catch (SQLException e) {
                ExceptionUtil.caught(e, "Connection Closed.");
            }
        }
        return result;
    }

    /** 
     * @see cdb.web.dao.AnomalyInfoWDAO#selectDefaultRightDown()
     */
    @Override
    public Location2D selectDefaultRightDown(AnomalyEnvelope reqContext) {
        if (defaultRightDownVal != null) {
            return defaultRightDownVal;
        } else {
            Location2D result = null;
            Connection conn = null;

            String dbId = convertDBID(reqContext);
            try {
                conn = DatabaseFactory.getConnection(dbId);
                PreparedStatement stmt = conn.prepareStatement(DEFAULT_RIGHT_DOWN);

                ResultSet rs = stmt.executeQuery();
                result = rs.next() ? new Location2D(rs.getInt(1), rs.getInt(2)) : null;

                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                ExceptionUtil.caught(e, reqContext);
                DatabaseFactory.removeConnectionCache(dbId);
            } catch (ClassNotFoundException e) {
                ExceptionUtil.caught(e, "DB Driver Not Found");
            } finally {
                LoggerUtil.info(logger,
                    "Find  DefaultRightDown" + (result == null ? "NULL" : result.toString()));
            }

            defaultRightDownVal = result;
            return result;
        }
    }

    /** 
     * @see cdb.web.dao.AnomalyInfoWDAO#selectInBoxWithinTimeRange(cdb.web.bean.Location2D, cdb.web.bean.Location2D, cdb.web.bean.AnomalyRequest)
     */
    @Override
    public List<AnomalyVO> selectInBoxWithinTimeRange(Location2D leftUperCorner,
                                                      Location2D rightDownCorner,
                                                      AnomalyEnvelope reqContext) {
        List<AnomalyVO> resultSet = new ArrayList<AnomalyVO>();
        Connection conn = null;

        String dbId = convertDBID(reqContext);
        try {
            conn = DatabaseFactory.getConnection(dbId);
            PreparedStatement stmt = conn.prepareStatement(ANOMALY_IN_CERTAIN_TEMPORAL_SPATIAL);

            long daysOfStartDate = reqContext.getsDate().getTime() / (24 * 60 * 60 * 1000);
            stmt.setLong(1, daysOfStartDate);
            long daysOfEndDate = reqContext.geteDate().getTime() / (24 * 60 * 60 * 1000);
            stmt.setLong(2, daysOfEndDate);

            stmt.setInt(3, leftUperCorner.getRow());
            stmt.setInt(4, rightDownCorner.getRow());
            stmt.setInt(5, leftUperCorner.getColumn());
            stmt.setInt(6, rightDownCorner.getColumn());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                AnomalyVO bean = new AnomalyVO();
                bean.setLongi(rs.getDouble(1));
                bean.setLati(rs.getDouble(2));
                bean.setDate(new Date(rs.getLong(3) * 24 * 60 * 60 * 1000));
                resultSet.add(bean);
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            ExceptionUtil.caught(e, reqContext);
            DatabaseFactory.removeConnectionCache(dbId);
        } catch (ClassNotFoundException e) {
            ExceptionUtil.caught(e, "DB Driver Not Found");
        } finally {
            LoggerUtil.info(logger, "Finished AnomalyQuery: " + resultSet.size() + "\t Query: "
                                    + reqContext.toString());
        }

        return resultSet;
    }

    /**
     * convert to Database identity
     * 
     * @param reqContext
     * @return
     */

    protected String convertDBID(AnomalyEnvelope reqContext) {
        return "H2_" + reqContext.getDsName() + "_" + reqContext.getDsFreq();
    }

}
