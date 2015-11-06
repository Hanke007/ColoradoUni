package cdb.common.lang;

import java.util.List;
import java.util.Map;

import cdb.common.model.Cluster;
import cdb.common.model.DenseMatrix;
import cdb.common.model.Point;

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

    public static void gnuLinepoint(Map<String, List<Point>> pltContext, String workingRoot) {
        // delete files in working directory
        FileUtil.deleteDir(workingRoot);

        // fill files
        for (String key : pltContext.keySet()) {
            String fileName = workingRoot + '[' + key + ']';
            StringBuilder content = new StringBuilder();

            for (Point point : pltContext.get(key)) {
                int dimensn = point.dimension();
                for (int i = 0; i < dimensn; i++) {
                    content.append('\t').append(point.getValue(i));
                }
                content.append('\n');
            }
            FileUtil.delete(fileName);
            FileUtil.existDirAndMakeDir(fileName);
            FileUtil.write(fileName, content.toString());
        }

        // GUNPLOT script
        String plotFile = workingRoot + "z.plt";
        StringBuilder plotCon = new StringBuilder();
        plotCon.append("set term pngcairo").append("\nset output \"trend.png\"")
            .append("\nset xrange [-10: 380] \nset xtics 30").append("\n plot \\");
        for (String key : pltContext.keySet()) {
            plotCon.append("\n\"[" + key + "]\" using 1:2 title \"" + key + "\",\\");
        }
        plotCon.delete(plotCon.length() - 2, plotCon.length());

        FileUtil.write(plotFile, plotCon.toString());
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

    public static void gnuLPWithMultipleFile(double[] datas, Cluster[] resultSet,
                                             String workingRoot) {
        // delete files in working directory
        FileUtil.deleteDir(workingRoot);

        // fill files
        int fileCount = 0;
        for (Cluster cluster : resultSet) {
            String fileName = workingRoot + '[' + (fileCount++) + ']';
            StringBuilder content = new StringBuilder();
            for (Integer pIndx : cluster) {
                content.append(pIndx).append('\t').append(datas[pIndx]).append('\n');
            }

            FileUtil.delete(fileName);
            FileUtil.existDirAndMakeDir(fileName);
            FileUtil.write(fileName, content.toString());
        }

        String plotFile = workingRoot + "z.plt";
        StringBuilder plotCon = new StringBuilder();
        plotCon.append("set term pngcairo").append("\nset output \"trend.png\"")
            .append("\n plot \\");
        for (int i = 0; i < fileCount; i++) {
            plotCon.append("\n\"[" + i + "]\" using 1:2 notitle,\\");
        }
        plotCon.delete(plotCon.length() - 2, plotCon.length());

        FileUtil.write(plotFile, plotCon.toString());
    }
}
