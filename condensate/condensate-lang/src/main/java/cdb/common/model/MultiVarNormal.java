package cdb.common.model;

/**
 * the data structure of multivariate normal distribution
 * 
 * @author chench
 * @version $Id: ExpectationMaximumUtil.java, v 0.1 Feb 17, 2016 7:52:10 PM chench Exp $
 */
public class MultiVarNormal {
    private int             dimnVar;
    private UJMPDenseVector mu;
    private UJMPDenseMatrix sigmaMatrix;

    public MultiVarNormal(int dimnVar) {
        this.dimnVar = dimnVar;
    }

    /**
     * Compute the density of the sample
     * 
     * @param sample    the given sample
     * @return  the density
     */
    public double density(UJMPDenseVector sample) {//pdf function
        double density = Math.pow(2 * Math.PI, dimnVar) * sigmaMatrix.getMatrix().det();
        density = 1.0 / Math.sqrt(density);//expected value

        UJMPDenseVector unbiasedVec = sample.minus(mu);
        density *= Math
            .exp(-0.5 * sigmaMatrix.inverse().times(unbiasedVec).innerProduct(unbiasedVec));//sigmaMatric covariance matrix, calculate pdf
        return density;
    }

    /**
     * Getter method for property <tt>dimnVar</tt>.
     * 
     * @return property value of dimnVar
     */
    public int getDimnVar() {
        return dimnVar;
    }

    /**
     * Setter method for property <tt>dimnVar</tt>.
     * 
     * @param dimnVar value to be assigned to property dimnVar
     */
    public void setDimnVar(int dimnVar) {
        this.dimnVar = dimnVar;
    }

    /**
     * Getter method for property <tt>mu</tt>.
     * 
     * @return property value of mu
     */
    public UJMPDenseVector getMu() {
        return mu;
    }

    /**
     * Setter method for property <tt>mu</tt>.
     * 
     * @param mu value to be assigned to property mu
     */
    public void setMu(UJMPDenseVector mu) {
        this.mu = mu;
    }

    /**
     * Getter method for property <tt>sigmaMatrix</tt>.
     * 
     * @return property value of sigmaMatrix
     */
    public UJMPDenseMatrix getSigmaMatrix() {
        return sigmaMatrix;
    }

    /**
     * Setter method for property <tt>sigmaMatrix</tt>.
     * 
     * @param sigmaMatrix value to be assigned to property sigmaMatrix
     */
    public void setSigmaMatrix(UJMPDenseMatrix sigmaMatrix) {
        this.sigmaMatrix = sigmaMatrix;
    }

}
