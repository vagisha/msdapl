/*
 * Training.java
 *
 * Created November 19, 2003
 *
 * Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.project;

import java.util.*;
import java.sql.*;

import org.yeastrc.data.*;
import org.yeastrc.db.*;

import org.apache.commons.lang.StringUtils;


/**
 * This implementation of a Project is for a Training project.
 *
 * @version 2003-11-21
 * @author Michael Riffle <mriffle@u.washington.edu>
 */
public class Training extends Project {	

	/**
	 * Instantiate a new project.
	 */
	public Training() {		
		this.groups = null;
		this.hours = 0;
		this.days = 0;
		this.description = null;
	}


	/**
	 * Use this method to save this object's data to the database.
	 * These objects should generally represent a single row of data in a table.
	 * This will update the row if this object represents a current row of data, or it will
	 * insert a new row if this data is not in the database (that is, if there is no ID in the object).
	 * @throws InvalidIDException If there is an ID set in this object, but it is not in the database.
	 * @throws SQLException If there is a problem interracting with the database
	 */
	public void save() throws InvalidIDException, SQLException {
		// Whether or not this is a new project being added to the database
		boolean newProject;

		if (super.id == 0) { newProject = true; }
		else { newProject = false; }

		// Call save in the Project class first.  This will save general project data.
		super.save();

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			// Get our updatable result set
			String sqlStr = "SELECT * FROM tblTraining WHERE projectID = " + super.id;
			rs = stmt.executeQuery(sqlStr);

			// See if we're updating a row or adding a new row.
			if (newProject == false) {

				// Make sure this row is actually in the database.  This shouldn't ever happen.
				if (!rs.next()) {
					throw new InvalidIDException("ID was set in Training, but not found in database on save()");
				}

				// Make sure the result set is set up w/ current values from this object
				if (this.groups == null) { rs.updateNull("trainingGroups"); }
				else { rs.updateString("trainingGroups", StringUtils.join(this.groups.toArray(), ",")); }
				
				if (this.hours == 0) { rs.updateNull("trainingHours"); }
				else { rs.updateInt("trainingHours", this.hours); }

				if (this.days == 0) { rs.updateNull("trainingDays"); }
				else { rs.updateInt("trainingDays", this.days); }

				if (this.description == null) { rs.updateNull("trainingDescription"); }
				else { rs.updateString("trainingDescription", this.description); }
				

				// Update the row
				rs.updateRow();

			} else {
				// We're adding a new row.

				rs.moveToInsertRow();

				rs.updateInt("projectID", super.id);

				if (this.groups == null) { rs.updateNull("trainingGroups"); }
				else { rs.updateString("trainingGroups", StringUtils.join(this.groups.toArray(), ",")); }

				if (this.hours == 0) { rs.updateNull("trainingHours"); }
				else { rs.updateInt("trainingHours", this.hours); }

				if (this.hours == 0) { rs.updateNull("trainingDays"); }
				else { rs.updateInt("trainingDays", this.days); }

				if (this.description == null) { rs.updateNull("trainingDescription"); }
				else { rs.updateString("trainingDescription", this.description); }

				rs.insertRow();
			}

			rs.close();
			rs = null;
			
			stmt.close();
			stmt = null;
			
			conn.close();
			conn = null;
		}
		catch(SQLException e) { throw e; }
		catch(InvalidIDException e) { throw e; }
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


