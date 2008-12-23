package org.yeastrc.www.proteinfer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;

import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferRunDAO;
import edu.uwpr.protinfer.database.dto.ProteinferRun;

public class ProteinferRunSearcher {

    private static final Logger log = Logger.getLogger(ProteinferRunSearcher.class);
    
    private static final ProteinferDAOFactory factory = ProteinferDAOFactory.instance();
    private static final ProteinferRunDAO runDao = factory.getProteinferRunDao();
    
    private ProteinferRunSearcher() {}
    
    public static List<ProteinferJob> getProteinferRunIdsForMsSearch(int msSearchId) {
        List<Integer> msRunSearchIds = getRunSearchIdsForMsSearch(msSearchId);
        List<Integer> pinferRunIds = getProteinferRunIdsForRunSearches(msRunSearchIds);
        if(pinferRunIds == null || pinferRunIds.size() == 0)
            return new ArrayList<ProteinferJob>(0);
        
        List<ProteinferJob> jobs = new ArrayList<ProteinferJob>(pinferRunIds.size());
        for(int pid: pinferRunIds) {
            ProteinferRun run = runDao.getProteinferRun(pid);
            if(run != null) {
                ProteinferJob job = null;
                try {
                    job = getJobForPinferRunId(run.getId());
                }
                catch (SQLException e) {
                   log.error("Exception getting ProteinferJob", e);
                   continue;
                }
                if(job == null) {
                    log.error("No job found with protein inference run id: "+pid);
                    continue;
                }
                job.setProgram(run.getProgramString());
                job.setComments(run.getComments());
                jobs.add(job);
            }
        }
        return jobs;
    }
    
    private static ProteinferJob getJobForPinferRunId(int pinferRunId) throws SQLException {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            
            String sql = "SELECT * FROM YRC_JOB_QUEUE.tblJobs AS j, YRC_JOB_QUEUE.tblProteinInferJobs AS pj "+
                        "WHERE j.id = pj.jobID AND pj.piRunID="+pinferRunId;
            
            conn = DBConnectionManager.getConnection( "yrc" );
            stmt = conn.prepareStatement( sql );
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                ProteinferJob job = new ProteinferJob();
                job.setId( rs.getInt("jobID"));
                job.setSubmitter( rs.getInt( "submitter" ) );
                job.setType( rs.getInt( "type" ) );
                job.setSubmitDate( rs.getDate( "submitDate" ) );
                job.setLastUpdate( rs.getDate( "lastUpdate" ) );
                job.setStatus( rs.getInt( "status" ) );
                job.setAttempts( rs.getInt( "attempts" ) );
                job.setLog( rs.getString( "log" ) );
                job.setPinferRunId(pinferRunId);
                return job;
            }
            
            rs.close(); rs = null;
            stmt.close(); stmt = null;
            conn.close(); conn = null;
            
        } finally {
            
            if (rs != null) {
                try { rs.close(); rs = null; } catch (Exception e) { ; }
            }

            if (stmt != null) {
                try { stmt.close(); stmt = null; } catch (Exception e) { ; }
            }
            
            if (conn != null) {
                try { conn.close(); conn = null; } catch (Exception e) { ; }
            }           
        }
        return null;
    }

    private static List<Integer> getProteinferRunIdsForRunSearches(List<Integer> msRunSearchIds) {
        return runDao.getProteinferIdsForRunSearches(msRunSearchIds);
    }

    private static List<Integer> getRunSearchIdsForMsSearch(int msSearchId) {
        org.yeastrc.ms.dao.DAOFactory factory = org.yeastrc.ms.dao.DAOFactory.instance();
        MsRunSearchDAO runSearchDao = factory.getMsRunSearchDAO();
        return runSearchDao.loadRunSearchIdsForSearch(msSearchId);
    }
}
