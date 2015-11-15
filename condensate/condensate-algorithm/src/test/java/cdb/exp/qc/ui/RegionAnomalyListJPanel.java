package cdb.exp.qc.ui;

import java.awt.Dimension;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;

import cdb.common.lang.StatisticParamUtil;
import cdb.common.model.RegionAnomalyInfoVO;

/**
 * 
 * @author Chao Chen
 * @version $Id: RegionAnomalyListJPanel.java, v 0.1 Nov 2, 2015 1:41:35 PM chench Exp $
 */
public class RegionAnomalyListJPanel extends JPanel {
    /**  default values*/
    private static final long serialVersionUID = 1L;
    /** *the list of the anomalies*/
    private RegionJList       regnJList;
    /** scroll panels*/
    private JScrollPane       jScrollPane;
    /** the number of field making the top contributions*/
    private int               fContriNum;
    /** the labels to show*/
    private static String[]   LABELS           = { "GradCol", "GradRow", "DffMean[-1]",
                                                   "DffMean[+1]", "DffSd[-1]", "DffSd[+1]",
                                                   "Spactial: SumCorr", "Spactial: SumDff", "Mean",
                                                   "Sd" };
    //    private static String[]   LABELS           = { "GradCol", "GradRow", "DffEntropy[-1]",
    //                                                   "DffEntropy[1]", "DffMean[-1]", "DffMean[1]",
    //                                                   "DffSd[-1]", "DffSd[1]", "Spactial: SumCorr",
    //                                                   "Spactial: SumDff", "Entropy", "GradMean",
    //                                                   "Mean", "Sd" };

    /**
     * @param regnAnmlInfoArrs  the list of anomalies objects
     * @param regnInfoRootDir   the directory of region information files
     * @param fContriNum        the number of field making the top contributions
     */
    public RegionAnomalyListJPanel(List<RegionAnomalyInfoVO> regnAnmlInfoArrs,
                                   String regnInfoRootDir, int fContriNum) {
        this.fContriNum = fContriNum;

        int[] fIndices = new int[fContriNum];
        Vector<String> labelArr = new Vector<String>();
        cmpTopConstributionField(regnAnmlInfoArrs, labelArr, fIndices);

        regnJList = new RegionJList(labelArr, regnInfoRootDir, LABELS);
        ((DefaultListCellRenderer) regnJList.getCellRenderer())
            .setHorizontalAlignment(JLabel.CENTER);
        regnJList.setRegnAnmlInfoArrs(regnAnmlInfoArrs);
        regnJList.setfIndices(fIndices);

        this.setPreferredSize(new Dimension(200, 100));
        jScrollPane = new JScrollPane();
        jScrollPane.setViewportView(regnJList);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(jScrollPane);
    }

    public void updateContext(List<RegionAnomalyInfoVO> regnAnmlInfoArrs) {
        int[] fIndices = new int[fContriNum];
        Vector<String> labelArr = new Vector<String>();
        cmpTopConstributionField(regnAnmlInfoArrs, labelArr, fIndices);

        ListModel<String> jListModel = new DefaultComboBoxModel<String>(labelArr);
        regnJList.setModel(jListModel);
        regnJList.setRegnAnmlInfoArrs(regnAnmlInfoArrs);
        regnJList.setfIndices(fIndices);
    }

    protected void cmpTopConstributionField(List<RegionAnomalyInfoVO> regnAnmlInfoArrs,
                                            Vector<String> labelArr, int[] fIndices) {
        for (RegionAnomalyInfoVO regnAnmlInfo : regnAnmlInfoArrs) {
            StringBuilder labelList = new StringBuilder();

            // add position information
            labelList.append('(').append(regnAnmlInfo.getX()).append(',')
                .append(regnAnmlInfo.getY()).append("):  ");

            int fIndx = 0;
            double[] fVals = new double[LABELS.length];
            for (double dVal : regnAnmlInfo.getdPoint()) {
                if (fIndx >= LABELS.length) {
                    break;
                }

                fVals[fIndx] = dVal;
                fIndx++;
            }

            // find the top three maximum field
            for (int indx = 0; indx < fContriNum; indx++) {
                int maxIndx = StatisticParamUtil.indexOfAbsMaxNum(fVals);
                if (maxIndx == -1) {
                    break;
                }
                double dVal = fVals[maxIndx];
                fVals[maxIndx] = 0.0d;

                fIndices[indx] = maxIndx;
                labelList.append("##").append(LABELS[maxIndx])
                    .append(": " + String.format("%.3f", dVal));
            }

            labelArr.add(labelList.toString());
        }
    }
}
