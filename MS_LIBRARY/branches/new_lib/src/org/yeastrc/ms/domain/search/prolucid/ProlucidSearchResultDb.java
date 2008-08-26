/**
 * ProlucidSearchResultDb.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.prolucid;

import org.yeastrc.ms.domain.search.MsSearchResultDb;

/**
 * 
 */
public interface ProlucidSearchResultDb extends MsSearchResultDb {
    
    public abstract ProlucidResultData getProlucidResultData();
}
