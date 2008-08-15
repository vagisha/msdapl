package org.yeastrc.ms.dao;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsRunSearchDb;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsSearchModification;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearchDb;

public class MsSearchDAOImplTest extends BaseDAOTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOperationsOnMsPeptideSearch() {
        
        int runId_1 = 21;
        assertEquals(0, searchDao.loadSearchesForRun(runId_1).size()); // runId = 1
        assertEquals(0, searchDao.loadSearchIdsForRun(runId_1).size()); // runId = 1
        
        // create and save a search with no seq. db information or modifications or enzymes
        MsRunSearch search_1 = makePeptideSearch(SearchFileFormat.SQT_SEQ, false, false, false, false);
        assertEquals(167, search_1.getSearchDuration());
        assertEquals(0, search_1.getSearchDatabases().size());
        assertEquals(0, search_1.getStaticModifications().size());
        assertEquals(0, search_1.getDynamicModifications().size());
        
        int searchId_1 = searchDao.saveSearch(search_1, runId_1, 32); // runId = 21; experimentId = 32
        List<MsRunSearchDb> searchList = (List<MsRunSearchDb>) searchDao.loadSearchesForRun(runId_1);
        assertEquals(1, searchList.size());
        assertEquals(0, resultDao.loadResultIdsForSearch(searchId_1).size());
        checkSearch(search_1, searchList.get(0));
        
        
        // create and save a search with seq. db information and modifications AND enzymes
        MsRunSearch search_2 = makePeptideSearch(SearchFileFormat.SQT_SEQ, true, true, true, true);
        assertTrue(search_2.getSearchDatabases().size() > 0);
        assertTrue(search_2.getStaticModifications().size() > 0);
        assertTrue(search_2.getDynamicModifications().size() > 0);
        
        
        int runId_2 = 23;
        int searchId_2 = searchDao.saveSearch(search_2, runId_2, 32); // runId = 23; experimentId = 32
        searchList = searchDao.loadSearchesForRun(runId_2);
        assertEquals(1, searchList.size());
        assertEquals(2, seqDbDao.loadSearchDatabases(searchId_2).size());
        assertEquals(2, modDao.loadStaticModificationsForSearch(searchId_2).size());
        assertEquals(3, modDao.loadDynamicModificationsForSearch(searchId_2).size());
        assertEquals(2, enzymeDao.loadEnzymesForSearch(searchId_2).size());
        checkSearch(search_2, searchList.get(0));
        
        // add results for the search
        MsSearchResult r1 = makeSearchResult(searchId_2, 3, "PEPTIDE1", true, true); // charge = 3
        MsSearchResult r2 = makeSearchResult(searchId_2, 3, "PEPTIDE1", true, true); // charge = 3;
        int r1_id = resultDao.save(r1, searchId_2, 2);
        int r2_id = resultDao.save(r2, searchId_2, 3);
        assertEquals(2, resultDao.loadResultIdsForSearch(searchId_2).size());
        
        assertTrue(r1.getProteinMatchList().size() > 0);
        assertEquals(r1.getProteinMatchList().size(), matchDao.loadResultProteins(r1_id).size());
        assertTrue(r1.getResultPeptide().getDynamicModifications().size() > 0);
        assertEquals(r1.getResultPeptide().getDynamicModifications().size(), 
                        modDao.loadDynamicModificationsForSearchResult(r1_id).size());
        
        
        assertTrue(r2.getProteinMatchList().size() > 0);
        assertEquals(r2.getProteinMatchList().size(), matchDao.loadResultProteins(r2_id).size());
        assertTrue(r2.getResultPeptide().getDynamicModifications().size() > 0);
        assertEquals(r2.getResultPeptide().getDynamicModifications().size(),
                        modDao.loadDynamicModificationsForSearchResult(r2_id).size());
        
        // delete the searches
        searchDao.deleteSearch(searchId_1);
        searchDao.deleteSearch(searchId_2);
        
