/**
 * IDPickerExecutor.java
 * @author Vagisha Sharma
 * Nov 3, 2008
 * @version 1.0
 */
package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDatabaseDAO;
import org.yeastrc.ms.domain.nrseq.NrDbProtein;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.SearchProgram;

import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerRunDAO;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerFilter;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerInputSummary;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerRun;
import edu.uwpr.protinfer.filter.Filter;
import edu.uwpr.protinfer.filter.FilterException;
import edu.uwpr.protinfer.filter.fdr.FdrCalculatorException;
import edu.uwpr.protinfer.filter.fdr.FdrFilterCriteria;
import edu.uwpr.protinfer.infer.InferredProtein;
import edu.uwpr.protinfer.infer.Peptide;
import edu.uwpr.protinfer.infer.PeptideEvidence;
import edu.uwpr.protinfer.infer.PeptideHit;
import edu.uwpr.protinfer.infer.PeptideSpectrumMatch;
import edu.uwpr.protinfer.infer.Protein;
import edu.uwpr.protinfer.infer.ProteinHit;
import edu.uwpr.protinfer.infer.SpectrumMatch;
import edu.uwpr.protinfer.util.StringUtils;


public class IDPickerExecutor {

    private static Logger log = Logger.getLogger(IDPickerExecutor.class);
    
    
    public void execute(IdPickerRun idpRun) throws Exception {
        
        if(idpRun.getInputSummaryList().size() == 0)
            return;
        
        long start = System.currentTimeMillis();
        
        // create the parameters object
        IDPickerParams params = makeIdPickerParams(idpRun.getFilters());
        
        // get the program used for the data
        // NOTE: WE ASSUME ALL THE GIVEN runSearchIds WERE SEARCHED WITH THE SAME PROGRAM
        SearchProgram program = getSearchProgram(idpRun.getInputSummaryList().get(0));
        
        // get all the search hits for the given runSearchIds
        List<PeptideSpectrumMatchIDP> allPsms = getAllSearchHits(idpRun, params, program);
        
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
        
        // update the summary statistics
        updateSummaryAfterFiltering(filteredPsms, idpRun);
        
        // assign ids to peptides and proteins(nrseq ids)
        assignIdsToPeptidesAndProteins(filteredPsms);
        
        // infer the proteins;
        List<InferredProtein<SpectrumMatchIDP>> proteins = inferProteins(filteredPsms, params);
        
        // calculate the protein coverage
        calculateProteinSequenceCoverage(proteins);
        
        
        // FINALLY save the results
        IdPickerResultSaver.instance().saveIdPickerResults(idpRun, proteins);
        
        long end = System.currentTimeMillis();
        log.info("IDPicker TOTAL run time: "+timeElapsed(start, end));
    }
    
    private static void calculateProteinSequenceCoverage(List<InferredProtein<SpectrumMatchIDP>> proteins) throws Exception {
        
        for(InferredProtein<SpectrumMatchIDP> prot: proteins) {
            int nrseqProteinId = prot.getProteinId();
            String proteinSeq = null;
            try {
                proteinSeq = NrSeqLookupUtil.getProteinSequence(nrseqProteinId);
            }
            catch (Exception e) {
                log.error("Exception getting nrseq protein for proteinId: "+nrseqProteinId, e);
                throw e;
            }
            
            if(proteinSeq == null) {
                log.error("Protein sequence for proteinId: "+nrseqProteinId+" is null.");
                throw new Exception("Protein sequence for proteinId: "+nrseqProteinId+" is null.");
            }
                
            List<String> peptides = new ArrayList<String>();
            for(PeptideEvidence<SpectrumMatchIDP> pev: prot.getPeptides()) {
                peptides.add(pev.getPeptideSeq());
            }
            int lengthCovered = StringUtils.getCoveredSequenceLength(proteinSeq, peptides);
            float percCovered = ((float)lengthCovered/(float)proteinSeq.length()) * 100.0f;
            prot.setPercentCoverage(percCovered);
        }
    }

    private void assignIdsToPeptidesAndProteins(List<PeptideSpectrumMatchIDP> filteredPsms) throws Exception {
        assignNrSeqProteinIds(filteredPsms);
        assignPeptideIds(filteredPsms);
    }

