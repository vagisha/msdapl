package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.Program;

import edu.uwpr.protinfer.PeptideDefinition;
import edu.uwpr.protinfer.PeptideKeyCalculator;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerInput;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerRun;
import edu.uwpr.protinfer.infer.Peptide;
import edu.uwpr.protinfer.infer.PeptideHit;
import edu.uwpr.protinfer.infer.Protein;
import edu.uwpr.protinfer.util.TimeUtils;

public class PercolatorResultsGetter implements ResultsGetter {

private static final Logger log = Logger.getLogger(IdPickerInputGetter.class);
    
    private static PercolatorResultsGetter instance = new PercolatorResultsGetter();
    
    private PercolatorResultsGetter() {}
    
    public static final PercolatorResultsGetter instance() {
        return instance;
    }
    
    @Override
    public List<PeptideSpectrumMatchIDP> getResults(IdPickerRun run, IDPickerParams params) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public List<PeptideSpectrumMatchIDP> getResults(List<IdPickerInput> inputList, Program inputGenerator, IDPickerParams params) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Returns a list of peptide spectrum matches which are filtered by relevant score(s)
     * and for min peptide length and 
     * ranked by relevant score(s) for each peptide (as defined in the PeptideDefinition). 
     * Ambiguous spectra are filtered.
     */
    @Override
    public  List<PeptideSpectrumMatchNoFDR> getResultsNoFdr(IdPickerRun run, IDPickerParams params) {
        
        return getResultsNoFdr(run.getInputList(), run.getInputGenerator(), params);
    }
    
    
    @Override
    public List<PeptideSpectrumMatchNoFDR> getResultsNoFdr(List<IdPickerInput> inputList, Program inputGenerator,
            IDPickerParams params) {
        
        long start = System.currentTimeMillis();

        PercolatorParams percParams = new PercolatorParams(params);
        
        // 1. Get ALL the PercolatorResults (filtered by score(s) and peptide length
        //    Ambiguous spectra are also removed
        List<PercolatorResult> allResults = getAllPercolatorResults(inputList, inputGenerator, percParams);

        // 2. Convert list of PercolatorResult to PeptideSpectrumMatchNoFDR
        //    Rank the results for each peptide.
        List<PeptideSpectrumMatchNoFDR> psmList = rankAndConvertResults(params, percParams, allResults);
        
        // 3. Get all the matching proteins
        assignMatchingProteins(psmList);
        
        
        long end = System.currentTimeMillis();
        log.info("Total time to get results: "+TimeUtils.timeElapsedMinutes(start, end)+" minutes \n");
        
        return psmList;
    }
    
    
    // Assign matching proteins to each peptide
    private void assignMatchingProteins(List<PeptideSpectrumMatchNoFDR> psmList) {
        
        MsSearchResultProteinDAO protDao = DAOFactory.instance().getMsProteinMatchDAO();
        
        long s = System.currentTimeMillis();
        // map of protein accession and protein
        Map<String, Protein> proteinMap = new HashMap<String, Protein>();
        
        for(PeptideSpectrumMatchNoFDR psm: psmList) {
            
            // read the matching proteins from the database now
            List<MsSearchResultProtein> msProteinList = protDao.loadResultProteins(psm.getHitId());

            for (MsSearchResultProtein protein: msProteinList) {
                // we could have multiple accessions, keep the first one only
                String[] accessionStrings = protein.getAccession().split("\\cA");
            
                Protein prot = proteinMap.get(accessionStrings[0]);
                // If we have not already seen this protein create a new entry
                if(prot == null) {
                    prot = new Protein(accessionStrings[0], -1);
                    proteinMap.put(accessionStrings[0], prot);
                }
                psm.getPeptideHit().addProtein(prot);
            }
        }
        
        long e = System.currentTimeMillis();
        log.info("\tTime to get matching proteins: "+TimeUtils.timeElapsedSeconds(s,e)+" seconds.");
    }

