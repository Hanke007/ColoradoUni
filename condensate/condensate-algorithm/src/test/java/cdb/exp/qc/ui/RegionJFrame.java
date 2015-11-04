package cdb.exp.qc.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;

import cdb.common.lang.FileUtil;
import cdb.dal.vo.RegionAnomalyInfoVO;

/**
 * 
 * @author Chao Chen
 * @version $Id: RegionJFrame.java, v 0.1 Nov 2, 2015 1:22:57 PM chench Exp $
 */
public class RegionJFrame extends JFrame {
    /**  default values*/
    private static final long       serialVersionUID = 1L;
    /** image handler*/
    private RegionImageJPanel       imgPanel;
    /** list panel handler*/
    private RegionAnomalyListJPanel anmlPanel;
    /** the label to show the date of current image*/
    private JLabel                  dateStrLabel;
    /** the button handler to see the next one*/
    private ControlButton           nextButton;
    /** the button handler to see the previous one*/
    private ControlButton           prevButton;

    /**
     * @param imgRootDir        the directory of region image files
     * @param regnInfoRootDir   the directory of region information files
     * @param regnAnmInfoFile   the file contains anomaly regions
     * @param freqId            the frequency identification
     * @param fContriNum        the number of field making the top contributions
     */
    public RegionJFrame(String imgRootDir, String regnInfoRootDir, String regnAnmInfoFile,
                        String freqId, int fContriNum) {
        Map<String, List<RegionAnomalyInfoVO>> regnAnmlRep = parseAnomalyInfoVO(regnAnmInfoFile);
        List<String> keys = new ArrayList<String>(regnAnmlRep.keySet());
        Collections.sort(keys);

        initializeLayout(regnAnmlRep, keys, imgRootDir, regnInfoRootDir, regnAnmInfoFile, freqId,
            fContriNum);

        // basic action listener
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    protected Map<String, List<RegionAnomalyInfoVO>> parseAnomalyInfoVO(String regnAnmInfoFile) {
        Map<String, List<RegionAnomalyInfoVO>> regnAnmlRep = new HashMap<String, List<RegionAnomalyInfoVO>>();
        String[] lines = FileUtil.readLines(regnAnmInfoFile);
        for (String line : lines) {
            RegionAnomalyInfoVO one = RegionAnomalyInfoVO.parseOf(line);

            String dateStr = one.getDateStr();
            List<RegionAnomalyInfoVO> anmArr = regnAnmlRep.get(dateStr);
            if (anmArr == null) {
                anmArr = new ArrayList<RegionAnomalyInfoVO>();
                regnAnmlRep.put(dateStr, anmArr);
            }
            anmArr.add(one);
        }
        return regnAnmlRep;
    }

    protected void initializeLayout(Map<String, List<RegionAnomalyInfoVO>> regnAnmlRep,
                                    List<String> keys, String imgRootDir, String regnInfoRootDir,
                                    String regnAnmInfoFile, String freqId, int fContriNum) {
        setLayout(new GridBagLayout());
        GridBagConstraints gridConsts = new GridBagConstraints();

        String dateStr = keys.get(0);
        List<RegionAnomalyInfoVO> regnAnmlInfoArrs = regnAnmlRep.get(dateStr);

        // label layer
        dateStrLabel = new JLabel(dateStr);
        labelLayer(gridConsts);

        // image layer
        String imgFile = imgRootDir + dateStr + '_' + freqId + ".bmp";
        imgPanel = new RegionImageJPanel(imgFile, regnAnmlInfoArrs);
        imagePanelLayer(gridConsts);

        // list layer
        anmlPanel = new RegionAnomalyListJPanel(regnAnmlInfoArrs, regnInfoRootDir, fContriNum);
        listPanelLayer(gridConsts);

        // button layer
        prevButton = new ControlButton("Prev", imgPanel, anmlPanel, dateStrLabel, -1, keys,
            regnAnmlRep, imgRootDir, freqId);
        nextButton = new ControlButton("Next", imgPanel, anmlPanel, dateStrLabel, +1, keys,
            regnAnmlRep, imgRootDir, freqId);
        buttonLayer(gridConsts);
    }

    protected void labelLayer(GridBagConstraints gridConsts) {
        gridConsts.fill = GridBagConstraints.BOTH;
        gridConsts.ipady = 0; // make this component tall
        gridConsts.gridwidth = 2;
        gridConsts.weightx = 1;
        gridConsts.gridx = 0;
        gridConsts.gridy = 0;
        gridConsts.insets = new Insets(0, 95, 0, 0);
        dateStrLabel.setHorizontalAlignment(JLabel.CENTER);
        this.add(dateStrLabel, gridConsts);
    }

    protected void imagePanelLayer(GridBagConstraints gridConsts) {
        gridConsts.fill = GridBagConstraints.BOTH;
        gridConsts.ipady = 400; // make this component tall
        gridConsts.gridwidth = 2;
        gridConsts.weightx = 1;
        gridConsts.gridx = 0;
        gridConsts.gridy = 1;
        gridConsts.insets = new Insets(0, 95, 0, 0);
        this.add(imgPanel, gridConsts);
    }

    protected void listPanelLayer(GridBagConstraints gridConsts) {
        gridConsts.fill = GridBagConstraints.HORIZONTAL;
        gridConsts.ipady = 50;
        gridConsts.gridwidth = 2;
        gridConsts.weightx = 1;
        gridConsts.gridx = 0;
        gridConsts.gridy = 2;
        gridConsts.insets = new Insets(0, 0, 0, 0);
        this.add(anmlPanel, gridConsts);
    }

    protected void buttonLayer(GridBagConstraints gridConsts) {
        gridConsts.fill = GridBagConstraints.HORIZONTAL;
        gridConsts.ipady = 12;
        gridConsts.weightx = 0.5;
        gridConsts.gridwidth = 1;
        gridConsts.gridx = 0;
        gridConsts.gridy = 3;
        gridConsts.insets = new Insets(15, 0, 0, 40); // top padding
        this.add(prevButton, gridConsts);

        gridConsts.fill = GridBagConstraints.HORIZONTAL;
        gridConsts.ipady = 12;
        gridConsts.weightx = 0.5;
        gridConsts.gridwidth = 1;
        gridConsts.gridx = 1;
        gridConsts.gridy = 3;
        gridConsts.insets = new Insets(15, 40, 0, 0); // top padding
        this.add(nextButton, gridConsts);
    }
}
