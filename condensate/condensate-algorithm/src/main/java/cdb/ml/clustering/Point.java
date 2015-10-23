package cdb.ml.clustering;

import java.util.Arrays;

import cdb.common.lang.StringUtil;

/**
 * The point describe a single observation, and it will be used in clustering methods.
 * 
 * @author Chao Chen
 * @version $Id: Point.java, v 0.1 Aug 17, 2015 9:53:53 AM chench Exp $
 */
public class Point {

    /** data */
    private double[] data;
    /** length of the data*/
    private int      dataDimnsn;

    /**
     * Construction
     * 
     * @param dimention
     */
    public Point(int dimention) {
        super();
        this.dataDimnsn = dimention;
        data = new double[dimention];
    }

    public Point(double... args) {
        super();
        this.dataDimnsn = args.length;
        data = args;
    }

    /*========================================
     * Getter/Setter
     *========================================*/
    /**
     * Get the value given the specified position
     * 
     * @param i     the position index
     * @return      the value in the corresponding index
     */
    public double getValue(int i) {
        return data[i];
    }

    /**
     * Set the value given the specified position
     * 
     * @param i     the position index
     * @param val   the value to set
     */
    public void setValue(int i, double val) {
        data[i] = val;
    }

    /**
     * Return the length of the vector
     * 
     * @return
     */
    public int dimension() {
        return dataDimnsn;
    }

    /*========================================
     * Unary Vector operations
     *========================================*/
    /**
     * Scalar multiplication operator.
     * 
     * @param alpha The scalar value to be multiplied to the original vector.
     * @return The resulting vector, multiplied by alpha.
     */
    public Point scale(double alpha) {
        Point a = this;
        Point c = new Point(dataDimnsn);

        for (int i = 0; i < dataDimnsn; i++) {
            c.setValue(i, (alpha * a.getValue(i)));
        }

        return c;
    }

    /*========================================
     * Binary Vector operations
     *========================================*/
    /**
     * Vector sum (a + b)
     * 
     * @param b The vector to be added to this vector.
     * @return The resulting vector after summation.
     */
    public Point plus(Point b) {
        Point a = this;
        if (a.dataDimnsn != b.dataDimnsn)
            throw new RuntimeException("Vector lengths disagree");

        Point c = new Point(dataDimnsn);
        for (int i = 0; i < dataDimnsn; i++) {
            double val = a.getValue(i) + b.getValue(i);
            c.setValue(i, val);
        }
        return c;
    }

    /**
     * Vector subtraction (a - b)
     * 
     * @param b The vector to be subtracted from this vector.
     * @return The resulting vector after subtraction.
     */
    public Point minus(Point b) {
        Point a = this;
        if (a.dataDimnsn != b.dataDimnsn)
            throw new RuntimeException("Vector lengths disagree");

        Point c = new Point(dataDimnsn);
        for (int i = 0; i < dataDimnsn; i++) {
            double val = a.getValue(i) - b.getValue(i);
            c.setValue(i, val);
        }
        return c;
    }

    /**
     * Scalar subtraction operator.
     * 
     * @param alpha The scalar value to be subtracted from the original vector.
     * @return The resulting vector, subtracted by alpha.
     */
    public Point sub(double alpha) {
        Point a = this;
        Point c = new Point(dataDimnsn);

        for (int i = 0; i < dataDimnsn; i++) {
            c.setValue(i, (a.getValue(i) - alpha));
        }
        return c;
    }

    /**
     * Inner product of two vectors.
     * 
     * @param b The vector to be inner-producted with this vector.
     * @return The inner-product value.
     */
    public double innerProduct(Point b) {
        Point a = this;
        double sum = 0.0;
        if (a.dataDimnsn != b.dataDimnsn)
            throw new RuntimeException("Vector lengths disagree");

        for (int i = 0; i < dataDimnsn; i++) {
            sum += a.getValue(i) * b.getValue(i);
        }
        return sum;
    }

    /**
     * 2-norm of the vector.
     * 
     * @return 2-norm value of the vector.
     */
    public double norm() {
        Point a = this;
        return Math.sqrt(a.innerProduct(a));
    }

    /**
     * Sum of every element in the vector.
     * 
     * @return Sum value of every element.
     */
    public double sum() {
        Point a = this;

        double sum = 0.0;
        for (int i = 0; i < dataDimnsn; i++) {
            sum += a.getValue(i);
        }
        return sum;
    }

    /**
     * Average of every element. 
     * 
     * @return The average value.
     */
    public double average() {
        Point a = this;

        return a.sum() / (double) this.dataDimnsn;
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
    public static Point parseOf(String line) {
        if (StringUtil.isBlank(line)) {
            return null;
        }

        String[] elems = line.split("\\#");

        int dataDimnsn = elems.length;
        Point obj = new Point(dataDimnsn);
        for (int i = 0; i < dataDimnsn; i++) {
            obj.setValue(i, Double.valueOf(elems[i].trim()));
        }

        return obj;
    }

}
