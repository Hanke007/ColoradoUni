package cdb.web.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

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
import cdb.common.lang.StringUtil;
import cdb.web.bean.AnomalyRequest;
import cdb.web.bean.GeoLocation;
import cdb.web.envelope.AnomalyEnvelope;
import cdb.web.service.AbstractAnmlDtcnService;
import cdb.web.vo.AggregatedAnomalyVO;
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
     * @param session           Session object
     * @return
     */
    @RequestMapping(value = "/anomaly/ajaxRetrvAnomaly", method = RequestMethod.POST)
    @ResponseBody
    public List<AnomalyVO> ajaxRetrieveAnomalies(@RequestBody AnomalyRequest anomlyRequest,
                                                 HttpSession session) {
        List<AnomalyVO> result = new ArrayList<AnomalyVO>();

        // parameter validation
        if (anomlyRequest == null) {
            return result;
        } else if (StringUtil.isBlank(anomlyRequest.geteDate())
                   || StringUtil.isBlank(anomlyRequest.getsDate())) {
            LoggerUtil.debug(logger, "Date SCARSITY!\nRequest: " + anomlyRequest);
            return result;
        } else
            if (anomlyRequest.getLocations() == null || anomlyRequest.getLocations().size() < 2) {
            LoggerUtil.debug(logger, "LOCATION SCARSITY!\nRequest: " + anomlyRequest);
            return result;
        }

        StopWatch stopWatch = new StopWatch();
        try {
            LoggerUtil.debug(logger, "Request: " + anomlyRequest);
            stopWatch.start();
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

            result = anomalyService.retrvAnomaly(leftUperCorner, rightDownCorner, reqContext,
                session);
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

    /**
     * make a summary of the anomalies given start and end dates
     * 
     * @param anomlyRequest     Request object
     * @param session           Session object
     * @return
     */
    @RequestMapping(value = "/anomaly/ajaxRetrvAggAnomaly", method = RequestMethod.POST)
    @ResponseBody
    public List<AggregatedAnomalyVO> ajaxRetrieveAggregatedAnomalies(@RequestBody AnomalyRequest anomlyRequest,
                                                                     HttpSession session) {
        List<AggregatedAnomalyVO> result = new ArrayList<AggregatedAnomalyVO>();

        // parameter validation
        if (anomlyRequest == null) {
            return result;
        } else if (StringUtil.isBlank(anomlyRequest.geteDate())
                   || StringUtil.isBlank(anomlyRequest.getsDate())) {
            LoggerUtil.debug(logger, "Date SCARSITY!\nRequest: " + anomlyRequest);
            return result;
        } else
            if (anomlyRequest.getLocations() == null || anomlyRequest.getLocations().size() < 2) {
            LoggerUtil.debug(logger, "LOCATION SCARSITY!\nRequest: " + anomlyRequest);
            return result;
        }

        StopWatch stopWatch = new StopWatch();
        try {
            LoggerUtil.debug(logger, "Request: " + anomlyRequest);
            stopWatch.start();
            AnomalyEnvelope reqContext = new AnomalyEnvelope();
            Date sDate = DateUtil.parse(anomlyRequest.getsDate(), DateUtil.WEB_FORMAT);
            reqContext.setsDate(sDate);
            Date eDate = DateUtil.parse(anomlyRequest.geteDate(), DateUtil.WEB_FORMAT);
            reqContext.seteDate(eDate);
            if (sDate.after(eDate)) {
                return result;
            }

            reqContext.setDsFreq(anomlyRequest.getDsFreq());
            reqContext.setDsName(anomlyRequest.getDsName());
            GeoLocation leftUperCorner = anomlyRequest.getLocations().get(0);
            GeoLocation rightDownCorner = anomlyRequest.getLocations().get(1);

            result = anomalyService.retrvAggregatedAnomaly(leftUperCorner, rightDownCorner,
                reqContext, session);
            stopWatch.stop();
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "Check date format.");
        } catch (Exception e) {
            ExceptionUtil.caught(e, "URL:/anomaly/ajaxRetrvAggAnomaly failed.");
        } finally {
            LoggerUtil.info(logger,
                "Request: " + anomlyRequest + " Times: " + stopWatch.getTotalTimeSeconds());
        }
        return result;
    }

}
