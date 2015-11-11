package cdb.web.dao;

import java.util.List;

import org.apache.log4j.Logger;

import cdb.common.lang.log4j.LoggerDefineConstant;
import cdb.web.bean.Location2D;
import cdb.web.envelope.AnomalyEnvelope;
import cdb.web.vo.AnomalyVO;

/**
 * 
 * @author Chao Chen
 * @version $Id: AnomalyInfoWDAO.java, v 0.1 Nov 9, 2015 2:43:37 PM chench Exp $
 */
public interface AnomalyInfoWDAO {

    /** logger */
    public final static Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** select the nearest approximated geological location with the given longitude and latitude*/
    public final String NEAREST_APPROXIMATED = "SELECT * FROM ( "
                                               + "SELECT ROW, COL, (LON-?)*(LON-?) + (LAT-?)*(LAT-?) as dis "
                                               + "FROM ( Select * FROM LOCATIONS "
                                               + "WHERE LON > ? AND LON < ? AND LAT > ? AND LAT < ?)) "
                                               + "ORDER BY dis ASC LIMIT 1 ";

    /** select the default value of the right down corner*/
    public final String DEFAULT_RIGHT_DOWN = "SELECT MAX(ROW), MAX(COL) FROM LOCATIONS";

    /** select the anomalies occurred in certain region within certain period*/
    public final String ANOMALY_IN_CERTAIN_TEMPORAL_SPATIAL = "SELECT loc.LON, loc.LAT, tp.TIMESTAMP "
                                                              + "FROM (SELECT * FROM VECTORS "
                                                              + "WHERE TIMESTAMPID IN ( SELECT ID "
                                                              + "FROM TIMESTAMPS "
                                                              + "WHERE TIMESTAMP >= ? AND TIMESTAMP <= ? "
                                                              + ")) As vec "
                                                              + "INNER JOIN LOCATIONS  AS loc  ON loc.ID = vec.LOCATIONID  "
                                                              + "INNER JOIN TIMESTAMPS As tp   ON tp.ID = vec.TIMESTAMPID "
                                                              + "WHERE ROW >= ? AND ROW <= ? AND COL >= ? AND COL <= ? ";

    /**
     * select the nearest approximated geological location with the given longitude and latitude
     * 
     * @param longi     the given longitude
     * @param lati      the given latitude
     * @param reqContext            the major context of the request
     * @return
     */
    public Location2D selectNearestLeftUp(double longi, double lati, AnomalyEnvelope reqContext);

    /**
     * select the nearest approximated geological location with the given longitude and latitude
     * 
     * @param longi     the given longitude
     * @param lati      the given latitude
     * @param reqContext            the major context of the request
     * @return
     */
    public Location2D selectNearestRightDown(double longi, double lati, AnomalyEnvelope reqContext);

    /**
     * select the default value of the right down corner
     * 
     * @param reqContext            the major context of the request
     * @return
     */
    public Location2D selectDefaultRightDown(AnomalyEnvelope reqContext);

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
}
