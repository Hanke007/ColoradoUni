package cdb.dal.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cdb.common.lang.DateUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.model.Point;
import cdb.common.model.RegionAnomalyInfoVO;

/**
 * 
 * @author Chao Chen
 * @version $Id: DBUtil.java, v 0.1 Nov 9, 2015 1:14:05 PM chench Exp $
 */
public final class DBUtil {
    /**
     * forbidden construction
     */
    private DBUtil() {

    }

    public static List<RegionAnomalyInfoVO> excuteSQLWithReturnList(String sql) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = getAvailConnection();
            if (conn == null) {
                return null;
            }

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            List<RegionAnomalyInfoVO> resultSet = new ArrayList<RegionAnomalyInfoVO>();
            while (rs.next()) {
                RegionAnomalyInfoVO bean = new RegionAnomalyInfoVO();
                bean.setX(rs.getInt(1));
                bean.setY(rs.getInt(2));
                bean.setDateStr(DateUtil.format(rs.getDate(3), DateUtil.SHORT_FORMAT));
                bean.setdPoint(Point.parseOf(rs.getString(4)));
                bean.setWidth(rs.getInt(5));
                bean.setHeight(rs.getInt(6));
                resultSet.add(bean);
            }
            return resultSet;
        } catch (ClassNotFoundException | SQLException e) {
            ExceptionUtil.caught(e, "No SQL Connection.");
        }

        return null;
    }

    protected static Connection getAvailConnection() {
        Connection conn = null;
        String[] urls = { "jdbc:mysql://128.138.189.106:3306/regionanomalyrep",
                          "jdbc:mysql://localhost:3306/regionanomalyrep" };
        for (String url : urls) {
            try {
                conn = DriverManager.getConnection(url, "chench", "123456");
            } catch (SQLException e) {
                continue;
            }
        }

        return conn;
    }
}
