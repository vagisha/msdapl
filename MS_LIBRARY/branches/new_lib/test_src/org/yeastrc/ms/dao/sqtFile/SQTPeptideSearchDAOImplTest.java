package org.yeastrc.ms.dao.sqtFile;

import java.util.List;

import org.yeastrc.ms.domain.sqtFile.SQTSearch;
import org.yeastrc.ms.domain.sqtFile.impl.SQTSearchDbImpl;


public class SQTPeptideSearchDAOImplTest extends SQTBaseDAOTestCase {

   
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOperationsOnSQTPeptideSearch () {
        
        // no saved search exists right now
        assertNull(sqtSearchDao.loadSearch(1));
        
        // save a search (don't add any SQT headers)
        SQTSearchDbImpl search_1 = makeSQTPeptideSearch(1, false, false, false, false);
        assertEquals(0, search_1.getHeaders().size());
        int searchId_1 = sqtSearchDao.saveSearch(search_1);
        
        // load the search using the general MsPeptideSearch DAO and make sure
        // the class of the returned object is SQTSearchResult
        assertTrue(searchDao.loadSearch(searchId_1) instanceof SQTSearch);
        assertFalse(null instanceof SQTSearch);
        
        // load using our specialized SQTSearchDAO
        SQTSearch search_1_db = sqtSearchDao.loadSearch(searchId_1);
        assertNotNull(search_1_db);
        assertEquals(0, search_1_db.getHeaders().size());
        
        // save another search (add SQT headers)
        SQTSearchDbImpl search_2 = makeSQTPeptideSearch(1, false, false, false, true);
        assertEquals(2, search_2.getHeaders().size());
        int searchId_2 = sqtSearchDao.saveSearch(search_2);
        
        // load the search with headers and check values
        SQTSearchDbImpl search_2_db = sqtSearchDao.loadSearch(searchId_2);
        assertNotNull(search_2_db);
        assertEquals(2, search_2_db.getHeaders().size());
        assertEquals(search_2.getRunId(), search_2_db.getRunId());
        assertEquals(search_2.getOriginalFileType(), search_2_db.getOriginalFileType());
        assertEquals(search_2.getSearchEngineName(), search_2_db.getSearchEngineName());
        assertEquals(search_2.getSearchEngineVersion(), search_2_db.getSearchEngineVersion());
        assertEquals(search_2.getSearchDate().toString(), search_2_db.getSearchDate().toString());
        assertEquals(search_2.getSearchDuration(), search_2_db.getSearchDuration());
        assertEquals(search_2.getPrecursorMassType(), search_2_db.getPrecursorMassType());
        assertEquals(search_2.getPrecursorMassTolerance().doubleValue(), search_2_db.getPrecursorMassTolerance().doubleValue());
        assertEquals(search_2.getFragmentMassType(), search_2_db.getFragmentMassType());
        assertEquals(search_2.getFragmentMassTolerance().doubleValue(), search_2_db.getFragmentMassTolerance().doubleValue());
        
        // load both searches; make sure the right object types are returned
        List<SQTSearchDbImpl> searches = sqtSearchDao.loadSearchesForRun(1);
        assertEquals(2, searches.size());
        assertTrue(searches.get(0) instanceof SQTSearch);
        assertTrue(searches.get(1) instanceof SQTSearch);
        
        // delete the searches
        sqtSearchDao.deleteSearch(searchId_1);
        assertNull(sqtSearchDao.loadSearch(searchId_1));
        assertEquals(0, sqtHeaderDao.loadSQTHeadersForSearch(searchId_1).size());
        
        sqtSearchDao.deleteSearch(searchId_2);
        assertNull(sqtSearchDao.loadSearch(searchId_2));
        assertEquals(0, sqtHeaderDao.loadSQTHeadersForSearch(searchId_2).size());
        
    }
}
