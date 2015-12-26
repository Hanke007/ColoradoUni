package cdb.dataset.parameter;

import java.util.Map;

import org.apache.log4j.Logger;

import cdb.common.lang.log4j.LoggerDefineConstant;
import cdb.common.model.DenseMatrix;
import cdb.dal.file.DatasetProc;

/**
 * 
 * @author Chao Chen
 * @version $Id: AbstractParamCalculator.java, v 0.1 Dec 23, 2015 11:35:36 AM chench Exp $
 */
public abstract class AbstractParamCalculator {

    /** the root directory of the dataset*/
    protected String      rootDir;
    /** the number of rows in sub-regions*/
    protected int         regionHeight;
    /** the number of columns in sub-regions*/
    protected int         regionWeight;
    /** the frequency id*/
    protected String      freqId;
    /** file dataset parser */
    protected DatasetProc dProc;

    /** logger */
    protected final static Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_THREAD);

    /**
     * @param rootDir           the root directory of the dataset
     * @param regionHeight      the number of rows in sub-regions
     * @param regionWeight      the number of columns in sub-regions
     * @param freqId            the frequency id
     * @param dProc             file dataset parser
     */
    public AbstractParamCalculator(String rootDir, int regionHeight, int regionWeight,
                                   String freqId, DatasetProc dProc) {
        super();
        this.rootDir = rootDir;
        this.regionHeight = regionHeight;
        this.regionWeight = regionWeight;
        this.freqId = freqId;
        this.dProc = dProc;
    }

    /**
     * compute the parameters
     * 
     * @param meanRep       the repository of mean variables
     * @param sdRep         the repository of standard deviation variables
     */
    public abstract void calculate(Map<String, DenseMatrix> meanRep,
                                   Map<String, DenseMatrix> sdRep);

}
