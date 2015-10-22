package cdb.dataset.gen.support;

import java.util.ArrayList;
import java.util.List;

import cdb.common.lang.FileUtil;
import cdb.common.lang.StatisticParamUtil;
import cdb.dal.vo.DenseMatrix;
import cdb.dal.vo.ImageInfoVO;
import cdb.ml.clustering.Point;
import cdb.service.dataset.DatasetProc;

/**
 * 
 * @author Chao Chen
 * @version $Id: AVHRDataTransformer.java, v 0.1 Oct 22, 2015 4:01:19 PM chench Exp $
 */
public class ImageInfoVOTransformerImpl extends AbstractDataTransformer {

    /** the rate of the sampling*/
    private double sampleRatio;
    /** the minimum value of the data*/
    private double minVal;
    /** the maximum value of the data*/
    private double maxVal;
    /** the number of the counted distribution*/
    private int    k;

    /**
     * @param sampleRatio   the rate of the sampling
     * @param minVal        the minimum value of the data
     * @param maxVal        the maximum value of the data
     * @param k             the number of the counted distribution
     */
    public ImageInfoVOTransformerImpl(double sampleRatio, double minVal, double maxVal, int k) {
        super();
        this.sampleRatio = sampleRatio;
        this.minVal = minVal;
        this.maxVal = maxVal;
        this.k = k;
    }

    /** 
     * @see cdb.dataset.gen.support.AbstractDataTransformer#transform(java.lang.String, java.lang.String, java.util.List, java.util.List, cdb.service.dataset.DatasetProc)
     */
    @Override
    public void transform(String rootDir, String freqId, List<String> tDataDump,
                          List<String> tDateDump, DatasetProc dataProc) {
        List<ImageInfoVO> imgList = new ArrayList<ImageInfoVO>();
        for (int tIndx = 0, len = tDataDump.size(); tIndx < len; tIndx++) {
            String fileAnml = tDataDump.get(tIndx);

            DenseMatrix dMatrix = dataProc.read(fileAnml);
            if (dMatrix == null) {
                continue;
            }

            ImageInfoVO imgVO = new ImageInfoVO();
            Point distribution = StatisticParamUtil.distributionInPoint(dMatrix, minVal, maxVal, k,
                sampleRatio);
            imgVO.setDistribution(distribution);

            double entropy = StatisticParamUtil.entropy(distribution);
            imgVO.setEntropy(entropy);

            imgVO.setDateStr(tDateDump.get(tIndx));

            imgVO.setFreqIdDomain(freqId);

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
        if (!imgList.isEmpty()) {
            imgList.remove(imgList.size() - 1);
            imgList.remove(0);
        }

        // record in file-system
        StringBuilder strBuld = new StringBuilder();
        for (ImageInfoVO one : imgList) {
            strBuld.append(one.toString()).append('\n');
        }
        FileUtil.write(rootDir + "ClassificationDataset/IMG_" + freqId, strBuld.toString());
    }

}
