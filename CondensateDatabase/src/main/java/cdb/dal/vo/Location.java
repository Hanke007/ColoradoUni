package cdb.dal.vo;

/**
 * 
 * @author Chao Chen
 * @version $Id: Location.java, v 0.1 Sep 15, 2015 4:15:47 PM chench Exp $
 */
public class Location {

    /** data */
    private int[] data;
    /** length of the data*/
    private int   dataDimnsn;

    /**
     * @param the dimension of the location
     */
    public Location(int dimension) {
        this.dataDimnsn = dimension;
        data = new int[dimension];
    }

    /**
     * @param x     index in x-axis
     * @param y     index in y-axis
     */
    public Location(int x, int y) {
        this.dataDimnsn = 2;
        data = new int[2];
        data[0] = x;
        data[1] = y;
    }

    /**
     * return the index in x-axis
     * 
     * @return  the index in x-axis
     */
    public int x() {
        return data[0];
    }

    /**
     * return the index in y-axis
     * 
     * @return the index in y-axis
     */
    public int y() {
        return data[1];
    }

    /**
     * return the detailed indexes
     * 
     * @return  the detailed indexes
     */
    public int[] loc() {
        return data;
    }

    /**
     * Return the length of the vector
     * 
     * @return
     */
    public int dimension() {
        return dataDimnsn;
    }
}
