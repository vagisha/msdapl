package edu.uwpr.protinfer.database.dao.ibatis;

import java.util.List;

import junit.framework.TestCase;
import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;

public class ProteinferSpectrumMatchDAOTest extends TestCase {

    private final ProteinferDAOFactory factory = ProteinferDAOFactory.testInstance();
    private final ProteinferSpectrumMatchDAO psmDao = factory.getProteinferSpectrumMatchDao();
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public final void testSaveSpectrumMatch() {
        int id = psmDao.saveSpectrumMatch(createProteinferSpectrumMatch(21, 124, 34));
        assertEquals(1, id);
        
        id = psmDao.saveSpectrumMatch(createProteinferSpectrumMatch(21, 124, 34));
        assertEquals(2, id);
        
        id = psmDao.saveSpectrumMatch(createProteinferSpectrumMatch(21, 124, 34));
        assertEquals(3, id);
    }
    
    public final void testGetSpectrumMatch() {
        ProteinferSpectrumMatch psm = psmDao.getSpectrumMatch(1); // we inserted this in the test above
        assertEquals(1, psm.getId());
        assertEquals(21, psm.getMsRunSearchResultId());
        assertEquals(123, psm.getProteinferPeptideId());
        //assertEquals(34, psm.getRank());
    }
    
    public final void testGetSpectrumMatchesForPeptide() {
        List<ProteinferSpectrumMatch> psmList = psmDao.getSpectrumMatchesForPeptide(123);
        assertEquals(3, psmList.size());
        for(ProteinferSpectrumMatch psm: psmList) {
            assertEquals(21, psm.getMsRunSearchResultId());
            assertEquals(123, psm.getProteinferPeptideId());
            //assertEquals(34, psm.getRank());
        }
    }
    
    public static final ProteinferSpectrumMatch createProteinferSpectrumMatch(int runSearchResultId, int pinferPeptideId, int rank) {
        ProteinferSpectrumMatch psm = new ProteinferSpectrumMatch();
        psm.setMsRunSearchResultId(runSearchResultId);
        psm.setProteinferPeptideId(pinferPeptideId);
        //psm.setRank(rank);
        return psm;
    }
}
