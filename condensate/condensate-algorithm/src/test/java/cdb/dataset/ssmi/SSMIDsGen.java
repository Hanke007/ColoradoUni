package cdb.dataset.ssmi;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.util.StopWatch;

import cdb.common.lang.ClusterHelper;
import cdb.common.lang.DateUtil;
import cdb.common.lang.DistanceUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.FileUtil;
import cdb.common.lang.ImageWUtil;
import cdb.common.lang.LoggerUtil;
import cdb.common.lang.SerializeUtil;
import cdb.common.lang.StatisticParamUtil;
import cdb.dal.vo.AnomalyInfoVO;
import cdb.dal.vo.DenseMatrix;
import cdb.dal.vo.ImageInfoVO;
import cdb.dal.vo.SparseMatrix;
import cdb.ml.clustering.Cluster;
import cdb.ml.clustering.KMeansPlusPlusUtil;
import cdb.ml.clustering.Point;
import cdb.ml.clustering.Samples;
import cdb.service.dataset.DatasetProc;
import cdb.service.dataset.SSMIFileDtProc;

/**
 * 
 * @author Chao Chen
 * @version $Id: SSMIDsGen.java, v 0.1 Oct 14, 2015 11:57:33 AM chench Exp $
 */
public class SSMIDsGen extends AbstractDsGen {

    /** frequency identity*/
    protected final static String FREQNCY_ID        = "s19v";
    protected final static String FREQNCY_ID_TARGET = "s19v";
    protected final static double alpha             = 2.0;
    protected final static int    maxIter           = 5;

