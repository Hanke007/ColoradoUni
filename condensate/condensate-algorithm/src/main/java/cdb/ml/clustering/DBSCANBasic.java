package cdb.ml.clustering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.ListUtils;
import org.springframework.util.StopWatch;

import cdb.common.lang.DistanceUtil;
import cdb.common.lang.LoggerUtil;
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
	public static List<Cluster> cluster(final Samples points, double eps, final int minPts,
			final int type, final double outlierPercent) {

		final int pointCount = points.length()[0];
		List<Cluster> resultSet = new ArrayList<Cluster>();
		PointStatus[] visited = new PointStatus[pointCount];// 1-visited 2-noise
		
		//auto-select parameters
		eps = parameterSelection(points, minPts, pointCount, type, outlierPercent);

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
			Cluster cluster, double eps, final int minPts, final int type, PointStatus[] visited) {
		// add P to cluster C
		cluster.add(pid);
		visited[pid] = PointStatus.IN_CLUSTER;

		//Collections.sort(neighbors);
		// for each point, expand neighbors
		for (int i = 0; i < neighbors.size(); i++) {
			Integer current = neighbors.get(i);
			PointStatus pStatus = visited[current];
			// only check non-visited points
			if (pStatus == null) {
				List<Integer> currentNeighbors = new ArrayList<Integer>();
				
				currentNeighbors = neighborQuery(points, current, eps, pointCount, type);
				
				if (currentNeighbors.size() >= minPts) {
					Set<Integer> newSet = new LinkedHashSet<Integer>(neighbors);
					newSet.addAll(currentNeighbors);
					neighbors = new ArrayList<Integer>(newSet);
					//neighbors = joinList(neighbors,currentNeighbors);//update neighbors
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
     * Parameter Selection
     * k = 4, minPts?
     * How to select parameters for high dimension dataset?
     */
	protected static double parameterSelection(Samples points, final double minPts, final int pointCount, final int type, final double outlierPercent) {
		
		List<Double> distSorted = new ArrayList<Double>();
		
		double eps = 0;
		for (int i = 0; i < pointCount; i++) {
			Point current = points.getPointRef(i);
			List<Double> distances = new ArrayList<Double>();
			
			
			
			for (int j = 0; j < pointCount; j++) {
				Point neighbor = points.getPointRef(j);//get actual point features vector
				// Neighbor-P Distance
				double distance = DistanceUtil.distance(current, neighbor, type);
				distances.add(distance);
			}
			
			StopWatch stopWatch = null;
			stopWatch = new StopWatch();
	        stopWatch.start();
			
			//get the distance at minPts
			Collections.sort(distances);
			distSorted.add(distances.get((int) (minPts)));
			
			stopWatch.stop();
			System.out.println("DIST TIME SPENDED: " + stopWatch.getTotalTimeMillis() / 1000.0);
		}
		
		//get eps: sort and cut at percentage of outliers
		Collections.sort(distSorted, Collections.reverseOrder());
		eps = distSorted.get((int) Math.round(pointCount*outlierPercent));
		
		return eps;
	}

}// end of DBSCANBasic
