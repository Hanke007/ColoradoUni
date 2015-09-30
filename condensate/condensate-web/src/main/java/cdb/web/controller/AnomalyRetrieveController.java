package cdb.web.controller;

import java.text.ParseException;
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
import cdb.web.bean.AnomlyRequest;
import cdb.web.bean.Location2D;
import cdb.web.service.AbstractAnmlDtcnService;
import cdb.web.vo.AnomalyVO;

/**
 * The controller to handle the url 
 * 
 * @author Chao Chen
 * @version $Id: AnomalyRetrieveController.java, v 0.1 Sep 26, 2015 4:14:18 PM chench Exp $
 */
@Controller
public class AnomalyRetrieveController {

    /** Anomaly detection service*/
    @Autowired
    private AbstractAnmlDtcnService anomalyService;

    /**
     * 
     * <a href="http://www.leveluplunch.com/java/tutorials/014-post-json-to-spring-rest-webservice/">Json with multiple object</a>
     *  
     * @param anomlyRequest     Request object
     * @return
     */
    @RequestMapping(value = "/anomaly/ajaxRetrv", method = RequestMethod.POST)
    @ResponseBody
    public List<AnomalyVO> ajaxRetrieveAnomalies(@RequestBody AnomlyRequest anomlyRequest) {
        try {
            int loctnNum = anomlyRequest.getLocations().size();
            Date sDate = DateUtil.parse(anomlyRequest.getsDate(), DateUtil.SHORT_FORMAT);
            Date eDate = DateUtil.parse(anomlyRequest.geteDate(), DateUtil.SHORT_FORMAT);
            return anomalyService.retrvAnomaly(sDate, eDate,
                anomlyRequest.getLocations().toArray(new Location2D[loctnNum]),
                anomlyRequest.getDsFreq());
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "Check date format.");
        }
        return null;
    }

}
