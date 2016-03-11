package cdb.ml.pd;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cdb.common.lang.DateUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.model.DiscoveredEvent;
import cdb.common.model.RegionAnomalyInfoVO;
import cdb.dal.util.DBUtil;

/**
 * The abstract class of pattern discoverer
 * 
 * @author Chao Chen
 * @version $Id: AbstractPatternDiscoverer.java, v 0.1 Nov 24, 2015 10:03:54 AM chench Exp $
 */
public abstract class AbstractPatternDiscoverer {

    /** the context of sql*/
    protected String sqlContext;
    /** the number of the produced results*/
    protected int    rankNum;

    /**
     * @param sqlContext    the context of sql
     * @param rankNum       the number of the produced results
     */
    public AbstractPatternDiscoverer(String sqlContext, int rankNum) {
        super();
        this.sqlContext = sqlContext;
        this.rankNum = rankNum;
    }

    /**
     * pattern discovery
     * 
     * @return  the list of the events
     */
    public abstract List<DiscoveredEvent> discoverPattern();

    /**
     * Pre-process data into insular events
     * 
     * @param sql               the context of sql
     * @param freqInDay         the number of locations occurring in one day
     * @param insularEventArr   the insular event in one location with a time-span
     */
    protected void preprocessing(String sql, Map<String, Integer> freqInDay,
                                 List<DiscoveredEvent> insularEventArr) {
        try {
            List<RegionAnomalyInfoVO> dbSet = DBUtil.excuteSQLWithReturnList(sql);//sorted by locations
            int arrNum = dbSet.size();
            DiscoveredEvent curVO = new DiscoveredEvent();
            convert2DiscoveredEvent(curVO, dbSet.get(0));
            freqInDay.put(dbSet.get(0).getDateStr(), Integer.valueOf(1));

            for (int i = 1; i < arrNum; i++) {
                RegionAnomalyInfoVO one = dbSet.get(i);
                Date date = DateUtil.parse(one.getDateStr(), DateUtil.SHORT_FORMAT);

                int x = one.getX();
                int y = one.getY();
                double diffAfter = (date.getTime() - curVO.getDataEnd().getTime())
                                   / (24.0 * 60 * 60 * 1000);

                if (curVO.getX() == x && curVO.getY() == y && diffAfter <= 1.0d) {//same object, connect the adjacent time-stamp
                    curVO.setDataEnd(date);
                    curVO.getDays().add(date);
                } else {
                    // add to array
                    insularEventArr.add(curVO);
                    // update current iterator
                    curVO = new DiscoveredEvent();
                    convert2DiscoveredEvent(curVO, one);
                }

                // update count map
                Integer count = freqInDay.get(one.getDateStr());
                if (count == null) {
                    count = 1;
                } else {
                    count++;
                }
                freqInDay.put(one.getDateStr(), count);
            }
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "");
        }
    }

    /**
     * convert region anomaly value object to discovered event object
     * 
     * @param target    discovered event object
     * @param source    region anomaly value object
     */
    protected void convert2DiscoveredEvent(DiscoveredEvent target, RegionAnomalyInfoVO source) {
        try {
            target.setX(source.getX());
            target.setY(source.getY());
            target.setDateBegin(DateUtil.parse(source.getDateStr(), DateUtil.SHORT_FORMAT));
            target.setDataEnd(DateUtil.parse(source.getDateStr(), DateUtil.SHORT_FORMAT));
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "");
        }
    }

}
