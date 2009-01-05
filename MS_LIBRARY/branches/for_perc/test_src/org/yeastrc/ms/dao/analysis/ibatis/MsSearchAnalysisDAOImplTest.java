package org.yeastrc.ms.dao.analysis.ibatis;

import java.util.Collections;
import java.util.List;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsSearchAnalysisDAO;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.analysis.impl.SearchAnalysisBean;
import org.yeastrc.ms.domain.search.Program;

public class MsSearchAnalysisDAOImplTest extends BaseDAOTestCase {

    private static final MsSearchAnalysisDAO analysisDao = DAOFactory.instance().getMsSearchAnalysisDAO();
    protected void setUp() throws Exception {
        super.setUp();
        BaseDAOTestCase.resetDatabase();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testSaveMsSearchAnalysis() {
        SearchAnalysisBean analysis = new SearchAnalysisBean();
        analysis.setAnalysisProgram(Program.PERCOLATOR);
        analysis.setSearchId(23);
        analysis.setAnalysisProgramVersion("1.07");
        int id = analysisDao.save(analysis);
        assertTrue(id > 0);
    }
    
    public final void testLoad() {
        
        SearchAnalysisBean analysis = new SearchAnalysisBean();
        analysis.setAnalysisProgram(Program.PERCOLATOR);
        analysis.setSearchId(23);
        analysis.setAnalysisProgramVersion("1.07");
        int id1 = analysisDao.save(analysis);
        assertEquals(1, id1);
        
        analysis = new SearchAnalysisBean();
        analysis.setAnalysisProgram(Program.BIBLIOSPEC);
        analysis.setSearchId(123);
        analysis.setAnalysisProgramVersion("1.03");
        int id2 = analysisDao.save(analysis);
        assertEquals(2, id2);
        
        MsSearchAnalysis fromDb = analysisDao.load(2);
        assertEquals(Program.BIBLIOSPEC, fromDb.getAnalysisProgram());
        assertEquals("1.03", fromDb.getAnalysisProgramVersion());
        assertEquals(id2, fromDb.getId());
        assertEquals(123, fromDb.getSearchId());
        
        fromDb = analysisDao.load(1);
        assertEquals(Program.PERCOLATOR, fromDb.getAnalysisProgram());
        assertEquals("1.07", fromDb.getAnalysisProgramVersion());
        assertEquals(id1, fromDb.getId());
        assertEquals(23, fromDb.getSearchId());
        
    }

    public final void testGetAnalysisIdsForSearch() {
        SearchAnalysisBean analysis = new SearchAnalysisBean();
        analysis.setAnalysisProgram(Program.PERCOLATOR);
        analysis.setSearchId(23);
        analysis.setAnalysisProgramVersion("1.07");
        int id1 = analysisDao.save(analysis);
        assertEquals(1, id1);
        
        analysis = new SearchAnalysisBean();
        analysis.setAnalysisProgram(Program.PERCOLATOR);
        analysis.setSearchId(23);
        analysis.setAnalysisProgramVersion("1.03");
        int id2 = analysisDao.save(analysis);
        assertEquals(2, id2);
        
        List<Integer> analysisIds = analysisDao.getAnalysisIdsForSearch(23);
        assertEquals(2, analysisIds.size());
        Collections.sort(analysisIds);
        assertEquals(Integer.valueOf(id1), analysisIds.get(0));
        assertEquals(Integer.valueOf(id2), analysisIds.get(1));
    }

    public final void testUpdateAnalysisProgram() {
        
        SearchAnalysisBean analysis = new SearchAnalysisBean();
        analysis.setAnalysisProgram(Program.PERCOLATOR);
        analysis.setSearchId(23);
        analysis.setAnalysisProgramVersion("1.07");
        int id1 = analysisDao.save(analysis);
        assertEquals(1, id1);
        
        analysis = new SearchAnalysisBean();
        analysis.setAnalysisProgram(Program.BIBLIOSPEC);
        analysis.setSearchId(123);
        analysis.setAnalysisProgramVersion("1.03");
        int id2 = analysisDao.save(analysis);
        assertEquals(2, id2);
        
        MsSearchAnalysis fromDb = analysisDao.load(2);
        assertEquals(Program.BIBLIOSPEC, fromDb.getAnalysisProgram());
        assertEquals("1.03", fromDb.getAnalysisProgramVersion());
        assertEquals(id2, fromDb.getId());
        assertEquals(123, fromDb.getSearchId());
        
        analysisDao.updateAnalysisProgram(id2, Program.PERCOLATOR);
        fromDb = analysisDao.load(2);
        assertEquals(Program.PERCOLATOR, fromDb.getAnalysisProgram());
        assertEquals("1.03", fromDb.getAnalysisProgramVersion());
        assertEquals(id2, fromDb.getId());
        assertEquals(123, fromDb.getSearchId());
    }

    public final void testUpdateAnalysisProgramVersion() {
        SearchAnalysisBean analysis = new SearchAnalysisBean();
        analysis.setAnalysisProgram(Program.PERCOLATOR);
        analysis.setSearchId(23);
        analysis.setAnalysisProgramVersion("1.07");
        int id1 = analysisDao.save(analysis);
        assertEquals(1, id1);
        
        analysis = new SearchAnalysisBean();
        analysis.setAnalysisProgram(Program.BIBLIOSPEC);
        analysis.setSearchId(123);
        analysis.setAnalysisProgramVersion("1.03");
        int id2 = analysisDao.save(analysis);
        assertEquals(2, id2);
        
        MsSearchAnalysis fromDb = analysisDao.load(2);
        assertEquals(Program.BIBLIOSPEC, fromDb.getAnalysisProgram());
        assertEquals("1.03", fromDb.getAnalysisProgramVersion());
        assertEquals(id2, fromDb.getId());
        assertEquals(123, fromDb.getSearchId());
        
        analysisDao.updateAnalysisProgramVersion(id2, "1.05");
        fromDb = analysisDao.load(2);
        assertEquals(Program.BIBLIOSPEC, fromDb.getAnalysisProgram());
        assertEquals("1.05", fromDb.getAnalysisProgramVersion());
        assertEquals(id2, fromDb.getId());
        assertEquals(123, fromDb.getSearchId());
    }

    public final void testDeleteInt() {
        
        SearchAnalysisBean analysis = new SearchAnalysisBean();
        analysis.setAnalysisProgram(Program.PERCOLATOR);
        analysis.setSearchId(23);
        analysis.setAnalysisProgramVersion("1.07");
        int id1 = analysisDao.save(analysis);
        assertEquals(1, id1);
        
        analysis = new SearchAnalysisBean();
        analysis.setAnalysisProgram(Program.BIBLIOSPEC);
        analysis.setSearchId(123);
        analysis.setAnalysisProgramVersion("1.03");
        int id2 = analysisDao.save(analysis);
        assertEquals(2, id2);
        
        analysisDao.delete(id2);
        assertNull(analysisDao.load(id2));
        assertNotNull(analysisDao.load(id1));
        analysisDao.delete(id1);
        assertNull(analysisDao.load(id1));
        assertNull(analysisDao.load(id2));
        
    }

}
