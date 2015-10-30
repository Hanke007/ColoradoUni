package cdb.dal.vo;

import java.text.ParseException;

import cdb.common.lang.DateUtil;
import cdb.common.lang.ExceptionUtil;

/**
 * 
 * @author Chao Chen
 * @version $Id: RegionInfoWindow.java, v 0.1 Oct 26, 2015 1:16:05 PM chench Exp $
 */
public class RegionInfoWindow {
    /** the window of objects */
    private RegionInfoVO[][][] regnWindow;
    private String[]           dateWindow;
    /** */
    private int                curIndx;
    private int                winSize;

    /**
     *  Construction
     */
    public RegionInfoWindow(int winSize, int regnRowNum, int regnColNum) {
        super();
        this.winSize = winSize;
        regnWindow = new RegionInfoVO[winSize][regnRowNum][regnColNum];
        dateWindow = new String[winSize];
        curIndx = -1;
    }

    /**
     * get the item given the index
     * 
     * @param indx
     * @return
     */
    public RegionInfoVO[][] get(int indx) {
        return regnWindow[indx];
    }

    /**
     * add new item into this window
     * 
     * @param regInfoVO
     */
    public void put(RegionInfoVO[][] regInfoVO, String dateStr) {
        // add new items into this window
        if (curIndx == winSize - 1) {
            for (int i = 1; i < winSize; i++) {
                regnWindow[i - 1] = regnWindow[i];
                dateWindow[i - 1] = dateWindow[i];
            }

            regnWindow[winSize - 1] = regInfoVO;
            dateWindow[winSize - 1] = dateStr;
        } else {
            curIndx++;
            regnWindow[curIndx] = regInfoVO;
            dateWindow[curIndx] = dateStr;
        }

        // check whether the objects in this window are continuous across the time
        try {
            if (curIndx == 0) {
                return;
            } else if (!DateUtil.isNextDay(dateWindow[curIndx], dateWindow[curIndx - 1],
                DateUtil.SHORT_FORMAT)) {
                // the object in window is not continues
                curIndx = 0;
                regnWindow[curIndx] = regInfoVO;
                dateWindow[curIndx] = dateStr;
            }
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "Date format error.");
        }

    }

    /**
     * check whether the window is empty
     * 
     * @return true if the window is empty
     */
    public boolean isEmpty() {
        return curIndx == -1;
    }

    /**
     * check whether the window is full
     * 
     * @return true if the window is full
     */
    public boolean isFull() {
        return curIndx == winSize - 1;
    }

    /**
     * Getter method for property <tt>winSize</tt>.
     * 
     * @return property value of winSize
     */
    public int getWinSize() {
        return winSize;
    }

}
