package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;

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

        Map<Integer, Integer> resultRanks = rankResults(resultList, percParams);
        
        // make a list of peptide spectrum matches and read the matching proteins from the database
        s = System.currentTimeMillis();

        List<PeptideSpectrumMatchNoFDR> psmList = new ArrayList<PeptideSpectrumMatchNoFDR>(resultList.size());
        for (PercolatorResult result: resultList) {

            // get the peptide
            Peptide peptide = new Peptide(result.getResultPeptide().getPeptideSequence(), -1);
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
            specMatch.setSequence(result.getResultPeptide().getModifiedPeptideSequence());
            specMatch.setRank(resultRanks.get(result.getId()));

            PeptideSpectrumMatchNoFDRImpl psm = new PeptideSpectrumMatchNoFDRImpl();
            psm.setPeptide(peptHit);
            psm.setSpectrumMatch(specMatch);

            psmList.add(psm);
        }
        e = System.currentTimeMillis();
        log.info("\tTime to get matching proteins and create list of spectrum matches: "+TimeUtils.timeElapsedSeconds(s,e)+" seconds.");
        e = System.currentTimeMillis();
        log.info("Total time: "+TimeUtils.timeElapsedSeconds(start, e));
        return psmList;
    }

    private Map<Integer, Integer> rankResults(List<PercolatorResult> resultList, PercolatorParams percParams) {
        
        // sort the results by peptide sequence
        Collections.sort(resultList, new Comparator<PercolatorResult>() {
            @Override
            public int compare(PercolatorResult o1, PercolatorResult o2) {
                return o1.getResultPeptide().getPeptideSequence().compareTo(o2.getResultPeptide().getPeptideSequence());
                
            }});
        
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
        
        
        Map<Integer, Integer> resultRankMap = new HashMap<Integer, Integer>(resultList.size());
        
        List<PercolatorResult> resForScanCharge = new ArrayList<PercolatorResult>();
        String lastPeptideSeq = null;
        for(PercolatorResult result: resultList) {
            
            if(!result.getResultPeptide().getModifiedPeptideSequence().equals(lastPeptideSeq)) {
                if(lastPeptideSeq != null) {
                    Collections.sort(resForScanCharge, scoreComparator);
                    int rank = 1;
                    for(PercolatorResult res: resForScanCharge) {
                        resultRankMap.put(res.getId(), rank); rank++;
                    }
                }
                resForScanCharge.clear();
                lastPeptideSeq = result.getResultPeptide().getModifiedPeptideSequence();
            }
            resForScanCharge.add(result);
        }
        Collections.sort(resForScanCharge, scoreComparator);
        int rank = 1;
        for(PercolatorResult res: resForScanCharge) {
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
