/**
 * MS2FileRun.java
 * @author Vagisha Sharma
 * Jul 2, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.run.ms2file.impl;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.run.impl.RunBean;
import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;
import org.yeastrc.ms.domain.run.ms2file.MS2Run;

/**
 * 
 */
public class MS2RunBean extends RunBean implements MS2Run {

    private List<MS2NameValuePair> headers;
    
    public MS2RunBean() {
        headers = new ArrayList<MS2NameValuePair>();
    }
    
    public void setHeaderList(List<MS2NameValuePair> headers) {
        this.headers = headers;
    }
    
    public List<MS2NameValuePair> getHeaderList() {
        return headers;
    }
}
