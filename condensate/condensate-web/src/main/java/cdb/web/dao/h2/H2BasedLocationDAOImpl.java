package cdb.web.dao.h2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.LoggerUtil;
import cdb.dal.util.DatabaseFactory;
import cdb.web.bean.Location2D;
import cdb.web.dao.LocationDAO;
import cdb.web.envelope.AnomalyEnvelope;

/**
 * 
 * @author Chao Chen
 * @version $Id: H2BasedLocationDAOImpl.java, v 0.1 Nov 30, 2015 3:43:28 PM chench Exp $
 */
@Repository
public class H2BasedLocationDAOImpl extends AbstractH2BasedDAO implements LocationDAO {

    /** 
     * @see cdb.web.dao.LocationDAO#selectNearestLeftUp(double, double, cdb.web.envelope.AnomalyEnvelope)
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
     * @see cdb.web.dao.LocationDAO#selectNearestRightDown(double, double, cdb.web.envelope.AnomalyEnvelope)
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
     * @see cdb.web.dao.LocationDAO#selectDefaultRightDown(cdb.web.envelope.AnomalyEnvelope)
     */
    @Override
    public Location2D selectDefaultRightDown(AnomalyEnvelope reqContext) {
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

        return result;
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
