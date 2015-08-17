package cdb.service.dataset;

import cdb.dal.vo.DenseIntMatrix;

/**
 * Processor Interface to cope with the operations for different data sets.
 * 
 * @author Chao Chen
 * @version $Id: DatasetProc.java, v 0.1 Jul 22, 2015 3:43:35 PM chench Exp $
 */
public interface DatasetProc {

    /**
     * read the data in the given file path.
     * 
     * @param fileName  the file storing the data
     * @return          a general GeoEntity given the file name 
     */
    public DenseIntMatrix read(String fileName);
}
