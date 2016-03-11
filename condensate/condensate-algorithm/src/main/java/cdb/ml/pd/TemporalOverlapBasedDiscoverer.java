package cdb.ml.pd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cdb.common.lang.StatisticParamUtil;
import cdb.common.model.DiscoveredEvent;
import cdb.common.model.Point;

/**
 * 
 * @author Chao Chen
 * @version $Id: TemporalSpatialBasedDiscoverer.java, v 0.1 Nov 24, 2015 11:39:59 AM chench Exp $
 */
public class TemporalOverlapBasedDiscoverer extends TemporalDurationBasedDiscoverer {

    /**
     * @param sqlContext
     * @param rankNum
     */
    public TemporalOverlapBasedDiscoverer(String sqlContext, int rankNum) {
        super(sqlContext, rankNum);
    }

    /** 
     * @see cdb.ml.pd.AbstractPatternDiscoverer#discoverPattern()
     */
    @Override
    public List<DiscoveredEvent> discoverPattern() {
        Map<String, Integer> freqInDay = new HashMap<String, Integer>();
        List<DiscoveredEvent> insularEventArr = new ArrayList<DiscoveredEvent>();
        preprocessing(sqlContext, freqInDay, insularEventArr);//merge same locations with continued time duration

        // group such event with the same time duration into one cluster as a final event - for one object
        List<DiscoveredEvent> eventRep = groupBySameDuration(insularEventArr);
        
        // initial scores before overlapping by time
        for (DiscoveredEvent evt:eventRep) {
        	double score = (evt.getDays().size() + 1) * (evt.getLocations().size());
        	evt.setScore(score);
        }
        // merge event has overlaps
        List<DiscoveredEvent> eventArr = new ArrayList<DiscoveredEvent>();
        DiscoveredEvent curInfo = eventRep.get(0);
        while (!eventRep.isEmpty()) {
            boolean hasOne = false;

            long timeLowerBound = curInfo.getDateBegin().getTime();
            long timeUpperBound = curInfo.getDataEnd().getTime();

            // 1. merge elements with overlapped in time range
            int eventSize = eventRep.size();
            for (int i = eventSize - 1; i >= 0; i--) {
                DiscoveredEvent pivotOfArr = eventRep.get(i);
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
                    for (String locStr : eventRep.get(i).getLocations()) {
                        curInfo.getLocations().add(locStr);
                    }

                    // update score
                    double tspan = (timeEnd - timeBegin) / (24 * 60 * 60 * 1000);
                    curInfo.setScore((tspan + 1) * eventRep.get(i).getLocations().size());
                    
                    // remove merged element
                    eventRep.remove(i);
                }
            }

            // 2. check whether changed
            if (!hasOne) {
                eventArr.add(curInfo);
                curInfo = eventRep.isEmpty() ? null : eventRep.get(0);
            }
        }
        
        // compute the scores -> update to use property score
        int numEvent = eventArr.size();
        double[] neighbors = new double[numEvent];
        for (int i = 0; i < numEvent; i++) {
            DiscoveredEvent one = eventArr.get(i);
            double timeSpan = (one.getDataEnd().getTime() - one.getDateBegin().getTime())
                              / (24 * 60 * 60 * 1000);
            neighbors[i] = one.getLocations().size() + timeSpan;//wrong? one location appears across multiple days
            
           // neighbors[i] = one.getScore();//accumulated score
        }

        // make output
        int[] topIndx = StatisticParamUtil.findTopAbsMaxNum(new Point(neighbors), rankNum,
            numEvent);
        List<DiscoveredEvent> resultEventArr = new ArrayList<DiscoveredEvent>();
        for (int indx : topIndx) {
            resultEventArr.add(eventArr.get(indx));
        }
        return resultEventArr;
    }

}
