package org.yeastrc.ms.dao;

import java.sql.Date;
import java.util.List;

import junit.framework.TestCase;

import org.yeastrc.ms.dao.ibatis.DAOFactory;
import org.yeastrc.ms.domain.IMsExperiment;
import org.yeastrc.ms.domain.db.MsExperiment;

public class MsExperimentDAOImplTest extends TestCase {

    private MsExperimentDAO expDao;
    
    public void setUp() throws Exception {
        expDao = DAOFactory.instance().getMsExperimentDAO();
    }
    
    public void testSave() {
        IMsExperiment experiment = createMsExperiment();
        try {
            int expId = expDao.save(experiment);
            System.out.println("Inserted experiment id: "+expId);
            assertNotSame(0, expId);
        }
        catch (RuntimeException e) {
            e.printStackTrace();
            fail("Error saving experiment to database");
        }
    }

    private IMsExperiment createMsExperiment() {
        MsExperiment experiment = new MsExperiment();
        experiment.setDate(new Date(new java.util.Date().getTime()));
        experiment.setServerAddress("server/address");
        experiment.setServerDirectory("server/directory");
        return experiment;
    }
    
    public void testSelectAllExperimentIds() {
        List<Integer> expIds = expDao.selectAllExperimentIds();
        assertEquals(1, expIds.size());
    }
    
    public void testLoad() {
        try {
            IMsExperiment experiment = expDao.load(0);
            assertNull(experiment);
            List<Integer> expIds = expDao.selectAllExperimentIds();
            assertEquals(1, expIds.size());
            assertNotNull(expDao.load(expIds.get(0)));
        }
        catch(RuntimeException e) {
            e.printStackTrace();
            fail("Error loading experiment from the database");
        }
    }

    
    public void testDelete() {
        expDao.delete(0); // should not delete anything
        List<Integer> expIds = expDao.selectAllExperimentIds();
        assertEquals(1, expIds.size());
        expDao.delete(expIds.get(0));
        expIds = expDao.selectAllExperimentIds();
        assertEquals(0, expIds.size());
    }
    
}
