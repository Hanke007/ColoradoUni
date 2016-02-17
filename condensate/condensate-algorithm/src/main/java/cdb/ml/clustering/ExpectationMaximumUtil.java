/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package cdb.ml.clustering;

import org.apache.log4j.Logger;

import cdb.common.lang.LoggerUtil;
import cdb.common.lang.log4j.LoggerDefineConstant;
import cdb.common.model.Cluster;
import cdb.common.model.Samples;
import cdb.common.model.UJMPDenseMatrix;
import cdb.common.model.UJMPDenseVector;

/**
 * 
 * @author chench
 * @version $Id: ExpectationMaximumUtil.java, v 0.1 Feb 17, 2016 6:46:06 PM chench Exp $
 */
public class ExpectationMaximumUtil {

    /** logger */
    private final static Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_THREAD);

    /**
     * forbid construction
     */
    private ExpectationMaximumUtil() {

    }

    /**
     * divide the samples into K classes
     * 
     * @param points        the sample to be clustered, which every row is a sample
     * @param K             the number of classes
     * @param maxIteration  the maximum number of iterations
     * @return
     */
    public static Cluster[] cluster(final Samples points, final int K, final int maxIteration) {
        final int sampleNum = points.length()[0];
        final int varNum = points.length()[1];
        if (sampleNum < K) {
            throw new RuntimeException("Number of samples is less than the number of classes.");
        }
        UJMPDenseVector[] samples = new UJMPDenseVector[sampleNum];
        for (int i = 0; i < sampleNum; i++) {
            samples[i] = points.getPointW(i);
        }

        //Create the initial clusters
        LoggerUtil.debug(logger, "0. Create the initial clusters.");
        int[] assigmnt = new int[sampleNum];
        chooseInitialCenters(assigmnt, sampleNum, K);

        //Converge to a minimun
        LoggerUtil.debug(logger, "1. Locally search optimal solution.");
        Cluster[] resultSet = new Cluster[K];
        MultiVarNormal[] models = new MultiVarNormal[K];

        int round = 0;
        while (round < maxIteration) {
            //E-step: 
            eStep(samples, assigmnt, K, sampleNum, varNum, models);

            //M-step:
            mStep(samples, assigmnt, K, sampleNum, varNum, models, resultSet);
        }
        return resultSet;
    }

    /**
     * make a initial division, and make sure no empty class
     * 
     * @param clusters
     * @param assigmnt
     * @param K
     */
    protected static void chooseInitialCenters(final int[] assigmnt, final int sampleNum,
                                               final int K) {
        boolean isNotEmptyCluster = false;
        while (!isNotEmptyCluster) {
            int[] numInGroups = new int[K];
            for (int i = 0; i < sampleNum; i++) {
                int gIndx = (int) (Math.random() * K);
                numInGroups[gIndx]++;
                assigmnt[i] = gIndx;
            }

            isNotEmptyCluster = true;
            // check empty cluster
            for (int k = 0; k < K; k++) {
                if (numInGroups[k] == 0) {
                    isNotEmptyCluster = false;
                }
            }
        }
    }

    protected static void eStep(UJMPDenseVector[] samples, int[] assigmnt, int K, int sampleNum,
                                int varNum, MultiVarNormal[] models) {
        int[] numInGroups = new int[K];
        UJMPDenseVector[] mus = new UJMPDenseVector[K];
        UJMPDenseMatrix[] sigmaMatrices = new UJMPDenseMatrix[K];
        for (int k = 0; k < K; k++) {
            mus[k] = new UJMPDenseVector(varNum);
            sigmaMatrices[k] = new UJMPDenseMatrix(varNum, varNum);
        }
        //      update means
        for (int i = 0; i < sampleNum; i++) {
            int gIndx = assigmnt[i];
            numInGroups[gIndx]++;
            mus[gIndx].plusW(samples[i]);
        }
        for (int k = 0; k < K; k++) {
            mus[k].scale(1.0 / numInGroups[k]);
            models[k].setMu(mus[k]);
        }

        //      update variance matrix
        for (int i = 0; i < sampleNum; i++) {
            int gIndx = assigmnt[i];
            UJMPDenseVector unbiasedVec = samples[i].minus(mus[gIndx]);
            sigmaMatrices[gIndx] = sigmaMatrices[gIndx].plus(unbiasedVec.outerProduct(unbiasedVec));
        }
        for (int k = 0; k < K; k++) {
            sigmaMatrices[k].scale(1.0 / numInGroups[k]);
            models[k].setSigmaMatrix(sigmaMatrices[k]);
        }
    }

    protected static void mStep(UJMPDenseVector[] samples, int[] assigmnt, int K, int sampleNum,
                                int varNum, MultiVarNormal[] models, Cluster[] resultSet) {
        for (int k = 0; k < K; k++) {
            resultSet[k] = new Cluster();
        }

        double sumLikilyHood = 0.0d;
        for (int i = 0; i < sampleNum; i++) {
            int pivot = -1;
            double max = -1.0 * Double.MAX_VALUE;
            for (int k = 0; k < K; k++) {
                double density = models[k].density(samples[i]);

                if (density >= max) {
                    pivot = k;
                    max = density;
                }
            }
            sumLikilyHood += max;
            assigmnt[i] = pivot;
            resultSet[pivot].add(i);
        }
        LoggerUtil.debug(logger, "Log[Pr]: " + Math.log(sumLikilyHood));
    }

    /**
     * the data structure of multivariate normal distribution
     * 
     * @author chench
     * @version $Id: ExpectationMaximumUtil.java, v 0.1 Feb 17, 2016 7:52:10 PM chench Exp $
     */
    protected class MultiVarNormal {
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
        public double density(UJMPDenseVector sample) {
            double density = Math.pow(2 * Math.PI, dimnVar) * sigmaMatrix.getMatrix().det();
            density = 1.0 / Math.sqrt(density);

            UJMPDenseVector unbiasedVec = sample.minus(mu);
            density *= Math
                .exp(-0.5 * sigmaMatrix.inverse().times(unbiasedVec).innerProduct(unbiasedVec));
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
}
