package cdb.dataset.generator;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import cdb.common.lang.DateUtil;
import cdb.common.lang.ExceptionUtil;

/**
 * 
 * @author Chao Chen
 * @version $Id: AVHRSourceDataDump.java, v 0.1 Oct 22, 2015 3:29:42 PM chench Exp $
 */
public class AVHRSourceDumpImpl extends AbstractSourceDump {

    /** 
     * @see cdb.dataset.generator.AbstractSourceDump#collect(java.lang.String, java.lang.String, java.lang.String, java.util.List, java.util.List)
     */
    @Override
    public void collect(String rootDir, String sDateStr, String eDateStr, String freqId,
                        List<String> tDataDump, List<String> tDateDump) {
        try {
            Date sDate = DateUtil.parse(sDateStr, DateUtil.SHORT_FORMAT);
            Date eDate = DateUtil.parse(eDateStr, DateUtil.SHORT_FORMAT);
            imgWorkingSetGen(rootDir, sDate, eDate, freqId, tDataDump, tDateDump);
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "Check date format.");
        }
    }

    /**
     * @see cdb.dataset.generator.AbstractSourceDump#binFileConvntn(java.lang.String, java.lang.String, java.lang.String)
     */
    protected String binFileConvntn(String rootDir, String taskId, String freqId) {
        return BinFileConvntnUtil.fileAVHR(rootDir, taskId, freqId);
    }

}
