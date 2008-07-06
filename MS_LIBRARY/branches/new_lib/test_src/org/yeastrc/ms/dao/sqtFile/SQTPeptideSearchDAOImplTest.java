package org.yeastrc.ms.dao.sqtFile;

import org.yeastrc.ms.dto.sqtFile.SQTPeptideSearch;


public class SQTPeptideSearchDAOImplTest extends SQTBaseDAOTestCase {

   
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOperationsOnSQTPeptideSearch () {
        
        // no saved search exists right now
        assertNull(sqtSearchDao.load(1));
        
        // save a search (don't add any SQT headers)
        SQTPeptideSearch search_1 = makeSQTPeptideSearch(1, false, false, false, false);
        assertEquals(0, search_1.getHeaders().size());
        int searchId_1 = sqtSearchDao.save(search_1);
        
        // load the search using the general MsPeptideSearch DAO and make sure
        // the class of the returned object is SQTSearchResult
        assertTrue(searchDao.loadSearch(searchId_1) instanceof SQTPeptideSearch);
        assertFalse(null instanceof SQTPeptideSearch);
        
        // load using our specialized SQTSearchDAO
        SQTPeptideSearch search_1_db = sqtSearchDao.load(searchId_1);
        assertNotNull(search_1_db);
        assertEquals(0, search_1_db.getHeaders().size());
        
        // save another search (add SQT headers)
        SQTPeptideSearch search_2 = makeSQTPeptideSearch(1, false, false, false, true);
        assertEquals(2, search_2.getHeaders().size());
        int searchId_2 = sqtSearchDao.save(search_2);
        
        // load the search with headers and check values
        SQTPeptideSearch search_2_db = sqtSearchDao.load(searchId_2);
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
        
        // delete the searches
        sqtSearchDao.delete(searchId_1);
        assertNull(sqtSearchDao.load(searchId_1));
        assertEquals(0, sqtHeaderDao.loadSQTHeadersForSearch(searchId_1).size());
        
        sqtSearchDao.delete(searchId_2);
        assertNull(sqtSearchDao.load(searchId_2));
        assertEquals(0, sqtHeaderDao.loadSQTHeadersForSearch(searchId_2).size());
        
    }
}
