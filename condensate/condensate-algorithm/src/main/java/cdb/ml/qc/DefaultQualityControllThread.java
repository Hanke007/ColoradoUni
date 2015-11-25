package cdb.ml.qc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

import cdb.common.lang.ClusterHelper;
import cdb.common.lang.ConfigureUtil;
import cdb.common.lang.DistanceUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.FileUtil;
import cdb.common.lang.LoggerUtil;
import cdb.common.lang.StringUtil;
import cdb.common.model.Cluster;
import cdb.common.model.Point;
import cdb.common.model.RegionAnomalyInfoVO;
import cdb.common.model.RegionInfoVO;
import cdb.common.model.Samples;
import cdb.ml.clustering.KMeansPlusPlusUtil;

/**
 * 
 * @author Chao Chen
 * @version $Id: DefaultQualityControllThread.java, v 0.1 Oct 27, 2015 10:22:58 AM chench Exp $
 */
public class DefaultQualityControllThread extends AbstractQualityControllThread {
    /** the tolerance of the merging process*/
    private double  alpha                   = 2.0;
    /** the maximum number of iterations in clustering process*/
    private int     maxIter                 = 20;
    /** the maximum number of the resulting clusters*/
    private int     maxClusterNum           = 50;
    /** the potential ratio of the malicious data*/
    private double  potentialMaliciousRatio = 0.15;
    /** the row numbers of every region*/
    private int     regionHeight            = 1;
    /** the column numbers of every region*/
    private int     regionWeight            = 1;
    /** pivot to indicate whether to save the processed data*/
    private boolean needSaveData            = false;
    /** filter category*/
    private String  filterCategory          = null;

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
                                        int regionWeight, boolean needSaveData) {
        super();
        this.alpha = alpha;
        this.maxIter = maxIter;
        this.maxClusterNum = maxClusterNum;
        this.potentialMaliciousRatio = potentialMaliciousRatio;
        this.regionHeight = regionHeight;
        this.regionWeight = regionWeight;
        this.needSaveData = needSaveData;
    }

    /**
     * @param configFileName        the file path of the configuration file
     */
    public DefaultQualityControllThread(String configFileName) {
        super();
        Properties properties = ConfigureUtil.read(configFileName);
        this.alpha = Double.valueOf(properties.getProperty("ALPHA"));
        this.maxIter = Integer.valueOf(properties.getProperty("MAX_ITERATION"));
        this.maxClusterNum = Integer.valueOf(properties.getProperty("MAX_CLUSTER_NUM"));
        this.potentialMaliciousRatio = Double
            .valueOf(properties.getProperty("ESTIMATED_RATIO_OF_RARE"));
        this.regionHeight = Integer.valueOf(properties.getProperty("REGION_HEIGHT"));
        this.regionWeight = Integer.valueOf(properties.getProperty("REGION_WEIGHT"));

        this.filterCategory = properties.getProperty("DATA_FILTERING_CATEGORY");
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
                raArr.addAll(innerDectectoin(fileName));
            }
            save(raArr, resultFile);
        }
    }

    /**
     * pattern detection function
     * @param fileName  the file to store the data
     * @return
     */
    protected List<RegionAnomalyInfoVO> innerDectectoin(String fileName) {
        List<RegionAnomalyInfoVO> resultArr = new ArrayList<RegionAnomalyInfoVO>();
        try {
            // load features for every sample
            Queue<RegionInfoVO> regnList = readRegionInfoStep(fileName);
            if (regnList.isEmpty()) {
                return new ArrayList<RegionAnomalyInfoVO>();
            } else if (regnList.size() < maxClusterNum * 50) {
                LoggerUtil.warn(logger, "Lack of data : " + regnList.size() + "\t"
                                        + fileName.substring(fileName.lastIndexOf('/')));
                return new ArrayList<RegionAnomalyInfoVO>();
            }

            // making clustering samples
            RegionInfoVO pivot = regnList.peek();
            //        int pDimen = 6 + pivot.getDistribution().dimension() + pivot.getGradCol().dimension()
            //                     + pivot.getGradRow().dimension() + pivot.gettGradCon().dimension()
            //                     + pivot.getsCorrCon().dimension() + pivot.getsDiffCon().dimension();
            int pDimen = 12;
            int rRIndx = pivot.getrIndx();
            int cRIndx = pivot.getcIndx();
            Samples dataSample = new Samples(regnList.size(), pDimen);
            List<String> regnDateStr = new ArrayList<String>();
            QualityControllHelper.normalizeFeatures(dataSample, regnList, regnDateStr,
                filterCategory);
            if (needSaveData) {
                persistFeatureStep(fileName, dataSample, regnDateStr);
            }

            resultArr = discoverPatternStep(dataSample, fileName, regnDateStr, rRIndx, cRIndx);
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "Date format parsing error.");
        }

        // rare pattern detection
        return resultArr;
    }

    protected void persistFeatureStep(String fileName, Samples dataSample,
                                      List<String> regnDateStr) {
        StringBuilder stringBuilder = new StringBuilder();
        int dSize = dataSample.length()[0];
        for (int dIndx = 0; dIndx < dSize; dIndx++) {
            Point p = dataSample.getPointRef(dIndx);
            String dataStr = regnDateStr.get(dIndx);
            stringBuilder.append(p.toString()).append("# ").append(dataStr).append('\n');
        }

        // replace freqId_rWidth_rHeight_ORG to freqId_rWidth_rHeight
        int lSlant = fileName.lastIndexOf('/');
        int lSecSlant = fileName.substring(0, lSlant).lastIndexOf('/');
        String lDirName = fileName.substring(lSecSlant + 1, lSlant);
        String sFileName = StringUtil.replace(fileName, lDirName,
            lDirName.substring(0, lDirName.length() - 4));
        FileUtil.existDirAndMakeDir(sFileName);
        FileUtil.write(sFileName, stringBuilder.toString());
    }

    protected Queue<RegionInfoVO> readRegionInfoStep(String fileName) {
        Queue<RegionInfoVO> regnList = new LinkedList<RegionInfoVO>();
        if (!FileUtil.exists(fileName)) {
            return regnList;
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

        return regnList;
    }

    protected List<RegionAnomalyInfoVO> discoverPatternStep(Samples dataSample, String fileName,
                                                            List<String> regnDateStr, int rRIndx,
                                                            int cRIndx) {
        // clustering 
        Cluster[] roughClusters = KMeansPlusPlusUtil.cluster(dataSample, maxClusterNum, 20,
            DistanceUtil.SQUARE_EUCLIDEAN_DISTANCE);
        Cluster[] newClusters = ClusterHelper.mergeAdjacentCluster(dataSample, roughClusters,
            DistanceUtil.SQUARE_EUCLIDEAN_DISTANCE, alpha, maxIter);

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
                raVO.setdPoint(dataSample.getPointRef(dIndx));
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
