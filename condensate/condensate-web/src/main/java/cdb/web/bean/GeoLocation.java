package cdb.web.bean;

/**
 * 
 * @author Chao Chen
 * @version $Id: GeoLocation.java, v 0.1 Nov 9, 2015 4:33:55 PM chench Exp $
 */
public class GeoLocation {
    /** the position of the anomaly*/
    private double longitude;
    /** the position of the anomaly*/
    private double latitude;

    /**
     * 
     */
    public GeoLocation() {
        super();
    }

    /**
     * @param longi     the position of the anomaly
     * @param lati      the position of the anomaly
     */
    public GeoLocation(double longitude, double latitude) {
        super();
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * Getter method for property <tt>longitude</tt>.
     * 
     * @return property value of longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Setter method for property <tt>longitude</tt>.
     * 
     * @param longitude value to be assigned to property longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Getter method for property <tt>latitude</tt>.
     * 
     * @return property value of latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Setter method for property <tt>latitude</tt>.
     * 
     * @param latitude value to be assigned to property latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "GeoLocation [longitude=" + longitude + ", latitude=" + latitude + "]";
    }

}
