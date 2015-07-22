package cdb.dal.vo;

/**
 * General Mode to store the data.
 * 
 * @author chench
 * @version $Id: GeoEntity.java, v 0.1 Jul 22, 2015 3:46:54 PM chench Exp $
 */
public class GeoEntity {

    /** Datas */
    private double[][] datas;
    /** the number of rows */
    private int        rowNum;
    /** the number of columns */
    private int        colNum;

    /**
     * Constructions 
     * 
     * @param rowNum    the number of rows
     * @param colNum    the number of columns
     */
    public GeoEntity(int rowNum, int colNum) {
        this.rowNum = rowNum;
        this.colNum = colNum;
    }

    /**
     * Get the value given the specified row and column index
     * 
     * @param i     the index along row
     * @param j     the index along column
     * @return      the value in the corresponding index
     */
    public double getVal(int i, int j) {
        return datas[i][j];
    }

    /**
     * Set the value given the specified row and column index
     * 
     * @param i     the index along row
     * @param j     the index along column
     * @param val   the value to set
     */
    public void setVal(int i, int j, double val) {
        if (datas == null) {
            datas = new double[rowNum][colNum];
        }

        datas[i][j] = val;
    }

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

}
