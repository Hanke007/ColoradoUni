package cdb.common.lang;

import cdb.dal.vo.DenseIntMatrix;

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
     * The data matrix will be transformed into GNUPlot data file 
     * in the type of HeatMap.
     * 
     * @param dMatrix
     * @param fileName
     */
    public static void gnuHeatmap(DenseIntMatrix dMatrix, String fileName) {
        int rowNum = dMatrix.getRowNum();
        int colNum = dMatrix.getColNum();

        FileUtil.delete(fileName);
        for (int row = 0; row < rowNum; row++) {
            StringBuilder content = new StringBuilder();
            for (int col = 0; col < colNum; col++) {
                content.append(row).append('\t').append(col).append('\t')
                    .append(dMatrix.getVal(row, col)).append('\n');
            }

            FileUtil.writeAsAppend(fileName, content.append('\n').toString());
        }
    }
}
