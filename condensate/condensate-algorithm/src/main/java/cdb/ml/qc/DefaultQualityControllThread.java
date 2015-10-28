package cdb.ml.qc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

import cdb.common.lang.ClusterHelper;
import cdb.common.lang.DistanceUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.FileUtil;
import cdb.common.lang.LoggerUtil;
import cdb.dal.vo.RegionAnomalyInfoVO;
import cdb.dal.vo.RegionInfoVO;
import cdb.ml.clustering.Cluster;
import cdb.ml.clustering.KMeansPlusPlusUtil;
import cdb.ml.clustering.Samples;

/**
 * 
 * @author Chao Chen
 * @version $Id: DefaultQualityControllThread.java, v 0.1 Oct 27, 2015 10:22:58 AM chench Exp $
 */
public class DefaultQualityControllThread extends AbstractQualityControllThread {
    /** the tolerance of the merging process*/
    private double alpha                   = 2.0;
    /** the maximum number of iterations in clustering process*/
    private int    maxIter                 = 20;
    /** the maximum number of the resulting clusters*/
    private int    maxClusterNum           = 50;
    /** the potential ratio of the malicious data*/
    private double potentialMaliciousRatio = 0.15;
    /** the row numbers of every region*/
    private int    regionHeight            = 1;
    /** the column numbers of every region*/
    private int    regionWeight            = 1;

    /** mutex object*/
    private static Object                    ANOMALY_MUTEX = new Object();
    /** the buffer of the Region anomaly object*/
    private static List<RegionAnomalyInfoVO> raInfoBuffer  = new ArrayList<RegionAnomalyInfoVO>();

    protected static void save(List<RegionAnomalyInfoVO> raArr, String resultFile) {
        synchronized (ANOMALY_MUTEX) {
            raInfoBuffer.addAll(raArr);

            if (raInfoBuffer.size() >= 1000 * 1000) {
                StringBuilder strBuffer = new StringBuilder();
                for (RegionAnomalyInfoVO one : raInfoBuffer) {
                    strBuffer.append(one.toString()).append('\n');
                }
                raInfoBuffer.clear();
                FileUtil.writeAsAppendWithDirCheck(resultFile, strBuffer.toString());
            }
        }
    }

    public static void flush(String resultFile) {
        synchronized (ANOMALY_MUTEX) {
            StringBuilder strBuffer = new StringBuilder();
            for (RegionAnomalyInfoVO one : raInfoBuffer) {
                strBuffer.append(one.toString()).append('\n');
            }
            raInfoBuffer.clear();
            FileUtil.writeAsAppendWithDirCheck(resultFile, strBuffer.toString());
        }
    }

    /**
     * @param alpha                     the tolerance of the merging process
     * @param maxIter                   the maximum number of iterations in clustering process
     * @param maxClusterNum             the maximum number of the resulting clusters
     * @param potentialMaliciousRatio   the potential ratio of the malicious data
     * @param regionHeight              the row numbers of every region
     * @param regionWeight              the column numbers of every region
     */
    public DefaultQualityControllThread(double alpha, int maxIter, int maxClusterNum,
                                        double potentialMaliciousRatio, int regionHeight,
                                        int regionWeight) {
        super();
        this.alpha = alpha;
        this.maxIter = maxIter;
        this.maxClusterNum = maxClusterNum;
        this.potentialMaliciousRatio = potentialMaliciousRatio;
        this.regionHeight = regionHeight;
        this.regionWeight = regionWeight;
    }

    /** 
     * @see cdb.ml.qc.AbstractQualityControllThread#run()
     */
    @Override
    public void run() {
        Entry<String, List<String>> dEntry = null;
        while ((dEntry = task()) != null) {
            String resultFile = dEntry.getKey();
            List<String> fileNames = dEntry.getValue();

            List<RegionAnomalyInfoVO> raArr = new ArrayList<RegionAnomalyInfoVO>();
            for (String fileName : fileNames) {
                raArr.addAll(malicousDetection(fileName));
            }
            save(raArr, resultFile);
        }
    }

