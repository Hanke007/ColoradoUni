package cdb.exp.qc.analysis;

import java.util.Properties;

import cdb.common.lang.ConfigureUtil;
import cdb.exp.qc.ui.RegionJFrame;

/**
 * 
 * @author Chao Chen
 * @version $Id: RegionInfoAnalysis.java, v 0.1 Oct 27, 2015 3:55:09 PM chench
 *          Exp $
 */
public class RegionIndividualAnalysis extends AbstractQcAnalysis {

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		gui();

	}

	public static void gui() {
		Properties properties = ConfigureUtil.read("src/test/resources/sqlDump.properties");
		String imgRootDir = properties.getProperty("IMG_ROOT_DIR");
		String regnInfoRootDir = properties.getProperty("REGN_INFO_ROOT_DIR");
		String regnAnmInfoFile = properties.getProperty("REGN_ANM_INFO_FILE");
		String freqId = properties.getProperty("FREQ_ID");
		String sql = properties.getProperty("DUMP");

		RegionJFrame frame = new RegionJFrame(imgRootDir, regnInfoRootDir, regnAnmInfoFile, freqId, 3, 2010, true, sql);
		frame.pack();
		frame.setLocation(30, 20);
		frame.setSize(950, 950);
		frame.setVisible(true);
	}

}
