/**
 * SearchDataUploadService.java
 * @author Vagisha Sharma
 * Mar 25, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service;

/**
 * 
 */
public interface SearchDataUploadService extends UploadService {

    public void setExperimentId(int experimentId);
    
    public void setDecoyDirectory(String directory);
    
    public void setRemoteDecoyDirectory(String directory);
    
}
