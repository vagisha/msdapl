/**
 * 
 */
package org.yeastrc.www.go;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
	
	/**
	 * Returns the appropriate Annotation type (NONE, EXACT, INDIRECT) for the given protein ID and go term ID
	 * @param nrseqProteinId
	 * @param goTermId
	 * @return
	 * @throws SQLException
	 */
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
	
	/**
	 * Returns a list "non obsolete" terms matching the given parameters
	 * @param queryTerms
	 * @param bp
	 * @param mf
	 * @param cc
	 * @param matchAll -- If true only results matching all query terms will be returned.
	 * @param searchSyn -- If true exact synonyms are searched
	 * @return
	 * @throws SQLException
	 */
	public static List<GONode> getMatchingTerms(String queryTerms, boolean bp, boolean mf, boolean cc, 
			boolean matchAll, boolean searchSyn) throws SQLException {
		
		if(queryTerms == null || queryTerms.trim().length() == 0)
			return new ArrayList<GONode>(0);
		
		// If there are any commas remove them
		queryTerms = queryTerms.replaceAll(",", "");
		
		List<GONode> matchedNodes = searchTermName(queryTerms, bp, mf, cc, matchAll);
		Set<String> uniqAcc = new HashSet<String>(matchedNodes.size() * 2);
		for(GONode node: matchedNodes)
			uniqAcc.add(node.getAccession());
		
		if(searchSyn) {
			List<GONode> synMatches = searchTermSynonyms(queryTerms, bp, mf, cc, matchAll);
			for(GONode node: synMatches) {
				if(!uniqAcc.contains(node.getAccession())) {
					matchedNodes.add(node);
				}
			}
		}
		return matchedNodes;
	}

	private static List<GONode> searchTermName(String queryTerms, boolean bp,
			boolean mf, boolean cc, boolean matchAll) throws SQLException {
		// Get our connection to the database.
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			
			String sqlStr = "SELECT  t.name, t.term_type, t.is_obsolete, d.term_definition, t.is_root, t.id, t.acc "+
							"FROM term AS t "+
							"LEFT OUTER JOIN term_definition AS d ON t.id = d.term_id "+
							"WHERE t.name LIKE ? "+
							"AND t.is_obsolete=0 ";
			
			if(bp && mf && cc) {
				bp = false; mf = false; cc = false;
			}
			String domainString = "";
			if(bp)
				domainString += "\"biological_process\"";
			if(mf) {
				if(bp)	domainString += ",";
				domainString += "\"molecular_function\"";
			}
			if(cc) {
				if(bp || mf)	domainString += ",";
				domainString += "\"cellular_component\"";
			}
			
			if(domainString.length() > 0) {
				if(domainString.indexOf(',') != -1)
					sqlStr += " AND t.term_type IN ("+domainString+")";
				else
					sqlStr += " AND t.term_type = "+domainString;
			}
				
			
			conn = DBConnectionManager.getConnection("go");	
			stmt = conn.prepareStatement(sqlStr);
			
			Map<Integer, GONode> nodeMap = new HashMap<Integer, GONode>();
			Map<Integer, Integer> seenCount = new HashMap<Integer, Integer>();
			
			String[] words = queryTerms.split("\\s+");
			int wordCount = 0;
			
			for(String word: words) {
				word = word.trim();
				if(word.length() == 0)
					continue;
				
				word = "%"+word+"%";
				stmt.setString(1, word);
				rs = stmt.executeQuery();
				wordCount++;
				
				while(rs.next()) {
					int nodeId = rs.getInt("id");
					
					if(nodeMap.containsKey(nodeId)) {
						if(matchAll) {
							Integer count = seenCount.get(nodeId);
							count++;
							seenCount.put(nodeId, count);
						}
						continue;
					}
					
					// We are matching ALL terms and this is NOT the first word we are looking at.
					// This means that this GO term did not match the first word. So we ignore it.
					if(matchAll && wordCount > 1)
						continue;
					
					GONode node = new GONode();
					node.setId(nodeId);
					node.setAccession(rs.getString("acc"));
					node.setName(rs.getString("name"));
					node.setDefinition(rs.getString("term_definition"));
					
					String aspect = rs.getString("term_type");
					if(aspect.equals("biological_process"))
						node.setAspect(GOUtils.BIOLOGICAL_PROCESS);
					else if(aspect.equals("molecular_function"))
						node.setAspect(GOUtils.MOLECULAR_FUNCTION);
					else if(aspect.equals("cellular_component"))
						node.setAspect(GOUtils.CELLULAR_COMPONENT);
					
					if(rs.getInt("is_root") == 1)
						node.setRoot(true);
					if(rs.getInt("is_obsolete") == 1)
						node.setObsolete(true);
					
					if(seenCount.containsKey(nodeId)) {
						System.out.println("ALREADY SEEN id: "+nodeId);
					}
					seenCount.put(nodeId, 1);
					
					nodeMap.put(nodeId, node);
				}
			}
			
			if(!matchAll)
				return new ArrayList<GONode>(nodeMap.values());
			else {
				List<GONode> goodNodes = new ArrayList<GONode>();
				for(Integer nodeId: seenCount.keySet()) {
					// add a term if it matched ALL words
					if(seenCount.get(nodeId).intValue() == wordCount) {
						goodNodes.add(nodeMap.get(nodeId));
					}
				}
				return goodNodes;
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
	}
	
	private static List<GONode> searchTermSynonyms(String queryTerms, boolean bp,
			boolean mf, boolean cc, boolean matchAll) throws SQLException {
		// Get our connection to the database.
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			
			String sqlStr = "SELECT  DISTINCT(t.id), t.name, t.term_type, t.is_obsolete, d.term_definition, t.is_root, t.id, t.acc, ts.term_synonym "+
							"FROM (term AS t, term_synonym AS ts ) "+
							"LEFT OUTER JOIN term_definition AS d ON t.id = d.term_id "+
							"WHERE ts.term_synonym LIKE ? "+
							"AND ts.term_id = t.id "+
							"AND t.is_obsolete=0 ";
			
			if(bp && mf && cc) {
				bp = false; mf = false; cc = false;
			}
			String domainString = "";
			if(bp)
				domainString += "\"biological_process\"";
			if(mf) {
				if(bp)	domainString += ",";
				domainString += "\"molecular_function\"";
			}
			if(cc) {
				if(bp || mf)	domainString += ",";
				domainString += "\"cellular_component\"";
			}
			
			if(domainString.length() > 0) {
				if(domainString.indexOf(',') != -1)
					sqlStr += " AND t.term_type IN ("+domainString+")";
				else
					sqlStr += " AND t.term_type = "+domainString;
			}
			
			sqlStr += "GROUP BY t.id ORDER BY t.id";
				
			
			conn = DBConnectionManager.getConnection("go");	
			stmt = conn.prepareStatement(sqlStr);
			
			Map<Integer, GONode> nodeMap = new HashMap<Integer, GONode>();
			Map<Integer, Integer> seenCount = new HashMap<Integer, Integer>();
			
			String[] words = queryTerms.split("\\s+");
			int wordCount = 0;
			
			for(String word: words) {
				word = word.trim();
				if(word.length() == 0)
					continue;
				
				word = "%"+word+"%";
				stmt.setString(1, word);
				rs = stmt.executeQuery();
				wordCount++;
				
				while(rs.next()) {
					int nodeId = rs.getInt("id");
					
					if(nodeMap.containsKey(nodeId)) {
						if(matchAll) {
							Integer count = seenCount.get(nodeId);
							count++;
							seenCount.put(nodeId, count);
						}
						nodeMap.get(nodeId).addSynonym(rs.getString("term_synonym"));
						continue;
					}
					
					// We are matching ALL terms and this is NOT the first word we are looking at.
					// This means that this GO term did not match the first word. So we ignore it.
					if(matchAll && wordCount > 1)
						continue;
					
					GONode node = new GONode();
					node.setId(nodeId);
					node.setAccession(rs.getString("acc"));
					node.setName(rs.getString("name"));
					node.setDefinition(rs.getString("term_definition"));
					node.addSynonym(rs.getString("term_synonym"));
					
					String aspect = rs.getString("term_type");
					if(aspect.equals("biological_process"))
						node.setAspect(GOUtils.BIOLOGICAL_PROCESS);
					else if(aspect.equals("molecular_function"))
						node.setAspect(GOUtils.MOLECULAR_FUNCTION);
					else if(aspect.equals("cellular_component"))
						node.setAspect(GOUtils.CELLULAR_COMPONENT);
					
					if(rs.getInt("is_root") == 1)
						node.setRoot(true);
					if(rs.getInt("is_obsolete") == 1)
						node.setObsolete(true);
					
					if(seenCount.containsKey(nodeId)) {
						System.out.println("ALREADY SEEN id: "+nodeId);
					}
					seenCount.put(nodeId, 1);
					
					nodeMap.put(nodeId, node);
				}
			}
			
			if(!matchAll)
				return new ArrayList<GONode>(nodeMap.values());
			else {
				List<GONode> goodNodes = new ArrayList<GONode>();
				for(Integer nodeId: seenCount.keySet()) {
					// add a term if it matched ALL words
					if(seenCount.get(nodeId).intValue() == wordCount) {
						goodNodes.add(nodeMap.get(nodeId));
					}
				}
				return goodNodes;
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
	}
	
	
}
