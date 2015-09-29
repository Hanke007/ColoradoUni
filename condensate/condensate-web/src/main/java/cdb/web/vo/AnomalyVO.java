/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2015 All Rights Reserved.
 */
package cdb.web.vo;

import java.util.Date;

/**
 * The POJO of Anomalies
 * 
 * @author chench
 * @version $Id: AnomalyVO.java, v 0.1 Sep 26, 2015 5:00:22 PM chench Exp $
 */
public class AnomalyVO {

    /** the timestamp of the anomaly*/
    private Date   date;
    /** the value of the anomaly*/
    private double val;
    /** the position of the anomaly*/
    private double longi;
    /** the position of the anomaly*/
    private double lati;

    /**
     * Construction
     */
    public AnomalyVO() {
        super();
    }

    /**
     * Construction
     * 
     * @param date
     * @param val
     * @param longi
     * @param lati
     */
    public AnomalyVO(Date date, double val, double longi, double lati) {
        super();
        this.date = date;
        this.val = val;
        this.longi = longi;
        this.lati = lati;
    }

    /**
     * Getter method for property <tt>date</tt>.
     * 
     * @return property value of date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Setter method for property <tt>date</tt>.
     * 
     * @param date value to be assigned to property date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Getter method for property <tt>val</tt>.
     * 
     * @return property value of val
     */
    public double getVal() {
        return val;
    }

    /**
     * Setter method for property <tt>val</tt>.
     * 
     * @param val value to be assigned to property val
     */
    public void setVal(double val) {
        this.val = val;
    }

    /**
     * Getter method for property <tt>longi</tt>.
     * 
     * @return property value of longi
     */
    public double getLongi() {
        return longi;
    }

    /**
     * Setter method for property <tt>longi</tt>.
     * 
     * @param longi value to be assigned to property longi
     */
    public void setLongi(double longi) {
        this.longi = longi;
    }

    /**
     * Getter method for property <tt>lati</tt>.
     * 
     * @return property value of lati
     */
    public double getLati() {
        return lati;
    }

    /**
     * Setter method for property <tt>lati</tt>.
     * 
     * @param lati value to be assigned to property lati
     */
    public void setLati(double lati) {
        this.lati = lati;
    }

}
