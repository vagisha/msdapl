/* Publication.java
 * Created on Jun 9, 2004
 */
package org.yeastrc.progress;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.yeastrc.data.IData;
import org.yeastrc.data.InvalidIDException;
import org.yeastrc.db.DBConnectionManager;

/**
 * Add one sentence class summary here.
 * Add class description here
 * 
 * @author Michael Riffle <mriffle@alumni.washington.edu>
 * @version 1.0, Jun 9, 2004
 *
 */
public class Publication implements IData {

	/**
	 * Use this method to save this object's data to the database.
	 * These objects should generally represent a single row of data in a table.
	 * This will update the row if this object represents a current row of data, or it will
	 * insert a new row if this data is not in the database (that is, if there is no ID in the object).
	 * @throws InvalidIDException If there is an ID set in this object, but it is not in the database.
	 * @throws SQLException If there is a problem interracting with the database
	 */
	public void save() throws InvalidIDException, SQLException {

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			// Get our updatable result set
			String sqlStr = "SELECT * FROM tblPublications WHERE id = " + this.id;
			rs = stmt.executeQuery(sqlStr);

			// See if we're updating a row or adding a new row.
			if (this.id > 0) {

				// Make sure this row is actually in the database.  This shouldn't ever happen.
				if (!rs.next()) {
					throw new InvalidIDException("ID was set in Publication, but not found in database on save()");
				}

				// Make sure the result set is set up w/ current values from this object
				if (this.citation == null) { rs.updateNull("citation"); }
				else { rs.updateString("citation", this.citation); }

				if (this.projectID == 0) { rs.updateNull("projectID"); }
				else { rs.updateInt("projectID", this.projectID); }

				if (this.reportYear == 0) { rs.updateNull("reportYear"); }
				else { rs.updateInt("reportYear", this.reportYear); }

				if (this.pubYear == 0) { rs.updateNull("pubYear"); }
				else { rs.updateInt("pubYear", this.pubYear); }

				if (this.pubMedID == 0) { rs.updateNull("pubMedID"); }
				else { rs.updateInt("pubMedID", this.pubMedID); }

				if (this.yrcAcknowledged == null) { rs.updateNull("yrcAcknowledged"); }
				else { rs.updateString("yrcAcknowledged", this.yrcAcknowledged); }

				if (this.type == null) { rs.updateNull("type"); }
				else { rs.updateString("type", this.type); }
				
				if (this.inPress == null) { rs.updateNull("inPress"); }
				else { rs.updateString("inPress", this.inPress); }

				// Update the row
				rs.updateRow();

			} else {
				// We're adding a new row.

				rs.moveToInsertRow();

				if (this.citation == null) { rs.updateNull("citation"); }
				else { rs.updateString("citation", this.citation); }

				if (this.projectID == 0) { rs.updateNull("projectID"); }
				else { rs.updateInt("projectID", this.projectID); }

				if (this.reportYear == 0) { rs.updateNull("reportYear"); }
				else { rs.updateInt("reportYear", this.reportYear); }

				if (this.pubYear == 0) { rs.updateNull("pubYear"); }
				else { rs.updateInt("pubYear", this.pubYear); }

				if (this.pubMedID == 0) { rs.updateNull("pubMedID"); }
				else { rs.updateInt("pubMedID", this.pubMedID); }

				if (this.yrcAcknowledged == null) { rs.updateNull("yrcAcknowledged"); }
				else { rs.updateString("yrcAcknowledged", this.yrcAcknowledged); }

				if (this.type == null) { rs.updateNull("type"); }
				else { rs.updateString("type", this.type); }
				
				if (this.citation == null) { rs.updateNull("inPress"); }
				else { rs.updateString("inPress", this.inPress); }

				rs.insertRow();

				// Get the ID generated for this item from the database, and set expID
				rs.last();
				this.id = rs.getInt("id");

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
	}

	/**
	 * Use this method to populate this object with data from the datbase.
	 * @param id The experiment ID to load.
	 * @throws InvalidIDException If this ID is not valid (or not found)
	 * @throws SQLException If there is a problem interracting with the database
	 */
	public void load(int id) throws InvalidIDException, SQLException {

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		Statement stmt = null;
		ResultSet rs = null;

		try{
			stmt = conn.createStatement();

			// Our SQL statement
			String sqlStr = "SELECT * FROM tblPublications WHERE id = " + id;

			// Our results
			rs = stmt.executeQuery(sqlStr);

			// No rows returned.
			if( !rs.next() ) {
				throw new InvalidIDException("ID not found for this Publication.");
			}

			// Populate the object from this row.
			this.id = id;
			this.pubYear = rs.getInt("pubYear");
			this.pubMedID = rs.getInt("pubMedID");
			this.citation = rs.getString("citation");
			this.yrcAcknowledged = rs.getString("yrcAcknowledged");
			this.projectID = rs.getInt("projectID");
			this.reportYear = rs.getInt("reportYear");
			this.type = rs.getString("type");
			this.inPress = rs.getString("inPress");
			
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
			String sqlStr = "SELECT id FROM tblPublications WHERE id = " + this.id;

			// Our results
			rs = stmt.executeQuery(sqlStr);

			// No rows returned.
			if( !rs.next() ) {
				throw new InvalidIDException("ID not found for this Publication.");
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
			this.id = 0;

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


	private int id;
	private int pubYear;
	private int pubMedID;
	private String citation;
	private String yrcAcknowledged;
	private int projectID;
	private int reportYear;
	private String type;
	private String inPress;

	/**
	 * @return
	 */
	public String getCitation() {
		return citation;
	}

	/**
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return
	 */
	public String getInPress() {
		return inPress;
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
	public int getPubMedID() {
		return pubMedID;
	}

	/**
	 * @return
	 */
	public int getPubYear() {
		return pubYear;
	}

	/**
	 * @return
	 */
	public int getReportYear() {
		return reportYear;
	}

	/**
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return
	 */
	public String getYrcAcknowledged() {
		return yrcAcknowledged;
	}

	/**
	 * @param string
	 */
	public void setCitation(String string) {
		citation = string;
	}

	/**
	 * @param i
	 */
	public void setId(int i) {
		id = i;
	}

	/**
	 * @param string
	 */
	public void setInPress(String string) {
		inPress = string;
	}

	/**
	 * @param i
	 */
	public void setProjectID(int i) {
		projectID = i;
	}

	/**
	 * @param i
	 */
	public void setPubMedID(int i) {
		pubMedID = i;
	}

	/**
	 * @param i
	 */
	public void setPubYear(int i) {
		pubYear = i;
	}

	/**
	 * @param i
	 */
	public void setReportYear(int i) {
		reportYear = i;
	}

	/**
	 * @param string
	 */
	public void setType(String string) {
		type = string;
	}

	/**
	 * @param string
	 */
	public void setYrcAcknowledged(String string) {
		yrcAcknowledged = string;
	}

}
