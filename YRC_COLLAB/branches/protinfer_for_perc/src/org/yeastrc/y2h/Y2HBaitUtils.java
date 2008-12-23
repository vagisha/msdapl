/*
 * Y2HBaitUtils.java
 * Created on Apr 1, 2005
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.y2h;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Set;

import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.nr_seq.NRProtein;
import org.yeastrc.nr_seq.NRProteinFactory;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Apr 1, 2005
 */

public class Y2HBaitUtils {
	
	// Our single instance of this class
	private static final Y2HBaitUtils INSTANCE = new Y2HBaitUtils();

	// Private constructor
	private Y2HBaitUtils() {

	}

	/**
	 * Get the single instance of this class
	 * @return
	 */
	public static Y2HBaitUtils getInstance() {
		return Y2HBaitUtils.INSTANCE;
	}
	
	/**
	 * Save the supplied Y2HBait to the database.
	 * @param bait The bait containing the data we'd like to save
	 * @throws SQLException
	 * @throws Exception
	 */
	public void saveBait(Y2HBait bait) throws SQLException, Exception {

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			// Get our updatable result set
			String sqlStr = "SELECT * FROM tblY2HBait WHERE baitID = " + bait.getId();
			rs = stmt.executeQuery(sqlStr);
			
			// See if we're updating a row or adding a new row.
			if (bait.getId() > 0) {

				// Make sure this row is actually in the database.  This shouldn't ever happen.
				if (!rs.next()) {
					throw new Exception("ID was set in Y2H Bait, but not found in database on save()");
				}

				// Make sure the result set is set up w/ current values from this object
				if (bait.getProtein() == null) { throw new Exception("No protein set in bait on save()."); }
				else { rs.updateInt("proteinID", bait.getProtein().getId()); }

				if (bait.isFullLength()) { rs.updateString("fullLength", "Y"); }
				else { rs.updateString("fullLength", "N"); }

				if (bait.getStartResidue() == 0) { rs.updateNull("startResidue"); }
				else { rs.updateInt("startResidue", bait.getStartResidue()); }

				if (bait.getEndResidue() == 0) { rs.updateNull("endResidue"); }
				else { rs.updateInt("endResidue", bait.getEndResidue()); }

				// Update the row
				rs.updateRow();
			} else {
				// We're adding a new row.

				rs.moveToInsertRow();

				// Make sure the result set is set up w/ current values from this object
				if (bait.getProtein() == null) { throw new Exception("No protein set in bait on save()."); }
				else { rs.updateInt("proteinID", bait.getProtein().getId()); }

				if (bait.isFullLength()) { rs.updateString("fullLength", "Y"); }
				else { rs.updateString("fullLength", "N"); }

				if (bait.getStartResidue() == 0) { rs.updateNull("startResidue"); }
				else { rs.updateInt("startResidue", bait.getStartResidue()); }

				if (bait.getEndResidue() == 0) { rs.updateNull("endResidue"); }
				else { rs.updateInt("endResidue", bait.getEndResidue()); }

				rs.insertRow();
				
				// Get the ID generated for this item from the database, and set ID
				rs.last();
				bait.setId(rs.getInt("baitID"));
			}

			rs.close(); rs = null;
			stmt.close(); stmt = null;
			
			// SAVE ANY MUTATIONS ASSOCIATED WITH THIS BAIT
			sqlStr = "DELETE FROM tblY2HBaitMutation WHERE baitID = " + bait.getId();
			stmt = conn.createStatement();
			stmt.executeUpdate(sqlStr);
			stmt.close(); stmt = null;
			
			Set mutations = bait.getMutations();
			if (mutations != null) {
				Iterator iter = mutations.iterator();
				while (iter.hasNext()) {
					Y2HBaitMutation mut = (Y2HBaitMutation)(iter.next());
					
					sqlStr  = "INSERT INTO tblY2HBaitMutation (baitID, position, origAminoAcid, newAminoAcid) VALUES (" + bait.getId() + ", ";
					sqlStr += mut.getPosition() + ", ";
					sqlStr += "'" + mut.getOrigAminoAcid() + "', ";
					sqlStr += "'" + mut.getNewAminoAcid() + "')";
					
					stmt = conn.createStatement();
					stmt.executeUpdate(sqlStr);
					
					stmt.close(); stmt = null;
				}
				mutations.clear();
				mutations = null;
			}
			
				
			conn.close();conn = null;
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
	 * Attempt to load the supplied baitID from the database.
	 * @param baitID The baitID to attempt to load
	 * @return The populated Y2HBait with the given ID
	 * @throws Exception If there is a problem loading that ID.
	 */
	public Y2HBait loadBait(int baitID) throws Exception {
		Y2HBait retBait = new Y2HBait();
		
		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		Statement stmt = null;
		ResultSet rs = null;

		try{
			stmt = conn.createStatement();

			// Our SQL statement
			String sqlStr = "SELECT * FROM tblY2HBait WHERE baitID = " + baitID;

			// Our results
			rs = stmt.executeQuery(sqlStr);

			// No rows returned.
			if( !rs.next() ) {
				throw new Exception("Loading Y2H Bait failed due to invalid ID.");
			}

			// Populate the object from this row.
			retBait.setId(rs.getInt("baitID"));
			retBait.setProtein( (NRProtein)(NRProteinFactory.getInstance().getProtein( rs.getInt("proteinID")) ) );

			if (rs.getString("fullLength") != null && rs.getString("fullLength").equals("N")) { retBait.setFullLength(false); }
			else { retBait.setFullLength(true); }
			
			retBait.setStartResidue(rs.getInt("startResidue"));
			retBait.setEndResidue(rs.getInt("endResidue"));
			
			rs.close(); rs = null;
			stmt.close(); stmt = null;

			// ATTEMPT TO LOAD ANY BAIT MUTATIONS HERE
			sqlStr = "SELECT * FROM tblY2HBaitMutation WHERE baitID = " + baitID;
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sqlStr);
			while (rs.next()) {
				Y2HBaitMutation mut = new Y2HBaitMutation();
				mut.setId(rs.getInt("mutationID"));
				mut.setBaitID(baitID);
				mut.setPosition(rs.getInt("position"));
				mut.setOrigAminoAcid(rs.getString("origAminoAcid"));
				mut.setNewAminoAcid(rs.getString("newAminoAcid"));
				
				retBait.addMutation(mut);
				mut = null;
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
		
		return retBait;
	}
	
}
