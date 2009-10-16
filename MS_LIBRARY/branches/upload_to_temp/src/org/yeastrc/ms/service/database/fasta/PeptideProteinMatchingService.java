/**
 * PeptideProteinMatchingService.java
 * @author Vagisha Sharma
 * Sep 11, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service.database.fasta;

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

import org.apache.log4j.Logger;
import org.yeastrc.ms.ConnectionFactory;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.domain.general.EnzymeRule;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.nrseq.NrDbProtein;
import org.yeastrc.ms.service.MsDataUploadProperties;
import org.yeastrc.ms.util.StringUtils;

/**
 * 
 */
public class PeptideProteinMatchingService {

    
    private final boolean createSuffixTables; 
    private final boolean useSingleQuery;
    private final boolean createSuffixInMemory;
    private final int databaseId;
    private int numEnzymaticTermini = 0;
    private List<EnzymeRule> enzymeRules;
    
    private Map<String, List<Integer>> suffixMap;
    
    private Map<String, Integer> suffixIdMap;
    
    private static final Logger log = Logger.getLogger(PeptideProteinMatchingService.class.getName());
    
    public PeptideProteinMatchingService(int databaseId) throws SQLException {
        
        this.databaseId = databaseId;
        
        createSuffixTables = MsDataUploadProperties.useNrseqSuffixTables();
        createSuffixInMemory = MsDataUploadProperties.useNrseqSuffixInMemory();
        useSingleQuery = true;
        
        if(createSuffixTables) {
            createSuffixTable(databaseId);
        }
        else if(createSuffixInMemory) {
            buildInMemorySuffixes(databaseId);
        }
    }
    
    // --------------------------------------------------------------------------------
    // SUFFIX TABLE IN DATABASE
    // --------------------------------------------------------------------------------
    private void createSuffixTable(int databaseId) throws SQLException {
        FastaDatabaseSuffixCreator creator = new FastaDatabaseSuffixCreator();
        creator.createSuffixTable(databaseId);
        if(useSingleQuery)
            this.suffixIdMap = creator.getSuffixIdMap();
    }
    
    // --------------------------------------------------------------------------------
    // SUFFIX MAP IN MEMORY
    // --------------------------------------------------------------------------------
    private void buildInMemorySuffixes(int databaseId) {
        
        FastaInMemorySuffixCreator creator = new FastaInMemorySuffixCreator();
        suffixMap = creator.buildInMemorySuffixes(databaseId);
    }
    
    // --------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------
    public void setNumEnzymaticTermini(int net) {
        this.numEnzymaticTermini = net;
    }
    
    public int getNumEnzymaticTermini() {
        return this.numEnzymaticTermini;
    }
   
    public void setEnzymeRules(List<EnzymeRule> enzymeRules) {
        this.enzymeRules = enzymeRules;
    }
    
    public void setEnzymes(List<MsEnzyme> enzymes) {
        enzymeRules = new ArrayList<EnzymeRule>(enzymes.size());
        for(MsEnzyme enzyme: enzymes)
            enzymeRules.add(new EnzymeRule(enzyme));
    }

    public List<PeptideProteinMatch> getMatchingProteins(String peptide) {
        
        return getMatchingProteins(peptide, enzymeRules, numEnzymaticTermini);
    }
    
    private List<PeptideProteinMatch> getMatchingProteins(String peptide, List<EnzymeRule> enzymeRules, 
            int numEnzymaticTermini) {
        
        // find the matching database protein ids for the given peptide and fasta databases
        List<Integer> dbProtIds = getMatchingDbProteinIds(peptide);
        
        // find the best protein peptide match based on the given enzyme and num enzymatic termini criteria
        List<PeptideProteinMatch> matchingProteins = new ArrayList<PeptideProteinMatch>(dbProtIds.size());
        for(int dbProtId: dbProtIds) {
            NrDbProtein dbProt = NrSeqLookupUtil.getDbProtein(dbProtId);
            PeptideProteinMatch match = getPeptideProteinMatch(dbProt, peptide, enzymeRules, numEnzymaticTermini);
            if(match != null) {
                matchingProteins.add(match);
            }
        }
        return matchingProteins;
    }
    
