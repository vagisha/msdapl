package org.yeastrc.ms.parser.sqtFile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class SQTFileReaderApp {

    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {

        String file = "./resources/PARC_p75_01_itms.sqt";
        String outFile = "./resources/test.out";
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
        
        SQTFileReader reader = new SQTFileReader();
        try {
            reader.open(file);
            Header header = reader.getHeader();
            System.out.println(header.toString());
//            writer.write(header.toString());
//            writer.write("\n");
            while (reader.hasScans()) {
                ScanResult scan = reader.getNextScan();
                System.out.println(scan.toString());
//                writer.write(scan.toString());
//                writer.write("\n");
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        writer.close();
    }

}
