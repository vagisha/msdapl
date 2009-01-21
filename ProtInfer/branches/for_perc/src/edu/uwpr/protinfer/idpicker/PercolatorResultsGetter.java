package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;

import edu.uwpr.protinfer.PeptideDefinition;
import edu.uwpr.protinfer.PeptideKeyCalculator;
import edu.uwpr.protinfer.infer.Peptide;
import edu.uwpr.protinfer.infer.PeptideHit;
import edu.uwpr.protinfer.infer.Protein;
import edu.uwpr.protinfer.infer.ProteinHit;
import edu.uwpr.protinfer.util.TimeUtils;

public class PercolatorResultsGetter implements ResultsGetter {

private static final Logger log = Logger.getLogger(IdPickerInputGetter.class);
    
    private static PercolatorResultsGetter instance = new PercolatorResultsGetter();
    
    private PercolatorResultsGetter() {}
    
    public static final PercolatorResultsGetter instance() {
        return instance;
    }
    
    @Override
    public List<PeptideSpectrumMatchIDP> getResults(int inputId, IDPickerParams params) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Returns a list of peptide spectrum matches which are filtered by relevant score(s)
     * and for min peptide length and 
     * ranked by relevant score(s) for each peptide (as defined in the PeptideDefinition). 
     */
    @Override
    public  List<PeptideSpectrumMatchNoFDR> getResultsNoFdr(int inputId, IDPickerParams params) {
        
        PercolatorParams percParams = new PercolatorParams(params);
        PercolatorResultDAO resultDao = DAOFactory.instance().getPercolatorResultDAO();
        MsSearchResultProteinDAO protDao = DAOFactory.instance().getMsProteinMatchDAO();

        log.info("Loading Percolator results for runSearchAnalysisID: "+inputId);

        long start = System.currentTimeMillis();
        long s = start;
        List<PercolatorResult> resultList = resultDao.loadResultsWithScoreThresholdForRunSearchAnalysis(inputId, 
                                            percParams.getQvalueCutoff(), 
                                            percParams.getPEPCutoff(), 
                                            percParams.getDiscriminantScoreCutoff());
        log.info("\tTotal hits that pass score thresholds for runSearchAnalysisID "+inputId+": "+resultList.size());
        long e = System.currentTimeMillis();

        log.info("\tTime: "+TimeUtils.timeElapsedSeconds(s,e)+" seconds.");
        
        // Remove search hits to small peptides
        removeSmallPeptides(resultList, params.getMinPeptideLength());

        // Rank the Percolator results
        Map<Integer, Integer> resultRanks = rankResults(resultList, percParams);
        
        // make a list of peptide spectrum matches and read the matching proteins from the database
        s = System.currentTimeMillis();

        PeptideDefinition peptideDef = params.getPeptideDefinition();
        
        List<PeptideSpectrumMatchNoFDR> psmList = new ArrayList<PeptideSpectrumMatchNoFDR>(resultList.size());
        for (PercolatorResult result: resultList) {

            // get the peptide
            String peptideKey = PeptideKeyCalculator.getKey(result, peptideDef);
            Peptide peptide = new Peptide(result.getResultPeptide().getPeptideSequence(), peptideKey, -1);
            
            PeptideHit peptHit = new PeptideHit(peptide);

            // read the matching proteins from the database now
            List<MsSearchResultProtein> msProteinList = protDao.loadResultProteins(result.getId());

            for (MsSearchResultProtein protein: msProteinList) {
                String[] accessionStrings = protein.getAccession().split("\\cA");

                for(String accession: accessionStrings) {
                    Protein prot = new Protein(accession, -1);
                    peptHit.addProteinHit(new ProteinHit(prot, '\u0000', '\u0000'));

                }
            }
            SpectrumMatchNoFDRImpl specMatch = new SpectrumMatchNoFDRImpl();
            specMatch.setHitId(result.getId());
            specMatch.setScanId(result.getScanId());
            specMatch.setCharge(result.getCharge());
            specMatch.setSourceId(inputId);
            specMatch.setModifiedSequence(result.getResultPeptide().getModifiedPeptide());
            specMatch.setRank(resultRanks.get(result.getId()));

            PeptideSpectrumMatchNoFDRImpl psm = new PeptideSpectrumMatchNoFDRImpl();
            psm.setPeptide(peptHit);
            psm.setSpectrumMatch(specMatch);

            psmList.add(psm);
        }
        e = System.currentTimeMillis();
        log.info("\tTime to get matching proteins and create list of spectrum matches: "+TimeUtils.timeElapsedSeconds(s,e)+" seconds.");
        e = System.currentTimeMillis();
        log.info("Total time: "+TimeUtils.timeElapsedSeconds(start, e)+"\n");
        return psmList;
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

    private Map<Integer, Integer> rankResults(List<PercolatorResult> resultList, PercolatorParams percParams) {
        
        final PeptideDefinition peptideDef = percParams.getIdPickerParams().getPeptideDefinition(); 
   
        // sort the results by peptide key based on the peptide definition
        Collections.sort(resultList, new Comparator<PercolatorResult>() {
            @Override
            public int compare(PercolatorResult o1, PercolatorResult o2) {
                return PeptideKeyCalculator.getKey(o1, peptideDef).compareTo(PeptideKeyCalculator.getKey(o2, peptideDef));
                
            }});
        
        // which score comparator will we use
        Comparator<PercolatorResult> scoreComparator = null;
        if(percParams.getQvalueCutoff() != -1.0 && percParams.getPEPCutoff() != -1.0) {
                scoreComparator = new PercolatorResultComparatorPEPQVal();
        }
        else if(percParams.getQvalueCutoff() != -1.0 && percParams.getDiscriminantScoreCutoff() != -1.0) {
            scoreComparator = new PercolatorResultComparatorDSQVal();
        }
        else if(percParams.getPEPCutoff() != -1.0 && percParams.getQvalueCutoff() == -1.0) {
            scoreComparator = new PercolatorResultComparatorPEP();
        }
        else if(percParams.getDiscriminantScoreCutoff() != -1.0 && percParams.getQvalueCutoff() == -1.0) {
            scoreComparator = new PercolatorResultComparatorDS();
        }
        scoreComparator = new PercolatorResultComparatorQVal();
        
        
        // Map of resultID and rank
        Map<Integer, Integer> resultRankMap = new HashMap<Integer, Integer>(resultList.size());
        
        List<PercolatorResult> resForPeptide = new ArrayList<PercolatorResult>();
        String lastPeptideKey = null;
        for(PercolatorResult result: resultList) {
            
            if(!PeptideKeyCalculator.getKey(result, peptideDef).equals(lastPeptideKey)) {
                if(lastPeptideKey != null) {
                    Collections.sort(resForPeptide, scoreComparator);
                    int rank = 1;
                    for(PercolatorResult res: resForPeptide) {
                        resultRankMap.put(res.getId(), rank); rank++;
                    }
                }
                resForPeptide.clear();
                lastPeptideKey = PeptideKeyCalculator.getKey(result, peptideDef);
            }
            resForPeptide.add(result);
        }
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
