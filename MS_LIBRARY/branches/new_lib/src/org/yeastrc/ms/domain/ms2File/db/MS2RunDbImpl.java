/**
 * MS2FileRun.java
 * @author Vagisha Sharma
 * Jul 2, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.ms2File.db;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.db.MsRunDbImpl;
import org.yeastrc.ms.domain.ms2File.MS2HeaderDb;
import org.yeastrc.ms.domain.ms2File.MS2RunDb;

/**
 * 
 */
public class MS2RunDbImpl extends MsRunDbImpl implements MS2RunDb {

    private List<? super MS2HeaderDb> headers;
    
    public MS2RunDbImpl() {
        headers = new ArrayList<MS2HeaderDb>();
    }
    
    public void setHeaderList(List<? super MS2HeaderDb> headers) {
        this.headers = headers;
    }
    
    public List<MS2HeaderDb> getHeaderList() {
        return (List<MS2HeaderDb>) headers;
    }
    
    public void addMS2Header(MS2HeaderDbImpl header) {
        headers.add(header);
    }
}
