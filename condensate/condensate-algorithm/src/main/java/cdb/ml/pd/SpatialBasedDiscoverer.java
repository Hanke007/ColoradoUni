package cdb.ml.pd;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cdb.common.lang.DateUtil;
import cdb.common.lang.StatisticParamUtil;
import cdb.common.model.DiscoveredEvent;
import cdb.common.model.Point;

/**
 * For every location, group anomalies in continuous days into one event.
 * 
 * @author Chao Chen
 * @version $Id: LocationBasedDiscoverer.java, v 0.1 Nov 24, 2015 11:05:06 AM chench Exp $
 */
public class SpatialBasedDiscoverer extends AbstractPatternDiscoverer {

    /**
     * @param sqlContext    the context of sql
     * @param rankNum       the number of the produced results
     */
    public SpatialBasedDiscoverer(String sqlContext, int rankNum) {
        super(sqlContext, rankNum);
    }

    /** 
     * @see cdb.ml.pd.AbstractPatternDiscoverer#discoverPattern()
     */
    @Override
    public List<DiscoveredEvent> discoverPattern() {
        Map<String, Integer> freqInDay = new HashMap<String, Integer>();
        List<DiscoveredEvent> insularEventArr = new ArrayList<DiscoveredEvent>();
        preprocessing(sqlContext, freqInDay, insularEventArr);

        // compute ranking scores
        int numCan = insularEventArr.size();
        double[] neighbors = new double[numCan];
        for (int i = 0; i < numCan; i++) {
            DiscoveredEvent one = insularEventArr.get(i);
            for (Date date : one.getDays()) {
                String key = DateUtil.format(date, DateUtil.SHORT_FORMAT);
                neighbors[i] += freqInDay.get(key);
            }
        }

        // make output
        int[] topIndx = StatisticParamUtil.findTopAbsMaxNum(new Point(neighbors), rankNum, numCan);
        List<DiscoveredEvent> resultEventArr = new ArrayList<DiscoveredEvent>();
        for (int indx : topIndx) {
            resultEventArr.add(insularEventArr.get(indx));
        }
        return resultEventArr;
    }

}
