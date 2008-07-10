package org.yeastrc.ms.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.dto.IMsSearchModification;
import org.yeastrc.ms.dto.MsPeptideSearchDynamicMod;
import org.yeastrc.ms.dto.MsPeptideSearchStaticMod;
import org.yeastrc.ms.dto.MsSearchResultDynamicMod;

public class MsPeptideSearchModDAOImplTest extends BaseDAOTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOperationsForStaticModifications() {
        
        // create some static modification objects
        IMsSearchModification mod1_1 = getStaticMod('A', "123.4");
        IMsSearchModification mod1_2 = getStaticMod('B', "56.7");
        
        IMsSearchModification mod2_1 = getStaticMod('X', "987.6");
        IMsSearchModification mod2_2 = getStaticMod('Y', "54.3");
        
        // save them to the database
        modDao.saveStaticModification(mod1_1, 1);
        modDao.saveStaticModification(mod1_2, 1);
        modDao.saveStaticModification(mod2_1, 2);
        modDao.saveStaticModification(mod2_2, 2);
        
        // load them back
        List<MsPeptideSearchStaticMod> modList1 = modDao.loadStaticModificationsForSearch(1);
        assertEquals(2, modList1.size());
        
        List<MsPeptideSearchStaticMod> modList2 = modDao.loadStaticModificationsForSearch(2);
        assertEquals(2, modList2.size());
        
        // sort by id
        Collections.sort(modList1, new MsSearchModComparator());
        Collections.sort(modList2, new MsSearchModComparator());
        
        // Make sure all fields were saved and read back accurately
        compareStaticMods(mod1_1, modList1.get(0), 1);
        compareStaticMods(mod1_2, modList1.get(1), 1);
        compareStaticMods(mod2_1, modList2.get(0), 2);
        compareStaticMods(mod2_2, modList2.get(1), 2);
        
        
        // now delete the modifications
        modDao.deleteStaticModificationsForSearch(1);
        modList1 = modDao.loadStaticModificationsForSearch(1);
        assertEquals(0, modList1.size());
        
