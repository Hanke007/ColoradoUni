package cdb.exp.qc.analysis.ext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cdb.common.lang.ClusterHelper;
import cdb.common.lang.DistanceUtil;
import cdb.common.lang.FileUtil;
import cdb.common.lang.LoggerUtil;
import cdb.common.lang.StatisticParamUtil;
import cdb.common.model.Cluster;
import cdb.common.model.RegionAnomalyInfoVO;
import cdb.common.model.Samples;
import cdb.exp.qc.analysis.AbstractQcAnalysis;
import cdb.ml.clustering.KMeansPlusPlusUtil;

/**
 * 
 * @author Chao Chen
 * @version $Id: RegionAnmlyAnalysis.java, v 0.1 Nov 5, 2015 11:05:52 AM chench Exp $
 */
public class RegionAnmlyAnalysis extends AbstractQcAnalysis {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        //        clusteringAnal();
        //        categoryCountRegionAnal();
        categoryCountDayAnal();
    }

    public static void categoryCountRegionAnal() {
        String rootDir = "C:/Users/chench/Desktop/SIDS/SSMI/";
        String freqId = "n19v";
        int regionHeight = 8;
        int regionWeight = 8;
        int fContriNum = 2;

        String regnAnomlyFile = rootDir + "Anomaly/REG_" + freqId + '_' + regionHeight + '_'
                                + regionWeight;
        String[] lines = FileUtil.readLines(regnAnomlyFile);
        String[] LABELS = { "GradCol", "GradRow", "DffMean[-1]", "DffMean[+1]", "DffSd[-1]",
                            "DffSd[+1]", "Spactial: SumCorr", "Spactial: SumDff", "Mean", "Sd" };

        Map<Integer, Double[]> statRepo = new HashMap<Integer, Double[]>();
        for (String line : lines) {
            RegionAnomalyInfoVO regnAnmVO = RegionAnomalyInfoVO.parseOf(line);
            double[] fVals = new double[LABELS.length];
            for (int dIndx = 0; dIndx < LABELS.length; dIndx++) {
                fVals[dIndx] = regnAnmVO.getdPoint().getValue(dIndx);
            }

            int year = Integer.valueOf(regnAnmVO.getDateStr()) / 10000;
            Double[] stats = statRepo.get(year);
            if (stats == null) {
                stats = new Double[LABELS.length];
                Arrays.fill(stats, 0.0d);
                statRepo.put(year, stats);
            }

            for (int vIndx = 0; vIndx < fContriNum; vIndx++) {
                int maxIndx = StatisticParamUtil.indexOfAbsMaxNum(fVals);
                if (maxIndx == -1) {
                    break;
                } else {
                    fVals[maxIndx] = 0.0d;
                }
                stats[maxIndx]++;
            }
        }

        // output in Matlab fasion
        StringBuilder strBuilder = new StringBuilder();
        for (Integer key : statRepo.keySet()) {
            Double[] stats = statRepo.get(key);
            strBuilder.append(key);

            for (double val : stats) {
                strBuilder.append(',').append(val);
            }
            strBuilder.append('\n');
        }
        FileUtil.write(rootDir + "Anomaly/REG_MAT", strBuilder.toString());
    }

    public static void categoryCountDayAnal() {
        String rootDir = "C:/Users/chench/Desktop/SIDS/SSMI/";
        String freqId = "n19v";
        int regionHeight = 8;
        int regionWeight = 8;
        int fContriNum = 2;

        String regnAnomlyFile = rootDir + "Anomaly/REG_" + freqId + '_' + regionHeight + '_'
                                + regionWeight;
        String[] lines = FileUtil.readLines(regnAnomlyFile);
        String[] LABELS = { "GradCol", "GradRow", "DffMean[-1]", "DffMean[+1]", "DffSd[-1]",
                            "DffSd[+1]", "Spactial: SumCorr", "Spactial: SumDff", "Mean", "Sd" };

        Map<Integer, Double[]> dateRepo = new HashMap<Integer, Double[]>();
        for (String line : lines) {
            RegionAnomalyInfoVO regnAnmVO = RegionAnomalyInfoVO.parseOf(line);
            double[] fVals = new double[LABELS.length];
            for (int dIndx = 0; dIndx < LABELS.length; dIndx++) {
                fVals[dIndx] = regnAnmVO.getdPoint().getValue(dIndx);
            }

            int date = Integer.valueOf(regnAnmVO.getDateStr());
            Double[] stats = dateRepo.get(date);
            if (stats == null) {
                stats = new Double[LABELS.length];
                Arrays.fill(stats, 0.0d);
                dateRepo.put(date, stats);
            }

            for (int vIndx = 0; vIndx < fContriNum; vIndx++) {
                int maxIndx = StatisticParamUtil.indexOfAbsMaxNum(fVals);
                if (maxIndx == -1) {
                    break;
                } else {
                    fVals[maxIndx] = 0.0d;
                }
                stats[maxIndx]++;
            }
        }

        // by year
        Map<Integer, Double[]> statRepo = new HashMap<Integer, Double[]>();
        for (int date : dateRepo.keySet()) {
            Double[] fVals = dateRepo.get(date);

            int year = date / 10000;
            Double[] stats = statRepo.get(year);
            if (stats == null) {
                stats = new Double[LABELS.length];
                Arrays.fill(stats, 0.0d);
                statRepo.put(year, stats);
            }

            for (int vIndx = 0; vIndx < LABELS.length; vIndx++) {
                if (fVals[vIndx] != 0.0d) {
                    stats[vIndx]++;
                }
            }
        }

        // output in Matlab fasion
        StringBuilder strBuilder = new StringBuilder();
        for (Integer key : statRepo.keySet()) {
            Double[] stats = statRepo.get(key);
            strBuilder.append(key);

            for (double val : stats) {
                strBuilder.append(',').append(val);
            }
            strBuilder.append('\n');
        }
        FileUtil.write(rootDir + "Anomaly/REG_MAT", strBuilder.toString());
    }

    public static void clusteringAnal() {
        String rootDir = "C:/Users/chench/Desktop/SIDS/SSMI/";
        String freqId = "n19v";
        int regionHeight = 8;
        int regionWeight = 8;

        // clustering
        double alpha = 1.0;
        int maxIter = 1;
        int maxClusterNum = 10;

        String regnAnomlyFile = rootDir + "Anomaly/REG_" + freqId + '_' + regionHeight + '_'
                                + regionWeight;
        String[] lines = FileUtil.readLines(regnAnomlyFile);

        int dIndx = 0;
        Samples dSample = new Samples(lines.length, 12);
        for (String line : lines) {
            RegionAnomalyInfoVO regnAnmVO = RegionAnomalyInfoVO.parseOf(line);
            dSample.setPoint(dIndx++, regnAnmVO.getdPoint());
        }

        // clustering
        Cluster[] roughClusters = KMeansPlusPlusUtil.cluster(dSample, maxClusterNum, 20,
            DistanceUtil.SQUARE_EUCLIDEAN_DISTANCE);
        Cluster[] newClusters = ClusterHelper.mergeAdjacentCluster(dSample, roughClusters,
            DistanceUtil.SQUARE_EUCLIDEAN_DISTANCE, alpha, maxIter);

        for (Cluster cluster : newClusters) {
            LoggerUtil.info(logger, cluster.getCenter());
        }
    }

}
