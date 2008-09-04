package org.yeastrc.ms.dao.search;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.SearchProgram;

public class MsSearchDAOImplTest extends BaseDAOTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOperationsOnMsSearch() {
        
        // create and save a search with no seq. db information or modifications or enzymes
        MsSearch search_1 = makeSearch(false, false, false, false);
        assertEquals(0, search_1.getSearchDatabases().size());
        assertEquals(0, search_1.getStaticResidueMods().size());
        assertEquals(0, search_1.getDynamicResidueMods().size());
        assertEquals(0, search_1.getEnzymeList().size());
        
        int searchId_1 = searchDao.saveSearch(search_1);
        assertEquals(0, resultDao.loadResultIdsForRunSearch(searchId_1).size());
        checkSearch(search_1, searchDao.loadSearch(searchId_1));
        
        
        // create and save a search with seq. db information and modifications AND enzymes
        MsSearch search_2 = makeSearch(true, true, true, true);
        assertTrue(search_2.getSearchDatabases().size() > 0);
        assertTrue(search_2.getStaticResidueMods().size() > 0);
        assertTrue(search_2.getDynamicResidueMods().size() > 0);
        assertTrue(search_2.getEnzymeList().size() > 0);
        
        int searchId_2 = searchDao.saveSearch(search_2);
        assertEquals(2, seqDbDao.loadSearchDatabases(searchId_2).size());
        assertEquals(2, modDao.loadStaticResidueModsForSearch(searchId_2).size());
        assertEquals(3, modDao.loadDynamicResidueModsForSearch(searchId_2).size());
        assertEquals(2, enzymeDao.loadEnzymesForSearch(searchId_2).size());
        checkSearch(search_2, searchDao.loadSearch(searchId_2));
        
        
        // delete the searches
        searchDao.deleteSearch(searchId_1);
        searchDao.deleteSearch(searchId_2);
        
        testSearchDeleted(searchId_1);
        testSearchDeleted(searchId_2);
        
    }

    private void testSearchDeleted(int searchId) {
        assertNull(searchDao.loadSearch(searchId));
        assertEquals(0, seqDbDao.loadSearchDatabases(searchId).size());
        assertEquals(0, modDao.loadStaticResidueModsForSearch(searchId).size());
        assertEquals(0, modDao.loadDynamicResidueModsForSearch(searchId).size());
        assertEquals(0, modDao.loadStaticTerminalModsForSearch(searchId).size());
        assertEquals(0, modDao.loadDynamicTerminalModsForSearch(searchId).size());
        assertEquals(0, runSearchDao.loadRunSearchIdsForSearch(searchId).size());
    }
    
    public static class MsSearchTest implements MsSearch {

        private List<MsResidueModification> dynamicResidueModifications = new ArrayList<MsResidueModification>();
        private List<MsResidueModification> staticResidueModifications = new ArrayList<MsResidueModification>();
        private List<MsTerminalModification> dynamicTerminalModifications = new ArrayList<MsTerminalModification>();
        private List<MsTerminalModification> staticTerminalModifications = new ArrayList<MsTerminalModification>();
        
        private List<MsSearchDatabase> searchDatabases = new ArrayList<MsSearchDatabase>();
        private List<MsEnzyme> enzymes = new ArrayList<MsEnzyme>();
        private String searchEngineVersion;
        private SearchProgram searchProgram;
        private Date searchDate;


        public List<MsSearchDatabase> getSearchDatabases() {
            return searchDatabases;
        }

        public void setSearchDatabases(List<MsSearchDatabase> searchDatabases) {
            this.searchDatabases = searchDatabases;
        }
        
        public Date getSearchDate() {
            return searchDate;
        }

        public void setSearchDate(Date searchDate) {
            this.searchDate = searchDate;
        }
        
        public List<MsResidueModification> getStaticResidueMods() {
            return staticResidueModifications;
        }

        public void setStaticResidueMods(
                List<MsResidueModification> staticModifications) {
            this.staticResidueModifications = staticModifications;
        }
        
        public List<MsResidueModification> getDynamicResidueMods() {
            return dynamicResidueModifications;
        }

        public void setDynamicResidueMods(
                List<MsResidueModification> dynaResMods) {
            this.dynamicResidueModifications = dynaResMods;
        }

        public List<MsEnzyme> getEnzymeList() {
            return enzymes;
        }
        
        public void setEnzymeList(List<MsEnzyme> enzymeList) {
            this.enzymes = enzymeList;
        }

        @Override
        public List<MsTerminalModification> getDynamicTerminalMods() {
            return dynamicTerminalModifications;
        }

        public void setDynamicTerminalMods(List<MsTerminalModification> mods) {
            this.dynamicTerminalModifications = mods;
        }
        
        @Override
        public List<MsTerminalModification> getStaticTerminalMods() {
            return staticTerminalModifications;
        }

        public void setStaticTerminalMods(List<MsTerminalModification> mods) {
            this.staticTerminalModifications = mods;
        }
        
        public void setSearchProgram(SearchProgram searchProgram) {
            this.searchProgram = searchProgram;
        }
        
        @Override
        public SearchProgram getSearchProgram() {
            return searchProgram;
        }

        public void setAnalysisProgramVersion(String searchEngineVersion) {
            this.searchEngineVersion = searchEngineVersion;
        }
        
        @Override
        public String getSearchProgramVersion() {
            return searchEngineVersion;
        }

        @Override
        public String getServerAddress() {
            return "remote.server";
        }

        @Override
        public String getServerDirectory() {
            return "remote/directory";
        }
    }
}
