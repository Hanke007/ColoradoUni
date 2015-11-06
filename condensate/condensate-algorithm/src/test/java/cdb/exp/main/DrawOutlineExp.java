package cdb.exp.main;

import java.util.ArrayList;
import java.util.List;

import cdb.common.lang.SerializeUtil;
import cdb.common.lang.VisualizationUtil;
import cdb.common.model.DenseMatrix;
import cdb.common.model.Location;
import cdb.common.model.Point;
import cdb.ml.anomaly.AnomalyDetection;
import cdb.ml.anomaly.NearestNeighborOutlierDetection;

/**
 * 
 * @author Chao Chen
 * @version $Id: DrawOutlineExp.java, v 0.1 Oct 5, 2015 3:22:57 PM chench Exp $
 */
public class DrawOutlineExp {

    /** the root directory of the dataset*/
    protected final static String ROOT_DIR   = "C:/Users/chench/Desktop/SIDS/";
    /** frequency identity*/
    protected final static String FREQNCY_ID = "s19h";

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        case1();
    }

    public static void case1() {
        String fileName = ROOT_DIR + "Condensate/sd_198803_" + FREQNCY_ID + ".OBJ";
        DenseMatrix sd = (DenseMatrix) SerializeUtil.readObject(fileName);

        int rowNum = sd.getRowNum();
        int colNum = sd.getColNum();
        List<Point> anomly = new ArrayList<Point>();
        List<Location> locs = new ArrayList<Location>();
        for (int row = 0; row < rowNum; row++) {
            for (int col = 0; col < colNum; col++) {
                double val = sd.getVal(row, col);
                if (val < 3.5) {
                    sd.setVal(row, col, 10);
                    anomly.add(new Point(row, col));
                    locs.add(new Location(row, col));
                } else {
                    sd.setVal(row, col, 0);
                }
            }
        }

        AnomalyDetection detector = new NearestNeighborOutlierDetection();
        int[] anIndx = detector.detect(anomly.toArray(new Point[anomly.size()]), 30, 200);

        for (int indx : anIndx) {
            Location loc = locs.get(indx);
            sd.setVal(loc.x(), loc.y(), 0);
        }

        VisualizationUtil.gnuHeatmap(sd, ROOT_DIR + "mean");
    }

}
