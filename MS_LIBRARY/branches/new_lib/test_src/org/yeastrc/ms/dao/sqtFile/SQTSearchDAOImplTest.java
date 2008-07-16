package org.yeastrc.ms.dao.sqtFile;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.dao.MsSearchDAOImplTest.MsSearchTest;
import org.yeastrc.ms.domain.sqtFile.SQTField;
import org.yeastrc.ms.domain.sqtFile.SQTSearch;
import org.yeastrc.ms.domain.sqtFile.SQTSearchDb;


public class SQTSearchDAOImplTest extends SQTBaseDAOTestCase {

   
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
        SQTSearch search_1 = makeSQTSearch(false, false, false, false);
        assertEquals(0, search_1.getHeaders().size());
        int searchId_1 = sqtSearchDao.saveSearch(search_1, 1); // runId = 1
        
        // load the search using the general MsPeptideSearch DAO and make sure
        // the class of the returned object is SQTSearchResult
        assertTrue(searchDao.loadSearch(searchId_1) instanceof SQTSearchDb);
        assertFalse(null instanceof SQTSearchDb);
        
        // load using our specialized SQTSearchDAO
        SQTSearchDb search_1_db = sqtSearchDao.loadSearch(searchId_1);
        assertNotNull(search_1_db);
        assertEquals(0, search_1_db.getHeaders().size());
        checkSQTSearch(search_1, search_1_db);
        
        // save another search (add SQT headers)
        SQTSearch search_2 = makeSQTSearch(false, false, false, true);
        assertEquals(2, search_2.getHeaders().size());
        int searchId_2 = sqtSearchDao.saveSearch(search_2, 1); // runId = 1
        
        // load the search with headers and check values
        SQTSearchDb search_2_db = sqtSearchDao.loadSearch(searchId_2);
        assertNotNull(search_2_db);
        assertEquals(2, search_2_db.getHeaders().size());
        assertEquals(1,  search_2_db.getRunId());
        checkSQTSearch(search_2, search_2_db);
        
        // load both searches; make sure the right object types are returned
        List<SQTSearchDb> searches = sqtSearchDao.loadSearchesForRun(1);
        assertEquals(2, searches.size());
        assertTrue(searches.get(0) instanceof SQTSearchDb);
        assertTrue(searches.get(1) instanceof SQTSearchDb);
        
        // delete the searches
        sqtSearchDao.deleteSearch(searchId_1);
        assertNull(sqtSearchDao.loadSearch(searchId_1));
        assertEquals(0, sqtHeaderDao.loadSQTHeadersForSearch(searchId_1).size());
        
        sqtSearchDao.deleteSearch(searchId_2);
        assertNull(sqtSearchDao.loadSearch(searchId_2));
        assertEquals(0, sqtHeaderDao.loadSQTHeadersForSearch(searchId_2).size());
        
    }
    
    private void checkSQTSearch(SQTSearch input, SQTSearchDb output) {
        super.checkSearch(input, output);
        assertEquals(input.getHeaders().size(), output.getHeaders().size());
    }
    
    public static final class SQTSearchTest extends MsSearchTest implements SQTSearch {

        private List<SQTField> headers = new ArrayList<SQTField>();

        public List<SQTField> getHeaders() {
            return headers ;
        }

        public void setHeaders(List<SQTField> headers) {
            this.headers = headers;
        }

        public void addHeader(SQTField header) {
            headers.add(header);
        }
    }
}
