package org.yeastrc.ms.dao;


import org.yeastrc.ms.domain.MsPeptideSearchResult;

public class MsPeptideSearchResultDAOImplTest extends BaseDAOTestCase {

    
    private int searchId_1 = 1;
    private int searchId_2 = 2;
    
    protected void setUp() throws Exception {
        super.setUp();
        
        // modifications for searchId_1
//        IMsSearchModification mod1 = makeStaticMod(searchId_1, 'C', "50.0");
//        modDao.saveStaticModification(mod1);
//        IMsSearchModification mod2 = makeStaticMod(searchId_1, 'S', "80.0");
//        modDao.saveStaticModification(mod2);
//        
//        IMsSearchDynamicMod dmod1 = makeDynamicMod(searchId_1, 'A', "10.0", '*');
//        modDao.saveDynamicModification(dmod1);
//        IMsSearchDynamicMod dmod2 = makeDynamicMod(searchId_1, 'B', "20.0", '#');
//        modDao.saveDynamicModification(dmod2);
//        
//        // modifications for searchId_2
//        IMsSearchModification mod3 = makeStaticMod(searchId_2, 'M', "16.0");
//        modDao.saveStaticModification(mod3);
//        IMsSearchModification mod4 = makeStaticMod(searchId_2, 'S', "80.0");
//        modDao.saveStaticModification(mod4);
//        
//        IMsSearchDynamicMod dmod3 = makeDynamicMod(searchId_2, 'X', "100.0", '*');
//        modDao.saveDynamicModification(dmod3);
//        IMsSearchDynamicMod dmod4 = makeDynamicMod(searchId_2, 'Y', "90.0", '\u0000');
//        modDao.saveDynamicModification(dmod4);
//        IMsSearchDynamicMod dmod5 = makeDynamicMod(searchId_2, 'A', "10.0", '#');
//        modDao.saveDynamicModification(dmod5);
        
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
        // delete modifications for searchId_1
        modDao.deleteDynamicModificationsForSearch(searchId_1);
        modDao.deleteStaticModificationsForSearch(searchId_1);
        
        // delete modifications for searchId_2
        modDao.deleteDynamicModificationsForSearch(searchId_2);
        modDao.deleteStaticModificationsForSearch(searchId_2);
    }

    public void testOperationsOnMsSearchResult() {
        MsPeptideSearchResult result = resultDao.load(searchId_1);
        assertNull(result);
        
        // insert a search result with NO extra information
        result = makeSearchResult(searchId_1, 100, 3, "PEPTIDE1", false, false);
        int resultId_1 = resultDao.save(result);
        
        // read it back
        result = resultDao.load(resultId_1);
        assertNotNull(result);
        assertEquals(0, result.getProteinMatchList().size());
        assertEquals(0, result.getDynamicModifications().size());
        // NOTE: we are saving static modifications for the search in the setUp method
        // so they will get added to the search result when we call this method.
        assertEquals(2, result.getStaticModifications().size());
       
        
        // save another result this time save protein matches
        result = makeSearchResult(searchId_1, 100, 3, "PEPTIDE2", true, false);
        int resultId_2 = resultDao.save(result);
        
        // read it back
        result = resultDao.load(resultId_2);
        assertNotNull(result);
        assertEquals(2, result.getProteinMatchList().size());
        assertEquals(0, result.getDynamicModifications().size());
        // NOTE: we are saving static modifications for the search in the setUp method
        // so they will get added to the search result when we call this method.
        assertEquals(2, result.getStaticModifications().size());
        
        
        // save another result this time save protein matches AND dynamic mods
        // this time use searchId_2
        result = makeSearchResult(searchId_2, 100, 3, "PEPTIDE3", true, true);
        int resultId_3 = resultDao.save(result);
        
        // read it back
        result = resultDao.load(resultId_3);
        assertNotNull(result);
        assertEquals(2, result.getProteinMatchList().size());
        assertEquals(3, result.getDynamicModifications().size());
        // NOTE: we are saving static modifications for the search in the setUp method
        // so they will get added to the search result when we call this method.
        assertEquals(2, result.getStaticModifications().size());
        
        
        // delete ALL results for searchId_1
        resultDao.deleteResultsForSearch(searchId_1);
        // make sure everthing was deleted
        assertEquals(0, resultDao.loadResultIdsForSearch(searchId_1).size());
        assertNull(resultDao.load(resultId_1));
        assertNull(resultDao.load(resultId_2));
        assertEquals(0, modDao.loadDynamicModificationsForSearchResult(resultId_1).size());
        assertEquals(0, matchDao.loadResultProteins(resultId_1).size());
        assertEquals(0, modDao.loadDynamicModificationsForSearchResult(resultId_2).size());
        assertEquals(0, matchDao.loadResultProteins(resultId_2).size());
        
        // these are for searchId_2 so should still exist
        assertNotNull(resultDao.load(resultId_3)); 
        assertEquals(3, modDao.loadDynamicModificationsForSearchResult(resultId_3).size());
        assertEquals(2,  matchDao.loadResultProteins(resultId_3).size());
        
        // delete ALL results for searchId_2
        resultDao.deleteResultsForSearch(searchId_2);
        // make sure everything was deleted
        assertEquals(0, resultDao.loadResultIdsForSearch(searchId_2).size());
        assertNull(resultDao.load(resultId_3));
        assertEquals(0, modDao.loadDynamicModificationsForSearchResult(resultId_3).size());
        assertEquals(0, matchDao.loadResultProteins(resultId_3).size());
    }
}
