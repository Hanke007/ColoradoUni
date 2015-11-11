package cdb.web.envelope;

import java.util.Date;

/**
 * 
 * @author Chao Chen
 * @version $Id: AnomalyEnvelope.java, v 0.1 Nov 9, 2015 3:27:16 PM chench Exp $
 */
public class AnomalyEnvelope {
    /** the name of the date set*/
    private String dsName;
    /** the frequency of date set*/
    private String dsFreq;
    /** the date to start*/
    private Date   sDate;
    /** the date to end*/
    private Date   eDate;

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
    public Date getsDate() {
        return sDate;
    }

    /**
     * Setter method for property <tt>sDate</tt>.
     * 
     * @param sDate value to be assigned to property sDate
     */
    public void setsDate(Date sDate) {
        this.sDate = sDate;
    }

    /**
     * Getter method for property <tt>eDate</tt>.
     * 
     * @return property value of eDate
     */
    public Date geteDate() {
        return eDate;
    }

    /**
     * Setter method for property <tt>eDate</tt>.
     * 
     * @param eDate value to be assigned to property eDate
     */
    public void seteDate(Date eDate) {
        this.eDate = eDate;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "AnomalyEnvelope [dsName=" + dsName + ", dsFreq=" + dsFreq + ", sDate=" + sDate
               + ", eDate=" + eDate + "]";
    }

}
