/**
 * Ms2FileReaderApp.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.ms2File;

import java.io.File;

import org.yeastrc.ms.domain.ms2File.MS2Scan;
import org.yeastrc.ms.util.Sha1SumCalculator;


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
            String sha1Sum = Sha1SumCalculator.instance().sha1SumFor(new File(file));
            reader.open(file, sha1Sum);
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
        finally {
            reader.close();
        }
    }

}
