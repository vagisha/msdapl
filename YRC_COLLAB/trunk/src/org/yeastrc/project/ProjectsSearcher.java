/* ProjectsSearch.java
 * Created on Mar 31, 2004
 */
package org.yeastrc.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Date;

import org.yeastrc.data.InvalidIDException;
import org.yeastrc.db.DBConnectionManager;

/**
 * Provides a set of methods for setting search parameters, and retrieving
 * search results based on those parameters.
 * 
 * @author Michael Riffle <mriffle@alumni.washington.edu>
 * @version 1.0, Mar 31, 2004
 *
 */
public class ProjectsSearcher {

	/**
	 * Get a new ProjectSearch
	 */
	public ProjectsSearcher() {
		this.searchTokens = new HashSet<String>();
		this.types = new HashSet();
		this.groups = new HashSet();
	}

	public List search() throws SQLException {
		ArrayList retList = new ArrayList();
		Set<Integer> addedProjects = new HashSet<Integer>();
		
		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			boolean haveConstraint = false;
			
			/*
			String sqlStr = "SELECT P.projectID, P.projectSubmitDate ";
			sqlStr += "FROM tblProjects AS P LEFT OUTER JOIN tblResearchers AS RPI ";
			sqlStr += "ON RPI.researcherID = P.projectPI LEFT OUTER JOIN tblResearchers AS RB ";
			sqlStr += "ON RB.researcherID = P.projectResearcherB LEFT OUTER JOIN tblResearchers AS RC ";
			sqlStr += "ON RC.researcherID = P.projectResearcherC LEFT OUTER JOIN tblResearchers AS RD ";
			sqlStr += "ON RD.researcherID = P.projectResearcherD";
			*/
			
			String sqlStr = "SELECT P.projectID, P.projectSubmitDate FROM tblProjects AS P ";
			sqlStr += "INNER JOIN tblProjectResearcher AS PR ON P.projectID = PR.projectID ";
			sqlStr += "INNER JOIN tblResearchers AS R ON PR.researcherID = R.researcherID";
			
			
			// We have a project type constraint
			if (this.types.size() > 0) {
				sqlStr += " WHERE";
				haveConstraint = true;
				
				sqlStr += " (";
				
				Iterator iter = this.types.iterator();
				String type = (String)(iter.next());
				sqlStr += "P.projectType = '" + type + "'";
				
				while (iter.hasNext()) {
					type = (String)(iter.next());
					sqlStr += " OR P.projectType = '" + type + "'";
				}
				
				sqlStr += ")";
			}

			// We have search tokens
			if (this.searchTokens.size() > 0) {
				if (haveConstraint) {
					sqlStr += " AND";
				} else {
					sqlStr += " WHERE";
					haveConstraint = true;
				}
				
				// need this to be like (R.researcherLastName LIKE '%tok1' OR R.researcherLastName LIKE '%tok2' ... OR R.researcherLastName LIKE '%tokN%')
				// then repeat for each of the searched fields, each of those also seperated by OR

				Set<String> fieldsToSearch = new HashSet<String>();
				fieldsToSearch.add( "R.researcherLastName" );
				fieldsToSearch.add( "R.researcherFirstName" );
				fieldsToSearch.add( "P.projectAbstract" );
				fieldsToSearch.add( "P.publicAbstract" );
				fieldsToSearch.add( "P.projectKeywords" );
				fieldsToSearch.add( "P.projectProgress" );
				fieldsToSearch.add( "P.projectTitle" );
				
				// loop through the fields to search
				int oc = 0;
				for( String fieldToSearch : fieldsToSearch ) {
					if( oc == 0 )
						sqlStr += " (";
					else
						sqlStr += " OR (";
					
					int tc = 0;
					for( String stoken : searchTokens ) {
						if ( tc != 0 )
							sqlStr += " OR ";
						else
							sqlStr += " ";
						
						sqlStr += fieldToSearch + " LIKE '%" + stoken + "%'";
						tc++;

					}//end token for loop	
					
					sqlStr += ")";
					oc++;

				}//end search field for loop
				
			}//end searchTokens size check
			
			// Start date constraint
			if (this.startDate != null) {
				if (haveConstraint) { sqlStr += " AND"; }
				else {
					sqlStr += " WHERE";
					haveConstraint = true;
				}
				
				String year = String.valueOf(this.startDate.getYear() + 1900);
				String month = String.valueOf(this.startDate.getMonth() + 1);
				String day = String.valueOf(this.startDate.getDate());
				
				sqlStr += " P.projectSubmitDate >= '" + year + "-" + month + "-" + day + "'";
			}

			// End date constraint
			if (this.endDate != null) {
				if (haveConstraint) { sqlStr += " AND"; }
				else {
					sqlStr += " WHERE";
					haveConstraint = true;
				}
				
				String year = String.valueOf(this.endDate.getYear() + 1900);
				String month = String.valueOf(this.endDate.getMonth() + 1);
				String day = String.valueOf(this.endDate.getDate());
				
				sqlStr += " P.projectSubmitDate <= '" + year + month + day + "'";
			}
			
			sqlStr += " ORDER BY P.projectSubmitDate";
			
			//System.out.println( sqlStr );
			
			stmt = conn.prepareStatement(sqlStr);
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				int projectID = rs.getInt("projectID");
				Project p;
				try { p = ProjectFactory.getProject(projectID); }
				catch (InvalidIDException e) { continue; }
				
				// Make sure this project belongs to one of the specified groups, if not skip it.
				if (this.groups != null && !this.groups.isEmpty()) {
					boolean foundGroup = false;
					String[] tgroups = p.getGroupsArray();
					for (int i = 0; i < tgroups.length; i++) {
						if (this.groups.contains(tgroups[i])) {
							foundGroup = true;
							break;
						}
					}
					if (!foundGroup) { continue; }
				}
				
				// Don't add this project to the list if the Researcher doesn't have access
				if (this.researcher != null && !p.checkReadAccess(this.researcher)) {
					 continue;
				}

				if( !addedProjects.contains( p.getID() ) ) {
					retList.add(p);
					addedProjects.add( p.getID() );
				}
			}

			
			rs.close();
			rs = null;
			
