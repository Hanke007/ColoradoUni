package cdb.exp.qc.analysis;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import cdb.common.lang.FileUtil;
import cdb.dal.vo.RegionAnomalyInfoVO;
import cdb.ml.clustering.Point;

/**
 * 
 * @author Chao Chen
 * @version $Id: RegionMatlabPartner.java, v 0.1 Oct 30, 2015 3:21:02 PM chench Exp $
 */
public class RegionMatlabPartner {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        //        case1();

        final XYSeries series1 = new XYSeries("First");
        series1.add(1.0, 1.0);
        series1.add(2.0, 4.0);
        series1.add(3.0, 3.0);
        series1.add(4.0, 5.0);
        series1.add(5.0, 5.0);
        series1.add(6.0, 7.0);
        series1.add(7.0, 7.0);
        series1.add(8.0, 8.0);

        final XYSeries series2 = new XYSeries("Second");
        series2.add(1.0, 5.0);
        series2.add(2.0, 7.0);
        series2.add(3.0, 6.0);
        series2.add(4.0, 8.0);
        series2.add(5.0, 4.0);
        series2.add(6.0, 4.0);
        series2.add(7.0, 2.0);
        series2.add(8.0, 1.0);

        final XYSeries series3 = new XYSeries("Third");
        series3.add(3.0, 4.0);
        series3.add(4.0, 3.0);
        series3.add(5.0, 2.0);
        series3.add(6.0, 3.0);
        series3.add(7.0, 6.0);
        series3.add(8.0, 3.0);
        series3.add(9.0, 4.0);
        series3.add(10.0, 3.0);

        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        dataset.addSeries(series3);

        // create the chart...
        final JFreeChart chart = ChartFactory.createXYLineChart("Line Chart Demo 6", // chart title
            "X", // x axis label
            "Y", // y axis label
            dataset, // data
            PlotOrientation.VERTICAL, true, // include legend
            true, // tooltips
            false // urls
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint(Color.white);

        //        final StandardLegend legend = (StandardLegend) chart.getLegend();
        //      legend.setDisplaySeriesShapes(true);

        // get a reference to the plot for further customisation...
        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
        //    plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShapesVisible(1, false);
        plot.setRenderer(renderer);

        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        // OPTIONAL CUSTOMISATION COMPLETED.

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        ApplicationFrame demo = new ApplicationFrame("Test");
        demo.setContentPane(chartPanel);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }

    protected static void case1() {
        // thread setting
        int regionHeight = 8;
        int regionWeight = 8;

        // read objects
        String rootDir = "C:/Users/chench/Desktop/SIDS/SSMI/";
        String freqId = "n19v";

        RegionAnomalyInfoVO regAnmVO = new RegionAnomalyInfoVO();
        regAnmVO.setX(312 / 8);
        regAnmVO.setY(152 / 8);
        regAnmVO.setDateStr("20120712");
        analSpecificRegnAndField(regionHeight, regionWeight, rootDir, freqId, regAnmVO);
    }

    protected static void analSpecificRegnAndField(int regionHeight, int regionWeight,
                                                   String rootDir, String freqId,
                                                   RegionAnomalyInfoVO regAnmVO) {
        String regnLoc = "" + regAnmVO.getX() + '_' + regAnmVO.getY();
        String[] lines = FileUtil
            .readLines(rootDir + "ClassificationDataset/" + freqId + '_' + regionHeight + '_'
                       + regionWeight + "_New/" + regnLoc);

        int dateVal = Integer.valueOf(regAnmVO.getDateStr());
        Point point = null;
        for (String line : lines) {
            point = Point.parseOf(line);
            if (point.getValue(16) == dateVal) {
                break;
            }
        }
        System.out.println(regnLoc + "\n" + point.toString() + "\n\n\n");

        int fIndx = 1;
        StringBuilder strBuild = new StringBuilder();
        for (Double val : point) {
            if (fIndx >= 15) {
                break;
            } else if (Math.abs(val) >= 3.0d) {
                strBuild.append(fIndx).append('\t').append(val).append('\n');
            }

            fIndx++;
        }

        System.out.println(strBuild);
    }
}
