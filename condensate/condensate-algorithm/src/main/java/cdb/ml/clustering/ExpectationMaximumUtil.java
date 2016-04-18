/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package cdb.ml.clustering;

import org.apache.log4j.Logger;

import cdb.common.lang.LoggerUtil;
import cdb.common.lang.log4j.LoggerDefineConstant;
import cdb.common.model.Cluster;
import cdb.common.model.MultiVarNormal;
import cdb.common.model.Samples;
import cdb.common.model.UJMPDenseMatrix;
import cdb.common.model.UJMPDenseVector;

/**
 * multivariate expectation maximum algorithm
 * Technical detail of the algorithm can be found in
 * Lee, Gyemin, and Clayton Scott.
 * EM algorithms for multivariate Gaussian mixture models with truncated and censored data
 * Computational Statistics & Data Analysis
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
        
        for (int k = 0; k < K; k++) {
            models[k] = new MultiVarNormal(varNum);
        }

        int round = 0;
        while (round < maxIteration) {
            //E-step: 
            eStep(samples, assigmnt, K, sampleNum, varNum, models);

            //M-step:
            mStep(samples, assigmnt, K, sampleNum, varNum, models, resultSet);
            
            round++;
        }
        //after all iterations, make assignment 
        
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
            mus[gIndx].plusW(samples[i]);//add vector
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
            	
            	try{
                double density = models[k].density(samples[i]);
                if (density >= max) {//assign the sample to the cluster where it has maximum pdf
                    pivot = k;
                    max = density;
                }
                
            	} catch(Exception e) {
            		System.out.println(models[k].getSigmaMatrix().toString());
            	}
            }
            sumLikilyHood += max;
            assigmnt[i] = pivot;
            resultSet[pivot].add(i);
        }
        LoggerUtil.debug(logger, "Log[Pr]: " + Math.log(sumLikilyHood));
    }


}
