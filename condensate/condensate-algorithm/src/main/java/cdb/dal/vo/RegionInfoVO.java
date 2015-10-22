package cdb.dal.vo;

import cdb.common.lang.StringUtil;
import cdb.ml.clustering.Point;

/**
 * 
 * @author Chao Chen
 * @version $Id: RegionInfoVO.java, v 0.1 Oct 22, 2015 2:32:54 PM chench Exp $
 */
public class RegionInfoVO {
    //==================================
    //  Intra-region features 
    //==================================
    /** Plain feature: distribution of the image*/
    private Point  distribution;
    /** Plain feature: entropy of the image*/
    private double entropy;
    /** the date with the format yyyymmdd*/
    private String dateStr;
    /** the gradients between pixels along rows*/
    private Point  gradRow;
    /** the gradients between pixels along columns*/
    private Point  gradCol;
    /** the mean of the gradients in the region*/
    private double gradMean;
    /** the mean in the region*/
    private double mean;
    /** the standard deviation in the region*/
    private double sd;
    //==================================
    //  Contextual features
    //==================================
    /** temporal attribute: gradients in adjacent days [-1, 0, 1]*/
    private Point  tGradCon;
    /** spatial attribute: correlation in 3-by-3 box*/
    private Point  sCorrCon;
    /** spatial attribute: difference in 3-by-3 box*/
    private Point  sDiffCon;

    /** the frequency identification in Domain*/
    private String freqIdDomain;
    /** Output lable:  1: Left  2: Match    3: Right*/
    private int    resultLabel;

    /**
     * Getter method for property <tt>distribution</tt>.
     * 
     * @return property value of distribution
     */
    public Point getDistribution() {
        return distribution;
    }

    /**
     * Setter method for property <tt>distribution</tt>.
     * 
     * @param distribution value to be assigned to property distribution
     */
    public void setDistribution(Point distribution) {
        this.distribution = distribution;
    }

    /**
     * Getter method for property <tt>entropy</tt>.
     * 
     * @return property value of entropy
     */
    public double getEntropy() {
        return entropy;
    }

    /**
     * Setter method for property <tt>entropy</tt>.
     * 
     * @param entropy value to be assigned to property entropy
     */
    public void setEntropy(double entropy) {
        this.entropy = entropy;
    }

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
     * Getter method for property <tt>gradRow</tt>.
     * 
     * @return property value of gradRow
     */
    public Point getGradRow() {
        return gradRow;
    }

    /**
     * Setter method for property <tt>gradRow</tt>.
     * 
     * @param gradRow value to be assigned to property gradRow
     */
    public void setGradRow(Point gradRow) {
        this.gradRow = gradRow;
    }

    /**
     * Getter method for property <tt>gradCol</tt>.
     * 
     * @return property value of gradCol
     */
    public Point getGradCol() {
        return gradCol;
    }

    /**
     * Setter method for property <tt>gradCol</tt>.
     * 
     * @param gradCol value to be assigned to property gradCol
     */
    public void setGradCol(Point gradCol) {
        this.gradCol = gradCol;
    }

    /**
     * Getter method for property <tt>gradMean</tt>.
     * 
     * @return property value of gradMean
     */
    public double getGradMean() {
        return gradMean;
    }

    /**
     * Setter method for property <tt>gradMean</tt>.
     * 
     * @param gradMean value to be assigned to property gradMean
     */
    public void setGradMean(double gradMean) {
        this.gradMean = gradMean;
    }

    /**
     * Getter method for property <tt>mean</tt>.
     * 
     * @return property value of mean
     */
    public double getMean() {
        return mean;
    }

    /**
     * Setter method for property <tt>mean</tt>.
     * 
     * @param mean value to be assigned to property mean
     */
    public void setMean(double mean) {
        this.mean = mean;
    }

    /**
     * Getter method for property <tt>sd</tt>.
     * 
     * @return property value of sd
     */
    public double getSd() {
        return sd;
    }

