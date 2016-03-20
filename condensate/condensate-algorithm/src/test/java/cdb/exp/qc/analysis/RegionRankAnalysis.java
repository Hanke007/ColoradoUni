package cdb.exp.qc.analysis;

import java.util.List;
import java.util.Properties;

import cdb.common.lang.ConfigureUtil;
import cdb.common.lang.DateUtil;
import cdb.common.lang.FileUtil;
import cdb.common.lang.LoggerUtil;
import cdb.common.model.DiscoveredEvent;
import cdb.ml.pd.AbstractPatternDiscoverer;
import cdb.ml.pd.SpatialBasedDiscoverer;
import cdb.ml.pd.TemporalDurationBasedDiscoverer;
import cdb.ml.pd.TemporalOverlapBasedDiscoverer;

import com.google.gson.Gson;

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
            	String midrfile = "C:/Dataset/SSMI/events/all_em_alpha1";
                pDiscoverer = new TemporalOverlapBasedDiscoverer(sql, rankNum);
                List<DiscoveredEvent> resultEventArr = pDiscoverer.discoverPattern();
                int k = 0;
                for (DiscoveredEvent event : resultEventArr) {
                	k++;
                	event.setRank(k);
                	//record events to json file for post analysis
                	Gson gson = new Gson();
                	String json = gson.toJson(event);
                	FileUtil.writeAsAppendWithDirCheck(midrfile,json);
                	FileUtil.writeAsAppendWithDirCheck(midrfile,",");
                	
                	//write to log
                    String duration = "Rank[" + event.getRank() +"]: " 
                    				  + "During ["
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

}