    private PeptideProteinMatch getPeptideProteinMatch(NrDbProtein dbProt, String peptide,
            List<EnzymeRule> enzymeRules, int minEnzymaticTermini) {
        
        String sequence = NrSeqLookupUtil.getProteinSequence(dbProt.getProteinId());
        
        return getPeptideProteinMatch(dbProt, peptide, enzymeRules,
                minEnzymaticTermini, sequence);
    }
    
    PeptideProteinMatch getPeptideProteinMatch(
            NrDbProtein dbProt, String peptide, List<EnzymeRule> enzymeRules,
            int minEnzymaticTermini, String sequence) {
        
        int idx = sequence.indexOf(peptide);
        
        while(idx != -1) {
            
            char nterm = idx == 0 ? '-' : sequence.charAt(idx - 1);
            char cterm = idx + peptide.length() == sequence.length() ? '-' : sequence.charAt(idx + peptide.length());
            
            PeptideProteinMatch match = new PeptideProteinMatch();
            match.setPeptide(peptide);
            match.setPreResidue(nterm);
            match.setPostResidue(cterm);
            match.setProtein(dbProt);
            
            if(enzymeRules.size() == 0) {
                return match;
            }
            // look at each enzyme rule and return the first match
            for(EnzymeRule rule: enzymeRules) {
                int net = rule.getNumEnzymaticTermini(peptide, nterm, cterm);
                if(net >= minEnzymaticTermini) {
                    match.setNumEnzymaticTermini(net);
                    return match;
                }
            }
            idx = sequence.indexOf(peptide, idx+1);
        }
        
        return null;
    }
    
    private List<Integer> getMatchingDbProteinIds(String peptide) {
        
        if(this.createSuffixTables) {
            return getMatchingDbProteinIdsForPeptideFromDatabase(peptide, useSingleQuery);
        }
        else if(this.createSuffixInMemory) {
            return getMatchingDbProteinIdsForPeptideFromMemory(peptide);
        }
        else {
            return NrSeqLookupUtil.getDbProteinIdsMatchingPeptide(peptide, databaseId);
        }
    }
    
    private List<Integer> getMatchingDbProteinIdsForPeptideFromDatabase(String peptide, boolean oneQuery) {
        
        if(oneQuery) {
            return getDbProteinIdsOneQuery(peptide);
        }
        else {
            return getDbProteinIdsMultiQuery(peptide);
        }
    }

