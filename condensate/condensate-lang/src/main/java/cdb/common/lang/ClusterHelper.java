package cdb.common.lang;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import cdb.common.model.Cluster;
import cdb.common.model.Location;
import cdb.common.model.MultiVarNormal;
import cdb.common.model.Point;
import cdb.common.model.Samples;
import cdb.common.model.UJMPDenseMatrix;
import cdb.common.model.UJMPDenseVector;

/**
 * 
 * @author Chao Chen
 * @version $Id: ClusterLocHelper.java, v 0.1 Sep 15, 2015 3:51:20 PM chench Exp
 *          $
 */
public final class ClusterHelper {

	/**
	 * forbidden construction
	 */
	private ClusterHelper() {
	}

	/**
	 * save the detailed 2D indexes of the given clustering result
	 * 
	 * @param clusters
	 *            the clustering result
	 * @param fileName
	 *            the target file to store the clustering information
	 * @param rowNum
	 *            the number of the rows
	 * @param colNum
	 *            the number of the columns
	 */
	public static void saveLoc(Cluster[] clusters, String fileName, int rowNum, int colNum) {
		FileUtil.existDirAndMakeDir(fileName);

		int clusterIndex = 0;
		StringBuilder content = new StringBuilder();
		for (Cluster cluster : clusters) {
			content.append(clusterIndex++).append("::");

			for (int index : cluster) {
				int row = index / colNum;
				int col = index % colNum;

				content.append(row).append(':').append(col).append(',');
			}
			content.deleteCharAt(content.length() - 1);
			content.append('\n');
		}

		FileUtil.write(fileName, content.toString());
	}