    /** */
    protected final static int    K                        = 20;
    /** */
    protected final static double POTENTIAL_MALICOUS_RATIO = 0.15;

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        imageVOGen();
        //        malicousDetection();
        stopWatch.stop();
        LoggerUtil.info(logger, "OVERALL TIME SPENDED: " + stopWatch.getTotalTimeMillis() / 1000.0);
    }

    //=======================================
    //
    //  Image Value Object Generator
    //
    //=======================================
    public static void imageVOGen() {
        // make task lists
        List<String> taskIds = null;
        try {
            LoggerUtil.info(logger, "2. making working set.");
            Date sDate = DateUtil.parse("19900101", DateUtil.SHORT_FORMAT);
            Date eDate = DateUtil.parse("20090101", DateUtil.SHORT_FORMAT);
            taskIds = imgWorkingSetGen(sDate, eDate, FREQNCY_ID);
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "Check date format.");
        }

        // make object
        DatasetProc dataProc = new SSMIFileDtProc();
        List<ImageInfoVO> imgList = new ArrayList<ImageInfoVO>();
        for (String fileAnml : taskIds) {
            DenseMatrix dMatrix = dataProc.read(fileAnml);
            if (dMatrix == null) {
                continue;
            }

            ImageInfoVO imgVO = new ImageInfoVO();
            Point distribution = StatisticParamUtil.distributionInPoint(dMatrix, 0, 500, 5, 0.8);
            imgVO.setDistribution(distribution);

            double entropy = StatisticParamUtil.entropy(distribution);
            imgVO.setEntropy(entropy);

            int dIndx = fileAnml.indexOf("_v") - 8;
            String dateStr = fileAnml.substring(dIndx, dIndx + 8);
            imgVO.setDateStr(dateStr);

            imgVO.setFreqIdDomain(FREQNCY_ID);

            imgList.add(imgVO);
        }

        for (int i = 1, len = imgList.size() - 1; i < len; i++) {
            ImageInfoVO prevOne = imgList.get(i - 1);
            ImageInfoVO curOne = imgList.get(i);
            ImageInfoVO nextOne = imgList.get(i + 1);

            double prevGrad = curOne.getEntropy() - prevOne.getEntropy();
            double nextGrad = nextOne.getEntropy() - curOne.getEntropy();

            curOne.setPrevGrad(prevGrad);
            curOne.setNextGrad(nextGrad);
        }
        //remove the head and tail
        imgList.remove(imgList.size() - 1);
        imgList.remove(0);

        // record in file-system
        StringBuilder strBuld = new StringBuilder();
        for (ImageInfoVO one : imgList) {
            strBuld.append(one.toString()).append('\n');
        }
        FileUtil.writeAsAppendWithDirCheck(ROOT_DIR + "ClassificationDataset/IMG_" + FREQNCY_ID,
            strBuld.toString());

    }

    public static void malicousDetection() {

        List<ImageInfoVO> imgList = new ArrayList<ImageInfoVO>();

        String fileName = ROOT_DIR + "ClassificationDataset/IMG_" + FREQNCY_ID;
        String[] lines = FileUtil.readLines(fileName);
        for (String line : lines) {
            imgList.add(ImageInfoVO.parseOf(line));
        }

        int dSeq = 0;
        Samples dataSample = new Samples(imgList.size(), 8);
        for (ImageInfoVO one : imgList) {
            Point distribution = one.getDistribution();
            int distbnNum = distribution.dimension();
            for (int i = 0; i < distbnNum; i++) {
                dataSample.setValue(dSeq, i, distribution.getValue(i));
            }

            dataSample.setValue(dSeq, distbnNum, one.getEntropy());
            dataSample.setValue(dSeq, distbnNum + 1, one.getPrevGrad());
            dataSample.setValue(dSeq, distbnNum + 2, one.getNextGrad());
            dSeq++;
        }

        // clustering 
        Cluster[] roughClusters = KMeansPlusPlusUtil.cluster(dataSample, K, 20,
            DistanceUtil.SQUARE_EUCLIDEAN_DISTANCE);
        Cluster[] newClusters = ClusterHelper.mergeAdjacentCluster(dataSample, roughClusters,
            DistanceUtil.SQUARE_EUCLIDEAN_DISTANCE, alpha, maxIter);

        Map<Integer, Cluster> num2ClusrMp = new HashMap<Integer, Cluster>();
        for (Cluster clust : newClusters) {
            num2ClusrMp.put(clust.getList().size(), clust);
        }

        List<Integer> numArray = new ArrayList<Integer>(num2ClusrMp.keySet());
        Collections.sort(numArray);

        DatasetProc dProc = new SSMIFileDtProc();
        int curNum = 0;
        int totalNum = imgList.size();
        int newClusterNum = newClusters.length;
        for (int i = 0; i < newClusterNum; i++) {
            Cluster cluster = num2ClusrMp.get(numArray.get(i));

            curNum += cluster.getList().size();
            if (curNum > totalNum * POTENTIAL_MALICOUS_RATIO) {
                break;
            }

            for (int dIndx : cluster.getList()) {
                ImageInfoVO one = imgList.get(dIndx);
                String fileAnml = binFileConvntn(one.getDateStr(), FREQNCY_ID);

                DenseMatrix matrix = dProc.read(fileAnml);
                ImageWUtil.plotGrayImage(matrix,
                    ROOT_DIR + "Anomaly/Malicious/" + one.getDateStr() + ".jpg",
                    ImageWUtil.JPG_FORMMAT);
            }
        }
    }

    //=======================================
    //
    //  Anomaly Value Object Generator
    //
    //=======================================
    public static void anomalyVOGen() {
        // make task lists
        List<String> taskIds = null;
        try {
            LoggerUtil.info(logger, "2. detect anomalies.");
            Date sDate = DateUtil.parse("20000701", DateUtil.SHORT_FORMAT);
            Date eDate = DateUtil.parse("20000703", DateUtil.SHORT_FORMAT);
            taskIds = anomalyWorkingSetGen(sDate, eDate, FREQNCY_ID);
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "Check date format.");
        }

        // detect anomaly object
        List<AnomalyInfoVO> anmlyList = new ArrayList<AnomalyInfoVO>();
        for (String fileAnml : taskIds) {
            SparseMatrix sMatrix = (SparseMatrix) SerializeUtil.readObject(fileAnml);
            if (sMatrix == null) {
                continue;
            }

            int sampleSize = sMatrix.itemCount();
            Samples sample = new Samples(sampleSize, 2);
            Cluster[] clusters = clustering(sMatrix, sample);

            Map<Integer, Cluster> num2ClusrMp = new HashMap<Integer, Cluster>();
            for (Cluster clust : clusters) {
                num2ClusrMp.put(clust.getList().size(), clust);
            }

            List<Integer> numArray = new ArrayList<Integer>(num2ClusrMp.keySet());
            Collections.sort(numArray);

            // just fetch the first five anomaly objects
            int newClusterNum = numArray.size();
            for (int cIndx = 0; cIndx < newClusterNum; cIndx++) {
                int indx = numArray.size() - 1 - cIndx;

                Cluster gClstr = num2ClusrMp.get(numArray.get(indx));
                anmlyList.add(makeObject(sample, gClstr, sMatrix, fileAnml));
            }
            LoggerUtil.info(logger, "MergedNum: " + numArray.size());
            ImageWUtil.plotGrayImageWithCenter(sMatrix, ROOT_DIR + "1.jpg", ImageWUtil.JPG_FORMMAT,
                clusters);
        }

        // record in file-system
        StringBuilder strBuld = new StringBuilder();
        for (AnomalyInfoVO one : anmlyList) {
            strBuld.append(one.toString()).append('\n');
        }
        FileUtil.writeAsAppendWithDirCheck(ROOT_DIR + "ClassificationDataset/ANLY" + FREQNCY_ID,
            strBuld.toString());
    }

    public static Cluster[] clustering(SparseMatrix sMatrix, Samples sample) {
        // make clustering data structure
        int itemCount = 0;
        int[] dimnsn = sMatrix.length();
        for (int row = 0; row < dimnsn[0]; row++) {
            int[] cols = sMatrix.getRowRef(row).indexList();
            if (cols == null) {
                continue;
            }

            // make samples
            for (int col : cols) {
                sample.setValue(itemCount, 0, row);
                sample.setValue(itemCount, 1, col);
                itemCount++;
            }
        }

        // clustering
        Cluster[] roughClusters = KMeansPlusPlusUtil.cluster(sample, K, 20,
            DistanceUtil.SQUARE_EUCLIDEAN_DISTANCE);

        return ClusterHelper.mergeAdjacentCluster(sample, roughClusters,
            DistanceUtil.SQUARE_EUCLIDEAN_DISTANCE, alpha, maxIter);
    }

    public static AnomalyInfoVO makeObject(Samples sample, Cluster gClstr, SparseMatrix sMatrix,
                                           String fileAnml) {
        try {
            AnomalyInfoVO obj = new AnomalyInfoVO();

            // mean and sd values
            DescriptiveStatistics paramComp = new DescriptiveStatistics();
            for (Integer pIndx : gClstr.getList()) {
                Point p = sample.getPoint(pIndx);

                int row = (int) p.getValue(0);
                int col = (int) p.getValue(1);
                paramComp.addValue(sMatrix.getValue(row, col));
            }

            // centroid
            Point centrInPoint = gClstr.centroid(sample);

            // date & season
            int underlineIndx = fileAnml.lastIndexOf(FREQNCY_ID);
            String dateStr = fileAnml.substring(underlineIndx - 9, underlineIndx - 1);
            Date date = DateUtil.parse(dateStr, DateUtil.SHORT_FORMAT);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int dateInYear = cal.get(Calendar.DAY_OF_YEAR);
            int season = (cal.get(Calendar.MONTH) - 1) / 4;

            obj.setMeanVal(paramComp.getMean());
            obj.setSdVal(paramComp.getStandardDeviation());
            obj.setCentroid(centrInPoint);
            obj.setDateInYear(dateInYear);
            obj.setSeason(season);
            obj.setDateStr(dateStr);
            obj.setFreqIdDomain(FREQNCY_ID);
            obj.setFreqIdTarget(FREQNCY_ID_TARGET);
            return obj;
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "");
        }

        return null;
    }
}
