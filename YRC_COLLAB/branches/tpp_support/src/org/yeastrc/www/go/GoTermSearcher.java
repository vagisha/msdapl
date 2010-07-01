/**
 * 
 */
package org.yeastrc.www.go;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.yeastrc.bio.go.GONode;
import org.yeastrc.bio.go.GOUtils;
import org.yeastrc.db.DBConnectionManager;

/**
 * GoTermSearcher.java
 * @author Vagisha Sharma
 * May 27, 2010
 * 
 */
public class GoTermSearcher {

	private GoTermSearcher() {}
	
	public static Set<GONode> getTermsForProtein(int nrseqProteinId, int goAspect) throws SQLException {
		
		// Get our connection to the database.
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		Set<GONode> nodes = new HashSet<GONode>();
		
		try {
			String sqlStr = "SELECT t.name, t.term_type, t.is_obsolete, d.term_definition, t.is_root, t.id, t.acc "+
							"FROM (term AS t, GOProteinLookup AS prot) "+
							"LEFT OUTER JOIN term_definition AS d ON t.id = d.term_id "+
							"WHERE prot.proteinID="+nrseqProteinId+" "+
							"AND prot.termID=t.id";
			
			conn = DBConnectionManager.getConnection("go");	
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(sqlStr);
			
			
			while (rs.next()) {
				
				int aspect = GOUtils.getGOTypeFromDatabase(rs.getString(2));
				if(aspect != goAspect)
					continue;
				
				// We found one
				GONode retNode = new GONode();
				retNode.setAccession(rs.getString(7));
				retNode.setAspect(aspect);
				retNode.setName(rs.getString(1));
				retNode.setDefinition(rs.getString(4));
				retNode.setId(rs.getInt(6));
				
				if (rs.getInt(3) == 0) retNode.setObsolete(false);
				else retNode.setObsolete(true);
				
				if (rs.getInt(5) == 0) retNode.setRoot(false);
				else retNode.setRoot(true);
				
				nodes.add(retNode);
			}
			
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
		
		return nodes;
	}
	
	public static Set<GONodeAnnotation> getAnnotationsForProtein(int nrseqProteinId, int goAspect) throws SQLException {
		
		// Get our connection to the database.
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		Set<GONodeAnnotation> nodes = new HashSet<GONodeAnnotation>();
		
		try {
			String sqlStr = "SELECT t.name, t.term_type, t.is_obsolete, d.term_definition, t.is_root, t.id, t.acc, prot.exact "+
							"FROM (term AS t, GOProteinLookup AS prot) "+
							"LEFT OUTER JOIN term_definition AS d ON t.id = d.term_id "+
							"WHERE prot.proteinID="+nrseqProteinId+" "+
							"AND prot.termID=t.id";
			
			conn = DBConnectionManager.getConnection("go");	
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(sqlStr);
			
			
			while (rs.next()) {
				
				int aspect = GOUtils.getGOTypeFromDatabase(rs.getString(2));
				if(aspect != goAspect)
					continue;
				
				// We found one
				GONode retNode = new GONode();
				retNode.setAccession(rs.getString(7));
				retNode.setAspect(aspect);
				retNode.setName(rs.getString(1));
				retNode.setDefinition(rs.getString(4));
				retNode.setId(rs.getInt(6));
				
				if (rs.getInt(3) == 0) retNode.setObsolete(false);
				else retNode.setObsolete(true);
				
				if (rs.getInt(5) == 0) retNode.setRoot(false);
				else retNode.setRoot(true);
				
				GONodeAnnotation annotation = new GONodeAnnotation();
				annotation.setNode(retNode);
				if(rs.getInt(8) == 1)
					annotation.setExact(true);
				
				nodes.add(annotation);
			}
			
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
		
		return nodes;
	}
	
	public static Annotation isProteinAnnotated(int nrseqProteinId, int goTermId) throws SQLException {
		
		// Get our connection to the database.
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			String sqlStr = "SELECT exact "+
							"FROM GOProteinLookup "+
							"WHERE proteinID="+nrseqProteinId+" "+
							"AND termID="+goTermId;
			
			conn = DBConnectionManager.getConnection("go");	
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(sqlStr);
			
			
			if (rs.next()) {
				
				if(rs.getInt(1) == 1)
					return Annotation.EXACT;
				else
					return Annotation.INDIRECT;
				
			}
			else
				return Annotation.NONE;
			
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
	}
}
