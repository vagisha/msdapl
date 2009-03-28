/**
 * SearchDataUploadService.java
 * @author Vagisha Sharma
 * Mar 25, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service;

import java.util.List;

/**
 * 
 */
public interface SearchDataUploadService extends UploadService {

    public void setExperimentId(int experimentId);
    
    public void setSearchDate(java.util.Date date);
    
    public void setRawDataFileNames(List<String> rawDataFileNames);
    
    public void setDecoyDirectory(String directory);
    
    public void setRemoteDecoyDirectory(String directory);
    
}
