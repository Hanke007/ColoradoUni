package cdb.web.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cdb.common.lang.DateUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.LoggerUtil;
import cdb.web.bean.AnomalyRequest;
import cdb.web.bean.GeoLocation;
import cdb.web.envelope.AnomalyEnvelope;
import cdb.web.service.AbstractAnmlDtcnService;
import cdb.web.vo.AnomalyVO;

/**
 * The controller to handle the url 
 * 
 * @author Chao Chen
 * @version $Id: AnomalyRetrieveController.java, v 0.1 Sep 26, 2015 4:14:18 PM chench Exp $
 */
@Controller
public class AnomalyRetrieveController extends AbstractController {

    @Autowired
    /** Anomaly detection service*/
    private AbstractAnmlDtcnService anomalyService;

    /**
     * 
     * <a href="http://www.leveluplunch.com/java/tutorials/014-post-json-to-spring-rest-webservice/">Json with multiple object</a>
     *  
     * @param anomlyRequest     Request object
     * @return
     */
    @RequestMapping(value = "/anomaly/ajaxRetrvAnomaly", method = RequestMethod.POST)
    @ResponseBody
    public List<AnomalyVO> ajaxRetrieveAnomalies(@RequestBody AnomalyRequest anomlyRequest) {
        List<AnomalyVO> result = new ArrayList<AnomalyVO>();

        // parameter validation
        if (anomlyRequest == null) {
            return result;
        } else
            if (anomlyRequest.getLocations() == null || anomlyRequest.getLocations().size() < 2) {
            LoggerUtil.warn(logger, "LOCATION SCARSITY!\nRequest: " + anomlyRequest);
            return result;
        }

        StopWatch stopWatch = new StopWatch();
        try {
            stopWatch.start();
            LoggerUtil.info(logger, "Request: " + anomlyRequest);

            AnomalyEnvelope reqContext = new AnomalyEnvelope();
            Date sDate = DateUtil.parse(anomlyRequest.getsDate(), DateUtil.WEB_FORMAT);
            reqContext.setsDate(sDate);
            Date eDate = DateUtil.parse(anomlyRequest.geteDate(), DateUtil.WEB_FORMAT);
            Date eDefault = new Date(sDate.getTime() + 10 * 24 * 60 * 60 * 1000);
            reqContext.seteDate(eDate.after(eDefault) ? eDefault : eDate);
            reqContext.setDsFreq(anomlyRequest.getDsFreq());
            reqContext.setDsName(anomlyRequest.getDsName());
            GeoLocation leftUperCorner = anomlyRequest.getLocations().get(0);
            GeoLocation rightDownCorner = anomlyRequest.getLocations().get(1);

            result = anomalyService.retrvAnomaly(leftUperCorner, rightDownCorner, reqContext);
            stopWatch.stop();
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "Check date format.");
        } catch (Exception e) {
            ExceptionUtil.caught(e, "URL:/anomaly/ajaxRetrvAnomaly failed.");
        } finally {
            LoggerUtil.info(logger,
                "Request: " + anomlyRequest + " Times: " + stopWatch.getTotalTimeSeconds());
        }
        return result;
    }

}
