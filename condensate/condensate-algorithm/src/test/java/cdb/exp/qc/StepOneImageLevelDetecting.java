package cdb.exp.qc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cdb.common.lang.ClusterHelper;
import cdb.common.lang.DistanceUtil;
import cdb.common.lang.FileUtil;
import cdb.common.lang.ImageWUtil;
import cdb.dal.vo.DenseMatrix;
import cdb.dal.vo.ImageInfoVO;
import cdb.dataset.generator.BinFileConvntnUtil;
import cdb.ml.clustering.Cluster;
import cdb.ml.clustering.KMeansPlusPlusUtil;
import cdb.ml.clustering.Point;
import cdb.ml.clustering.Samples;
import cdb.service.dataset.AVHRFileDtProc;
import cdb.service.dataset.DatasetProc;
import cdb.service.dataset.SSMIFileDtProc;

/**
 * 
 * @author Chao Chen
 * @version $Id: StepOneMaliciousFiltering.java, v 0.1 Oct 20, 2015 3:40:26 PM chench Exp $
 */
public class StepOneImageLevelDetecting extends AbstractDetecting {

    protected final static double alpha                    = 2.0;
    protected final static int    maxIter                  = 5;
    /** */
    protected final static int    K                        = 20;
    /** */
    protected final static double POTENTIAL_MALICOUS_RATIO = 0.20;

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        //        AVHR();
        SSMI();
    }

    public static void AVHR() {
        String rootDir = "C:/Users/chench/Desktop/SIDS/AVHR/";
        String freqId = "1400_chn4";
        malicousDetection(rootDir, freqId, new AVHRFileDtProc());
    }

    public static void SSMI() {
        String rootDir = "C:/Users/chench/Desktop/SIDS/SSMI/";
        String freqId = "n85v";

        malicousDetection(rootDir, freqId, new SSMIFileDtProc());
    }

    protected static void malicousDetection(String rootDir, String freqId, DatasetProc dProc) {

        List<ImageInfoVO> imgList = new ArrayList<ImageInfoVO>();

        String fileName = rootDir + "ClassificationDataset/IMG_" + freqId;
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

                String fileAnml = null;
                if (freqId.indexOf("chn") != -1) {
                    fileAnml = BinFileConvntnUtil.fileAVHR(rootDir, one.getDateStr(), freqId);
                } else {
                    fileAnml = BinFileConvntnUtil.fileSSMI(rootDir, one.getDateStr(), freqId);
                }

                DenseMatrix matrix = dProc.read(fileAnml);
                ImageWUtil.plotGrayImage(matrix,
                    rootDir + "Anomaly/Malicious/" + one.getDateStr() + "_" + freqId + ".jpg",
                    ImageWUtil.JPG_FORMMAT);
            }
        }
    }

}
