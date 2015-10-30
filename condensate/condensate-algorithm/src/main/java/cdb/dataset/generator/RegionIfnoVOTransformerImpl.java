package cdb.dataset.generator;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cdb.common.lang.FileUtil;
import cdb.common.lang.StatisticParamUtil;
import cdb.dal.vo.DenseMatrix;
import cdb.dal.vo.RegionInfoVO;
import cdb.dal.vo.RegionInfoWindow;
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
     * @param regionHeight
     * @param regionWeight
     * @param minVal
     * @param maxVal
     * @param k
     * @param meanRep
     * @param sdRep
     */
    public RegionIfnoVOTransformerImpl(int regionHeight, int regionWeight, double minVal,
                                       double maxVal, int k, Map<String, DenseMatrix> meanRep,
                                       Map<String, DenseMatrix> sdRep) {
        super();
        this.regionHeight = regionHeight;
        this.regionWeight = regionWeight;
        this.minVal = minVal;
        this.maxVal = maxVal;
        this.k = k;
        this.meanRep = meanRep;
        this.sdRep = sdRep;
    }

    /** 
     * @see cdb.dataset.generator.AbstractDataTransformer#transform(java.lang.String, java.lang.String, java.util.List, java.util.List, cdb.service.dataset.DatasetProc)
     */
    @Override
    public void transform(String rootDir, String freqId, List<String> tDataDump,
                          List<String> tDateDump, DatasetProc dataProc) {
        int[] dimns = dataProc.dimensions(freqId);
        int rowNum = dimns[0];
        int colNum = dimns[1];
        int regnRowNum = rowNum / regionHeight;
        int regnColNum = colNum / regionWeight;

        StringBuilder regnInfoBuffer = new StringBuilder();
        RegionInfoWindow regnWindow = new RegionInfoWindow(3, regnRowNum, regnColNum);
        for (int tIndx = 0, len = tDataDump.size(); tIndx < len; tIndx++) {

            String fileAnml = tDataDump.get(tIndx);
            DenseMatrix ovlMatrix = dataProc.read(fileAnml);
            if (ovlMatrix == null) {
                continue;
            }

            RegionInfoVO[][] regns = new RegionInfoVO[regnRowNum][regnColNum];
            for (int row = 0; row + regionHeight < rowNum; row += regionHeight) {
                int rIndx = row / regionHeight;

                for (int col = 0; col + regionWeight < colNum; col += regionWeight) {
                    RegionInfoVO regn = new RegionInfoVO();

                    // region data
                    boolean isPerfect = true;
                    DenseMatrix dMatrix = new DenseMatrix(regionHeight, regionWeight);
                    for (int i = row; i < row + regionHeight; i++) {
                        for (int j = col; j < col + regionWeight; j++) {
                            double val = ovlMatrix.getVal(i, j);
                            if (Double.isNaN(val)) {
                                isPerfect = false;
                                break;
                            }

                            dMatrix.setVal(i - row, j - col, val);
                        }

                        // if there exists missing value, then throw this region
                        if (!isPerfect) {
                            break;
                        }
                    }

                    // if there exists missing value, then throw this region
                    if (!isPerfect) {
                        continue;
                    }

                    // plain features
                    plainInfoInsert(dMatrix, regn, tIndx, tDateDump, freqId);

                    int cIndx = col / regionWeight;
                    regn.setrIndx(rIndx);
                    regn.setcIndx(cIndx);

                    regns[rIndx][cIndx] = regn;
                }
            }
            regnWindow.put(regns, tDateDump.get(tIndx));

            // contextual features: spatial attributes
            conSpatialFeatureInsert(regns, regnRowNum, regnColNum, tDateDump.get(tIndx));

            // contextual features: temporal attributes
            if (regnWindow.isFull()) {
                conTemporalFeatureInsert(regnWindow, regnRowNum, regnColNum, regnInfoBuffer);
            }

            // check buffer wehter to flush
            if (regnInfoBuffer.length() >= 1000 * 1000 * 80) {
                FileUtil.writeAsAppend(rootDir + "ClassificationDataset/REG_" + freqId + '_'
                                       + regionHeight + '_' + regionWeight,
                    regnInfoBuffer.toString());
                regnInfoBuffer = new StringBuilder();
            }
        }

        // record in file-system
        FileUtil.writeAsAppend(rootDir + "ClassificationDataset/REG_" + freqId + '_' + regionHeight
                               + '_' + regionWeight,
            regnInfoBuffer.toString());

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
                int indx = j * (regionHeight - 1) + i;

                double gradient = dMatrix.getVal(i + 1, j) - dMatrix.getVal(i, j);
                gradRow.setValue(indx, gradient);
                gradSum += Math.abs(gradient);
            }
        }
        regn.setGradRow(gradRow);

        Point gradCol = new Point((regionWeight - 1) * regionHeight);
        for (int i = 0; i < regionHeight; i++) {
            for (int j = 0; j + 1 < regionWeight; j++) {
                int indx = i * (regionWeight - 1) + j;

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
        DenseMatrix sd = sdRep.get(month);

        List<Entry<Integer, Integer>> badRegns = new ArrayList<Entry<Integer, Integer>>();
        for (int rIndx = 0; rIndx < regnRowNum; rIndx++) {
            for (int cIndx = 0; cIndx < regnColNum; cIndx++) {
                RegionInfoVO regn = regns[rIndx][cIndx];
                if (regn == null) {
                    Entry<Integer, Integer> badRegn = new AbstractMap.SimpleEntry<Integer, Integer>(
                        rIndx, cIndx);
                    badRegns.add(badRegn);
                    continue;
                }

                boolean isBad = false;
                Point sCorrCon = new Point(9);
                Point sDiffCon = new Point(9);
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        int rInRegns = rIndx - 1 + i;
                        int cInRegns = cIndx - 1 + j;

                        if (rInRegns < 0 | rInRegns >= regnRowNum | cInRegns < 0
                            | cInRegns >= regnColNum) {
                            continue;
                        } else if (regns[rInRegns][cInRegns] == null) {
                            Entry<Integer, Integer> badRegn = new AbstractMap.SimpleEntry<Integer, Integer>(
                                rIndx, cIndx);
                            badRegns.add(badRegn);
                            isBad = true;
                            break;
                        }

                        int pSeq = i * 3 + j;
                        double diff = regns[rInRegns][cInRegns].getMean() - regn.getMean();
                        double corr = (regns[rInRegns][cInRegns].getMean()
                                       - mean.getVal(rInRegns, cInRegns))
                                      * (regn.getMean() - mean.getVal(rIndx, cIndx))
                                      / (sd.getVal(rInRegns, cInRegns) * sd.getVal(rIndx, cIndx));
                        sDiffCon.setValue(pSeq, diff);
                        sCorrCon.setValue(pSeq, corr);
                    }

                    if (isBad) {
                        break;
                    }
                }
                regn.setsCorrCon(sCorrCon);
                regn.setsDiffCon(sDiffCon);
            }
        }

        // throw away bad data
        for (Entry<Integer, Integer> badRegn : badRegns) {
            regns[badRegn.getKey()][badRegn.getValue()] = null;
        }
    }

    private void conTemporalFeatureInsert(RegionInfoWindow regnWindow, int regnRowNum,
                                          int regnColNum, StringBuilder regnInfoBuffer) {
        // compute the differences
        RegionInfoVO[][] prevImage = regnWindow.get(0);
        RegionInfoVO[][] curImage = regnWindow.get(1);
        RegionInfoVO[][] nextImage = regnWindow.get(2);

        for (int rRIndx = 0; rRIndx < regnRowNum; rRIndx++) {
            for (int cRIndx = 0; cRIndx < regnColNum; cRIndx++) {
                RegionInfoVO prevRgn = prevImage[rRIndx][cRIndx];
                RegionInfoVO curRgn = curImage[rRIndx][cRIndx];
                RegionInfoVO nextRgn = nextImage[rRIndx][cRIndx];

                // if there exists bad data, then throw them away
                if (prevRgn == null | curRgn == null | nextRgn == null) {
                    continue;
                }

                Point tGradCon = new Point(3 * 2);
                tGradCon.setValue(0, curRgn.getEntropy() - prevRgn.getEntropy());
                tGradCon.setValue(1, nextRgn.getEntropy() - curRgn.getEntropy());
                tGradCon.setValue(2, curRgn.getMean() - prevRgn.getMean());
                tGradCon.setValue(3, nextRgn.getMean() - curRgn.getMean());
                tGradCon.setValue(4, curRgn.getSd() - prevRgn.getSd());
                tGradCon.setValue(5, nextRgn.getSd() - curRgn.getSd());

                curRgn.settGradCon(tGradCon);
                regnInfoBuffer.append(curRgn.toString()).append('\n');
            }
        }

    }

}
