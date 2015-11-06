package cdb.common.model;

import java.util.Arrays;

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
     * set the value to given index
     * 
     * @param i     the index of the data
     */
    public void setVal(int i, int val) {
        if (i < dataDimnsn) {
            data[i] = val;
        }
    }

    /**
     * return the value given the index
     * 
     * @param i     the index of the data
     * @return      the value
     */
    public int getVal(int i) {
        return data[i];
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

    /** 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        Location b2 = (Location) obj;
        return this.x() == b2.x() && this.y() == b2.y();
    }

    /**
     * Getter method for property <tt>data</tt>.
     * 
     * @return property value of data
     */
    public int[] getData() {
        return data;
    }

    /**
     * Setter method for property <tt>data</tt>.
     * 
     * @param data value to be assigned to property data
     */
    public void setData(int[] data) {
        this.data = data;
    }

    /**
     * Getter method for property <tt>dataDimnsn</tt>.
     * 
     * @return property value of dataDimnsn
     */
    public int getDataDimnsn() {
        return dataDimnsn;
    }

    /**
     * Setter method for property <tt>dataDimnsn</tt>.
     * 
     * @param dataDimnsn value to be assigned to property dataDimnsn
     */
    public void setDataDimnsn(int dataDimnsn) {
        this.dataDimnsn = dataDimnsn;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String arrStr = Arrays.toString(data).replace(',', '#');
        return arrStr.substring(1, arrStr.length() - 1);
    }

    /**
     * similar to valueOf function of Numeric
     * 
     * @param line  the string contains object attributes
     * @return      the object
     */
    public static Location parseOf(String line) {
        String[] elems = line.split("\\#");

        int dataDimnsn = elems.length;
        Location obj = new Location(dataDimnsn);
        for (int i = 0; i < dataDimnsn; i++) {
            obj.setVal(i, Integer.valueOf(elems[i]));
        }

        return obj;
    }

}
