package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.dto.MsPeptideSearchResult;
import org.yeastrc.ms.dto.MsProteinMatch;

import junit.framework.TestCase;

public class MsPeptideSearchResultDAOImplTest extends TestCase {

    private MsPeptideSearchResultDAO resultDao = DAOFactory.instance().getMsPeptideSearchResultDAO();
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testLoad() {
        MsPeptideSearchResult result = resultDao.load(1);
        List<MsProteinMatch> resultProteins = result.getProteinMatchList();
        assertEquals(3, resultProteins.size());
    }

    public void testSaveMsPeptideSearchResult() {
//        fail("Not yet implemented");
    }

    public void testDeleteInt() {
//        fail("Not yet implemented");
    }

}
