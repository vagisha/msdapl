/**
 * ProlucidSearchResultDAO.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.prolucid;

import java.util.List;

import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.domain.search.prolucid.ProlucidResultDataDb;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResult;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResultDb;

/**
 * 
 */
public interface ProlucidSearchResultDAO extends MsSearchResultDAO<ProlucidSearchResult, ProlucidSearchResultDb>  {

    public abstract void saveAllProlucidResultData(List<ProlucidResultDataDb> resultDataList);
}
