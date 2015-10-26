package cdb.dataset.generator;

import java.util.List;

import cdb.service.dataset.DatasetProc;

/**
 * 
 * @author Chao Chen
 * @version $Id: AbstractDataTransformer.java, v 0.1 Oct 22, 2015 3:57:21 PM chench Exp $
 */
public abstract class AbstractDataTransformer {

    /**
     * transform data into another formation
     * 
     * @param rootDir       the root directory
     * @param freqId        the frequency id
     * @param tDataDump     the array of the data file string
     * @param tDateDump     the array of the date string
     * @param dataProc      ata parser
     */
    public abstract void transform(String rootDir, String freqId, List<String> tDataDump,
                                   List<String> tDateDump, DatasetProc dataProc);
}
