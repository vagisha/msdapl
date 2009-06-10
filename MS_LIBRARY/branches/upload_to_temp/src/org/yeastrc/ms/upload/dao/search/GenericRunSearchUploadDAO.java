/**
 * MsRunSearchDAO.java
 * @author Vagisha Sharma
 * Aug 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search;

import org.yeastrc.ms.domain.search.MsRunSearch;

/**
 * 
 */
public interface GenericRunSearchUploadDAO <O extends MsRunSearch> {

    public abstract O loadRunSearch(int runSearchId);
    
    
    /**
     * Returns the database id of a search on the given filename and belonging 
     * to the given search group.
     * @param searchId
     * @param filename
     * @return
     */
    public abstract int loadIdForSearchAndFileName(int searchId, String filename);
    
    
    /**
     * Saves the search (for a single run) in the database.
     * @param search
     * @return database id of the search
     */
    public abstract int saveRunSearch(O search);
    
}
