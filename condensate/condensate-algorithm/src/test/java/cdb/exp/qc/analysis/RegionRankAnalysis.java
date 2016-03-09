package cdb.exp.qc.analysis;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import cdb.common.lang.ConfigureUtil;
import cdb.common.lang.DateUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.LoggerUtil;
import cdb.common.model.RegionAnomalyInfoVO;
import cdb.common.model.DiscoveredEvent;
import cdb.dal.util.DBUtil;
import cdb.ml.pd.AbstractPatternDiscoverer;
import cdb.ml.pd.SpatialBasedDiscoverer;
import cdb.ml.pd.TemporalDurationBasedDiscoverer;
import cdb.ml.pd.TemporalOverlapBasedDiscoverer;

/**
 * 
 * @author Chao Chen
 * @version $Id: RegionRankAnalysis.java, v 0.1 Nov 18, 2015 3:27:05 PM chench Exp $
 */
public class RegionRankAnalysis extends AbstractQcAnalysis {

    /**
     * 
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        rank();
    }

    public static void rank() throws Exception {
        Properties properties = ConfigureUtil.read("src/test/resources/rankSql.properties");
        String sql = properties.getProperty("DUMP");
        String groupStrategy = properties.getProperty("GROUP_STRATEGY");
        int rankNum = Integer.valueOf(properties.getProperty("RANK_NUMBER"));

        //Map<String, Integer> countByDate = new HashMap<String, Integer>();
        //List<DiscoveredEvent> candiates = new ArrayList<DiscoveredEvent>();
        //loadAndMake(sql, countByDate, candiates); //duplicate with pre-processing

        AbstractPatternDiscoverer pDiscoverer = null;
        switch (groupStrategy) {
            case "GROUP_BY_LOCATION": {
                pDiscoverer = new SpatialBasedDiscoverer(sql, rankNum);
                List<DiscoveredEvent> resultEventArr = pDiscoverer.discoverPattern();
                for (DiscoveredEvent event : resultEventArr) {
                    System.out.println(event);
                }
                break;
            }
            case "GROUP_BY_TIMERANGE": {
                pDiscoverer = new TemporalDurationBasedDiscoverer(sql, rankNum);
                List<DiscoveredEvent> resultEventArr = pDiscoverer.discoverPattern();

                // output results
                for (DiscoveredEvent event : resultEventArr) {
                    String timeDurationStr = "During  ["
                                             + DateUtil.format(event.getDateBegin(),
                                                 DateUtil.SHORT_FORMAT)
                                             + ", " + DateUtil.format(event.getDataEnd(),
                                                 DateUtil.SHORT_FORMAT)
                                             + "]: ";

                    StringBuilder rankCon = new StringBuilder(timeDurationStr);
                    for (String locStr : event.getLocations()) {
                        rankCon.append(locStr);
                    }
                    System.out.println(rankCon.toString());
                }
                break;
            }
            case "GROUP_BY_EVENT": {
                pDiscoverer = new TemporalOverlapBasedDiscoverer(sql, rankNum);
                List<DiscoveredEvent> resultEventArr = pDiscoverer.discoverPattern();

                for (DiscoveredEvent event : resultEventArr) {
                    String duration = "During ["
                                      + DateUtil.format(event.getDateBegin(), DateUtil.SHORT_FORMAT)
                                      + ", "
                                      + DateUtil.format(event.getDataEnd(), DateUtil.SHORT_FORMAT)
                                      + "]: ";
                    StringBuilder ranCon = new StringBuilder(duration);
                    for (String locKey : event.getLocations()) {
                        ranCon.append(locKey);
                    }
                    LoggerUtil.info(logger, ranCon.toString());
                }
            }
            default:
                break;
        }
    }

    protected static void loadAndMake(String sql, Map<String, Integer> countByDate,
                                      List<DiscoveredEvent> candiates) {
        try {
            List<RegionAnomalyInfoVO> dbSet = DBUtil.excuteSQLWithReturnList(sql);

            int arrNum = dbSet.size();
            DiscoveredEvent curVO = new DiscoveredEvent();
            convert2RRIVO(curVO, dbSet.get(0));
            countByDate.put(dbSet.get(0).getDateStr(), Integer.valueOf(1));

            for (int i = 1; i < arrNum; i++) {
                RegionAnomalyInfoVO one = dbSet.get(i);
                Date date = DateUtil.parse(one.getDateStr(), DateUtil.SHORT_FORMAT);

                int x = one.getX();
                int y = one.getY();
                double diffAfter = (date.getTime() - curVO.getDataEnd().getTime())
                                   / (24.0 * 60 * 60 * 1000);

                if (curVO.getX() == x && curVO.getY() == y && diffAfter <= 1.0d) {
                    curVO.setDataEnd(date);
                    curVO.getDays().add(date);
                } else {
                    // add to array
                    candiates.add(curVO);

                    // update current iterator
                    curVO = new DiscoveredEvent();
                    convert2RRIVO(curVO, one);
                }

                // update count map
                Integer count = countByDate.get(one.getDateStr());
                if (count == null) {
                    count = 1;
                } else {
                    count++;
                }
                countByDate.put(one.getDateStr(), count);
            }
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "");
        }
    }

    protected static void convert2RRIVO(DiscoveredEvent target, RegionAnomalyInfoVO source) {
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
