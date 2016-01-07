package cdb.dataset.parameter;

import java.io.File;
import java.util.List;
import java.util.Map.Entry;

import cdb.common.lang.FileUtil;
import cdb.common.lang.LoggerUtil;
import cdb.common.lang.SerializeUtil;
import cdb.common.model.DenseMatrix;
import cdb.dal.file.DatasetProc;

/**
 * 
 * @author Chao Chen
 * @version $Id: SSMIParamThread.java, v 0.1 Oct 26, 2015 10:08:06 AM chench Exp
 *          $
 */
public class DefaultParamThread extends AbstractParamThread {
	/** the row numbers of every region */
	private int regionHeight = 1;
	/** the column numbers of every region */
	private int regionWeight = 1;
	/** the frequency identification */
	private String freqId;
	/** the data files parsing processor */
	private DatasetProc dProc;

	/**
	 * default construction
	 */
	public DefaultParamThread() {
		super();
	}

	/**
	 * @param regionHeight
	 *            the row numbers of every region
	 * @param regionWeight
	 *            the column numbers of every region
	 * @param freqId
	 *            the frequency identification
	 * @param dProc
	 *            the data files parsing processor
	 */
	public DefaultParamThread(int regionHeight, int regionWeight, String freqId, DatasetProc dProc) {
		super();
		this.regionHeight = regionHeight;
		this.regionWeight = regionWeight;
		this.freqId = freqId;
		this.dProc = dProc;
	}

	/**
	 * @see cdb.dataset.parameter.AbstractParamThread#run()
	 */
	@Override
	public void run() {
		int[] dimsns = dProc.dimensions(freqId);
		int regionRow = dimsns[0] / regionHeight;
		int regionCol = dimsns[1] / regionWeight;

		Entry<String, List<String>> dEntry = null;
		while ((dEntry = task()) != null) {
			String dirIdSerial = dEntry.getKey();
			List<String> fileRes = dEntry.getValue();

			DenseMatrix means = new DenseMatrix(regionRow, regionCol);
			DenseMatrix meanSquares = new DenseMatrix(regionRow, regionCol);
			DenseMatrix counts = new DenseMatrix(regionRow, regionCol);

			for (String fileRe : fileRes) {
				File[] dFiles = FileUtil.parserFilesByPattern(fileRe);
				for (File dFile : dFiles) {
					DenseMatrix dMatrix = dProc.read(dFile.getAbsolutePath());
					if (dMatrix == null) {
						continue;
					}

					for (int row = 0; row < dimsns[0]; row++) {
						for (int col = 0; col < dimsns[1]; col++) {
							double val = dMatrix.getVal(row, col);
							int rRIndx = row / regionHeight;
							int cRIndx = col / regionWeight;
							if (Double.isNaN(val)) {
								continue;
							} else if (rRIndx >= regionRow | cRIndx >= regionCol) {
								continue;
							}

							means.add(rRIndx, cRIndx, val);
							meanSquares.add(rRIndx, cRIndx, val * val);
							counts.add(rRIndx, cRIndx, 1.0d);
						}
					}
				}
			}

			// compute parameters
			DenseMatrix sds = meanSquares;
			for (int row = 0; row < regionRow; row++) {
				for (int col = 0; col < regionCol; col++) {
					double count = counts.getVal(row, col);
					if (count == 0) {
						continue;
					}

					double mean = means.getVal(row, col) / count;
					double sd = Math.sqrt((meanSquares.getVal(row, col) / count - mean * mean) * count / (count - 1));

					means.setVal(row, col, mean);
					sds.setVal(row, col, sd);
				}
			}

			// serialize parameters
			SerializeUtil.writeObject(means, dirIdSerial + "mean.OBJ");
			SerializeUtil.writeObject(sds, dirIdSerial + "sd.OBJ");
			LoggerUtil.info(logger, dirIdSerial + "\t completed.");
		}
	}

}
