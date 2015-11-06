package cdb.dataset.generator.ui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Deque;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import cdb.common.lang.FileUtil;
import cdb.common.model.AnomalyInfoVO;

/**
 * 
 * @author Chao Chen
 * @version $Id: DsGenFrame.java, v 0.1 Oct 15, 2015 1:02:38 PM chench Exp $
 */
public class DsGenFrame extends JFrame {
    /**  */
    private static final long serialVersionUID = 1L;

    private ButtonGroup sConGroup;
    //    private ButtonGroup tConGroup;
    private ButtonGroup otConGroup;

    private JLabel     dateStrLabel;
    private DelButton  delButn;
    private NextButton nextButn;
    private ImagePanel leftImgPanel;
    private ImagePanel rightImgPanel;

    private Deque<AnomalyInfoVO> anmlyArr = new LinkedList<AnomalyInfoVO>();

    public DsGenFrame(String rootDataDir, String rootImageDir) {
        super("SSMI Dataset Generator");

        parse(rootDataDir);

        initializeLayout(rootDataDir, rootImageDir);
    }

    protected void initializeLayout(String rootDataDir, String rootImageDir) {
        setLayout(new GridBagLayout());
        GridBagConstraints gridConsts = new GridBagConstraints();

        // image layer
        leftImgPanel = new ImagePanel();
        rightImgPanel = new ImagePanel();
        imagePanelLayer(gridConsts, rootImageDir);

        // ratio button layer
        sConGroup = new ButtonGroup();
        otConGroup = new ButtonGroup();
        ratioButtonLayer(gridConsts);

        // click button layer
        delButn = new DelButton("Delete", rootDataDir, rootImageDir, anmlyArr);
        delButn.setsConGroup(sConGroup);
        delButn.setOtConGroup(otConGroup);
        delButn.setLeftImgPanel(leftImgPanel);
        delButn.setRightImgPanel(rightImgPanel);
        delButn.setDateStrLabel(dateStrLabel);
        nextButn = new NextButton("Next", rootDataDir, rootImageDir, anmlyArr);
        nextButn.setsConGroup(sConGroup);
        nextButn.setOtConGroup(otConGroup);
        nextButn.setLeftImgPanel(leftImgPanel);
        nextButn.setRightImgPanel(rightImgPanel);
        nextButn.setDateStrLabel(dateStrLabel);
        clickButtonLayer(gridConsts);

        // label layer
        dataStrLabelLayer(gridConsts);

        // basic action listener
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                JOptionPane.showMessageDialog(null,
                    "Going to close?\n Sorry, No chance to regret!");
                System.exit(0);
            }
        });
    }

    protected void imagePanelLayer(GridBagConstraints gridConsts, String rootImageDir) {
        AnomalyInfoVO firstOne = anmlyArr.peekFirst();
        int row = (int) firstOne.getCentroid().getValue(0);
        int col = (int) firstOne.getCentroid().getValue(1);
        String oriImg = Freq2ImageFileUtil.freq2ImageFile(rootImageDir, firstOne,
            firstOne.getFreqIdDomain());
        String cmpImg = Freq2ImageFileUtil.freq2ImageFile(rootImageDir, firstOne,
            firstOne.getFreqIdTarget());

        dateStrLabel = new JLabel(firstOne.getDateStr());

        leftImgPanel.setImage(oriImg, row, col);
        gridConsts.fill = GridBagConstraints.HORIZONTAL;
        gridConsts.ipady = 340; // make this component tall
        gridConsts.gridwidth = 1;
        gridConsts.weightx = 1;
        gridConsts.gridx = 0;
        gridConsts.gridy = 0;
        gridConsts.insets = new Insets(10, 5, 0, 0);
        this.add(leftImgPanel, gridConsts);

        rightImgPanel.setImage(cmpImg, row, col);
        gridConsts.fill = GridBagConstraints.HORIZONTAL;
        gridConsts.ipady = 340; // make this component tall
        gridConsts.gridwidth = 1;
        gridConsts.weightx = 1;
        gridConsts.gridx = 1;
        gridConsts.gridy = 0;
        gridConsts.insets = new Insets(10, 0, 0, 0);
        this.add(rightImgPanel, gridConsts);
    }

    protected void ratioButtonLayer(GridBagConstraints gridConsts) {
        // Spatial context ration button
        JPanel sConPanl = new JPanel();
        sConPanl.setLayout(new FlowLayout());
        sConPanl.setBorder(BorderFactory.createTitledBorder("Spatial Context"));
        JRadioButton s1 = new JRadioButton("Sea");
        s1.setActionCommand("1");
        s1.setSelected(true);
        JRadioButton s2 = new JRadioButton("Ice");
        s2.setActionCommand("2");
        JRadioButton s3 = new JRadioButton("Continent");
        s3.setActionCommand("3");
        sConGroup.add(s1);
        sConGroup.add(s2);
        sConGroup.add(s3);
        sConPanl.add(s1);
        sConPanl.add(s2);
        sConPanl.add(s3);
        gridConsts.fill = GridBagConstraints.HORIZONTAL;
        gridConsts.ipady = 0;
        gridConsts.gridwidth = 1;
        gridConsts.weightx = 1;
        gridConsts.gridx = 0;
        gridConsts.gridy = 1;
        gridConsts.insets = new Insets(0, 0, 0, 0);
        this.add(sConPanl, gridConsts);

        // Temporal context ratio button
        //        JPanel tConPanl = new JPanel();
        //        tConPanl.setLayout(new GridLayout(1, 3));
        //        tConPanl.setBorder(BorderFactory.createTitledBorder("Temporal Context"));
        //        JRadioButton t1 = new JRadioButton("First");
        //        JRadioButton t2 = new JRadioButton("Continue");
        //        JRadioButton t3 = new JRadioButton("Last");
        //        ButtonGroup tConGroup = new ButtonGroup();
        //        tConGroup.add(t1);
        //        tConGroup.add(t2);
        //        tConGroup.add(t3);
        //        tConPanl.add(t1);
        //        tConPanl.add(t2);
        //        tConPanl.add(t3);
        //        gridConsts.fill = GridBagConstraints.HORIZONTAL;
        //        gridConsts.ipady = 0;
        //        gridConsts.gridwidth = 1;
        //        gridConsts.weightx = 1;
        //        gridConsts.gridx = 1;
        //        gridConsts.gridy = 1;
        //        gridConsts.insets = new Insets(0, 0, 0, 0);
        //        frame.add(tConPanl, gridConsts);

        // Output ratio Button
        JPanel otConPanl = new JPanel();
        otConPanl.setLayout(new FlowLayout());
        otConPanl.setBorder(BorderFactory.createTitledBorder("Output Variable"));
        JRadioButton ot1 = new JRadioButton("Left");
        ot1.setActionCommand("1");
        JRadioButton ot2 = new JRadioButton("Match");
        ot2.setSelected(true);
        ot2.setActionCommand("2");
        JRadioButton ot3 = new JRadioButton("Right");
        ot3.setActionCommand("3");
        otConGroup.add(ot1);
        otConGroup.add(ot2);
        otConGroup.add(ot3);
        otConPanl.add(ot1);
        otConPanl.add(ot2);
        otConPanl.add(ot3);
        gridConsts.fill = GridBagConstraints.HORIZONTAL;
        gridConsts.ipady = 0;
        gridConsts.gridwidth = 1;
        gridConsts.weightx = 1;
        gridConsts.gridx = 1;
        gridConsts.gridy = 1;
        gridConsts.insets = new Insets(0, 0, 0, 0);
        this.add(otConPanl, gridConsts);
    }

    protected void clickButtonLayer(GridBagConstraints gridConsts) {
        gridConsts.fill = GridBagConstraints.HORIZONTAL;
        gridConsts.ipady = 12;
        gridConsts.weightx = 0.5;
        gridConsts.gridwidth = 1;
        gridConsts.gridx = 0;
        gridConsts.gridy = 2;
        gridConsts.insets = new Insets(15, 0, 0, 100); // top padding
        this.add(delButn, gridConsts);

        gridConsts.fill = GridBagConstraints.HORIZONTAL;
        gridConsts.ipady = 12;
        gridConsts.weightx = 1;
        gridConsts.gridwidth = 1;
        gridConsts.gridx = 1;
        gridConsts.gridy = 2;
        gridConsts.insets = new Insets(15, 100, 0, 0); // top padding
        this.add(nextButn, gridConsts);
    }

    protected void dataStrLabelLayer(GridBagConstraints gridConsts) {
        gridConsts.fill = GridBagConstraints.HORIZONTAL;
        gridConsts.ipady = 0;
        gridConsts.weightx = 0.5;
        gridConsts.gridwidth = 2;
        gridConsts.gridx = 0;
        gridConsts.gridy = 3;
        gridConsts.insets = new Insets(0, 300, 0, 0);
        this.add(dateStrLabel, gridConsts);
    }

    protected void parse(String rootDataDir) {
        String anomlyTextFile = rootDataDir + "RAWDATA_V1";
        String[] lines = FileUtil.readLines(anomlyTextFile);
        for (String line : lines) {
            anmlyArr.add(AnomalyInfoVO.parseOf(line));
        }
    }
}
