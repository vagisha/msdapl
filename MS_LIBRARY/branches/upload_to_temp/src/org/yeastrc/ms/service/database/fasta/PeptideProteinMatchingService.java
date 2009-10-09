/**
 * PeptideProteinMatchingService.java
 * @author Vagisha Sharma
 * Sep 11, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service.database.fasta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.domain.general.EnzymeRule;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.nrseq.NrDbProtein;
import org.yeastrc.ms.domain.nrseq.NrDbProteinFull;
import org.yeastrc.ms.service.MsDataUploadProperties;
import org.yeastrc.ms.util.TimeUtils;

/**
 * 
 */
public class PeptideProteinMatchingService {

    
    private final boolean createSuffixTables; 
    private final int databaseId;
    private int numEnzymaticTermini = 0;
    private List<EnzymeRule> enzymeRules;
    
    private Map<String, List<Integer>> suffixMap;
    
    private static final Logger log = Logger.getLogger(PeptideProteinMatchingService.class.getName());
    
    public PeptideProteinMatchingService(int databaseId) throws SQLException {
        
        this.databaseId = databaseId;
        
        createSuffixTables = MsDataUploadProperties.useNrseqSuffixTables();
        if(createSuffixTables) {
            createSuffixTable(databaseId);
        }
    }
    
    private void createSuffixTable(int databaseId) throws SQLException {
//        FastaDatabaseSuffixCreator creator = new FastaDatabaseSuffixCreator();
//        creator.createSuffixTable(databaseId);
        buildSuffixes(databaseId);
    }
    
    public void buildSuffixes(int databaseId) {
        
        suffixMap = new HashMap<String, List<Integer>>(3200000);
        
        // get all the ids from tblProteinDatabase for the given databaseID
        List<Integer> dbProteinIds = NrSeqLookupUtil.getDbProteinIdsForDatabase(databaseId);
        log.info("# proteins: "+dbProteinIds.size()+" in databaseID: "+databaseId);
        
        // some proteins in a fasta file have the same sequence.  We will not create suffixes twice
        Set<Integer> seenSequenceIds = new HashSet<Integer>(dbProteinIds.size());
        
        long s = System.currentTimeMillis();
        
        int cnt = 0;
        for(int dbProteinId: dbProteinIds) {
            NrDbProteinFull protein = NrSeqLookupUtil.getDbProteinFull(dbProteinId);
            
            if(seenSequenceIds.contains(protein.getSequenceId()))
                continue;
            else
                seenSequenceIds.add(protein.getSequenceId());
            
            String sequence = NrSeqLookupUtil.getProteinSequenceForNrSeqDbProtId(dbProteinId);
            
//            System.out.println("Looking at sequence: "+ ++cnt);
            createSuffixes(sequence, protein.getSequenceId(), dbProteinId);
            
        }
        
        long e = System.currentTimeMillis();
        
        log.info("Created suffix map with "+suffixMap.size()+" entries");
        log.info("Total time to create 5-mer suffix map for databaseID: "+databaseId+" was "
                +TimeUtils.timeElapsedSeconds(s, e)+"\n\n");
    }
    
    private void createSuffixes(String sequence, int sequenceId, int dbProteinId) {
        
        int SUFFIX_LENGTH = 5;
        for(int i = 0; i < sequence.length(); i++) {
            int end = Math.min(i+SUFFIX_LENGTH, sequence.length());
            String subseq = sequence.substring(i, end);
            
            List<Integer> matchingProteins = suffixMap.get(subseq);
            if(matchingProteins == null) {
                matchingProteins = new ArrayList<Integer>();
                suffixMap.put(subseq, matchingProteins);
            }
            matchingProteins.add(dbProteinId);
            
            if(i+SUFFIX_LENGTH >= sequence.length())
                break;
        }
    }
    
    public void setNumEnzymaticTermini(int net) {
        this.numEnzymaticTermini = net;
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
            return getMatchingDbProteinIdsForSuffix(peptide);
        }
        else {
            return NrSeqLookupUtil.getDbProteinIdsMatchingPeptide(peptide, databaseId);
        }
    }
    
    private List<Integer> getMatchingDbProteinIdsForSuffix(String suffix) {
        
        // suffixes we store are 5 aa long. 
        Map<Integer, Integer> matches = new HashMap<Integer, Integer>();
        int SUFFIX_LENGTH = 5;
        int numSuffixesInSeq = 0;
        for(int i = 0; i < suffix.length(); i++) {
            int end = Math.min(i+SUFFIX_LENGTH, suffix.length());
            String subseq = suffix.substring(i, end);
            
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
            
            if(i+SUFFIX_LENGTH >= suffix.length())
                break;
        }
        
        List<Integer> allMatches = new ArrayList<Integer>();
        for(int proteinId: matches.keySet()) {
            int cnt = matches.get(proteinId);
            if(cnt == numSuffixesInSeq)
                allMatches.add(proteinId);
        }
        if(allMatches.size() > 10)
            System.out.println("!!!# matches found: "+allMatches.size());
        return allMatches;
        
        
        
//        Connection conn = null;
//        Statement stmt = null;
//        ResultSet rs = null;
//        List<Integer> dbProteinIds = new ArrayList<Integer>();
//        try {
//            conn = ConnectionFactory.getNrseqConnection();
//            String sql = "SELECT dbProteinID FROM "+
//            FastaDatabaseSuffixCreator.getSuffixTableName(databaseId)+
//            " WHERE suffix LIKE '"+suffix+"%'";
//            
//            stmt = conn.createStatement();
//            rs = stmt.executeQuery(sql);
//            while(rs.next()) {
//                dbProteinIds.add((rs.getInt("dbProteinID")));
//            }
//        }
//        catch (SQLException e) {
//            throw new RuntimeException("Exception getting matching proteinIds for suffix: "+suffix+" and database: "+this.databaseId, e);
//        }
//        finally {
//            
//            if(conn != null)    try {conn.close();} catch(SQLException e){}
//            if(stmt != null)    try {stmt.close();} catch(SQLException e){}
//            if(rs != null)    try {rs.close();} catch(SQLException e){}
//        }
//        return dbProteinIds;
    }
}
