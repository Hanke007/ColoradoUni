package cdb.web.dao.h2;

import cdb.web.envelope.AnomalyEnvelope;

/**
 * 
 * @author Chao Chen
 * @version $Id: AbstractH2BasedDAO.java, v 0.1 Nov 30, 2015 3:48:44 PM chench Exp $
 */
public abstract class AbstractH2BasedDAO {

    /**
     * convert to Database identity
     * 
     * @param reqContext
     * @return
     */
    protected String convertDBID(AnomalyEnvelope reqContext) {
        return "H2_" + reqContext.getDsName() + "_" + reqContext.getDsFreq();
    }
}
