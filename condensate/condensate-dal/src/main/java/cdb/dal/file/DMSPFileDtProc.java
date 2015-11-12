package cdb.dal.file;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import cdb.common.lang.Byte2NumUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.StringUtil;
import cdb.common.model.DenseMatrix;

/**
 * 
 * See <a href="http://nsidc.org/data/docs/daac/nsidc0051_gsfc_seaice.gd.html">Sea Ice Concentrations from Nimbus-7 SMMR and DMSP SSM/I-SSMIS Passive Microwave Data</a>
 * 
 * @author Chao Chen
 * @version $Id: DMSPFileDtProc.java, v 0.1 Sep 14, 2015 6:33:59 PM chench Exp $
 */
public class DMSPFileDtProc implements DatasetProc {

    /** 
     * @see cdb.dal.file.DatasetProc#read(java.lang.String)
     */
    public DenseMatrix read(String fileName) {
        // check validation
        if (StringUtil.isBlank(fileName)) {
            return null;
        }

        // initialize the dimension w.r.t pattern of file name
        int[] dimension = dimensions(fileName);

        DenseMatrix result = new DenseMatrix(dimension[0], dimension[1]);
        readInner(fileName, result);
        return result;
    }

    /** 
     * @see cdb.dal.file.DatasetProc#read(java.lang.String, int[], int[])
     */
    public DenseMatrix read(String fileName, int[] rowIncluded, int[] colIncluded) {
        throw new RuntimeException("Unsupported method");
    }

    /**
     * read data from given file path
     * 
     * @param fileName      the file contains data
     * @param geoEntity     the Object to store data
     */
    protected void readInner(String fileName, DenseMatrix geoEntity) {

        DataInputStream inStream = null;
        try {
            inStream = new DataInputStream(new FileInputStream(fileName));

            // read header information
            inStream.read(new byte[300]);

            // read image information
            int rowNum = geoEntity.getRowNum();
            int colNum = geoEntity.getColNum();
            byte[] buffer = new byte[colNum];
            for (int i = 0; i < rowNum; i++) {
                //read all bytes in i-th row
                inStream.read(buffer);

                for (int j = 0; j < colNum; j++) {
                    int val = Byte2NumUtil.byte2int(buffer, j, 1);
                    geoEntity.setVal(i, j, val);
                }
            }

        } catch (FileNotFoundException e) {
            ExceptionUtil.caught(e, fileName + " Not Found.");
        } catch (IOException e) {
            ExceptionUtil.caught(e, fileName + " IO crash.");
        } finally {
            IOUtils.closeQuietly(inStream);
        }
    }

    /** 
     * @see cdb.dal.file.DatasetProc#mask(java.lang.String)
     */
    @Override
    public DenseMatrix mask(String fileName) {
        throw new RuntimeException("Unsupported method");
    }

    /** 
     * @see cdb.dal.file.DatasetProc#dimensions(java.lang.String)
     */
    @Override
    public int[] dimensions(String freqId) {
        int[] dimension = new int[2];
        if (freqId.endsWith("nrt_n.bin")) {
            dimension[0] = 448;
            dimension[1] = 304;
        } else if (freqId.endsWith("nrt_s.bin")) {
            dimension[0] = 332;
            dimension[1] = 316;
        }
        return dimension;
    }

}
