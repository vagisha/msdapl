/**
 * ProlucidSearchScan.java
 * @author Vagisha Sharma
 * Aug 31, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.prolucid;

import java.util.List;

import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScan;

/**
 * 
 */
public interface ProlucidSearchScan extends SQTSearchScan {
    
    public abstract List<ProlucidSearchResult> getScanResults();
}
