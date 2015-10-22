package cdb.dataset.gen.support;

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
import cdb.common.lang.SerializeUtil;
import cdb.dal.vo.AnomalyInfoVO;
import cdb.dal.vo.SparseMatrix;
import cdb.ml.clustering.Cluster;
import cdb.ml.clustering.KMeansPlusPlusUtil;
import cdb.ml.clustering.Point;
import cdb.ml.clustering.Samples;
import cdb.service.dataset.DatasetProc;

/**
 * 
 * @author Chao Chen
 * @version $Id: AnomalyInfoVOTransformerImpl.java, v 0.1 Oct 22, 2015 4:28:30 PM chench Exp $
 */
public class AnomalyInfoVOTransformerImpl extends AbstractDataTransformer {

    private int    k;
    private double alpha;
    private int    maxIter;

    /**
     * @param k
     * @param alpha
     * @param maxIter
     */
    public AnomalyInfoVOTransformerImpl(int k, double alpha, int maxIter) {
        super();
        this.k = k;
        this.alpha = alpha;
        this.maxIter = maxIter;
    }

    /** 
     * @see cdb.dataset.gen.support.AbstractDataTransformer#transform(java.lang.String, java.lang.String, java.util.List, java.util.List, cdb.service.dataset.DatasetProc)
     */
    @Override
    public void transform(String rootDir, String freqId, List<String> tDataDump,
                          List<String> tDateDump, DatasetProc dataProc) {
        // make task lists
        List<String> taskIds = null;
        try {
            Date sDate = DateUtil.parse("19920110", DateUtil.SHORT_FORMAT);
            Date eDate = DateUtil.parse("19920111", DateUtil.SHORT_FORMAT);
            taskIds = anomalyWorkingSetGen(rootDir, sDate, eDate, freqId);
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
                anmlyList.add(makeObject(sample, gClstr, sMatrix, fileAnml, freqId));
            }
            ImageWUtil.plotGrayImageWithCenter(sMatrix, rootDir + "1.jpg", ImageWUtil.JPG_FORMMAT,
                clusters);
        }

        // record in file-system
        StringBuilder strBuld = new StringBuilder();
        for (AnomalyInfoVO one : anmlyList) {
            strBuld.append(one.toString()).append('\n');
        }
        FileUtil.writeAsAppendWithDirCheck(rootDir + "ClassificationDataset/ANLY" + freqId,
            strBuld.toString());
    }

    protected List<String> anomalyWorkingSetGen(String rootDir, Date sDate, Date eDate,
                                                String freqId) {
        List<String> testSet = new ArrayList<String>();

        Date curDate = sDate;
        while (curDate.before(eDate)) {
            String fileAnml = rootDir + "StatisticAnomaly/"
                              + DateUtil.format(curDate, DateUtil.SHORT_FORMAT) + '_' + freqId
                              + ".OBJ";
            testSet.add(fileAnml);

            //move to next day
            curDate.setTime(curDate.getTime() + 24 * 60 * 60 * 1000);
        }
        return testSet;
    }

    public Cluster[] clustering(SparseMatrix sMatrix, Samples sample) {
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
        Cluster[] roughClusters = KMeansPlusPlusUtil.cluster(sample, k, 20,
            DistanceUtil.SQUARE_EUCLIDEAN_DISTANCE);

        return ClusterHelper.mergeAdjacentCluster(sample, roughClusters,
            DistanceUtil.SQUARE_EUCLIDEAN_DISTANCE, alpha, maxIter);
    }

    public static AnomalyInfoVO makeObject(Samples sample, Cluster gClstr, SparseMatrix sMatrix,
                                           String fileAnml, String freqId) {
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
            int underlineIndx = fileAnml.lastIndexOf(freqId);
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
            obj.setFreqIdDomain(freqId);
            return obj;
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "");
        }

        return null;
    }

}