    // Convert the list of PercolatorResult to a list of PeptideSpectrumMatchNoFDR 
    // Results are ranked for each peptide. 
    private List<PeptideSpectrumMatchNoFDR> rankAndConvertResults(IDPickerParams params, PercolatorParams percParams,
            List<PercolatorResult> allResults) {
        
        long s = System.currentTimeMillis();
        // Rank the Percolator results
        Map<Integer, Integer> resultRanks = rankResults(allResults, percParams);
        
        // make a list of peptide spectrum matches and read the matching proteins from the database
        PeptideDefinition peptideDef = params.getPeptideDefinition();
        
        // map of peptide_key and peptideHit
        Map<String, PeptideHit> peptideHitMap = new HashMap<String, PeptideHit>();
        
        // convert the list of PercolatorResult into a list of PeptideSpectrumMatchNoFDR objects
        List<PeptideSpectrumMatchNoFDR> psmList = new ArrayList<PeptideSpectrumMatchNoFDR>(allResults.size());
        Iterator<PercolatorResult> iter = allResults.iterator();
        while(iter.hasNext()) {

            PercolatorResult result = iter.next();
            
            // get the peptide key
            String peptideKey = PeptideKeyCalculator.getKey(result, peptideDef);
            
            PeptideHit peptHit = peptideHitMap.get(peptideKey);
            // If we haven't already seen this peptide, create a new entry
            if(peptHit == null) {
                Peptide peptide = new Peptide(result.getResultPeptide().getPeptideSequence(), peptideKey, -1);
                peptHit = new PeptideHit(peptide);
                peptideHitMap.put(peptideKey, peptHit);
            }
            
            SpectrumMatchNoFDRImpl specMatch = new SpectrumMatchNoFDRImpl();
            specMatch.setHitId(result.getId());
            specMatch.setScanId(result.getScanId());
            specMatch.setCharge(result.getCharge());
            specMatch.setSourceId(result.getRunSearchAnalysisId());
            specMatch.setModifiedSequence(result.getResultPeptide().getModifiedPeptide());
            specMatch.setRank(resultRanks.get(result.getId()));

            PeptideSpectrumMatchNoFDRImpl psm = new PeptideSpectrumMatchNoFDRImpl();
            psm.setPeptide(peptHit);
            psm.setSpectrumMatch(specMatch);

            psmList.add(psm);
            
            // remove the original result to free up space.
            result = null;
            iter.remove();
        }
        
        // free up unused maps
        allResults.clear();  allResults = null;
        resultRanks.clear(); resultRanks = null;
        peptideHitMap.clear(); peptideHitMap = null;
        
        long e = System.currentTimeMillis();
        log.info("\tTime to rank peptide spectra and create list of spectrum matches: "+TimeUtils.timeElapsedSeconds(s,e)+" seconds.");
        e = System.currentTimeMillis();
        return psmList;
    }

