/**
 * SequestRunSearchResultDb.java
 * @author Vagisha Sharma
 * Aug 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.sequest;

import org.yeastrc.ms.domain.search.MsSearchResultDb;


/**
 * 
 */
public interface SequestSearchResultDb extends MsSearchResultDb {
    
    public SequestResultData getSequestResultData();
}
