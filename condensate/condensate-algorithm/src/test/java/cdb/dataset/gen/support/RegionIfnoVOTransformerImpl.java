package cdb.dataset.gen.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cdb.common.lang.FileUtil;
import cdb.common.lang.StatisticParamUtil;
import cdb.dal.vo.DenseMatrix;
import cdb.dal.vo.RegionInfoVO;
import cdb.ml.clustering.Point;
import cdb.service.dataset.DatasetProc;

/**
 * 
 * @author Chao Chen
 * @version $Id: RegionIfnoVOTransformerImpl.java, v 0.1 Oct 22, 2015 4:56:42 PM chench Exp $
 */
public class RegionIfnoVOTransformerImpl extends AbstractDataTransformer {

    /** the number of rows in sub-regions*/
    private int                      regionHeight;
    /** the number of columns in sub-regions*/
    private int                      regionWeight;
    /** the minimum value of the data*/
    private double                   minVal;
    /** the maximum value of the data*/
    private double                   maxVal;
    /** the number of the counted distribution*/
    private int                      k;
    /** the repository of means for different months*/
    private Map<String, DenseMatrix> meanRep;
    /** the repository of sds for different months*/
    private Map<String, DenseMatrix> sdRep;

    /** 
     * @see cdb.dataset.gen.support.AbstractDataTransformer#transform(java.lang.String, java.lang.String, java.util.List, java.util.List, cdb.service.dataset.DatasetProc)
     */
    @Override
    public void transform(String rootDir, String freqId, List<String> tDataDump,
                          List<String> tDateDump, DatasetProc dataProc) {

        List<List<RegionInfoVO>> regnSeqTime = new ArrayList<List<RegionInfoVO>>();
        for (int tIndx = 0, len = tDataDump.size(); tIndx < len; tIndx++) {
            List<RegionInfoVO> regnInOneImage = new ArrayList<RegionInfoVO>();
            regnSeqTime.add(regnInOneImage);

            String fileAnml = tDataDump.get(tIndx);
            DenseMatrix ovlMatrix = dataProc.read(fileAnml);
            if (ovlMatrix == null) {
                continue;
            }
            int rowNum = ovlMatrix.getRowNum();
            int colNum = ovlMatrix.getColNum();

            int regnRowNum = rowNum / regionHeight;
            int regnColNum = colNum / regionWeight;
            RegionInfoVO[][] regns = new RegionInfoVO[regnRowNum][regnColNum];
            for (int row = 0; row + regionHeight < rowNum; row += regionHeight) {
                int rIndx = row / regionHeight;

                for (int col = 0; col + regionWeight < colNum; col += regionWeight) {
                    RegionInfoVO regn = new RegionInfoVO();

                    // region data
                    DenseMatrix dMatrix = new DenseMatrix(regionHeight, regionWeight);
                    for (int i = row; i < row + regionHeight; i++) {
                        for (int j = col; j < col + regionWeight; j++) {
                            dMatrix.setVal(i - row, j - col, ovlMatrix.getVal(i, j));
                        }
                    }

                    // plain features
                    plainInfoInsert(dMatrix, regn, tIndx, tDateDump, freqId);

                    int cIndx = col / regionWeight;
                    regn.setrIndx(rIndx);
                    regn.setcIndx(cIndx);

                    regns[rIndx][cIndx] = regn;
                    regnInOneImage.add(regn);
                }
            }

            // contextual features: spatial attributes
            conSpatialFeatureInsert(regns, regnRowNum, regnColNum, tDateDump.get(tIndx));
        }

        // contextual features: temporal attributes
        conTemporalFeatureInsert(regnSeqTime, regnSeqTime.get(0).size());

        // record in file-system
        StringBuilder strBuld = new StringBuilder();
        for (List<RegionInfoVO> regnArr : regnSeqTime) {
            for (RegionInfoVO one : regnArr) {
                strBuld.append(one.toString()).append('\n');
            }
        }
        FileUtil.write(rootDir + "ClassificationDataset/REG_" + freqId, strBuld.toString());

    }