        modDao.deleteStaticModificationsForSearch(2);
        modList2 = modDao.loadStaticModificationsForSearch(2);
        assertEquals(0, modList2.size());
        
    }
    
    
    public void testOperationsForDynamicModifications() {
        
        // create some dynamic modification objects with the following values
        String[] mass = new String[] {"123.4", "56.7","987.6","54.3"};
        char[] residue = new char[]{'A', 'B', 'X', 'Y'};
        char[] symbol = new char[] {'*', '#', '&', '@'};
        
        doDynamicModTest(residue, mass, symbol);
    }
    
    public void testOperationsForDynamicModificationsWithEmptySymbol() {
        
        // create some dynamic modification objects with the following values
        String[] mass = new String[] {"123.4", "56.7","987.6","54.3"};
        char[] residue = new char[]{'A', 'B', 'X', 'Y'};
        char[] symbol = new char[] {'\u0000', '\u0000', '*', '\u0000'};
        createDynaMods(mass, residue, symbol);
        
        doDynamicModTest(residue, mass, symbol);
    }

    private IMsSearchModification[] createDynaMods(String[] mass, char[] residue, char[] symbol) {
        assertTrue(residue.length > 0);
        assertEquals(mass.length, residue.length);
        assertEquals(residue.length, symbol.length);
        
        IMsSearchModification[] mods = new IMsSearchModification[residue.length];
        for (int i = 0; i < residue.length; i++) {
            mods[i] = getDynamicMod(residue[i], mass[i], symbol[i]);
        }
        return mods;
    }

    private void doDynamicModTest(char[] residue, String[] mass, char[] symbol) {
        
        IMsSearchModification[] mods = createDynaMods(mass, residue, symbol);
        assertEquals(residue.length, mods.length);
        
        // save them
        int wid1 = 0;
        int wid2 = 0;
        int searchId = 0;
        for (int i = 0; i < mods.length; i++) {
            searchId = i % 2  == 0 ? 2 : 1; // even numbers get a search id of 2; odd numbers get 1
            modDao.saveDynamicModification(mods[i], searchId);
            searchId = searchId == 1 ? wid1++ : wid2++;
        }
            
        // read them back and make sure inserted values were accurate
        List<MsPeptideSearchDynamicMod> modList1 = modDao.loadDynamicModificationsForSearch(1);
        assertEquals(2, modList1.size());
        
        List<MsPeptideSearchDynamicMod> modList2 = modDao.loadDynamicModificationsForSearch(2);
        assertEquals(2, modList2.size());
        
        // sort them
        Collections.sort(modList1, new MsSearchModComparator());
        Collections.sort(modList2, new MsSearchModComparator());
        
        
        // combine the two lists and sort by id
        List<MsPeptideSearchDynamicMod> modList = new ArrayList<MsPeptideSearchDynamicMod>(modList1.size() + modList2.size());
        modList.addAll(modList1);
        modList.addAll(modList2);
        Collections.sort(modList, new MsSearchModComparator());
        
        // Make sure all fields were saved and read back accurately
        for (int i = 0; i < mods.length; i++) {
            searchId = i % 2  == 0 ? 2 : 1; // even numbers get a search id of 2; odd numbers get 1
            compareDynamicMods(mods[i], modList.get(i), searchId);
        }
        
        // now delete the modifications
        modDao.deleteDynamicModificationsForSearch(1);
        modList1 = modDao.loadDynamicModificationsForSearch(1);
        assertEquals(0, modList1.size());

        modDao.deleteDynamicModificationsForSearch(2);
        modList2 = modDao.loadDynamicModificationsForSearch(2);
        assertEquals(0, modList2.size());
    }
    
    
    public void testOperationsForDynaModsForSearchResult() {
        
        // create some dynamic mods 
        String[] mass = new String[] {"123.4", "56.7","987.6","54.3"};
        char[] residue = new char[]{'A', 'B', 'X', 'Y'};
        char[] symbol = new char[] {'*', '#', '&', '@'};
        IMsSearchModification[] mods = createDynaMods(mass, residue, symbol);
        assertEquals(residue.length, mods.length);
            
        
        // save them to the database
        int mod1_1Id = modDao.saveDynamicModification(mods[0], 1);
        int mod1_2Id = modDao.saveDynamicModification(mods[1], 1);
        int mod2_1Id = modDao.saveDynamicModification(mods[2], 2);
        int mod2_2Id = modDao.saveDynamicModification(mods[3], 2);
        
        // save some dynamic modifications for two search results
        modDao.saveDynamicModificationForSearchResult(3, mod1_1Id, 10); // resultId, modId, position
        modDao.saveDynamicModificationForSearchResult(3, mod1_2Id, 20);
        modDao.saveDynamicModificationForSearchResult(4, mod2_1Id, 1);
        modDao.saveDynamicModificationForSearchResult(4, mod2_2Id, 2);
        
        // load dynamic modifications for the two search results
        List<MsSearchResultDynamicMod> resultMods1 = modDao.loadDynamicModificationsForSearchResult(3);
        assertEquals(2, resultMods1.size());
        List<MsSearchResultDynamicMod> resultMods2 = modDao.loadDynamicModificationsForSearchResult(4);
        assertEquals(2, resultMods2.size());
        
        
        // make sure the values saved and read back are accurate
        // NOTE: sort by position; msDynamicModResult table does not have a id field so we sort by position.
        // make sure to save dynamic modifications for search result in increasing order of position.
        Collections.sort(resultMods1, new MsSearchResultDynamicModComparator());
        
        compareResultMods(mods[0], resultMods1.get(0), 3, mod1_1Id);
        compareResultMods(mods[1], resultMods1.get(1), 3, mod1_2Id);
        
        
        Collections.sort(resultMods2, new MsSearchResultDynamicModComparator());
        compareResultMods(mods[2], resultMods2.get(0), 4, mod2_1Id);
        compareResultMods(mods[3], resultMods2.get(1), 4, mod2_2Id);
        
        
        // delete the search and result modification entries
        modDao.deleteDynamicModificationsForSearch(1);
        assertEquals(0, modDao.loadDynamicModificationsForSearch(1).size());
        assertEquals(0, modDao.loadDynamicModificationsForSearchResult(3).size());
        // the other one should still be there
        assertEquals(2, modDao.loadDynamicModificationsForSearch(2).size());
        assertEquals(2, modDao.loadDynamicModificationsForSearchResult(4).size());
        
        modDao.deleteDynamicModificationsForSearch(2);
        assertEquals(0, modDao.loadDynamicModificationsForSearch(2).size());
        assertEquals(0, modDao.loadDynamicModificationsForSearchResult(4).size());
        
    }
    
    private void compareResultMods(IMsSearchModification searchMod,
            MsSearchResultDynamicMod resultMod, int resultId,
            int modId) {
        assertEquals(resultId, resultMod.getResultId());
        assertEquals(modId, resultMod.getModificationId());
        assertEquals(searchMod.getModificationType(), resultMod.getModificationType());
        assertEquals(searchMod.getModificationMass().doubleValue(), resultMod.getModificationMass().doubleValue());
        assertEquals(searchMod.getModifiedResidue(), resultMod.getModifiedResidue());
        assertEquals(searchMod.getModificationSymbol(), resultMod.getModificationSymbol());
        
    }

    private IMsSearchModification getStaticMod(char residue, String mass) {
        MsPeptideSearchStaticMod mod = new MsPeptideSearchStaticMod();
        mod.setModifiedResidue(residue);
        mod.setModificationMass(new BigDecimal(mass));
        return mod;
    }
    
    private IMsSearchModification getDynamicMod(char residue, String mass, char symbol) {
        MsPeptideSearchDynamicMod mod = new MsPeptideSearchDynamicMod();
        mod.setModifiedResidue(residue);
        mod.setModificationMass(new BigDecimal(mass));
        mod.setModificationSymbol(symbol);
        return mod;
    }
    
    private void compareStaticMods(IMsSearchModification original, MsPeptideSearchStaticMod fromDb, int searchId) {
        assertEquals(searchId, fromDb.getSearchId());
        assertEquals(original.getModifiedResidue(), fromDb.getModifiedResidue());
        assertEquals(original.getModificationMass().doubleValue(), fromDb.getModificationMass().doubleValue());
        assertEquals(IMsSearchModification.nullCharacter, fromDb.getModificationSymbol());
    }
    
    private void compareDynamicMods(IMsSearchModification original, MsPeptideSearchDynamicMod fromDb, int searchId) {
        assertEquals(searchId, fromDb.getSearchId());
        assertEquals(original.getModifiedResidue(), fromDb.getModifiedResidue());
        assertEquals(original.getModificationMass().doubleValue(), fromDb.getModificationMass().doubleValue());
        assertEquals(original.getModificationSymbol(), fromDb.getModificationSymbol());
    }
    
    private static final class MsSearchResultDynamicModComparator implements
            Comparator<MsSearchResultDynamicMod> {
        public int compare(MsSearchResultDynamicMod o1,
                MsSearchResultDynamicMod o2) {
            return new Integer(o1.getModifiedPosition()).compareTo(new Integer(o2.getModifiedPosition()));
        }
    }

    private static final class MsSearchModComparator implements Comparator<MsPeptideSearchStaticMod> {
        public int compare(MsPeptideSearchStaticMod o1, MsPeptideSearchStaticMod o2) {
            return new Integer(o1.getId()).compareTo(o2.getId());
        }
}
}
