package org.yeastrc.www.proteinfer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;

import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferRunDAO;
import edu.uwpr.protinfer.database.dto.ProteinferRun;
import edu.uwpr.protinfer.database.dto.ProteinferInput.InputType;

public class ProteinferRunSearcher {

    private static final Logger log = Logger.getLogger(ProteinferRunSearcher.class);
    
    private static final ProteinferDAOFactory factory = ProteinferDAOFactory.instance();
    private static final ProteinferRunDAO runDao = factory.getProteinferRunDao();
    
    private ProteinferRunSearcher() {}
    
    public static List<ProteinferJob> getProteinferJobsForMsSearch(int msSearchId) {
        
        Set<Integer> pinferRunIds = new HashSet<Integer>();
        
        // first check if search results were used to do protein inference
        List<Integer> msRunSearchIds = getRunSearchIdsForMsSearch(msSearchId);
        List<Integer> sPinferRunIds = runDao.loadProteinferIdsForInputIds(msRunSearchIds, InputType.SEARCH);
        if(pinferRunIds != null)    
            pinferRunIds.addAll(sPinferRunIds);
        
        // now check if there is any analysis associated with this search
        List<Integer> analysisIds = getAnalysisIdsForMsSearch(msSearchId);
        
        // for each analysisId get the ids of all the protein inference runs
        for(int analysisId: analysisIds) {
            // get a list run search analyses
            List<Integer> rsAnalysisIds = getRunSearchAnalysisIdsForAnalysis(analysisId);
            // get a list of protein inference ids associated with these run search analyses
            List<Integer> aPinferIds = runDao.loadProteinferIdsForInputIds(rsAnalysisIds, InputType.ANALYSIS);
            if(aPinferIds != null)
                pinferRunIds.addAll(aPinferIds);
        }
        
        if(pinferRunIds == null || pinferRunIds.size() == 0)
            return new ArrayList<ProteinferJob>(0);
        
        List<ProteinferJob> jobs = new ArrayList<ProteinferJob>(pinferRunIds.size());
        for(int pid: pinferRunIds) {
            ProteinferRun run = runDao.loadProteinferRun(pid);
            if(run != null) {
                
                ProteinferJob job = getJobForPinferRunId(run.getId());
                if(job != null)
                    jobs.add(job);
            }
        }
        // sort jobs by id
        Collections.sort(jobs, new Comparator<ProteinferJob>() {
            public int compare(ProteinferJob o1, ProteinferJob o2) {
                return Integer.valueOf(o1.getPinferId()).compareTo(o2.getPinferId());
            }});
        return jobs;
    }
    
    public static ProteinferJob getJobForPinferRunId(int pinferRunId) {
        
        ProteinferRun run = runDao.loadProteinferRun(pinferRunId);
        if(run != null) {
            
//            // make sure the input generator for this protein inference program was
//            // a search program or an analysis program
//            if(!Program.isSearchProgram(run.getInputGenerator()) && !Program.isAnalysisProgram(run.getInputGenerator()))
//                continue;
            ProteinferJob job = null;
            try {
                job = getJob(run.getId());
            }
            catch (SQLException e) {
               log.error("Exception getting ProteinferJob", e);
               return null;
            }
            if(job == null) {
                log.error("No job found with protein inference run id: "+pinferRunId);
                return null;
            }
            job.setProgram(run.getProgramString());
            job.setComments(run.getComments());
            return job;
        }
        return null;
    }
    
    
    private static ProteinferJob getJob(int pinferRunId) throws SQLException {
        
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

    private static List<Integer> getRunSearchIdsForMsSearch(int msSearchId) {
        org.yeastrc.ms.dao.DAOFactory factory = org.yeastrc.ms.dao.DAOFactory.instance();
        MsRunSearchDAO runSearchDao = factory.getMsRunSearchDAO();
        return runSearchDao.loadRunSearchIdsForSearch(msSearchId);
    }
    
    private static List<Integer> getAnalysisIdsForMsSearch(int msSearchId) {
        org.yeastrc.ms.dao.DAOFactory factory = org.yeastrc.ms.dao.DAOFactory.instance();
        return factory.getMsSearchAnalysisDAO().getAnalysisIdsForSearch(msSearchId);
    }
    
    private static List<Integer> getRunSearchAnalysisIdsForAnalysis(int analysisId) {
        org.yeastrc.ms.dao.DAOFactory factory = org.yeastrc.ms.dao.DAOFactory.instance();
        return factory.getMsRunSearchAnalysisDAO().getRunSearchAnalysisIdsForAnalysis(analysisId);
    }
}
