package edu.uwpr.protinfer.msdata;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;

import edu.uwpr.protinfer.SequestHit;
import edu.uwpr.protinfer.infer.ModifiedPeptide;
import edu.uwpr.protinfer.infer.Peptide;
import edu.uwpr.protinfer.infer.PeptideHit;
import edu.uwpr.protinfer.infer.PeptideSpectrumMatch;
import edu.uwpr.protinfer.infer.Protein;
import edu.uwpr.protinfer.infer.ProteinHit;
import edu.uwpr.protinfer.infer.SearchSource;
import edu.uwpr.protinfer.infer.SpectrumMatch;

public class MsDataSearchResultsReader {

    private Map<String, Peptide> peptideIds = new HashMap<String, Peptide>();
    private Map<String, Protein> proteinIds = new HashMap<String, Protein>();
    private int lastPeptideId = 0;
    private int lastProteinId = 0;
    
    public List<SequestHit> getHitsForRunSearch(int runSearchId) {
        MsRunSearchDAO runSearchDao = DAOFactory.instance().getMsRunSearchDAO();
        MsRunSearch runSearch = runSearchDao.loadRunSearch(runSearchId);
        String fileName = getRunFileName(runSearch.getRunId());
        
        SearchSource source = new SearchSource(fileName, runSearchId);
        
        SearchFileFormat format = runSearch.getSearchFileFormat();
        if (format == SearchFileFormat.SQT_NSEQ || format == SearchFileFormat.SQT_SEQ) {
            return loadHitsForSequestSearch(runSearchId, source);
        }
        else if (format == SearchFileFormat.SQT_PLUCID) {
            return loadHitsForProlucidSearch(runSearchId, source);
        }
        else
            return null;
    }
    
    private List<SequestHit> loadHitsForSequestSearch(int runSearchId, SearchSource source) {
        SequestSearchResultDAO resultDao = DAOFactory.instance().getSequestResultDAO();
        List<Integer> resultIds = resultDao.loadResultIdsForRunSearch(runSearchId);
        List<SequestHit> searchHits = new ArrayList<SequestHit>(resultIds.size());
        for (Integer resultId: resultIds) {
            SequestSearchResult result = resultDao.load(resultId);
            
            SequestResultData scores = result.getSequestResultData();
            int xcorrRank = scores.getxCorrRank();
            if (xcorrRank != 1)
                continue; // we want only top hits
            int scanId = result.getScanId();
            int charge = result.getCharge();
            int scanNumber = getScanNumber(scanId);
            
            // get the peptide
            PeptideHit peptHit = getPeptideHit(result.getResultPeptide());
            
//          // get the proteins
//            List<MsSearchResultProtein> msProteinList = result.getProteinMatchList();
//            for (MsSearchResultProtein protein: msProteinList) {
//                Protein prot = new Protein(protein.getAccession(), 0); // don't have the nrseq database id here
//                peptHit.addProteinHit(new ProteinHit(prot, '\u0000', '\u0000'));
//            }
            
            SequestHit hit = new SequestHit(source, scanNumber, charge, peptHit);
            hit.setHitId(resultId);
            hit.setScanId(scanId);
            hit.setXcorr(scores.getxCorr());
            hit.setDeltaCn(scores.getDeltaCN());
            
            searchHits.add(hit);
        }
        return searchHits;
    }
    
    public void loadProteinsForHits(List<? extends PeptideSpectrumMatch<? extends SpectrumMatch>> hits) {
        MsSearchResultProteinDAO protDao = DAOFactory.instance().getMsProteinMatchDAO();
        for(PeptideSpectrumMatch<? extends SpectrumMatch> match: hits) {
            int resultId = match.getSpectrumMatch().getHitId();
            List<MsSearchResultProtein> proteins = protDao.loadResultProteins(resultId);
            PeptideHit peptHit = match.getPeptideHit();
            for (MsSearchResultProtein msProt: proteins) {
                Protein prot = proteinIds.get(msProt.getAccession());
                if (prot == null) {
                    prot = new Protein(msProt.getAccession(), lastProteinId++);
                    peptHit.addProteinHit(new ProteinHit(prot, '\u0000', '\u0000'));
                }
            }
        }
    }
    
    private PeptideHit getPeptideHit(MsSearchResultPeptide resultPeptide) {
        String sequence = resultPeptide.getPeptideSequence();
        Peptide peptide = peptideIds.get(sequence);
        if (peptide == null) {
            peptideIds.put(sequence, new Peptide(sequence, lastPeptideId++));
        }
        
        // At this point we are not adding any modifications or protein matches
        ModifiedPeptide modPeptide = new ModifiedPeptide(peptide);
        
        // get the modifications for the peptide
//        List<MsResultResidueMod> residueMods = resultPeptide.getResultDynamicResidueModifications();
//        for (MsResultResidueMod mod: residueMods) {
//            modPeptide.addModification(new PeptideModification(mod.getModifiedPosition(), mod.getModificationMass()));
//        }
        
        return new PeptideHit(modPeptide);
    }

    private List<SequestHit> loadHitsForProlucidSearch(int runSearchId, SearchSource source) {
        // TODO to be implemented
        return null; 
    }
    
    private String getRunFileName(int runId) {
        MsRunDAO runDao = DAOFactory.instance().getMsRunDAO();
        return runDao.loadFilenameNoExtForRun(runId);
    }
    
    private int getScanNumber(int scanId) {
        MsScanDAO scanDao = DAOFactory.instance().getMsScanDAO();
        MsScan scan = scanDao.load(scanId);
        return scan.getStartScanNum();
    }
    
    public static void main(String[] args) {
        int runSearchId = 118;
        MsDataSearchResultsReader reader = new MsDataSearchResultsReader();
        long start = System.currentTimeMillis();
        System.out.println("Start: "+new Date(start).toString());
        List<SequestHit> searchHits = reader.getHitsForRunSearch(runSearchId);
        long end = System.currentTimeMillis();
        System.out.println(("End: "+new Date(end).toString()));
        
        System.out.println("Number of hits found: "+searchHits.size());
    }
}
