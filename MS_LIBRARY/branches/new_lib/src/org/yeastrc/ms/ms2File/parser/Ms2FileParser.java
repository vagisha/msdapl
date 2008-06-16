/**
 * Ms2FileParser.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.ms2File.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 
 */
public class Ms2FileParser {

    private BufferedReader reader;
    
    public Ms2FileParser (String filePath) throws FileNotFoundException {
        try {
            reader = new BufferedReader(new FileReader(filePath));
        }
        finally {
            cleanUp();
        }
    }

    public List<Ms2FileHeader> getHeaders() throws IOException {
        
        String line = null;
        List <Ms2FileHeader> headers = new ArrayList<Ms2FileHeader>();
        while (true) {
            try {
                line = reader.readLine();
            }
            finally {
                cleanUp();
            }
            if (line == null && !line.startsWith("H")) {
                break;
            }
            
            
        }
        return headers;
    }
    
    private void cleanUp() {
        if (reader != null) 
            try {reader.close();}
            catch (IOException e) {}
    }
    
}
