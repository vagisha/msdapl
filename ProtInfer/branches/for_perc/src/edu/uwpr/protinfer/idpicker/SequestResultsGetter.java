/**
 * SequestResultsGetter.java
 * @author Vagisha Sharma
 * Jan 2, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;

import edu.uwpr.protinfer.database.dto.idpicker.IdPickerInput;
import edu.uwpr.protinfer.infer.PeptideHit;
import edu.uwpr.protinfer.util.TimeUtils;

/**
 * 
 */
public class SequestResultsGetter extends SearchResultsGetter<SequestSearchResult> {

    private static final Logger log = Logger.getLogger(SequestResultsGetter.class);
    
    private static final SequestResultsGetter instance = new SequestResultsGetter();
    
    private SequestResultsGetter() {}
    
    public static SequestResultsGetter instance() {
        return instance;
    }
    

    PeptideSpectrumMatchIDP createPeptideSpectrumMatch(SequestSearchResult result, PeptideHit peptHit) {
        
        SequestResultData scores = result.getSequestResultData();
        
        SpectrumMatchIDPImpl specMatch = new SpectrumMatchIDPImpl();
        specMatch.setHitId(result.getId());
        specMatch.setScanId(result.getScanId());
        specMatch.setCharge(result.getCharge());
        specMatch.setSourceId(result.getRunSearchId());
        specMatch.setModifiedSequence(result.getResultPeptide().getModifiedPeptide());
//            specMatch.setRank(scores.getxCorrRank()); // Rank will be based on calculated FDR
        
        PeptideSpectrumMatchIDPImpl psm = new PeptideSpectrumMatchIDPImpl();
        psm.setPeptide(peptHit);
        psm.setSpectrumMatch(specMatch);
        psm.setAbsoluteScore(scores.getxCorr().doubleValue());
        psm.setRelativeScore(scores.getDeltaCN().doubleValue());
        return psm;
    }
    
    
    List<SequestSearchResult> getAllSearchResults(List<IdPickerInput> inputList, Program inputGenerator,  IDPickerParams params) {
        
        
        SequestSearchResultDAO resultDao = DAOFactory.instance().getSequestResultDAO();
        
        List<SequestSearchResult> allResults = new ArrayList<SequestSearchResult>();
        
        for(IdPickerInput input: inputList) {
            
            int inputId = input.getInputId();
            log.info("Loading top hits for runSearchID: "+inputId);
            
            long start = System.currentTimeMillis();
            long s = start;
            List<SequestSearchResult> resultList = resultDao.loadTopResultsForRunSearchN(inputId, true); // get modifications
            log.info("\tTotal top hits for "+inputId+": "+resultList.size());
            long e = System.currentTimeMillis();
            
            log.info("\tTime: "+TimeUtils.timeElapsedSeconds(s,e)+" seconds.");
            
            allResults.addAll(resultList);
        }
        
        return allResults;
    }

}
