package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorPeptideResultDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.dao.analysis.percolator.ibatis.PercolatorPeptideResultDAOImpl;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerInput;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerRun;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;

import edu.uwpr.protinfer.PeptideKeyCalculator;
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
     * @throws ModifiedSequenceBuilderException 
     */
    @Override
    public  List<PeptideSpectrumMatchNoFDR> getResultsNoFdr(IdPickerRun run, IDPickerParams params) 
        throws ModifiedSequenceBuilderException {
        
        return getResultsNoFdr(run.getInputList(), run.getInputGenerator(), params);
    }
    
    
    @Override
    public List<PeptideSpectrumMatchNoFDR> getResultsNoFdr(List<IdPickerInput> inputList, Program inputGenerator,
            IDPickerParams params) throws ModifiedSequenceBuilderException {
        
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
            List<MsSearchResultProtein> msProteinList = protDao.loadResultProteins(psm.getSearchResultId());

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
            List<PercolatorResult> allResults) throws ModifiedSequenceBuilderException {
        
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
            specMatch.setResultId(result.getPercolatorResultId());
            specMatch.setSearchResultId(result.getId());
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
        log.info("Number of ions seen: "+peptideHitMap.size());
        
        // free up unused maps
        allResults.clear();  allResults = null;
        resultRanks.clear(); resultRanks = null;
        peptideHitMap.clear(); peptideHitMap = null;
        
        long e = System.currentTimeMillis();
        log.info("\tTime to rank peptide spectra and create list of spectrum matches: "+TimeUtils.timeElapsedSeconds(s,e)+" seconds.");
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
            
            List<PercolatorResult> resultList = null;
            if(!percParams.isUsePeptideLevelScores()) {
            	log.info("Filtering on PSM scores");
            	resultList = resultDao.loadTopPercolatorResultsN(inputId, 
                                            qvalCutoff, 
                                            pepCutoff, 
                                            dsCutoff,
                                            true); // get the dynamic residue mods
            }
            else {
            	log.info("Filtering on peptide scores");
            	PercolatorPeptideResultDAO peptResDao = DAOFactory.instance().getPercolatorPeptideResultDAO();
            	resultList = peptResDao.loadPercolatorPsms(inputId, qvalCutoff, pepCutoff, dsCutoff);
            }
            
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
        AmbiguousSpectraFilter specFilter = AmbiguousSpectraFilter.instance();
        specFilter.filterSpectraWithMultipleResults(psmList);
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
        
        PercolatorResultsRanker resultRanker = PercolatorResultsRanker.instance();
        return resultRanker.rankResultsByPeptide(resultList, percParams.hasQvalueCutoff(),
                percParams.hasPepCutoff(), percParams.hasDiscriminantScoreCutoff());
    }
    
}
