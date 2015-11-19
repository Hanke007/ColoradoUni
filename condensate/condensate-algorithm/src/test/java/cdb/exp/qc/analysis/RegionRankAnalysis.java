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
import cdb.common.lang.StatisticParamUtil;
import cdb.common.model.Point;
import cdb.common.model.RegionAnomalyInfoVO;
import cdb.common.model.RegionRankInfoVO;
import cdb.dal.util.DBUtil;

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

        Map<String, Integer> countByDate = new HashMap<String, Integer>();
        List<RegionRankInfoVO> candiates = new ArrayList<RegionRankInfoVO>();
        loadAndMake(sql, countByDate, candiates);

        switch (groupStrategy) {
            case "GROUP_BY_LOCATION": {
                groupByLocation(rankNum, countByDate, candiates);
                break;
            }
            case "GROUP_BY_TIMERANGE": {
                Map<String, List<RegionRankInfoVO>> canByTimeRange = new HashMap<String, List<RegionRankInfoVO>>();
                int[] topIndx = groupByTimeRange(rankNum, countByDate, candiates, canByTimeRange);
                List<String> keySet = new ArrayList<String>(canByTimeRange.keySet());

                // output results
                for (int indx : topIndx) {
                    String key = keySet.get(indx);
                    List<RegionRankInfoVO> arr = canByTimeRange.get(key);

                    StringBuilder rankCon = new StringBuilder("During " + key + ": ");
                    for (RegionRankInfoVO one : arr) {
                        rankCon.append("(").append(one.getX()).append(", ").append(one.getY())
                            .append("), ");
                    }
                    System.out.println(rankCon.toString());
                }
                break;
            }
            case "GROUP_BY_EVENT": {
                Map<String, List<RegionRankInfoVO>> canByTimeRange = new HashMap<String, List<RegionRankInfoVO>>();
                int[] topIndx = groupByTimeRange(rankNum, countByDate, candiates, canByTimeRange);
                List<String> keySet = new ArrayList<String>(canByTimeRange.keySet());

                List<List<RegionRankInfoVO>> eventList = new ArrayList<List<RegionRankInfoVO>>();
                for (int indx : topIndx) {
                    eventList.add(canByTimeRange.get(keySet.get(indx)));
                }

                // merge event has overlaps
                List<RegionRankInfoVO> newMergedRep = new ArrayList<RegionRankInfoVO>();
                RegionRankInfoVO curInfo = eventList.get(0).get(0);
                while (!eventList.isEmpty()) {
                    boolean hasOne = false;

                    long timeLowerBound = curInfo.getDateBegin().getTime();
                    long timeUpperBound = curInfo.getDataEnd().getTime();

                    // 1. merge elements with overlapped in time range
                    int eventSize = eventList.size();
                    for (int i = eventSize - 1; i >= 0; i--) {
                        RegionRankInfoVO pivotOfArr = eventList.get(i).get(0);
                        long timeBegin = pivotOfArr.getDateBegin().getTime();
                        long timeEnd = pivotOfArr.getDataEnd().getTime();

                        if (timeEnd < timeLowerBound || timeBegin > timeUpperBound) {
                            // no overlaps in time range
                            continue;
                        } else {
                            hasOne = true;

                            // update upper bound
                            if (timeBegin < timeLowerBound) {
                                curInfo.setDateBegin(pivotOfArr.getDateBegin());
                                timeLowerBound = timeBegin;
                            }
                            // update lower bound
                            if (timeEnd > timeUpperBound) {
                                curInfo.setDataEnd(pivotOfArr.getDataEnd());
                                timeUpperBound = timeEnd;
                            }

                            // update location array
                            for (RegionRankInfoVO one : eventList.get(i)) {
                                String locationKey = "(" + one.getX() + ", " + one.getY() + "), ";
                                curInfo.getLocations().add(locationKey);
                            }

                            // remove merged element
                            eventList.remove(i);
                        }
                    }

                    // 2. check whether changed
                    if (!hasOne) {
                        newMergedRep.add(curInfo);
                        curInfo = eventList.isEmpty() ? null : eventList.get(0).get(0);
                    }
                }

                // output
                int numCan = newMergedRep.size();
                double[] neighbors = new double[numCan];
                for (int i = 0; i < numCan; i++) {
                    RegionRankInfoVO one = newMergedRep.get(i);
                    neighbors[i] = one.getLocations().size();
                }

                int[] finalIndx = StatisticParamUtil.findTopAbsMaxNum(new Point(neighbors), numCan,
                    numCan);
                for (int indx : finalIndx) {
                    RegionRankInfoVO one = newMergedRep.get(indx);
                    String duration = "During ["
                                      + DateUtil.format(one.getDateBegin(), DateUtil.SHORT_FORMAT)
                                      + ", "
                                      + DateUtil.format(one.getDataEnd(), DateUtil.SHORT_FORMAT)
                                      + "]: ";
                    StringBuilder ranCon = new StringBuilder(duration);
                    for (String locKey : one.getLocations()) {
                        ranCon.append(locKey);
                    }
                    System.out.println(ranCon.toString());
                }
            }
            default:
                break;
        }
    }

    protected static void groupByLocation(int rankNum, Map<String, Integer> countByDate,
                                          List<RegionRankInfoVO> candiates) {
        int numCan = candiates.size();
        double[] neighbors = new double[numCan];
        for (int i = 0; i < numCan; i++) {
            RegionRankInfoVO one = candiates.get(i);
            for (Date date : one.getDays()) {
                String key = DateUtil.format(date, DateUtil.SHORT_FORMAT);
                neighbors[i] += countByDate.get(key);
            }
        }
        int[] topIndx = StatisticParamUtil.findTopAbsMaxNum(new Point(neighbors), rankNum, numCan);
        for (int indx : topIndx) {
            System.out.println(candiates.get(indx));
        }
    }

    protected static int[] groupByTimeRange(int rankNum, Map<String, Integer> countByDate,
                                            List<RegionRankInfoVO> candiates,
                                            Map<String, List<RegionRankInfoVO>> canByTimeRange) {
        for (RegionRankInfoVO one : candiates) {
            String key = "[" + DateUtil.format(one.getDateBegin(), DateUtil.SHORT_FORMAT) + ", "
                         + DateUtil.format(one.getDataEnd(), DateUtil.SHORT_FORMAT) + "]";
            List<RegionRankInfoVO> arr = canByTimeRange.get(key);
            if (arr == null) {
                arr = new ArrayList<RegionRankInfoVO>();
                canByTimeRange.put(key, arr);
            }
            arr.add(one);
        }
        List<String> keySet = new ArrayList<String>(canByTimeRange.keySet());

        // compute the scores
        int numKey = keySet.size();
        double[] neighbors = new double[numKey];
        for (int i = 0; i < numKey; i++) {
            String key = keySet.get(i);
            List<RegionRankInfoVO> arr = canByTimeRange.get(key);
            RegionRankInfoVO one = arr.get(0);
            for (Date date : one.getDays()) {
                String dKey = DateUtil.format(date, DateUtil.SHORT_FORMAT);
                neighbors[i] += countByDate.get(dKey);
            }
            neighbors[i] += arr.size() * 0.5;
        }
        return StatisticParamUtil.findTopAbsMaxNum(new Point(neighbors), rankNum, numKey);
    }

    protected static void loadAndMake(String sql, Map<String, Integer> countByDate,
                                      List<RegionRankInfoVO> candiates) {
        try {
            List<RegionAnomalyInfoVO> dbSet = DBUtil.excuteSQLWithReturnList(sql);

            int arrNum = dbSet.size();
            RegionRankInfoVO curVO = new RegionRankInfoVO();
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
                    curVO = new RegionRankInfoVO();
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

    protected static void convert2RRIVO(RegionRankInfoVO target, RegionAnomalyInfoVO source) {
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