    private void assignPeptideIds(List<PeptideSpectrumMatchIDP> filteredPsms) {
        Map<String, Integer> peptideIds = new HashMap<String, Integer>(filteredPsms.size());
        int lastPeptId = 1;
        for(PeptideSpectrumMatchIDP psm: filteredPsms) {
            Peptide pept = psm.getPeptideHit().getPeptide();
            Integer id = peptideIds.get(pept.getSequence());
            if (id == null) {
                id = lastPeptId;
                peptideIds.put(pept.getSequence(), id);
                lastPeptId++;
            }
            pept.setId(id);
        }
    }

    private int getNrSeqDatabaseId(int runSearchId) {
        DAOFactory fact = DAOFactory.instance();
        MsRunSearchDAO runSearchDao = fact.getMsRunSearchDAO();
        MsSearchDatabaseDAO dbDao = fact.getMsSequenceDatabaseDAO();
        
        MsRunSearch runSearch = runSearchDao.loadRunSearch(runSearchId);
        if(runSearch == null) {
            log.error("Could not load runSearch with id: "+runSearchId);
            return 0;
        }
        List<MsSearchDatabase> searchDbs = dbDao.loadSearchDatabases(runSearch.getSearchId());
        if(searchDbs.size() != 1) {
            log.warn("No search database found for searchID: "+runSearch.getSearchId());
            return 0;
        }
        int nrseqDbId = searchDbs.get(0).getSequenceDatabaseId();
        return nrseqDbId;
    }
    
    private void assignNrSeqProteinIds(List<PeptideSpectrumMatchIDP> filteredPsms) throws Exception {
        
        Map<Integer, Integer> nrseqDbIds = new HashMap<Integer, Integer>();
        Map<String, NrDbProtein> nrseqIdMap = new HashMap<String, NrDbProtein>();
       
        for(PeptideSpectrumMatchIDP hit: filteredPsms) {
            
           int runSearchId = hit.getSpectrumMatch().getSourceId();
           Integer nrseqDbId = nrseqDbIds.get(runSearchId);
           if(nrseqDbId == null) {
               nrseqDbId = getNrSeqDatabaseId(runSearchId);
               if(nrseqDbId == 0) {
                   log.error("Could not find nrseq db ID for runSearchID "+runSearchId);
                   throw new Exception("Could not find nrseq db ID for runSearchID "+runSearchId);
               }
               nrseqDbIds.put(runSearchId, nrseqDbId);
           }
           
           
           PeptideHit phit = hit.getPeptideHit();
           for(ProteinHit prHit: phit.getProteinList()) {
               Protein pr = prHit.getProtein();
               // look for an exact match
               NrDbProtein nrDbProt = nrseqIdMap.get(pr.getAccession());
               if(nrDbProt == null) {
                   nrDbProt  = NrSeqLookupUtil.getDbProtein(nrseqDbId, pr.getAccession());
                   if(nrDbProt == null) {
                       // look for a match LIKE accession
                       List<Integer> ids = NrSeqLookupUtil.getDbProteinIdsPartialAccession(nrseqDbId, pr.getAccession());
                       if(ids.size() != 1) {
                           // finally try to match the peptide sequence and accession
                           ids = NrSeqLookupUtil.getDbProteinIdsForPeptidePartialAccession(nrseqDbId, pr.getAccession(),
                                   phit.getPeptide().getSequence());
                           if(ids.size() != 1) {
                               log.error("Could not find nrseq id for protein: "+pr.getAccession()+
                                           "; database: "+nrseqDbId);
                               throw new Exception("Could not find nrseq id for protein: "+pr.getAccession()+"; database: "+nrseqDbId);
                           }
                           else {
                               nrDbProt = NrSeqLookupUtil.getDbProtein(ids.get(0));
                               nrseqIdMap.put(pr.getAccession(), nrDbProt);
                           }
                       }
                       else {
                           nrDbProt = NrSeqLookupUtil.getDbProtein(ids.get(0));
                           nrseqIdMap.put(pr.getAccession(), nrDbProt);
                       }
                   }
                   else
                       nrseqIdMap.put(pr.getAccession(), nrDbProt);
               }
               pr.setId(nrDbProt.getProteinId()); // protein ID, NOT the id (primary key) from tblProteinDatabase
               pr.setAccession(nrDbProt.getAccessionString()); // this will set the correct accession; 
                                                               // SQT files sometimes have truncated accessions
           }
        }
        
    }

