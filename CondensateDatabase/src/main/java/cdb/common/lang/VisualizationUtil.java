package cdb.common.lang;

import cdb.dal.vo.DenseMatrix;

/**
 * 
 * @author Chao Chen
 * @version $Id: VisualizationUtil.java, v 0.1 Sep 17, 2015 10:37:31 AM chench Exp $
 */
public class VisualizationUtil {

    /**
     * forbidden construction
     */
    private VisualizationUtil() {
    }

    /**
     * The data in the array will be transformer into GNUPlot data file
     * in the type of line point
     * 
     * @param data
     * @param iniSeq        the first number of the corresponding data
     * @param fileName
     */
    public static void gnuLinepoint(double[] data, int iniSeq, String fileName) {
        FileUtil.delete(fileName);

        int dNum = iniSeq;
        StringBuilder content = new StringBuilder();
        for (double one : data) {
            content.append(dNum++).append('\t').append(one).append('\n');
        }
        FileUtil.write(fileName, content.toString());
    }

    /**
     * The data matrix will be transformed into GNUPlot data file 
     * in the type of HeatMap.
     * 
     * @param dMatrix
     * @param fileName
     */
    public static void gnuHeatmap(DenseMatrix dMatrix, String fileName) {
        int rowNum = dMatrix.getRowNum();
        int colNum = dMatrix.getColNum();

        FileUtil.delete(fileName);
        for (int row = 0; row < rowNum; row++) {
            StringBuilder content = new StringBuilder();
            for (int col = 0; col < colNum; col++) {
                content.append(row).append('\t').append(colNum - 1 - col).append('\t')
                    .append(dMatrix.getVal(row, col)).append('\n');
            }

            FileUtil.writeAsAppend(fileName, content.append('\n').toString());
        }
    }
}
