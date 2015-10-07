package cdb.web.controller;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cdb.common.lang.DateUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.web.bean.AnomalyRequest;
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
    @Autowired
    protected ServletContext        servltContext;

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

    @RequestMapping(value = "/anomaly/ajaxRetrvImage", method = RequestMethod.POST)
    public ModelAndView ajaxDrawImage(@RequestBody AnomalyRequest anomlyRequest) {
        try {
            int loctnNum = anomlyRequest.getLocations().size();
            Date sDate = DateUtil.parse(anomlyRequest.getsDate(), DateUtil.WEB_FORMAT);
            Date eDate = DateUtil.parse(anomlyRequest.geteDate(), DateUtil.WEB_FORMAT);
            List<String> imageUrl = anomalyService.retrvImageUrl(sDate, eDate,
                anomlyRequest.getLocations().toArray(new Location2D[loctnNum]),
                anomlyRequest.getDsFreq());

            ModelAndView respnse = new ModelAndView("anomaly");
            Map<String, Object> vcContxt = new HashMap<String, Object>();
            vcContxt.put("imageUrl", imageUrl);
            respnse.addObject("context", vcContxt);
            return respnse;
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "Check date format.");
        }

        return new ModelAndView("welcome");
    }

}