    // This method also updates the summary with the total number of proteins found for all the 
    // search hits.
    private List<PeptideSpectrumMatchIDP> getAllSearchHits(IdPickerRun idpRun, IDPickerParams params, SearchProgram program) {
        
        Set<String> allProteins = new HashSet<String>();
        Set<String> allPeptides = new HashSet<String>();
        
        List<IdPickerInputSummary> idpInputList = idpRun.getInputSummaryList();
        List<PeptideSpectrumMatchIDP> allPsms = new ArrayList<PeptideSpectrumMatchIDP>();
        SearchResultsGetter resGetter = SearchResultsGetter.instance();
        
        for(IdPickerInputSummary input: idpInputList) {
            
            int runSearchId = input.getRunSearchId();
            List<PeptideSpectrumMatchIDP> psms = resGetter.getHitsForRunSearch(runSearchId, params.getDecoyPrefix(), program);
            allPsms.addAll(psms);
            
            // count the number of target and decoy hits
            int target = 0;
            int decoy = 0;
            for(PeptideSpectrumMatchIDP psm: psms) {
                if(psm.isDecoyMatch())  decoy++;
                else                    target++;
                
                PeptideHit pept = psm.getPeptideHit();
                allPeptides.add(pept.getSequence());
                for(ProteinHit prot: pept.getProteinList()) {
                    allProteins.add(prot.getAccession());
                }
            }
            
            input.setNumDecoyHits(decoy);
            input.setNumTargetHits(target); 
        }
        idpRun.setNumUnfilteredProteins(allProteins.size());
        idpRun.setNumUnfilteredPeptides(allPeptides.size());
        
        return allPsms;
    }

    private SearchProgram getSearchProgram(IdPickerInputSummary idpInput) {
        MsRunSearchDAO runSearchDao = DAOFactory.instance().getMsRunSearchDAO();
        int runSearchId = idpInput.getRunSearchId();
        return runSearchDao.loadSearchProgramForRunSearch(runSearchId);
    }
    
