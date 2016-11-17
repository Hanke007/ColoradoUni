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
 * multivariate expectation maximum algorithm Technical detail of the algorithm
 * can be found in Lee, Gyemin, and Clayton Scott. EM algorithms for
 * multivariate Gaussian mixture models with truncated and censored data
 * Computational Statistics & Data Analysis
 * 
 * @author qliu update
 * @version $Id: EMUtil.java, v 0.1 April 17th$
 */
public class EMUtil {

	/** logger */
	private final static Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_THREAD);

	/**
	 * forbid construction
	 */
	private EMUtil() {

	}

	/**
	 * divide the samples into K classes
	 * 
	 * @param points
	 *            the sample to be clustered, which every row is a sample
	 * @param K
	 *            the number of classes
	 * @param maxIteration
	 *            the maximum number of iterations
	 * @return
	 */
	public static Cluster[] cluster(final Samples points, int maxK, final int maxIteration) {
		final int sampleNum = points.length()[0];
		final int varNum = points.length()[1];
		if (sampleNum < maxK) {
			throw new RuntimeException("Number of samples is less than the number of classes.");
		}
		UJMPDenseVector[] samples = new UJMPDenseVector[sampleNum];
		for (int i = 0; i < sampleNum; i++) {
			samples[i] = points.getPointW(i);
		}

		// Create the initial clusters
		LoggerUtil.debug(logger, "0. Create the initial clusters.");
		int[] assigmnt = new int[sampleNum];
		double[][] clusterProps = new double[sampleNum][maxK];
		chooseInitialCenters(assigmnt, sampleNum, maxK);

		// Converge to a minimun
		LoggerUtil.debug(logger, "1. Locally search optimal solution.");
		MultiVarNormal[] models = new MultiVarNormal[maxK];

		for (int k = 0; k < maxK; k++) {
			models[k] = new MultiVarNormal(varNum);
		}

		int round = 0;
		int K = maxK;
		while (round < maxIteration) {
			// E-step:
			eStep(samples, assigmnt, K, sampleNum, varNum, models, round, clusterProps);

			// M-step:
			mStep(samples, assigmnt, K, sampleNum, varNum, models, clusterProps);

			// R-step
			K = rStep(samples, assigmnt, K, sampleNum, varNum, models, clusterProps);

			round++;
		}
		// make assignment
		Cluster[] resultSet = new Cluster[K];
		for (int k = 0; k < K; k++) {
			resultSet[k] = new Cluster();
		}

		for (int i = 0; i < sampleNum; i++) {
			int pivot = -1;
			double max = -1.0 * Double.MAX_VALUE;
			for (int k = 0; k < K; k++) {
				try {
					double density = models[k].density(samples[i]);
					if (density >= max) {// assign the sample to the cluster
						// where it has maximum pdf
						pivot = k;
						max = density;
					}

				} catch (Exception e) {
					System.out.println(models[k].getSigmaMatrix().toString());
				}
			}
			// sumLikilyHood += max;
			// assigmnt[i] = pivot;
			resultSet[pivot].add(i);
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
	protected static void chooseInitialCenters(final int[] assigmnt, final int sampleNum, int K) {
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

	protected static void eStep(UJMPDenseVector[] samples, int[] assigmnt, int K, int sampleNum, int varNum,
			MultiVarNormal[] models, int round, double[][] clusterPs) {
		int[] numInGroups = new int[K];
		UJMPDenseVector[] mus = new UJMPDenseVector[K];
		UJMPDenseMatrix[] sigmaMatrices = new UJMPDenseMatrix[K];
		if (round == 0) {
			for (int k = 0; k < K; k++) {
				mus[k] = new UJMPDenseVector(varNum);
				sigmaMatrices[k] = new UJMPDenseMatrix(varNum, varNum);
			}
			// calculate initial means
			for (int i = 0; i < sampleNum; i++) {
				int gIndx = assigmnt[i];
				numInGroups[gIndx]++;
				mus[gIndx].plusW(samples[i]);// add vector
			}
			for (int k = 0; k < K; k++) {
				mus[k].scale(1.0 / numInGroups[k]);
				models[k].setMu(mus[k]);
			}

			// calculate initial variance matrix
			for (int i = 0; i < sampleNum; i++) {
				int gIndx = assigmnt[i];
				UJMPDenseVector unbiasedVec = samples[i].minus(mus[gIndx]);
				sigmaMatrices[gIndx] = sigmaMatrices[gIndx].plus(unbiasedVec.outerProduct(unbiasedVec));
			}
			for (int k = 0; k < K; k++) {
				sigmaMatrices[k].scale(1.0 / numInGroups[k]);
				models[k].setSigmaMatrix(sigmaMatrices[k]);
			}

			for (int k = 0; k < K; k++) {
				// total responsibility allocated to each cluster
				for (int i = 0; i < sampleNum; i++) {
					models[k].setMc((double) sampleNum / K);
				}
			}

		}
		for (int i = 0; i < sampleNum; i++) {
			double sumProp = 0;
			for (int k = 0; k < K; k++) {
				double density = models[k].density(samples[i]);
				clusterPs[i][k] = density;
				sumProp += models[k].getMc() * density;
			}
			// normalization
			for (int k = 0; k < K; k++) {
				clusterPs[i][k] = models[k].getMc() * clusterPs[i][k] / sumProp;
			}
		} //
	}

	protected static void mStep(UJMPDenseVector[] samples, int[] assigmnt, int K, int sampleNum, int varNum,
			MultiVarNormal[] models, double[][] clusterPs) {
		double sumLikilyHood = 0.0d;
		// For each cluster, update its parameter based on the weighted data
		// points
		int[] numInGroups = new int[K];
		UJMPDenseVector[] mus = new UJMPDenseVector[K];
		UJMPDenseMatrix[] sigmaMatrices = new UJMPDenseMatrix[K];

		for (int k = 0; k < K; k++) {
			mus[k] = new UJMPDenseVector(varNum);
			sigmaMatrices[k] = new UJMPDenseMatrix(varNum, varNum);
		}

		for (int k = 0; k < K; k++) {
			// total responsibility allocated to each cluster
			models[k].setMc(0);// initilize to 0
			for (int i = 0; i < sampleNum; i++) {
				models[k].setMc(models[k].getMc() + clusterPs[i][k]);
				UJMPDenseVector sampleTemp = samples[i].copy();
				mus[k].plusW(sampleTemp.scale(clusterPs[i][k]));// add vector
			}
		}

		for (int k = 0; k < K; k++) {
			// total responsibility allocated to each cluster
			for (int i = 0; i < sampleNum; i++) {
				// sigma
				UJMPDenseVector unbiasedVec = samples[i].minus(mus[k]);
				UJMPDenseVector unbiasedTemp = unbiasedVec.copy();
				sigmaMatrices[k] = sigmaMatrices[k]
						.plus(unbiasedTemp.outerProduct(unbiasedTemp).scale(clusterPs[i][k]));

			}
		}

		// update means
		for (int k = 0; k < K; k++) {
			mus[k].scale(1.0 / models[k].getMc());
			models[k].setMu(mus[k]);
		}

		for (int k = 0; k < K; k++) {
			sigmaMatrices[k].scale(1.0 / models[k].getMc());
			models[k].setSigmaMatrix(sigmaMatrices[k]);
		}
	}

	/* remove clusters if has < 1 possession */
	protected static int rStep(UJMPDenseVector[] samples, int[] assigmnt, int K, int sampleNum, int varNum,
			MultiVarNormal[] models, double[][] clusterPs) {
		double sumLikilyHood = 0.0d;
		int newK = K;

		for (int k = 0; k < K; k++) {
			if (models[k].getMc() < 1) {
				newK--;// remove a cluster
			}
		}

		if (newK < K) {
			MultiVarNormal[] reducedModels = new MultiVarNormal[newK];
			for (int k = 0; k < newK; k++) {
				reducedModels[k] = new MultiVarNormal(varNum);
			}

			// create new models set
			int nk = 0;
			for (int k = 0; k < K; k++) {
				if (models[k].getMc() >= 1) {
					reducedModels[nk].setDimnVar(models[k].getDimnVar());
					reducedModels[nk].setMc(models[k].getMc());
					reducedModels[nk].setPic(models[k].getPic());
					reducedModels[nk].setMu(models[k].getMu());
					reducedModels[nk].setSigmaMatrix(models[k].getSigmaMatrix());
					nk++;
				}
			}

			// reset models and reset K
			for (int k = 0; k < newK; k++) {
				models[k].setDimnVar(reducedModels[k].getDimnVar());
				models[k].setMc(reducedModels[k].getMc());
				models[k].setPic(reducedModels[k].getPic());
				models[k].setMu(reducedModels[k].getMu());
				models[k].setSigmaMatrix(reducedModels[k].getSigmaMatrix());
			}

		}

		// for loglikelyhood test
//		for (int i = 0; i < sampleNum; i++) {
//			int pivot = -1;
//			double max = -1.0 * Double.MAX_VALUE;
//			for (int k = 0; k < newK; k++) {
//				try {
//					double density = models[k].density(samples[i]);
//					if (density >= max) {// assign the sample to the cluster
//						// where it has maximum pdf
//						pivot = k;
//						max = density;
//					}
//
//				} catch (Exception e) {
//					System.out.println(models[k].getSigmaMatrix().toString());
//				}
//			}
//			sumLikilyHood += max;
//		}
//		System.out.println("[Pr]: " + sumLikilyHood);
		return newK;
	}

}
