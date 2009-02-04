/**
 * SequestResultsGetter.java
 * @author Vagisha Sharma
 * Jan 2, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;

import edu.uwpr.protinfer.PeptideDefinition;
import edu.uwpr.protinfer.PeptideKeyCalculator;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerRun;
import edu.uwpr.protinfer.infer.Peptide;
import edu.uwpr.protinfer.infer.PeptideHit;
import edu.uwpr.protinfer.infer.Protein;
import edu.uwpr.protinfer.util.TimeUtils;

/**
 * 
 */
public class SequestResultsGetter implements ResultsGetter {

    private static final Logger log = Logger.getLogger(SequestResultsGetter.class);
    
    private static final SequestResultsGetter instance = new SequestResultsGetter();
    
    private SequestResultsGetter() {}
    
    public static SequestResultsGetter instance() {
        return instance;
    }

    @Override
    public List<PeptideSpectrumMatchNoFDR> getResultsNoFdr(IdPickerRun run, IDPickerParams params) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public List<PeptideSpectrumMatchIDP> getResults(int inputId, IDPickerParams params) {
        
        SequestSearchResultDAO resultDao = DAOFactory.instance().getSequestResultDAO();
        MsSearchResultProteinDAO protDao = DAOFactory.instance().getMsProteinMatchDAO();
        
        log.info("Loading top hits for runSearchID: "+inputId);
        
        long start = System.currentTimeMillis();
        long s = start;
        List<SequestSearchResult> resultList = resultDao.loadTopResultsForRunSearchN(inputId, true); // get modifications
        log.info("\tTotal top hits for "+inputId+": "+resultList.size());
        long e = System.currentTimeMillis();
        
        log.info("\tTime: "+TimeUtils.timeElapsedSeconds(s,e)+" seconds.");
        
       
        // Remove search hits to small peptides
        removeSmallPeptides(resultList, params);
        
        
        // make a list of peptide spectrum matches and read the matching proteins from the database
        s = System.currentTimeMillis();
        
        String decoyPrefix = params.getDecoyPrefix();
        
        PeptideDefinition peptideDef = params.getPeptideDefinition();
        
        // map of peptide_key and peptideHit
        Map<String, PeptideHit> peptideHitMap = new HashMap<String, PeptideHit>();
        
        // map of protein accession and protein
        Map<String, Protein> proteinMap = new HashMap<String, Protein>();
        
        List<PeptideSpectrumMatchIDP> psmList = new ArrayList<PeptideSpectrumMatchIDP>(resultList.size());
        
        for (SequestSearchResult result: resultList) {
            
            SequestResultData scores = result.getSequestResultData();
            
            // get the peptide
            String peptideKey = PeptideKeyCalculator.getKey(result, peptideDef);
            
            PeptideHit peptHit = peptideHitMap.get(peptideKey);
            // If we haven't already seen this peptide, create a new entry
            if(peptHit == null) {
                Peptide peptide = new Peptide(result.getResultPeptide().getPeptideSequence(), peptideKey, -1);
                peptHit = new PeptideHit(peptide);
                peptideHitMap.put(peptideKey, peptHit);
            }
            
            // read the matching proteins from the database now
            List<MsSearchResultProtein> msProteinList = protDao.loadResultProteins(result.getId());
           
            for (MsSearchResultProtein protein: msProteinList) {
                
                // we could have multiple accessions, keep the first one only
                String[] accessionStrings = protein.getAccession().split("\\cA");
            
                Protein prot = proteinMap.get(accessionStrings[0]);
                // If we have not already seen this protein create a new entry
                if(prot == null) {
                    prot = new Protein(accessionStrings[0], -1);
                    if(decoyPrefix != null) {
                        if (prot.getAccession().startsWith(decoyPrefix))
                            prot.setDecoy();
                    }
                    proteinMap.put(accessionStrings[0], prot);
                }
                
                peptHit.addProtein(prot);

            }
            
            SpectrumMatchIDPImpl specMatch = new SpectrumMatchIDPImpl();
            specMatch.setHitId(result.getId());
            specMatch.setScanId(result.getScanId());
            specMatch.setCharge(result.getCharge());
            specMatch.setSourceId(inputId);
            specMatch.setModifiedSequence(result.getResultPeptide().getModifiedPeptide());
//            specMatch.setRank(scores.getxCorrRank()); // Rank will be based on calculated FDR
            
            PeptideSpectrumMatchIDPImpl psm = new PeptideSpectrumMatchIDPImpl();
            psm.setPeptide(peptHit);
            psm.setSpectrumMatch(specMatch);
            psm.setAbsoluteScore(scores.getxCorr().doubleValue());
            psm.setRelativeScore(scores.getDeltaCN().doubleValue());
            
            psmList.add(psm);
        }
        e = System.currentTimeMillis();
        log.info("\tTime to get matching proteins and create list of spectrum matches: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds.");
        e = System.currentTimeMillis();
        log.info("Total time: "+TimeUtils.timeElapsedSeconds(start, e)+" seconds.");
        return psmList;
    }
    
    private void removeSmallPeptides(List<SequestSearchResult> resultList, IDPickerParams params) {
        
        log.info("Removing search hits with peptide length < "+params.getMinPeptideLength());
        Iterator<SequestSearchResult> iter = resultList.iterator();
        int removed = 0;
        while(iter.hasNext()) {
            SequestSearchResult res = iter.next();
            // if the length of the peptide is less than the required threshold do not add it to the final list
            if(res.getResultPeptide().getPeptideSequence().length() < params.getMinPeptideLength()) {
                iter.remove();
                removed++;
            }
        }
        log.info("\tRemoved "+removed+" spectra. Remaining spectra: "+resultList.size());
     }
    
}
