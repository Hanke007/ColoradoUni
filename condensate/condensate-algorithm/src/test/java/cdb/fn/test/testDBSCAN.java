package cdb.fn.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import cdb.common.lang.DistanceUtil;
import cdb.common.model.Cluster;
import cdb.common.model.Point;
import cdb.common.model.Samples;
import cdb.ml.clustering.DBSCANBasic;

public class testDBSCAN {

	private static BufferedReader br;

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		final int eps = 6;
		final int minPts = 6;
		final int type = DistanceUtil.SQUARE_EUCLIDEAN_DISTANCE;
		final int testSize = 178;
		final int testDim = 2;
		final Samples points = new Samples(testSize, testDim);

		br = new BufferedReader(new FileReader("/Users/mira67/Documents/testdata.txt"));
		
		initSample(points, br, testSize, testDim);
		
		//clustering
		List<Cluster> clusters = DBSCANBasic.cluster(points, eps, minPts, type);
		//print out results
		for (int i = 0; i < clusters.size(); i++) {
			System.out.println("Cluster " + i + " :");
			System.out.println(Arrays.toString(clusters.get(i).getList().toArray()));
		}
		
	}
	
	/**
     * Initialize sample data
     * http://people.cs.nctu.edu.tw/~rsliang/dbscan/testdatagen.html
     */
    public static void initSample(Samples points, BufferedReader br, final int testSize, final int testDim) {

    	for (int k = 0; k < testSize; k++) {
    		String[] st = null;
    		Point point = new Point(2);//need new, otherwise replace with same value
			try {
				st = br.readLine().trim().split("\t");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			for (int j = 0; j < testDim; j++) {
				point.setValue(j, Double.parseDouble(st[j]));
			}
			points.setPoint(k, point);
		}
//    	
//		//test
//		for (int i = 0; i < testSize; i++) {
//			System.out.println(i);
//			System.out.println(points.getPoint(i).getValue(0));
//		}
//		
    }
}

