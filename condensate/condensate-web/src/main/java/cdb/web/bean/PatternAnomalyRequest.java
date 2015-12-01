package cdb.web.bean;

import java.util.List;

/**
 * 
 * @author Chao Chen
 * @version $Id: PatternAnomalyRequest.java, v 0.1 Dec 1, 2015 10:02:15 AM chench Exp $
 */
public class PatternAnomalyRequest {
    /** the name of the date set*/
    private String            dsName;
    /** the frequency of date set*/
    private String            dsFreq;
    /** the month to start*/
    private int               sMonth;
    /** the month to end*/
    private int               eMonth;
    /** the year to start*/
    private int               sYear;
    /** the year to end*/
    private int               eYear;
    /** the locations of the target pixel*/
    private List<GeoLocation> locations;

    /**
     * Getter method for property <tt>dsName</tt>.
     * 
     * @return property value of dsName
     */
    public String getDsName() {
        return dsName;
    }

    /**
     * Setter method for property <tt>dsName</tt>.
     * 
     * @param dsName value to be assigned to property dsName
     */
    public void setDsName(String dsName) {
        this.dsName = dsName;
    }

    /**
     * Getter method for property <tt>dsFreq</tt>.
     * 
     * @return property value of dsFreq
     */
    public String getDsFreq() {
        return dsFreq;
    }

    /**
     * Setter method for property <tt>dsFreq</tt>.
     * 
     * @param dsFreq value to be assigned to property dsFreq
     */
    public void setDsFreq(String dsFreq) {
        this.dsFreq = dsFreq;
    }

    /**
     * Getter method for property <tt>sMonth</tt>.
     * 
     * @return property value of sMonth
     */
    public int getsMonth() {
        return sMonth;
    }

    /**
     * Setter method for property <tt>sMonth</tt>.
     * 
     * @param sMonth value to be assigned to property sMonth
     */
    public void setsMonth(int sMonth) {
        this.sMonth = sMonth;
    }

    /**
     * Getter method for property <tt>eMonth</tt>.
     * 
     * @return property value of eMonth
     */
    public int geteMonth() {
        return eMonth;
    }

    /**
     * Setter method for property <tt>eMonth</tt>.
     * 
     * @param eMonth value to be assigned to property eMonth
     */
    public void seteMonth(int eMonth) {
        this.eMonth = eMonth;
    }

    /**
     * Getter method for property <tt>sYear</tt>.
     * 
     * @return property value of sYear
     */
    public int getsYear() {
        return sYear;
    }

    /**
     * Setter method for property <tt>sYear</tt>.
     * 
     * @param sYear value to be assigned to property sYear
     */
    public void setsYear(int sYear) {
        this.sYear = sYear;
    }

    /**
     * Getter method for property <tt>eYear</tt>.
     * 
     * @return property value of eYear
     */
    public int geteYear() {
        return eYear;
    }

    /**
     * Setter method for property <tt>eYear</tt>.
     * 
     * @param eYear value to be assigned to property eYear
     */
    public void seteYear(int eYear) {
        this.eYear = eYear;
    }

    /**
     * Getter method for property <tt>locations</tt>.
     * 
     * @return property value of locations
     */
    public List<GeoLocation> getLocations() {
        return locations;
    }

    /**
     * Setter method for property <tt>locations</tt>.
     * 
     * @param locations value to be assigned to property locations
     */
    public void setLocations(List<GeoLocation> locations) {
        this.locations = locations;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PatternAnomalyRequest [dsName=" + dsName + ", dsFreq=" + dsFreq + ", sMonth="
               + sMonth + ", eMonth=" + eMonth + ", sYear=" + sYear + ", eYear=" + eYear
               + ", locations=" + locations + "]";
    }

}
