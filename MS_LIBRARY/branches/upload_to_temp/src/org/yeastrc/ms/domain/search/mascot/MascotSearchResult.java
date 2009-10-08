/**
 * SequestRunSearchResultDb.java
 * @author Vagisha Sharma
 * Aug 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.mascot;

import org.yeastrc.ms.domain.search.MsSearchResult;


/**
 * 
 */
public interface MascotSearchResult extends MsSearchResult {
    
    public MascotResultData getMascotResultData();
}
