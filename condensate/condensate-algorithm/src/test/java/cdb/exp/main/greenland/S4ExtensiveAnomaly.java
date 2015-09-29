package cdb.exp.main.greenland;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cdb.common.lang.ClusterLocHelper;
import cdb.common.lang.LoggerUtil;
import cdb.common.lang.MatrixFileUtil;
import cdb.common.lang.StatisticParamUtil;
import cdb.common.lang.VisualizationUtil;
import cdb.dal.vo.DenseMatrix;
import cdb.dal.vo.Location;
import cdb.ml.anomaly.NearestNeighborOutlierDetection;
import cdb.ml.clustering.Point;
import cdb.service.dataset.NetCDFDtProc;

/**
 * 
 * @author Chao Chen
 * @version $Id: S4ExtensiveAnomaly.java, v 0.1 Sep 22, 2015 1:40:08 PM chench Exp $
 */
public class S4ExtensiveAnomaly extends AbstractGreenLandAnalysis {

    public final static int NEAREST_NEIGHBOR_NUM = 5;

    public final static int ANOMALY_NUM          = 1;

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        case1();
    }

    public static void case1() {
        String[] filePatternSets = {
                "C:/Users/chench/Desktop/SIDS/2000/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2001/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2002/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2003/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2004/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2005/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2006/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2007/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2008/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2009/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2010/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2011/GLSMD25E2_\\d{8}_v01r01.nc",
                "C:/Users/chench/Desktop/SIDS/2012/GLSMD25E2_\\d{8}_v01r01.nc" };

        LoggerUtil.info(logger, "1. loading dataset.");
        List<DenseMatrix> seralData = new ArrayList<DenseMatrix>();
        List<String> fileAssigmnt = new ArrayList<String>();
        loadingdataset(filePatternSets, seralData, fileAssigmnt);

        LoggerUtil.info(logger, "2. loading spatial clustering.");
        String clstFile = ROOT_DIR + "Clustering/kmean_5";
        List<Location> oneCluster = loadingSpatialClusterResulting(clstFile, 1);

        anomalyDetection(seralData, oneCluster, fileAssigmnt, 365);
    }

    public static void loadingdataset(String[] filePatternSets, List<DenseMatrix> seralData,
                                      List<String> fileAssigmnt) {
        // tailor the data
        int[] rowIncluded = new int[100];
        int[] colIncluded = new int[100];
        for (int i = 0; i < rowIncluded.length; i++) {
            rowIncluded[i] = 370 + i;
            colIncluded[i] = 260 + i;
        }

        MatrixFileUtil.read(filePatternSets, seralData, fileAssigmnt, new NetCDFDtProc(),
            rowIncluded, colIncluded, 1.0d);
    }

    public static List<Location> loadingSpatialClusterResulting(String clstFile, int oneSeq) {
        List<List<Location>> locSet = new ArrayList<List<Location>>();
        ClusterLocHelper.readLoc(clstFile, locSet);
        return locSet.get(oneSeq);
    }

    public static void anomalyDetection(List<DenseMatrix> seralData, List<Location> oneCluster,
                                        List<String> fileAssigmnt, int repeatCycle) {
        // save data in specific cluster
        LoggerUtil.info(logger, "3. compute statistical parameters.");
        int mNum = seralData.size();
        for (int i = 0; i < mNum; i++) {
            DenseMatrix curMatrix = seralData.get(i);
            DenseMatrix newMatrix = new DenseMatrix(curMatrix.getRowNum(), curMatrix.getColNum(),
                Double.NaN);

            for (Location loc : oneCluster) {
                newMatrix.setVal(loc.x(), loc.y(), curMatrix.getVal(loc.x(), loc.y()));
            }
            seralData.set(i, newMatrix);
        }
        double[] means = StatisticParamUtil.meanSeqTimeseries(seralData);
        double[] sds = StatisticParamUtil.sdSeqTimeseries(seralData);

        // transfer data to Samples formulation
        LoggerUtil.info(logger, "4. clustering time-series data.");
        int cycles = mNum / repeatCycle + 1;
        Point[][] samples = new Point[repeatCycle][cycles];
        for (int i = 0; i < mNum; i++) {
            int dateInYear = i % repeatCycle;
            int seqInYear = i / repeatCycle;
            double mean = means[i];
            double sd = sds[i];

            samples[dateInYear][seqInYear] = new Point(mean, sd);
        }

        // out-lier detection 
        LoggerUtil.info(logger, "5. detecting outliers.");
        int[][] anomalies = new int[repeatCycle][0];
        NearestNeighborOutlierDetection dectector = new NearestNeighborOutlierDetection();
        for (int dateSeq = 0; dateSeq < repeatCycle; dateSeq++) {
            anomalies[dateSeq] = dectector.detect(samples[dateSeq], NEAREST_NEIGHBOR_NUM,
                ANOMALY_NUM);
        }

        // visualization
        visualization(means, anomalies, repeatCycle);
    }

    protected static void visualization(double[] means, int[][] anomalies, int repeatCycle) {
        Map<String, List<Point>> pltContext = new HashMap<String, List<Point>>();
        for (int dateSeq = 0; dateSeq < repeatCycle; dateSeq++) {
            for (int anomalyIndx : anomalies[dateSeq]) {
                String key = String.valueOf(anomalyIndx + 1990);
                List<Point> value = pltContext.get(key);
                if (value == null) {
                    value = new ArrayList<Point>();
                    pltContext.put(key, value);
                }

                double meanVal = means[anomalyIndx * repeatCycle + dateSeq];
                Point point = new Point(dateSeq, meanVal);
                value.add(point);
            }
        }
        VisualizationUtil.gnuLinepoint(pltContext, ROOT_DIR + "Statistcs/Automatic2/");
    }
}