	public static void readLoc(String fileName, List<List<Location>> locSet) {
		// check essential information
		File file = new File(fileName);
		if (!file.isFile() | !file.exists()) {
			ExceptionUtil.caught(new FileNotFoundException("File Not Found"), fileName);
		}

		// read and parse locations
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				List<Location> oneSet = new ArrayList<Location>();

				int colonIndx = line.indexOf("::");
				String[] eleSet = line.substring(colonIndx + 2).split("\\,");
				for (String ele : eleSet) {
					String[] indices = ele.split("\\:");
					oneSet.add(new Location(Integer.parseInt(indices[0]), Integer.parseInt(indices[1])));
				}
				locSet.add(oneSet);
			}
		} catch (FileNotFoundException e) {
			ExceptionUtil.caught(e, file);
		} catch (IOException e) {
			ExceptionUtil.caught(e, file);
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}

	public static Cluster[] mergeAdjacentCluster(Samples dataSample, Cluster[] clusters, int disType, double alpha,
			int maxIterative) {
		Cluster[] oldClusters = mergeAdjacentClusterOnce(dataSample, clusters, disType, alpha);

		int iter = 0;
		Cluster[] newClusters = null;
		while (iter < maxIterative) {
			newClusters = mergeAdjacentClusterOnce(dataSample, oldClusters, disType, 1.0);

			if (newClusters.length == oldClusters.length) {
				newClusters = oldClusters;
				break;
			} else {
				oldClusters = newClusters;
			}
		}

		return newClusters;
	}
	
	/* s(i) = b(i) - a(i) / max{a(i), b(i)} */
	public static ArrayList<Double> calculateSilhouette(Cluster[] clusters, final int type, Samples points) {
		int clen = clusters.length;
		ArrayList<Double> silhouettes = new ArrayList<Double>(clen);
		for (int i = 0; i <  clen; i++) {
			silhouettes.add(i, (double) 0);
		}
		double a = 0;
		double minb = Double.MAX_VALUE;
		int k = 0;
		for (Cluster nc : clusters) {
			for (int idx : nc.getList()) {
				a = DistanceUtil.distance(points.getPointRef(idx), nc.centroid(points), type);
				for (Cluster cl : clusters) {
					if( !cl.getList().contains(idx) ) {
						double b = DistanceUtil.distance(points.getPointRef(idx), cl.centroid(points), type);
						if (b < minb) {
							minb = b;
						}
					}
				}//to get b(i)
				//calculate
				double silh = 0;
				if (a < minb){
					silh = 1 - a/minb;
				} else if (a == minb) {
					silh = 0;
				} else {
					silh = minb/a - 1;
				}
				silhouettes.set(k, silh + silhouettes.get(k));
			}
			k++;//increase cluster index
		}
		return silhouettes;
	}

	/* Boundary optimization */
	public static Cluster[] boundaryOptimizeCluster(Samples points, Cluster[] clusters) {
		int K = clusters.length;// number of clusters

		final int sampleNum = points.length()[0];
		final int varNum = points.length()[1];// dimension

		UJMPDenseVector[] samples = new UJMPDenseVector[sampleNum];
		for (int i = 0; i < sampleNum; i++) {
			samples[i] = points.getPointW(i);
		}
		Cluster[] newClusters = null;

		int[] numInGroups = new int[K];
		UJMPDenseVector[] mus = new UJMPDenseVector[K];
		UJMPDenseMatrix[] sigmaMatrices = new UJMPDenseMatrix[K];

		MultiVarNormal[] models = new MultiVarNormal[K];
		
        for (int k = 0; k < K; k++) {
            models[k] = new MultiVarNormal(varNum);
        }
		
		for (int k = 0; k < K; k++) {
			mus[k] = new UJMPDenseVector(varNum);
			sigmaMatrices[k] = new UJMPDenseMatrix(varNum, varNum);
		}
		
		// update means
		int m = 0;
		for (Cluster cl : clusters) {
			for (int idx : cl.getList()) {
				numInGroups[m]++;
				mus[m].plusW(samples[idx]);// add vector
			}
			m++;
		}//

		for (int k = 0; k < K; k++) {
			mus[k].scale(1.0 / numInGroups[k]);
			models[k].setMu(mus[k]);
		}

		// update variance matrix
		m = 0;
		for (Cluster cl : clusters) {
			for (int idx : cl.getList()) {
				UJMPDenseVector unbiasedVec = samples[idx].minus(mus[m]);
				sigmaMatrices[m] = sigmaMatrices[m].plus(unbiasedVec.outerProduct(unbiasedVec));
			}
			m++;
		}//
		
//		for (int i = 0; i < sampleNum; i++) {
//			int gIndx = assigmnt[i];
//			UJMPDenseVector unbiasedVec = samples[i].minus(mus[gIndx]);
//			sigmaMatrices[gIndx] = sigmaMatrices[gIndx].plus(unbiasedVec.outerProduct(unbiasedVec));
//		}
		
		for (int k = 0; k < K; k++) {
			sigmaMatrices[k].scale(1.0 / numInGroups[k]);
			models[k].setSigmaMatrix(sigmaMatrices[k]);
		}
		
		//remove elements from cluster based on p-value
		m = 1;

			for (int idx : clusters[K-1].getList()) {
				 double density = models[m].density(samples[idx]);
				 if (density < 0.05) {
					 System.out.println(density);
				 }
			}

		return newClusters;
	}

	protected static Cluster[] mergeAdjacentClusterOnce(Samples dataSample, Cluster[] oriClusters, int disType,
			double alpha) {
		// sorting clusters by item number in each cluster
		Cluster[] clusters = ascndingClusters(oriClusters);
		// Cluster[] clusters = oriClusters;

		// computer center and radius
		int k = clusters.length;
		Point[] centers = new Point[k];
		double[] radiuses = new double[k];
		for (int cIndx = 0; cIndx < k; cIndx++) {
			centers[cIndx] = clusters[cIndx].centroid(dataSample);

			radiuses[cIndx] = alpha * cmpRadiusInner(dataSample, clusters[cIndx], centers[cIndx], disType);
		}

		// merge clusters with overlaps
		int newClusterNum = k;
		for (int cIndx = 0; cIndx < k; cIndx++) {
			List<Integer> curArr = clusters[cIndx].getList();
			if (curArr.isEmpty()) {
				continue;
			}

			Point curCenter = centers[cIndx];
			double curRadius = radiuses[cIndx];
			for (int jIndx = cIndx + 1; jIndx < k; jIndx++) {
				List<Integer> iterArr = clusters[jIndx].getList();
				if (iterArr.isEmpty()) {
					continue;
				}

				Point iterCenter = centers[jIndx];
				if (DistanceUtil.distance(curCenter, iterCenter, disType) >= curRadius) {
					// not adjacent, then skip
					continue;
				}

				// merge adjacent clusters
				curArr.addAll(iterArr);
				iterArr.clear();
				newClusterNum--;
			}
		}

		int newClusterSeq = 0;
		Cluster[] newClusters = new Cluster[newClusterNum];
		for (int cIndx = 0; cIndx < k; cIndx++) {
			List<Integer> curArr = clusters[cIndx].getList();
			if (!curArr.isEmpty()) {
				newClusters[newClusterSeq] = clusters[cIndx];

				Point newCenter = clusters[cIndx].centroid(dataSample);
				newClusters[newClusterSeq].setCenter(newCenter);

				double radius = cmpRadiusInner(dataSample, newClusters[newClusterSeq], newCenter, disType);
				newClusters[newClusterSeq].setRadius(radius);
				newClusterSeq++;
			}
		}
		return newClusters;
	}

	protected static Cluster[] ascndingClusters(Cluster[] clusters) {

		Map<Integer, List<Cluster>> num2Cluster = new HashMap<Integer, List<Cluster>>();
		List<Integer> nums = new ArrayList<Integer>();
		for (Cluster cluster : clusters) {
			Integer key = cluster.getList().size();

			List<Cluster> cArr = num2Cluster.get(key);
			if (cArr == null) {
				cArr = new ArrayList<Cluster>();
				num2Cluster.put(key, cArr);
				nums.add(key);
			}

			cArr.add(cluster);
		}

		// sort in ascending
		Collections.sort(nums);

		// sorting
		int newSeq = 0;
		int k = clusters.length;
		Cluster[] newClusters = new Cluster[k];
		for (Integer num : nums) {
			List<Cluster> cArr = num2Cluster.get(num);

			for (Cluster cluster : cArr) {
				newClusters[newSeq] = cluster;
				newSeq++;
			}
		}

		return newClusters;
	}

	protected static double cmpRadiusInner(Samples dataSample, Cluster cluster, Point center, int disType) {
		double maxVal = Double.MIN_VALUE;
		for (int dIndx : cluster.getList()) {
			double curDis = DistanceUtil.distance(dataSample.getPoint(dIndx), center, disType);

			if (curDis > maxVal) {
				maxVal = curDis;
			}
		}
		return maxVal;
	}

}
