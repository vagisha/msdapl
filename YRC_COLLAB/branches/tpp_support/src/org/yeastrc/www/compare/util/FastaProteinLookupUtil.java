/**
 * FastaProteinNameLookupUtil.java
 * @author Vagisha Sharma
 * May 16, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare.util;

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

    public ProteinListing getProteinListing(int nrseqProteinId, List<Integer> dbIds) {
        
        ProteinListing listing = new ProteinListing();
        listing.setNrseqProteinId(nrseqProteinId);
        
        List<NrDbProtein> nrDbProtList = NrSeqLookupUtil.getProtein(nrseqProteinId, dbIds);
        
        List<ProteinNameDescription> cndList = new ArrayList<ProteinNameDescription>();
        
        for(NrDbProtein nrp: nrDbProtList) {
            String acc  = nrp.getAccessionString();
            String description = nrp.getDescription();
            
            ProteinNameDescription cnd = new ProteinNameDescription();
            cnd.setName(acc);
            cnd.setDescription(description);
            cndList.add(cnd);
        }
        listing.setNameAndDescription(cndList);
        
        return listing;
    }
    
    public List<Integer> getProteinIds(String fastaProteinName, int pinferId) {
        
        List<Integer> dbIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
        Set<Integer> found = new HashSet<Integer>();
        List<NrDbProtein> matching = NrSeqLookupUtil.getDbProteinsForAccession(dbIds, fastaProteinName);
        for(NrDbProtein prot: matching)
            found.add(prot.getProteinId());
        return new ArrayList<Integer>(found);
    }
    
    public List<Integer> getProteinIds(List<String> fastaProteinNames, int pinferId) {
        
        List<Integer> dbIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
        Set<Integer> found = new HashSet<Integer>();
        for(String ra: fastaProteinNames) {
            List<NrDbProtein> matching = NrSeqLookupUtil.getDbProteinsForAccession(dbIds, ra);
            for(NrDbProtein prot: matching)
                found.add(prot.getProteinId());
        }
        return new ArrayList<Integer>(found);
    }
    
}
