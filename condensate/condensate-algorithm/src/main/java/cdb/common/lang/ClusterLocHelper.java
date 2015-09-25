package cdb.common.lang;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import cdb.dal.vo.Location;
import cdb.ml.clustering.Cluster;

/**
 * 
 * @author Chao Chen
 * @version $Id: ClusterLocHelper.java, v 0.1 Sep 15, 2015 3:51:20 PM chench Exp $
 */
public final class ClusterLocHelper {

    /**
     * forbidden construction
     */
    private ClusterLocHelper() {
    }

    /**
     * save the detailed 2D indexes of the given clustering result
     * 
     * @param clusters      the clustering result
     * @param fileName      the target file to store the clustering information
     * @param rowNum        the number of the rows
     * @param colNum        the number of the columns
     */
    public static void saveLoc(Cluster[] clusters, String fileName, int rowNum, int colNum) {
        FileUtil.existDirAndMakeDir(fileName);

        int clusterIndex = 0;
        StringBuilder content = new StringBuilder();
        for (Cluster cluster : clusters) {
            content.append(clusterIndex++).append("::");

            for (int index : cluster) {
                int row = index / colNum;
                int col = index % colNum;

                content.append(row).append(':').append(col).append(',');
            }
            content.deleteCharAt(content.length() - 1);
            content.append('\n');
        }

        FileUtil.write(fileName, content.toString());
    }

    public static void readLoc(String fileName, List<List<Location>> locSet) {
        // check essential information
        File file = new File(fileName);
        if (!file.isFile() | !file.exists()) {
            ExceptionUtil.caught(new FileNotFoundException("File Not Found"), fileName);
        }

        // read and parse locations
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                List<Location> oneSet = new ArrayList<Location>();

                int colonIndx = line.indexOf("::");
                String[] eleSet = line.substring(colonIndx + 2).split("\\,");
                for (String ele : eleSet) {
                    String[] indices = ele.split("\\:");
                    oneSet.add(new Location(Integer.parseInt(indices[0]), Integer
                        .parseInt(indices[1])));
                }
                locSet.add(oneSet);
            }
        } catch (FileNotFoundException e) {
            ExceptionUtil.caught(e, file);
        } catch (IOException e) {
            ExceptionUtil.caught(e, file);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }
}
