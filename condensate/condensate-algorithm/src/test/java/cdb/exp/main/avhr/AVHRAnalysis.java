package cdb.exp.main.avhr;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cdb.common.lang.DateUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.StringUtil;
import cdb.common.lang.log4j.LoggerDefineConstant;

/**
 * 
 * @author Chao Chen
 * @version $Id: AVHRAnalysis.java, v 0.1 Oct 20, 2015 3:53:24 PM chench Exp $
 */
public abstract class AVHRAnalysis {
    /** the root directory of the dataset*/
    protected final static String   ROOT_DIR = "C:/Users/chench/Desktop/SIDS/AVHR/";
    /** the Calendar object */
    protected final static Calendar cal      = Calendar.getInstance();

    /** logger */
    protected final static Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /**
     * the conventional way to name the serialized mean files
     * 
     * @param taskId    the task identify   yyyymm
     * @param freqId    the frequency identify
     * @return          file name
     */
    protected static String meanSerialNameConvntn(String taskId, String freqId) {
        return ROOT_DIR + "Condensate/mean_" + taskId + "_" + freqId + ".OBJ";
    }

    /**
     * the conventional way to name the serialized sd files
     * 
     * @param taskId    the task identify   yyyymm
     * @param freqId    the frequency identify
     * @return          file name
     */
    protected static String sdSerialNameConvntn(String taskId, String freqId) {
        return ROOT_DIR + "Condensate/sd_" + taskId + "_" + freqId + ".OBJ";
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
                fileName += "a14_n005_" + timeStr + "_" + freqId + ".v3";
            }

            return fileName;
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "Date parse crashed. " + taskId);
        }

        return null;
    }

    /**
     * get the dimensions given the frequency identification
     *      
     * @param freqId    frequency id
     * @return
     */
    protected static int[] dimensions(String freqId) {
        int[] dimension = new int[2];
        dimension[0] = 452;
        dimension[1] = 452;
        return dimension;
    }

    protected static Map<String, List<String>> imgWorkingSetGen(Date sDate, Date eDate,
                                                                String freqId) {
        Map<String, List<String>> testSet = new HashMap<String, List<String>>();

        Date curDate = sDate;
        while (curDate.before(eDate)) {
            String dateStr = DateUtil.format(curDate, DateUtil.SHORT_FORMAT);
            String fileAnml = binFileConvntn(dateStr, freqId);

            cal.setTime(curDate);
            String month = StringUtil.alignRight("" + (cal.get(Calendar.MONTH) + 1), 2, "0");

            List<String> fileArr = testSet.get(month);
            if (fileArr == null) {
                fileArr = new ArrayList<String>();
                testSet.put(month, fileArr);
            }
            fileArr.add(fileAnml);

            //move to next day
            curDate.setTime(curDate.getTime() + 24 * 60 * 60 * 1000);
        }
        return testSet;
    }
}
