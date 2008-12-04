package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.dao.search.prolucid.ProlucidSearchResultDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.prolucid.ProlucidResultData;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResult;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;

import edu.uwpr.protinfer.infer.Peptide;
import edu.uwpr.protinfer.infer.PeptideHit;
import edu.uwpr.protinfer.infer.Protein;
import edu.uwpr.protinfer.infer.ProteinHit;

public class SearchResultsGetter {

    private static final Logger log = Logger.getLogger(SearchResultsGetter.class);
    
    private static SearchResultsGetter instance = new SearchResultsGetter();
    
    private SearchResultsGetter() {}
    
    public static final SearchResultsGetter instance() {
        return instance;
    }
    
    public List<PeptideSpectrumMatchIDP> getHitsForRunSearch(int runSearchId, String decoyPrefix,
            SearchFileFormat format) {
        
        log.info("Reading hits for runSearchId: "+runSearchId+"; Program: "+format.getFormatType());
        
        if (format == SearchFileFormat.SQT_NSEQ || format == SearchFileFormat.SQT_SEQ) {
            return loadHitsForSequestSearch(runSearchId, decoyPrefix);
        }
        else if (format == SearchFileFormat.SQT_PLUCID) {
            return loadHitsForProlucidSearch(runSearchId, decoyPrefix);
        }
        else
            return null;
    }
    
    public static float timeElapsed(long start, long end) {
        return (end - start)/(1000.0f);
    }
    
    private List<PeptideSpectrumMatchIDP> loadHitsForSequestSearch(int runSearchId, String decoyPrefix) {
        
        SequestSearchResultDAO resultDao = DAOFactory.instance().getSequestResultDAO();
        MsSearchResultProteinDAO protDao = DAOFactory.instance().getMsProteinMatchDAO();
        
        log.info("Loading top hits for runSearchID: "+runSearchId);
        
        long start = System.currentTimeMillis();
        long s = start;
        List<SequestSearchResult> resultList = resultDao.loadTopResultsForRunSearchN(runSearchId);
        log.info("\tTotal top hits for "+runSearchId+": "+resultList.size());
        long e = System.currentTimeMillis();
        
        log.info("\tTime: "+timeElapsed(s,e));
        
       
        List<PeptideSpectrumMatchIDP> psmList = new ArrayList<PeptideSpectrumMatchIDP>(resultList.size());
        for (SequestSearchResult result: resultList) {
            
            SequestResultData scores = result.getSequestResultData();
            
            // get the peptide
            Peptide peptide = new Peptide(result.getResultPeptide().getPeptideSequence(), -1);
            PeptideHit peptHit = new PeptideHit(peptide);
            
            // read the matching proteins from the database now
            s = System.currentTimeMillis();
            List<MsSearchResultProtein> msProteinList = protDao.loadResultProteins(result.getId());
            e = System.currentTimeMillis();
            log.info("\tTime to get matching proteins: "+timeElapsed(s, e));
           
            for (MsSearchResultProtein protein: msProteinList) {
                Protein prot = new Protein(protein.getAccession(), -1);
                if (prot.getAccession().startsWith(decoyPrefix))
                    prot.setDecoy();
                peptHit.addProteinHit(new ProteinHit(prot, '\u0000', '\u0000'));
            }
            
            SpectrumMatchIDPImpl specMatch = new SpectrumMatchIDPImpl();
            specMatch.setHitId(result.getId());
            specMatch.setScanId(result.getScanId());
            specMatch.setCharge(result.getCharge());
            specMatch.setSourceId(runSearchId);
            
            PeptideSpectrumMatchIDPImpl psm = new PeptideSpectrumMatchIDPImpl();
            psm.setPeptide(peptHit);
            psm.setSpectrumMatchMatch(specMatch);
            psm.setAbsoluteScore(scores.getxCorr().doubleValue());
            psm.setRelativeScore(scores.getDeltaCN().doubleValue());
            
            psmList.add(psm);
        }
        e = System.currentTimeMillis();
        log.info("Total time: "+timeElapsed(start, e));
        return psmList;
    }
    
    private List<PeptideSpectrumMatchIDP> loadHitsForProlucidSearch(int runSearchId, String decoyPrefix) {
        
        ProlucidSearchResultDAO resultDao = DAOFactory.instance().getProlucidResultDAO();
        MsSearchResultProteinDAO protDao = DAOFactory.instance().getMsProteinMatchDAO();
        
        log.info("Loading top hits for runSearchID: "+runSearchId);
        
        long start = System.currentTimeMillis();
        long s = start;
        List<ProlucidSearchResult> resultList = resultDao.loadTopResultsForRunSearchN(runSearchId);
        log.info("\tTotal top hits for "+runSearchId+": "+resultList.size());
        long e = System.currentTimeMillis();
        
        log.info("\tTime: "+timeElapsed(s, e));
        
       
        List<PeptideSpectrumMatchIDP> psmList = new ArrayList<PeptideSpectrumMatchIDP>(resultList.size());
        for (ProlucidSearchResult result: resultList) {
            
            ProlucidResultData scores = result.getProlucidResultData();
            
            // get the peptide
            Peptide peptide = new Peptide(result.getResultPeptide().getPeptideSequence(), -1);
            PeptideHit peptHit = new PeptideHit(peptide);
            
            // read the matching proteins from the database now
            s = System.currentTimeMillis();
            List<MsSearchResultProtein> msProteinList = protDao.loadResultProteins(result.getId());
            e = System.currentTimeMillis();
            log.info("\tTime to get matching proteins: "+timeElapsed(s, e));
           
            for (MsSearchResultProtein protein: msProteinList) {
                Protein prot = new Protein(protein.getAccession(), -1);
                if (prot.getAccession().startsWith(decoyPrefix))
                    prot.setDecoy();
                peptHit.addProteinHit(new ProteinHit(prot, '\u0000', '\u0000'));
            }
            
            SpectrumMatchIDPImpl specMatch = new SpectrumMatchIDPImpl();
            specMatch.setHitId(result.getId());
            specMatch.setScanId(result.getScanId());
            specMatch.setCharge(result.getCharge());
            specMatch.setSourceId(runSearchId);
            
            PeptideSpectrumMatchIDPImpl psm = new PeptideSpectrumMatchIDPImpl();
            psm.setPeptide(peptHit);
            psm.setSpectrumMatchMatch(specMatch);
            psm.setAbsoluteScore(scores.getPrimaryScore().doubleValue());
            psm.setRelativeScore(scores.getDeltaCN().doubleValue());
            
            psmList.add(psm);
        }
        e = System.currentTimeMillis();
        log.info("Total time: "+timeElapsed(start, e));
        return psmList;
    }
}
