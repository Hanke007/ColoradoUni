package cdb.exp.qc.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

import cdb.common.lang.DateUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.FileUtil;
import cdb.dal.vo.RegionAnomalyInfoVO;
import cdb.dal.vo.RegionInfoVO;
import cdb.ml.clustering.Point;
import cdb.ml.qc.RegionInfoVOHelper;

/**
 * 
 * @author Chao Chen
 * @version $Id: RegionJList.java, v 0.1 Nov 3, 2015 10:58:59 AM chench Exp $
 */
public class RegionJList extends JList<String> {
    /**  default values*/
    private static final long         serialVersionUID = 1L;
    /** the suggested indices*/
    private int[]                     fIndices;
    /** the list of anomalies objects*/
    private List<RegionAnomalyInfoVO> regnAnmlInfoArrs;

    public RegionJList(Vector<String> labelArr, String regnInfoRootDir, String[] labels) {
        super(labelArr);
        ((DefaultListCellRenderer) getCellRenderer()).setHorizontalAlignment(JLabel.CENTER);

        this.addMouseListener(new RegionMouseAdapter(regnInfoRootDir, labels));
    }

    /**
     * Setter method for property <tt>fIndices</tt>.
     * 
     * @param fIndices value to be assigned to property fIndices
     */
    public void setfIndices(int[] fIndices) {
        this.fIndices = fIndices;
    }

    /**
     * Setter method for property <tt>regnAnmlInfoArrs</tt>.
     * 
     * @param regnAnmlInfoArrs value to be assigned to property regnAnmlInfoArrs
     */
    public void setRegnAnmlInfoArrs(List<RegionAnomalyInfoVO> regnAnmlInfoArrs) {
        this.regnAnmlInfoArrs = regnAnmlInfoArrs;
    }

    protected class RegionMouseAdapter extends MouseAdapter {
        /**  the directory of region information files*/
        private String   regnInfoRootDir;
        /** the labels of each field*/
        private String[] labels;

        /**
         * @param regnInfoRootDir
         */
        public RegionMouseAdapter(String regnInfoRootDir, String[] labels) {
            super();
            this.regnInfoRootDir = regnInfoRootDir;
            this.labels = labels;
        }

        /** 
         * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() != 2) {
                return;
            }

            try {
                int selectedIndx = ((RegionJList) e.getSource()).getSelectedIndex();
                RegionAnomalyInfoVO one = regnAnmlInfoArrs.get(selectedIndx);
                int rRIndx = one.getX() / one.getWidth();
                int cRIndx = one.getY() / one.getHeight();
                collectXYSeries(rRIndx, cRIndx);
            } catch (ParseException e1) {
                ExceptionUtil.caught(e1, "Date format error.");
            }
        }

        private void collectXYSeries(int rRIndx, int cRIndx) throws ParseException {
            String fileName = "" + rRIndx + '_' + cRIndx;
            String[] lines = FileUtil.readLines(regnInfoRootDir + fileName);

            // group by year
            Map<Integer, XYSeries> fRep1 = new HashMap<Integer, XYSeries>();
            Map<Integer, XYSeries> fRep2 = new HashMap<Integer, XYSeries>();
            Map<Integer, XYSeries> fRep3 = new HashMap<Integer, XYSeries>();
            Calendar cal = Calendar.getInstance();
            for (String line : lines) {
                RegionInfoVO regnVO = RegionInfoVO.parseOf(line);
                Point point = RegionInfoVOHelper.make12Features(regnVO);

                Date date = DateUtil.parse(regnVO.getDateStr(), DateUtil.SHORT_FORMAT);
                cal.setTime(date);
                int year = cal.get(Calendar.YEAR);
                int daysInYear = cal.get(Calendar.DAY_OF_YEAR);

                fillRepInDaysFasion(0, daysInYear, year, point, fRep1);
                fillRepInDaysFasion(1, daysInYear, year, point, fRep2);
                fillRepInDaysFasion(2, daysInYear, year, point, fRep3);
            }

            // transform to matrix
            XYSeriesCollection fData1 = fillDataset(fRep1);
            XYSeriesCollection fData2 = fillDataset(fRep2);
            XYSeriesCollection fData3 = fillDataset(fRep3);
            XYSeriesCollection[] fDatas = { fData1, fData2, fData3 };
            String[] titles = { labels[fIndices[0]], labels[fIndices[1]], labels[fIndices[2]] };

            RegionChartFrame regnChartFrame = new RegionChartFrame("Analysis Result", fDatas,
                titles);
            RefineryUtilities.centerFrameOnScreen(regnChartFrame);
            regnChartFrame.setSize(1000, 600);
            regnChartFrame.setVisible(true);
        }

        private void fillRepInDaysFasion(int seq, int daysInYear, int year, Point point,
                                         Map<Integer, XYSeries> fRep) {
            int field = fIndices[seq];
            XYSeries fXYSer = fRep.get(year);
            if (fXYSer == null) {
                fXYSer = new XYSeries(String.valueOf(year));
                fRep.put(year, fXYSer);
            }

            fXYSer.add(daysInYear, point.getValue(field));
        }

        private XYSeriesCollection fillDataset(Map<Integer, XYSeries> fRep) {
            XYSeriesCollection fData = new XYSeriesCollection();
            for (XYSeries xySer : fRep.values()) {
                fData.addSeries(xySer);
            }
            return fData;
        }
    }
}
