/**
 * IdPickerExecutorFDR.java
 * @author Vagisha Sharma
 * Jan 2, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.search.Program;

import edu.uwpr.protinfer.database.dto.idpicker.IdPickerInput;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerRun;
import edu.uwpr.protinfer.filter.Filter;
import edu.uwpr.protinfer.filter.FilterException;
import edu.uwpr.protinfer.filter.fdr.FdrCalculatorException;
import edu.uwpr.protinfer.filter.fdr.FdrFilterCriteria;
import edu.uwpr.protinfer.infer.InferredProtein;
import edu.uwpr.protinfer.infer.PeptideEvidence;
import edu.uwpr.protinfer.util.TimeUtils;

/**
 * 
 */
public class IdPickerExecutorFDR {

    private static Logger log = Logger.getLogger(IDPickerExecutor.class);
    
    public void execute(IdPickerRun idpRun, IDPickerParams params) throws Exception {
        
        // get the program used for the generating the input data
        // NOTE: WE ASSUME ALL THE GIVEN inputIds WERE SEARCHED/ANALYSED WITH THE SAME PROGRAM
        Program program = idpRun.getInputGenerator();
        
        // get all the search hits for the given inputIds
        List<PeptideSpectrumMatchIDP> allPsms = getAllSearchHits(idpRun, params);
        
        // filter the search hits
        List<PeptideSpectrumMatchIDP> filteredPsms;
        try {
            filteredPsms = filterSearchHits(allPsms, params, program);
        }
        catch (FdrCalculatorException e) {
            log.error("Error calculating FDR", e);
            throw new Exception(e);
        }
        catch (FilterException e) {
            log.error("Error filtering on fdr", e);
            throw new Exception(e);
        }
        
        if(filteredPsms == null || filteredPsms.size() == 0) {
            log.error("No filtered hits found!");
            throw new Exception("No filtered hits found!");
        }
        // Our search results should already be filtered at this point
        // so remove all spectra with multiple results
        if(params.isRemoveAmbiguousSpectra()) {
            IDPickerExecutor.removeSpectraWithMultipleResults(filteredPsms);
        }
        
        // update the summary statistics
        updateSummaryAfterFiltering(filteredPsms, idpRun);
        
        // assign ids to peptides and proteins(nrseq ids)
        IDPickerExecutor.assignIdsToPeptidesAndProteins(filteredPsms, program);
        
        // infer the proteins;
        List<InferredProtein<SpectrumMatchIDP>> proteins = IDPickerExecutor.inferProteins(filteredPsms, params);
        
        // rank spectrum matches (based on FDR)
        rankPeptideSpectrumMatches(proteins);
        
        // FINALLY save the results
        //IdPickerResultSaver.instance().saveResults(idpRun, proteins);
    }
    
    
    private List<PeptideSpectrumMatchIDP> getAllSearchHits(IdPickerRun idpRun, IDPickerParams params) {
        
        IdPickerInputGetter resGetter = IdPickerInputGetter.instance();
        List<PeptideSpectrumMatchIDP> allPsms = resGetter.getInput(idpRun, params);
        return allPsms;
    }
    
    private  List<PeptideSpectrumMatchIDP> filterSearchHits(List<PeptideSpectrumMatchIDP> searchHits, 
            IDPickerParams params, Program program) throws FdrCalculatorException, FilterException {
        
        Comparator<PeptideSpectrumMatchIDP> absScoreComparator = getAbsoluteScoreComparator(program);
        Comparator<PeptideSpectrumMatchIDP> relScoreComparator = getRelativeScoreComparator(program);
        
        return filterSearchHits(searchHits, params, program, absScoreComparator, relScoreComparator);
    }

    
    private  List<PeptideSpectrumMatchIDP> filterSearchHits(List<PeptideSpectrumMatchIDP> searchHits, 
                                                           IDPickerParams params, Program program,
                                                           Comparator<PeptideSpectrumMatchIDP> absoluteScoreComparator,
                                                           Comparator<PeptideSpectrumMatchIDP> relativeScoreComparator) 
    throws FdrCalculatorException, FilterException {

        long start = System.currentTimeMillis();
        long s = start;
        
        FdrCalculatorIdPicker<PeptideSpectrumMatchIDP> calculator = new FdrCalculatorIdPicker<PeptideSpectrumMatchIDP>();
        if(!params.useIdPickerFDRFormula()) {
            calculator.setUseIdPickerFdr(false);
        }
        
        calculator.setDecoyRatio(params.getDecoyRatio());

        // Calculate FDR from relative scores (e.g. DeltaCN) first.
        calculator.calculateFdr(searchHits, relativeScoreComparator);
        
        // Filter based on the given FDR cutoff
        FdrFilterCriteria filterCriteria = new FdrFilterCriteria(params.getMaxRelativeFdr());
        List<PeptideSpectrumMatchIDP> filteredHits = Filter.filter(searchHits, filterCriteria);
        long e = System.currentTimeMillis();
        log.info("Calculated FDR for relative scores + filtered in: "+TimeUtils.timeElapsedSeconds(s, e));

        // Clear the fdr scores for the filtered hits and calculate FDR from xCorr scores
        for (PeptideSpectrumMatchIDP hit: filteredHits)
            hit.setFdr(1.0);

        // Calculate FDR from absolute scores (e.g. XCorr)
        s = System.currentTimeMillis();
        
        // IDPicker separates charge states for calculating FDR using XCorr scores
        if(program == Program.SEQUEST || program == Program.EE_NORM_SEQUEST)
            calculator.separateChargeStates(true);
        
        calculator.calculateFdr(searchHits, absoluteScoreComparator);

        filterCriteria = new FdrFilterCriteria(params.getMaxAbsoluteFdr());
        filteredHits = Filter.filter(searchHits, filterCriteria);
        e = System.currentTimeMillis();
        log.info("Calculated FDR for absolute scores + filtered in: "+TimeUtils.timeElapsedSeconds(s, e));
        
        log.info("Total time for filtering: "+TimeUtils.timeElapsedSeconds(start, e));
        
        return filteredHits;
    }
    
