package cdb.dataset.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import cdb.common.lang.DateUtil;
import cdb.common.lang.ExceptionUtil;

/**
 * 
 * @author Chao Chen
 * @version $Id: BinFileConvntnUtil.java, v 0.1 Oct 22, 2015 4:21:37 PM chench Exp $
 */
public final class BinFileConvntnUtil {
    /** the Calendar object */
    protected final static Calendar cal = Calendar.getInstance();

    /**
     * forbidden construction
     */
    private BinFileConvntnUtil() {

    }

    /**
     * SSMI file mode
     * 
     * @param rootDir   the root directory
     * @param taskId    the task identify   yyyymmdd
     * @param freqId    the frequency identify
     * @return          file name
     */
    public static String fileSSMI(String rootDir, String taskId, String freqId) {
        int timeRange = Integer.valueOf(taskId) / 100;
        int year = timeRange / 100;
        String fileName = rootDir + year + "/";
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

    /**
     * AVHR file mode
     * 
     * @param rootDir   the root directory
     * @param taskId    the task identify   yyyymmdd
     * @param freqId    the frequency identify
     * @return          file name
     */
    public static String fileAVHR(String rootDir, String taskId, String freqId) {
        try {
            Date date = DateUtil.parse(taskId, DateUtil.SHORT_FORMAT);
            cal.setTime(date);

            int timeRange = Integer.valueOf(taskId) / 100;
            String fileName = rootDir;

            if (timeRange < 198501) {
                fileName += "a07_s005_" + timeRange + "_" + freqId + ".v2";
            } else if (timeRange < 199001) {
                fileName += "a09_s005_" + timeRange + "_" + freqId + ".v2";
            } else if (timeRange < 199501) {
                fileName += "a11_s005_" + timeRange + "_" + freqId + ".v2";
            } else {
                fileName += "a14_s005_" + timeRange + "_" + freqId + ".v2";
            }

            return fileName;
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "Date parse crashed. " + taskId);
        }

        return null;
    }
}
