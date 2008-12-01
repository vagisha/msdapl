package edu.uwpr.protinfer.database.dao.ibatis;

import java.sql.Date;

import junit.framework.TestCase;
import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dto.ProteinInferenceProgram;
import edu.uwpr.protinfer.database.dto.ProteinferRun;
import edu.uwpr.protinfer.database.dto.ProteinferStatus;

public class ProteinferRunDAOTest extends TestCase {

    private static final ProteinferDAOFactory factory = ProteinferDAOFactory.testInstance();
    private static final ProteinferRunDAO runDao = factory.getProteinferRunDao();
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testSaveNewProteinferRun() {
        int id = runDao.saveNewProteinferRun(ProteinInferenceProgram.IDPICKER);
        assertEquals(1, id);
        
        id = runDao.saveNewProteinferRun(ProteinInferenceProgram.NONE);
        assertEquals(2, id);
    }

    public final void testSaveBaseProteinferRunOfQ() {
        ProteinferRun run = new ProteinferRun();
        run.setComments("some comments");
        run.setStatus(ProteinferStatus.PENDING);
        run.setProgram(ProteinInferenceProgram.IDPICKER);
        
        int id = runDao.save(run);
        assertEquals(3, id);
    }

    public final void testGetProteinferRun() {
        ProteinferRun run = runDao.getProteinferRun(3);
        assertEquals(3, run.getId());
        assertEquals(ProteinferStatus.PENDING, run.getStatus());
        assertEquals(ProteinInferenceProgram.IDPICKER, run.getProgram());
        assertEquals("some comments", run.getComments());
        assertNull(run.getDateCompleted());
        assertNotNull(run.getDateCreated());
    }
    
    public final void testUpdateBaseProteinferRunOfQ() {
        ProteinferRun run = runDao.getProteinferRun(3);
        assertNull(run.getDateCompleted());
        assertEquals(ProteinferStatus.PENDING, run.getStatus());
        run.setStatus(ProteinferStatus.COMPLETE);
        run.setDateCompleted(new Date(System.currentTimeMillis()));
        
        runDao.update(run);
        run = runDao.getProteinferRun(3);
        assertNotNull(run.getDateCompleted());
        assertEquals(ProteinferStatus.COMPLETE, run.getStatus());
    }

    public final void testGetProteinferIdsForRunSearches() {
        fail("Not yet implemented"); // TODO
    }

}
