/* Y2HScreenResult.java
 * Created on Apr 20, 2004
 */
package org.yeastrc.y2h;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.yeastrc.bio.taxonomy.TaxonomyUtils;
import org.yeastrc.data.IData;
import org.yeastrc.data.InvalidIDException;
import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.orf.*;

/**
 * Add one sentence class summary here.
 * Add class description here
 * 
 * @author Michael Riffle <mriffle@alumni.washington.edu>
 * @version 1.0, Apr 20, 2004
 *
 */
public class Y2HScreenResult implements IData {

	/**
	 * Our constructor!
	 */
	public Y2HScreenResult() {

		// Initialize our vars
		this.ID = 0;
		this.screenID = 0;
		this.preyORF = null;
		this.numHits = 0;
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
			String sqlStr = "SELECT * FROM tblY2HScreenResult WHERE resultID = " + this.ID;
			rs = stmt.executeQuery(sqlStr);

			// See if we're updating a row or adding a new row.
			if (this.ID > 0) {

				// Make sure this row is actually in the database.  This shouldn't ever happen.
				if (!rs.next()) {
					throw new InvalidIDException("ID was set in Y2H Screen Result, but not found in database on save()");
				}

				// Make sure the result set is set up w/ current values from this object
				if (this.preyORF == null) { rs.updateNull("preyORF"); }
				else { rs.updateString("preyORF", this.preyORF.getSystematicName()); }

				if (this.screenID == 0) { rs.updateNull("screenID"); }
				else { rs.updateInt("screenID", this.screenID); }

				if (this.numHits == 0) { rs.updateNull("numHits"); }
				else { rs.updateInt("numHits", this.numHits); }

				// Update the row
				rs.updateRow();

			} else {
				// We're adding a new row.

				rs.moveToInsertRow();

				if (this.preyORF == null) { rs.updateNull("preyORF"); }
				else { rs.updateString("preyORF", this.preyORF.getSystematicName()); }

				if (this.screenID == 0) { rs.updateNull("screenID"); }
				else { rs.updateInt("screenID", this.screenID); }

				if (this.numHits == 0) { rs.updateNull("numHits"); }
				else { rs.updateInt("numHits", this.numHits); }

				rs.insertRow();
				
				// Get the ID generated for this item from the database, and set ID
				rs.last();
				this.ID = rs.getInt("screenID");
			}

			rs.close(); rs = null;
			stmt.close(); stmt = null;
			conn.close(); conn = null;
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
	public void load(int id) throws InvalidIDException, SQLException, Exception {

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		Statement stmt = null;
		ResultSet rs = null;

		try{
			stmt = conn.createStatement();

			// Our SQL statement
			String sqlStr = "SELECT * FROM tblY2HScreenResult WHERE resultID = " + id;

			// Our results
			rs = stmt.executeQuery(sqlStr);

			// No rows returned.
			if( !rs.next() ) {
				throw new InvalidIDException("Loading Y2H Screen Result failed due to invalid ID.");
			}

			// Populate the object from this row.
			this.ID = rs.getInt("resultID");
			this.screenID = rs.getInt("screenID");
			this.numHits = rs.getInt("numHits");
			
			ORFFactory of = ORFFactory.getInstance();
			this.preyORF = of.getORF(rs.getString("preyORF"), TaxonomyUtils.SACCHAROMYCES_CEREVISIAE);
			
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
			String sqlStr = "SELECT resultID FROM tblY2HScreenResult WHERE resultID = " + this.ID;

			// Our results
			rs = stmt.executeQuery(sqlStr);

			// No rows returned.
			if( !rs.next() ) {
				throw new InvalidIDException("Attempted to delete a Y2H Screen Result not found in the database.");
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
	private int screenID;
	private ORF preyORF;
	private int numHits;

	/**
	 * @return
	 */
	public int getID() {
		return ID;
	}

	/**
	 * @return
	 */
	public int getNumHits() {
		return numHits;
	}

	/**
	 * @return
	 */
	public ORF getPreyORF() {
		return preyORF;
	}

	/**
	 * @return
	 */
	public int getScreenID() {
		return screenID;
	}

	/**
	 * @param i
	 */
	public void setID(int i) {
		ID = i;
	}

	/**
	 * @param i
	 */
	public void setNumHits(int i) {
		numHits = i;
	}

	/**
	 * Increment the number of hits by one (system won't increment past 2 hits)
	 */
	public void addHit() {
		if (this.numHits >= 2)
			return;
			
		this.numHits++;
	}

	/**
	 * @param string
	 */
	public void setPreyORF(ORF orf) {
		preyORF = orf;
	}

	/**
	 * @param i
	 */
	public void setScreenID(int i) {
		screenID = i;
	}

}
