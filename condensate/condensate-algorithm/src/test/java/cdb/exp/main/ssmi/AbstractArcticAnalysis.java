package cdb.exp.main.ssmi;

import org.apache.log4j.Logger;

import cdb.common.lang.log4j.LoggerDefineConstant;

/**
 * 
 * @author Chao Chen
 * @version $Id: AbstractArcticAnalysis.java, v 0.1 Sep 28, 2015 9:46:55 AM chench Exp $
 */
public abstract class AbstractArcticAnalysis {

    /** the root directory of the dataset*/
    protected final static String ROOT_DIR = "C:/Users/chench/Desktop/SIDS/";

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
    protected static String binFileConvntn(String taskId, String freqId) {
        int timeRange = Integer.valueOf(taskId) / 100;
        int year = timeRange / 10000;
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
}
