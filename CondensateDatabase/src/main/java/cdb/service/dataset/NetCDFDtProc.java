package cdb.service.dataset;

import cdb.dal.vo.DenseIntMatrix;

/**
 * 
 * See <a href="http://nsidc.org/data/docs/measures/nsidc-0533/index.html">MEaSUREs Greenland Surface Melt Daily 25km EASE-Grid 2.0 </a>
 * 
 * @author Chao Chen
 * @version $Id: NetCDFDtProc.java, v 0.1 Sep 15, 2015 9:40:32 AM chench Exp $
 */
public class NetCDFDtProc implements DatasetProc {

    /** 
     * @see cdb.service.dataset.DatasetProc#read(java.lang.String)
     */
    public DenseIntMatrix read(String fileName) {
        return null;
    }

}
