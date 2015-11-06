package cdb.dal.dao;

import cdb.dal.model.RegionDescBean;
import com.ibatis.sqlmap.client.SqlMapClient;
import java.sql.SQLException;

public class RegiondescDAOImpl implements RegiondescDAO {
    /**
     * This field was generated by Apache iBATIS ibator.
     * This field corresponds to the database table regiondesc
     *
     * @ibatorgenerated Thu Nov 05 16:46:02 MST 2015
     */
    private SqlMapClient sqlMapClient;

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method corresponds to the database table regiondesc
     *
     * @ibatorgenerated Thu Nov 05 16:46:02 MST 2015
     */
    public RegiondescDAOImpl(SqlMapClient sqlMapClient) {
        super();
        this.sqlMapClient = sqlMapClient;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method corresponds to the database table regiondesc
     *
     * @ibatorgenerated Thu Nov 05 16:46:02 MST 2015
     */
    public int deleteByPrimaryKey(Integer id) throws SQLException {
        RegionDescBean key = new RegionDescBean();
        key.setId(id);
        int rows = sqlMapClient.delete("regiondesc.ibatorgenerated_deleteByPrimaryKey", key);
        return rows;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method corresponds to the database table regiondesc
     *
     * @ibatorgenerated Thu Nov 05 16:46:02 MST 2015
     */
    public void insert(RegionDescBean record) throws SQLException {
        sqlMapClient.insert("regiondesc.ibatorgenerated_insert", record);
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method corresponds to the database table regiondesc
     *
     * @ibatorgenerated Thu Nov 05 16:46:02 MST 2015
     */
    public void insertSelective(RegionDescBean record) throws SQLException {
        sqlMapClient.insert("regiondesc.ibatorgenerated_insertSelective", record);
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method corresponds to the database table regiondesc
     *
     * @ibatorgenerated Thu Nov 05 16:46:02 MST 2015
     */
    public RegionDescBean selectByPrimaryKey(Integer id) throws SQLException {
        RegionDescBean key = new RegionDescBean();
        key.setId(id);
        RegionDescBean record = (RegionDescBean) sqlMapClient
            .queryForObject("regiondesc.ibatorgenerated_selectByPrimaryKey", key);
        return record;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method corresponds to the database table regiondesc
     *
     * @ibatorgenerated Thu Nov 05 16:46:02 MST 2015
     */
    public int updateByPrimaryKeySelective(RegionDescBean record) throws SQLException {
        int rows = sqlMapClient.update("regiondesc.ibatorgenerated_updateByPrimaryKeySelective",
            record);
        return rows;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method corresponds to the database table regiondesc
     *
     * @ibatorgenerated Thu Nov 05 16:46:02 MST 2015
     */
    public int updateByPrimaryKey(RegionDescBean record) throws SQLException {
        int rows = sqlMapClient.update("regiondesc.ibatorgenerated_updateByPrimaryKey", record);
        return rows;
    }

}