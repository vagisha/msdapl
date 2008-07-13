package org.yeastrc.ms.dao;

import java.sql.Date;
import java.util.List;

import junit.framework.TestCase;

import org.yeastrc.ms.dao.ibatis.DAOFactory;
import org.yeastrc.ms.domain.MsExperiment;
import org.yeastrc.ms.domain.MsExperimentDb;

public class MsExperimentDAOImplTest extends TestCase {

    private MsExperimentDAO expDao;
    
    public void setUp() throws Exception {
        expDao = DAOFactory.instance().getMsExperimentDAO();
    }
    
    public void testSaveLoadDelete() {
        // save an experiment
        MsExperiment experiment = createMsExperiment();
        int expId = expDao.save(experiment);
        
        MsExperimentDb experimentDb = expDao.load(0); // there should be experiments with id 0
        assertNull(experimentDb);
        
        List<Integer> expIds = expDao.selectAllExperimentIds();
        assertEquals(1, expIds.size());
        assertEquals(Integer.valueOf(expId), expIds.get(0));
        experimentDb = expDao.load(expIds.get(0));
        assertNotNull(experimentDb);
        
        // make sure the original experiment and the one saved in the database are same.
        assertEquals(experiment.getDate().toString(), experimentDb.getDate().toString());
        assertEquals(experiment.getServerAddress(), experimentDb.getServerAddress());
        assertEquals(experiment.getServerDirectory(), experiment.getServerDirectory());
        
        // delete everything.
        expDao.delete(0); // should not delete anything
        expIds = expDao.selectAllExperimentIds();
        assertEquals(1, expIds.size());
        expDao.delete(expIds.get(0));
        expIds = expDao.selectAllExperimentIds();
        assertEquals(0, expIds.size());
    }

    private MsExperiment createMsExperiment() {
        MsExperiment experiment = new MsExperiment(){
            public Date getDate() {
                return new Date(new java.util.Date().getTime());
            }
            public String getServerAddress() {
                return "server/address";
            }
            public String getServerDirectory() {
                return "server/directory";
            }};
        return experiment;
    }
}