    private IDPickerParams makeIdPickerParams(List<IdPickerFilter> filters) {
        IDPickerParams params = new IDPickerParams();
        for(IdPickerFilter filter: filters) {
            if(filter.getFilterName().equalsIgnoreCase("maxAbsFDR"))
                params.setMaxAbsoluteFdr(Float.valueOf(filter.getFilterValue()));
            else if(filter.getFilterName().equalsIgnoreCase("maxRelativeFDR"))
                params.setMaxRelativeFdr(Float.valueOf(filter.getFilterValue()));
            else if (filter.getFilterName().equalsIgnoreCase("decoyRatio"))
                params.setDecoyRatio(Float.valueOf(filter.getFilterValue()));
            else if (filter.getFilterName().equalsIgnoreCase("decoyPrefix"))
                params.setDecoyPrefix(filter.getFilterValue());
            else if (filter.getFilterName().equalsIgnoreCase("parsimonyAnalysis"))
                params.setDoParsimonyAnalysis(Boolean.valueOf(filter.getFilterValue()));
            else if(filter.getFilterName().equalsIgnoreCase("FDRFormula")) {
                String val = filter.getFilterValue();
                if(val.equals("2R/(F+R)"))
                    params.setUseIdPickerFDRFormula(true);
                else
                    params.setUseIdPickerFDRFormula(false);
            }
        }
        return params;
    }

    
    private  List<PeptideSpectrumMatchIDP> filterSearchHits(List<PeptideSpectrumMatchIDP> searchHits, 
            IDPickerParams params, SearchProgram program) throws FdrCalculatorException, FilterException {
        
        Comparator<PeptideSpectrumMatchIDP> absScoreComparator = getAbsoluteScoreComparator(program);
        Comparator<PeptideSpectrumMatchIDP> relScoreComparator = getRelativeScoreComparator(program);
        
        return filterSearchHits(searchHits, params, program, absScoreComparator, relScoreComparator);
    }

    
    private  List<PeptideSpectrumMatchIDP> filterSearchHits(List<PeptideSpectrumMatchIDP> searchHits, 
                                                           IDPickerParams params, SearchProgram program,
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
        log.info("Calculated FDR for relative scores + filtered in: "+timeElapsed(s, e));

        // Clear the fdr scores for the filtered hits and calculate FDR from xCorr scores
        for (PeptideSpectrumMatchIDP hit: filteredHits)
            hit.setFdr(1.0);

        // Calculate FDR from absolute scores (e.g. XCorr)
        s = System.currentTimeMillis();
        
        // IDPicker separates charge states for calculating FDR using XCorr scores
        if(program == SearchProgram.SEQUEST || program == SearchProgram.EE_NORM_SEQUEST)
            calculator.separateChargeStates(true);
        
        calculator.calculateFdr(searchHits, absoluteScoreComparator);

        filterCriteria = new FdrFilterCriteria(params.getMaxAbsoluteFdr());
        filteredHits = Filter.filter(searchHits, filterCriteria);
        e = System.currentTimeMillis();
        log.info("Calculated FDR for absolute scores + filtered in: "+timeElapsed(s, e));
        
        log.info("Total time for filtering: "+timeElapsed(start, e));
        
        return filteredHits;
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
                    IdPickerInputSummary input = idpRun.getInputSummaryForRunSearch(lastSourceId);
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
            IdPickerInputSummary input = idpRun.getInputSummaryForRunSearch(lastSourceId);
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
    
    private Comparator<PeptideSpectrumMatchIDP> getAbsoluteScoreComparator(SearchProgram program) {
        if(program == SearchProgram.SEQUEST || program == SearchProgram.EE_NORM_SEQUEST) {
            // we will be comparing XCorr
            return new Comparator<PeptideSpectrumMatchIDP>() {
                public int compare(PeptideSpectrumMatchIDP o1, PeptideSpectrumMatchIDP o2) {
                    return Double.valueOf(o1.getAbsoluteScore()).compareTo(o2.getAbsoluteScore());
                }};
        }
        else if(program == SearchProgram.PROLUCID) {
            // TODO here we need to know what primary score is used by ProLuCID
            return null;
        }
        else {
            log.error("Unsupported search file format: "+program.toString());
            return null;
        }
    }
    
    private Comparator<PeptideSpectrumMatchIDP> getRelativeScoreComparator(SearchProgram program) {
        if(program == SearchProgram.SEQUEST || program == SearchProgram.EE_NORM_SEQUEST) {
            // we will be comparing DeltaCN -- 0.0 is be best score; 1.0 is worst
            return new Comparator<PeptideSpectrumMatchIDP>() {
                public int compare(PeptideSpectrumMatchIDP o1, PeptideSpectrumMatchIDP o2) {
                    return Double.valueOf(o2.getRelativeScore()).compareTo(o1.getRelativeScore());
                }};
        }
        else if(program == SearchProgram.PROLUCID) {
            // TODO here we need to know what primary score is used by ProLuCID
            return null;
        }
        else {
            log.error("Unsupported search file format: "+program.toString());
            return null;
        }
    }

    
    public <S extends SpectrumMatch, T extends PeptideSpectrumMatch<S>> 
        List<InferredProtein<S>> inferProteins(List<T> psms, IDPickerParams params) {
        
        ProteinInferrerIdPicker inferrer = new ProteinInferrerIdPicker(params);
        return inferrer.inferProteins(psms);
    }
    
    private static float timeElapsed(long start, long end) {
        return (end - start)/(1000.0f);
    }
    
    public static void main(String[] args) {
        ProteinferDAOFactory factory = ProteinferDAOFactory.instance();
        IdPickerRunDAO runDao = factory.getIdPickerRunDao();
        IdPickerRun run = runDao.getProteinferRun(4);
        System.out.println("Number of files: "+run.getInputSummaryList().size());
        System.out.println("Number of filters: "+run.getFilters().size());
        
        IDPickerExecutor executor = new IDPickerExecutor();
        try {
            executor.execute(run);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
