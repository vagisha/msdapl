package org.yeastrc.ms.dao.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.dao.util.DynamicModLookupUtil;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResidueModificationDb;
import org.yeastrc.ms.domain.search.MsResultDynamicResidueMod;
import org.yeastrc.ms.domain.search.MsResultDynamicResidueModDb;

public class MsSearchModificationDAOImplTest extends BaseDAOTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOperationsForStaticModifications() {

        // create some static modification objects
        MsResidueModification mod1_1 = makeStaticMod('A', "123.4");
        MsResidueModification mod1_2 = makeStaticMod('B', "56.7");

        MsResidueModification mod2_1 = makeStaticMod('X', "987.6");
        MsResidueModification mod2_2 = makeStaticMod('Y', "54.3");

        // save them to the database
        modDao.saveStaticResidueMod(mod1_1, 1); // searchId = 1
        modDao.saveStaticResidueMod(mod1_2, 1);
        modDao.saveStaticResidueMod(mod2_1, 2); // searchId = 2
        modDao.saveStaticResidueMod(mod2_2, 2);

        // load them back
        List<MsResidueModificationDb> modList1 = modDao.loadStaticResidueModsForSearch(1);
        assertEquals(2, modList1.size());

        List<MsResidueModificationDb> modList2 = modDao.loadStaticResidueModsForSearch(2);
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
        modDao.deleteStaticResidueModsForSearch(1);
        modList1 = modDao.loadStaticResidueModsForSearch(1);
        assertEquals(0, modList1.size());

        modDao.deleteStaticResidueModsForSearch(2);
        modList2 = modDao.loadStaticResidueModsForSearch(2);
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

    private MsResidueModification[] createDynaMods(String[] mass, char[] residue, char[] symbol) {
        assertTrue(residue.length > 0);
        assertEquals(mass.length, residue.length);
        assertEquals(residue.length, symbol.length);

        MsResidueModification[] mods = new MsResidueModification[residue.length];
        for (int i = 0; i < residue.length; i++) {
            mods[i] = makeDynamicMod(residue[i], mass[i], symbol[i]);
        }
        return mods;
    }

    private void doDynamicModTest(char[] residue, String[] mass, char[] symbol) {

        MsResidueModification[] mods = createDynaMods(mass, residue, symbol);
        assertEquals(residue.length, mods.length);

        // save them
        int wid1 = 0;
        int wid2 = 0;
        int searchId = 0;
        for (int i = 0; i < mods.length; i++) {
            searchId = i % 2  == 0 ? 2 : 1; // even numbers get a search id of 2; odd numbers get 1
            modDao.saveDynamicResidueMod(mods[i], searchId);
            searchId = searchId == 1 ? wid1++ : wid2++;
        }

        // read them back and make sure inserted values were accurate
        List<MsResidueModificationDb> modList1 = modDao.loadDynamicResidueModsForSearch(1);
        assertEquals(2, modList1.size());

        List<MsResidueModificationDb> modList2 = modDao.loadDynamicResidueModsForSearch(2);
        assertEquals(2, modList2.size());

        // sort them
        Collections.sort(modList1, new MsSearchModComparator());
        Collections.sort(modList2, new MsSearchModComparator());


        // combine the two lists and sort by id
        List<MsResidueModificationDb> modList = new ArrayList<MsResidueModificationDb>(modList1.size() + modList2.size());
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
        modList1 = modDao.loadDynamicResidueModsForSearch(1);
        assertEquals(0, modList1.size());

        modDao.deleteDynamicModificationsForSearch(2);
        modList2 = modDao.loadDynamicResidueModsForSearch(2);
        assertEquals(0, modList2.size());
    }


