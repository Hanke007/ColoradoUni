package cdb.exp.analysis;

import java.util.ArrayList;
import java.util.List;

import cdb.common.lang.ClusterLocHelper;
import cdb.common.lang.VisualizationUtil;
import cdb.dal.vo.DenseMatrix;
import cdb.dal.vo.Location;
import cdb.service.dataset.DatasetProc;
import cdb.service.dataset.NetCDFDtProc;

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
        ClusterLocHelper.readLoc(clstFile, locSet);
        for (List<Location> locOne : locSet) {
            lIndex++;
            for (Location loc : locOne) {
                dMatrix.setVal(loc.x(), loc.y(), lIndex);
            }
        }

        VisualizationUtil.gnuHeatmap(dMatrix, ROOT_DIR + "sd");
    }

    public static void case2() {
        String clstFile = ROOT_DIR + "Clustering/kmean_5";

        // read clustering result
        int lIndex = -1;
        DenseMatrix dMatrix = new DenseMatrix(100, 100);
        List<List<Location>> locSet = new ArrayList<List<Location>>();
        ClusterLocHelper.readLoc(clstFile, locSet);
        for (List<Location> locOne : locSet) {
            lIndex++;
            for (Location loc : locOne) {
                dMatrix.setVal(loc.x(), loc.y(), lIndex);
            }
        }

        //         read matrix 
        String fileName = "C:\\Users\\chench\\Desktop\\SIDS\\2012\\GLSMD25E2_20120901_v01r01.nc";
        int[] rowIncluded = new int[100];
        int[] colIncluded = new int[100];
        for (int i = 0; i < rowIncluded.length; i++) {
            rowIncluded[i] = 370 + i;
            colIncluded[i] = 260 + i;
        }
        DatasetProc dProc = new NetCDFDtProc();
        DenseMatrix matrix = dProc.read(fileName, rowIncluded, colIncluded);
        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                double val = matrix.getVal(x, y);

                if (val == 10000) {
                    dMatrix.setVal(x, y, 0);

                    if (locSet.get(1).indexOf(new Location(x, y)) != -1) {
                        System.out.println(x + "," + y);
                    }
                }
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
        ClusterLocHelper.readLoc(clstFile, locSet);
        for (List<Location> locOne : locSet) {
            lIndex++;
            for (Location loc : locOne) {
                dMatrix.setVal(loc.x(), loc.y(), lIndex);
            }
        }

        VisualizationUtil.gnuHeatmap(dMatrix, ROOT_DIR + "sd");
    }
}
