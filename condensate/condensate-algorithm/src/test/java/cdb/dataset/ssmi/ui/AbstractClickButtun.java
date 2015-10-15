package cdb.dataset.ssmi.ui;

import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Deque;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import cdb.dal.vo.AnomalyInfoVO;

/**
 * 
 * @author Chao Chen
 * @version $Id: AbstractClickButtun.java, v 0.1 Oct 15, 2015 3:13:53 PM chench Exp $
 */
public abstract class AbstractClickButtun extends Button implements ActionListener {

    /**  */
    protected static final long    serialVersionUID = 1L;
    protected ImagePanel           leftImgPanel;
    protected ImagePanel           rightImgPanel;
    protected ButtonGroup          sConGroup;
    protected ButtonGroup          tConGroup;
    protected ButtonGroup          otConGroup;
    protected String               rootDataDir;
    protected String               rootImageDir;
    protected JLabel               dateStrLabel;
    protected Deque<AnomalyInfoVO> anmlyArr;

    public AbstractClickButtun(String label, String rootDataDir, String rootImageDir,
                               Deque<AnomalyInfoVO> anmlyArr) {
        super(label);
        this.rootDataDir = rootDataDir;
        this.rootImageDir = rootImageDir;
        this.anmlyArr = anmlyArr;
        this.addActionListener(this);
    }

    /** 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // update GUI
        if (anmlyArr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Congratulations! No records.");
            System.exit(0);
        }

        AnomalyInfoVO firstOne = anmlyArr.peekFirst();
        int row = (int) firstOne.getCentroid().getValue(0);
        int col = (int) firstOne.getCentroid().getValue(1);
        String oriImg = Freq2ImageFileUtil.freq2ImageFile(rootImageDir, firstOne,
            firstOne.getFreqIdDomain());
        String cmpImg = Freq2ImageFileUtil.freq2ImageFile(rootImageDir, firstOne,
            firstOne.getFreqIdTarget());

        dateStrLabel.setText(firstOne.getDateStr());
        leftImgPanel.setImage(oriImg, row, col);
        leftImgPanel.updateUI();
        rightImgPanel.setImage(cmpImg, row, col);
        rightImgPanel.updateUI();

    }

    /**
     * Getter method for property <tt>leftImgPanel</tt>.
     * 
     * @return property value of leftImgPanel
     */
    public ImagePanel getLeftImgPanel() {
        return leftImgPanel;
    }

    /**
     * Setter method for property <tt>leftImgPanel</tt>.
     * 
     * @param leftImgPanel value to be assigned to property leftImgPanel
     */
    public void setLeftImgPanel(ImagePanel leftImgPanel) {
        this.leftImgPanel = leftImgPanel;
    }

    /**
     * Getter method for property <tt>rightImgPanel</tt>.
     * 
     * @return property value of rightImgPanel
     */
    public ImagePanel getRightImgPanel() {
        return rightImgPanel;
    }

    /**
     * Setter method for property <tt>rightImgPanel</tt>.
     * 
     * @param rightImgPanel value to be assigned to property rightImgPanel
     */
    public void setRightImgPanel(ImagePanel rightImgPanel) {
        this.rightImgPanel = rightImgPanel;
    }

    /**
     * Getter method for property <tt>sConGroup</tt>.
     * 
     * @return property value of sConGroup
     */
    public ButtonGroup getsConGroup() {
        return sConGroup;
    }

    /**
     * Setter method for property <tt>sConGroup</tt>.
     * 
     * @param sConGroup value to be assigned to property sConGroup
     */
    public void setsConGroup(ButtonGroup sConGroup) {
        this.sConGroup = sConGroup;
    }

    /**
     * Getter method for property <tt>tConGroup</tt>.
     * 
     * @return property value of tConGroup
     */
    public ButtonGroup gettConGroup() {
        return tConGroup;
    }

    /**
     * Setter method for property <tt>tConGroup</tt>.
     * 
     * @param tConGroup value to be assigned to property tConGroup
     */
    public void settConGroup(ButtonGroup tConGroup) {
        this.tConGroup = tConGroup;
    }

    /**
     * Getter method for property <tt>otConGroup</tt>.
     * 
     * @return property value of otConGroup
     */
    public ButtonGroup getOtConGroup() {
        return otConGroup;
    }

    /**
     * Setter method for property <tt>otConGroup</tt>.
     * 
     * @param otConGroup value to be assigned to property otConGroup
     */
    public void setOtConGroup(ButtonGroup otConGroup) {
        this.otConGroup = otConGroup;
    }

    /**
     * Getter method for property <tt>rootDataDir</tt>.
     * 
     * @return property value of rootDataDir
     */
    public String getRootDataDir() {
        return rootDataDir;
    }

    /**
     * Setter method for property <tt>rootDataDir</tt>.
     * 
     * @param rootDataDir value to be assigned to property rootDataDir
     */
    public void setRootDataDir(String rootDataDir) {
        this.rootDataDir = rootDataDir;
    }

    /**
     * Getter method for property <tt>rootImageDir</tt>.
     * 
     * @return property value of rootImageDir
     */
    public String getRootImageDir() {
        return rootImageDir;
    }

    /**
     * Setter method for property <tt>rootImageDir</tt>.
     * 
     * @param rootImageDir value to be assigned to property rootImageDir
     */
    public void setRootImageDir(String rootImageDir) {
        this.rootImageDir = rootImageDir;
    }

    /**
     * Getter method for property <tt>dateStrLabel</tt>.
     * 
     * @return property value of dateStrLabel
     */
    public JLabel getDateStrLabel() {
        return dateStrLabel;
    }

    /**
     * Setter method for property <tt>dateStrLabel</tt>.
     * 
     * @param dateStrLabel value to be assigned to property dateStrLabel
     */
    public void setDateStrLabel(JLabel dateStrLabel) {
        this.dateStrLabel = dateStrLabel;
    }

    /**
     * Getter method for property <tt>anmlyArr</tt>.
     * 
     * @return property value of anmlyArr
     */
    public Deque<AnomalyInfoVO> getAnmlyArr() {
        return anmlyArr;
    }

    /**
     * Setter method for property <tt>anmlyArr</tt>.
     * 
     * @param anmlyArr value to be assigned to property anmlyArr
     */
    public void setAnmlyArr(Deque<AnomalyInfoVO> anmlyArr) {
        this.anmlyArr = anmlyArr;
    }

}
