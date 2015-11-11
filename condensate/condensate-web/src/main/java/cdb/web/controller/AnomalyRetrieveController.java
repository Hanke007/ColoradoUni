package cdb.web.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
        try {
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

            return anomalyService.retrvAnomaly(leftUperCorner, rightDownCorner, reqContext);
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "Check date format.");
        } catch (Exception e) {
            ExceptionUtil.caught(e, "URL:/anomaly/ajaxRetrvAnomaly failed.");
        } finally {

        }
        return new ArrayList<AnomalyVO>();
    }

    /**
     * 
     * <a href="http://www.leveluplunch.com/java/tutorials/014-post-json-to-spring-rest-webservice/">Json with multiple object</a>
     *  
     * @param anomlyRequest     Request object
     * @return
     */
    @RequestMapping(value = "/anomaly/ajaxImutation")
    @ResponseBody
    public List<AnomalyVO> ajaxImutation(@RequestBody AnomalyRequest anomlyRequest) {
        try {
            List<AnomalyVO> arr = new ArrayList<AnomalyVO>();
            Date sDate = DateUtil.parse(anomlyRequest.getsDate(), DateUtil.WEB_FORMAT);
            Date eDate = DateUtil.parse(anomlyRequest.geteDate(), DateUtil.WEB_FORMAT);

            Date cDate = sDate;
            while (!cDate.after(eDate)) {
                int num = (int) (Math.random() * 50);
                for (int i = 0; i < num; i++) {
                    int row = (int) (Math.random() * 200 + 100);
                    int col = (int) (Math.random() * 200 + 50);

                    AnomalyVO one = new AnomalyVO();
                    one.setDate(new Date(cDate.getTime()));
                    one.setLati(row);
                    one.setLongi(col);
                    one.setVal(0);
                    arr.add(one);
                }

                cDate.setTime(cDate.getTime() + 24 * 60 * 60 * 1000);
            }

            return arr;
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "Check date format.");
        }
        return null;
    }

}