    private Comparator<PeptideSpectrumMatchIDP> getAbsoluteScoreComparator(Program program) {
        if(program == Program.SEQUEST || program == Program.EE_NORM_SEQUEST) {
            // we will be comparing XCorr
            return new Comparator<PeptideSpectrumMatchIDP>() {
                public int compare(PeptideSpectrumMatchIDP o1, PeptideSpectrumMatchIDP o2) {
                    return Double.valueOf(o1.getAbsoluteScore()).compareTo(o2.getAbsoluteScore());
                }};
        }
        else if(program == Program.PROLUCID) {
            // TODO here we need to know what primary score is used by ProLuCID
            return null;
        }
        else {
            log.error("Unsupported search file format: "+program.toString());
            return null;
        }
    }
    
    private Comparator<PeptideSpectrumMatchIDP> getRelativeScoreComparator(Program program) {
        if(program == Program.SEQUEST || program == Program.EE_NORM_SEQUEST) {
            // we will be comparing DeltaCN -- 0.0 is be best score; 1.0 is worst
            return new Comparator<PeptideSpectrumMatchIDP>() {
                public int compare(PeptideSpectrumMatchIDP o1, PeptideSpectrumMatchIDP o2) {
                    return Double.valueOf(o2.getRelativeScore()).compareTo(o1.getRelativeScore());
                }};
        }
        else if(program == Program.PROLUCID) {
            // TODO here we need to know what primary score is used by ProLuCID
            return null;
        }
        else {
            log.error("Unsupported search file format: "+program.toString());
            return null;
        }
    }
    
    private void updateSummaryAfterFiltering(List<PeptideSpectrumMatchIDP> filteredPsms, IdPickerRun idpRun) {
        
        // sort the filtered hits by source
        Collections.sort(filteredPsms, new Comparator<PeptideSpectrumMatchIDP>() {
            public int compare(PeptideSpectrumMatchIDP o1,PeptideSpectrumMatchIDP o2) {
                return Integer.valueOf(o1.getSpectrumMatch().getSourceId()).compareTo(o2.getSpectrumMatch().getSourceId());
            }});
        
        // count the number of filtered hits for each source
        int filteredCnt = 0;
        int lastSourceId = -1;
        for(PeptideSpectrumMatchIDP hit: filteredPsms) {
            if(lastSourceId != hit.getSpectrumMatch().getSourceId()) {
                if(lastSourceId != -1){
                    IdPickerInput input = idpRun.getInputSummaryForRunSearch(lastSourceId);
                    if(input == null) {
                        log.error("Could not find input summary for runSearchID: "+lastSourceId);
                    }
                    else {
                        input.setNumFilteredTargetHits(filteredCnt);
                    }
                }
                filteredCnt = 0;
                lastSourceId = hit.getSpectrumMatch().getSourceId();
            }
            filteredCnt++;
        }
        // update the last one;
        if(lastSourceId != -1) {
            IdPickerInput input = idpRun.getInputSummaryForRunSearch(lastSourceId);
            if(input == null) {
                log.error("Could not find input summary for runSearchID: "+lastSourceId);
            }
            else {
                input.setNumFilteredTargetHits(filteredCnt);
            }
        }
        else {
            log.error("Could not update input summary for runSearchIds");
        }
    }

    private void rankPeptideSpectrumMatches(List<InferredProtein<SpectrumMatchIDP>> proteins) {
       
        for(InferredProtein<SpectrumMatchIDP> protein: proteins) {
            
            // look at each peptide for the protein
            for(PeptideEvidence<SpectrumMatchIDP> pev: protein.getPeptides()) {
                // rank all the spectra for this peptide (based on calculated FDR)
                List<SpectrumMatchIDP> psmList = pev.getSpectrumMatchList();
                Collections.sort(psmList, new Comparator<SpectrumMatchIDP>(){
                    @Override
                    public int compare(SpectrumMatchIDP o1, SpectrumMatchIDP o2) {
                        return Double.valueOf(o1.getFdr()).compareTo(o2.getFdr());
                    }});
                int rank = 1;
                for(SpectrumMatchIDP psm: psmList) {
                    psm.setRank(rank); 
                    rank++;
                }
            }
        }
    }
}
