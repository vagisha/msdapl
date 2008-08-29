package org.yeastrc.ms.dao.search.sequest;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.dao.search.MsRunSearchDAOImplTest.MsRunSearchTest;
import org.yeastrc.ms.dao.search.sqtfile.SQTBaseDAOTestCase;
import org.yeastrc.ms.domain.search.sqtfile.SQTField;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearch;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearchDb;


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
        SQTRunSearch search_1 = makeSQTRunSearch(false, false, false, false);
        assertEquals(0, search_1.getHeaders().size());
        int searchId_1 = sqtSearchDao.saveRunSearch(search_1, 1, 1); // runId = 1; // searchGroupId = 1
        
        // load the search using the general MsPeptideSearch DAO and make sure
        // the class of the returned object is SQTSearchResult
        assertTrue(searchDao.loadSearch(searchId_1) instanceof SQTRunSearchDb);
        assertFalse(null instanceof SQTRunSearchDb);
        
        // load using our specialized SQTSearchDAO
        SQTRunSearchDb search_1_db = sqtSearchDao.loadSearch(searchId_1);
        assertNotNull(search_1_db);
        assertEquals(0, search_1_db.getHeaders().size());
        checkSQTSearch(search_1, search_1_db);
        
        // save another search (add SQT headers)
        SQTRunSearch search_2 = makeSQTRunSearch(false, false, false, true);
        assertEquals(2, search_2.getHeaders().size());
        int searchId_2 = sqtSearchDao.saveRunSearch(search_2, 1, 1); // runId = 1; // searchGroupId = 1
        
        // load the search with headers and check values
        SQTRunSearchDb search_2_db = sqtSearchDao.loadSearch(searchId_2);
        assertNotNull(search_2_db);
        assertEquals(2, search_2_db.getHeaders().size());
        assertEquals(1,  search_2_db.getRunId());
        checkSQTSearch(search_2, search_2_db);
        
        // load both searches; make sure the right object types are returned
        List<SQTRunSearchDb> searches = sqtSearchDao.loadSearchesForRun(1);
        assertEquals(2, searches.size());
        assertTrue(searches.get(0) instanceof SQTRunSearchDb);
        assertTrue(searches.get(1) instanceof SQTRunSearchDb);
        
        // delete the searches
        sqtSearchDao.deleteSearch(searchId_1);
        assertNull(sqtSearchDao.loadSearch(searchId_1));
        assertEquals(0, sqtHeaderDao.loadSQTHeadersForRunSearch(searchId_1).size());
        
        sqtSearchDao.deleteSearch(searchId_2);
        assertNull(sqtSearchDao.loadSearch(searchId_2));
        assertEquals(0, sqtHeaderDao.loadSQTHeadersForRunSearch(searchId_2).size());
        
    }
    
    private void checkSQTSearch(SQTRunSearch input, SQTRunSearchDb output) {
        super.checkSearch(input, output);
        assertEquals(input.getHeaders().size(), output.getHeaders().size());
    }
    
    public static final class SQTRunSearchTest extends MsRunSearchTest implements SQTRunSearch {

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
