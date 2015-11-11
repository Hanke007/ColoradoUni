package cdb.web.bean;

/**
 * 
 * @author Chao Chen
 * @version $Id: GeoLocation.java, v 0.1 Sep 30, 2015 10:31:59 AM chench Exp $
 */
public class Location2D {
    /** longitude of some position*/
    private int row;
    /** latitude of some position*/
    private int column;

    /**
     * construction
     */
    public Location2D() {
        super();
    }

    /**
     * construction
     * 
     * @param longitude
     * @param latitude
     */
    public Location2D(int row, int column) {
        super();
        this.row = row;
        this.column = column;
    }

    /**
     * Getter method for property <tt>row</tt>.
     * 
     * @return property value of row
     */
    public int getRow() {
        return row;
    }

    /**
     * Setter method for property <tt>row</tt>.
     * 
     * @param row value to be assigned to property row
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * Getter method for property <tt>column</tt>.
     * 
     * @return property value of column
     */
    public int getColumn() {
        return column;
    }

    /**
     * Setter method for property <tt>column</tt>.
     * 
     * @param column value to be assigned to property column
     */
    public void setColumn(int column) {
        this.column = column;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Location2D [row=" + row + ", column=" + column + "]";
    }

}
