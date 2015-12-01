package cdb.web.service;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cdb.web.bean.GeoLocation;
import cdb.web.bean.Location2D;
import cdb.web.dao.AnomalyInfoWDAO;
import cdb.web.dao.LocationDAO;
import cdb.web.envelope.AnomalyEnvelope;
import cdb.web.vo.AggregatedAnomalyVO;
import cdb.web.vo.AnomalyVO;

/**
 * Detect anomalies in SSIM-data set
 * See <a href="http://nsidc.org/data/docs/daac/nsidc0001_ssmi_tbs.gd.html">Daily Polar Gridded Brightness Temperatures Document</a>
 * 
 * @author Chao Chen
 * @version $Id: AnomalyDetectionService.java, v 0.1 Sep 28, 2015 6:12:09 PM chench Exp $
 */
@Service
public class DefaultAnmlDtcnServiceImpl extends AbstractAnmlDtcnService {
    @Autowired
    /** the database access object */
    private AnomalyInfoWDAO anomalyInfoWDAO;

    @Autowired
    /** the database access object */
    private LocationDAO locationDAO;

    /** 
     * @see cdb.web.service.AbstractAnmlDtcnService#retrvAnomaly(cdb.common.model.Point, cdb.common.model.Point, cdb.web.envelope.AnomalyEnvelope)
     */
    @Override
    public List<AnomalyVO> retrvAnomaly(GeoLocation leftUperCorner, GeoLocation rightDownCorner,
                                        AnomalyEnvelope reqContext, HttpSession session) {
        // achieve the parameter in the session, otherwise in database 
        String lucSesnKey = reqContext.getDsName() + '_' + reqContext.getDsFreq() + "_LUC_"
                            + leftUperCorner;
        Location2D leftUpCorn = ((leftUpCorn = (Location2D) session
            .getAttribute(lucSesnKey)) == null)
                ? locationDAO.selectNearestLeftUp(leftUperCorner.getLongitude(),
                    leftUperCorner.getLatitude(), reqContext)
                : leftUpCorn;
        session.setAttribute(lucSesnKey, leftUpCorn);

        // achieve the parameter in the session, otherwise in database 
        String rdcSesnKey = reqContext.getDsName() + '_' + reqContext.getDsFreq() + "_RDC_"
                            + rightDownCorner;
        Location2D rightDownCorn = ((rightDownCorn = (Location2D) session
            .getAttribute(rdcSesnKey)) == null)
                ? locationDAO.selectNearestRightDown(rightDownCorner.getLongitude(),
                    rightDownCorner.getLatitude(), reqContext)
                : rightDownCorn;
        session.setAttribute(rdcSesnKey, rightDownCorn);

        return anomalyInfoWDAO.selectInBoxWithinTimeRange(leftUpCorn, rightDownCorn, reqContext);
    }

    /** 
     * @see cdb.web.service.AbstractAnmlDtcnService#retrvAggregatedAnomaly(cdb.web.bean.GeoLocation, cdb.web.bean.GeoLocation, cdb.web.envelope.AnomalyEnvelope)
     */
    @Override
    public List<AggregatedAnomalyVO> retrvAggregatedAnomaly(GeoLocation leftUperCorner,
                                                            GeoLocation rightDownCorner,
                                                            AnomalyEnvelope reqContext,
                                                            HttpSession session) {
        // achieve the parameter in the session, otherwise in database 
        String lucSesnKey = reqContext.getDsName() + '_' + reqContext.getDsFreq() + "_LUC_"
                            + leftUperCorner;
        Location2D leftUpCorn = ((leftUpCorn = (Location2D) session
            .getAttribute(lucSesnKey)) == null)
                ? locationDAO.selectNearestLeftUp(leftUperCorner.getLongitude(),
                    leftUperCorner.getLatitude(), reqContext)
                : leftUpCorn;
        session.setAttribute(lucSesnKey, leftUpCorn);

        // achieve the parameter in the session, otherwise in database 
        String rdcSesnKey = reqContext.getDsName() + '_' + reqContext.getDsFreq() + "_RDC_"
                            + rightDownCorner;
        Location2D rightDownCorn = ((rightDownCorn = (Location2D) session
            .getAttribute(rdcSesnKey)) == null)
                ? locationDAO.selectNearestRightDown(rightDownCorner.getLongitude(),
                    rightDownCorner.getLatitude(), reqContext)
                : rightDownCorn;
        session.setAttribute(rdcSesnKey, rightDownCorn);

        return anomalyInfoWDAO.selectAggregatedInInBoxWithinTimeRange(leftUpCorn, rightDownCorn,
            reqContext);
    }

}
