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

import cdb.common.lang.ClusterHelper;
import cdb.common.lang.DateUtil;
import cdb.common.lang.DistanceUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.FileUtil;
import cdb.common.lang.ImageWUtil;
import cdb.common.lang.LoggerUtil;
import cdb.common.lang.SerializeUtil;
import cdb.dal.vo.AnomalyInfoVO;
import cdb.dal.vo.SparseMatrix;
import cdb.ml.clustering.Cluster;
import cdb.ml.clustering.KMeansPlusPlusUtil;
import cdb.ml.clustering.Point;
import cdb.ml.clustering.Samples;

/**
 * 
 * @author Chao Chen
 * @version $Id: SSMIDsGen.java, v 0.1 Oct 14, 2015 11:57:33 AM chench Exp $
 */
public class SSMIDsGen extends AbstractDsGen {

    /** frequency identity*/
    protected final static String FREQNCY_ID        = "s22v";
    protected final static String FREQNCY_ID_TARGET = "s19v";
    protected final static double alpha             = 2.0;
    protected final static int    maxIter           = 5;

    /** */
    protected final static int K = 20;

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        case1();
    }

    public static void case1() {
        // make task lists
        List<String> taskIds = null;
        try {
            LoggerUtil.info(logger, "2. detect anomalies.");
            Date sDate = DateUtil.parse("20000701", DateUtil.SHORT_FORMAT);
            Date eDate = DateUtil.parse("20000703", DateUtil.SHORT_FORMAT);
            taskIds = workingSetGen(sDate, eDate);
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
        FileUtil.writeAsAppendWithDirCheck(ROOT_DIR + "ClassificationDataset/" + FREQNCY_ID,
            strBuld.toString());
    }

    public static List<String> workingSetGen(Date sDate, Date eDate) {
        List<String> testSet = new ArrayList<String>();

        Date curDate = sDate;
        while (curDate.before(eDate)) {
            String fileAnml = ROOT_DIR + "StatisticAnomaly/"
                              + DateUtil.format(curDate, DateUtil.SHORT_FORMAT) + '_' + FREQNCY_ID
                              + ".OBJ";
            testSet.add(fileAnml);

            //move to next day
            curDate.setTime(curDate.getTime() + 24 * 60 * 60 * 1000);
        }
        return testSet;
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
