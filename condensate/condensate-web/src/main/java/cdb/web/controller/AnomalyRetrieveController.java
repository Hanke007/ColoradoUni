package cdb.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cdb.web.vo.AnomalyVO;

/**
 * The controller to handle the url 
 * 
 * @author Chao Chen
 * @version $Id: AnomalyRetrieveController.java, v 0.1 Sep 26, 2015 4:14:18 PM chench Exp $
 */
@Controller
public class AnomalyRetrieveController {

    @RequestMapping(value = "/anomaly/ajaxRetrv", method = RequestMethod.GET)
    @ResponseBody
    public List<AnomalyVO> ajaxRetrieveAnomalies(@RequestParam("dsName") String dsName) {
        List<AnomalyVO> result = new ArrayList<AnomalyVO>();
        result.add(new AnomalyVO(new Date(), 0.0d, 0.0d, 0.0d));
        return result;
    }
}
