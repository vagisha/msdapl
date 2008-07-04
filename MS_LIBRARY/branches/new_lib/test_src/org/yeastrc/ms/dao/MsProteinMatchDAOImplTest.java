package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.dto.MsProteinMatch;

import junit.framework.TestCase;

public class MsProteinMatchDAOImplTest extends TestCase {

    private MsProteinMatchDAO matchDao = DAOFactory.instance().getMsProteinmatchDAO();
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testLoadResultProteins() {
        List<MsProteinMatch> resultProteins = matchDao.loadResultProteins(1);
        assertEquals(3, resultProteins.size());
    }

    public void testSaveMsProteinMatch() {
//        fail("Not yet implemented");
    }

    public void testDeleteInt() {
//        fail("Not yet implemented");
    }

}
