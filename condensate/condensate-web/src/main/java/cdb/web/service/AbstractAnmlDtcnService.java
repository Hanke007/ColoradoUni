package cdb.web.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import cdb.common.lang.log4j.LoggerDefineConstant;
import cdb.service.dataset.DatasetProc;
import cdb.web.bean.Location2D;
import cdb.web.vo.AnomalyVO;

/**
 * Detect anomalies
 * 
 * @author Chao Chen
 * @version $Id: AbstractService.java, v 0.1 Sep 29, 2015 2:35:51 PM chench Exp $
 */
public abstract class AbstractAnmlDtcnService {
    /** logger */
    protected final static Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);
    /** the data processor to parse the binary data*/
    protected DatasetProc         dProc;

    /**
     * detect anomalies in SSIM-dataset given start and end dates
     * 
     * @param sDate     start date
     * @param eDate     end date
     * @param locals    the target locations to check
     * @return          the list of the anomalies
     * @throws ParseException  Date parse exception
     */
    public abstract List<AnomalyVO> retrvAnomaly(Date sDate, Date eDate, Location2D[] locals,
                                                 String freqId);

    /**
     * query the anomalies in Repository
     * 
     * @param sDate     start date
     * @param eDate     end date
     * @param locals    the target locations to check
     * @return          the list of the anomalies
     * @throws ParseException  Date parse exception
     */
    public abstract List<String> retrvImageUrl(Date sDate, Date eDate, Location2D[] locals,
                                               String freqId);

    /**
     * Getter method for property <tt>dProc</tt>.
     * 
     * @return property value of dProc
     */
    public DatasetProc getdProc() {
        return dProc;
    }

    /**
     * Setter method for property <tt>dProc</tt>.
     * 
     * @param dProc value to be assigned to property dProc
     */
    public void setdProc(DatasetProc dProc) {
        this.dProc = dProc;
    }
}
