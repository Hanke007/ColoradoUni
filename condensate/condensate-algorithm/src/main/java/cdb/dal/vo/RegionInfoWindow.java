package cdb.dal.vo;

/**
 * 
 * @author Chao Chen
 * @version $Id: RegionInfoWindow.java, v 0.1 Oct 26, 2015 1:16:05 PM chench Exp $
 */
public class RegionInfoWindow {
    /** the window of objects */
    private RegionInfoVO[][][] regnWindow;
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
     * add new item into windows
     * 
     * @param regInfoVO
     */
    public void put(RegionInfoVO[][] regInfoVO) {
        if (curIndx == winSize - 1) {
            for (int i = 1; i < winSize; i++) {
                regnWindow[i - 1] = regnWindow[i];
            }

            regnWindow[winSize - 1] = regInfoVO;
        } else {
            curIndx++;
            regnWindow[curIndx] = regInfoVO;
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
