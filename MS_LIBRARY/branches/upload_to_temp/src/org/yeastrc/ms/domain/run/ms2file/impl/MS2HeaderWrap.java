/**
 * MS2HeaderDbImpl.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.run.ms2file.impl;

import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;


/**
 * 
 */
public class MS2HeaderWrap  implements MS2NameValuePair {

    private int runId;                  
    private MS2NameValuePair header;
    
    public MS2HeaderWrap(MS2NameValuePair header, int runId) {
        this.header = header;
        this.runId = runId;
    }
    
    public int getRunId() {
        return runId;
    }
    /**
     * @param runId the runId to set
     */
    public void setRunId(int runId) {
        this.runId = runId;
    }
    
    @Override
    public String getName() {
        return header.getName();
    }

    @Override
    public String getValue() {
        return header.getValue();
    }

}
