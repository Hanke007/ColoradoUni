package cdb.dal.test;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import cdb.common.lang.DateUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.FileUtil;
import cdb.common.model.RegionAnomalyInfoVO;
import cdb.dal.dao.AnomalyinfoDAOImpl;
import cdb.dal.model.AnomalyInfoBean;

/**
 * 
 * @author Chao Chen
 * @version $Id: DalTest.java, v 0.1 Nov 5, 2015 5:15:18 PM chench Exp $
 */
public class DalTest {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = null;
        try {
            List<AnomalyInfoBean> records = new ArrayList<AnomalyInfoBean>();
            String[] lines = FileUtil.readLines(
                "/Users/chench/git/ColoradoUni/condensate/condensate-dal/src/test/resources/REG_n19v_8_8");
            for (String line : lines) {
                RegionAnomalyInfoVO bean = RegionAnomalyInfoVO.parseOf(line);

                AnomalyInfoBean model = new AnomalyInfoBean();
                model.setX(bean.getX());
                model.setY(bean.getY());
                model.setDate(DateUtil.parse(bean.getDateStr(), DateUtil.SHORT_FORMAT));
                model.setDesc(bean.getdPoint().toString());
                model.setRid(1);
                records.add(model);
            }

            ctx = new ClassPathXmlApplicationContext("springContext.xml");
            AnomalyinfoDAOImpl dao = (AnomalyinfoDAOImpl) ctx.getBean("anomalyinfoDAOImpl");
            dao.insertSelectiveArr(records);

        } catch (Exception e) {
            ExceptionUtil.caught(e, "It goes wrong.");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

}
