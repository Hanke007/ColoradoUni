package cdb.ml.qc;

import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import cdb.common.lang.ConfigureUtil;
import cdb.common.lang.log4j.LoggerDefineConstant;

/**
 * 
 * @author Chao Chen
 * @version $Id: AbstractQCThread.java, v 0.1 Oct 27, 2015 10:20:38 AM chench Exp $
 */
public abstract class AbstractQualityControllThread extends Thread {
    /** the queue of overall tasks*/
    public static Queue<Entry<String, List<String>>> tasks;
    /** logger */
    protected final static Logger                    logger = Logger
        .getLogger(LoggerDefineConstant.SERVICE_THREAD);

    //============================================================
    //          Common parameters
    //============================================================
    /** the tolerance of the merging process*/
    protected double  alpha                   = 2.0;
    /** the maximum number of iterations in clustering process*/
    protected int     maxIter                 = 20;
    /** the maximum number of the resulting clusters*/
    protected int     maxClusterNum           = 50;
    /** the potential ratio of the malicious data*/
    protected double  potentialMaliciousRatio = 0.15;
    /** the row numbers of every region*/
    protected int     regionHeight            = 1;
    /** the column numbers of every region*/
    protected int     regionWeight            = 1;
    /** pivot to indicate whether to save the processed data*/
    protected boolean needSaveData            = false;
    /** filter category*/
    protected String  filterCategory          = null;

    /**
     * @param configFileName        the file path of the configuration file
     */
    public AbstractQualityControllThread(String configFileName) {
        Properties properties = ConfigureUtil.read(configFileName);
        this.alpha = Double.valueOf(properties.getProperty("ALPHA"));
        this.maxIter = Integer.valueOf(properties.getProperty("MAX_ITERATION"));
        this.maxClusterNum = Integer.valueOf(properties.getProperty("MAX_CLUSTER_NUM"));
        this.potentialMaliciousRatio = Double
            .valueOf(properties.getProperty("ESTIMATED_RATIO_OF_RARE"));
        this.regionHeight = Integer.valueOf(properties.getProperty("REGION_HEIGHT"));
        this.regionWeight = Integer.valueOf(properties.getProperty("REGION_WEIGHT"));

        this.filterCategory = properties.getProperty("DATA_FILTERING_CATEGORY");
    }

    /**
     * @param alpha                     the tolerance of the merging process
     * @param maxIter                   the maximum number of iterations in clustering process
     * @param maxClusterNum             the maximum number of the resulting clusters
     * @param potentialMaliciousRatio   the potential ratio of the malicious data
     * @param regionHeight              the row numbers of every region
     * @param regionWeight              the column numbers of every region
     */
    public AbstractQualityControllThread(double alpha, int maxIter, int maxClusterNum,
                                         double potentialMaliciousRatio, int regionHeight,
                                         int regionWeight, boolean needSaveData) {
        super();
        this.alpha = alpha;
        this.maxIter = maxIter;
        this.maxClusterNum = maxClusterNum;
        this.potentialMaliciousRatio = potentialMaliciousRatio;
        this.regionHeight = regionHeight;
        this.regionWeight = regionWeight;
        this.needSaveData = needSaveData;
    }

    /**
     * thread-save retrieve unit task
     * 
     * @return
     */
    protected static synchronized Entry<String, List<String>> task() {
        return tasks.poll();
    }

    /** 
     * @see java.lang.Thread#run()
     */
    @Override
    public abstract void run();
}
