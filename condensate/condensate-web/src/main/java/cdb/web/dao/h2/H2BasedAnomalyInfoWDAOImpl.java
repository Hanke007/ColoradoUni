package cdb.web.dao.h2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import cdb.common.lang.DateUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.LoggerUtil;
import cdb.dal.util.DatabaseFactory;
import cdb.web.bean.Location2D;
import cdb.web.dao.AnomalyInfoWDAO;
import cdb.web.envelope.AnomalyEnvelope;
import cdb.web.vo.AggregatedAnomalyVO;
import cdb.web.vo.AnomalyVO;

/**
 * 
 * @author Chao Chen
 * @version $Id: H2BasedAnomalyInfoWDAOImpl.java, v 0.1 Nov 9, 2015 2:56:26 PM chench Exp $
 */
@Repository
public class H2BasedAnomalyInfoWDAOImpl extends AbstractH2BasedDAO implements AnomalyInfoWDAO {
    /** the window size of each sub-aggregated-query */
    protected long bufferWindow = 1 * 365 * 24 * 60 * 60 * 1000L;

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
            LoggerUtil.debug(logger, "Finished AnomalyQuery: " + resultSet.size() + "\t Query: "
                                     + reqContext.toString());
        }

        return resultSet;
    }

    /** 
     * @see cdb.web.dao.AnomalyInfoWDAO#selectAggregatedInInBoxWithinTimeRange(cdb.web.bean.Location2D, cdb.web.bean.Location2D, cdb.web.envelope.AnomalyEnvelope)
     */
    @Override
    public List<AggregatedAnomalyVO> selectAggregatedInInBoxWithinTimeRange(Location2D leftUperCorner,
                                                                            Location2D rightDownCorner,
                                                                            AnomalyEnvelope reqContext) {
        Map<AggregatedAnomalyVO, AggregatedAnomalyVO> aggrgtdRep = new HashMap<AggregatedAnomalyVO, AggregatedAnomalyVO>();
        String dbId = convertDBID(reqContext);

        //since the query ranging for more than 3 years always require too much memory,
        //we split it into smaller one and merge the results 
        Date constEndDay = new Date(reqContext.geteDate().getTime());
        Date dBegin = reqContext.getsDate();
        while (dBegin.before(constEndDay)) {
            Date dEnd = new Date(dBegin.getTime() + bufferWindow);
            dEnd = dEnd.before(constEndDay) ? dEnd : constEndDay;

            // merge sub-query result
            Set<AggregatedAnomalyVO> subResultSet = selectAggregatedInner(leftUperCorner,
                rightDownCorner, dBegin, dEnd, dbId);
            for (AggregatedAnomalyVO one : subResultSet) {
                if (aggrgtdRep.containsKey(one)) {
                    // merge existing results
                    AggregatedAnomalyVO existingObject = aggrgtdRep.get(one);
                    existingObject.setFrequency(existingObject.getFrequency() + one.getFrequency());
                    existingObject.setMean(existingObject.getMean() + one.getMean());
                } else {
                    aggrgtdRep.put(one, one);
                }
            }

            // update repeated parameter
            dBegin = dEnd;
        }

        List<AggregatedAnomalyVO> resultSet = new ArrayList<AggregatedAnomalyVO>(
            aggrgtdRep.values());
        for (AggregatedAnomalyVO one : resultSet) {
            one.setMean(one.getMean() / one.getFrequency());
        }
        return resultSet;
    }

    /**
     * select the anomalies occurred in certain region within certain period
     * 
     * @param leftUperCorner        the location in left upper corner of the box
     * @param rightDownCorner       the location in right down corner of the box
     * @param reqContext            the major context of the request
     * @return
     */
    protected Set<AggregatedAnomalyVO> selectAggregatedInner(Location2D leftUperCorner,
                                                             Location2D rightDownCorner,
                                                             Date dateBegin, Date dateEnd,
                                                             String dbId) {
        Set<AggregatedAnomalyVO> resultSet = new HashSet<AggregatedAnomalyVO>();
        Connection conn = null;

        try {
            conn = DatabaseFactory.getConnection(dbId);
            PreparedStatement stmt = conn
                .prepareStatement(AGGREGATED_ANOMALIES_IN_CERTAIN_TEMPORAL_SPATIAL);

            long daysOfStartDate = dateBegin.getTime() / (24 * 60 * 60 * 1000);
            stmt.setLong(1, daysOfStartDate);
            long daysOfEndDate = dateEnd.getTime() / (24 * 60 * 60 * 1000);
            stmt.setLong(2, daysOfEndDate);

            stmt.setInt(3, leftUperCorner.getRow());
            stmt.setInt(4, rightDownCorner.getRow());
            stmt.setInt(5, leftUperCorner.getColumn());
            stmt.setInt(6, rightDownCorner.getColumn());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                AggregatedAnomalyVO bean = new AggregatedAnomalyVO();
                bean.setLongi(rs.getDouble(1));
                bean.setLati(rs.getDouble(2));
                bean.setFrequency(rs.getDouble(3));
                bean.setMean(rs.getDouble(4));
                resultSet.add(bean);
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            ExceptionUtil.caught(e, dbId);
            DatabaseFactory.removeConnectionCache(dbId);
        } catch (ClassNotFoundException e) {
            ExceptionUtil.caught(e, "DB Driver Not Found");
        } finally {
            LoggerUtil
                .debug(logger,
                    "Sub-Aggregated Query: " + resultSet.size() + "\t Query: " + "[dbID=" + dbId
                               + ", sDate=" + DateUtil.format(dateBegin, DateUtil.WEB_FORMAT)
                               + ", eDate=" + DateUtil.format(dateEnd, DateUtil.WEB_FORMAT) + "]");
        }
        return resultSet;
    }

}
