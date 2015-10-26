package cdb.dataset.generator;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.StopWatch;

import cdb.common.lang.LoggerUtil;
import cdb.common.lang.log4j.LoggerDefineConstant;
import cdb.service.dataset.DatasetProc;

/**
 * 
 * @author Chao Chen
 * @version $Id: RemoteSensingGen.java, v 0.1 Oct 22, 2015 3:19:55 PM chench Exp $
 */
public class RemoteSensingGen {
    /** the root directory of the dataset*/
    private String rootDir;
    /** the start date string*/
    private String sDateStr;
    /** the end date string*/
    private String eDateStr;
    /** the frequency id*/
    private String freqId;

    /** source data dump maker*/
    private AbstractSourceDump      sourceDumper;
    /** data parser*/
    private DatasetProc             dataProc;
    /** data transformer*/
    private AbstractDataTransformer dataTransformer;

    /** logger */
    protected final static Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /**
     * @param rootDir       the root directory of the dataset
     * @param sDateStr      the start date string
     * @param eDateStr      the end date string
     * @param freqId        the frequency id
     */
    public RemoteSensingGen(String rootDir, String sDateStr, String eDateStr, String freqId) {
        super();
        this.rootDir = rootDir;
        this.sDateStr = sDateStr;
        this.eDateStr = eDateStr;
        this.freqId = freqId;
    }

    public void run() {
        // making source files
        LoggerUtil.info(logger, "1. making source files.");
        List<String> sourceFiles = new ArrayList<String>();
        List<String> timeStrs = new ArrayList<String>();
        sourceDumper.collect(rootDir, sDateStr, eDateStr, freqId, sourceFiles, timeStrs);

        // transforming source data
        LoggerUtil.info(logger, "2. tranforming source data.");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        dataTransformer.transform(rootDir, freqId, sourceFiles, timeStrs, dataProc);
        stopWatch.stop();
        LoggerUtil.info(logger, "OVERALL TIME SPENDED: " + stopWatch.getTotalTimeMillis() / 1000.0);
    }

    /**
     * Setter method for property <tt>sourceDumper</tt>.
     * 
     * @param sourceDumper value to be assigned to property sourceDumper
     */
    public void setSourceDumper(AbstractSourceDump sourceDumper) {
        this.sourceDumper = sourceDumper;
    }

    /**
     * Setter method for property <tt>dataProc</tt>.
     * 
     * @param dataProc value to be assigned to property dataProc
     */
    public void setDataProc(DatasetProc dataProc) {
        this.dataProc = dataProc;
    }

    /**
     * Setter method for property <tt>dataTransformer</tt>.
     * 
     * @param dataTransformer value to be assigned to property dataTransformer
     */
    public void setDataTransformer(AbstractDataTransformer dataTransformer) {
        this.dataTransformer = dataTransformer;
    }

}
