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
		// loadMaskResult();
	}

	public static void loadAnomalyResult() {
		ClassPathXmlApplicationContext ctx = null;
		try {
			String[] lines = FileUtil.readLines("C:/Dataset/SSMI/Anomaly/REG_n19v_2_2");

			int fileLen = lines.length;
			int numSplits = 100;
			int linesPerSplit = fileLen / numSplits;
			int remainingLines = fileLen % numSplits;
			int k = 0;
			
			List<AnomalyInfoBean> records = new ArrayList<AnomalyInfoBean>();
			RegionAnomalyInfoVO bean;

			for (int i = 0; i < linesPerSplit; i++) {
				AnomalyInfoBean model = new AnomalyInfoBean();
				records.add(model);
			}

			for (int nsplit = 1; nsplit <= numSplits; nsplit++) {
				k = 0;
				for (int i = linesPerSplit*(nsplit-1); i < linesPerSplit*nsplit; i++) {
					bean = RegionAnomalyInfoVO.parseOf(lines[i]);
					records.get(k).setX(bean.getX());
					records.get(k).setY(bean.getY());
					records.get(k).setDate(DateUtil.parse(bean.getDateStr(), DateUtil.SHORT_FORMAT));
					records.get(k).setDesc(bean.getdPoint().toString());
					records.get(k).setRid(5);
					k++;//update index of records
				}

				ctx = new ClassPathXmlApplicationContext("springContext.xml");
				AnomalyInfoDAOImpl dao = (AnomalyInfoDAOImpl) ctx.getBean("anomalyinfoDAOImpl");

				int sNum = records.size();
				int sIndx = 0;
				int eIndx = 0;
				for (int sBegin = 0; sBegin < sNum; sBegin += 10000) {
					sIndx = sBegin;
					eIndx = (sIndx + 10000);
					eIndx = (eIndx > sNum) ? sNum : eIndx;
					dao.insertSelectiveArr(records.subList(sIndx, eIndx));
				}
				System.out.print(nsplit);
			} // splits

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
				// ID, LON, LAT
				Point point = new Point(rs.getInt(1), rs.getInt(2), rs.getDouble(3), rs.getDouble(4));
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