    protected List<RegionAnomalyInfoVO> malicousDetection(String fileName) {
        // read objects
        Queue<RegionInfoVO> regnList = new LinkedList<RegionInfoVO>();
        readRegionVO(fileName, regnList);
        if (regnList.isEmpty()) {
            return new ArrayList<RegionAnomalyInfoVO>();
        } else if (regnList.size() < maxClusterNum * 50) {
            LoggerUtil.warn(logger, "Lack of data : " + regnList.size() + "\t"
                                    + fileName.substring(fileName.lastIndexOf('/')));
            return new ArrayList<RegionAnomalyInfoVO>();
        }

        // making clustering samples
        RegionInfoVO pivot = regnList.peek();
        int pDimen = 6 + pivot.getDistribution().dimension() + pivot.getGradCol().dimension()
                     + pivot.getGradRow().dimension() + pivot.gettGradCon().dimension()
                     + pivot.getsCorrCon().dimension() + pivot.getsDiffCon().dimension();
        int rRIndx = pivot.getrIndx();
        int cRIndx = pivot.getcIndx();
        Samples dataSample = new Samples(regnList.size(), pDimen);
        List<String> regnDateStr = new ArrayList<String>();
        tranformRegionVO(regnList, dataSample, regnDateStr);

        // clustering 
        Cluster[] roughClusters = KMeansPlusPlusUtil.cluster(dataSample, maxClusterNum, 20,
            DistanceUtil.SQUARE_EUCLIDEAN_DISTANCE);
        Cluster[] newClusters = ClusterHelper.mergeAdjacentCluster(dataSample, roughClusters,
            DistanceUtil.SQUARE_EUCLIDEAN_DISTANCE, alpha, maxIter);

        // compute potential error data
        return computingPotentialError(fileName, newClusters, regnDateStr, rRIndx, cRIndx);
    }

    protected void readRegionVO(String fileName, Queue<RegionInfoVO> regnList) {
        if (!FileUtil.exists(fileName)) {
            return;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileName));

            String line = null;
            while ((line = reader.readLine()) != null) {
                RegionInfoVO regnVO = RegionInfoVO.parseOf(line);
                regnList.add(regnVO);
            }

        } catch (FileNotFoundException e) {
            ExceptionUtil.caught(e, "无法找到对应的加载文件: " + fileName);
        } catch (IOException e) {
            ExceptionUtil.caught(e, "读取文件发生异常，校验文件格式");
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    protected void tranformRegionVO(Queue<RegionInfoVO> regnList, Samples dataSample,
                                    List<String> regnDateStr) {
        int dSeq = 0;
        RegionInfoVO one = null;
        while ((one = regnList.poll()) != null) {
            int pSeq = 0;

            // distribution information
            for (double distrbtnVal : one.getDistribution()) {
                dataSample.setValue(dSeq, pSeq++, distrbtnVal);
            }

            // gradient along row
            for (double gradRowVal : one.getGradRow()) {
                dataSample.setValue(dSeq, pSeq++, gradRowVal);
            }

            // gradient along column
            for (double gradColVal : one.getGradCol()) {
                dataSample.setValue(dSeq, pSeq++, gradColVal);
            }

            // Contextual: temporal gradients
            for (double tGradConVal : one.gettGradCon()) {
                dataSample.setValue(dSeq, pSeq++, tGradConVal);
            }

            // Contextual: spatial correlations
            for (double sCorrConVal : one.getsCorrCon()) {
                dataSample.setValue(dSeq, pSeq++, sCorrConVal);
            }

            // Contextual: spatial differences
            for (double sDiffConVal : one.getsDiffCon()) {
                dataSample.setValue(dSeq, pSeq++, sDiffConVal);
            }

            dataSample.setValue(dSeq, pSeq++, one.getEntropy());
            dataSample.setValue(dSeq, pSeq++, one.getGradMean());
            dataSample.setValue(dSeq, pSeq++, one.getMean());
            dataSample.setValue(dSeq, pSeq++, one.getSd());
            dataSample.setValue(dSeq, pSeq++, one.getrIndx());
            dataSample.setValue(dSeq, pSeq++, one.getcIndx());

            regnDateStr.add(one.getDateStr());
            dSeq++;
        }
    }

    protected List<RegionAnomalyInfoVO> computingPotentialError(String fileName,
                                                                Cluster[] newClusters,
                                                                List<String> regnDateStr,
                                                                int rRIndx, int cRIndx) {
        int clusterNum = newClusters.length;
        double[] sizeTable = new double[clusterNum];
        for (int i = 0; i < clusterNum; i++) {
            sizeTable[i] = newClusters[i].getList().size();
        }
        LoggerUtil.info(logger, fileName.substring(fileName.lastIndexOf('/'))
                                + " resulting clusters: " + Arrays.toString(sizeTable));

        int curNum = 0;
        int totalNum = regnDateStr.size();
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
                raVO.setDateStr(regnDateStr.get(dIndx));
                raVO.setHeight(regionHeight);
                raVO.setWidth(regionWeight);
                raVO.setX(rRIndx * regionHeight);
                raVO.setY(cRIndx * regionWeight);
                raArr.add(raVO);
            }
        }

        return raArr;
    }

    /**
     * find the index of the data with the minimum value
     *      
     * @param sizeTable   the array of distance
     * @return          the index of the data  with the maximum anomaly scores
     */
    protected int findMinimum(double[] sizeTable) {
        double min = Double.MAX_VALUE;
        int pivot = -1;
        // ignoring side effects
        for (int i = 0; i < sizeTable.length; i++) {
            double val = sizeTable[i];

            if (min > val) {
                min = val;
                pivot = i;
            }
        }

        sizeTable[pivot] = Double.MAX_VALUE;
        return pivot;
    }

}
