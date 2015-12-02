package cdb.ml.qc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.stat.StatUtils;

import cdb.common.lang.ClusterHelper;
import cdb.common.lang.DistanceUtil;
import cdb.common.lang.FileUtil;
import cdb.common.lang.LoggerUtil;
import cdb.common.model.Cluster;
import cdb.common.model.DenseMatrix;
import cdb.common.model.RegionAnomalyInfoVO;
import cdb.common.model.Samples;
import cdb.dal.file.DatasetProc;
import cdb.ml.clustering.KMeansPlusPlusUtil;

/**
 * 
 * @author Chao Chen
 * @version $Id: SpatialFeatureQualityControllThread.java, v 0.1 Dec 2, 2015 10:07:05 AM chench Exp $
 */
public class SpatialFeatureQualityControllThread extends DefaultQualityControllThread {
    /** data-set processer*/
    private DatasetProc dProc;

    /**
     * @param configFileName        the file path of the configuration file
     */
    public SpatialFeatureQualityControllThread(String configFileName, DatasetProc dProc) {
        super(configFileName);
        this.dProc = dProc;

    }

    /** 
     * @see cdb.ml.qc.DefaultQualityControllThread#innerDectectoin(java.lang.String)
     */
    @Override
    protected List<RegionAnomalyInfoVO> innerDectectoin(String fileName) {
        if (!FileUtil.exists(fileName)) {
            return new ArrayList<RegionAnomalyInfoVO>();
        }
        DenseMatrix dMatrix = dProc.read(fileName);
        int dCount = dMatrix.count();

        // normalize data
        List<RegionAnomalyInfoVO> dArr = new ArrayList<RegionAnomalyInfoVO>();
        Samples dataSample = new Samples(dCount, 1);
        normalizeStep(dMatrix, dArr, dataSample, dCount);

        // detect anomaly

        return discoverPatternStep(dataSample, dArr, fileName);
    }

    protected void normalizeStep(DenseMatrix dMatrix, List<RegionAnomalyInfoVO> dArr,
                                 Samples dataSample, int dCount) {
        int rowNum = dMatrix.getRowNum();
        int colNum = dMatrix.getColNum();
        double[] featureVals = new double[dCount];

        int dSeq = 0;
        for (int row = 0; row < rowNum; row++) {
            for (int col = 0; col < colNum; col++) {
                double val = dMatrix.getVal(row, col);
                if (Double.isNaN(val)) {
                    continue;
                }

                RegionAnomalyInfoVO regnInfoVO = new RegionAnomalyInfoVO();
                regnInfoVO.setX(row);
                regnInfoVO.setY(col);
                dArr.add(regnInfoVO);
                featureVals[dSeq++] = val;
            }
        }

        dSeq = 0;
        featureVals = StatUtils.normalize(featureVals);
        for (double val : featureVals) {
            dataSample.setValue(dSeq++, 0, val);
        }
    }

    protected List<RegionAnomalyInfoVO> discoverPatternStep(Samples dataSample,
                                                            List<RegionAnomalyInfoVO> dArr,
                                                            String fileName) {
        Cluster[] roughClusters = KMeansPlusPlusUtil.cluster(dataSample, maxClusterNum, 20,
            DistanceUtil.SQUARE_EUCLIDEAN_DISTANCE);
        Cluster[] newClusters = ClusterHelper.mergeAdjacentCluster(dataSample, roughClusters,
            DistanceUtil.SQUARE_EUCLIDEAN_DISTANCE, alpha, maxIter);

        int clusterNum = newClusters.length;
        double[] sizeTable = new double[clusterNum];
        for (int i = 0; i < clusterNum; i++) {
            sizeTable[i] = newClusters[i].getList().size();
        }

        String fileAlias = fileName.substring(fileName.lastIndexOf('/'));
        String dateStrg = fileAlias.substring(6, 14);
        LoggerUtil.info(logger, fileAlias + " resulting clusters: " + Arrays.toString(sizeTable));

        int totalNum = dataSample.length()[0];
        int curNum = 0;
        int newClusterNum = newClusters.length;
        List<RegionAnomalyInfoVO> raArr = new ArrayList<RegionAnomalyInfoVO>();
        for (int i = 0; i < newClusterNum; i++) {
            Cluster cluster = newClusters[findMinimum(sizeTable)];

            curNum += cluster.getList().size();
            if (curNum > totalNum * potentialMaliciousRatio) {
                break;
            }

            // result presentation
            for (int dIndx : cluster.getList()) {
                RegionAnomalyInfoVO raVO = new RegionAnomalyInfoVO();
                raVO.setX(dArr.get(dIndx).getX());
                raVO.setY(dArr.get(dIndx).getY());
                raVO.setDateStr(dateStrg);
                raVO.setdPoint(dataSample.getPointRef(dIndx));
                raArr.add(raVO);
            }
        }

        return raArr;
    }

}
