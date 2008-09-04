package org.yeastrc.ms.dao.search;

import java.sql.Date;
import java.util.Collections;
import java.util.List;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsRunSearchDb;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.SearchProgram;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearchDb;

public class MsRunSearchDAOImplTest extends BaseDAOTestCase {

    private static int searchId_1 = 34;
    
    protected void setUp() throws Exception {
        super.setUp();
        
        // modifications for searchId_1
        MsResidueModification mod1 = makeStaticResidueMod('C', "50.0");
        modDao.saveStaticResidueMod(mod1, searchId_1);
        MsResidueModification mod2 = makeStaticResidueMod('S', "80.0");
        modDao.saveStaticResidueMod(mod2, searchId_1);
        
        MsResidueModification dmod1 = makeDynamicResidueMod('A', "10.0", '*');
        modDao.saveDynamicResidueMod(dmod1, searchId_1);
        MsResidueModification dmod2 = makeDynamicResidueMod('B', "20.0", '#');
        modDao.saveDynamicResidueMod(dmod2, searchId_1);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        // delete modifications for searchId_1
        modDao.deleteDynamicResidueModsForSearch(searchId_1);
        modDao.deleteStaticResidueModsForSearch(searchId_1);
    }

    public void testOperationsOnMsRunSearch() {

        int runId = 21;

        assertEquals(0, runSearchDao.loadRunSearchIdsForRun(runId).size()); 

        // create and save a run search
        MsRunSearch runSearch_1 = makeRunSearch(SearchFileFormat.SQT_SEQ);
        assertEquals(167, runSearch_1.getSearchDuration());
        assertEquals(SearchFileFormat.SQT_SEQ, runSearch_1.getSearchFileFormat());

        int runSearchId_1 = runSearchDao.saveRunSearch(runSearch_1, runId, 32); // runId = 21; searchId = 32
        List<Integer> idList = runSearchDao.loadRunSearchIdsForRun(runId);
        assertEquals(1, idList.size());
        assertEquals(runSearchId_1, idList.get(0).intValue());
        assertEquals(0, resultDao.loadResultIdsForRunSearch(runSearchId_1).size());
        MsRunSearchDb runSearchDb_1 = runSearchDao.loadRunSearch(idList.get(0));
        checkRunSearch(runSearch_1, runSearchDb_1);
        assertEquals(32, runSearchDb_1.getSearchId());

        // create and save another run search for the same run
        MsRunSearch search_2 = makeRunSearch(SearchFileFormat.SQT_PLUCID);
        int runSearchId_2 = runSearchDao.saveRunSearch(search_2, runId, 87); // runId = 21; searchId = 87
        idList = runSearchDao.loadRunSearchIdsForRun(runId);
        assertEquals(2, idList.size());
        Collections.sort(idList);
        assertEquals(runSearchId_1, idList.get(0).intValue());
        assertEquals(runSearchId_2, idList.get(1).intValue());

        runSearchDb_1 = runSearchDao.loadRunSearch(idList.get(0));
        checkRunSearch(runSearch_1, runSearchDb_1);
        assertEquals(32, runSearchDb_1.getSearchId());
        MsRunSearchDb runSearchDb_2 = runSearchDao.loadRunSearch(idList.get(1));
        checkRunSearch(search_2, runSearchDb_2);
        assertEquals(87, runSearchDb_2.getSearchId());

        // add results for the searches
        MsSearchResult r1 = makeSearchResult(searchId_1, runSearchId_2, 3, "PEPTIDE1", true); // charge = 3
        MsSearchResult r2 = makeSearchResult(searchId_1, runSearchId_2, 3, "PEPTIDE1", true); // charge = 3;
        int r1_id = resultDao.save(searchId_1, "dummy_db", r1, runSearchId_2, 2);
        int r2_id = resultDao.save(searchId_1, "dummy_db", r2, runSearchId_2, 3);
        assertEquals(2, resultDao.loadResultIdsForRunSearch(runSearchId_2).size());

        assertTrue(r1.getProteinMatchList().size() == 0);
        assertEquals(r1.getProteinMatchList().size(), matchDao.loadResultProteins(r1_id).size());
        assertTrue(r1.getResultPeptide().getResultDynamicResidueModifications().size() > 0);
        assertEquals(r1.getResultPeptide().getResultDynamicResidueModifications().size(), 
                modDao.loadDynamicResidueModsForResult(r1_id).size());


        assertTrue(r2.getProteinMatchList().size() ==  0);
        assertEquals(r2.getProteinMatchList().size(), matchDao.loadResultProteins(r2_id).size());
        assertTrue(r2.getResultPeptide().getResultDynamicResidueModifications().size() > 0);
        assertEquals(r2.getResultPeptide().getResultDynamicResidueModifications().size(),
                modDao.loadDynamicResidueModsForResult(r2_id).size());

        // delete the searches
        runSearchDao.deleteRunSearch(runSearchId_1);
        runSearchDao.deleteRunSearch(runSearchId_2);

        testRunSearchDeleted(runId, runSearchId_1, new int[0] );
        testRunSearchDeleted(runId, runSearchId_2, new int[]{r1_id, r2_id});
    }

    
    public void testReturnedSearchType() {
      MsRunSearch search = makeRunSearch(SearchFileFormat.SQT_SEQ);
      assertEquals(SearchFileFormat.SQT_SEQ, search.getSearchFileFormat());
      int runSearchId_1 = runSearchDao.saveRunSearch(search, 21, 0); // runId = 21
      MsRunSearchDb searchDb = runSearchDao.loadRunSearch(runSearchId_1);
      assertTrue(searchDb instanceof SQTRunSearchDb);
      assertEquals(SearchFileFormat.SQT_SEQ, searchDb.getSearchFileFormat());
      
      search = makeRunSearch(SearchFileFormat.PEPXML);
      assertEquals(SearchFileFormat.PEPXML, search.getSearchFileFormat());
      int runSearchId_2 = runSearchDao.saveRunSearch(search, 21, 0);
      searchDb = runSearchDao.loadRunSearch(runSearchId_2);
      assertTrue(searchDb instanceof MsRunSearchDb);
      assertFalse(searchDb instanceof SQTRunSearchDb);
      assertEquals(SearchFileFormat.PEPXML, searchDb.getSearchFileFormat());
      
      runSearchDao.deleteRunSearch(runSearchId_1);
      runSearchDao.deleteRunSearch(runSearchId_2);
      
      testRunSearchDeleted(21, runSearchId_1, new int[0] );
      testRunSearchDeleted(21, runSearchId_2, new int[0] );
      
  }
    
