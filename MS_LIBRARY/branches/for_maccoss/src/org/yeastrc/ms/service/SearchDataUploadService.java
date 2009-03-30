/**
 * SearchDataUploadService.java
 * @author Vagisha Sharma
 * Mar 25, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service;

import java.util.List;

import org.yeastrc.ms.domain.run.RunFileFormat;

/**
 * 
 */
public interface SearchDataUploadService extends UploadService {

    public void setExperimentId(int experimentId);
    
    public void setSearchDate(java.util.Date date);
    
    public void setRawDataFileNames(List<String> rawDataFileNames, RunFileFormat format);
    
    public void setDecoyDirectory(String directory);
    
    public void setRemoteDecoyDirectory(String directory);
    
    /**
     * If true, the charge and mass reported for a search result will be matched against 
     * the charge and mass used for the actual search.  If no match is found the result
     * will not be uploaded.
     * NOTE: This was added to deal with buggy SQT files from the MacCoss lab.
     * @param check
     */
    public void checkResultChargeMass(boolean check);
    
}
