package edu.uwpr.protinfer.database.dao.ibatis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import junit.framework.TestCase;
import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dto.ProteinferPeptide;
import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;

public class ProteinferPeptideDAOTest extends TestCase {

    private static final ProteinferDAOFactory factory = ProteinferDAOFactory.testInstance();
    private static final ProteinferPeptideDAO peptDao = factory.getProteinferPeptideDao();
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testSaveProteinferPeptide() {
        int id = peptDao.saveProteinferPeptide(createProteinferPeptide(456, 3));
        assertEquals(1, id);
        
        id = peptDao.saveProteinferPeptide(createProteinferPeptide(456, 4));
        assertEquals(2, id);
        
        id = peptDao.saveProteinferPeptide(createProteinferPeptide(456, 2));
        assertEquals(3, id);
    }

    public final void testGetPeptide() {
        ProteinferPeptide peptide = peptDao.getPeptide(2);
        assertEquals(456, peptide.getProteinferId());
        assertEquals(2, peptide.getId());
        assertEquals(4, peptide.getSpectralCount());
        assertEquals(4, peptide.getSpectrumMatchList().size());
        
        ProteinferSpectrumMatch bestPsm = peptide.getBestSpectrumMatch();
        assertEquals(1, bestPsm.getRank());
        
        List<ProteinferSpectrumMatch> psmList = peptide.getSpectrumMatchList();
        assertTrue(spectrumMatchesCorrect(2, psmList));
    }
    
    public final void testGetPeptideIdsForProteinferRun() {
        List<Integer> peptList = peptDao.getPeptideIdsForProteinferRun(456);
        assertEquals(3, peptList.size());
    }

    public final void testGetPeptidesForProteinferRun() {
        List<ProteinferPeptide> peptList = peptDao.getPeptidesForProteinferRun(456);
        assertEquals(3, peptList.size());
        Collections.sort(peptList, new Comparator<ProteinferPeptide> (){
            public int compare(ProteinferPeptide o1, ProteinferPeptide o2) {
                return Integer.valueOf(o1.getId()).compareTo(o2.getId());
            }});
        assertEquals(3, peptList.get(0).getSpectralCount());
        spectrumMatchesCorrect(1, peptList.get(0).getSpectrumMatchList());
        assertEquals(4, peptList.get(1).getSpectralCount());
        spectrumMatchesCorrect(2, peptList.get(1).getSpectrumMatchList());
        assertEquals(2, peptList.get(2).getSpectralCount());
        spectrumMatchesCorrect(3, peptList.get(2).getSpectrumMatchList());
        
    }
    
    public final void testGetPeptideIdsForProteinferProtein() {
        fail("Not yet implemented"); // TODO
    }

    public final void testGetPeptidesForProtein() {
        fail("Not yet implemented"); // TODO
    }
    
    private static boolean spectrumMatchesCorrect(int pinferProteinId, List<ProteinferSpectrumMatch> psmList) {
        Collections.sort(psmList, new Comparator<ProteinferSpectrumMatch>() {
            public int compare(ProteinferSpectrumMatch o1,
                    ProteinferSpectrumMatch o2) {
                return Integer.valueOf(o1.getRank()).compareTo(o2.getRank());
            }});
        
        int i = 1;
        for(ProteinferSpectrumMatch psm: psmList) {
            int runSearchResultId = 22 + i;
            int rank = 0 + i;
            assertEquals(runSearchResultId, psm.getMsRunSearchResultId());
            assertEquals(rank, psm.getRank());
            assertEquals(pinferProteinId, psm.getProteinferPeptideId());
            i++;
        }
        return true;
    }

    public static final ProteinferPeptide createProteinferPeptide(int pinferId, int numPsm) {
        ProteinferPeptide peptide = new ProteinferPeptide();
        peptide.setProteinferId(pinferId);
        List<ProteinferSpectrumMatch> psmList = new ArrayList<ProteinferSpectrumMatch>();
        for (int i = 1; i <= numPsm; i++) {
            int runSearchResultId = 22 + i;
            int rank = 0 + i;
            ProteinferSpectrumMatch psm1 = ProteinferSpectrumMatchDAOTest.createProteinferSpectrumMatch(runSearchResultId,0,rank);
            psmList.add(psm1);
        }
        
        peptide.setSpectrumMatchList(psmList);
        
        return peptide;
    }
}
