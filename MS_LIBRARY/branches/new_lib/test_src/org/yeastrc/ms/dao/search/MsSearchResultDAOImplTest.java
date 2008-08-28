package org.yeastrc.ms.dao.search;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResultDynamicResidueMod;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultDb;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.ValidationStatus;

public class MsSearchResultDAOImplTest extends BaseDAOTestCase {

    
    private int searchId_1 = 25;
    private int searchId_2 = 98;
    
    protected void setUp() throws Exception {
        super.setUp();
        
        // modifications for searchId_1
        MsResidueModification mod1 = makeStaticMod('C', "50.0");
        modDao.saveStaticResidueMod(mod1, searchId_1);
        MsResidueModification mod2 = makeStaticMod('S', "80.0");
        modDao.saveStaticResidueMod(mod2, searchId_1);
        
        MsResidueModification dmod1 = makeDynamicMod('A', "10.0", '*');
        modDao.saveDynamicResidueMod(dmod1, searchId_1);
        MsResidueModification dmod2 = makeDynamicMod('B', "20.0", '#');
        modDao.saveDynamicResidueMod(dmod2, searchId_1);
        
        // modifications for searchId_2
        MsResidueModification mod3 = makeStaticMod('M', "16.0");
        modDao.saveStaticResidueMod(mod3, searchId_2);
        MsResidueModification mod4 = makeStaticMod('S', "80.0");
        modDao.saveStaticResidueMod(mod4, searchId_2);
        
        MsResidueModification dmod3 = makeDynamicMod('X', "100.0", '*');
        modDao.saveDynamicResidueMod(dmod3, searchId_2);
        MsResidueModification dmod4 = makeDynamicMod('Y', "90.0", '\u0000');
        modDao.saveDynamicResidueMod(dmod4, searchId_2);
        MsResidueModification dmod5 = makeDynamicMod('A', "10.0", '#');
        modDao.saveDynamicResidueMod(dmod5, searchId_2);
        
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
        // delete modifications for searchId_1
        modDao.deleteDynamicModificationsForSearch(searchId_1);
        modDao.deleteStaticResidueModsForSearch(searchId_1);
        
        // delete modifications for searchId_2
        modDao.deleteDynamicModificationsForSearch(searchId_2);
        modDao.deleteStaticResidueModsForSearch(searchId_2);
    }

    public void testOperationsOnMsSearchResult() {
        assertNull(resultDao.load(searchId_1));
        
        // insert a search result with NO extra information
        MsSearchResult result1 = makeSearchResult(searchId_1, 3, "PEPTIDE1", false, false);
        int resultId_1 = resultDao.save(result1, "dummy_db", searchId_1, 123);// scanId = 123
        
        // read it back
        MsSearchResultDb resultdb1 = resultDao.load(resultId_1);
        assertNotNull(resultdb1);
        assertEquals(0, resultdb1.getProteinMatchList().size());
        assertEquals(0, resultdb1.getResultPeptide().getResultDynamicResidueModifications().size());
        checkSearchResult(result1, resultdb1);
       
        
        // save another result this time save protein matches
        MsSearchResult result2 = makeSearchResult(searchId_1, 3, "PEPTIDE2", true, false);
        int resultId_2 = resultDao.save(result2, "dummy_db", searchId_1, 123); // scanId = 123
        
        // read it back
        MsSearchResultDb resultdb2 = resultDao.load(resultId_2);
        assertNotNull(resultdb2);
        assertEquals(2, resultdb2.getProteinMatchList().size());
        assertEquals(0, resultdb2.getResultPeptide().getResultDynamicResidueModifications().size());
        checkSearchResult(result2, resultdb2);
        
        
        // save another result this time save protein matches AND dynamic mods
        // this time use searchId_2
        MsSearchResult result3 = makeSearchResult(searchId_2, 3, "PEPTIDE3", true, true);
        int resultId_3 = resultDao.save(result3, "dummy_db", searchId_2, 321);
        
        // read it back
        MsSearchResultDb resultdb3 = resultDao.load(resultId_3);
        assertNotNull(resultdb3);
        assertEquals(2, resultdb3.getProteinMatchList().size());
        assertEquals(3, resultdb3.getResultPeptide().getResultDynamicResidueModifications().size());
        
        
        // delete ALL results for searchId_1
//        resultDao.deleteResultsForSearch(searchId_1);
        // make sure everything was deleted
        assertEquals(0, resultDao.loadResultIdsForRunSearch(searchId_1).size());
        assertNull(resultDao.load(resultId_1));
        assertNull(resultDao.load(resultId_2));
        assertEquals(0, modDao.loadDynamicResidueModsForResult(resultId_1).size());
        assertEquals(0, matchDao.loadResultProteins(resultId_1).size());
        assertEquals(0, modDao.loadDynamicResidueModsForResult(resultId_2).size());
        assertEquals(0, matchDao.loadResultProteins(resultId_2).size());
        
        // these are for searchId_2 so should still exist
        assertNotNull(resultDao.load(resultId_3)); 
        assertEquals(3, modDao.loadDynamicResidueModsForResult(resultId_3).size());
        assertEquals(2,  matchDao.loadResultProteins(resultId_3).size());
        
        // delete ALL results for searchId_2
//        resultDao.deleteResultsForSearch(searchId_2);
        // make sure everything was deleted
        assertEquals(0, resultDao.loadResultIdsForRunSearch(searchId_2).size());
        assertNull(resultDao.load(resultId_3));
        assertEquals(0, modDao.loadDynamicResidueModsForResult(resultId_3).size());
        assertEquals(0, matchDao.loadResultProteins(resultId_3).size());
    }
    
