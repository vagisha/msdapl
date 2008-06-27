/**
 * Ms2FileRun.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dto.ms2File;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.dto.MsRun;

/**
 * 
 */
public class Ms2FileRun extends MsRun {

    private List<MS2FileHeader> ms2FileHeaders;

    public Ms2FileRun() {
        ms2FileHeaders = new ArrayList<MS2FileHeader>();
    }
    
    /**
     * @return the ms2FileHeaders
     */
    public List<MS2FileHeader> getMs2FileHeaders() {
        return ms2FileHeaders;
    }

    /**
     * @param ms2FileHeaders the ms2FileHeaders to set
     */
    public void setMs2FileHeaders(List<MS2FileHeader> ms2FileHeaders) {
        this.ms2FileHeaders = ms2FileHeaders;
    }
    
    public void addMs2FileHeader(MS2FileHeader header) {
        ms2FileHeaders.add(header);
    }
}
