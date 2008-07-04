package org.yeastrc.ms.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import junit.framework.TestCase;

import org.yeastrc.ms.dto.MsSearchDynamicMod;
import org.yeastrc.ms.dto.MsSearchMod;
import org.yeastrc.ms.dto.MsSearchResultDynamicMod;

public class MsSearchModDAOImplTest extends TestCase {

    private MsSearchModDAO modDao = DAOFactory.instance().getMsSearchModDAO();
    private MsSearchDynamicMod mod1_1;
    private MsSearchDynamicMod mod1_2;
    private MsSearchDynamicMod mod2_1;
    private MsSearchDynamicMod mod2_2;
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOperationsForStaticModifications() {
        
        // create some static modification objects
        BigDecimal mass1_1 = new BigDecimal("123.4");
        MsSearchMod mod1_1 = getStaticMod(1, 'A', mass1_1);
        BigDecimal mass1_2 = new BigDecimal("56.7");
        MsSearchMod mod1_2 = getStaticMod(1, 'B', mass1_2);
        
        BigDecimal mass2_1 = new BigDecimal("987.6");
        MsSearchMod mod2_1 = getStaticMod(2, 'X', mass2_1);
        BigDecimal mass2_2 = new BigDecimal("54.3");
        MsSearchMod mod2_2 = getStaticMod(2, 'Y', mass2_2);
        
        // save them to the database
        modDao.saveStaticModification(mod1_1);
        modDao.saveStaticModification(mod1_2);
        modDao.saveStaticModification(mod2_1);
        modDao.saveStaticModification(mod2_2);
        
        // load them back
        List<MsSearchMod> modList1 = modDao.loadStaticModificationsForSearch(1);
        assertEquals(2, modList1.size());
        
        List<MsSearchMod> modList2 = modDao.loadStaticModificationsForSearch(2);
        assertEquals(2, modList2.size());
        
        // sort by id
        Collections.sort(modList1, new MsSearchModComparator());
        Collections.sort(modList2, new MsSearchModComparator());
        
        // Make sure all fields were saved and read back accurately
        MsSearchMod mod = modList1.get(0);
        assertEquals(1, mod.getSearchId());
        assertEquals('A', mod.getModifiedResidue());
        assertEquals(mass1_1.doubleValue(), mod.getModificationMass().doubleValue());
        
        mod = modList1.get(1);
        assertEquals(1, mod.getSearchId());
        assertEquals('B', mod.getModifiedResidue());
        assertEquals(mass1_2.doubleValue(), mod.getModificationMass().doubleValue());
        
        mod = modList2.get(0);
        assertEquals(2, mod.getSearchId());
        assertEquals('X', mod.getModifiedResidue());
        assertEquals(mass2_1.doubleValue(), mod.getModificationMass().doubleValue());
        
        mod = modList2.get(1);
        assertEquals(2, mod.getSearchId());
        assertEquals('Y', mod.getModifiedResidue());
        assertEquals(mass2_2.doubleValue(), mod.getModificationMass().doubleValue());
        
        // now delete the modifications
        modDao.deleteStaticModificationsForSearch(1);
        modList1 = modDao.loadStaticModificationsForSearch(1);
        assertEquals(0, modList1.size());
        
        modDao.deleteStaticModificationsForSearch(2);
        modList2 = modDao.loadStaticModificationsForSearch(2);
        assertEquals(0, modList2.size());
        
    }
    
    public void testOperationsForDynamicModifications() {
        
        // create some dynamic modification objects
        BigDecimal[] mass = new BigDecimal[] {new BigDecimal("123.4"), new BigDecimal("56.7"),
                new BigDecimal("987.6"), new BigDecimal("54.3")};
        char[] residue = new char[]{'A', 'B', 'X', 'Y'};
        char[] symbol = new char[] {'*', '#', '&', '@'};
        createDynaMods(mass, residue, symbol);
        
        doDynamicModTest(residue, mass, symbol);
    }
    
    public void testOperationsForDynamicModificationsWithEmptySymbol() {
        
        // create some dynamic modification objects
        BigDecimal[] mass = new BigDecimal[] {new BigDecimal("123.4"), new BigDecimal("56.7"),
                new BigDecimal("987.6"), new BigDecimal("54.3")};
        char[] residue = new char[]{'A', 'B', 'X', 'Y'};
        char[] symbol = new char[] {'\u0000', '\u0000', '*', '\u0000'};
        createDynaMods(mass, residue, symbol);
        
        doDynamicModTest(residue, mass, symbol);
    }

    private void createDynaMods(BigDecimal[] mass, char[] residue, char[] symbol) {
        mod1_1 = getDynamicMod(1, residue[0], mass[0], symbol[0]);
        mod1_2 = getDynamicMod(1, residue[1], mass[1], symbol[1]);
        mod2_1 = getDynamicMod(2, residue[2], mass[2], symbol[2]);
        mod2_2 = getDynamicMod(2, residue[3], mass[3], symbol[3]);
    }

