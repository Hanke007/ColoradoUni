package cdb.dal.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.h2.jdbcx.JdbcConnectionPool;

import cdb.common.lang.StringUtil;

/**
 * 
 * @author Chao Chen
 * @version $Id: DatabaseFactory.java, v 0.1 Nov 9, 2015 3:02:30 PM chench Exp $
 */
public final class DatabaseFactory {
    /** the repository of the data base connections*/
    private final static Map<String, Connection>         dbRep = new HashMap<String, Connection>();
    /** the repository of the h2 database connections*/
    private final static Map<String, JdbcConnectionPool> h2Rep = new HashMap<String, JdbcConnectionPool>();;

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
            JdbcConnectionPool connPoolH2 = h2Rep.get(dbId);
            if (connPoolH2 == null) {
                String freqId = StringUtil.toLowerCase(dbId.substring(dbId.lastIndexOf('_') + 1));
                connPoolH2 = JdbcConnectionPool
                    .create("jdbc:h2:/h2database/" + freqId + "19902014a2;SCHEMA=" + freqId
                            + "19902014a2;AUTO_SERVER=true;CACHE_SIZE=1048576;MULTI_THREADED=1",
                        "", "");
                h2Rep.put(dbId, connPoolH2);
            }
            conn = connPoolH2.getConnection();
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
