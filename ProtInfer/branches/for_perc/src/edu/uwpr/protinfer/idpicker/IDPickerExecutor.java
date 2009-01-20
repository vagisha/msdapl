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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDatabaseDAO;
import org.yeastrc.ms.domain.analysis.MsRunSearchAnalysis;
import org.yeastrc.ms.domain.nrseq.NrDbProtein;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.Program;

import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerRunDAO;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerParam;
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
import edu.uwpr.protinfer.util.TimeUtils;


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
        log.info("IDPicker TOTAL run time: "+TimeUtils.timeElapsedMinutes(start, end)+" minutes");
    }
    
    protected static <T extends SpectrumMatch> void calculateProteinSequenceCoverage(List<InferredProtein<T>> proteins) throws Exception {
        
        long start = System.currentTimeMillis();
        
        for(InferredProtein<T> prot: proteins) {
            int nrseqProteinId = prot.getProteinId();
            String proteinSeq = null;
            try {
                proteinSeq = NrSeqLookupUtil.getProteinSequenceForNrSeqDbProtId(nrseqProteinId);
//                proteinSeq = NrSeqLookupUtil.getProteinSequence(nrseqProteinId);
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
        long end = System.currentTimeMillis();
        log.info("Calculated protein sequence coverage in : "+TimeUtils.timeElapsedSeconds(start, end)+" seconds");
    }

    protected static <T extends PeptideSpectrumMatch<?>> void assignIdsToPeptidesAndProteins(List<T> filteredPsms, Program inputGenerator) throws Exception {
        assignNrSeqProteinIds(filteredPsms, inputGenerator);
        assignPeptideIds(filteredPsms);
    }

    private static <T extends PeptideSpectrumMatch<?>> void assignPeptideIds(List<T> filteredPsms) {
        Map<String, Integer> peptideIds = new HashMap<String, Integer>(filteredPsms.size());
        int currPeptId = 1;
        for(T psm: filteredPsms) {
            Peptide pept = psm.getPeptideHit().getPeptide();
            Integer id = peptideIds.get(pept.getSequence());
            if (id == null) {
                id = currPeptId;
                peptideIds.put(pept.getSequence(), id);
                currPeptId++;
            }
            pept.setId(id);
        }
    }

    private static int getNrSeqDatabaseId(int inputId, Program inputGenerator) {
        
        DAOFactory fact = DAOFactory.instance();
        MsRunSearchDAO runSearchDao = fact.getMsRunSearchDAO();
        MsSearchDatabaseDAO dbDao = fact.getMsSequenceDatabaseDAO();
        
        int runSearchId = 0;
        if(Program.isSearchProgram(inputGenerator)) {
            runSearchId = inputId;
        }
        else {
            MsRunSearchAnalysisDAO rsAnalysisDao = fact.getMsRunSearchAnalysisDAO();
            MsRunSearchAnalysis analysis = rsAnalysisDao.load(inputId);
            
            runSearchId = analysis.getRunSearchId();
        }
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
    
    private static <T extends PeptideSpectrumMatch<?>> void assignNrSeqProteinIds(List<T> filteredPsms, Program inputGenerator) throws Exception {
        
        long start = System.currentTimeMillis();
        
        Map<Integer, Integer> nrseqDbIds = new HashMap<Integer, Integer>();
        Map<String, NrDbProtein> nrseqIdMap = new HashMap<String, NrDbProtein>();
       
        for(T hit: filteredPsms) {
            
           int inputId = hit.getSpectrumMatch().getSourceId();
           Integer nrseqDbId = nrseqDbIds.get(inputId);
           if(nrseqDbId == null) {
               nrseqDbId = getNrSeqDatabaseId(inputId, inputGenerator);
               if(nrseqDbId == 0) {
                   log.error("Could not find nrseq db ID for runSearchID "+inputId);
                   throw new Exception("Could not find nrseq db ID for runSearchID "+inputId);
               }
               nrseqDbIds.put(inputId, nrseqDbId);
           }
           
           
           List<ProteinHit> moreProteins = new ArrayList<ProteinHit>();
           
           PeptideHit phit = hit.getPeptideHit();
           for(ProteinHit prHit: phit.getProteinList()) {
               Protein pr = prHit.getProtein();
               
               // look for a match in our map
               NrDbProtein nrDbProt = nrseqIdMap.get(pr.getAccession());
               
               // this protein is not in our map
               if(nrDbProt == null) {
                   
                   // look for an exact match
                   nrDbProt  = NrSeqLookupUtil.getDbProtein(nrseqDbId, pr.getAccession());
                   
                   // exact match not found
                   if(nrDbProt == null) {
                       // look for a match LIKE accession
                       List<Integer> ids = NrSeqLookupUtil.getDbProteinIdsPartialAccession(nrseqDbId, pr.getAccession());
                       
                       // more than one match found
                       if(ids.size() != 1) {
                           
                           // finally try to match the peptide sequence and accession
                           ids = NrSeqLookupUtil.getDbProteinIdsForPeptidePartialAccession(nrseqDbId, pr.getAccession(),
                                   phit.getPeptide().getSequence());
                           if(ids.size() != 1) {
                               log.error("Found multiple ("+ids.size()+") nrseq ids for protein: "+pr.getAccession()+
                                           "; database: "+nrseqDbId+"; peptide: "+phit.getPeptide().getSequence());
                              // throw new Exception("Could not find nrseq id for protein: "+pr.getAccession()+"; database: "+nrseqDbId);
                           
                               // IF WE HAVE MULTIPLE MATCHES IT MEANS WE HAVE A TRUNCATED ACCESSION AND
                               // A VERY SHORT PEPTIDE SEQUENCE.  ADD THEM ALL TO THE LIST
                               
                               for(int id: ids) {
                                   NrDbProtein nrDbProtM = NrSeqLookupUtil.getDbProtein(id);
                                   nrseqIdMap.put(nrDbProtM.getAccessionString(), nrDbProtM);
                                   Protein prM = new Protein(nrDbProtM.getAccessionString(), -1);
                                   prM.setId(nrDbProtM.getId());
                                   // pr.setId(nrDbProt.getProteinId()); // protein ID, NOT the id (primary key) from tblProteinDatabase
                                   prM.setAccession(nrDbProtM.getAccessionString());
                                   moreProteins.add(new ProteinHit(prM, '\u0000', '\u0000'));
                               }
                           
                           }
                           // match found -- with peptide sequence and partial accession
                           else {
                               nrDbProt = NrSeqLookupUtil.getDbProtein(ids.get(0));
                               nrseqIdMap.put(pr.getAccession(), nrDbProt);
                           }
                       }
                       // match found with partial accession
                       else {
                           nrDbProt = NrSeqLookupUtil.getDbProtein(ids.get(0));
                           nrseqIdMap.put(pr.getAccession(), nrDbProt);
                       }
                   }
                   // exact match found
                   else
                       nrseqIdMap.put(pr.getAccession(), nrDbProt);
               }
               
               // If we found an exact match
               if(nrDbProt != null) {
                   pr.setId(nrDbProt.getId());
//                 pr.setId(nrDbProt.getProteinId()); // protein ID, NOT the id (primary key) from tblProteinDatabase
                   pr.setAccession(nrDbProt.getAccessionString()); // this will set the correct accession; 
                                                               // SQT files sometimes have truncated accessions
               }
               else {pr.setId(-1);}
           }
           
           // REMOVE ALL PROTEINS FOR WHICH NO ID WAS FOUND
           Iterator<ProteinHit> iter = phit.getProteinList().iterator();
           while(iter.hasNext()) {
               ProteinHit prot = iter.next();
               if(prot.getProtein().getId() == -1)
                   iter.remove();
           }
           // ADD ALL THE ADDITIONAL PROTEINS, IF ANY.
           for(ProteinHit prot: moreProteins) {
               phit.addProteinHit(prot);
           }
        }
        long end = System.currentTimeMillis();
        log.info("Retrieved NRSEQ ids in: "+TimeUtils.timeElapsedMinutes(start, end)+" minutes");
    }
    
    private IDPickerParams makeIdPickerParams(List<IdPickerParam> filters) {
        
        IDPickerParams params = new IDPickerParams();
        params.setDoFdrCalculation(false); // set this to false initially
                                           // if we find FDR calculation filters we will set this to true;
        List<IdPickerParam> moreFilters = new ArrayList<IdPickerParam>();
        for(IdPickerParam filter: filters) {
            if(filter.getName().equalsIgnoreCase("maxAbsFDR")) {
                params.setMaxAbsoluteFdr(Float.valueOf(filter.getValue()));
                params.setDoFdrCalculation(true);
            }
            else if(filter.getName().equalsIgnoreCase("maxRelativeFDR")) {
                params.setMaxRelativeFdr(Float.valueOf(filter.getValue()));
                params.setDoFdrCalculation(true);
            }
            else if (filter.getName().equalsIgnoreCase("decoyRatio"))
                params.setDecoyRatio(Float.valueOf(filter.getValue()));
            else if (filter.getName().equalsIgnoreCase("decoyPrefix"))
                params.setDecoyPrefix(filter.getValue());
            else if (filter.getName().equalsIgnoreCase("parsimonyAnalysis"))
                params.setDoParsimonyAnalysis(Boolean.valueOf(filter.getValue()));
            else if(filter.getName().equalsIgnoreCase("FDRFormula")) {
                String val = filter.getValue();
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

    
    protected static <T extends PeptideSpectrumMatch<?>> void removeSpectraWithMultipleResults(List<T> psmList) {
        
        long s = System.currentTimeMillis();
        // sort by scanID
        Collections.sort(psmList, new Comparator<PeptideSpectrumMatch<?>>() {
            public int compare(PeptideSpectrumMatch<?> o1, PeptideSpectrumMatch<?> o2) {
                return Integer.valueOf(o1.getScanId()).compareTo(o2.getScanId());
            }});
        
        // get a list of scan Ids that have multiple results
        Set<Integer> scanIdsToRemove = new HashSet<Integer>();
        
        int lastScanId = -1;
        for (int i = 0; i < psmList.size(); i++) {
            T psm = psmList.get(i);
            if(lastScanId != -1){
                if(lastScanId == psm.getScanId()) {
                    scanIdsToRemove.add(lastScanId);
                }
            }
            lastScanId = psm.getScanId();
        }
        log.info("Found "+scanIdsToRemove.size()+" scanIds with multiple results");
        
        Iterator<T> iter = psmList.iterator();
        while(iter.hasNext()) {
            T psm = iter.next();
            if(scanIdsToRemove.contains(psm.getScanId())) {
//                log.info("Removing for scanID: "+psm.getScanId()+"; resultID: "+psm.getHitId());
                iter.remove();
            }
        }
        long e = System.currentTimeMillis();
        log.info("Removed scans with multiple results in: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
    }
    
    
    protected static <S extends SpectrumMatch, T extends PeptideSpectrumMatch<S>> 
        List<InferredProtein<S>> inferProteins(List<T> psms, IDPickerParams params) {
        
        ProteinInferrerIdPicker inferrer = new ProteinInferrerIdPicker(params.getDoParsimonyAnalysis());
        return inferrer.inferProteins(psms);
    }
    
    public static <T extends InferredProtein<?>>void replaceNrSeqDbProtIdsWithProteinIds(List<T> proteins) {
        
        long s = System.currentTimeMillis();
        for(T prot: proteins) {
            Protein pr = prot.getProtein();
            int nrseqDbProtId = pr.getId(); // This is the id (primary key) from tblProteinDatabase
            NrDbProtein nrDbProt = NrSeqLookupUtil.getDbProtein(nrseqDbProtId);
            pr.setId(nrDbProt.getProteinId());
        }
        long e = System.currentTimeMillis();
        log.info("Replaced NRSEQ dbProt Ids with NRSEQ protein Ids in: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
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