    // Returns a list of PercolatorResults for the given inputIds, filtered by relevant scores and 
    // min. peptide length.
    private List<PercolatorResult> getAllPercolatorResults(List<IdPickerInput> inputList, 
            Program inputGenerator,
            PercolatorParams percParams) {
        
        PercolatorResultDAO resultDao = DAOFactory.instance().getPercolatorResultDAO();
        
        List<PercolatorResult> allResults = new ArrayList<PercolatorResult>();
        
        Double qvalCutoff = percParams.hasQvalueCutoff() ? percParams.getQvalueCutoff() : null;
        Double pepCutoff = percParams.hasPepCutoff() ? percParams.getPEPCutoff() : null;
        Double dsCutoff = percParams.hasDiscriminantScoreCutoff() ? percParams.getDiscriminantScoreCutoff() : null;
        
        log.info("Thresholds -- qvalue: "+qvalCutoff+"; pep: "+pepCutoff+" ds: "+dsCutoff);
        
        // first get all the results; remove all hits to small peptides; results will be filtered by relevant scores.
        for(IdPickerInput input: inputList) {
            
            int inputId = input.getInputId();
            
            log.info("Loading Percolator results for runSearchAnalysisID: "+inputId);

            long s = System.currentTimeMillis();
            List<PercolatorResult> resultList = resultDao.loadTopPercolatorResultsN(inputId, 
                                            qvalCutoff, 
                                            pepCutoff, 
                                            dsCutoff,
                                            true); // get the dynamic residue mods
            log.info("\tTotal hits that pass score thresholds for runSearchAnalysisID "+inputId+": "+resultList.size());
            long e = System.currentTimeMillis();

            log.info("\tTime: "+TimeUtils.timeElapsedSeconds(s,e)+" seconds.");
        
            // Remove search hits to small peptides
            removeSmallPeptides(resultList, percParams.getIdPickerParams().getMinPeptideLength());
            
            
            // We are not going to calculate FDR so we remove all spectra with multiple results at this point
            // Our search results should already be filtered at this point
            if(percParams.getIdPickerParams().isRemoveAmbiguousSpectra()) {
                removeSpectraWithMultipleResults(resultList);
            }
            
            allResults.addAll(resultList);
            
            input.setNumFilteredTargetHits(resultList.size());
            input.setNumTargetHits(IdPickerInputGetter.instance().getUnfilteredInputCount(inputId, inputGenerator));
        }
        return allResults;
    }
    
    
    protected void removeSpectraWithMultipleResults(List<PercolatorResult> psmList) {
        
        long s = System.currentTimeMillis();
        // sort by scanID
        Collections.sort(psmList, new Comparator<PercolatorResult>() {
            public int compare(PercolatorResult o1, PercolatorResult o2) {
                return Integer.valueOf(o1.getScanId()).compareTo(o2.getScanId());
            }});
        
        // get a list of scan Ids that have multiple results
        Set<Integer> scanIdsToRemove = new HashSet<Integer>();
//        Set<Integer> allScanIds = new HashSet<Integer>();
        
        int lastScanId = -1;
        for (int i = 0; i < psmList.size(); i++) {
            PercolatorResult psm = psmList.get(i);
//            allScanIds.add(psm.getScanId());
            if(lastScanId != -1){
                if(lastScanId == psm.getScanId()) {
                    scanIdsToRemove.add(lastScanId);
                }
            }
            lastScanId = psm.getScanId();
        }
        
        Iterator<PercolatorResult> iter = psmList.iterator();
        while(iter.hasNext()) {
            PercolatorResult psm = iter.next();
            if(scanIdsToRemove.contains(psm.getScanId())) {
//                log.info("Removing for scanID: "+psm.getScanId()+"; resultID: "+psm.getHitId());
                iter.remove();
            }
        }
        long e = System.currentTimeMillis();
//        log.info("\nRR\t"+runSearchAnalysisId+"\t"+allScanIds.size()+"\t"+scanIdsToRemove.size());
        log.info("Removed "+scanIdsToRemove.size()+" scans with multiple results. "+
                "Remaining results: "+psmList.size()+". Time: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds\n");
    }


    private void removeSmallPeptides(List<PercolatorResult> resultList, int minPeptideLength) {

        log.info("Removing search hits with peptide length < "+minPeptideLength);
        Iterator<PercolatorResult> iter = resultList.iterator();
        int removed = 0;
        while(iter.hasNext()) {
            PercolatorResult res = iter.next();
            // if the length of the peptide is less than the required threshold do not add it to the final list
            if(res.getResultPeptide().getPeptideSequence().length() < minPeptideLength) {
                iter.remove();
                removed++;
            }
        }
        log.info("\tRemoved "+removed+" spectra. Remaining spectra: "+resultList.size());
    }
    
