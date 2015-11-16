package cdb.dal.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.h2.jdbcx.JdbcConnectionPool;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cdb.common.lang.DateUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.FileUtil;
import cdb.common.model.Point;
import cdb.common.model.RegionAnomalyInfoVO;
import cdb.dal.dao.AnomalyInfoDAOImpl;
import cdb.dal.dao.MaskDescDAO;
import cdb.dal.model.AnomalyInfoBean;
import cdb.dal.model.MaskDescBean;

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
        loadAnomalyResult();
        //        loadMaskResult();
    }

    public static void loadAnomalyResult() {
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

    public static void loadMaskResult(int mId) {
        final String SELECT_ALL = "SELECT ROW, COL, LON, LAT FROM LOCATIONS";
        List<Point> locs = new ArrayList<Point>();
        try {
            // achieve data
            JdbcConnectionPool connPoolH2 = JdbcConnectionPool.create(
                "jdbc:h2:tcp://localhost/~/ssmi37v19902014a2N;SCHEMA=ssmi37v19902014a2N;AUTO_SERVER=true;MULTI_THREADED=1",
                "", "");
            Connection conn = connPoolH2.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SELECT_ALL);

            while (rs.next()) {
                //ID, LON, LAT
                Point point = new Point(rs.getInt(1), rs.getInt(2), rs.getDouble(3),
                    rs.getDouble(4));
                locs.add(point);
            }
            conn.close();
            connPoolH2.dispose();

            // write data

        } catch (SQLException e) {
            ExceptionUtil.caught(e, "");
        }

        ClassPathXmlApplicationContext ctx = null;
        try {
            List<MaskDescBean> records = new ArrayList<MaskDescBean>();

            for (Point point : locs) {
                MaskDescBean model = new MaskDescBean();
                model.setX(Double.valueOf(point.getValue(0)).intValue());
                model.setY(Double.valueOf(point.getValue(1)).intValue());
                model.setLon(point.getValue(2));
                model.setLat(point.getValue(3));
                model.setCategory(mId);

                records.add(model);
            }

            ctx = new ClassPathXmlApplicationContext("springContext.xml");
            MaskDescDAO dao = (MaskDescDAO) ctx.getBean("maskDescDAOImpl");
            dao.insertSelectiveArr(records.subList(0, records.size() / 2));
            dao.insertSelectiveArr(records.subList(records.size() / 2, records.size()));

        } catch (Exception e) {
            ExceptionUtil.caught(e, "It goes wrong.");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }

    }

}
