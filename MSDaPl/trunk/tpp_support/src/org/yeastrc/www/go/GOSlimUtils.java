/**
 * GOSlimUtils.java
 * @author Vagisha Sharma
 * May 19, 2010
 * @version 1.0
 */
package org.yeastrc.www.go;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.bio.go.GOAnnotation;
import org.yeastrc.bio.go.GONode;
import org.yeastrc.bio.go.GOUtils;
import org.yeastrc.db.DBConnectionManager;

/**
 * 
 */
public class GOSlimUtils {

	private GOSlimUtils() {}
	
	/**
	 * @return A list of GONodes from the database where the term_type is "subset"
	 * @throws SQLException if there is a database error
	 */
	public static List<GONode> getGOSlims() throws SQLException {
		
		// Get our connection to the database.
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		List<GONode> nodes = new ArrayList<GONode>();
		
		try {
			String sqlStr = "SELECT t.name, t.term_type, t.is_obsolete, d.term_definition, t.is_root, t.id, t.acc "+
							"FROM term AS t "+
							"LEFT OUTER JOIN term_definition AS d ON t.id = d.term_id "+
							"WHERE t.term_type=\"subset\"";
			
			conn = DBConnectionManager.getConnection("go");	
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(sqlStr);
			
			
			while (rs.next()) {
				
				// We found one
				GONode retNode = new GONode();
				retNode.setAccession(rs.getString(7));
				retNode.setAspect(GOUtils.getGOTypeFromDatabase(rs.getString(2)));
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
	
	/**
	 * Returns a subset of GO terms that are a member of the given GO slim and GO aspect.
	 * @return A list of GONodes  
	 * @throws SQLException if there is a database error
	 */
	public static List<GONode> getGOSlimTerms(int goSlimTermId, int goAspect) throws SQLException {
		
		String term_type = "";
		if(goAspect == GOUtils.BIOLOGICAL_PROCESS)
			term_type = "biological_process";
		else if(goAspect == GOUtils.MOLECULAR_FUNCTION)
			term_type = "molecular_function";
		else if(goAspect == GOUtils.CELLULAR_COMPONENT)
			term_type = "cellular_component";
		
		// Get our connection to the database.
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		List<GONode> nodes = new ArrayList<GONode>();
		
		try {
			String sqlStr = "SELECT t.name, t.term_type, t.is_obsolete, d.term_definition, t.is_root, t.id, t.acc "+
							"FROM term AS t, term_definition AS d, term_subset AS s "+
							"WHERE t.id = s.term_id "+
							"AND t.id = d.term_id "+
							"AND t.term_type=\""+term_type+"\" "+
							"AND s.subset_id="+goSlimTermId;
			
			conn = DBConnectionManager.getConnection("go");	
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(sqlStr);
			
			
			while (rs.next()) {
				
				// We found one
				GONode retNode = new GONode();
				retNode.setAccession(rs.getString(7));
				retNode.setAspect(GOUtils.getGOTypeFromDatabase(rs.getString(2)));
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

	/**
	 * Returns a list of GOAnnotation, members of the given GO Slim, that the given protein is 
	 * annotated with.
	 * @param nrseqProteinId
	 * @param goSlimTermId
	 * @param goAspect
	 * @return
	 * @throws SQLException 
	 */
	public static List<GOAnnotation> getAnnotations(int nrseqProteinId, GOSlimFilter filter) throws SQLException {
		
		String term_type = "";
		if(filter.getGoAspect() == GOUtils.BIOLOGICAL_PROCESS)
			term_type = "biological_process";
		else if(filter.getGoAspect() == GOUtils.MOLECULAR_FUNCTION)
			term_type = "molecular_function";
		else if(filter.getGoAspect() == GOUtils.CELLULAR_COMPONENT)
			term_type = "cellular_component";
		
		
		// Get our connection to the database.
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		List<GOAnnotation> nodes = new ArrayList<GOAnnotation>();
		
		try {
			String evidenceToExclude = null;
			List<Integer> excludeCodes = filter.getExcludeEvidenceCodes();
			if(excludeCodes != null && excludeCodes.size() > 0) {
				evidenceToExclude = "";
				for(Integer code: excludeCodes) {
					evidenceToExclude += ","+code;
				}
				if(evidenceToExclude.length() > 0)
					evidenceToExclude = evidenceToExclude.substring(1);
			}
			
			String lookupTable = evidenceToExclude != null ? "GOProteinLookup_Ref_EvidenceCodes" : "GOProteinLookup_Ref";
			
			String sqlStr = "SELECT t.name, t.term_type, t.is_obsolete, d.term_definition, t.is_root, t.id, t.acc, lookup.exact "+
							"FROM (term AS t, term_subset AS s, "+lookupTable+" AS lookup) "+
							"LEFT OUTER JOIN term_definition AS d ON t.id = d.term_id "+
							"WHERE t.id = s.term_id "+
							"AND t.id = lookup.termID "+
							"AND lookup.proteinID = "+nrseqProteinId+" "+
							"AND t.term_type=\""+term_type+"\" "+
							"AND s.subset_id="+filter.getSlimTermId();
			if(evidenceToExclude != null)
				sqlStr += " AND lookup.evidenceCode NOT IN ("+evidenceToExclude+")";
			
			//System.out.println(sqlStr);
			
			conn = DBConnectionManager.getConnection("go");	
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(sqlStr);
			
			
			while (rs.next()) {
				
				// We found one
				GONode retNode = new GONode();
				retNode.setAccession(rs.getString(7));
				retNode.setAspect(GOUtils.getGOTypeFromDatabase(rs.getString(2)));
				retNode.setName(rs.getString(1));
				retNode.setDefinition(rs.getString(4));
				retNode.setId(rs.getInt(6));
				
				if (rs.getInt(3) == 0) retNode.setObsolete(false);
				else retNode.setObsolete(true);
				
				if (rs.getInt(5) == 0) retNode.setRoot(false);
				else retNode.setRoot(true);
				
				GOAnnotation annotation = new GOAnnotation();
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
	
}
