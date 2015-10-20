package cdb.dataset.avhr;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import cdb.common.lang.DateUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.StringUtil;
import cdb.common.lang.log4j.LoggerDefineConstant;

/**
 * 
 * @author Chao Chen
 * @version $Id: AbstractDSGen.java, v 0.1 Oct 14, 2015 10:48:34 AM chench Exp $
 */
public abstract class AbstractDsGen {

    /** the root directory of the dataset*/
    protected final static String   ROOT_DIR = "C:/Users/chench/Desktop/SIDS/AVHR/";
    /** the Calendar object */
    protected final static Calendar cal      = Calendar.getInstance();

    /** logger */
    protected final static Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    protected static List<String> imgWorkingSetGen(Date sDate, Date eDate, String freqId,
                                                   List<String> timeShorFormat) {
        List<String> testSet = new ArrayList<String>();

        Date curDate = sDate;
        while (curDate.before(eDate)) {
            String dateStr = DateUtil.format(curDate, DateUtil.SHORT_FORMAT);
            String fileAnml = binFileConvntn(dateStr, freqId);
            testSet.add(fileAnml);
            timeShorFormat.add(dateStr);

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

        try {
            Date date = DateUtil.parse(taskId, DateUtil.SHORT_FORMAT);
            cal.setTime(date);

            int timeRange = Integer.valueOf(taskId) / 100;
            int year = timeRange / 100;
            String fileName = ROOT_DIR + year + "PROC/";
            if (timeRange < 200201) {
                String timeStr = year + StringUtil.alignRight("" + cal.get(Calendar.DAY_OF_YEAR), 3,
                    '0');
                fileName += "a14_n005_" + timeStr + "_0400_" + freqId + ".v3";
            }

            return fileName;
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "Date parse crashed. " + taskId);
        }

        return null;
    }

}
