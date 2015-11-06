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
 * @author Chao Chen
 * @version $Id: AVHRFileDtProc.java, v 0.1 Oct 20, 2015 11:33:01 AM chench Exp $
 */
public class AVHRFileDtProc implements DatasetProc {

    /** 
     * @see cdb.dal.file.DatasetProc#read(java.lang.String)
     */
    @Override
    public DenseMatrix read(String fileName) {
        // check validation
        if (StringUtil.isBlank(fileName)) {
            return null;
        }

        // initialize the dimension w.r.t pattern of file name
        int[] dimension = dimensions(fileName);

        DenseMatrix result = new DenseMatrix(dimension[0], dimension[1]);
        boolean isSuccess = readInner(fileName, result);
        return isSuccess ? result : null;
    }

    /** 
     * @see cdb.dal.file.DatasetProc#read(java.lang.String, int[], int[])
     */
    @Override
    public DenseMatrix read(String fileName, int[] rowIncluded, int[] colIncluded) {
        return null;
    }

    /**
     * read data from given file path
     * 
     * @param fileName      the file contains data
     * @param geoEntity     the Object to store data
     */
    protected boolean readInner(String fileName, DenseMatrix geoEntity) {
        DataInputStream inStream = null;
        try {
            inStream = new DataInputStream(new FileInputStream(fileName));

            int rowNum = geoEntity.getRowNum();
            int colNum = geoEntity.getColNum();
            byte[] buffer = new byte[2 * colNum];
            for (int i = 0; i < rowNum; i++) {
                //read all bytes in i-th row
                inStream.read(buffer);

                for (int j = 0; j < colNum; j++) {
                    int val = Byte2NumUtil.byte2int(buffer, j * 2, 2);
                    geoEntity.setVal(i, j, calibretion(val));
                }
            }

            return true;
        } catch (FileNotFoundException e) {
            ExceptionUtil.caught(e, fileName + " Not Found.");
        } catch (IOException e) {
            ExceptionUtil.caught(e, fileName + " IO crash.");
        } finally {
            IOUtils.closeQuietly(inStream);
        }

        return false;
    }

    /**
     * transform original data to label data
     * 
     * @param val
     * @return
     */
    private double calibretion(double val) {
        if (val == 0 | val > 3100) {
            val = Double.NaN;
        } else {
            val /= 10;
        }

        return val;
    }

    /** 
     * @see cdb.dal.file.DatasetProc#dimensions(java.lang.String)
     */
    @Override
    public int[] dimensions(String freqId) {
        int[] dimension = new int[2];
        if (freqId.indexOf("v3") != -1) {
            dimension[0] = 452;
            dimension[1] = 452;
        }
        return dimension;
    }

}
