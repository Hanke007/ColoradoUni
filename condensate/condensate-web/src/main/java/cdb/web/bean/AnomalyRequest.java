package cdb.web.bean;

import java.util.List;

/**
 * 
 * @author Chao Chen
 * @version $Id: AnomlyRequest.java, v 0.1 Sep 30, 2015 9:52:45 AM chench Exp $
 */
public class AnomalyRequest {

    /** the name of the date set*/
    private String            dsName;
    /** the frequency of date set*/
    private String            dsFreq;
    /** the date to start*/
    private String            sDate;
    /** the date to end*/
    private String            eDate;
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
     * Getter method for property <tt>sDate</tt>.
     * 
     * @return property value of sDate
     */
    public String getsDate() {
        return sDate;
    }

    /**
     * Setter method for property <tt>sDate</tt>.
     * 
     * @param sDate value to be assigned to property sDate
     */
    public void setsDate(String sDate) {
        this.sDate = sDate;
    }

    /**
     * Getter method for property <tt>eDate</tt>.
     * 
     * @return property value of eDate
     */
    public String geteDate() {
        return eDate;
    }

    /**
     * Setter method for property <tt>eDate</tt>.
     * 
     * @param eDate value to be assigned to property eDate
     */
    public void seteDate(String eDate) {
        this.eDate = eDate;
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
        return "AnomalyRequest [dsName=" + dsName + ", dsFreq=" + dsFreq + ", sDate=" + sDate
               + ", eDate=" + eDate + ", locations=" + locations + "]";
    }

}