    private void doDynamicModTest(char[] residue, BigDecimal[] mass, char[] symbol) {
        
        // save them to the database
        modDao.saveDynamicModification(mod1_1);
        modDao.saveDynamicModification(mod1_2);
        modDao.saveDynamicModification(mod2_1);
        modDao.saveDynamicModification(mod2_2);
        
        // load them back
        List<MsSearchDynamicMod> modList1 = modDao.loadDynamicModificationsForSearch(1);
        assertEquals(2, modList1.size());
        
        List<MsSearchDynamicMod> modList2 = modDao.loadDynamicModificationsForSearch(2);
        assertEquals(2, modList2.size());
        
        // combine the two lists and sort by id
        List<MsSearchDynamicMod> modList = new ArrayList<MsSearchDynamicMod>(modList1.size() + modList2.size());
        modList.addAll(modList1);
        modList.addAll(modList2);
        Collections.sort(modList, new MsSearchModComparator());
        
        // Make sure all fields were saved and read back accurately
        for (int i = 0; i < residue.length; i++) {
            int searchId = i < 2 ? 1 : 2;
            MsSearchDynamicMod mod = modList.get(i);
            assertEquals(searchId, mod.getSearchId());
            assertEquals(residue[i], mod.getModifiedResidue());
            assertEquals(mass[i].doubleValue(), mod.getModificationMass().doubleValue());
            assertEquals(symbol[i], mod.getModificationSymbol());
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
        // create some dynamic modification objects
        BigDecimal[] mass = new BigDecimal[] {new BigDecimal("123.4"), new BigDecimal("56.7"),
                new BigDecimal("987.6"), new BigDecimal("54.3")};
        char[] residue = new char[]{'A', 'B', 'X', 'Y'};
        char[] symbol = new char[] {'*', '#', '&', '@'};
        createDynaMods(mass, residue, symbol);
        
        // save them to the database
        int mod1_1Id = modDao.saveDynamicModification(mod1_1);
        int mod1_2Id = modDao.saveDynamicModification(mod1_2);
        int mod2_1Id = modDao.saveDynamicModification(mod2_1);
        int mod2_2Id = modDao.saveDynamicModification(mod2_2);
        
        // save some dynamic modifications for two search results
        modDao.saveDynamicModificationForSearchResult(1, mod1_1Id, 10);
        modDao.saveDynamicModificationForSearchResult(1, mod1_2Id, 20);
        modDao.saveDynamicModificationForSearchResult(2, mod2_1Id, 1);
        modDao.saveDynamicModificationForSearchResult(2, mod2_2Id, 2);
        
        // load dynamic modifications for the two search results
        List<MsSearchResultDynamicMod> resultMods1 = modDao.loadDynamicModificationsForSearchResult(1);
        assertEquals(2, resultMods1.size());
        List<MsSearchResultDynamicMod> resultMods2 = modDao.loadDynamicModificationsForSearchResult(2);
        assertEquals(2, resultMods2.size());
        
        
        // make sure the values saved and read back are accurate
        
        Collections.sort(resultMods1, new MsSearchResultDynamicModComparator());
        
        MsSearchResultDynamicMod resultMod = resultMods1.get(0);
        assertEquals(10, resultMod.getModificationPosition());
        assertEquals(mass[0].doubleValue(), resultMod.getModificationMass().doubleValue());
        assertEquals(residue[0], resultMod.getModifiedResidue());
        assertEquals(symbol[0], resultMod.getModificationSymbol());
        
        resultMod = resultMods1.get(1);
        assertEquals(20, resultMod.getModificationPosition());
        assertEquals(mass[1].doubleValue(), resultMod.getModificationMass().doubleValue());
        assertEquals(residue[1], resultMod.getModifiedResidue());
        assertEquals(symbol[1], resultMod.getModificationSymbol());
        
        
        Collections.sort(resultMods2, new MsSearchResultDynamicModComparator());
        
        resultMod = resultMods2.get(0);
        assertEquals(1, resultMod.getModificationPosition());
        assertEquals(mass[2].doubleValue(), resultMod.getModificationMass().doubleValue());
        assertEquals(residue[2], resultMod.getModifiedResidue());
        assertEquals(symbol[2], resultMod.getModificationSymbol());
        
        resultMod = resultMods2.get(1);
        assertEquals(2, resultMod.getModificationPosition());
        assertEquals(mass[3].doubleValue(), resultMod.getModificationMass().doubleValue());
        assertEquals(residue[3], resultMod.getModifiedResidue());
        assertEquals(symbol[3], resultMod.getModificationSymbol());
        
        
        // delete the search and result modification entries
        modDao.deleteDynamicModificationsForSearch(1);
        List<MsSearchDynamicMod> modList1 = modDao.loadDynamicModificationsForSearch(1);
        assertEquals(0, modList1.size());
        
        List<MsSearchDynamicMod> modList2 = modDao.loadDynamicModificationsForSearch(2);
        assertEquals(2, modList2.size());
        
        modDao.deleteDynamicModificationsForSearch(2);
        modList2 = modDao.loadDynamicModificationsForSearch(2);
        assertEquals(0, modList2.size());
        
    }
    
    private MsSearchMod getStaticMod(int searchId, char residue, BigDecimal mass) {
        MsSearchMod mod = new MsSearchMod();
        mod.setSearchId(searchId);
        mod.setModifiedResidue(residue);
        mod.setModificationMass(mass);
        return mod;
    }
    
    private MsSearchDynamicMod getDynamicMod(int searchId, char residue, BigDecimal mass, char symbol) {
        MsSearchDynamicMod mod = new MsSearchDynamicMod();
        mod.setSearchId(searchId);
        mod.setModifiedResidue(residue);
        mod.setModificationMass(mass);
        mod.setModificationSymbol(symbol);
        return mod;
    }
    
    private static final class MsSearchResultDynamicModComparator implements
            Comparator<MsSearchResultDynamicMod> {
        public int compare(MsSearchResultDynamicMod o1,
                MsSearchResultDynamicMod o2) {
            return new Integer(o1.getModificationPosition()).compareTo(new Integer(o2.getModificationPosition()));
        }
    }

    private static final class MsSearchModComparator implements Comparator<MsSearchMod> {
        public int compare(MsSearchMod o1, MsSearchMod o2) {
            return new Integer(o1.getId()).compareTo(o2.getId());
        }
}
}
