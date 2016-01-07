package cdb.dataset.generator;

import java.util.Properties;

import org.apache.log4j.Logger;

import cdb.common.lang.ConfigureUtil;
import cdb.common.lang.log4j.LoggerDefineConstant;
import cdb.dal.file.AVHRFileDtProc;
import cdb.dal.file.SSMIFileDtProc;
import cdb.dataset.generator.AVHRSourceDumpImpl;
import cdb.dataset.generator.AnomalyInfoVOTransformerImpl;
import cdb.dataset.generator.ImageInfoVOTransformerImpl;
import cdb.dataset.generator.RemoteSensingGen;
import cdb.dataset.generator.SSMISourceDumpImpl;
import cdb.dataset.generator.ui.DsGenFrame;
import cdb.dataset.parameter.AVHRParamCalculator;
import cdb.dataset.parameter.SSMIParamCalculator;

/**
 * 
 * @author Chao Chen
 * @version $Id: ImageSSMIDsGen.java, v 0.1 Oct 22, 2015 4:09:10 PM chench Exp $
 */
public class RemoteSensingOverallDsGen {

	/** logger */
	protected final static Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		regionAVHR();
	}

	public static void imageSSMI() {
		String rootDir = "C:/Users/chench/Desktop/SIDS/SSMI/";
		String freqId = "n22v";
		String sDateStr = "19900101";
		String eDateStr = "20150101";

		double sampleRatio = 0.8;
		double minVal = 0.0d;
		double maxVal = 500.0d;
		int k = 5;

		RemoteSensingGen rsGen = new RemoteSensingGen(rootDir, sDateStr, eDateStr, freqId);
		rsGen.setDataProc(new SSMIFileDtProc());
		rsGen.setSourceDumper(new SSMISourceDumpImpl());
		rsGen.setDataTransformer(new ImageInfoVOTransformerImpl(sampleRatio, minVal, maxVal, k));
		rsGen.run();
	}

	public static void imageAVHR() {
		String rootDir = "C:/Users/chench/Desktop/SIDS/AVHR/";
		String freqId = "1400_chn4";
		String sDateStr = "20000101";
		String eDateStr = "20010101";

		double sampleRatio = 0.8;
		double minVal = 0.0d;
		double maxVal = 500.0d;
		int k = 5;

		RemoteSensingGen rsGen = new RemoteSensingGen(rootDir, sDateStr, eDateStr, freqId);
		rsGen.setDataProc(new AVHRFileDtProc());
		rsGen.setSourceDumper(new AVHRSourceDumpImpl());
		rsGen.setDataTransformer(new ImageInfoVOTransformerImpl(sampleRatio, minVal, maxVal, k));
		rsGen.run();
	}

	public static void anamlySSMI() {
		String rootDir = "C:/Users/chench/Desktop/SIDS/SSMI/";
		String freqId = "s22v";
		String sDateStr = "19920110";
		String eDateStr = "19920111";

		int k = 50;
		double alpha = 2.0d;
		int maxIter = 20;

		RemoteSensingGen rsGen = new RemoteSensingGen(rootDir, sDateStr, eDateStr, freqId);
		rsGen.setDataProc(new SSMIFileDtProc());
		rsGen.setSourceDumper(new SSMISourceDumpImpl());
		rsGen.setDataTransformer(new AnomalyInfoVOTransformerImpl(k, alpha, maxIter));
		rsGen.run();

		String rootDataDir = "C:/Users/chench/Desktop/SIDS/SSMI/ClassificationDataset/";
		String rootImageDir = "C:/Users/chench/Desktop/SIDS/SSMI/Anomaly/2000LowFreq/";
		DsGenFrame frame = new DsGenFrame(rootDataDir, rootImageDir);
		frame.pack();
		frame.setLocation(300, 20);
		frame.setSize(670, 560);
		frame.setVisible(true);
	}

	public static void regionSSMI() {
		String rootDir = "C:/Users/chench/Desktop/SIDS/SSMI/";
		int regionHeight = 8;
		int regionWeight = 8;
		int minVal = 0;
		int maxVal = 400;
		int k = 5;
		String freqId = "n19v";
		String sDateStr = "19980101";
		String eDateStr = "20150101";

		Properties properties = ConfigureUtil.read("src/test/resources/regionInfoDsGen.properties");

		RemoteSensingGen rsGen = new RemoteSensingGen(rootDir, sDateStr, eDateStr, freqId);
		rsGen.setDataProc(new SSMIFileDtProc());
		rsGen.setSourceDumper(new SSMISourceDumpImpl());
		rsGen.setDataTransformer(new RegionIfnoVOTransformerImpl(regionHeight, regionWeight, minVal, maxVal, k,
				new SSMIParamCalculator(rootDir, 1992, 2014, regionHeight, regionWeight, freqId, new SSMIFileDtProc()),
				properties));
		rsGen.run();
	}

	public static void regionAVHR() {
		String rootDir = "C:/Dataset/AVHR/";
		int regionHeight = 8;
		int regionWeight = 8;
		int minVal = 0;
		int maxVal = 8000;
		int k = 5;
		String freqId = "1400_temp";
		String sDateStr = "19810101";
		String eDateStr = "19980101";

		Properties properties = ConfigureUtil.read("src/test/resources/regionInfoDsGen.properties");

		RemoteSensingGen rsGen = new RemoteSensingGen(rootDir, sDateStr, eDateStr, freqId);
		rsGen.setDataProc(new AVHRFileDtProc());
		rsGen.setSourceDumper(new AVHRSourceDumpImpl());
		rsGen.setDataTransformer(new RegionIfnoVOTransformerImpl(regionHeight, regionWeight, minVal, maxVal, k,
				new AVHRParamCalculator(rootDir, 1985, 1998, regionHeight, regionWeight, freqId, new AVHRFileDtProc()),
				properties));
		rsGen.run();
	}

}
