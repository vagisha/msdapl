/**
 * Ms2FileValidator.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.ms2File.parser;

import java.util.HashMap;
import java.util.List;

/**
 * 
 */
public class Ms2FileValidator {
    
    private HashMap <String, Boolean> requiredHeaders;
    
    private static final Ms2FileValidator instance = new Ms2FileValidator();
    
    private Ms2FileValidator(){
        initRequiredHeadersMap();
    }

    private void initRequiredHeadersMap() {
        requiredHeaders = new HashMap<String, Boolean>();
        requiredHeaders.put("CreationDate", false);
        requiredHeaders.put("Extractor", false);
        requiredHeaders.put("ExtractorVersion", false);
        requiredHeaders.put("ExtractorOptions", false);
    }
    
    public static Ms2FileValidator instance() {
        return instance;
    }
    
    /**
     * @param headers
     * @return true if all the required headers are present
     */
    public synchronized boolean validateHeaders(List <Ms2FileHeader> headers) {
        for (Ms2FileHeader header: headers) {
            if (requiredHeaders.get(header))
                requiredHeaders.remove(header);
        }
        boolean foundAll = requiredHeaders.size() == 0;
        initRequiredHeadersMap(); // re-initialize the map
        return foundAll;
    }
}
