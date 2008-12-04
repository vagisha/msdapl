package edu.uwpr.protinfer.msdata;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
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
import edu.uwpr.protinfer.infer.Peptide;
import edu.uwpr.protinfer.infer.PeptideHit;
import edu.uwpr.protinfer.infer.Protein;
import edu.uwpr.protinfer.infer.ProteinHit;
import edu.uwpr.protinfer.infer.SearchSource;

public class MsDataSearchResultsReader {

    private static final Logger log = Logger.getLogger(MsDataSearchResultsReader.class);

    
    public List<SequestHit> getHitsForRunSearch(int runSearchId, String decoyPrefix) {
        log.info("Reading hits for runSearchId: "+runSearchId);
        
        MsRunSearchDAO runSearchDao = DAOFactory.instance().getMsRunSearchDAO();
        MsRunSearch runSearch = runSearchDao.loadRunSearch(runSearchId);
        String fileName = getRunFileName(runSearch.getRunId());
        
        SearchSource source = new SearchSource(fileName, runSearchId);
        
        SearchFileFormat format = runSearch.getSearchFileFormat();
        if (format == SearchFileFormat.SQT_NSEQ || format == SearchFileFormat.SQT_SEQ) {
            return loadHitsForSequestSearch(runSearchId, source, decoyPrefix);
        }
        else if (format == SearchFileFormat.SQT_PLUCID) {
            return loadHitsForProlucidSearch(runSearchId, source);
        }
        else
            return null;
    }
    
    private List<SequestHit> loadHitsForSequestSearch(int runSearchId, SearchSource source, String decoyPrefix) {
        
        SequestSearchResultDAO resultDao = DAOFactory.instance().getSequestResultDAO();
        MsSearchResultProteinDAO protDao = DAOFactory.instance().getMsProteinMatchDAO();
        
        try {
            Class.forName( "com.mysql.jdbc.Driver" );
        }
        catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        log.info("Loading top hits...");
        Date s = new Date();
        Date start = new Date();
        List<SequestSearchResult> resultList = resultDao.loadTopResultsForRunSearchN(runSearchId);
        log.info("Total top hits for "+runSearchId+": "+resultList.size());
        Date end = new Date();
        log.info("Time: "+((end.getTime() - start.getTime())/(1000.0f)));
        
        start = new Date();
        List<SequestHit> searchHits = new ArrayList<SequestHit>(resultList.size());
        for (SequestSearchResult result: resultList) {
            
            SequestResultData scores = result.getSequestResultData();
            int xcorrRank = scores.getxCorrRank();
//            if (xcorrRank != 1)
//                continue; // we want only top hits
            
            // get the peptide
            PeptideHit peptHit = getPeptideHit(result.getResultPeptide());
            
            // get the proteins
//            List<MsSearchResultProtein> msProteinList = result.getProteinMatchList();
            // need to load results separately
            List<MsSearchResultProtein> msProteinList = protDao.loadResultProteins(result.getId());
           
            for (MsSearchResultProtein protein: msProteinList) {
                Protein prot = new Protein(protein.getAccession(), -1);
                if (prot.getAccession().startsWith(decoyPrefix))
                    prot.setDecoy();
                peptHit.addProteinHit(new ProteinHit(prot, '\u0000', '\u0000'));
            }
            
//            int scanNumber = getScanNumber(scanId); THIS IS TIME CONSUMING
            SequestHit hit = new SequestHit(source, -1, result.getCharge(), peptHit);
            hit.setHitId(result.getId());
            hit.setScanId(result.getScanId());
            hit.setXcorr(scores.getxCorr());
            hit.setDeltaCn(scores.getDeltaCN());
            
            searchHits.add(hit);
        }
        end = new Date();
        
        log.info("Total rank 1 hits: "+searchHits.size());
        log.info("Time: "+((float)(end.getTime() - start.getTime())/(1000.0f)));
        log.info("Total time: "+((float)(end.getTime() - s.getTime())/(1000.0f)));
        return searchHits;
    }
    
    private PeptideHit getPeptideHit(MsSearchResultPeptide resultPeptide) {
        String sequence = resultPeptide.getPeptideSequence();
        Peptide peptide = new Peptide(sequence, -1);
        
        // At this point we are not adding any modifications or protein matches
        
        // get the modifications for the peptide
//        List<MsResultResidueMod> residueMods = resultPeptide.getResultDynamicResidueModifications();
//        for (MsResultResidueMod mod: residueMods) {
//            modPeptide.addModification(new PeptideModification(mod.getModifiedPosition(), mod.getModificationMass()));
//        }
        
        return new PeptideHit(peptide);
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
        int runSearchId = 10;
        MsDataSearchResultsReader reader = new MsDataSearchResultsReader();
        for (int i = 0; i < 10; i++) {
            long start = System.currentTimeMillis();
            System.out.println("Start: "+new Date(start).toString());
            List<SequestHit> searchHits = reader.getHitsForRunSearch(runSearchId, "Reverse_");
            long end = System.currentTimeMillis();
            System.out.println(("End: "+new Date(end).toString()));
            System.out.println("Number of hits found: "+searchHits.size());
        }
        
        
    }
}
