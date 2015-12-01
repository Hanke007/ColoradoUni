package cdb.web.vo;

/**
 * The POJO of Aggregated Anomalies
 * 
 * @author Chao Chen
 * @version $Id: AggregatedAnomalyVO.java, v 0.1 Nov 30, 2015 3:58:45 PM chench Exp $
 */
public class AggregatedAnomalyVO {
    /** the position of the anomaly*/
    private double longi;
    /** the position of the anomaly*/
    private double lati;
    /** the mean value of the occured anomaly*/
    private double mean;
    /** the frequency of the anomaly ocurring*/
    private double frequency;

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
     * Getter method for property <tt>frequency</tt>.
     * 
     * @return property value of frequency
     */
    public double getFrequency() {
        return frequency;
    }

    /**
     * Setter method for property <tt>frequency</tt>.
     * 
     * @param frequency value to be assigned to property frequency
     */
    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    /** 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(lati);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longi);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    /** 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AggregatedAnomalyVO other = (AggregatedAnomalyVO) obj;
        if (Double.doubleToLongBits(lati) != Double.doubleToLongBits(other.lati))
            return false;
        if (Double.doubleToLongBits(longi) != Double.doubleToLongBits(other.longi))
            return false;
        return true;
    }

}
