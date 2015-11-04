package cdb.exp.qc.ui;

import java.awt.event.WindowEvent;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
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
    public RegionChartFrame(String title, XYDataset[] datasets, String[] titles) {
        super(title);
        ChartPanel chartPanel = drawImage(datasets, titles);
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

    protected ChartPanel drawImage(XYDataset[] datasets, String[] titles) {
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new NumberAxis("Domain"));
        plot.setGap(10.0);

        int dataSize = datasets.length;
        for (int dIndx = 0; dIndx < dataSize; dIndx++) {
            XYDataset data = datasets[dIndx];
            XYItemRenderer renderer = new StandardXYItemRenderer();
            NumberAxis rangeAxis = new NumberAxis(titles[dIndx]);
            XYPlot subplot = new XYPlot(data, null, rangeAxis, renderer);
            subplot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
            plot.add(subplot, 1);
        }
        plot.setOrientation(PlotOrientation.VERTICAL);

        JFreeChart chart = new JFreeChart("Result Analysis", JFreeChart.DEFAULT_TITLE_FONT, plot,
            true);
        return new ChartPanel(chart, true, true, true, false, true);
    }
}
