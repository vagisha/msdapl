/**
 * LabDirector.java
 * @author Vagisha Sharma
 * Mar 25, 2010
 * @version 1.0
 */
package org.yeastrc.www.project;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.project.Projects;
import org.yeastrc.www.user.Groups;


/**
 * 
 */
public class LabDirector {

	private LabDirector() {}
	
	private static final Logger log = Logger.getLogger(LabDirector.class.getName());
	
	private static int MACCOSS = 0;
	private static int GOODLETT = 0;
	private static int BRUCE = 0;
	private static int VILLEN = 0;
	
	public static int getMacCoss() {
		if(MACCOSS == 0) {
			List<Integer> idsForMacCoss = getUserId("MacCoss");
			if(idsForMacCoss.size() == 0) {
				log.error("No ID found for MacCoss");
			}
			else if(idsForMacCoss.size() > 1) {
				log.error("Multiple IDs found for MacCoss");
			}
			else {
				int id = idsForMacCoss.get(0);
				if (Groups.getInstance().isMember(id, Projects.MACCOSS)) {
					MACCOSS = id;
				}
				else {
					log.error("User with last name MacCoss is not a member of MacCoss group");
				}
			}
		}
		return MACCOSS;
	}
	
	public static int getGoodlett() {
		
		if(GOODLETT == 0) {
			
			List<Integer> idsForGoodlett = getUserId("Goodlett");
			if(idsForGoodlett.size() == 0) {
				log.error("No ID found for Goodlett");
			}
			else if(idsForGoodlett.size() > 1) {
				log.error("Multiple IDs found for Goodlett");
			}
			else {
				int id = idsForGoodlett.get(0);
				if (Groups.getInstance().isMember(id, Projects.GOODLETT)) {
					GOODLETT = id;
				}
				else {
					log.error("User with last name Goodlett is not a member of Goodlett group");
				}
			}
		}
		return GOODLETT;
	}
	
	public static int getBruce() {
		if(BRUCE == 0) {
			
			List<Integer> idsForBruce = getUserId("Bruce");
			if(idsForBruce.size() == 0) {
				log.error("No ID found for Bruce");
			}
			else if(idsForBruce.size() > 1) {
				log.error("Multiple IDs found for Bruce");
			}
			else {
				int id = idsForBruce.get(0);
				if (Groups.getInstance().isMember(id, Projects.BRUCE)) {
					BRUCE = id;
				}
				else {
					log.error("User with last name Bruce is not a member of Bruce group");
				}
			}
		}
		return BRUCE;
	}
	
	public static int getVillen() {
		if(VILLEN == 0) {
			
			List<Integer> idsForVillen = getUserId("Villen");
			if(idsForVillen.size() == 0) {
				log.error("No ID found for Villen");
			}
			else if(idsForVillen.size() > 1) {
				log.error("Multiple IDs found for Villen");
			}
			else {
				int id = idsForVillen.get(0);
				if (Groups.getInstance().isMember(id, Projects.VILLEN)) {
					VILLEN = id;
				}
				else {
					log.error("User with last name Villen is not a member of Villen group");
				}
			}
		}
		return VILLEN;
	}
	
	private static List<Integer> getUserId(String lastName) {
		
		// Get our connection to the database.
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
			stmt = conn.createStatement();
			String sql = "SELECT researcherID from tblResearchers WHERE researcherLastName = \""+lastName+"\"";
			rs = stmt.executeQuery(sql);

			List<Integer> ids = new ArrayList<Integer>();
			while (rs.next()) {
				ids.add(rs.getInt("researcherID"));
			}
			
			return ids;
		}
		catch(SQLException e) {
			log.error("Error getting Lab director with name: "+lastName, e);
			return new ArrayList<Integer>(0);
		}
		finally {

			// Always make sure result sets and statements are closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try { rs.close(); } catch (SQLException e) { ; }
				rs = null;
			}
			if (stmt != null) {
				try { stmt.close(); } catch (SQLException e) { ; }
				stmt = null;
			}
			if (conn != null) {
				try { conn.close(); } catch (SQLException e) { ; }
				conn = null;
			}
		}
	}
}
