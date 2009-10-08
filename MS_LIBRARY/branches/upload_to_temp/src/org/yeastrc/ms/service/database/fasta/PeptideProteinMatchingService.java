/**
 * PeptideProteinMatchingService.java
 * @author Vagisha Sharma
 * Sep 11, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service.database.fasta;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.ConnectionFactory;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.domain.general.EnzymeRule;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.nrseq.NrDbProtein;
import org.yeastrc.ms.service.MsDataUploadProperties;

/**
 * 
 */
public class PeptideProteinMatchingService {

    
    private final boolean createSuffixTables; 
    private final int databaseId;
    private int numEnzymaticTermini = 0;
    private List<EnzymeRule> enzymeRules;
    
    public PeptideProteinMatchingService(int databaseId) throws SQLException {
        
        this.databaseId = databaseId;
        
        createSuffixTables = MsDataUploadProperties.useNrseqSuffixTables();
        if(createSuffixTables) {
            createSuffixTable(databaseId);
        }
    }
    
    private void createSuffixTable(int databaseId) throws SQLException {
        FastaDatabaseSuffixCreator creator = new FastaDatabaseSuffixCreator();
        creator.createSuffixTable(databaseId);
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
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Integer> dbProteinIds = new ArrayList<Integer>();
        try {
            conn = ConnectionFactory.getNrseqConnection();
            String sql = "SELECT dbProteinID FROM "+
            FastaDatabaseSuffixCreator.getSuffixTableName(databaseId)+
            " WHERE suffix LIKE '"+suffix+"%'";
            
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                dbProteinIds.add((rs.getInt("dbProteinID")));
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Exception getting matching proteinIds for suffix: "+suffix+" and database: "+this.databaseId, e);
        }
        finally {
            
            if(conn != null)    try {conn.close();} catch(SQLException e){}
            if(stmt != null)    try {stmt.close();} catch(SQLException e){}
            if(rs != null)    try {rs.close();} catch(SQLException e){}
        }
        return dbProteinIds;
    }
}
