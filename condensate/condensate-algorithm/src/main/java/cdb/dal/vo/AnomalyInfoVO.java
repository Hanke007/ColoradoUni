package cdb.dal.vo;

import cdb.ml.clustering.Point;

/**
 * 
 * @author Chao Chen
 * @version $Id: AnomalyInfoVO.java, v 0.1 Oct 14, 2015 10:58:44 AM chench Exp $
 */
public class AnomalyInfoVO {
    /** mean*/
    private double meanVal;
    /** standard deviation*/
    private double sdVal;
    /** the centroid of the anomaly*/
    private Point  centroid;
    /** the date anomaly happens*/
    private int    dateInYear;
    /** the season anomaly happens*/
    private int    season;
    /** the date with the format yyyymmdd*/
    private String dateStr;
    /** the frequency identification in Domain*/
    private String freqIdDomain;
    /** the frequency identification in Target*/
    private String freqIdTarget;

    /** Contextual attribute*/
    /** Spatial context labels  1: water  2: ice    3: antarctic island*/
    private int spatialConLabel;
    /** Temporal context labels 1: start  2: continued  3: end*/
    private int temporalConLabel;
    /** Output lable:  1: Left  2: Match    3: Right*/
    private int resultLabel;

    /**
     * Getter method for property <tt>meanVal</tt>.
     * 
     * @return property value of meanVal
     */
    public double getMeanVal() {
        return meanVal;
    }

    /**
     * Setter method for property <tt>meanVal</tt>.
     * 
     * @param meanVal value to be assigned to property meanVal
     */
    public void setMeanVal(double meanVal) {
        this.meanVal = meanVal;
    }

    /**
     * Getter method for property <tt>sdVal</tt>.
     * 
     * @return property value of sdVal
     */
    public double getSdVal() {
        return sdVal;
    }

    /**
     * Setter method for property <tt>sdVal</tt>.
     * 
     * @param sdVal value to be assigned to property sdVal
     */
    public void setSdVal(double sdVal) {
        this.sdVal = sdVal;
    }

    /**
     * Getter method for property <tt>centroid</tt>.
     * 
     * @return property value of centroid
     */
    public Point getCentroid() {
        return centroid;
    }

    /**
     * Setter method for property <tt>centroid</tt>.
     * 
     * @param centroid value to be assigned to property centroid
     */
    public void setCentroid(Point centroid) {
        this.centroid = centroid;
    }

    /**
     * Getter method for property <tt>dateInYear</tt>.
     * 
     * @return property value of dateInYear
     */
    public int getDateInYear() {
        return dateInYear;
    }

    /**
     * Setter method for property <tt>dateInYear</tt>.
     * 
     * @param dateInYear value to be assigned to property dateInYear
     */
    public void setDateInYear(int dateInYear) {
        this.dateInYear = dateInYear;
    }

    /**
     * Getter method for property <tt>season</tt>.
     * 
     * @return property value of season
     */
    public int getSeason() {
        return season;
    }

    /**
     * Setter method for property <tt>season</tt>.
     * 
     * @param season value to be assigned to property season
     */
    public void setSeason(int season) {
        this.season = season;
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
     * Getter method for property <tt>freqIdTarget</tt>.
     * 
     * @return property value of freqIdTarget
     */
    public String getFreqIdTarget() {
        return freqIdTarget;
    }

    /**
     * Setter method for property <tt>freqIdTarget</tt>.
     * 
     * @param freqIdTarget value to be assigned to property freqIdTarget
     */
    public void setFreqIdTarget(String freqIdTarget) {
        this.freqIdTarget = freqIdTarget;
    }

    /**
     * Getter method for property <tt>spatialConLabel</tt>.
     * 
     * @return property value of spatialConLabel
     */
    public int getSpatialConLabel() {
        return spatialConLabel;
    }

    /**
     * Setter method for property <tt>spatialConLabel</tt>.
     * 
     * @param spatialConLabel value to be assigned to property spatialConLabel
     */
    public void setSpatialConLabel(int spatialConLabel) {
        this.spatialConLabel = spatialConLabel;
    }

    /**
     * Getter method for property <tt>temporalConLabel</tt>.
     * 
     * @return property value of temporalConLabel
     */
    public int getTemporalConLabel() {
        return temporalConLabel;
    }

    /**
     * Setter method for property <tt>temporalConLabel</tt>.
     * 
     * @param temporalConLabel value to be assigned to property temporalConLabel
     */
    public void setTemporalConLabel(int temporalConLabel) {
        this.temporalConLabel = temporalConLabel;
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
        return meanVal + "," + sdVal + ", " + centroid + ", " + dateInYear + ", " + season + ", "
               + dateStr + ", " + freqIdDomain + ", " + freqIdTarget + ", " + spatialConLabel + ", "
               + temporalConLabel + ", " + resultLabel;
    }

    /**
     * similar to valueOf function of Numeric
     * 
     * @param line  the string contains object attributes
     * @return      the object
     */
    public static AnomalyInfoVO parseOf(String line) {
        String[] elems = line.split("\\,");

        AnomalyInfoVO obj = new AnomalyInfoVO();
        obj.setMeanVal(Double.valueOf(elems[0].trim()));
        obj.setSdVal(Double.valueOf(elems[1].trim()));
        obj.setCentroid(Point.parseOf(elems[2].trim()));
        obj.setDateInYear(Integer.valueOf(elems[3].trim()));
        obj.setSeason(Integer.valueOf(elems[4].trim()));
        obj.setDateStr(elems[5].trim());
        obj.setFreqIdDomain(elems[6].trim());
        obj.setFreqIdTarget(elems[7].trim());
        obj.setSpatialConLabel(Integer.valueOf(elems[8].trim()));
        obj.setTemporalConLabel(Integer.valueOf(elems[9].trim()));
        obj.setResultLabel(Integer.valueOf(elems[10].trim()));
        return obj;
    }

}
