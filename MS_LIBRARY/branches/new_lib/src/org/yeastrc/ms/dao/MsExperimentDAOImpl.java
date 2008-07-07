/**
 * MsExperimentDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.dto.MsExperiment;
import org.yeastrc.ms.dto.MsRun;
import org.yeastrc.ms.dto.MsRun.RunFileFormat;
import org.yeastrc.ms.dto.ms2File.MS2FileRun;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MsExperimentDAOImpl extends BaseSqlMapDAO implements MsExperimentDAO {
    
    public MsExperimentDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public MsExperiment load(int msExperimentId) {
        return (MsExperiment)queryForObject("MsExperiment.select", msExperimentId);
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
        MsRunDAO<MsRun> runDao = DAOFactory.instance().getMsRunDAO();
        List<Integer> runIds = runDao.loadRunIdsForExperiment(msExperimentId);
        
        if (runIds != null && runIds.size() > 0) {
            // we assume that ALL runs in an experiment have the same file format
            // get the format for the first run in the list
            RunFileFormat format = null;
            
            try {
                format = runDao.getRunFileFormat(runIds.get(0));
            }
            catch (Exception e) {
                // Exception is thrown when the run does not exist in the database
                // this should NEVER happen since we are getting the run ids 
                // from the database.
                log.error("Could not delete runs for experiment", e);
                throw new RuntimeException("Could not delete runs for experiment", e);
            }
            
            if (format == RunFileFormat.MS2) {
                MsRunDAO<MS2FileRun> ms2RunDao = DAOFactory.instance().getMS2FileRunDAO();
                ms2RunDao.deleteRunsForExperiment(msExperimentId);
            }
            else {
                runDao.deleteRunsForExperiment(msExperimentId);
            }
        }
        // delete the experiment
        delete("MsExperiment.delete", msExperimentId);
    }
}
