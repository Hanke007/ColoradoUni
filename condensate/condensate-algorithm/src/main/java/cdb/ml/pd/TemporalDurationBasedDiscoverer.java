package cdb.ml.pd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cdb.common.lang.DateUtil;
import cdb.common.lang.StatisticParamUtil;
import cdb.common.model.DiscoveredEvent;
import cdb.common.model.Point;

/**
 * First, group anomalies by locations and merge event in continuous days into one event.<br/>
 * Second, group such event with the same time duration into one cluster as a final event.
 * 
 * @author Chao Chen
 * @version $Id: TemporalBasedDiscoverer.java, v 0.1 Nov 24, 2015 11:14:22 AM chench Exp $
 */
public class TemporalDurationBasedDiscoverer extends AbstractPatternDiscoverer {

    /**
     * @param sqlContext
     * @param rankNum
     */
    public TemporalDurationBasedDiscoverer(String sqlContext, int rankNum) {
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

        // group such event with the same time duration into one cluster as a final event
        List<DiscoveredEvent> eventArr = groupBySameDuration(insularEventArr);

        // compute the scores
        int numKey = eventArr.size();
        double[] neighbors = new double[numKey];
        for (int i = 0; i < numKey; i++) {
            DiscoveredEvent durationEvent = eventArr.get(i);
            neighbors[i] = durationEvent.getLocations().size() + durationEvent.getDays().size();
        }

        // make output
        int[] topIndx = StatisticParamUtil.findTopAbsMaxNum(new Point(neighbors), rankNum, numKey);
        List<DiscoveredEvent> resultEventArr = new ArrayList<DiscoveredEvent>();
        for (int indx : topIndx) {
            resultEventArr.add(eventArr.get(indx));
        }
        return resultEventArr;
    }

    /**
     * group such event with the same time duration into one cluster as a final event
     * 
     * @param insularEventArr   the insular event in one location with a time-span
     * @return  required event array
     */
    protected List<DiscoveredEvent> groupBySameDuration(List<DiscoveredEvent> insularEventArr) {
        Map<String, DiscoveredEvent> eventsWithinSameDuration = new HashMap<String, DiscoveredEvent>();
        for (DiscoveredEvent one : insularEventArr) {
            String key = "[" + DateUtil.format(one.getDateBegin(), DateUtil.SHORT_FORMAT) + ", "
                         + DateUtil.format(one.getDataEnd(), DateUtil.SHORT_FORMAT) + "]";

            DiscoveredEvent event = eventsWithinSameDuration.get(key);
            if (event == null) {
                event = one;
                eventsWithinSameDuration.put(key, event);
            } 
            event.getLocations().add("(" + one.getX() + ", " + one.getY() + ")");
        }

        List<DiscoveredEvent> eventArr = new ArrayList<DiscoveredEvent>(
            eventsWithinSameDuration.values());
        return eventArr;
    }

}
