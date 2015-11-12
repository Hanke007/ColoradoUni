package cdb.dal.file;

import cdb.common.model.DenseMatrix;

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
    public DenseMatrix read(String fileName);

    /**
     * read the data in the given file path with specific rows and columns.
     * 
     * @param fileName      the file storing the data
     * @param rowIncluded   the given row indexes included
     * @param colIncluded   the given column indexes included
     * @return              a general GeoEntity given the file name 
     */
    public DenseMatrix read(String fileName, int[] rowIncluded, int[] colIncluded);

    /**
     * read the maskes in the given file path.
     * 
     * @param fileName  the file storing the data
     * @return          a general GeoEntity given the file name 
     */
    public DenseMatrix mask(String fileName);

    /**
     * get the dimensions given the frequency identification
     *      
     * @param freqId    frequency id
     * @return
     */
    public int[] dimensions(String freqId);
}
