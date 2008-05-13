/*
 * DataForProjectGenerator.java
 * Created on Jun 15, 2006
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.progress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.project.Project;
import org.yeastrc.project.Projects;
import org.yeastrc.y2h.Y2HScreen;
import org.yeastrc.yates.YatesRun;
import java.util.Date;
import org.yeastrc.microscopy.*;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Jun 15, 2006
 */

public class DataForProjectGenerator {

	// private constructor
	private DataForProjectGenerator() { }
	
	/**
	 * Get an instance of this class
	 * @return
	 */
	public static DataForProjectGenerator getInstance() {
		return new DataForProjectGenerator();
	}
	
	/**
	 * Generate a string that lists any data uploaded for the given project in the last reporting year
	 * @param p
	 * @return
	 * @throws Exception
	 */
	public String generateDataForProject( Project p, Date start, Date end ) throws Exception {
		
		String retString = null;
		
		//String startDate = (start.getYear() + 1900) + "-" + (start.getMonth() + 1) + "-" + start.getDay();
		//String endDate = (end.getYear() + 1900)  + "-" + (end.getMonth() + 1) + "-" + end.getDay();
		
		String startDate = "2006-07-01";
		String endDate = "2007-07-01";
		
		if (p.getGroups().contains( Projects.MACCOSS ) || p.getGroups().contains( Projects.YATES )) {

			// Look for mass spectrometry data

			// Get our connection to the database.
			Connection conn = DBConnectionManager.getConnection("yrc");
			PreparedStatement stmt = null;
			ResultSet rs = null;
			String sql = "SELECT id FROM tblYatesRun RUN WHERE projectID = " + p.getID() + " AND uploadDate >= '" + startDate +"' AND uploadDate <= '" + endDate + "'";
			//System.out.println(sql);
			stmt = conn.prepareStatement( sql );
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				
				retString = "\n\nThe following Mass Spectrometry data was uploaded to the collaborators' database in the last reporting year:\n";
				retString += "Upload Date\tBait Description\tComments\n";
				YatesRun run = new YatesRun();
				run.load( rs.getInt( 1 ) );
				retString += run.getUploadDate() + "\t";
				
				if (run.getBaitProtein() != null)
					retString += run.getBaitProtein().getListing();
				else {
					if (run.getBaitDesc() != null && !run.getBaitDesc().equals( "" ))
						retString += run.getBaitDesc();
					else
						retString += "No bait entered";
				}
										
				retString += "\t";
				
				if (run.getComments() != null)
					retString += run.getComments();
				else
					retString += "No comments";
				
				retString += "\n";
				
				while (rs.next()) {
					run = new YatesRun();
					run.load ( rs.getInt( 1 ) );
					
					retString += run.getUploadDate() + "\t";
					
					if (run.getBaitProtein() != null)
						retString += run.getBaitProtein().getListing();
					else {
						if (run.getBaitDesc() != null && !run.getBaitDesc().equals( "" ))
							retString += run.getBaitDesc();
						else
							retString += "No bait entered";
					}
											
					retString += "\t";
					
					if (run.getComments() != null)
						retString += run.getComments();
					else
						retString += "No comments";
					
					retString += "\n";
									
				}

			}
			
			try { rs.close(); } catch (Exception e) { ; }
			try { stmt.close(); } catch (Exception e) { ; }
			try { conn.close(); } catch (Exception e) { ; }
			rs = null; stmt = null; conn = null;
		}
			

		if (p.getGroups().contains( Projects.TWOHYBRID )) {
			
			// look for yeast two hybrid data
			
			// Get our connection to the database.
			Connection conn = DBConnectionManager.getConnection("yrc");
			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			String sql = "SELECT screenID FROM tblY2HScreen WHERE projectID = " + p.getID() + " AND screenDate >= '" + startDate + "' AND screenDate <= '" + endDate + "'";
			stmt = conn.prepareStatement( sql );
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				
				if (retString == null)
					retString = "";
				
				retString += "\n\nThe following Yeast Two-Hybrid data was generated in the last reporting year:\n";
				retString += "ScreenDate\tBait Description\tComments\n";
				
				Y2HScreen screen = new Y2HScreen();
				screen.load( rs.getInt( 1 ) );
				retString += screen.getScreenDate() + "\t" + screen.getBait().getProtein().getListing() + "\t" + screen.getComments() + "\t";
				
				while (rs.next()) {
					screen = new Y2HScreen();
					screen.load( rs.getInt ( 1 ) );
					
					retString += screen.getScreenDate() + "\t" + screen.getBait().getProtein().getListing() + "\t" + screen.getComments() + "\t";
				}
			}
			
			try { rs.close(); } catch (Exception e) { ; }
			try { stmt.close(); } catch (Exception e) { ; }
			try { conn.close(); } catch (Exception e) { ; }
			rs = null; stmt = null; conn = null;
		}
		
		if (p.getGroups().contains( Projects.MICROSCOPY )) {
			
			// look for microscopy data
			
			// Get our connection to the database.
			Connection conn = DBConnectionManager.getConnection("yrc");
			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			String sql = "SELECT locExpID FROM tblLocExperiments WHERE projectID = " + p.getID() + " AND locExpDate >= '" + startDate + "' AND locExpDate <= '" + endDate + "'";
			stmt = conn.prepareStatement( sql );
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				
				if (retString == null)
					retString = "";
				
				retString += "\n\nThe following microscopy data was generated in the last reporting year:\n";
				retString += "Experiment Date\tTagged Protein 1\tTagged Protein 2\tComments\n";
				
				Experiment exp = ExperimentFactory.getInstance().getExperiment( rs.getInt( 1 ) );
				retString += exp.getExperimentDate() + "\t";
				if (exp.getBait1() != null)
					retString += exp.getBait1().getListing();
				else
					retString += "n/a";
				
				retString += "\t";
				
				if (exp.getBait2() != null)
					retString += exp.getBait2().getListing();
				else
					retString += "n/a";
				
				retString += "\t";
				
				if (exp.getComments() != null)
					retString += exp.getComments();
				retString += "\n";
				
				while (rs.next()) {
					exp = ExperimentFactory.getInstance().getExperiment( rs.getInt( 1 ) );
					retString += exp.getExperimentDate() + "\t";
					if (exp.getBait1() != null)
						retString += exp.getBait1().getListing();
					else
						retString += "n/a";
					
					retString += "\t";
					
					if (exp.getBait2() != null)
						retString += exp.getBait2().getListing();
					else
						retString += "n/a";
					
					retString += "\t";
					
					if (exp.getComments() != null)
						retString += exp.getComments();
					retString += "\n";
				}
			}
			
			try { rs.close(); } catch (Exception e) { ; }
			try { stmt.close(); } catch (Exception e) { ; }
			try { conn.close(); } catch (Exception e) { ; }
			rs = null; stmt = null; conn = null;
			
		}
		
		
		return retString;
	}
	
}
