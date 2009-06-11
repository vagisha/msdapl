/**
 * GONRProteinCounter.java
 * Created by: Michael Riffle <mriffle@u.washington.edu>
 * Created on: Mar 29, 2007 at 12:51:39 PM
 */

package org.yeastrc.www.go;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.yeastrc.bio.go.GONode;
import org.yeastrc.bio.go.GOUtils;
import org.yeastrc.db.DBConnectionManager;

/**
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Mar 29, 2007
 *
 * Class definition goes here
 */
public class GOProteinCounter {

	// private constructor
	private GOProteinCounter()  { }

	/**
	 * Get an instance of this class
	 * @return
	 */
	public static GOProteinCounter getInstance() {
		return new GOProteinCounter();
	}
	
	/**
	 * Return the number of proteins in the YRC NR_SEQ database that are annotated with this GO term
	 * @param node The GO term we're testing
	 * @param exact If true, the number only includes proteins annotated with this exact term
	 * 				<BR>If false, it includes proteins annotated with this term or any of its children
	 * @return The number of proteins annotated with the given GO term
	 * @throws Exception
	 */
	public int countProteins( GONode node, boolean exact ) throws Exception {
		int count = 0;
		
		// Get our connection to the database.
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = DBConnectionManager.getConnection("go");
			
			String sql = "SELECT COUNT(*) FROM GOProteinLookup WHERE termID = ?";
			if (exact)
				sql += " AND exact = 1";
			
			stmt = conn.prepareStatement( sql );
			stmt.setInt( 1, node.getId() );
			rs = stmt.executeQuery();
			
			if (rs.next())
				count = rs.getInt( 1 );
			
			rs.close(); rs = null;
			stmt.close(); stmt = null;
			conn.close(); conn = null;
		} finally {

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
		
		return count;
	}
	
	/**
     * Return the number of proteins in the YRC NR_SEQ database that are annotated with this GO term
     * @param node The GO term we're testing
     * @param exact If true, the number only includes proteins annotated with this exact term
     *              <BR>If false, it includes proteins annotated with this term or any of its children
     * @param speciesID proteins with this speciesID are counted
     * @return The number of proteins annotated with the given GO term
     * @throws Exception
     */
    public int countProteins( GONode node, boolean exact, int speciesID ) throws Exception {
        int count = 0;
        
        if(speciesID == 4932)
            return countYeastProteins(node, exact, speciesID);
        
        // Get our connection to the database.
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionManager.getConnection("go");
            
            String sql = "SELECT COUNT(DISTINCT(proteinID)) FROM GOProteinLookup AS goprot, YRC_NRSEQ.tblProtein AS prot ";
            sql+= "WHERE goprot.termID = ? AND prot.speciesID = ? AND goprot.proteinID = prot.id";
            if (exact)
                sql += " AND goprot.exact = 1";
            
            stmt = conn.prepareStatement( sql );
            stmt.setInt( 1, node.getId() );
            stmt.setInt( 2, speciesID );
            rs = stmt.executeQuery();
            
            if (rs.next())
                count = rs.getInt( 1 );
            
            rs.close(); rs = null;
            stmt.close(); stmt = null;
            conn.close(); conn = null;
        } finally {

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
        
        return count;
    }
    
    private int countYeastProteins(GONode node, boolean exact, int speciesId) throws Exception {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionManager.getConnection("go");
            String sql = "SELECT proteinCount FROM YRC_NRSEQ_GO_COUNTS WHERE goAcc = ?";
            stmt = conn.prepareStatement( sql );
            stmt.setString( 1, node.getAccession() );
            rs = stmt.executeQuery();

            if (rs.next())
               return rs.getInt( 1 );

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
        return 0;
    }
	
    /**
     * Returns the number of proteins, for a given species, in the YRC NR_SEQ database that
     * have GO BIOLOGICAL PROCESS annotations
     * @param speciesID species we are interested in.
     * @return The number of proteins annotated with the given GO term
     * @throws Exception
     */
    public int countAllBiologicalProcessProteins( int speciesID) throws Exception {
        return countAllProteins(speciesID, GOUtils.BIOLOGICAL_PROCESS);
    }
    
    /**
     * Returns the number of proteins, for a given species, in the YRC NR_SEQ database that
     * have GO CELLULAR COMPONENT annotations
     * @param speciesID species we are interested in.
     * @return The number of proteins annotated with the given GO term
     * @throws Exception
     */
    public int countAllCellularComponentProteins( int speciesID) throws Exception {
        return countAllProteins(speciesID, GOUtils.CELLULAR_COMPONENT);
    }
    
    /**
     * Returns the number of proteins, for a given species, in the YRC NR_SEQ database that
     * have GO MOLECULAR FUNCTION annotations
     * @param speciesID species we are interested in.
     * @return The number of proteins annotated with the given GO term
     * @throws Exception
     */
    public int countAllMolecularFunctionProteins( int speciesID) throws Exception {
        return countAllProteins(speciesID, GOUtils.MOLECULAR_FUNCTION);
    }
    
    private int countAllProteins(int speciesID, int aspect) throws Exception {
        GONode rootNode = GOUtils.getAspectRootNode(aspect);
        return countProteins(rootNode, false, speciesID);
    }
}