    private List<Integer> getDbProteinIdsOneQuery(String peptide) {
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        List<String> suffixList = getSuffixList(peptide);
        String commaSep = StringUtils.makeQuotedCommaSeparated(suffixList);
        
        List<Integer> dbProteinIds = new ArrayList<Integer>();
        try {
            conn = ConnectionFactory.getNrseqConnection();
            String sql = "SELECT db.dbProteinID, count(*) AS cnt FROM "+
            FastaDatabaseSuffixCreator.getDbSuffixTableName(databaseId)+ " AS db, "+
            FastaDatabaseSuffixCreator.getMainSuffixTableName()+" AS s "+
            " WHERE s.suffix IN ("+commaSep+") AND s.id = db.suffixID "+
            " GROUP BY db.dbProteinID HAVING cnt = "+suffixList.size();
            
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                dbProteinIds.add((rs.getInt("dbProteinID")));
            }
//            if(dbProteinIds.size() == 0) {
//               System.out.println(sql);
//            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Exception getting matching proteinIds for suffix: "+peptide+" and database: "+this.databaseId, e);
        }
        finally {
            
            if(conn != null)    try {conn.close();} catch(SQLException e){}
            if(stmt != null)    try {stmt.close();} catch(SQLException e){}
            if(rs != null)    try {rs.close();} catch(SQLException e){}
        }
        return dbProteinIds;
    }
    
    private List<Integer> getDbProteinIdsMultiQuery(String peptide) {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        List<String> suffixList = getSuffixList(peptide);
        Map<Integer, Integer> proteinIdCount = new HashMap<Integer, Integer>();
        
        try {
            conn = ConnectionFactory.getNrseqConnection();
            String sql = "SELECT dbProteinID FROM "+
            FastaDatabaseSuffixCreator.getDbSuffixTableName(databaseId)+
//            FastaDatabaseSuffixCreator.getMainSuffixTableName()+" AS s "+
            " WHERE suffixID=?";
            
            stmt = conn.prepareStatement(sql);
            
            for(String suffix: suffixList) {
                stmt.setInt(1, suffixIdMap.get(suffix));
                rs = stmt.executeQuery();
                while(rs.next()) {
                    int dbProteinId = rs.getInt("dbProteinID");
                    Integer count = proteinIdCount.get(dbProteinId);
                    if(count == null) {
                        count = 0;
                    }
                    proteinIdCount.put(dbProteinId, ++count);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Exception getting matching proteinIds for suffix: "+peptide+" and database: "+this.databaseId, e);
        }
        finally {
            
            if(conn != null)    try {conn.close();} catch(SQLException e){}
            if(stmt != null)    try {stmt.close();} catch(SQLException e){}
            if(rs != null)    try {rs.close();} catch(SQLException e){}
        }
        
        List<Integer> dbProteinIds = new ArrayList<Integer>();
        int reqCnt = suffixList.size();
        for(int dbProteinId: proteinIdCount.keySet()) {
            if(proteinIdCount.get(dbProteinId) == reqCnt) {
                dbProteinIds.add(dbProteinId);
            }
        }
        return dbProteinIds;
    }
    
    private List<String> getSuffixList(String peptide) {
        
        Set<String> suffixList = new HashSet<String>();
        for(int i = 0; i < peptide.length(); i++) {
            int end = Math.min(i+FastaDatabaseSuffixCreator.SUFFIX_LENGTH, peptide.length());
            suffixList.add(peptide.substring(i, end));
            
            if(i+FastaDatabaseSuffixCreator.SUFFIX_LENGTH >= peptide.length())
                break;
        }
        return new ArrayList<String>(suffixList);
    }
    
    private List<Integer> getMatchingDbProteinIdsForPeptideFromMemory(String peptide) {
        
        // suffixes we store are 5 aa long. 
        Map<Integer, Integer> matches = new HashMap<Integer, Integer>();
        int SUFFIX_LENGTH = FastaDatabaseSuffixCreator.SUFFIX_LENGTH;
        int numSuffixesInSeq = 0;
        for(int i = 0; i < peptide.length(); i++) {
            int end = Math.min(i+SUFFIX_LENGTH, peptide.length());
            String subseq = peptide.substring(i, end);
            
            numSuffixesInSeq++;
//            System.out.println("Looking for match for: "+subseq);
//            System.out.println("suffix map size: "+suffixMap.size());
            List<Integer> matchingProteins = suffixMap.get(subseq);
            if(i == 0) {
                for(int proteinId: matchingProteins)
                    matches.put(proteinId, 1);
            }
            else {
                
                for(int proteinId: matchingProteins) {
                    Integer num = matches.get(proteinId);
                    if(num != null) {
                        matches.put(proteinId, ++num);
                    }
                }
            }
            
            if(i+SUFFIX_LENGTH >= peptide.length())
                break;
        }
        
        // keep only those matched that had all the 5-mers in our peptide.
        List<Integer> allMatches = new ArrayList<Integer>();
        for(int proteinId: matches.keySet()) {
            int cnt = matches.get(proteinId);
            if(cnt == numSuffixesInSeq)
                allMatches.add(proteinId);
        }
        if(allMatches.size() > 10)
            System.out.println("!!!# matches found: "+allMatches.size());
        return allMatches;
    }
}
