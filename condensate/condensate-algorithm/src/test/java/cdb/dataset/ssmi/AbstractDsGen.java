package cdb.dataset.ssmi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import cdb.common.lang.DateUtil;
import cdb.common.lang.log4j.LoggerDefineConstant;

/**
 * 
 * @author Chao Chen
 * @version $Id: AbstractDSGen.java, v 0.1 Oct 14, 2015 10:48:34 AM chench Exp $
 */
public abstract class AbstractDsGen {

    /** the root directory of the dataset*/
    protected final static String ROOT_DIR = "C:/Users/chench/Desktop/SIDS/SSMI/";

    /** logger */
    protected final static Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    protected static List<String> imgWorkingSetGen(Date sDate, Date eDate, String freqId) {
        List<String> testSet = new ArrayList<String>();

        Date curDate = sDate;
        while (curDate.before(eDate)) {
            String fileAnml = binFileConvntn(DateUtil.format(curDate, DateUtil.SHORT_FORMAT),
                freqId);
            testSet.add(fileAnml);

            //move to next day
            curDate.setTime(curDate.getTime() + 24 * 60 * 60 * 1000);
        }
        return testSet;
    }

    /**
     * the conventional way to name the binary files
     * 
     * @param taskId    the task identify   yyyymmdd
     * @param freqId    the frequency identify
     * @return          file name
     */
    public static String binFileConvntn(String taskId, String freqId) {
        int timeRange = Integer.valueOf(taskId) / 100;
        int year = timeRange / 100;
        String fileName = ROOT_DIR + year + "/";
        if (timeRange < 199201) {
            fileName += "tb_f08_" + taskId + "_v2_" + freqId + ".bin";
        } else if (timeRange < 199601) {
            fileName += "tb_f11_" + taskId + "_v2_" + freqId + ".bin";
        } else if (timeRange < 200901) {
            fileName += "tb_f13_" + taskId + "_v2_" + freqId + ".bin";
        } else if (timeRange < 201001) {
            fileName += "tb_f13_" + taskId + "_v3_" + freqId + ".bin";
        } else {
            fileName += "tb_f17_" + taskId + "_v4_" + freqId + ".bin";
        }

        return fileName;
    }

    protected static List<String> anomalyWorkingSetGen(Date sDate, Date eDate, String freqId) {
        List<String> testSet = new ArrayList<String>();

        Date curDate = sDate;
        while (curDate.before(eDate)) {
            String fileAnml = ROOT_DIR + "StatisticAnomaly/"
                              + DateUtil.format(curDate, DateUtil.SHORT_FORMAT) + '_' + freqId
                              + ".OBJ";
            testSet.add(fileAnml);

            //move to next day
            curDate.setTime(curDate.getTime() + 24 * 60 * 60 * 1000);
        }
        return testSet;
    }
}
