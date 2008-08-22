/**
 * SequestSearchScan.java
 * @author Vagisha Sharma
 * Aug 21, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.sequest;

import java.util.List;

import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScan;

/**
 * 
 */
public interface SequestSearchScan extends SQTSearchScan {

    public abstract List<SequestSearchResult> getScanResults();
}
