package cdb.common.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cdb.common.lang.DateUtil;

/**
 * 
 * @author Chao Chen
 * @version $Id: RegionRankInfoVO.java, v 0.1 Nov 18, 2015 4:15:10 PM chench Exp $
 */
public class RegionRankInfoVO {

    /**  the x coordinate of the rectangle region*/
    private int        x;
    /**  the y coordinate of the rectangle region*/
    private int        y;
    /** */
    private Date       dateBegin;
    /** */
    private Date       dataEnd;
    /** */
    private List<Date> days;

    /**
     * construction
     */
    public RegionRankInfoVO() {
        super();
        days = new ArrayList<Date>();
    }

    /**
     * Getter method for property <tt>x</tt>.
     * 
     * @return property value of x
     */
    public int getX() {
        return x;
    }

    /**
     * Setter method for property <tt>x</tt>.
     * 
     * @param x value to be assigned to property x
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Getter method for property <tt>y</tt>.
     * 
     * @return property value of y
     */
    public int getY() {
        return y;
    }

    /**
     * Setter method for property <tt>y</tt>.
     * 
     * @param y value to be assigned to property y
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Getter method for property <tt>dateBegin</tt>.
     * 
     * @return property value of dateBegin
     */
    public Date getDateBegin() {
        return dateBegin;
    }

    /**
     * Setter method for property <tt>dateBegin</tt>.
     * 
     * @param dateBegin value to be assigned to property dateBegin
     */
    public void setDateBegin(Date dateBegin) {
        this.dateBegin = dateBegin;
    }

    /**
     * Getter method for property <tt>dataEnd</tt>.
     * 
     * @return property value of dataEnd
     */
    public Date getDataEnd() {
        return dataEnd;
    }

    /**
     * Setter method for property <tt>dataEnd</tt>.
     * 
     * @param dataEnd value to be assigned to property dataEnd
     */
    public void setDataEnd(Date dataEnd) {
        this.dataEnd = dataEnd;
    }

    /**
     * Getter method for property <tt>days</tt>.
     * 
     * @return property value of days
     */
    public List<Date> getDays() {
        return days;
    }

    /**
     * Setter method for property <tt>days</tt>.
     * 
     * @param days value to be assigned to property days
     */
    public void setDays(List<Date> days) {
        this.days = days;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + "), During ["
               + DateUtil.format(dateBegin, DateUtil.SHORT_FORMAT) + ", "
               + DateUtil.format(dataEnd, DateUtil.SHORT_FORMAT) + "]";
    }
}
