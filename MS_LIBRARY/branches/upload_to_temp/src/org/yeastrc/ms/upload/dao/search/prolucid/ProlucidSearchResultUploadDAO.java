/**
 * ProlucidSearchResultDAO.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search.prolucid;

import java.util.List;

import org.yeastrc.ms.domain.search.prolucid.ProlucidResultDataWId;
import org.yeastrc.ms.upload.dao.search.GenericSearchResultUploadDAO;

/**
 * 
 */
public interface ProlucidSearchResultUploadDAO extends GenericSearchResultUploadDAO {

    public abstract void saveAllProlucidResultData(List<ProlucidResultDataWId> resultDataList);
    
}
