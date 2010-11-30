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
			int groupId;
			try {
				groupId = Groups.getInstance().getGroupID(Projects.MACCOSS);
			} catch (SQLException e) {
				log.error("Error getting groupID form : "+Projects.MACCOSS, e);
				return 0;
			}
			int userId = getLabDirector(groupId);
			if(userId == 0) {
				log.error("No Lab Director found for group MacCoss");
			}
			else {
				if (Groups.getInstance().isMember(userId, Projects.MACCOSS)) {
					MACCOSS = userId;
				}
				else {
					log.error("Lab director for MacCoss group not a member of MacCoss group");
				}
			}
		}
		return MACCOSS;
	}
	
	public static int getGoodlett() {
		
		if(GOODLETT == 0) {
			int groupId;
			try {
				groupId = Groups.getInstance().getGroupID(Projects.GOODLETT);
			} catch (SQLException e) {
				log.error("Error getting groupID form : "+Projects.GOODLETT, e);
				return 0;
			}
			int userId = getLabDirector(groupId);
			if(userId == 0) {
				log.error("No Lab Director found for group Goodlett");
			}
			else {
				if (Groups.getInstance().isMember(userId, Projects.GOODLETT)) {
					GOODLETT = userId;
				}
				else {
					log.error("Lab director for Goodlett group not a member of Goodlett group");
				}
			}
		}
		return GOODLETT;
	}
	
	public static int getBruce() {
		if(BRUCE == 0) {
			int groupId;
			try {
				groupId = Groups.getInstance().getGroupID(Projects.BRUCE);
			} catch (SQLException e) {
				log.error("Error getting groupID form : "+Projects.BRUCE, e);
				return 0;
			}
			int userId = getLabDirector(groupId);
			if(userId == 0) {
				log.error("No Lab Director found for group Bruce");
			}
			else {
				if (Groups.getInstance().isMember(userId, Projects.BRUCE)) {
					BRUCE = userId;
				}
				else {
					log.error("Lab director for Bruce group not a member of Bruce group");
				}
			}
		}
		return BRUCE;
	}
	
	public static int getVillen() {
		if(VILLEN == 0) {
			int groupId;
			try {
				groupId = Groups.getInstance().getGroupID(Projects.VILLEN);
			} catch (SQLException e) {
				log.error("Error getting groupID form : "+Projects.VILLEN, e);
				return 0;
			}
			int userId = getLabDirector(groupId);
			if(userId == 0) {
				log.error("No Lab Director found for group Villen");
			}
			else {
				if (Groups.getInstance().isMember(userId, Projects.VILLEN)) {
					VILLEN = userId;
				}
				else {
					log.error("Lab director for Villen group not a member of Villen group");
				}
			}
		}
		return VILLEN;
	}
	
	
	private static int getLabDirector(int groupId) {
		
		// Get our connection to the database.
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
			stmt = conn.createStatement();
			String sql = "SELECT researcherID FROM tblLabDirector WHERE groupID = "+groupId;
			rs = stmt.executeQuery(sql);

			if (rs.next()) {
				return rs.getInt("researcherID");
			}
			else
				return 0;
		}
		catch(SQLException e) {
			log.error("Error getting Lab director for groupID: "+groupId, e);
			return 0;
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
