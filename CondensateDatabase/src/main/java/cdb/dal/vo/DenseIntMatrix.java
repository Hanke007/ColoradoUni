package cdb.dal.vo;

/**
 * General Mode to store the data.
 * 
 * @author Chao Chen
 * @version $Id: GeoEntity.java, v 0.1 Jul 22, 2015 3:46:54 PM chench Exp $
 */
public class DenseIntMatrix implements java.io.Serializable {

    /** serialNum */
    private static final long serialVersionUID = 1L;
    /** data */
    private int[][]           data;
    /** the number of rows */
    private int               rowNum;
    /** the number of columns */
    private int               colNum;

    /**
     * Constructions 
     * 
     * @param rowNum    the number of rows
     * @param colNum    the number of columns
     */
    public DenseIntMatrix(int rowNum, int colNum) {
        this.rowNum = rowNum;
        this.colNum = colNum;
        data = new int[rowNum][colNum];
    }

    /**
     * Get the value given the specified row and column index
     * 
     * @param i     the index along row
     * @param j     the index along column
     * @return      the value in the corresponding index
     */
    public int getVal(int i, int j) {
        return data[i][j];
    }

    /**
     * Set the value given the specified row and column index
     * 
     * @param i     the index along row
     * @param j     the index along column
     * @param val   the value to set
     */
    public void setVal(int i, int j, int val) {
        data[i][j] = val;
    }

    //================================================
    //  Getter and Setter
    //================================================
    /**
     * Getter method for property <tt>rowNum</tt>.
     * 
     * @return property value of rowNum
     */
    public int getRowNum() {
        return rowNum;
    }

    /**
     * Getter method for property <tt>colNum</tt>.
     * 
     * @return property value of colNum
     */
    public int getColNum() {
        return colNum;
    }

    //================================================
    //  Operations
    //================================================
    /**
     * Add the given value to the specified position
     * 
     * @param i         the index along row
     * @param j         the index along column
     * @param addOne    the value to add
     */
    public void add(int i, int j, int addOne) {
        data[i][j] += addOne;
    }

    /**
     * Scale the whole matrix by given double param
     * 
     * @param param     the given value to scale the whole matrix
     */
    public void scale(double param) {
        for (int row = 0; row < rowNum; row++) {
            for (int col = 0; col < colNum; col++) {
                data[row][col] = (int) (data[row][col] / param);
            }
        }
    }

    /*========================================
     * Properties
     *========================================*/
    /**
     * Average of every element in given row
     * 
     * @param row       the index along row
     * @return
     */
    public double rowAvg(int row) {
        double sum = 0.0d;
        for (int j = 0; j < colNum; j++) {
            sum += data[row][j];
        }
        return sum / colNum;
    }

    /**
     * Average of every element in given column
     * 
     * @param column    the index along column
     * @return
     */
    public double colAvg(int column) {
        double sum = 0.0d;
        for (int i = 0; i < rowNum; i++) {
            sum += data[i][column];
        }
        return sum / rowNum;
    }
}
