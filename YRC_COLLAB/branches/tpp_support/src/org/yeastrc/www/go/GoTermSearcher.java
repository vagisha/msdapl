/**
 * 
 */
package org.yeastrc.www.go;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	private String queryString;
	private boolean bp;
	private boolean mf;
	private boolean cc;
	private boolean matchAll;
	private boolean searchSynonyms;
	
	private static Pattern GO_ACC_PATTERN = Pattern.compile("^GO:\\d+$");
	
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public void setUseBiologicalProcess(boolean useBiologicalProcess) {
		this.bp = useBiologicalProcess;
	}

	public void setUseMolecularFunction(boolean useMolecularFunction) {
		this.mf = useMolecularFunction;
	}

	public void setUseCellularComponent(boolean useCellularComponent) {
		this.cc = useCellularComponent;
	}

	public void setMatchAll(boolean matchAll) {
		this.matchAll = matchAll;
	}

	public void setSearchSynonyms(boolean searchSynonyms) {
		this.searchSynonyms = searchSynonyms;
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
	public List<GONode> getMatchingTerms() throws SQLException {
		
		if(queryString == null || queryString.trim().length() == 0)
			return new ArrayList<GONode>(0);
		
		// If there are any commas remove them
		queryString = queryString.replaceAll(",", "");
		String[] words = queryString.split("\\s+");
		
		List<String> accessionWords = new ArrayList<String>();
		List<String> nonAccWords = new ArrayList<String>();
		Matcher m;
		for(String word: words) {
			m = GO_ACC_PATTERN.matcher(word);
			if(m.matches())
				accessionWords.add(word);
			else
				nonAccWords.add(word);
		}
		
		Set<GONode> uniqNodes = new HashSet<GONode>();
		
		// Match GO accessions
		List<GONode> accMatches = searchTermAccession(accessionWords);
		for(GONode node: accMatches)
			uniqNodes.add(node);
		
		// Match GO term names
		List<GONode> matchedNodes = searchTermName(nonAccWords);
		for(GONode node: matchedNodes)
			uniqNodes.add(node);
		
		// Match GO term synonyms
		if(searchSynonyms) {
			List<GONode> synMatches = searchTermSynonyms(nonAccWords);
			for(GONode node: synMatches) {
				uniqNodes.add(node);
			}
		}
		return new ArrayList<GONode>(uniqNodes);
	}

	private List<GONode> searchTermAccession(List<String> words) throws SQLException {
		// Get our connection to the database.
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			
			String sqlStr = "SELECT  t.name, t.term_type, t.is_obsolete, d.term_definition, t.is_root, t.id, t.acc "+
							"FROM term AS t "+
							"LEFT OUTER JOIN term_definition AS d ON t.id = d.term_id "+
							"WHERE t.acc = ? "+
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
			
			int wordCount = 0;
			for(String word: words) {
				word = word.trim();
				if(word.length() == 0)
					continue;
				
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
	
	private List<GONode> searchTermName(List<String> words) throws SQLException {
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
	
	private List<GONode> searchTermSynonyms(List<String> words) throws SQLException {
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