	/**
	 * Use this method to populate this object with data from the datbase.
	 * @param id The experiment ID to load.
	 * @throws InvalidIDException If this ID is not valid (or not found)
	 * @throws SQLException If there is a problem interracting with the database
	 */
	public void load(int id) throws InvalidIDException, SQLException {
		// Load the Project data first
		super.load(id);

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		Statement stmt = null;
		ResultSet rs = null;

		try{
			stmt = conn.createStatement();

			// Our SQL statement
			String sqlStr = "SELECT * FROM tblTraining WHERE projectID = " + id;

			// Our results
			rs = stmt.executeQuery(sqlStr);

			// No rows returned.
			if( !rs.next() ) {
				throw new InvalidIDException("Loading Training Project failed due to invalid Project ID(" + String.valueOf(id) + ")");
			}

			// Populate the object from this row.
			this.hours = rs.getInt("trainingHours");
			this.days = rs.getInt("trainingDays");
			this.description = rs.getString("trainingDescription");

			String tmpStr;
			tmpStr = rs.getString("trainingGroups");
			if(tmpStr == null || tmpStr.equals("")) {
				this.groups = null;
			} else {
				this.groups = new HashSet();
				Object[] types = StringUtils.split(tmpStr, ",");
				for (int i = 0; i < types.length; i++) {
					this.groups.add(types[i]);
				}
			}
			
			rs.close();
			rs = null;
			
			stmt.close();
			stmt = null;
			
			conn.close();
			conn = null;
		}
		catch(SQLException e) { throw e; }
		catch(InvalidIDException e) { throw e; }
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


	/**
	 * Use this method to delete the data underlying this object from the database.
	 * Doing so will delete the row from the table corresponding to this object, and
	 * will remove the ID value from the object (since it represents the primary key)
	 * in the database.  This will cause subsequent calls to save() on the object to
	 * insert a new row into the database and generate a new ID.
	 * This will also call delete() on instantiated IData objects for all rows in the
	 * database which are dependent on this row.  For example, calling delete() on a
	 * MS Run objects would call delete() on all Run Result objects, which would then
	 * call delete() on all dependent Peptide objects for those results.
	 * Pre: object is populated with a valid ID.
	 * @throws SQLException if there is a problem working with the database.
	 * @throws InvalidIDException if the ID isn't set in this object, or if the ID isn't
	 * valid (that is, not found in the database).
	 */
	public void delete() throws InvalidIDException, SQLException {

		// Delete the general project entry first
		super.delete();

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			// Our SQL statement
			String sqlStr = "SELECT projectID FROM tblTraining WHERE projectID = " + super.id;

			// Our results
			rs = stmt.executeQuery(sqlStr);

			// No rows returned.
			if( !rs.next() ) {
				throw new InvalidIDException("Attempted to delete a Training Project not found in the database.");
			}

			// Delete the result row.
			rs.deleteRow();		

			rs.close();
			rs = null;
			
			stmt.close();
			stmt = null;
			
			conn.close();
			conn = null;
			
			// re-initialize the id
			super.id = 0;
		}
		catch(SQLException e) { throw e; }
		catch(InvalidIDException e) { throw e; }
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


	// SET METHODS
	
	/**
	 * Set a group to which this project belongs.
	 * This will add to a set of groups, so each consecutive call adds another group
	 * to the Set.  All groups passed in here should be present in Projects.GROUPS array.
	 * Ideally one would use the static variables in Projects for setting this in the form of:
	 * setGroup(Projects.YATES)
	 * @param group The group to add for this project.
	 * @throws InvalidIDException if group is an invalid group as defined by Projects.GROUPS
	 */
	public void setGroup(String group) throws InvalidIDException {
		if (group == null) { return; }

		if (Arrays.binarySearch(Projects.GROUPS, group) < 0) {
			throw new InvalidIDException("Attempt to insert an invalid group.");
		}

		if (this.groups == null) { this.groups = new HashSet(); }
	
		this.groups.add(group);
	}
	
	/**
	 * Removes a group from the set of groups to which this project belongs.  If the group
	 * isn't in the set, nothing happens.
	 * @param group The group to remove for this project.
	 */
	public void removeGroup(String group) {
		if (this.groups == null) { return; }
		if (group == null) { return; }
		this.groups.remove(group);
		
		// If the groups set is now empty, just set it to null
		if (this.groups.isEmpty()) { this.groups = null; }
	}

	/**
	 * Removes all the groups from the set of groups to which this project belongs.  Use with caution!
	 */
	public void clearGroups() {
		this.groups = null;
	}

	/**
	 * Set the number of hours for this training Project.
	 * @param hours The number of hours.
	 */
	public void setHours(int hours) { this.hours = hours; }
	
	/**
	 * Set the number of days for this training Project.
	 * @param days The number of days.
	 */
	public void setDays(int days) { this.days = days; }
	
	/**
	 * Set the description of the training for this training Project
	 * Setting it to null IS valid, it will null out the field in the database.
	 * @param desc The description.
	 */
	public void setDescription(String desc) { this.description = desc; }



	// GET METHODS
	/**
	 * Will return a Set of group names, to which this project belongs.  All groups names will be contained
	 * in the GROUPS array in the Projects class.
	 * @return A set of groups in the YRC, with which this project is affiliated.  Returns null if there are no groups.
	 */
	public Set getGroups() { return this.groups; }
	
	/**
	 * Will return the unabbreviated name for the type of the project.  At the time of this writing, 
	 * it will be one of the following:
	 * "Collaboration", "Technology", "Training" or "Dissemination"
	 * @return The unabbreviated name for the type of the project.
	 */
	public String getLongType() { return "Training"; }
	
	/**
	 * Will return the abbreviated name for the type of the project.  At the time of this writing,
	 * it will be one of the following: "C", "T", "Tech", "D"
	 * @return The abbreviated name for the type of the project.
	 */
	public String getShortType() { return "T"; }
	
	/**
	 * Get the number of days of training.
	 * @return the number of days of training.
	 */
	public int getDays() { return this.days; }
	
	/**
	 * Get the number of hours of training.
	 * @return the number of hours of training.
	 */
	public int getHours() { return this.hours; }
	
	/**
	 * Get the description of the training.
	 * @return the description of the training.  Returns null if no description has been created.
	 */
	public String getDescription() { return this.description; }




	// INSTANCE VARIABLES
	
	// The set of groups to which this project belongs
	private Set groups;
	private int hours;
	private int days;
	private String description;

}