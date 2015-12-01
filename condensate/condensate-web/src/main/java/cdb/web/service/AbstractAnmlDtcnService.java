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
     * @param sDate     start date
     * @param eDate     end date
     * @param locals    the target locations to check
     * @param session   session object
     * @return          the list of the anomalies
     */
    public abstract List<AnomalyVO> retrvAnomaly(GeoLocation leftUperCorner,
                                                 GeoLocation rightDownCorner,
                                                 AnomalyEnvelope reqContext, HttpSession session);

    /**
     * make a summary of the anomalies given start and end dates
     * 
     * @param sDate     start date
     * @param eDate     end date
     * @param locals    the target locations to check
     * @param session   session object
     * @return          the list of the anomalies
     */
    public abstract List<AggregatedAnomalyVO> retrvAggregatedAnomaly(GeoLocation leftUperCorner,
                                                                     GeoLocation rightDownCorner,
                                                                     AnomalyEnvelope reqContext,
                                                                     HttpSession session);
}
