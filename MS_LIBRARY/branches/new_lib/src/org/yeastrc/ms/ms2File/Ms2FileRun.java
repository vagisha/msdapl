/**
 * Ms2FileRun.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.ms2File;

import org.yeastrc.ms.dto.MsRun;

/**
 * 
 */
public class Ms2FileRun extends MsRun {

    private Ms2FileHeaders ms2FileHeaders;

    /**
     * @return the ms2FileHeaders
     */
    public Ms2FileHeaders getMs2FileHeaders() {
        return ms2FileHeaders;
    }

    /**
     * @param ms2FileHeaders the ms2FileHeaders to set
     */
    public void setMs2FileHeaders(Ms2FileHeaders ms2FileHeaders) {
        this.ms2FileHeaders = ms2FileHeaders;
    }
}
