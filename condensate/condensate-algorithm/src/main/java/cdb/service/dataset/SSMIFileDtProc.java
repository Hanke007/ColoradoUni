package cdb.service.dataset;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import cdb.common.lang.Byte2NumUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.StringUtil;
import cdb.dal.vo.DenseMatrix;

/**
 * Dataset Processor for Special Sensor Microwave Image(r) (SSMI) data set.
 * 
 * See <a href="http://nsidc.org/data/docs/daac/nsidc0001_ssmi_tbs.gd.html">Daily Polar Gridded Brightness Temperatures Document</a>
 * 
 * @author Chao Chen
 * @version $Id: SSMIFileDtProc.java, v 0.1 Jul 22, 2015 4:00:12 PM chench Exp $
 */
public class SSMIFileDtProc implements DatasetProc {

    /** 
     * @see cdb.service.dataset.DatasetProc#read(java.lang.String)
     */
    public DenseMatrix read(String fileName) {
        // check validation
        if (StringUtil.isBlank(fileName)) {
            return null;
        }

        // initialize the dimension w.r.t pattern of file name
        int[] dimension = new int[2];
        if ((fileName.indexOf("n85") != -1) | (fileName.indexOf("n91") != -1)) {
            dimension[0] = 896;
            dimension[1] = 608;
        } else if ((fileName.indexOf("n19") != -1) | (fileName.indexOf("n22") != -1)
                   | (fileName.indexOf("n37") != -1)) {
            dimension[0] = 448;
            dimension[1] = 304;
        } else if ((fileName.indexOf("s85") != -1) | (fileName.indexOf("s91") != -1)) {
            dimension[0] = 664;
            dimension[1] = 632;
        } else if ((fileName.indexOf("s19") != -1) | (fileName.indexOf("s22") != -1)
                   | (fileName.indexOf("s37") != -1)) {
            dimension[0] = 332;
            dimension[1] = 316;
        }

        DenseMatrix result = new DenseMatrix(dimension[0], dimension[1]);
        boolean isSuccess = readInner(fileName, result);
        return isSuccess ? result : null;
    }

    /** 
     * @see cdb.service.dataset.DatasetProc#read(java.lang.String, int[], int[])
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
        if (val == 0 | val > 5000) {
            val = Double.NaN;
        } else {
            val /= 10;
        }

        return val;
    }
}
