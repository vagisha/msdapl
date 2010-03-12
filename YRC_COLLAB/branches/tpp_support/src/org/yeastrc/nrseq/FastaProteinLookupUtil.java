/**
 * FastaProteinNameLookupUtil.java
 * @author Vagisha Sharma
 * May 16, 2009
 * @version 1.0
 */
package org.yeastrc.nrseq;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.domain.nrseq.NrDbProtein;
import org.yeastrc.www.compare.ProteinDatabaseLookupUtil;

/**
 * 
 */
public class FastaProteinLookupUtil {

    private static FastaProteinLookupUtil instance;
    
    private FastaProteinLookupUtil() {}
    
    public static FastaProteinLookupUtil getInstance() {
        if(instance == null)
            instance = new FastaProteinLookupUtil();
        return instance;
    }


    public List<Integer> getProteinIdsForAccessions(List<String> fastaAccessions, int pinferId) {
        
        List<Integer> dbIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
        Set<Integer> found = new HashSet<Integer>();
        for(String ra: fastaAccessions) {
            found.addAll(getProteinIdsForAccession(ra, dbIds));
        }
        return new ArrayList<Integer>(found);
    }
    
    private List<Integer> getProteinIdsForAccession(String fastaAccession, List<Integer> dbIds) {
        Set<Integer> found = new HashSet<Integer>();
        List<NrDbProtein> matching = NrSeqLookupUtil.getDbProteinsForAccession(dbIds, fastaAccession);
        for(NrDbProtein prot: matching)
            found.add(prot.getProteinId());
        return new ArrayList<Integer>(found);
    }
    
    public List<Integer> getProteinIdsForDescriptions(List<String> descriptionTerms, int pinferId) {
        
    	// get a list of databases associated with this protein inference
    	// Add the standard databases to the list too.
        List<Integer> dbIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId, true);
        
        Set<Integer> found = new HashSet<Integer>();
        for(String descTerm: descriptionTerms) {
            found.addAll(getProteinIdsForDescription(descTerm, dbIds));
        }
        return new ArrayList<Integer>(found);
    }
    
    private List<Integer> getProteinIdsForDescription(String descriptionTerm, List<Integer> dbIds) {
        
        Set<Integer> found = new HashSet<Integer>();
        List<NrDbProtein> matching = NrSeqLookupUtil.getDbProteinsForDescription(dbIds, descriptionTerm);
        for(NrDbProtein prot: matching)
            found.add(prot.getProteinId());
        return new ArrayList<Integer>(found);
    }
}