    /**
     * Setter method for property <tt>sd</tt>.
     * 
     * @param sd value to be assigned to property sd
     */
    public void setSd(double sd) {
        this.sd = sd;
    }

    /**
     * Getter method for property <tt>tGradCon</tt>.
     * 
     * @return property value of tGradCon
     */
    public Point gettGradCon() {
        return tGradCon;
    }

    /**
     * Setter method for property <tt>tGradCon</tt>.
     * 
     * @param tGradCon value to be assigned to property tGradCon
     */
    public void settGradCon(Point tGradCon) {
        this.tGradCon = tGradCon;
    }

    /**
     * Getter method for property <tt>sCorrCon</tt>.
     * 
     * @return property value of sCorrCon
     */
    public Point getsCorrCon() {
        return sCorrCon;
    }

    /**
     * Setter method for property <tt>sCorrCon</tt>.
     * 
     * @param sCorrCon value to be assigned to property sCorrCon
     */
    public void setsCorrCon(Point sCorrCon) {
        this.sCorrCon = sCorrCon;
    }

    /**
     * Getter method for property <tt>sDiffCon</tt>.
     * 
     * @return property value of sDiffCon
     */
    public Point getsDiffCon() {
        return sDiffCon;
    }

    /**
     * Setter method for property <tt>sDiffCon</tt>.
     * 
     * @param sDiffCon value to be assigned to property sDiffCon
     */
    public void setsDiffCon(Point sDiffCon) {
        this.sDiffCon = sDiffCon;
    }

    /**
     * Getter method for property <tt>freqIdDomain</tt>.
     * 
     * @return property value of freqIdDomain
     */
    public String getFreqIdDomain() {
        return freqIdDomain;
    }

    /**
     * Setter method for property <tt>freqIdDomain</tt>.
     * 
     * @param freqIdDomain value to be assigned to property freqIdDomain
     */
    public void setFreqIdDomain(String freqIdDomain) {
        this.freqIdDomain = freqIdDomain;
    }

    /**
     * Getter method for property <tt>resultLabel</tt>.
     * 
     * @return property value of resultLabel
     */
    public int getResultLabel() {
        return resultLabel;
    }

    /**
     * Setter method for property <tt>resultLabel</tt>.
     * 
     * @param resultLabel value to be assigned to property resultLabel
     */
    public void setResultLabel(int resultLabel) {
        this.resultLabel = resultLabel;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return distribution + "," + entropy + "," + dateStr + "," + gradRow + "," + gradCol + ","
               + gradMean + "," + mean + "," + sd + "," + tGradCon + "," + sCorrCon + "," + sDiffCon
               + "," + freqIdDomain + "," + resultLabel;
    }

    /**
     * similar to valueOf function of Numeric
     * 
     * @param line  the string contains object attributes
     * @return      the object
     */
    public static RegionInfoVO parseOf(String line) {
        String[] elems = line.split("\\,");

        RegionInfoVO regnVO = new RegionInfoVO();

        // plain features
        regnVO.setDistribution(Point.parseOf(elems[0].trim()));
        regnVO.setEntropy(Double.valueOf(elems[1].trim()));
        regnVO.setDateStr(elems[2].trim());
        regnVO.setGradRow(Point.parseOf(elems[3].trim()));
        regnVO.setGradCol(Point.parseOf(elems[4].trim()));
        regnVO.setGradMean(Double.valueOf(elems[5].trim()));
        regnVO.setMean(Double.valueOf(elems[6].trim()));
        regnVO.setSd(Double.valueOf(elems[7].trim()));

        // contextual features
        regnVO.settGradCon(Point.parseOf(elems[8].trim()));
        regnVO.setsCorrCon(Point.parseOf(elems[9].trim()));
        regnVO.setsDiffCon(Point.parseOf(elems[10].trim()));

        // identification label
        regnVO.setFreqIdDomain(elems[11].trim());
        regnVO.setResultLabel(
            StringUtil.isBlank(elems[12].trim()) ? 0 : Integer.valueOf(elems[12].trim()));
        return regnVO;
    }
}