    private void testRunSearchDeleted(int runId, int runSearchId, int[] resultIds) {
        assertEquals(0, runSearchDao.loadRunSearchIdsForRun(runId).size());
        assertEquals(0, resultDao.loadResultIdsForRunSearch(runSearchId).size());
        for (int id: resultIds) {
            assertEquals(0, matchDao.loadResultProteins(id).size());
            assertEquals(0, modDao.loadDynamicResidueModsForResult(id).size());
        }
    }

    public static class MsRunSearchTest implements MsRunSearch {

        private Date searchDate;
        private SearchFileFormat fileFormat;
        private int searchDuration;

        public Date getSearchDate() {
            return searchDate;
        }

        @Override
        public int getSearchDuration() {
            return searchDuration;
        }

        @Override
        public SearchFileFormat getSearchFileFormat() {
            return fileFormat;
        }

        public void setSearchDate(Date searchDate) {
            this.searchDate = searchDate;
        }

        public void setFileFormat(SearchFileFormat fileFormat) {
            this.fileFormat = fileFormat;
        }

        public void setSearchDuration(int searchDuration) {
            this.searchDuration = searchDuration;
        }

        @Override
        public SearchProgram getSearchProgram() {
            return SearchProgram.programForFileFormat(fileFormat);
        }
    }
}
