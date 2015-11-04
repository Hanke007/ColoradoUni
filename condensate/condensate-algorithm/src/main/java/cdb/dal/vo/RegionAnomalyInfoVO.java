package cdb.dal.vo;

import cdb.common.lang.StringUtil;
import cdb.ml.clustering.Point;

/**
 * 
 * @author Chao Chen
 * @version $Id: RegionAnomalyInfoVO.java, v 0.1 Oct 27, 2015 10:56:58 AM chench Exp $
 */
public class RegionAnomalyInfoVO {

    /**  the x coordinate of the rectangle region*/
    private int    x;
    /**  the y coordinate of the rectangle region*/
    private int    y;
    /** the width of the rectangle region*/
    private int    width;
    /** the height of the rectangle region*/
    private int    height;
    /** the string of the date*/
    private String dateStr;
    /** the data computed*/
    private Point  dPoint;

    /**
     * Getter method for property <tt>dateStr</tt>.
     * 
     * @return property value of dateStr
     */
    public String getDateStr() {
        return dateStr;
    }

    /**
     * Setter method for property <tt>dateStr</tt>.
     * 
     * @param dateStr value to be assigned to property dateStr
     */
    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
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
     * Getter method for property <tt>width</tt>.
     * 
     * @return property value of width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Setter method for property <tt>width</tt>.
     * 
     * @param width value to be assigned to property width
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Getter method for property <tt>height</tt>.
     * 
     * @return property value of height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Setter method for property <tt>height</tt>.
     * 
     * @param height value to be assigned to property height
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Getter method for property <tt>dPoint</tt>.
     * 
     * @return property value of dPoint
     */
    public Point getdPoint() {
        return dPoint;
    }

    /**
     * Setter method for property <tt>dPoint</tt>.
     * 
     * @param dPoint value to be assigned to property dPoint
     */
    public void setdPoint(Point dPoint) {
        this.dPoint = dPoint;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return x + "," + y + "," + width + "," + height + "," + dateStr + "," + dPoint.toString();
    }

    /**
     * similar to valueOf function of Numeric
     * 
     * @param line  the string contains object attributes
     * @return      the object
     */
    public static RegionAnomalyInfoVO parseOf(String line) {
        if (StringUtil.isBlank(line)) {
            return null;
        }

        String[] elems = line.split("\\,");
        RegionAnomalyInfoVO regnAnamlVO = new RegionAnomalyInfoVO();
        regnAnamlVO.setX(Integer.valueOf(elems[0].trim()));
        regnAnamlVO.setY(Integer.valueOf(elems[1].trim()));
        regnAnamlVO.setWidth(Integer.valueOf(elems[2].trim()));
        regnAnamlVO.setHeight(Integer.valueOf(elems[3].trim()));
        regnAnamlVO.setDateStr(elems[4].trim());
        regnAnamlVO.setdPoint(Point.parseOf(elems[5].trim()));
        return regnAnamlVO;
    }

}
