package cdb.web.dao;

import org.apache.log4j.Logger;

import cdb.common.lang.log4j.LoggerDefineConstant;
import cdb.web.bean.Location2D;
import cdb.web.envelope.AnomalyEnvelope;

/**
 * 
 * @author Chao Chen
 * @version $Id: LocationDAO.java, v 0.1 Nov 30, 2015 3:41:49 PM chench Exp $
 */
public interface LocationDAO {
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
}