    public void testOperationsForDynaModsForSearchResult() {

        // create some dynamic mods 
        String[] mass = new String[] {"123.4", "56.7","987.6","54.3"};
        char[] residue = new char[]{'A', 'B', 'X', 'Y'};
        char[] symbol = new char[] {'*', '#', '&', '@'};
        MsResidueModification[] mods = createDynaMods(mass, residue, symbol);
        assertEquals(residue.length, mods.length);


        // save them to the database
        int mod1_1Id = modDao.saveDynamicResidueMod(mods[0], 1); // searchId = 1
        int mod1_2Id = modDao.saveDynamicResidueMod(mods[1], 1);
        int mod2_1Id = modDao.saveDynamicResidueMod(mods[2], 2); // searchId = 2
        int mod2_2Id = modDao.saveDynamicResidueMod(mods[3], 2);

        // save some dynamic modifications for two search results
        MsResultDynamicResidueMod rmod1_1 = makeResultDynamicResidueMod(residue[0], mass[0], symbol[0], 10);
        MsResultDynamicResidueMod rmod1_2 = makeResultDynamicResidueMod(residue[1], mass[1], symbol[1], 20);
        MsResultDynamicResidueMod rmod2_1 = makeResultDynamicResidueMod(residue[2], mass[2], symbol[2], 1);
        MsResultDynamicResidueMod rmod2_2 = makeResultDynamicResidueMod(residue[3], mass[3], symbol[3], 2);
        
        modDao.saveDynamicResidueModForResult(rmod1_1, 3, getModId(1, mods[0])); // mod, resultId, modificationId
        modDao.saveDynamicResidueModForResult(rmod1_2, 3, getModId(1, mods[1]));
        modDao.saveDynamicResidueModForResult(rmod2_1, 4, getModId(2, mods[2]));
        modDao.saveDynamicResidueModForResult(rmod2_2, 4, getModId(2, mods[3]));

        // load dynamic modifications for the two search results
        List<MsResultDynamicResidueModDb> resultMods1 = modDao.loadDynamicResidueModsForResult(3);
        assertEquals(2, resultMods1.size());
        List<MsResultDynamicResidueModDb> resultMods2 = modDao.loadDynamicResidueModsForResult(4);
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
        assertEquals(0, modDao.loadDynamicResidueModsForSearch(1).size());
        assertEquals(0, modDao.loadDynamicResidueModsForResult(3).size());
        // the other one should still be there
        assertEquals(2, modDao.loadDynamicResidueModsForSearch(2).size());
        assertEquals(2, modDao.loadDynamicResidueModsForResult(4).size());

        modDao.deleteDynamicModificationsForSearch(2);
        assertEquals(0, modDao.loadDynamicResidueModsForSearch(2).size());
        assertEquals(0, modDao.loadDynamicResidueModsForResult(4).size());

    }

    private int getModId(int searchId, MsResidueModification mod) {
        DynamicModLookupUtil util = DynamicModLookupUtil.instance();
        return util.getDynamicResidueModificationId(searchId, mod.getModifiedResidue(), mod.getModificationMass());
    }

    private void compareResultMods(MsResidueModification searchMod,
            MsResultDynamicResidueModDb resultMod, int resultId,
            int modId) {
        assertEquals(resultId, resultMod.getResultId());
        assertEquals(modId, resultMod.getModificationId());
        assertEquals(searchMod.getModificationMass().doubleValue(), resultMod.getModificationMass().doubleValue());
        assertEquals(searchMod.getModifiedResidue(), resultMod.getModifiedResidue());
        assertEquals(searchMod.getModificationSymbol(), resultMod.getModificationSymbol());

    }

    private void compareStaticMods(MsResidueModification input, MsResidueModificationDb output, int searchId) {
        assertEquals(searchId, output.getSearchId());
        assertEquals(input.getModifiedResidue(), output.getModifiedResidue());
        assertEquals(input.getModificationMass().doubleValue(), output.getModificationMass().doubleValue());
        assertEquals('\u0000', output.getModificationSymbol());
    }

    private void compareDynamicMods(MsResidueModification input, MsResidueModificationDb output, int searchId) {
        assertEquals(searchId, output.getSearchId());
        assertEquals(input.getModifiedResidue(), output.getModifiedResidue());
        assertEquals(input.getModificationMass().doubleValue(), output.getModificationMass().doubleValue());
        assertEquals(input.getModificationSymbol(), output.getModificationSymbol());
    }

    private static final class MsSearchResultDynamicModComparator implements
    Comparator<MsResultDynamicResidueModDb> {
        public int compare(MsResultDynamicResidueModDb o1,
                MsResultDynamicResidueModDb o2) {
            return new Integer(o1.getModifiedPosition()).compareTo(new Integer(o2.getModifiedPosition()));
        }
    }

    private static final class MsSearchModComparator implements Comparator<MsResidueModificationDb> {
        public int compare(MsResidueModificationDb o1, MsResidueModificationDb o2) {
            return new Integer(o1.getId()).compareTo(o2.getId());
        }
    }
}