    private void plainInfoInsert(DenseMatrix dMatrix, RegionInfoVO regn, int tIndx,
                                 List<String> tDateDump, String freqId) {
        Point distribution = StatisticParamUtil.distributionInPoint(dMatrix, minVal, maxVal, k,
            1.0d);
        regn.setDistribution(distribution);

        double entropy = StatisticParamUtil.entropy(distribution);
        regn.setEntropy(entropy);

        regn.setDateStr(tDateDump.get(tIndx));
        regn.setFreqIdDomain(freqId);

        double gradSum = 0.0d;
        Point gradRow = new Point((regionHeight - 1) * regionWeight);
        for (int j = 0; j < regionWeight; j++) {
            for (int i = 0; i + 1 < regionHeight; i++) {
                int indx = j * regionWeight + i;

                double gradient = dMatrix.getVal(i + 1, j) - dMatrix.getVal(i, j);
                gradRow.setValue(indx, gradient);
                gradSum += Math.abs(gradient);
            }
        }
        regn.setGradRow(gradRow);

        Point gradCol = new Point((regionWeight - 1) * regionHeight);
        for (int i = 0; i < regionHeight; i++) {
            for (int j = 0; j + 1 < regionWeight; j++) {
                int indx = i * regionHeight + j;

                double gradient = dMatrix.getVal(i, j + 1) - dMatrix.getVal(i, j);
                gradCol.setValue(indx, gradient);
                gradSum += Math.abs(gradient);
            }
        }
        regn.setGradCol(gradCol);

        double gradMean = gradSum
                          / ((regionWeight - 1) * regionHeight + (regionHeight - 1) * regionWeight);
        regn.setGradMean(gradMean);

        double mean = dMatrix.average();
        regn.setMean(mean);

        double sd = dMatrix.sd();
        regn.setSd(sd);
    }

    private void conSpatialFeatureInsert(RegionInfoVO[][] regns, int regnRowNum, int regnColNum,
                                         String dateStr) {
        String month = dateStr.substring(4, 6);
        DenseMatrix mean = meanRep.get(month);
        DenseMatrix sd = sdRep.get(mean);

        for (int rIndx = 0; rIndx < regnRowNum; rIndx++) {
            for (int cIndx = 0; cIndx < regnColNum; cIndx++) {
                RegionInfoVO regn = regns[rIndx][cIndx];

                Point sCorrCon = new Point(9);
                Point sDiffCon = new Point(9);
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        int rInRegns = rIndx - 1 + i;
                        int cInRegns = cIndx - 1 + j;

                        if (rInRegns > 0 & rInRegns < regnRowNum & cInRegns > 0
                            & cInRegns < regnColNum) {
                            int pSeq = i * 3 + j;
                            double diff = regns[rInRegns][cInRegns].getMean() - regn.getMean();
                            double corr = (regns[rInRegns][cInRegns].getMean()
                                           - mean.getVal(rInRegns, cInRegns))
                                          * (regn.getMean() - mean.getVal(rIndx, cIndx))
                                          / (sd.getVal(rInRegns, cInRegns)
                                             * sd.getVal(rIndx, cIndx));
                            sDiffCon.setValue(pSeq, diff);
                            sCorrCon.setValue(pSeq, corr);
                        }
                    }
                }
            }
        }
    }

    private void conTemporalFeatureInsert(List<List<RegionInfoVO>> regnSeqTime,
                                          int regionNumInOneImg) {
        // compute the differences
        int imageNum = regnSeqTime.size();
        double[][] diffInEntropy = new double[imageNum][regionNumInOneImg];
        double[][] diffInMean = new double[imageNum][regionNumInOneImg];
        double[][] diffInSd = new double[imageNum][regionNumInOneImg];
        for (int i = 0; i + 1 < imageNum; i++) {
            List<RegionInfoVO> curImage = regnSeqTime.get(i);
            List<RegionInfoVO> nextImage = regnSeqTime.get(i + 1);

            for (int rgnIndx = 0; rgnIndx < regionNumInOneImg; rgnIndx++) {
                RegionInfoVO curRgn = curImage.get(rgnIndx);
                RegionInfoVO nextRgn = nextImage.get(rgnIndx);

                diffInEntropy[i][rgnIndx] = nextRgn.getEntropy() - curRgn.getEntropy();
                diffInMean[i][rgnIndx] = nextRgn.getMean() - curRgn.getMean();
                diffInSd[i][rgnIndx] = nextRgn.getSd() - curRgn.getSd();
            }
        }

        // make temporal features
        for (int i = 1; i < imageNum; i++) {
            List<RegionInfoVO> curImage = regnSeqTime.get(i);
            for (int rgnIndx = 0; rgnIndx < regionNumInOneImg; rgnIndx++) {
                Point tGradCon = new Point(3 * 2);
                tGradCon.setValue(0, diffInEntropy[i - 1][rgnIndx]);
                tGradCon.setValue(1, diffInEntropy[i][rgnIndx]);
                tGradCon.setValue(2, diffInMean[i - 1][rgnIndx]);
                tGradCon.setValue(3, diffInMean[i][rgnIndx]);
                tGradCon.setValue(4, diffInSd[i - 1][rgnIndx]);
                tGradCon.setValue(5, diffInSd[i][rgnIndx]);

                curImage.get(rgnIndx).settGradCon(tGradCon);
            }
        }
    }

}
