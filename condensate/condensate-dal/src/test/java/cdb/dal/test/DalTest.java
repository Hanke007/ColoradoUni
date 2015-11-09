package cdb.dal.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import cdb.common.lang.ExceptionUtil;
import cdb.dal.dao.RegiondescDAO;
import cdb.dal.model.RegionDescBean;

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
            ctx = new ClassPathXmlApplicationContext("springContext.xml");
            RegiondescDAO dao = (RegiondescDAO) ctx.getBean("regiondescDAO");
            RegionDescBean bean = new RegionDescBean();
            bean.setRheight(8);
            bean.setRwidth(8);
            dao.insert(bean);
            System.out.println(bean.getRheight());
        } catch (Exception e) {
            ExceptionUtil.caught(e, "It goes wrong.");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

}
