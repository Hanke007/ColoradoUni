package cdb.web.service;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import cdb.common.lang.log4j.LoggerDefineConstant;
import cdb.web.bean.GeoLocation;
import cdb.web.envelope.AnomalyEnvelope;
import cdb.web.vo.AggregatedAnomalyVO;
import cdb.web.vo.AnomalyVO;

/**
 * Detect anomalies
 * 
 * @author Chao Chen
 * @version $Id: AbstractService.java, v 0.1 Sep 29, 2015 2:35:51 PM chench Exp $
 */
public abstract class AbstractAnmlDtcnService {
    /** logger */
    protected final static Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /**
     * detect anomalies in SSIM-dataset given start and end dates
     * 
     * @param leftUperCorner        the geographic location in left upper corner
     * @param rightDownCorner       the geographic location in right down corner
     * @param reqContext            the request object
     * @param session               the http session
     * @return                      the list of the anomalies
     */
    public abstract List<AnomalyVO> retrvAnomaly(GeoLocation leftUperCorner,
                                                 GeoLocation rightDownCorner,
                                                 AnomalyEnvelope reqContext, HttpSession session);

    /**
     * make a summary of the anomalies given start and end dates
     * 
     * @param leftUperCorner        the geographic location in left upper corner
     * @param rightDownCorner       the geographic location in right down corner
     * @param reqContext            the request object
     * @param session               the http session
     * @return                      the list of the anomalies
     */
    public abstract List<AggregatedAnomalyVO> retrvYearlyAggregatedAnomaly(GeoLocation leftUperCorner,
                                                                           GeoLocation rightDownCorner,
                                                                           AnomalyEnvelope reqContext,
                                                                           HttpSession session);

    /**
     * make a summary of the anomalies given start and end dates
     * 
     * @param leftUperCorner        the geographic location in left upper corner
     * @param rightDownCorner       the geographic location in right down corner
     * @param reqContext            the request object
     * @param session               the http session
     * @return                      the list of the anomalies
     */
    public abstract List<AggregatedAnomalyVO> retrvMonthlyAggregatedAnomaly(GeoLocation leftUperCorner,
                                                                            GeoLocation rightDownCorner,
                                                                            AnomalyEnvelope reqContext,
                                                                            HttpSession session);
}
