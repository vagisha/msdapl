package org.yeastrc.ms.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.yeastrc.ms.dto.MsPeptideSearchResult;
import org.yeastrc.ms.dto.MsProteinMatch;
import org.yeastrc.ms.dto.MsSearchDynamicMod;
import org.yeastrc.ms.dto.MsSearchMod;
import org.yeastrc.ms.dto.MsSearchResultDynamicMod;

public class MsPeptideSearchResultDAOImplTest extends TestCase {

    private MsPeptideSearchResultDAO resultDao = DAOFactory.instance().getMsPeptideSearchResultDAO();
    private MsSearchModDAO modDao = DAOFactory.instance().getMsSearchModDAO();
    private MsProteinMatchDAO matchDao = DAOFactory.instance().getMsProteinMatchDAO();
    
    private int searchId_1 = 1;
    private int searchId_2 = 2;
    
    private int[] dynamods_1 = new int[2];
    private int[] dynamods_2 = new int[3];
    
    
    protected void setUp() throws Exception {
        super.setUp();
        
        // modifications for searchId_1
        saveStaticMod(searchId_1, 'C', "50.0");
        saveStaticMod(searchId_1, 'S', "80.0");
        dynamods_1[0] = saveDynamicMod(searchId_1, 'A', "10.0", '*');
        dynamods_1[1] = saveDynamicMod(searchId_1, 'B', "20.0", '#');
        
        
        // modifications for searchId_2
        saveStaticMod(searchId_2, 'M', "16.0");
        saveStaticMod(searchId_2, 'S', "80.0");
        dynamods_2[0] = saveDynamicMod(searchId_2, 'X', "100.0", '*');
        dynamods_2[1] = saveDynamicMod(searchId_2, 'Y', "90.0", '\u0000');
        dynamods_2[1] = saveDynamicMod(searchId_2, 'A', "10.0", '#');
        
    }
    
    private void saveStaticMod(int searchId, char modChar, String modMass) {
        MsSearchMod mod = new MsSearchMod();
        mod.setSearchId(searchId);
        mod.setModifiedResidue(modChar);
        mod.setModificationMass(new BigDecimal(modMass));
        modDao.saveStaticModification(mod);
    }
    
    private int saveDynamicMod(int searchId, char modChar, String modMass, char modSymbol) {
        MsSearchDynamicMod mod = new MsSearchDynamicMod();
        mod.setSearchId(searchId);
        mod.setModifiedResidue(modChar);
        mod.setModificationMass(new BigDecimal(modMass));
        mod.setModificationSymbol(modSymbol);
        return modDao.saveDynamicModification(mod);
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

    
    private MsPeptideSearchResult makeSearchResult(int searchId, int scanId, int charge, String peptide,
            boolean addPrMatch, boolean addDynaMod) {
        MsPeptideSearchResult result = new MsPeptideSearchResult();
        result.setSearchId(searchId);
        result.setScanId(scanId);
        result.setCharge(charge);
        result.setPeptide(peptide);
        
        // add protein matches
        if (addPrMatch)     addProteinMatches(result);
        
        // add dynamic modifications
        if (addDynaMod)     addDynamicModifications(result, searchId);
        
        return result;
    }

    private void addDynamicModifications(MsPeptideSearchResult result, int searchId) {
        
        List<MsSearchDynamicMod> dynaMods = modDao.loadDynamicModificationsForSearch(searchId);
        
        List<MsSearchResultDynamicMod> resultDynaMods = new ArrayList<MsSearchResultDynamicMod>(dynaMods.size());
        int pos = 1;
        for (MsSearchDynamicMod mod: dynaMods) {
            MsSearchResultDynamicMod resMod = new MsSearchResultDynamicMod();
            resMod.setModificationId(mod.getId());
            resMod.setModificationMass(mod.getModificationMass());
            resMod.setModificationPosition(pos++);
            resMod.setModificationSymbol(mod.getModificationSymbol());
            resMod.setModifiedResidue(mod.getModifiedResidue());
            resultDynaMods.add(resMod);
        }
        
        result.setDynamicModifications(resultDynaMods);
    }

    private void addProteinMatches(MsPeptideSearchResult result) {
        MsProteinMatch match1 = new MsProteinMatch();
        match1.setAccession("Accession_"+result.getPeptide()+"_1");
        match1.setDescription("Description_"+result.getPeptide()+"_1");
        
        result.addProteinMatch(match1);
        
        MsProteinMatch match2 = new MsProteinMatch();
        match2.setAccession("Accession_"+result.getPeptide()+"_2");
        
        result.addProteinMatch(match2);
    }
}
