/**
 * ProlucidSearchResultDAO.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.prolucid;

import java.util.List;

import org.yeastrc.ms.dao.search.GenericSearchResultDAO;
import org.yeastrc.ms.domain.search.prolucid.ProlucidResultDataWId;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResultIn;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResult;

/**
 * 
 */
public interface ProlucidSearchResultDAO extends GenericSearchResultDAO<ProlucidSearchResultIn, ProlucidSearchResult>  {

    public abstract void saveAllProlucidResultData(List<ProlucidResultDataWId> resultDataList);
}
