package cdb.exp.qc.analysis;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import cdb.common.lang.ConfigureUtil;
import cdb.common.lang.DateUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.ImageWUtil;
import cdb.common.model.DenseMatrix;
import cdb.common.model.RegionAnomalyInfoVO;
import cdb.common.model.SparseMatrix;
import cdb.dal.file.AVHRFileDtProc;
import cdb.dal.file.DatasetProc;
import cdb.dal.file.SSMIFileDtProc;
import cdb.dal.util.DBUtil;
import cdb.dataset.util.BinFileConvntnUtil;

/**
 * 
 * @author Chao Chen
 * @version $Id: ImageDisplayAnalysis.java, v 0.1 Dec 2, 2015 2:17:59 PM chench
 *          Exp $
 */
public class ImageDisplayAnalysis extends AbstractQcAnalysis {

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// DatasetProc dProc = new SSMIFileDtProc();
		// display();
		batchGenImage(new AVHRFileDtProc());
	}

	protected static void display(DatasetProc dProc) {
		Properties properties = ConfigureUtil.read("src/test/resources/zConfigQC.properties");
		String sql = properties.getProperty("DUMP");
		String rootDir = properties.getProperty("DATA_ROOT_DIR");
		String taskId = properties.getProperty("END_DATE");
		String freqId = properties.getProperty("FREQ_ID");

		SparseMatrix sMatrix = new SparseMatrix(dProc.dimensions(freqId)[0], dProc.dimensions(freqId)[1]);
		List<RegionAnomalyInfoVO> dbSet = DBUtil.excuteSQLWithReturnList(sql);
		for (RegionAnomalyInfoVO one : dbSet) {
			sMatrix.setValue(one.getX(), one.getY(), 1.0d);
		}

		DenseMatrix dMatrix = dProc.read(BinFileConvntnUtil.fileSSMI(rootDir, taskId, freqId));
		ImageWUtil.plotRGBImageWithMask(dMatrix, rootDir + taskId + ".bmp", sMatrix, ImageWUtil.BMP_FORMAT);
	}

	protected static void batchGenImage(DatasetProc dProc) {
		Properties properties = ConfigureUtil.read("src/test/resources/zConfigQC.properties");
		String rootDir = properties.getProperty("DATA_ROOT_DIR");
		String begDate = properties.getProperty("BEGIN_DATE");
		String endDate = properties.getProperty("END_DATE");
		String freqId = properties.getProperty("FREQ_ID");
		String imgDir = properties.getProperty("IMAGE_DIR");

		try {
			Date bDate = DateUtil.parse(begDate, DateUtil.SHORT_FORMAT);
			Date eDate = DateUtil.parse(endDate, DateUtil.SHORT_FORMAT);

			while (!bDate.after(eDate)) {
				DenseMatrix dMatrix = dProc.read(BinFileConvntnUtil.fileAVHR(rootDir, DateUtil.format(bDate, DateUtil.SHORT_FORMAT), freqId));
				if (dMatrix != null) {
					ImageWUtil.plotGrayImage(dMatrix, imgDir + DateUtil.format(bDate, DateUtil.SHORT_FORMAT) + ".bmp",
							ImageWUtil.BMP_FORMAT);
				}

				bDate.setTime(bDate.getTime() + 24 * 60 * 60 * 1000);
			}

		} catch (ParseException e) {
			ExceptionUtil.caught(e, "Date format crashed");
		}

	}
}
