package cdb.exp.qc.ui;

import java.awt.Button;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;

import cdb.common.model.RegionAnomalyInfoVO;

/**
 * 
 * @author Chao Chen
 * @version $Id: ControlButton.java, v 0.1 Nov 2, 2015 3:48:45 PM chench Exp $
 */
public class ControlButton extends Button implements ActionListener {
    /**  default values*/
    private static final long                      serialVersionUID = 1L;
    /** image handler*/
    private RegionImageJPanel                      imgPanel;
    /** list panel handler*/
    private RegionAnomalyListJPanel                anmlPanel;
    /** the label to show the date informatoin*/
    private JLabel                                 dateStrLabel;
    /** the step once click*/
    private int                                    stepWide;
    /** the key set of the anomaly repository*/
    private List<String>                           keys;
    /** the map contains the region anomaly objects*/
    private Map<String, List<RegionAnomalyInfoVO>> regnAnmlRep;
    /** the root directory of image repository*/
    private String                                 imgRootDir;
    /** the frequency id*/
    private String                                 freqId;
    /** the current index of images*/
    private static int                             curImgIndx       = 0;

    /**
     * @param imgPanel
     * @param anmlPanel
     * @param stepWide
     * @param keys
     * @param regnAnmlRep
     * @param imgRootDir
     * @param freqId
     * @throws HeadlessException
     */
    public ControlButton(String label, RegionImageJPanel imgPanel,
                         RegionAnomalyListJPanel anmlPanel, JLabel dateStrLabel, int stepWide,
                         List<String> keys, Map<String, List<RegionAnomalyInfoVO>> regnAnmlRep,
                         String imgRootDir, String freqId) throws HeadlessException {
        super(label);
        this.imgPanel = imgPanel;
        this.anmlPanel = anmlPanel;
        this.dateStrLabel = dateStrLabel;
        this.stepWide = stepWide;
        this.keys = keys;
        this.regnAnmlRep = regnAnmlRep;
        this.imgRootDir = imgRootDir;
        this.freqId = freqId;

        this.addActionListener(this);
    }

    /** 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        int nextImgIndx = curImgIndx + stepWide;
        if (nextImgIndx < 0 | nextImgIndx > keys.size() - 1) {
            return;
        } else {
            curImgIndx = nextImgIndx;
        }

        String dateStr = keys.get(curImgIndx);
        List<RegionAnomalyInfoVO> regnAnmlInfoArrs = regnAnmlRep.get(dateStr);

        // update label
        dateStrLabel.setText(dateStr);

        // update image panel
        String imgFile = imgRootDir + dateStr + '_' + freqId + ".bmp";
        imgPanel.updateImage(imgFile, regnAnmlInfoArrs);
        imgPanel.updateUI();

        // update list panel
        anmlPanel.updateContext(regnAnmlInfoArrs);
        anmlPanel.updateUI();
    }

}
