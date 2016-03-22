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
//import cdb.ml.clustering.STDBSCANKut;

public class testDBSCAN {

	private static BufferedReader br;

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		final double eps1 = 4;
		final double eps2 = 10;
		final int minPts = 6;
		final double delta = eps2;
		final int type = DistanceUtil.SQUARE_EUCLIDEAN_DISTANCE;
		final int testSize = 16*16;
		final int xlim = 16;
		final int ylim = 16;
		final int testDim = 1;
		final Samples points = new Samples(testSize, testDim);

		/*basic DBSCAN test data*/
		//br = new BufferedReader(new FileReader("/Users/mira67/Documents/testdata.txt"));
		
		/*Kut's ST-DBSCAN test data*/
		br = new BufferedReader(new FileReader("/Users/mira67/Documents/ricedata.txt"));
		
		initSample(points, br, testSize, testDim, true,xlim, ylim);
		
		//clustering with basic DBSCAN
		List<Cluster> clusters = DBSCANBasic.cluster(points, eps1, minPts, type);
		
		//clustering with Kut ST-DBSCAN
		//List<Cluster> clusters = STDBSCANKut.cluster(points, eps1, eps2, minPts, delta, type);
		
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
    public static void initSample(Samples points, BufferedReader br, final int testSize, final int testDim, boolean spatial, int xlim, int ylim) {

    	if (!spatial) {//non-spatial data
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
    	} else {
    		int x = 0, y = 0;//coordinates
    		int dataId = 0;
        	for (int k = 0; k < xlim; k++) {
        		String[] st = null;
    			try {
    				st = br.readLine().trim().split("\t");
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} 
    			for (int j = 0; j < ylim; j++) {
            		Point point = new Point(1,k,j);//need new, otherwise replace with same value
    				point.setValue(0, Double.parseDouble(st[j]));
        			points.setPoint(dataId, point);
        			dataId++;
    			}
    		}
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

