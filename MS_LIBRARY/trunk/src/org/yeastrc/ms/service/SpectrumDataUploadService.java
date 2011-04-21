/**
 * SpectrumDataUploadService.java
 * @author Vagisha Sharma
 * Mar 25, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service;

import java.util.List;

public interface SpectrumDataUploadService extends UploadService {

    public void setExperimentId(int experimentId);
    
    /**
     * Returns the filenames WITHOUT extensions
     * @return
     */
    public List<String> getFileNames();
    
//    public RunFileFormat getFileFormat();
}
