package cdb.ml.pd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cdb.common.datastructure.ListInMap;
import cdb.common.lang.DateUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.StatisticParamUtil;
import cdb.common.model.DiscoveredEvent;
import cdb.common.model.Point;
import cdb.common.model.RegionAnomalyInfoVO;
import cdb.dal.util.DBUtil;

/**
 * 
 * @author Chao Chen
 * @version $Id: SpatialTemporalDiscoverer.java, v 0.1 Feb 10, 2016 10:32:44 AM chench Exp $
 */
public class SpatialTemporalDiscoverer extends AbstractPatternDiscoverer {
    /** The threshold to transfer continuous data to categorical data*/
    private double pThreshold;
    /** The size of temporal window */
    private int    winSize;
    /** The number of feature to count*/
    private int    featureSize;

    /**
     * @param sqlContext
     * @param rankNum
     */
    public SpatialTemporalDiscoverer(String sqlContext, int winSize, int featureSize, int rankNum) {
        super(sqlContext, rankNum);
        this.winSize = winSize;
        this.featureSize = featureSize;
    }

    /** 
     * @see cdb.ml.pd.AbstractPatternDiscoverer#discoverPattern()
     */
    @Override
    public List<DiscoveredEvent> discoverPattern() {
        Map<String, Integer> freqInDay = new HashMap<String, Integer>();

        List<RegionAnomalyInfoVO> dbSet = DBUtil.excuteSQLWithReturnList(sqlContext);

        // group outliers by category ordered by time  
        ListInMap<String, RegionAnomalyInfoVO> outliersRep = new ListInMap<String, RegionAnomalyInfoVO>();
        int arrNum = dbSet.size();
        for (int i = 1; i < arrNum; i++) {
            RegionAnomalyInfoVO one = dbSet.get(i);
            Point features = one.getdPoint();
            int[] fIndices = StatisticParamUtil.findTopAbsMaxNum(features, featureSize);
            Arrays.sort(fIndices);

            StringBuilder keyStr = new StringBuilder();
            for (int fIndx : fIndices) {
                double fValue = features.getValue(fIndx);
                keyStr.append(fIndx).append('_')
                    .append(Math.abs(fValue) < pThreshold ? 0 : Math.signum(fValue)).append('#');
            }
            outliersRep.put(keyStr.toString(), one);
        }

        // discover group outlier pattern
        List<DiscoveredEvent> insularEventArr = new ArrayList<DiscoveredEvent>();
        try {
            for (List<RegionAnomalyInfoVO> stoArr : outliersRep.values()) {
                DiscoveredEvent curEvent = new DiscoveredEvent();

                long curTimePoint = DateUtil.parse(stoArr.get(0).getDateStr(), DateUtil.WEB_FORMAT)
                    .getTime();
                for (RegionAnomalyInfoVO sto : stoArr) {
                    long tp = DateUtil.parse(sto.getDateStr(), DateUtil.WEB_FORMAT).getTime();

                    if (Math.abs(tp - curTimePoint) <= winSize * 24 * 60 * 60 * 1000) {
                        curEvent.getRaObjects().add(sto);
                        curEvent.getLocations().add("(" + sto.getX() + ", " + sto.getY() + "), ");
                    } else {
                        if (curEvent.getRaObjects().size() >= 2) {
                            insularEventArr.add(curEvent);
                        }
                        curEvent = new DiscoveredEvent();
                    }
                    curTimePoint = tp;
                }
            }
        } catch (Exception e) {
            ExceptionUtil.caught(e, "DateUtil Issues.");
        }

        // give score to every event
        int eventLens = insularEventArr.size();
        double[] scoreArr = new double[eventLens];
        for (int eIndx = 0; eIndx < eventLens; eIndx++) {

        }

        List<DiscoveredEvent> resultEventArr = new ArrayList<DiscoveredEvent>();
        return resultEventArr;
    }

}
