package cdb.exp.qc.analysis;

import cdb.common.lang.FileUtil;
import cdb.common.lang.StringUtil;
import cdb.exp.qc.ui.RegionJFrame;

/**
 * 
 * @author Chao Chen
 * @version $Id: RegionInfoAnalysis.java, v 0.1 Oct 27, 2015 3:55:09 PM chench Exp $
 */
public class RegionInfoAnalysis extends AbstractQcAnalysis {

    /**
     * 
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        gui();

    }

    public static void gui() {
        String imgRootDir = null;
        String regnInfoRootDir = null;
        String regnAnmInfoFile = null;
        String freqId = null;

        String[] lines = FileUtil.readLines("src/test/resources/sqlDump.properties");
        StringBuilder sqlCon = new StringBuilder();
        for (String line : lines) {
            if (StringUtil.isBlank(line) | line.startsWith("#")) {
                // filtering footnotes
                continue;
            } else if (line.startsWith("$")) {
                String key = line.substring(1, line.indexOf('='));
                String val = line.substring(line.indexOf('=') + 1);

                if (StringUtil.equals(key, "IMG_ROOT_DIR")) {
                    imgRootDir = val;
                } else if (StringUtil.equals(key, "REGN_INFO_ROOT_DIR")) {
                    regnInfoRootDir = val;
                } else if (StringUtil.equals(key, "FREQ_ID")) {
                    freqId = val;
                } else if (StringUtil.equals(key, "REGN_ANM_INFO_FILE")) {
                    regnAnmInfoFile = val;
                }
                continue;
            }
            sqlCon.append(line).append('\t');
        }
        String sql = new String(sqlCon);

        RegionJFrame frame = new RegionJFrame(imgRootDir, regnInfoRootDir, regnAnmInfoFile, freqId,
            3, 2010, true, sql);
        frame.pack();
        frame.setLocation(300, 20);
        frame.setSize(520, 700);
        frame.setVisible(true);
    }

}
