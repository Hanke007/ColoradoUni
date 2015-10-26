package cdb.dataset.parameter;

import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import cdb.common.lang.log4j.LoggerDefineConstant;

import java.util.Queue;

/**
 * 
 * @author Chao Chen
 * @version $Id: AbstractParamThread.java, v 0.1 Oct 26, 2015 10:03:40 AM chench Exp $
 */
public abstract class AbstractParamThread extends Thread {
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
