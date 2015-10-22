package cdb.dataset.gen.support;

import java.util.Date;
import java.util.List;

import cdb.common.lang.DateUtil;

/**
 * 
 * @author Chao Chen
 * @version $Id: SourceDataDump.java, v 0.1 Oct 22, 2015 3:26:04 PM chench Exp $
 */
public abstract class AbstractSourceDump {

    /**
     * make source files between given start date and end date 
     * 
     * @param rootDir       the root directory
     * @param sDateStr      the start date string
     * @param eDateStr      the end date string
     * @param freqId        the frequency id
     * @param tDataDump     the array of the data file string
     * @param tDateDump     the array of the date string
     */
    public abstract void collect(String rootDir, String sDateStr, String eDateStr, String freqId,
                                 List<String> tDataDump, List<String> tDateDump);

    /**
     * making source files
     * 
     * @param rootDir       the root directory
     * @param sDateStr      the start date string
     * @param eDateStr      the end date string
     * @param freqId        the frequency id
     * @param tDataDump     the array of the data file string
     * @param tDateDump     the array of the date string
     */
    protected void imgWorkingSetGen(String rootDir, Date sDate, Date eDate, String freqId,
                                    List<String> tDataDump, List<String> tDateDump) {
        Date curDate = sDate;
        while (curDate.before(eDate)) {
            String dateStr = DateUtil.format(curDate, DateUtil.SHORT_FORMAT);
            String fileAnml = binFileConvntn(rootDir, dateStr, freqId);

            tDataDump.add(fileAnml);
            tDateDump.add(dateStr);

            //move to next day
            curDate.setTime(curDate.getTime() + 24 * 60 * 60 * 1000);
        }
    }

    /**
     * the conventional way to name the binary files
     * 
     * @param rootDir   the root directory
     * @param taskId    the task identify   yyyymmdd
     * @param freqId    the frequency identify
     * @return          file name
     */
    protected abstract String binFileConvntn(String rootDir, String taskId, String freqId);
}
