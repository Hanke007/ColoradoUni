package cdb.dataset.avhr;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.util.StopWatch;

import cdb.common.lang.DateUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.FileUtil;
import cdb.common.lang.LoggerUtil;
import cdb.common.lang.StatisticParamUtil;
import cdb.dal.vo.DenseMatrix;
import cdb.dal.vo.ImageInfoVO;
import cdb.ml.clustering.Point;
import cdb.service.dataset.AVHRFileDtProc;
import cdb.service.dataset.DatasetProc;

/**
 * 
 * @author Chao Chen
 * @version $Id: AVHRDsGen.java, v 0.1 Oct 20, 2015 3:15:07 PM chench Exp $
 */
public class AVHRDsGen extends AbstractDsGen {
    /** frequency identity*/
    protected final static String FREQNCY_ID = "chn3";
    protected final static double alpha      = 2.0;
    protected final static int    maxIter    = 5;

    /** */
    protected final static int         K                        = 20;
    /** */
    protected final static double      POTENTIAL_MALICOUS_RATIO = 0.15;
    /** */
    protected final static DatasetProc dataProc                 = new AVHRFileDtProc();

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        imageVOGen();
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
        List<String> timeShorFormat = new ArrayList<String>();
        List<String> taskIds = null;
        try {
            LoggerUtil.info(logger, "2. making working set.");
            Date sDate = DateUtil.parse("20000101", DateUtil.SHORT_FORMAT);
            Date eDate = DateUtil.parse("20010101", DateUtil.SHORT_FORMAT);
            taskIds = imgWorkingSetGen(sDate, eDate, FREQNCY_ID, timeShorFormat);
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "Check date format.");
        }

        // make object
        List<ImageInfoVO> imgList = new ArrayList<ImageInfoVO>();
        for (int tIndx = 0; tIndx < taskIds.size(); tIndx++) {
            String fileAnml = taskIds.get(tIndx);

            DenseMatrix dMatrix = dataProc.read(fileAnml);
            if (dMatrix == null) {
                continue;
            }

            ImageInfoVO imgVO = new ImageInfoVO();
            Point distribution = StatisticParamUtil.distributionInPoint(dMatrix, 0, 500, 5, 0.8);
            imgVO.setDistribution(distribution);

            double entropy = StatisticParamUtil.entropy(distribution);
            imgVO.setEntropy(entropy);

            imgVO.setDateStr(timeShorFormat.get(tIndx));

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

}
