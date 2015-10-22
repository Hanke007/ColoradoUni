package cdb.dataset.gen.ui;

import java.awt.event.ActionEvent;
import java.util.Deque;

import cdb.common.lang.FileUtil;
import cdb.dal.vo.AnomalyInfoVO;

/**
 * 
 * @author Chao Chen
 * @version $Id: NextButton.java, v 0.1 Oct 15, 2015 1:47:28 PM chench Exp $
 */
public class NextButton extends AbstractClickButtun {

    /**  */
    private static final long serialVersionUID = 1L;

    /**
     * @param label
     * @param rootDataDir
     * @param rootImageDir
     * @param anmlyArr
     */
    public NextButton(String label, String rootDataDir, String rootImageDir,
                      Deque<AnomalyInfoVO> anmlyArr) {
        super(label, rootDataDir, rootImageDir, anmlyArr);
    }

    /** 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String sConText = sConGroup.getSelection().getActionCommand();
        int spatialConLabel = Integer.valueOf(sConText);

        String rConText = otConGroup.getSelection().getActionCommand();
        int resultLabel = Integer.valueOf(rConText);

        AnomalyInfoVO one = anmlyArr.pollFirst();
        one.setSpatialConLabel(spatialConLabel);
        one.setResultLabel(resultLabel);
        append2File(one);

        // update GUI
        super.actionPerformed(e);
    }

    private void append2File(AnomalyInfoVO one) {
        String anomlyTextFile = rootDataDir + "RAWDATA_V2";
        FileUtil.writeAsAppend(anomlyTextFile, one.toString() + "\n");
    }

}
