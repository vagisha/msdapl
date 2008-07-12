package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.domain.MsSearch;
import org.yeastrc.ms.domain.db.MsSearchDbImpl;
import org.yeastrc.ms.domain.db.MsSearchResultDbImpl;

public class MsPeptideSearchDAOImplTest extends BaseDAOTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOperationsOnMsPeptideSearch() {
        
        assertEquals(0, searchDao.loadSearchesForRun(1).size());
        assertEquals(0, searchDao.loadSearchIdsForRun(1).size());
        
        // create and save a search with no seq. db information or modifications
        MsSearch search_1 = makePeptideSearch(1, false, false, false);
        int searchId_1 = searchDao.saveSearch(search_1);
        List<MsSearchDbImpl> searchList = (List<MsSearchDbImpl>) searchDao.loadSearchesForRun(1);
        assertEquals(1, searchList.size());
        assertEquals(search_1.getSearchDate().toString(), searchList.get(0).getSearchDate().toString());
        assertEquals(167, searchList.get(0).getSearchDuration());
        assertEquals(0, seqDbDao.loadSearchDatabases(searchId_1).size());
        assertEquals(0, modDao.loadStaticModificationsForSearch(searchId_1).size());
        assertEquals(0, modDao.loadDynamicModificationsForSearch(searchId_1).size());
        assertEquals(0, resultDao.loadResultIdsForSearch(searchId_1).size());
        
        // create and save a search with seq. db information and modifications
        MsSearch search_2 = makePeptideSearch(1, true, true, true);
        int searchId_2 = searchDao.saveSearch(search_2);
        searchList = (List<MsSearchDbImpl>) searchDao.loadSearchesForRun(1);
        assertEquals(2, searchList.size());
        assertEquals(2, seqDbDao.loadSearchDatabases(searchId_2).size());
        assertEquals(2, modDao.loadStaticModificationsForSearch(searchId_2).size());
        assertEquals(3, modDao.loadDynamicModificationsForSearch(searchId_2).size());
        
        // add results for the search
        MsSearchResultDbImpl r1 = makeSearchResult(searchId_2, 1, 3, "PEPTIDE1", true, true);
        MsSearchResultDbImpl r2 = makeSearchResult(searchId_2, 1, 3, "PEPTIDE1", true, true);
        int r1_id = resultDao.save(r1);
        int r2_id = resultDao.save(r2);
        assertEquals(2, resultDao.loadResultIdsForSearch(searchId_2).size());
        assertEquals(r1.getProteinMatchList().size(), matchDao.loadResultProteins(r1_id).size());
        assertEquals(r1.getDynamicModifications().size(), modDao.loadDynamicModificationsForSearchResult(r1_id).size());
        assertEquals(r2.getProteinMatchList().size(), matchDao.loadResultProteins(r2_id).size());
        assertEquals(r2.getDynamicModifications().size(), modDao.loadDynamicModificationsForSearchResult(r2_id).size());
        
        // delete the searches
        searchDao.deleteSearch(searchId_1);
        searchDao.deleteSearch(searchId_2);
        
        testSearchDeleted(1, searchId_1, new int[0] );
        testSearchDeleted(1, searchId_2, new int[]{r1_id, r2_id});
        
    }

    private void testSearchDeleted(int runId, int searchId, int[] resultIds) {
        assertEquals(0, searchDao.loadSearchIdsForRun(runId).size());
        assertEquals(0, seqDbDao.loadSearchDatabases(searchId).size());
        assertEquals(0, modDao.loadStaticModificationsForSearch(searchId).size());
        assertEquals(0, modDao.loadDynamicModificationsForSearch(searchId).size());
        assertEquals(0, resultDao.loadResultIdsForSearch(searchId).size());
        for (int id: resultIds) {
            assertEquals(0, matchDao.loadResultProteins(id).size());
            assertEquals(0, modDao.loadDynamicModificationsForSearchResult(id).size());
        }
    }
    
}
