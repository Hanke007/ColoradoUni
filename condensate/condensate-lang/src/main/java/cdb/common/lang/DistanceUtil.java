package cdb.common.lang;

import cdb.common.model.Point;

/**
 * 
 * @author Chao Chen
 * @version $Id: DistanceUtil.java, v 0.1 Oct 20, 2015 10:09:18 AM chench Exp $
 */
public final class DistanceUtil {

    /** sine distance*/
    public final static int SINE_DISTANCE                = 201;
    /** square error*/
    public final static int SQUARE_EUCLIDEAN_DISTANCE    = 202;
    /** pearson correlation*/
    public final static int PEARSON_CORRELATION_DISTANCE = 203;
    /** KL divergence*/
    public final static int KL_DISTANCE                  = 204;
    /** KL divergence with convergence insurance*/
    public final static int KL_DISTANCE_CONVEX           = 205;

    private DistanceUtil() {

    }

    /**
     * calculate the distance between two vectors
     *  
     * @param a     given vector
     * @param b     given vector
     * @param type  the distance to compute
     * @return
     */
    public static double distance(final Point a, final Point centroid, final int type) {
        //check vector with all zeros
        if (type != SQUARE_EUCLIDEAN_DISTANCE && (a.norm() == 0 || centroid.norm() == 0)) {
            return 0.0;
        }

        switch (type) {
            case SINE_DISTANCE:
                double cosine = a.innerProduct(centroid) / (a.norm() * centroid.norm());// a*b / (|a|*|b|)
                return Math.sqrt(1 - cosine * cosine);
            case SQUARE_EUCLIDEAN_DISTANCE:
                Point c = a.minus(centroid);
                return Math.sqrt(c.innerProduct(c)); // |a-b|
            case PEARSON_CORRELATION_DISTANCE:
                a.sub(a.average());
                centroid.sub(centroid.average());
                return a.innerProduct(centroid) / (a.norm() * centroid.norm());
            default:
                throw new RuntimeException("Wrong Distance Type! ");
        }
    }
}
