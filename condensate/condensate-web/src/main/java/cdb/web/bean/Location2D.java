package cdb.web.bean;

/**
 * 
 * @author Chao Chen
 * @version $Id: GeoLocation.java, v 0.1 Sep 30, 2015 10:31:59 AM chench Exp $
 */
public class Location2D {
    /** longitude of some position*/
    private int longitude;
    /** latitude of some position*/
    private int latitude;

    /**
     * construction
     */
    public Location2D() {
        super();
    }

    /**
     * construction
     * 
     * @param longitude
     * @param latitude
     */
    public Location2D(int longitude, int latitude) {
        super();
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * Getter method for property <tt>longitude</tt>.
     * 
     * @return property value of longitude
     */
    public int getLongitude() {
        return longitude;
    }

    /**
     * Setter method for property <tt>longitude</tt>.
     * 
     * @param longitude value to be assigned to property longitude
     */
    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    /**
     * Getter method for property <tt>latitude</tt>.
     * 
     * @return property value of latitude
     */
    public int getLatitude() {
        return latitude;
    }

    /**
     * Setter method for property <tt>latitude</tt>.
     * 
     * @param latitude value to be assigned to property latitude
     */
    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

}