    /**
     * PSM's are ranked for a peptide sequence (regardless of peptide definition).
     * @param resultList
     * @param percParams
     * @return
     */
    private Map<Integer, Integer> rankResults(List<PercolatorResult> resultList, PercolatorParams percParams) {
        
//        final PeptideDefinition peptideDef = percParams.getIdPickerParams().getPeptideDefinition(); 
   
        // sort the results by the peptide sequence (w/o mods)
        Collections.sort(resultList, new Comparator<PercolatorResult>() {
            @Override
            public int compare(PercolatorResult o1, PercolatorResult o2) {
                //return PeptideKeyCalculator.getKey(o1, peptideDef).compareTo(PeptideKeyCalculator.getKey(o2, peptideDef));
                return o1.getResultPeptide().getPeptideSequence().compareTo(o2.getResultPeptide().getPeptideSequence());
            }});
        
        // which score comparator will we use
        Comparator<PercolatorResult> scoreComparator = null;
        // we have both qvalue and PEP cutoff
        if(percParams.hasQvalueCutoff() && percParams.hasPepCutoff()) {
                scoreComparator = new PercolatorResultComparatorPEPQVal();
        }
        else if(percParams.hasQvalueCutoff() && percParams.hasDiscriminantScoreCutoff()) {
            scoreComparator = new PercolatorResultComparatorDSQVal();
        }
        else if(percParams.hasPepCutoff() && !percParams.hasQvalueCutoff()) {
            scoreComparator = new PercolatorResultComparatorPEP();
        }
        else if(percParams.hasDiscriminantScoreCutoff() && !percParams.hasQvalueCutoff()) {
            scoreComparator = new PercolatorResultComparatorDS();
        }
        else {
            scoreComparator = new PercolatorResultComparatorQVal();
        }
        
        
        // Map of resultID and rank
        Map<Integer, Integer> resultRankMap = new HashMap<Integer, Integer>((int)(resultList.size()*1.5));
        
        List<PercolatorResult> resForPeptide = new ArrayList<PercolatorResult>();
        String lastPeptide = null;
        for(PercolatorResult result: resultList) {
            
            if(!result.getResultPeptide().getPeptideSequence().equals(lastPeptide)) {
                if(lastPeptide != null) {
                    Collections.sort(resForPeptide, scoreComparator);
                    int rank = 1;
                    for(PercolatorResult res: resForPeptide) {
                        resultRankMap.put(res.getId(), rank); rank++;
                    }
                }
                resForPeptide.clear();
                lastPeptide = result.getResultPeptide().getPeptideSequence();
            }
            resForPeptide.add(result);
        }
        // last one
        Collections.sort(resForPeptide, scoreComparator);
        int rank = 1;
        for(PercolatorResult res: resForPeptide) {
            resultRankMap.put(res.getId(), rank); rank++;
        }
        return resultRankMap;
    }
    
    private static final class PercolatorResultComparatorQVal implements Comparator<PercolatorResult> {
        @Override
        public int compare(PercolatorResult o1, PercolatorResult o2) {
            return Double.valueOf(o1.getQvalue()).compareTo(o2.getQvalue());
        }
    }
    
    private static final class PercolatorResultComparatorPEP implements Comparator<PercolatorResult> {
        @Override
        public int compare(PercolatorResult o1, PercolatorResult o2) {
            return Double.valueOf(o1.getPosteriorErrorProbability()).compareTo(o2.getPosteriorErrorProbability());
        }
    }
    
    private static final class PercolatorResultComparatorPEPQVal implements Comparator<PercolatorResult> {
        @Override
        public int compare(PercolatorResult o1, PercolatorResult o2) {
            int val = Double.valueOf(o1.getQvalue()).compareTo(o2.getQvalue());
            if(val != 0)    return val;
            return Double.valueOf(o1.getPosteriorErrorProbability()).compareTo(o2.getPosteriorErrorProbability());
        }
    }
    
    private static final class PercolatorResultComparatorDS implements Comparator<PercolatorResult> {
        @Override
        public int compare(PercolatorResult o1, PercolatorResult o2) {
            return Double.valueOf(o1.getDiscriminantScore()).compareTo(o2.getDiscriminantScore());
        }
    }
    
    private static final class PercolatorResultComparatorDSQVal implements Comparator<PercolatorResult> {
        @Override
        public int compare(PercolatorResult o1, PercolatorResult o2) {
            int val = Double.valueOf(o1.getQvalue()).compareTo(o2.getQvalue());
            if(val != 0)    return val;
            return Double.valueOf(o1.getDiscriminantScore()).compareTo(o2.getDiscriminantScore());
        }
    }
    
}