    public static class MsSearchResultTest implements MsSearchResult {

        private ValidationStatus validationStatus;
        private MsSearchResultPeptide resultPeptide;
        private List<MsSearchResultProtein> proteinMatchList = new ArrayList<MsSearchResultProtein>();
        private int numIonsPredicted;
        private int numIonsMatched;
        private int charge;
        private BigDecimal calculatedMass;
        private int scanNumber;

        public BigDecimal getCalculatedMass() {
            return calculatedMass;
        }

        public int getCharge() {
            return charge;
        }

        public int getNumIonsMatched() {
            return numIonsMatched;
        }

        public int getNumIonsPredicted() {
            return numIonsPredicted;
        }

        public List<MsSearchResultProtein> getProteinMatchList() {
            return proteinMatchList;
        }

        public MsSearchResultPeptide getResultPeptide() {
            return resultPeptide;
        }

        public ValidationStatus getValidationStatus() {
            return validationStatus;
        }

        public void setValidationStatus(ValidationStatus validationStatus) {
            this.validationStatus = validationStatus;
        }

        public void setResultPeptide(MsSearchResultPeptide resultPeptide) {
            this.resultPeptide = resultPeptide;
        }

        public void setProteinMatchList(List<MsSearchResultProtein> proteinMatchList) {
            this.proteinMatchList = proteinMatchList;
        }

        public void setNumIonsPredicted(int numIonsPredicted) {
            this.numIonsPredicted = numIonsPredicted;
        }

        public void setNumIonsMatched(int numIonsMatched) {
            this.numIonsMatched = numIonsMatched;
        }

        public void setCharge(int charge) {
            this.charge = charge;
        }

        public void setCalculatedMass(BigDecimal calculatedMass) {
            this.calculatedMass = calculatedMass;
        }

        @Override
        public int getScanNumber() {
            return scanNumber;
        }
        
        public void setScanNumber(int scanNum) {
            this.scanNumber = scanNum;
        }
    }
    
    public static class MsSearchResultPeptideTest implements MsSearchResultPeptide {

        private String peptideSequence;
        private char preResidue;
        private char postResidue;
        private List<MsResultDynamicResidueMod> dynamicResModifications = new ArrayList<MsResultDynamicResidueMod>();
        private List<MsTerminalModification> dynamicTermModifications = new ArrayList<MsTerminalModification>();
        
        public String getPeptideSequence() {
            return peptideSequence;
        }
        public char getPostResidue() {
            return postResidue;
        }
        public char getPreResidue() {
            return preResidue;
        }
        public int getSequenceLength() {
            return peptideSequence.length();
        }
        public void setPeptideSequence(String peptideSequence) {
            this.peptideSequence = peptideSequence;
        }
        public void setPreResidue(char preResidue) {
            this.preResidue = preResidue;
        }
        public void setPostResidue(char postResidue) {
            this.postResidue = postResidue;
        }
        public void setResultDynamicResidueMods(
                List<MsResultDynamicResidueMod> dynaResMods) {
            this.dynamicResModifications = dynaResMods;
        }
        public void setDynamicTerminalMods(
                List<MsTerminalModification> dynaTermMods) {
            this.dynamicTermModifications = dynaTermMods;
        }
        @Override
        public List<MsResultDynamicResidueMod> getResultDynamicResidueModifications() {
            return dynamicResModifications;
        }
        @Override
        public List<MsTerminalModification> getDynamicTerminalModifications() {
            return dynamicTermModifications;
        }
    }
}
