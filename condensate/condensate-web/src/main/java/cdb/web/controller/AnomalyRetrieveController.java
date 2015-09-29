package cdb.web.controller;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cdb.common.lang.DateUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.dal.vo.Location;
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

    @RequestMapping(value = "/anomaly/ajaxRetrv", method = RequestMethod.GET)
    @ResponseBody
    public List<AnomalyVO> ajaxRetrieveAnomalies(@RequestParam("dsName") String dsName,
                                                 @RequestParam("dsFreq") String freqId) {
        try {
            Date sDate = DateUtil.parse("20050112", DateUtil.SHORT_FORMAT);
            Date eDate = DateUtil.parse("20100112", DateUtil.SHORT_FORMAT);
            Location[] locals = { new Location(100, 100) };
            return anomalyService.retrvAnomaly(sDate, eDate, locals, freqId);
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "Check date format.");
        }

        return null;
    }
}
