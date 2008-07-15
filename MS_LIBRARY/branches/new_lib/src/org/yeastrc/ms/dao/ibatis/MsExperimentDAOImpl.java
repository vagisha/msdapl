/**
 * MsExperimentDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.MsExperimentDAO;
import org.yeastrc.ms.dao.MsRunDAO;
import org.yeastrc.ms.domain.MsExperiment;
import org.yeastrc.ms.domain.MsExperimentDb;
import org.yeastrc.ms.domain.MsRun;
import org.yeastrc.ms.domain.MsRunDb;
import org.yeastrc.ms.domain.MsRun.RunFileFormat;
import org.yeastrc.ms.domain.ms2File.MS2Run;
import org.yeastrc.ms.domain.ms2File.MS2RunDb;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MsExperimentDAOImpl extends BaseSqlMapDAO implements MsExperimentDAO {
    
    public MsExperimentDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public MsExperimentDb load(int msExperimentId) {
        return (MsExperimentDb)queryForObject("MsExperiment.select", msExperimentId);
    }
    
    public int save(MsExperiment experiment) {
        return saveAndReturnId("MsExperiment.insert", experiment);
    }
    
    public List<Integer> selectAllExperimentIds() {
        return queryForList("MsExperiment.selectAll");
    }
    
    /**
     * Deletes the experiment and all its associated runs
     * This method will first check the type of each run and use the 
     * appropriate DAO class to delete the run
     */
    public void delete(int msExperimentId) {
        
        // delete the runs for this experiment
//        MsRunDAO<MsRun, MsRunDb> runDao = DAOFactory.instance().getMsRunDAO();
//        List<Integer> runIds = runDao.loadRunIdsForExperiment(msExperimentId);
//        
//        if (runIds != null && runIds.size() > 0) {
//            // we assume that ALL runs in an experiment have the same file format
//            // get the format for the first run in the list
//            RunFileFormat format = null;
//            
//            try {
//                format = runDao.getRunFileFormat(runIds.get(0));
//            }
//            catch (Exception e) {
//                // Exception is thrown when the run does not exist in the database
//                // this should NEVER happen since we are getting the run ids 
//                // from the database.
//                log.error("Could not delete runs for experiment", e);
//                throw new RuntimeException("Could not delete runs for experiment", e);
//            }
//            
//            if (format == RunFileFormat.MS2) {
//                MsRunDAO<MS2Run, MS2RunDb> ms2RunDao = DAOFactory.instance().getMS2FileRunDAO();
//                ms2RunDao.deleteRunsForExperiment(msExperimentId);
//            }
//            else {
//                runDao.deleteRunsForExperiment(msExperimentId);
//            }
//        }
        // delete the experiment
        delete("MsExperiment.delete", msExperimentId);
    }
}
