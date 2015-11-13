package cdb.exp.analysis;

import java.util.ArrayList;
import java.util.List;

import cdb.common.lang.ClusterHelper;
import cdb.common.lang.VisualizationUtil;
import cdb.common.model.DenseMatrix;
import cdb.common.model.Location;

/**
 * 
 * @author Chao Chen
 * @version $Id: ClusterAnalysis.java, v 0.1 Sep 16, 2015 9:56:27 AM chench Exp $
 */
public class ClusterAnalysis {

    public final static String ROOT_DIR = "C:/Users/chench/Desktop/SIDS/";

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        case1();
        //        case2();
    }

    public static void case1() {
        String clstFile = ROOT_DIR + "Clustering/kmean_5";

        // read clustering result
        int lIndex = -1;
        DenseMatrix dMatrix = new DenseMatrix(100, 100);
        List<List<Location>> locSet = new ArrayList<List<Location>>();
        ClusterHelper.readLoc(clstFile, locSet);
        for (List<Location> locOne : locSet) {
            lIndex++;
            for (Location loc : locOne) {
                dMatrix.setVal(loc.x(), loc.y(), lIndex);
            }
        }

        VisualizationUtil.gnuHeatmap(dMatrix, ROOT_DIR + "sd");
    }

    public static void case3() {
        String clstFile = ROOT_DIR + "Clustering/Hierarchy_5";

        // read clustering result
        int lIndex = -1;
        DenseMatrix dMatrix = new DenseMatrix(2559, 1);
        List<List<Location>> locSet = new ArrayList<List<Location>>();
        ClusterHelper.readLoc(clstFile, locSet);
        for (List<Location> locOne : locSet) {
            lIndex++;
            for (Location loc : locOne) {
                dMatrix.setVal(loc.x(), loc.y(), lIndex);
            }
        }

        VisualizationUtil.gnuHeatmap(dMatrix, ROOT_DIR + "sd");
    }
}
