package cdb.web.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cdb.web.bean.GeoLocation;
import cdb.web.bean.Location2D;
import cdb.web.dao.AnomalyInfoWDAO;
import cdb.web.envelope.AnomalyEnvelope;
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

    /** 
     * @see cdb.web.service.AbstractAnmlDtcnService#retrvAnomaly(cdb.common.model.Point, cdb.common.model.Point, cdb.web.envelope.AnomalyEnvelope)
     */
    @Override
    public List<AnomalyVO> retrvAnomaly(GeoLocation leftUperCorner, GeoLocation rightDownCorner,
                                        AnomalyEnvelope reqContext) {
        Location2D leftUpCorn = anomalyInfoWDAO.selectNearestApproximatedLocation(
            leftUperCorner.getLongitude(), leftUperCorner.getLatitude(), reqContext);
        Location2D rightDownCorn = anomalyInfoWDAO.selectNearestApproximatedLocation(
            rightDownCorner.getLongitude(), rightDownCorner.getLatitude(), reqContext);

        if (leftUpCorn == null | rightDownCorn == null) {
            return new ArrayList<AnomalyVO>();
        } else {
            return anomalyInfoWDAO.selectInBoxWithinTimeRange(leftUpCorn, rightDownCorn,
                reqContext);
        }
    }

}
