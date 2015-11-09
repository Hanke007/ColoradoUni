package cdb.dal.test;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import cdb.common.lang.DateUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.FileUtil;
import cdb.common.model.RegionAnomalyInfoVO;
import cdb.dal.dao.AnomalyInfoDAOImpl;
import cdb.dal.model.AnomalyInfoBean;
import cdb.dal.util.DBUtil;

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
        String sql = "SELECT anomalyinfo.x, anomalyinfo.y, anomalyinfo.date, anomalyinfo.desc "
                     + "FROM anomalyinfo " + "WHERE x > 100 AND x < 200 "
                     + "AND y > 100 AND y < 200 " + "AND date > 20100203 " + "AND date < 20150203 "
                     + "ORDER BY anomalyinfo.date ASC";
        DBUtil.excuteSQLWithReturnList(sql);
    }

    public static void case1() {
        ClassPathXmlApplicationContext ctx = null;
        try {
            List<AnomalyInfoBean> records = new ArrayList<AnomalyInfoBean>();
            String[] lines = FileUtil
                .readLines("C:/Users/chench/Desktop/SIDS/SSMI/Anomaly/REG_n19v_8_8");
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
            AnomalyInfoDAOImpl dao = (AnomalyInfoDAOImpl) ctx.getBean("anomalyinfoDAOImpl");
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
