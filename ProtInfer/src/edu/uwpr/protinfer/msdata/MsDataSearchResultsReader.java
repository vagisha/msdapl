package edu.uwpr.protinfer.msdata;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.domain.run.MsRun;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;

import edu.uwpr.protinfer.PeptideHit;
import edu.uwpr.protinfer.PeptideModification;
import edu.uwpr.protinfer.Protein;
import edu.uwpr.protinfer.ProteinHit;
import edu.uwpr.protinfer.SequestHit;
import edu.uwpr.protinfer.SearchSource;

public class MsDataSearchResultsReader {

    public List<SequestHit> getHitsForExperiment(int experimentId) {
        return null;
    }
    
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
            double xcorr = scores.getxCorr().doubleValue();
            
            // get the peptide
            MsSearchResultPeptide msPeptide = result.getResultPeptide();
            PeptideHit peptHit = new PeptideHit(msPeptide.getPeptideSequence());
            
            // get the modifications for the peptide
            List<MsResultResidueMod> residueMods = msPeptide.getResultDynamicResidueModifications();
            for (MsResultResidueMod mod: residueMods) {
                peptHit.addModification(new PeptideModification(mod.getModifiedPosition(), mod.getModificationMass()));
            }
            
            // get the proteins
            List<MsSearchResultProtein> msProteinList = result.getProteinMatchList();
            for (MsSearchResultProtein protein: msProteinList) {
                Protein prot = new Protein(protein.getAccession(), 0); // don't have the nrseq database id here
                peptHit.addProteinHit(new ProteinHit(prot, '\u0000', '\u0000'));
            }
            
            SequestHit hit = new SequestHit(source, scanNumber, charge, xcorr, peptHit);
            hit.setHitId(resultId);
            hit.setScanId(scanId);
            searchHits.add(hit);
        }
        return searchHits;
    }
    
    private List<SequestHit> loadHitsForProlucidSearch(int runSearchId, SearchSource source) {
        // TODO to be implemented
        return null; 
    }
    
    private String getRunFileName(int runId) {
        MsRunDAO runDao = DAOFactory.instance().getMsRunDAO();
        MsRun run = runDao.loadRun(runId);
        String fileName = run.getFileName();
        int idx = fileName.lastIndexOf('.');
        fileName = fileName.substring(0, idx);
        return fileName;
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
