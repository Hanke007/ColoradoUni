package cdb.ml.clustering;

import java.util.ArrayList;
import java.util.List;

import cdb.common.lang.DistanceUtil;
import cdb.common.model.Cluster;
import cdb.common.model.Point;
import cdb.common.model.Samples;

/**
 * class for basic DBSCAN algorithm.
 * "A density-based algorithm for discovering clusters in large spatial databases with noise."
 * 
 * @author Qi LIU
 * @version Feb.15 2016
 */

public class DBSCANBasic {

	private enum PointStatus {
		/* Noise */
		NOISE,
		/* In Cluster */
		IN_CLUSTER,
		/*VISITED*/
		VISITED
	}

	/**
	 * forbidden construction
	 */
	private DBSCANBasic() {

	}

	/**
	 * divide the samples into maximum K classes
	 * 
	 * @param Eps
	 *            Maximum radius of the neighborhood
	 * @param MinPts
	 *            Minimum number of points in the Eps-neighborhood of a point
	 * @param type
	 *            type of distance
	 * @return
	 */
	public static List<Cluster> cluster(final Samples points, final double eps, final int minPts,
			final int type) {

		final int pointCount = points.length()[0];
		List<Cluster> resultSet = new ArrayList<Cluster>();
		PointStatus[] visited = new PointStatus[pointCount];// 1-visited 2-noise

		for (int i = 0; i < pointCount; i++) {
			if (visited[i] != null) {
				continue;
			}
			// query neighborhood
			List<Integer> neighbors = new ArrayList<Integer>();// index of neighbors wrt points
			neighbors = neighborQuery(points, i, eps, pointCount, type);

			if (neighbors.size() < minPts) {
				visited[i] = PointStatus.NOISE;																								// cluster
			} else {// mark as noise
				Cluster cluster = new Cluster();
				resultSet.add(expandCluster(i, points, pointCount, neighbors, cluster, eps, minPts, type, visited));// expand	
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
			Cluster cluster, final double eps, final int minPts, final int type, PointStatus[] visited) {
		// add P to cluster C
		cluster.add(pid);
		visited[pid] = PointStatus.IN_CLUSTER;
		// for each point, expand neighbors
		for (int i = 0; i < neighbors.size(); i++) {
			Integer current = neighbors.get(i);
			PointStatus pStatus = visited[current];
			// only check non-visited points
			if (pStatus == null) {
				List<Integer> currentNeighbors = new ArrayList<Integer>();
				currentNeighbors = neighborQuery(points, current, eps, pointCount, type);
				if (currentNeighbors.size() >= minPts) {
					neighbors = joinList(neighbors,currentNeighbors);//update neighbors
				}
			}

			if (pStatus != PointStatus.IN_CLUSTER) {
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
	 * @param eps
	 * @param number
	 *            of total points
	 * @param type
	 *            of distance
	 * @return the List of neighbors
	 */
	protected static List<Integer> neighborQuery(Samples points, final int pid, final double eps, final int pointCount, final int type) {
		List<Integer> neighbors = new ArrayList<Integer>();
		for (int i = 0; i < pointCount; i++) {
			Point neighbor = points.getPointRef(i);//get actual point features vector
			// Neighbor-P Distance
			double distance = DistanceUtil.distance(neighbor, points.getPointRef(pid), type);
			if (distance <= eps) {
				neighbors.add(i);// add index instead of attributes
			}
		}
		return neighbors;
	}
	
	/**
     * Join two lists
     */
    private static List<Integer> joinList(final List<Integer> one, final List<Integer> two) {
    	int twosize = two.size();
        for (int i = 0; i < twosize; i++) {
            if (!one.contains(two.get(i))) {
                one.add(two.get(i));
            }
        }
        return one;
    }

}// end of DBSCANBasic
