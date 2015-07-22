package cdb.service.dataset;

import java.io.DataInputStream;
import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;

import cdb.common.lang.Byte2NumUtil;
import cdb.common.lang.StringUtil;
import cdb.dal.vo.GeoEntity;

/**
 * Processor for SSMI data set.
 * 
 * @author chench
 * @version $Id: SSMIFileDtProc.java, v 0.1 Jul 22, 2015 4:00:12 PM chench Exp $
 */
public class SSMIFileDtProc implements DatasetProc {

    /** 
     * @see cdb.service.dataset.DatasetProc#read(java.lang.String)
     */
    public GeoEntity read(String fileName) {
        // check validation
        if (StringUtil.isBlank(fileName)) {
            return null;
        }

        // initialize the dimension w.r.t pattern of file name
        int[] dimension = new int[2];
        if ((fileName.indexOf("n85") != -1) | (fileName.indexOf("n91") != -1)) {
            dimension[0] = 896;
            dimension[1] = 608;
        } else if (fileName.indexOf('n') != -1) {
            dimension[0] = 448;
            dimension[1] = 304;
        } else if ((fileName.indexOf("s85") != -1) | (fileName.indexOf("s91") != -1)) {
            dimension[0] = 664;
            dimension[1] = 632;
        } else if (fileName.indexOf('s') != -1) {
            dimension[0] = 332;
            dimension[1] = 316;
        }

        GeoEntity result = new GeoEntity(dimension[0], dimension[1]);
        readInner(fileName, result);
        return result;
    }

    /**
     * read data from given file path
     * 
     * @param fileName      the file contains data
     * @param geoEntity     the Object to store data
     */
    protected void readInner(String fileName, GeoEntity geoEntity) {
        DataInputStream inStream = null;
        try {
            inStream = new DataInputStream(new FileInputStream(fileName));

            int rowNum = geoEntity.getRowNum();
            int colNum = geoEntity.getColNum();
            byte[] buffer = new byte[2];
            for (int i = 0; i < rowNum; i++) {
                for (int j = 0; j < colNum; j++) {
                    inStream.read(buffer);
                    double val = Byte2NumUtil.byte2int(buffer) / 10.0;
                    geoEntity.setVal(i, j, val);
                }
            }

        } catch (Exception e) {

        } finally {
            IOUtils.closeQuietly(inStream);
        }
    }
}
