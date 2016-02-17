package cdb.common.model;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Samples contains data points
 * 
 * @author Chao Chen
 * @version $Id: Sample.java, v 0.1 Aug 17, 2015 9:59:39 AM chench Exp $
 */
public class Samples implements Iterable<Point> {

    /** data points*/
    private Point[] rows;
    /** the number of the data points*/
    private int     sampleNum;
    /** the dimension of the data point*/
    private int     dataDimnsn;

    /**
     * Construction
     * 
     * @param sampleNum
     * @param dataDimnsn
     */
    public Samples(int sampleNum, int dataDimnsn) {
        super();
        this.sampleNum = sampleNum;
        this.dataDimnsn = dataDimnsn;
        this.rows = new Point[sampleNum];

        for (int i = 0; i < sampleNum; i++) {
            rows[i] = new Point(dataDimnsn);
        }
    }

    /*========================================
     * Getter/Setter
     *========================================*/
    /**
     * Retrieve a stored value from the given index.
     * 
     * @param i The row index to retrieve.
     * @param j The column index to retrieve.
     * @return The value stored at the given index.
     */
    public double getValue(int i, int j) {
        return rows[i].getValue(j);
    }

    /**
     * Set a new value at the given index.
     * 
     * @param i The row index to store new value.
     * @param j The column index to store new value.
     * @param value The value to store.
     */
    public void setValue(int i, int j, double value) {
        rows[i].setValue(j, value);
    }

    /**
     * set the point to the given index
     * 
     * @param i the index to set
     * @param p the data point
     */
    public void setPoint(int i, Point p) {
        rows[i] = p;
    }

    /**
     * Return a reference of a given point.
     * Make sure to use this method only for read-only purpose.
     * 
     * @param index The row index to retrieve.
     * @return A reference to the designated row.
     */
    public Point getPointRef(int index) {
        return rows[index];
    }

    /**
     * Return a copy of a given point.
     * 
     * @param index The row index to retrieve.
     * @return A reference to the designated row.
     */
    public Point getPoint(int index) {
        Point newPoint = new Point(dataDimnsn);

        for (int i = 0; i < dataDimnsn; i++) {
            newPoint.setValue(i, rows[index].getValue(i));
        }
        return newPoint;
    }

    /**
     * Return a copy of a given point.
     * 
     * @param index The row index to retrieve.
     * @return A reference to the designated row.
     */
    public UJMPDenseVector getPointW(int index) {
        UJMPDenseVector newPoint = new UJMPDenseVector(dataDimnsn);
        for (int i = 0; i < dataDimnsn; i++) {
            newPoint.setValue(i, rows[index].getValue(i));
        }

        return newPoint;
    }

    /*========================================
     * Properties
     *========================================*/
    /**
     * Capacity of this matrix.
     * 
     * @return An array containing the length of this matrix.
     * Index 0 contains the number of the samples, while index 1 the dimension count.
     */
    public int[] length() {
        int[] lengthArray = new int[2];

        lengthArray[0] = this.sampleNum;
        lengthArray[1] = this.dataDimnsn;

        return lengthArray;
    }

    /**
     * Sum of every element. It ignores non-existing values.
     * 
     * @return The sum of all elements.
     */
    public double sum() {
        double sum = 0.0;

        for (int i = 0; i < this.sampleNum; i++) {
            Point v = this.getPointRef(i);
            sum += v.sum();
        }

        return sum;
    }

    /**
     * Average of every element. It ignores non-existing values.
     * 
     * @return The average value.
     */
    public double average() {
        return this.sum() / (sampleNum * dataDimnsn);
    }

    /** 
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<Point> iterator() {
        return Arrays.asList(rows).iterator();
    }
}
