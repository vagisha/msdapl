/**
 * Ms2FileReaderApp.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.ms2File;

import org.yeastrc.ms.domain.ms2File.MS2Scan;


/**
 * 
 */
public class Ms2FileReaderApp {

    /**
     * @param args
     */
    public static void main(String[] args) {
        String file = "./resources/sample.ms2";
        Ms2FileReader reader = new Ms2FileReader();
        try {
            reader.open(file);
            MS2Header header = reader.getRunHeader();
            System.out.println(header.toString());
            while (reader.hasNextScan()) {
                MS2Scan scan = reader.getNextScan();
                System.out.println(scan.toString());
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