			stmt.close();
			stmt = null;
			
			conn.close();
			conn = null;
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
		
		
		return retList;
	}

	/**
	 * Add a new word to use for searching
	 * @param word The phrase to add
	 */
	public void addSearchToken(String phrase) {
		if (phrase == null) { return; }

		this.searchTokens.add(phrase);
	}
	
	/**
	 * Add a project type to search by.
	 * @param type
	 */
	public void addType(String type) {
		if (type == null) return;
		this.types.add(type);
	}

	/**
	 * Add a YRC group to search by
	 * @param group
	 */
	public void addGroup(String group) {
		if (group == null) return;
		this.groups.add(group);
	}
	
	/** Set the researcher to use as the basis for checking access to the projects
	 *  returned.  If this researcher doesn't have access to a project, it won't be
	 *  in the returned list.
	 * 
	 * @param researcher The researcher
	 */
	public void setResearcher(Researcher researcher) {
		this.researcher = researcher;
	}
	
	/**
	 * Set the earliest submission date for returned projects.
	 * Defaults to the beginning of time.
	 * @param date
	 */
	public void setStartDate(Date date) {
		this.startDate = date;
	}
	
	/**
	 * Set the latest submission date for returned projects.
	 * Defaults to the end of time.
	 * @param date
	 */
	public void setEndDate(Date date) {
		this.endDate = date;
	}

	// The Strings we are using to conduct the search
	private Set<String> searchTokens;
	
	// The project types to include in the results
	private Set types;
	
	// The groups to which the projects belong to include in the results
	private Set groups;
	
	// The researcher requesting the list, or just the researcher who's access privledges are taken into account when making the list
	private Researcher researcher;
	
	// The earliest submission date of included projects (project submitted before this date are not returned)
	private Date startDate;
	
	// The latest submission date of included project (project submitted after this date are not returned)
	private Date endDate;

}
