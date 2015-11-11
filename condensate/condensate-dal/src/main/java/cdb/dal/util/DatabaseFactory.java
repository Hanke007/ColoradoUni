package cdb.dal.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import cdb.common.lang.StringUtil;

/**
 * 
 * @author Chao Chen
 * @version $Id: DatabaseFactory.java, v 0.1 Nov 9, 2015 3:02:30 PM chench Exp $
 */
public final class DatabaseFactory {
    /** the repository of the data sbase connections*/
    private final static Map<String, Connection> dbRep = new HashMap<String, Connection>();

    /**
     * forbidden construction
     */
    private DatabaseFactory() {

    }

    /**
     * get a connection via database indentification
     * 
     * @param dbId      the identification of certain database
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException 
     */
    public static Connection getConnection(String dbId) throws SQLException,
                                                        ClassNotFoundException {
        Connection conn = null;
        if (StringUtil.isBlank(dbId)) {
            return null;
        } else if (dbId.startsWith("H2")) {
            Class.forName("org.h2.Driver");
            String freq = StringUtil.toLowerCase(dbId.substring(dbId.lastIndexOf('_') + 1));
            conn = DriverManager.getConnection(
                "jdbc:h2:~/" + freq + "19902014;SCHEMA=" + freq + "19902014", "", "");
        } else if (dbId.startsWith("MYSQL")) {

        }

        return conn;
    }

    /**
     * 
     * @param dbId      the identification of certain database
     * @return          the handler of the statement
     * @throws SQLException  
     */
    public static Statement createStatement(String dbId) throws SQLException {
        return dbRep.get(dbId).createStatement();
    }

    /**
     * 
     * @param dbId      the identification of certain database
     * @param sql       the SQL script
     * @return          the handler of the statement
     * @throws SQLException
     */
    public static PreparedStatement createPreparedStatement(String dbId,
                                                            String sql) throws SQLException {
        return dbRep.get(dbId).prepareStatement(sql);
    }
}
