package cdb.common.model;

import cdb.common.lang.StringUtil;

/**
 * 
 * @author Chao Chen
 * @version $Id: ImageInfoVO.java, v 0.1 Oct 20, 2015 11:53:24 AM chench Exp $
 */
public class ImageInfoVO {

    /** Plain feature: distribution of the image*/
    private Point  distribution;
    /** Plain feature: entropy of the image*/
    private double entropy;
    /** Context feature: temporal entropy gradient [-1, 0]*/
    private double prevGrad;
    /** Context feature:  temporal entropy gradient [0, 1]*/
    private double nextGrad;
    /** the date with the format yyyymmdd*/
    private String dateStr;
    /** the frequency identification in Domain*/
    private String freqIdDomain;
    /** Output lable:  1: Left  2: Match    3: Right*/
    private int    resultLabel;

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
     * Getter method for property <tt>prevGrad</tt>.
     * 
     * @return property value of prevGrad
     */
    public double getPrevGrad() {
        return prevGrad;
    }

    /**
     * Setter method for property <tt>prevGrad</tt>.
     * 
     * @param prevGrad value to be assigned to property prevGrad
     */
    public void setPrevGrad(double prevGrad) {
        this.prevGrad = prevGrad;
    }

    /**
     * Getter method for property <tt>nextGrad</tt>.
     * 
     * @return property value of nextGrad
     */
    public double getNextGrad() {
        return nextGrad;
    }

    /**
     * Setter method for property <tt>nextGrad</tt>.
     * 
     * @param nextGrad value to be assigned to property nextGrad
     */
    public void setNextGrad(double nextGrad) {
        this.nextGrad = nextGrad;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return distribution + "," + entropy + "," + prevGrad + "," + nextGrad + "," + dateStr + ","
               + freqIdDomain + "," + resultLabel;
    }

    /**
     * similar to valueOf function of Numeric
     * 
     * @param line  the string contains object attributes
     * @return      the object
     */
    public static ImageInfoVO parseOf(String line) {
        String[] elems = line.split("\\,");

        ImageInfoVO imgVO = new ImageInfoVO();
        imgVO.setDistribution(Point.parseOf(elems[0].trim()));
        imgVO.setEntropy(Double.valueOf(elems[1].trim()));
        imgVO.setPrevGrad(Double.valueOf(elems[2].trim()));
        imgVO.setNextGrad(Double.valueOf(elems[3].trim()));
        imgVO.setDateStr(elems[4].trim());
        imgVO.setFreqIdDomain(elems[5].trim());
        imgVO.setResultLabel(
            StringUtil.isBlank(elems[6].trim()) ? 0 : Integer.valueOf(elems[6].trim()));
        return imgVO;
    }
}
