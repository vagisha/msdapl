/*
 * Dissemination.java
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


/**
 * This implementation of a Project is for a Dissemination project.
 *
 * @version 2003-11-21
 * @author Michael Riffle <mriffle@u.washington.edu>
 */
public class Dissemination extends Project {	

	/**
	 * Instantiate a new project.
	 */
	public Dissemination() {		
		this.type = null;
		this.name = null;
		this.phone = null;
		this.email = null;
		this.address = null;
		this.description = null;
		this.FEDEX = null;
		this.comments = null;
		this.commercial = false;
		this.shipped = false;
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
			String sqlStr = "SELECT * FROM tblDissemination WHERE projectID = " + super.id;
			rs = stmt.executeQuery(sqlStr);

			// See if we're updating a row or adding a new row.
			if (newProject == false) {

				// Make sure this row is actually in the database.  This shouldn't ever happen.
				if (!rs.next()) {
					throw new InvalidIDException("ID was set in Dissemination, but not found in database on save()");
				}

				// Make sure the result set is set up w/ current values from this object

				if (this.type == null) { rs.updateNull("shipPlasmidType"); }
				else { rs.updateString("shipPlasmidType", this.type); }

				if (this.name == null) { rs.updateNull("shipName"); }
				else { rs.updateString("shipName", this.name); }

				if (this.phone == null) { rs.updateNull("shipPhone"); }
				else { rs.updateString("shipPhone", this.phone); }

				if (this.email == null) { rs.updateNull("shipEmail"); }
				else { rs.updateString("shipEmail", this.email); }

				if (this.address == null) { rs.updateNull("shipAddress"); }
				else { rs.updateString("shipAddress", this.address); }

				if (this.description == null) { rs.updateNull("shipDescription"); }
				else { rs.updateString("shipDescription", this.description); }

				if (this.FEDEX == null) { rs.updateNull("shipFEDEX"); }
				else { rs.updateString("shipFEDEX", this.FEDEX); }

				if (this.comments == null) { rs.updateNull("shipComments"); }
				else { rs.updateString("shipComments", this.comments); }

				if (this.commercial) { rs.updateString("shipCommercial", "T"); }
				else { rs.updateString("shipCommercial", "F"); }

				if (this.shipped) { rs.updateString("shipShipped", "T"); }
				else { rs.updateString("shipShipped", "F"); }

				// Update the row
				rs.updateRow();

			} else {
				// We're adding a new row.

				rs.moveToInsertRow();

				rs.updateInt("projectID", super.id);

				if (this.type == null) { rs.updateNull("shipPlasmidType"); }
				else { rs.updateString("shipPlasmidType", this.type); }

				if (this.name == null) { rs.updateNull("shipName"); }
				else { rs.updateString("shipName", this.name); }

				if (this.phone == null) { rs.updateNull("shipPhone"); }
				else { rs.updateString("shipPhone", this.phone); }

				if (this.email == null) { rs.updateNull("shipEmail"); }
				else { rs.updateString("shipEmail", this.email); }

				if (this.address == null) { rs.updateNull("shipAddress"); }
				else { rs.updateString("shipAddress", this.address); }

				if (this.description == null) { rs.updateNull("shipDescription"); }
				else { rs.updateString("shipDescription", this.description); }

				if (this.FEDEX == null) { rs.updateNull("shipFEDEX"); }
				else { rs.updateString("shipFEDEX", this.FEDEX); }

				if (this.comments == null) { rs.updateNull("shipComments"); }
				else { rs.updateString("shipComments", this.comments); }

				if (this.commercial) { rs.updateString("shipCommercial", "T"); }
				else { rs.updateString("shipCommercial", "F"); }

				if (this.shipped) { rs.updateString("shipShipped", "T"); }
				else { rs.updateString("shipShipped", "F"); }

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
			String sqlStr = "SELECT * FROM tblDissemination WHERE projectID = " + id;

			// Our results
			rs = stmt.executeQuery(sqlStr);

			// No rows returned.
			if( !rs.next() ) {
				throw new InvalidIDException("Loading Dissemination Project failed due to invalid Project ID.");
			}

			// Populate the object from this row.
			this.type = rs.getString("shipPlasmidType");
			this.name = rs.getString("shipName");
			this.phone = rs.getString("shipPhone");
			this.email = rs.getString("shipEmail");
			this.address = rs.getString("shipAddress");
			this.description = rs.getString("shipDescription");
			this.FEDEX = rs.getString("shipFEDEX");
			this.comments = rs.getString("shipComments");

			String tmpStr = rs.getString("shipCommercial");
			if (tmpStr == null) { this.commercial = false; }
			else if (tmpStr.equals("T") || tmpStr.equals("t") || tmpStr.equals("Y") || tmpStr.equals("y")) {
				this.commercial = true;
			} else {
				this.commercial = false;
			}
			
			tmpStr = rs.getString("shipShipped");
			if (tmpStr == null) {
				this.shipped = false;
			}
			else if (tmpStr.equals("T") || tmpStr.equals("t") || tmpStr.equals("Y") || tmpStr.equals("y")) {
				this.shipped = true;
			} else {
				this.shipped = false;
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
			String sqlStr = "SELECT projectID FROM tblDissemination WHERE projectID = " + super.id;

			// Our results
			rs = stmt.executeQuery(sqlStr);

			// No rows returned.
			if( !rs.next() ) {
				throw new InvalidIDException("Attempted to delete a Dissemination Project not found in the database.");
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
	 * @throws InvalidIDException if group is an invalid group.  Dissemination projects must belong
	 * to either the Projects.MICROSCOPY and/or Projects.TWOHYBRID groups.
	 */
	public void setGroup(String group) throws InvalidIDException {
		if (group == null) { throw new InvalidIDException("Null sent in to setGroup in Dissemination"); }
		if (!group.equals(Projects.MICROSCOPY) && !group.equals(Projects.TWOHYBRID)) {
			throw new InvalidIDException("Invalid group for setGroup, must be MICROSCOPY or TWOHYBRID");
		}
		
		if (group.equals(Projects.TWOHYBRID)) {
			if (this.type == null || this.type.equals("")) { this.type = "T"; }
			else if (this.type.equals("M")) { this.type = "MT"; }
		} else {
			if (this.type == null || this.type.equals("")) { this.type = "M"; }
			else if (this.type.equals("T")) { this.type = "MT"; }
		}
	}

	/**
	 * Removes a group from the set of groups to which this project belongs.  If the group
	 * isn't in the set, nothing happens.
	 * @param group The group to remove for this project.
	 */
	public void removeGroup(String group) {
		if (group == null) { return; }
		if (this.type == null) { return; }
		
		if (group.equals(Projects.TWOHYBRID)) {
			if (this.type.equals("MT")) { this.type = "M"; }
			else if (this.type.equals("T")) { this.type = null; }
		} else if (group.equals(Projects.MICROSCOPY)) {
			if (this.type.equals("MT")) { this.type = "T"; }
			else if (this.type.equals("M")) { this.type = null; }
		}
	}
	
	/**
	 * Removes all the groups from the set of groups to which this project belongs.  Use with caution!
	 */
	public void clearGroups() {
		this.type = null;
	}

	
	/**
	 * Set the name of the person to whom the plasmids are being shipped.
	 * @param name The name of the person to whom the plasmids are being shipped.
	 */
	public void setName(String name) { this.name = name; }
	
	/**
	 * Set the phone number of the person to whom the plasmids are being shipped.
	 * @param phone The phone number.
	 */
	public void setPhone(String phone) { this.phone = phone; }
	
	/**
	 * Set the email address of the person to whom the plasmids are being shipped.
	 * @param email The email address.
	 */
	public void setEmail(String email) { this.email = email; }
	
	/**
	 * Set the mailing address of the person to whom the plamids are being shipped.
	 * @param address The mailing address.
	 */
	public void setAddress(String address) { this.address = address; }
	
	/**
	 * Set the description for this Dissemination project
	 * @param desc The description.
	 */
	public void setDescription(String desc) { this.description = desc; }
	
	/**
	 * Set the FEDEX # for this shipment.
	 * @param fedex The FEDEX #.
	 */
	public void setFEDEX(String fedex) { this.FEDEX = fedex; }
	
	/**
	 * Set the comments for this Dissemination project.
	 * @param comments The comments.
	 */
	public void setComments(String comments) { this.comments = comments; }
	
	/**
	 * Set whether or not these plasmids are going to be used for commercial purposes.
	 * @param arg true if they will, false if they won't
	 */
	public void setCommercial(boolean arg) { this.commercial = arg; }
	
	/**
	 * Set whether or not the plasmids have shipped.
	 * @param arg true if they have, false if they haven't
	 */
	public void setShipped(boolean arg) { this.shipped = arg; }


	// GET METHODS
	/**
	 * Will return a Set of group names, to which this project belongs.  All groups names will be contained
	 * in the GROUPS array in the Projects class.
	 * @return A set of groups in the YRC, with which this project is affiliated.  Returns null if there are no groups.
	 */
	public Set getGroups() {
		if (this.type == null) { return null; }
		
		Set returnSet = new HashSet();
		if (this.type.equals("M")) { returnSet.add(Projects.MICROSCOPY); }
		else if (this.type.equals("T")) { returnSet.add(Projects.TWOHYBRID); }
		else if (this.type.equals("MT")) {
			returnSet.add(Projects.MICROSCOPY);
			returnSet.add(Projects.TWOHYBRID);
		} else { return null; }
		
		return returnSet;
	}
	
	/**
	 * Will return the unabbreviated name for the type of the project.  At the time of this writing, 
	 * it will be one of the following:
	 * "Collaboration", "Technology", "Dissemination" or "Dissemination"
	 * @return The unabbreviated name for the type of the project.
	 */
	public String getLongType() { return "Dissemination"; }
	
	/**
	 * Will return the abbreviated name for the type of the project.  At the time of this writing,
	 * it will be one of the following: "C", "T", "Tech", "D"
	 * @return The abbreviated name for the type of the project.
	 */
	public String getShortType() { return "D"; }
	
	/**
	 * Get the Name to whom the plasmids were/are shipped.
	 * @return The name of the person.
	 */
	public String getName() { return this.name; }
	
	/**
	 * Get the phone number of the person to whom the plasmids shipped.
	 * @return The phone number.
	 */
	public String getPhone() { return this.phone; }
	
	/**
	 * Get the email address of the person to whom the plasmids have shipped.
	 * @return The email address.
	 */
	public String getEmail() { return this.email; }
	
	/**
	 * Get the mailing address of the person to whom the plasmids have shipped.
	 * @return The mailing address.
	 */
	public String getAddress() { return this.address; }
	
	/**
	 * Get the mailing address in HTML form.
	 * @return The mailing address in HTML form.
	 */
	public String getAddressAsHTML() {
		return org.yeastrc.utils.HTML.convertToHTML(this.getAddress());
	}

	/**
	 * Get the description of the Dissemination project
	 * @return The description.
	 */
	public String getDescription() { return this.description; }
	
	/**
	 * Get the FEDEX # for the shipment.
	 * @return The FEDEX #.
	 */
	public String getFEDEX() { return this.FEDEX; }
	
	/**
	 * Get the comments.
	 * @return The comments.
	 */
	public String getComments() { return this.comments; }
	
	/**
	 * Get whether or not these plasmids will be used for commercial purposes.
	 * @return true if yes, false if no
	 */
	public boolean getCommercial() { return this.commercial; }
	
	/**
	 * Get whether or not these plasmids have shipped.
	 * @return true if yes, false if no
	 */
	public boolean getShipped() { return this.shipped; }



	// INSTANCE VARIABLES
	
	// The set of groups to which this project belongs
	private String type;
	private String name;
	private String phone;
	private String email;
	private String address;
	private String description;
	private String FEDEX;
	private String comments;
	private boolean commercial;
	private boolean shipped;

}