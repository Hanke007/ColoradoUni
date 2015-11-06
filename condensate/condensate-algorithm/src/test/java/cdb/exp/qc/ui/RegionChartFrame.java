package cdb.exp.qc.ui;

import java.awt.Color;
import java.awt.event.WindowEvent;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;

/**
 * 
 * @author Chao Chen
 * @version $Id: RegionChartFrame.java, v 0.1 Nov 3, 2015 3:43:12 PM chench Exp $
 */
public class RegionChartFrame extends ApplicationFrame {
    /**  default values*/
    private static final long serialVersionUID = 1L;

    /**
     * @param title
     */
    public RegionChartFrame(String title, XYDataset[] datasets, String[] titles, int dayInYear) {
        super(title);
        ChartPanel chartPanel = drawImage(datasets, titles, dayInYear);
        this.setContentPane(chartPanel);
        this.pack();
    }

    /** 
     * @see org.jfree.ui.ApplicationFrame#windowClosing(java.awt.event.WindowEvent)
     */
    @Override
    public void windowClosing(WindowEvent event) {
        // ignore
    }

    protected ChartPanel drawImage(XYDataset[] datasets, String[] titles, int dayInYear) {
        CombinedDomainXYPlot parantPlot = new CombinedDomainXYPlot(new NumberAxis("Domain"));
        parantPlot.setGap(10.0);

        // make a vertical line
        ValueMarker domainMarker = new ValueMarker(dayInYear); // position is the value on the axis
        domainMarker.setPaint(Color.black);

        int dataSize = datasets.length;
        XYItemRenderer renderer = new XYLineAndShapeRenderer(); // three charts using the same render to share the same index color
        for (int dIndx = 0; dIndx < dataSize; dIndx++) {
            XYDataset data = datasets[dIndx];
            NumberAxis rangeAxis = new NumberAxis(titles[dIndx]);
            XYPlot subplot = new XYPlot(data, null, rangeAxis, renderer);
            subplot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
            subplot.addDomainMarker(domainMarker);
            parantPlot.add(subplot, 1);
        }
        parantPlot.setOrientation(PlotOrientation.VERTICAL);

        JFreeChart chart = new JFreeChart("Result Analysis", JFreeChart.DEFAULT_TITLE_FONT,
            parantPlot, true);
        return new ChartPanel(chart, true, true, true, false, true);
    }
}
