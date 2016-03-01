package cdb.ml.clustering;

import java.util.ArrayList;
import java.util.List;

import cdb.common.lang.DistanceUtil;
import cdb.common.model.Cluster;
import cdb.common.model.Point;
import cdb.common.model.Samples;

/**
 * class for ST-DBSCAN outlier detection algorithm.
 * "Derya Birant, Alp Kut. Spatio-Temporal Outlier Detection in Large Databases. 2006"
 * 
 * @author Qi LIU
 * @version Feb.16 2016
 */
public class STDBSCANKut {

	private enum PointStatus {
		/* Noise - outliers */
		OUTLIER,
		/* In Cluster */
		IN_CLUSTER
	}

	/**
	 * forbidden construction
	 */
	private STDBSCANKut() {

	}

	/**
	 * @param points
	 *            ST-Data samples
	 * @param Eps1
	 *            Maximum geographical coordinate (spatial) distance value
	 * @param Eps2
	 *            Maximum non-spatial distance value
	 * @param MinPts
	 *            Minimum number of points within Eps1 and Eps2 distance
	 * @param delta
	 *            Threshold value to be included in a cluster
	 * @param type
	 *            type of distance
	 * @return
	 */
	public static List<Cluster> cluster(final Samples points, final double eps1, final double eps2, final int minPts,
			final double delta, final int type) {

		final int pointCount = points.length()[0];

		List<Cluster> resultSet = new ArrayList<Cluster>();
		PointStatus[] visited = new PointStatus[pointCount];// 1-visited 2-noise

		for (int i = 0; i < pointCount; i++) {
			System.out.println("point id: " + i);
			if (visited[i] != null) {
				continue;
			}

			// query neighborhood
			List<Integer> neighbors = new ArrayList<Integer>();// index of neighbors wrt points
			neighbors = retrieveNeighbors(points, i, eps1, eps2, pointCount, type);

			if (neighbors.size() >= minPts) {
				final Cluster cluster = new Cluster();
				resultSet.add(expandCluster(i, points, pointCount, neighbors, cluster, eps1, eps2, delta, minPts, type,
						visited));// expand
				// cluster
			} else {// mark as noise
				visited[i] = PointStatus.OUTLIER;
			}
		}

		return resultSet;
	}

	/**
	 * Expands the cluster to include density-reachable items.
	 * 
	 * @param minPts
	 * @param eps
	 * @param cluster
	 * @param neighbors
	 * @param points
	 * @param i
	 * @param visited
	 *            the set of already visited points
	 * @return the expanded cluster
	 * 
	 */
	protected static Cluster expandCluster(final int pid, Samples points, final int pointCount, List<Integer> neighbors,
			Cluster cluster, final double eps1, final double eps2, final double delta, final int minPts, final int type,
			PointStatus[] visited) {
		// add P to cluster C
		cluster.add(pid);
		visited[pid] = PointStatus.IN_CLUSTER;// make sure visited is updated
												// globally
		// for each point, expand neighbors
		for (int i = 0; i < neighbors.size(); i++) {
			System.out.println("neighbor id: " + i);
			Integer current = neighbors.get(i);
			PointStatus pStatus = visited[current];// check neighbor point
													// status
			// only check non-visited points
			if (pStatus == null) {
				List<Integer> currentNeighbors = new ArrayList<Integer>();
				currentNeighbors = retrieveNeighbors(points, current, eps1, eps2, pointCount, type);
				if (currentNeighbors.size() >= minPts) {
					neighbors = joinList(currentNeighbors,neighbors);//update neighbors
				}
			}
			// compute non-spatial cluster centroid and delta, avoid spatially close but different cluster
			Point cluster_avg = cluster.centroid(points);
			double newDelta = DistanceUtil.distance(cluster_avg, points.getPointRef(current), type);
			
			if (pStatus != PointStatus.IN_CLUSTER && newDelta < delta) {
				visited[current] = PointStatus.IN_CLUSTER;
				cluster.add(current);
			}
		}
		return cluster;
	}

	/**
	 * Returns a list of density-reachable neighbors of p.
	 *
	 * @param samples
	 * @param point
	 *            to find neighbors
	 * @param eps1,
	 *            eps2
	 * @param number
	 *            of total points
	 * @param type
	 *            of distance
	 * @return the List of neighbors
	 */
	protected static List<Integer> retrieveNeighbors(Samples points, final int pid, final double eps1,
			final double eps2, final int pointCount, final int type) {
		List<Integer> neighbors = new ArrayList<Integer>();
		for (int i = 0; i < pointCount; i++) {
			Point neighbor = points.getPointRef(i);// get actual point features
													// vector
			// Neighbor-P Spatial Distance
			Point neighborLoc = new Point(neighbor.getLocation(0), neighbor.getLocation(1));
			Point currentLoc = new Point(points.getPointRef(pid).getLocation(0),
					points.getPointRef(pid).getLocation(1));

			double distanceS = DistanceUtil.distance(neighborLoc, currentLoc, type);
			// Neighbor-P non-Spatial Distance
			double distanceNS = DistanceUtil.distance(neighbor, points.getPointRef(pid), type);
			if (distanceS <= eps1 && distanceNS <= eps2) {
				neighbors.add(i);// add index instead of attributes
			}
		}
		return neighbors;
	}

	/**
	 * Join two lists
	 */
	private static List<Integer> joinList(final List<Integer> one, final List<Integer> two) {
		int onesize = one.size();
		for (int i = 0; i < onesize; i++) {
			if (!two.contains(one.get(i))) {
				two.add(one.get(i));
			}
		}
		return two;
	}

}// end of STDBSCANKut
