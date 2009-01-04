/**
 * IDPickerExecutor.java
 * @author Vagisha Sharma
 * Nov 3, 2008
 * @version 1.0
 */
package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDatabaseDAO;
import org.yeastrc.ms.domain.nrseq.NrDbProtein;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabase;

import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerRunDAO;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerFilter;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerRun;
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
        
        if(idpRun.getInputList().size() == 0)
            return;
        
        long start = System.currentTimeMillis();
        
        // create the parameters object
        IDPickerParams params = makeIdPickerParams(idpRun.getFilters());
        
        
        // Are we going to do FDR calculation?
        if(params.getDoFdrCalculation()) {
            IdPickerExecutorFDR fdrExe  = new IdPickerExecutorFDR();
            fdrExe.execute(idpRun, params);
        }
        else {
            IdPickerExecutorNoFDR noFdrExe = new IdPickerExecutorNoFDR();
            noFdrExe.execute(idpRun, params);
        }
        
        
        long end = System.currentTimeMillis();
        log.info("IDPicker TOTAL run time: "+timeElapsed(start, end));
    }
    
    protected static <T extends SpectrumMatch> void calculateProteinSequenceCoverage(List<InferredProtein<T>> proteins) throws Exception {
        
        for(InferredProtein<T> prot: proteins) {
            int nrseqProteinId = prot.getProteinId();
            String proteinSeq = null;
            try {
//                proteinSeq = NrSeqLookupUtil.getProteinSequenceForNrSeqDbProtId(nrseqProteinId);
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
            for(PeptideEvidence<T> pev: prot.getPeptides()) {
                peptides.add(pev.getPeptideSeq());
            }
            int lengthCovered = StringUtils.getCoveredSequenceLength(proteinSeq, peptides);
            float percCovered = ((float)lengthCovered/(float)proteinSeq.length()) * 100.0f;
            prot.setPercentCoverage(percCovered);
        }
    }

    protected static <T extends PeptideSpectrumMatch<?>> void assignIdsToPeptidesAndProteins(List<T> filteredPsms) throws Exception {
        assignNrSeqProteinIds(filteredPsms);
        assignPeptideIds(filteredPsms);
    }

    private static <T extends PeptideSpectrumMatch<?>> void assignPeptideIds(List<T> filteredPsms) {
        Map<String, Integer> peptideIds = new HashMap<String, Integer>(filteredPsms.size());
        int lastPeptId = 1;
        for(T psm: filteredPsms) {
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

    private static int getNrSeqDatabaseId(int runSearchId) {
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
    
    private static <T extends PeptideSpectrumMatch<?>> void assignNrSeqProteinIds(List<T> filteredPsms) throws Exception {
        
        Map<Integer, Integer> nrseqDbIds = new HashMap<Integer, Integer>();
        Map<String, NrDbProtein> nrseqIdMap = new HashMap<String, NrDbProtein>();
       
        for(T hit: filteredPsms) {
            
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
//               pr.setId(nrDbProt.getId());
               pr.setId(nrDbProt.getProteinId()); // protein ID, NOT the id (primary key) from tblProteinDatabase
               pr.setAccession(nrDbProt.getAccessionString()); // this will set the correct accession; 
                                                               // SQT files sometimes have truncated accessions
           }
        }
        
    }
    
    private IDPickerParams makeIdPickerParams(List<IdPickerFilter> filters) {
        
        IDPickerParams params = new IDPickerParams();
        params.setDoFdrCalculation(false); // set this to false initially
                                           // if we find FDR calculation filters we will set this to true;
        List<IdPickerFilter> moreFilters = new ArrayList<IdPickerFilter>();
        for(IdPickerFilter filter: filters) {
            if(filter.getFilterName().equalsIgnoreCase("maxAbsFDR")) {
                params.setMaxAbsoluteFdr(Float.valueOf(filter.getFilterValue()));
                params.setDoFdrCalculation(true);
            }
            else if(filter.getFilterName().equalsIgnoreCase("maxRelativeFDR")) {
                params.setMaxRelativeFdr(Float.valueOf(filter.getFilterValue()));
                params.setDoFdrCalculation(true);
            }
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
            else {
                moreFilters.add(filter);
            }
        }
        if(moreFilters.size() > 0)
            params.addMoreFilters(moreFilters);
        return params;
    }

    
    protected static final <S extends SpectrumMatch, T extends PeptideSpectrumMatch<S>> 
        List<InferredProtein<S>> inferProteins(List<T> psms, IDPickerParams params) {
        
        ProteinInferrerIdPicker inferrer = new ProteinInferrerIdPicker(params.getDoParsimonyAnalysis());
        return inferrer.inferProteins(psms);
    }
    
    private float timeElapsed(long start, long end) {
        return (end - start)/(1000.0f);
    }
    
    public static void main(String[] args) {
        ProteinferDAOFactory factory = ProteinferDAOFactory.instance();
        IdPickerRunDAO runDao = factory.getIdPickerRunDao();
        IdPickerRun run = runDao.loadProteinferRun(1);
        System.out.println("Number of files: "+run.getInputList().size());
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
