/**
 * 
 */
package org.yeastrc.jobqueue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.yeastrc.db.DBConnectionManager;

/**
 * @author Mike
 *
 */
public class MSJobFactory {

    private static MSJobFactory instance;
    
	private MSJobFactory() { }
	
	/**
	 * Get an instance of this class
	 * @return
	 */
	public static MSJobFactory getInstance() {
	    if(instance == null)
	        instance = new MSJobFactory();
	    return instance;
	}
	
	/**
	 * Get the given MSJob from the database
	 * @param jobID
	 * @return
	 * @throws Exception
	 */
	public MSJob getJob( int jobID ) throws Exception {
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		MSJob job = null;
		
		try {
			
			conn = DBConnectionManager.getConnection(DBConnectionManager.JOB_QUEUE);
			String sql = "SELECT submitter, type, submitDate, lastUpdate, status, attempts, log FROM tblJobs WHERE id = ?";
			stmt = conn.prepareStatement( sql );
			stmt.setInt( 1, jobID );
			rs = stmt.executeQuery();
			
			if ( !rs.next() )
				throw new Exception( "Invalid job ID on MSJob.getJob().  Job ID: " + jobID );
			
			job = new MSJob();
			job.setId( jobID );
			job.setSubmitter( rs.getInt( "submitter" ) );
			job.setType( rs.getInt( "type" ) );
			job.setSubmitDate( rs.getDate( "submitDate" ) );
			job.setLastUpdate( rs.getDate( "lastUpdate" ) );
			job.setStatus( rs.getInt( "status" ) );
			job.setAttempts( rs.getInt( "attempts" ) );
			job.setLog( rs.getString( "log" ) );
			
			rs.close(); rs = null;
			stmt.close(); stmt = null;
			
			sql = "SELECT * FROM tblMSJobs WHERE jobID = ?";
			stmt = conn.prepareStatement( sql );
			stmt.setInt( 1, jobID );
			rs = stmt.executeQuery();
			
			if (!rs.next())
				throw new Exception( "Invalid job ID on MSJob.getJob().  Job ID: " + jobID );
			
			job.setProjectID( rs.getInt( "projectID" ) );
			job.setServerDirectory( rs.getString( "serverDirectory" ) );
			job.setRunDate( rs.getDate( "runDate" ) );
			job.setBaitProtein( rs.getInt( "baitProtein" ) );
			job.setBaitProteinDescription( rs.getString( "baitDescription" ) );
			job.setTargetSpecies( rs.getInt( "targetSpecies" ) );
			job.setComments( rs.getString( "comments" ) );
			job.setRunID( rs.getInt( "runID" ) );
			job.setGroup( rs.getInt( "groupID" ) );
			
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
		
		
		return job;
	}
	
	/**
     * Get the MSJob from the database for the given experimentId
     * @param experimentId
     * @return
     * @throws Exception
     */
    public MSJob getJobForExperiment( int experimentId ) throws Exception {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        MSJob job = null;
        
        try {
            
            conn = DBConnectionManager.getConnection(DBConnectionManager.JOB_QUEUE);
            String sql = "SELECT * from tblJobs AS j, tblMSJobs AS mj WHERE mj.experimentID=? AND j.id = mj.jobID";
            stmt = conn.prepareStatement( sql );
            stmt.setInt( 1, experimentId );
            rs = stmt.executeQuery();
            
            if ( !rs.next() )
                throw new Exception( "No MSJobs found for experimentID: " + experimentId );
            
            job = new MSJob();
            job.setId(rs.getInt("id"));
            job.setSubmitter( rs.getInt( "submitter" ) );
            job.setType( rs.getInt( "type" ) );
            job.setSubmitDate( rs.getDate( "submitDate" ) );
            job.setLastUpdate( rs.getDate( "lastUpdate" ) );
            job.setStatus( rs.getInt( "status" ) );
            job.setAttempts( rs.getInt( "attempts" ) );
            job.setLog( rs.getString( "log" ) );
            
            job.setProjectID( rs.getInt( "projectID" ) );
            job.setServerDirectory( rs.getString( "serverDirectory" ) );
            job.setRunDate( rs.getDate( "runDate" ) );
            job.setBaitProtein( rs.getInt( "baitProtein" ) );
            job.setBaitProteinDescription( rs.getString( "baitDescription" ) );
            job.setTargetSpecies( rs.getInt( "targetSpecies" ) );
            job.setComments( rs.getString( "comments" ) );
            job.setRunID( rs.getInt( "runID" ) );
            job.setGroup( rs.getInt( "groupID" ) );
            
            
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
        
        return job;
    }
	
	
}
