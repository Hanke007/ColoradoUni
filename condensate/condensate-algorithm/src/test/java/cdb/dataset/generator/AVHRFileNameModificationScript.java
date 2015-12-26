package cdb.dataset.generator;

import java.io.File;
import java.util.Calendar;

import cdb.common.lang.DateUtil;

/**
 * 
 * @author Chao Chen
 * @version $Id: AVHRFileNameModificationScript.java, v 0.1 Dec 26, 2015 3:16:38 PM chench Exp $
 */
public class AVHRFileNameModificationScript {

    private static final String AVHR_ROOT_DIR = "C:/Users/chench/Desktop/SIDS/AVHR/2000abtm/";

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        File dRoot = new File(AVHR_ROOT_DIR);
        if (!dRoot.exists() || !dRoot.isDirectory()) {
            return;
        }

        Calendar cal = Calendar.getInstance();
        for (String fString : dRoot.list()) {
            File fObj = new File(AVHR_ROOT_DIR + fString);
            if (!fObj.isFile()) {
                continue;
            }

            int year = Integer.valueOf(fString.substring(9, 13));
            int day = Integer.valueOf(fString.substring(13, 16));
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.DAY_OF_YEAR, day);

            String dateStringInyyyyddd = fString.substring(9, 16);
            String dateStrInyyyymmdd = DateUtil.format(cal.getTime(), DateUtil.SHORT_FORMAT);
            String newfString = fString.replaceFirst(dateStringInyyyyddd, dateStrInyyyymmdd);

            File newfObj = new File(AVHR_ROOT_DIR + newfString);
            fObj.renameTo(newfObj);
        }
    }

}
