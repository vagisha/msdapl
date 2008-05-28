/* Y2HScreen.java
 * Created on Apr 20, 2004
 */
package org.yeastrc.y2h;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.yeastrc.data.IData;
import org.yeastrc.data.InvalidIDException;
import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;

/**
 * Add one sentence class summary here.
 * Add class description here
 * 
 * @author Michael Riffle <mriffle@alumni.washington.edu>
 * @version 1.0, Apr 20, 2004
 *
 */
public class Y2HScreen implements IData {

	/**
	 * Our constructor!
	 */
	public Y2HScreen() {

		// Initialize our vars
		this.ID = 0;
		this.bait = null;
		this.comments = null;
		this.screenDate = null;
		this.uploadDate = null;
		this.projectID = 0;
	}

	/**
	 * Get all of the Y2HScreenResult objects corresponding to this Screen
	 * @return A list of Y2HScreenResult objects corresponding to this Screen
	 */
	public List getResults() throws SQLException, Exception {
		return Y2HUtils.getScreenResults(this.ID);
	}

	/**
	 * Use this method to save this object's data to the database.
	 * These objects should generally represent a single row of data in a table.
	 * This will update the row if this object represents a current row of data, or it will
	 * insert a new row if this data is not in the database (that is, if there is no ID in the object).
	 * @throws InvalidIDException If there is an ID set in this object, but it is not in the database.
	 * @throws SQLException If there is a problem interracting with the database
	 */
	public void save() throws InvalidIDException, SQLException, Exception {

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			// Get our updatable result set
			String sqlStr = "SELECT * FROM tblY2HScreen WHERE screenID = " + this.ID;
			rs = stmt.executeQuery(sqlStr);

			// See if we're updating a row or adding a new row.
			if (this.ID > 0) {

				// Make sure this row is actually in the database.  This shouldn't ever happen.
				if (!rs.next()) {
					throw new InvalidIDException("ID was set in Y2H Screen, but not found in database on save()");
				}

				// Make sure the result set is set up w/ current values from this object
				if (this.bait == null) {
					throw new Exception("Have no bait for screen on save().");
				} else {
					// Save the bait as well
					Y2HBaitUtils.getInstance().saveBait(this.bait);
					rs.updateInt("baitID", this.bait.getId());
				}

				if (this.comments == null) { rs.updateNull("screenComments"); }
				else { rs.updateString("screenComments", this.comments); }
				
				if (this.screenDate == null) { rs.updateNull("screenDate"); }
				else { rs.updateDate("screenDate", this.screenDate); }
				
				if (this.uploadDate == null) { rs.updateNull("uploadDate"); }
				else { rs.updateDate("uploadDate", this.uploadDate); }

				rs.updateInt("projectID", this.projectID);

				// Update the row
				rs.updateRow();

			} else {
				// We're adding a new row.

				rs.moveToInsertRow();

				// Make sure the result set is set up w/ current values from this object
				if (this.bait == null) {
					throw new Exception("Have no bait for screen on save().");
				} else {
					// Save the bait as well
					Y2HBaitUtils.getInstance().saveBait(this.bait);
					rs.updateInt("baitID", this.bait.getId());
				}

				if (this.comments == null) { rs.updateNull("screenComments"); }
				else { rs.updateString("screenComments", this.comments); }
				
				if (this.screenDate == null) { rs.updateNull("screenDate"); }
				else { rs.updateDate("screenDate", this.screenDate); }	

				if (this.uploadDate == null) { rs.updateNull("uploadDate"); }
				else { rs.updateDate("uploadDate", this.uploadDate); }

				rs.updateInt("projectID", this.projectID);

				rs.insertRow();
				
				// Get the ID generated for this item from the database, and set ID
				rs.last();
				this.ID = rs.getInt("screenID");
			}

			rs.close(); rs = null;
			stmt.close(); stmt = null;
			conn.close(); conn = null;
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


	/**
	 * Use this method to populate this object with data from the datbase.
	 * @param id The experiment ID to load.
	 * @throws InvalidIDException If this ID is not valid (or not found)
	 * @throws SQLException If there is a problem interracting with the database
	 */
	public void load(int id) throws InvalidIDException, SQLException, Exception {

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		Statement stmt = null;
		ResultSet rs = null;

		try{
			stmt = conn.createStatement();

			// Our SQL statement
			String sqlStr = "SELECT * FROM tblY2HScreen WHERE screenID = " + id;

			// Our results
			rs = stmt.executeQuery(sqlStr);

			// No rows returned.
			if( !rs.next() ) {
				throw new InvalidIDException("Loading Y2H Screen failed due to invalid ID.");
			}

			// Populate the object from this row.
			this.ID = rs.getInt("screenID");
			this.bait = Y2HBaitUtils.getInstance().loadBait(rs.getInt("baitID"));
			this.screenDate = rs.getDate("screenDate");
			this.comments = rs.getString("screenComments");
			this.uploadDate = rs.getDate("uploadDate");
			this.projectID = rs.getInt("projectID");
			
			rs.close(); rs = null;
			stmt.close(); stmt = null;
			conn.close(); conn = null;
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

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			// Our SQL statement
			String sqlStr = "SELECT screenID FROM tblY2HScreen WHERE screenID = " + this.ID;

			// Our results
			rs = stmt.executeQuery(sqlStr);

			// No rows returned.
			if( !rs.next() ) {
				throw new InvalidIDException("Attempted to delete a Y2H Screen not found in the database.");
			}

			// Delete the result row.
			rs.deleteRow();		

			rs.close(); rs = null;
			stmt.close(); stmt = null;
			conn.close(); conn = null;
			
			// re-initialize the id
			this.ID = 0;
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
		
		// Delete all the results in the results table associated with this screen
		
	}



	// Instance vars
	private int ID;
	private Y2HBait bait;
	private java.sql.Date screenDate;
	private String comments;
	private int projectID;
	private java.sql.Date uploadDate;
	private Project project;

	/**
	 * @return
	 */
	public Y2HBait getBait() {
		return bait;
	}

	/**
	 * @return
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @return
	 */
	public int getID() {
		return ID;
	}

	/**
	 * @return
	 */
	public java.util.Date getScreenDate() {
		return screenDate;
	}

	/**
	 * @param string
	 */
	public void setBait(Y2HBait tbait) {
		this.bait = tbait;
	}

	/**
	 * @param string
	 */
	public void setComments(String string) {
		comments = string;
	}

	/**
	 * @param i
	 */
	public void setID(int i) {
		ID = i;
	}

	/**
	 * @param date
	 */
	public void setScreenDate(java.util.Date date) {
		screenDate = new java.sql.Date(date.getTime());
	}

	/**
	 * @param date
	 */
	public void setUploadDate(java.util.Date date) {
		uploadDate = new java.sql.Date(date.getTime());
	}

	/**
	 * @return
	 */
	public int getProjectID() {
		return projectID;
	}

	/**
	 * @return
	 */
	public java.util.Date getUploadDate() {
		return uploadDate;
	}

	/**
	 * Returned the populated Project object to which this Screen belongs
	 * @return The project object, if found.  null if not found
	 */
	public Project getProject() {
		// no project is set, kick back null
		if (this.projectID == 0) return null;

		// returned the cached project object, if it's correct
		if (this.project != null && this.project.getID() == this.projectID)
			return this.project;
		
		// try to get the project, cache it and return it
		try {
			this.project = ProjectFactory.getProject(this.projectID);
			return this.project;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @param i
	 */
	public void setProjectID(int i) {
		projectID = i;
	}

}
