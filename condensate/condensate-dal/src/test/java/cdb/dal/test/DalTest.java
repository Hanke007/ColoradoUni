package cdb.dal.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import cdb.common.lang.ExceptionUtil;

/**
 * 
 * @author Chao Chen
 * @version $Id: DalTest.java, v 0.1 Nov 5, 2015 5:15:18 PM chench Exp $
 */
public class DalTest {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext(
                "experiment/recommendation/wemarec/wemaRcmd.xml");
        } catch (Exception e) {
            ExceptionUtil.caught(e, "");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

}
