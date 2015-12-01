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
        Location2D[] mappdCorns = transfrmLocation(leftUperCorner, rightDownCorner, reqContext,
            session);
        Location2D leftUpCorn = mappdCorns[0];
        Location2D rightDownCorn = mappdCorns[1];

        return anomalyInfoWDAO.selectInBoxWithinTimeRange(leftUpCorn, rightDownCorn, reqContext);
    }

    /** 
     * @see cdb.web.service.AbstractAnmlDtcnService#retrvAggregatedAnomaly(cdb.web.bean.GeoLocation, cdb.web.bean.GeoLocation, cdb.web.envelope.AnomalyEnvelope)
     */
    @Override
    public List<AggregatedAnomalyVO> retrvYearlyAggregatedAnomaly(GeoLocation leftUperCorner,
                                                                  GeoLocation rightDownCorner,
                                                                  AnomalyEnvelope reqContext,
                                                                  HttpSession session) {
        Location2D[] mappdCorns = transfrmLocation(leftUperCorner, rightDownCorner, reqContext,
            session);
        Location2D leftUpCorn = mappdCorns[0];
        Location2D rightDownCorn = mappdCorns[1];

        return anomalyInfoWDAO.selectYearlyAggregatedInInBoxWithinTimeRange(leftUpCorn,
            rightDownCorn, reqContext);
    }

    /** 
     * @see cdb.web.service.AbstractAnmlDtcnService#retrvMonthlyAggregatedAnomaly(cdb.web.bean.GeoLocation, cdb.web.bean.GeoLocation, cdb.web.envelope.AnomalyEnvelope, javax.servlet.http.HttpSession)
     */
    @Override
    public List<AggregatedAnomalyVO> retrvMonthlyAggregatedAnomaly(GeoLocation leftUperCorner,
                                                                   GeoLocation rightDownCorner,
                                                                   AnomalyEnvelope reqContext,
                                                                   HttpSession session) {
        Location2D[] mappdCorns = transfrmLocation(leftUperCorner, rightDownCorner, reqContext,
            session);
        Location2D leftUpCorn = mappdCorns[0];
        Location2D rightDownCorn = mappdCorns[1];

        return anomalyInfoWDAO.selectMonthlylyAggregatedInInBoxWithinTimeRange(leftUpCorn,
            rightDownCorn, reqContext);
    }

    /**
     * transform geographic locations to figure locations
     * 
     * @param sDate     start date
     * @param eDate     end date
     * @param locals    the target locations to check
     * @param session   session object
     * @return
     */
    protected Location2D[] transfrmLocation(GeoLocation leftUperCorner, GeoLocation rightDownCorner,
                                            AnomalyEnvelope reqContext, HttpSession session) {
        Location2D[] results = new Location2D[2];
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

        results[0] = leftUpCorn;
        results[1] = rightDownCorn;
        return results;
    }

}
