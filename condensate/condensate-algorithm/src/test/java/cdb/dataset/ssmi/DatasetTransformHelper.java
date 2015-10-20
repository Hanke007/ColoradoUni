package cdb.dataset.ssmi;

import java.util.ArrayList;
import java.util.List;

import cdb.common.lang.FileUtil;
import cdb.dal.vo.AnomalyInfoVO;

/**
 * 
 * @author Chao Chen
 * @version $Id: TMP.java, v 0.1 Oct 15, 2015 5:14:34 PM chench Exp $
 */
public class DatasetTransformHelper extends AbstractDsGen {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        List<AnomalyInfoVO> anmlArr = new ArrayList<AnomalyInfoVO>();

        String fileName = ROOT_DIR + "ClassificationDataset/RAWDATA_V2";
        String[] lines = FileUtil.readLines(fileName);
        for (String line : lines) {
            anmlArr.add(AnomalyInfoVO.parseOf(line));

        }

        StringBuilder strBld = new StringBuilder();
        for (AnomalyInfoVO one : anmlArr) {
            strBld.append(one.getMeanVal()).append('\t').append(one.getSdVal()).append('\t')
                .append(one.getCentroid().getValue(0)).append('\t')
                .append(one.getCentroid().getValue(1)).append('\t').append(one.getDateInYear())
                .append('\t').append(one.getSeason()).append('\t').append(one.getSpatialConLabel())
                .append('\t').append(one.getResultLabel() - 1).append('\n');

            //            strBld.append(one.getMeanVal()).append('\t').append(one.getSdVal()).append('\t')
            //                .append(one.getCentroid().getValue(0)).append('\t')
            //                .append(one.getCentroid().getValue(1)).append('\t').append(one.getDateInYear())
            //                .append('\t').append(one.getSeason()).append('\t').append(one.getResultLabel())
            //                .append('\n');
        }
        FileUtil.write(ROOT_DIR + "ClassificationDataset/RAWDATA_V2_MATLAB", strBld.toString());
    }

}
