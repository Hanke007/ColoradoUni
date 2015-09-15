package cdb.service.dataset;

import java.io.IOException;

import ucar.ma2.ArrayByte;
import ucar.ma2.ArrayFloat;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.StringUtil;
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
        // check validation
        if (StringUtil.isBlank(fileName)) {
            return null;
        }

        DenseIntMatrix result = null;
        try {
            // parse resolution
            NetcdfFile ncfile = NetcdfFile.open(fileName);
            Variable latVar = ncfile.findVariable("latitude");
            ArrayFloat.D2 latArray = (ArrayFloat.D2) latVar.read();
            int[] dimension = latArray.getShape();
            result = new DenseIntMatrix(dimension[0], dimension[1]);

            // read data
            Variable seaice = ncfile.findVariable("greenland_surface_melt");
            ArrayByte.D3 netcdfData = (ArrayByte.D3) seaice.read();
            for (int row = 0; row < dimension[0]; row++) {
                for (int col = 0; col < dimension[1]; col++) {
                    // Sea ice coverage goes from 0 to %100. Convert to digital
                    // number, 0-255
                    result.setVal(row, col, netcdfData.get(0, row, col));
                }
            }

        } catch (IOException e) {
            ExceptionUtil.caught(e, "IO Exception File: " + fileName);
        } finally {
        }

        return result;
    }
}
