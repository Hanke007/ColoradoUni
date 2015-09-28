package cdb.common.lang;

import java.io.File;
import java.util.List;

import cdb.dal.vo.DenseMatrix;
import cdb.service.dataset.DatasetProc;

/**
 * The matrix file utility write data matrix into file,
 * where rules (gnuplot or matlab) should be fulfilled. 
 * 
 * @author Chao Chen
 * @version $Id: MatrixFileUtil.java, v 0.1 Jul 24, 2015 10:59:32 AM chench Exp $
 */
public final class MatrixFileUtil {

    /**
     * forbidden construction
     */
    private MatrixFileUtil() {

    }

    /**
     * read data following the given file regular pattern
     * 
     * @param fileRE            the regex-based file path 
     * @param seralData         the data structure to store the desired data
     * @param dProc             the data processor to parse the data
     * @param samplingParam     the sampling probability that data should be counted
     */
    public static void read(String fileRE, List<DenseMatrix> seralData, DatasetProc dProc,
                            double samplingParam) {
        File[] dFiles = FileUtil.parserFilesByPattern(fileRE);
        for (File file : dFiles) {
            if (Math.random() > samplingParam) {
                continue;
            }
            seralData.add(dProc.read(file.getAbsolutePath()));
        }
    }

    /**
     * read data within limited rows and columns in given files
     * 
     * @param filePatternSets       the regex-based file path 
     * @param seralData             the data structure to store the desired data
     * @param fileAssigmnt          the corresponding object to store file names 
     * @param dProc                 the data processor to parse the data
     * @param rowIncluded           the rows included
     * @param colIncluded           the column included
     * @param samplingParam         the sampling probability that data should be counted 
     */
    public static void read(String[] filePatternSets, List<DenseMatrix> seralData,
                            List<String> fileAssigmnt, DatasetProc dProc, int[] rowIncluded,
                            int[] colIncluded, double samplingParam) {
        for (String filePattern : filePatternSets) {
            File[] dFiles = FileUtil.parserFilesByPattern(filePattern);
            for (File file : dFiles) {
                if (Math.random() > samplingParam) {
                    continue;
                }

                seralData.add(dProc.read(file.getAbsolutePath(), rowIncluded, colIncluded));
                fileAssigmnt.add(file.getName());
            }
        }
    }

}
