package cdb.dataset.gen.ui;

import java.awt.event.ActionEvent;
import java.util.Deque;

import cdb.dal.vo.AnomalyInfoVO;

/**
 * 
 * @author Chao Chen
 * @version $Id: DelButton.java, v 0.1 Oct 15, 2015 2:45:29 PM chench Exp $
 */
public class DelButton extends AbstractClickButtun {
    /**
     * @param label
     * @param rootDataDir
     * @param rootImageDir
     * @param anmlyArr
     */
    public DelButton(String label, String rootDataDir, String rootImageDir,
                     Deque<AnomalyInfoVO> anmlyArr) {
        super(label, rootDataDir, rootImageDir, anmlyArr);
    }

    /**  */
    private static final long serialVersionUID = 1L;

    /** 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // directly delete this record
        anmlyArr.pollFirst();

        // update GUI
        super.actionPerformed(e);
    }

}
