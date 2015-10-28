package cdb.ml.qc;

import java.util.List;
import java.util.Queue;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

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
