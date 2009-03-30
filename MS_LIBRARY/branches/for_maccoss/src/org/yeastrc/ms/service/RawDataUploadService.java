/**
 * RawDataUploadService.java
 * @author Vagisha Sharma
 * Mar 25, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service;

import java.util.List;

import org.yeastrc.ms.domain.run.RunFileFormat;

public interface RawDataUploadService extends UploadService {

    public void setExperimentId(int experimentId);
    
    public List<String> getFileNames();
    
    public RunFileFormat getFileFormat();
}
