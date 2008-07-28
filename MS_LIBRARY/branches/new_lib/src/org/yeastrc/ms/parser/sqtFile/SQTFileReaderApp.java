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

        String file = "./resources/ForTest.sqt";
        String outFile = "./resources/test.out";
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
        
        SQTFileReader reader = new SQTFileReader();
        try {
            reader.open(file);
            SQTHeader header = reader.getSearchHeader();
            System.out.println(header.toString());
            writer.write(header.toString());
            writer.write("\n");
            while (reader.hasNextSearchScan()) {
                ScanResult scan = reader.getNextSearchScan();
                System.out.println(scan.toString());
                writer.write(scan.toString());
                writer.write("\n");
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            reader.close();
        }
        writer.close();
    }

}
