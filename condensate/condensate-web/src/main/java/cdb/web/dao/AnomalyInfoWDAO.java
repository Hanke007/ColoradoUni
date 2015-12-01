package cdb.web.dao;

import java.util.List;

import org.apache.log4j.Logger;

import cdb.common.lang.log4j.LoggerDefineConstant;
import cdb.web.bean.Location2D;
import cdb.web.envelope.AnomalyEnvelope;
import cdb.web.vo.AggregatedAnomalyVO;
import cdb.web.vo.AnomalyVO;

/**
 * 
 * @author Chao Chen
 * @version $Id: AnomalyInfoWDAO.java, v 0.1 Nov 9, 2015 2:43:37 PM chench Exp $
 */
public interface AnomalyInfoWDAO {

    /** logger */
    public final static Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** select the anomalies occurred in certain region within certain period*/
    public final String ANOMALY_IN_CERTAIN_TEMPORAL_SPATIAL              = "SELECT loc.LON, loc.LAT, tp.TIMESTAMP "
                                                                           + "FROM (SELECT * FROM VECTORS "
                                                                           + "WHERE TIMESTAMPID IN ( SELECT ID "
                                                                           + "FROM TIMESTAMPS "
                                                                           + "WHERE TIMESTAMP >= ? AND TIMESTAMP <= ? "
                                                                           + ")) As vec "
                                                                           + "INNER JOIN LOCATIONS  AS loc  ON loc.ID = vec.LOCATIONID  "
                                                                           + "INNER JOIN TIMESTAMPS As tp   ON tp.ID = vec.TIMESTAMPID "
                                                                           + "WHERE ROW >= ? AND ROW <= ? AND COL >= ? AND COL <= ? ";
    /** select the aggregated anomalies occurred in certain region within certain period*/
    public final String AGGREGATED_ANOMALIES_IN_CERTAIN_TEMPORAL_SPATIAL = "SELECT loc.LON, loc.LAT, COUNT(vec.ID), SUM(vec.VALUE) "
                                                                           + "FROM (SELECT * FROM VECTORS "
                                                                           + "WHERE TIMESTAMPID IN ( SELECT ID "
                                                                           + "FROM TIMESTAMPS "
                                                                           + "WHERE TIMESTAMP >= ? AND TIMESTAMP < ? "
                                                                           + ")) As vec "
                                                                           + "INNER JOIN LOCATIONS  AS loc  ON loc.ID = vec.LOCATIONID  "
                                                                           + "INNER JOIN TIMESTAMPS As tp   ON tp.ID = vec.TIMESTAMPID "
                                                                           + "WHERE ROW >= ? AND ROW <= ? AND COL >= ? AND COL <= ? "
                                                                           + "GROUP BY loc.ID";

    /**
     * select the anomalies occurred in certain region within certain period
     * 
     * @param leftUperCorner        the location in left upper corner of the box
     * @param rightDownCorner       the location in right down corner of the box
     * @param reqContext            the major context of the request
     * @return
     */
    public List<AnomalyVO> selectInBoxWithinTimeRange(Location2D leftUperCorner,
                                                      Location2D rightDownCorner,
                                                      AnomalyEnvelope reqContext);

    /**
     * select the aggregated anomalies occurred in certain region within certain period
     * 
     * @param leftUperCorner        the location in left upper corner of the box
     * @param rightDownCorner       the location in right down corner of the box
     * @param reqContext            the major context of the request
     * @return
     */
    public List<AggregatedAnomalyVO> selectAggregatedInInBoxWithinTimeRange(Location2D leftUperCorner,
                                                                            Location2D rightDownCorner,
                                                                            AnomalyEnvelope reqContext);
}