        testSearchDeleted(runId_1, searchId_1, new int[0] );
        testSearchDeleted(runId_2, searchId_2, new int[]{r1_id, r2_id});
        
    }

    public void testReturnedSearchType() {
        MsRunSearch search = makePeptideSearch(SearchFileFormat.SQT_SEQ, false, false, false, false);
        assertEquals(SearchFileFormat.SQT_SEQ, search.getSearchFileFormat());
        int searchId_1 = searchDao.saveSearch(search, 21, 0); // runId = 21
        MsRunSearchDb searchDb = searchDao.loadSearch(searchId_1);
        assertTrue(searchDb instanceof SQTRunSearchDb);
        assertEquals(SearchFileFormat.SQT_SEQ, searchDb.getSearchFileFormat());
        
        search = makePeptideSearch(SearchFileFormat.PEPXML, false, false, false, false);
        assertEquals(SearchFileFormat.PEPXML, search.getSearchFileFormat());
        int searchId_2 = searchDao.saveSearch(search, 21, 0);
        searchDb = searchDao.loadSearch(searchId_2);
        assertFalse(searchDb instanceof SQTRunSearchDb);
        assertEquals(SearchFileFormat.PEPXML, searchDb.getSearchFileFormat());
        
        searchDao.deleteSearch(searchId_1);
        searchDao.deleteSearch(searchId_2);
        
        testSearchDeleted(21, searchId_1, new int[0] );
        testSearchDeleted(21, searchId_2, new int[0] );
        
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
    
    public static class MsSearchTest implements MsRunSearch {

        private List<MsSearchModification> dynamicModifications = new ArrayList<MsSearchModification>();
        private List<MsSearchModification> staticModifications = new ArrayList<MsSearchModification>();
        private List<MsSearchDatabase> searchDatabases = new ArrayList<MsSearchDatabase>();
        private List<MsEnzyme> enzymes = new ArrayList<MsEnzyme>();
        private BigDecimal fragmentMassTolerance;
        private String fragmentMassType;
        private SearchFileFormat searchFileFormat;
        private String searchEngineVersion;
        private String searchEngineName;
        private int searchDuration;
        private Date searchDate;
        private String precursorMassType;
        private BigDecimal precursorMassTolerance;

        public List<MsSearchModification> getDynamicModifications() {
            return dynamicModifications;
        }

        public BigDecimal getFragmentMassTolerance() {
            return fragmentMassTolerance;
        }

        public String getFragmentMassType() {
            return fragmentMassType;
        }

        public BigDecimal getPrecursorMassTolerance() {
            return precursorMassTolerance;
        }

        public String getPrecursorMassType() {
            return precursorMassType;
        }

        public List<MsSearchDatabase> getSearchDatabases() {
            return searchDatabases;
        }

        public Date getSearchDate() {
            return searchDate;
        }

        public int getSearchDuration() {
            return searchDuration;
        }

        public String getSearchEngineName() {
            return searchEngineName;
        }

        public String getSearchEngineVersion() {
            return searchEngineVersion;
        }

        public SearchFileFormat getSearchFileFormat() {
            return searchFileFormat;
        }

        public List<MsSearchModification> getStaticModifications() {
            return staticModifications;
        }

        public void setDynamicModifications(
                List<MsSearchModification> dynamicModifications) {
            this.dynamicModifications = dynamicModifications;
        }

        public void setStaticModifications(
                List<MsSearchModification> staticModifications) {
            this.staticModifications = staticModifications;
        }

        public void setSearchDatabases(List<MsSearchDatabase> searchDatabases) {
            this.searchDatabases = searchDatabases;
        }

        public void setFragmentMassTolerance(BigDecimal fragmentMassTolerance) {
            this.fragmentMassTolerance = fragmentMassTolerance;
        }

        public void setFragmentMassType(String fragmentMassType) {
            this.fragmentMassType = fragmentMassType;
        }

        public void setSearchFileFormat(SearchFileFormat searchFileFormat) {
            this.searchFileFormat = searchFileFormat;
        }

        public void setSearchEngineVersion(String searchEngineVersion) {
            this.searchEngineVersion = searchEngineVersion;
        }

        public void setSearchEngineName(String searchEngineName) {
            this.searchEngineName = searchEngineName;
        }

        public void setSearchDuration(int searchDuration) {
            this.searchDuration = searchDuration;
        }

        public void setSearchDate(Date searchDate) {
            this.searchDate = searchDate;
        }

        public void setPrecursorMassType(String precursorMassType) {
            this.precursorMassType = precursorMassType;
        }

        public void setPrecursorMassTolerance(BigDecimal precursorMassTolerance) {
            this.precursorMassTolerance = precursorMassTolerance;
        }

        @Override
        public List<MsEnzyme> getEnzymeList() {
            return enzymes;
        }
        
        public void setEnzymeList(List<MsEnzyme> enzymeList) {
            this.enzymes = enzymeList;
        }
        
    }
}
