/**
 * DoProteinInferenceAction.java
 * @author Vagisha Sharma
 * Nov 3, 2008
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDatabaseDAO;
import org.yeastrc.ms.dao.search.MsSearchModificationDAO;
import org.yeastrc.ms.domain.nrseq.NrDbProtein;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.nr_seq.NRProtein;
import org.yeastrc.nr_seq.NRProteinFactory;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import edu.uwpr.protinfer.SequestHit;
import edu.uwpr.protinfer.SequestSpectrumMatch;
import edu.uwpr.protinfer.database.ProteinferSaver;
import edu.uwpr.protinfer.filter.FilterException;
import edu.uwpr.protinfer.filter.fdr.FdrCalculatorException;
import edu.uwpr.protinfer.idpicker.IDPickerExecutor;
import edu.uwpr.protinfer.idpicker.IDPickerParams;
import edu.uwpr.protinfer.idpicker.InferredPeptideGroup;
import edu.uwpr.protinfer.idpicker.InferredProteinGroup;
import edu.uwpr.protinfer.idpicker.SearchSummary;
import edu.uwpr.protinfer.idpicker.SearchSummary.RunSearch;
import edu.uwpr.protinfer.infer.InferredProtein;
import edu.uwpr.protinfer.infer.Peptide;
import edu.uwpr.protinfer.infer.PeptideEvidence;
import edu.uwpr.protinfer.infer.PeptideHit;
import edu.uwpr.protinfer.infer.PeptideModification;
import edu.uwpr.protinfer.infer.Protein;
import edu.uwpr.protinfer.infer.ProteinHit;
import edu.uwpr.protinfer.msdata.MsDataSearchResultsReader;
import edu.uwpr.protinfer.util.StringUtils;

/**
 * 
 */
public class DoProteinInferenceAction extends Action {

    private static final Logger log = Logger.getLogger(DoProteinInferenceAction.class);
    
    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {
        
        
        if (request.getSession().getAttribute("inferredProteins") != null) {
            return mapping.findForward("Success");
        }
        
        // User making this request
        User user = UserUtils.getUser(request);
        if (user == null) {
            ActionErrors errors = new ActionErrors();
            errors.add("username", new ActionMessage("error.login.notloggedin"));
            saveErrors( request, errors );
            return mapping.findForward("authenticate");
        }

        ProteinInferenceForm prinferForm = (ProteinInferenceForm) form;
        SearchSummary searchSummary = prinferForm.getSearchSummary();
        IDPickerParams params = prinferForm.getIdPickerParams();
        
        long s = System.currentTimeMillis();
        List<SequestHit> allFilteredHits = new ArrayList<SequestHit>();
        // get the search hits
        MsDataSearchResultsReader reader = new MsDataSearchResultsReader();
        
        Set<String> allProteins = new HashSet<String>();
        List<SequestHit> allHits = new ArrayList<SequestHit>();
        for (RunSearch runSearch: searchSummary.getRunSearchList()) {
            if (!runSearch.getIsSelected())
                continue;
            long start = System.currentTimeMillis();
            List<SequestHit> hits = reader.getHitsForRunSearch(runSearch.getRunSearchId(), params.getDecoyPrefix());
            allHits.addAll(hits);
            
            int targetCount = 0;
            int decoyCount = 0;
            for(SequestHit hit: hits) {
                if (hit.isDecoyMatch())
                    decoyCount++;
                else if (hit.isTargetMatch())
                    targetCount++;
                PeptideHit phit = hit.getPeptideHit();
                for(ProteinHit pr: phit.getProteinList()) {
                   allProteins.add(pr.getAccession());
                }
            }
            runSearch.setTotalDecoyHits(decoyCount);
            runSearch.setTotalTargetHits(targetCount);
            
            long end = System.currentTimeMillis();
            log.info("File: "+runSearch.getRunName()+"; ID: "+runSearch.getRunSearchId()+"; # hits: "+hits.size()+"; Time: "+getTime(start, end));
//            // filter the search hits
//            start = System.currentTimeMillis();
//            List<SequestHit> filteredHits = null;
//            try {
//                filteredHits = filterHits(hits, params);
//            }
//            catch (FdrCalculatorException e) {
//                e.printStackTrace();
//            }
//            catch (FilterException e) {
//                e.printStackTrace();
//            }
//            runSearch.setFilteredTargetHits(filteredHits.size());
//            end = System.currentTimeMillis();
//            log.info("File: "+runSearch.getRunName()+"; ID: "+runSearch.getRunSearchId()+"; # Filterted hits: "+filteredHits.size()+"; Time: "+getTime(start, end));
//            allFilteredHits.addAll(filteredHits);
            
            // get the nrseq protein ids
//            start = System.currentTimeMillis();
//            getNrseqIdsAndAccessions(hits, runSearch.getRunSearchId());
//            end = System.currentTimeMillis();
//            log.info("Assigned NR_SEQ ids in: "+getTime(start, end));
        }
        
        // do fdr calculation on ALL hits 
        // filter the search hits
        long start = System.currentTimeMillis();
        try {
            allFilteredHits = filterHits(allHits, params);
        }
        catch (FdrCalculatorException e) {
            e.printStackTrace();
        }
        catch (FilterException e) {
            e.printStackTrace();
        }
        
        // count the number of filtered hits for each input file
        for (RunSearch runSearch: searchSummary.getRunSearchList()) {
            if (!runSearch.getIsSelected())
                continue;
            int filteredCnt = 0;
            for(SequestHit hit: allFilteredHits) {
                if(hit.getSearchSource().getId() == runSearch.getRunSearchId())
                    filteredCnt++;
            }
            runSearch.setFilteredTargetHits(filteredCnt);
        }
        
        long end = System.currentTimeMillis();
        log.info("# Filterted hits: "+allFilteredHits.size()+"; Time: "+getTime(start, end));
        
        // get the nrseq protein ids
        start = System.currentTimeMillis();
        List<SequestHit> runSearchHits = new ArrayList<SequestHit>();
        Collections.sort(allFilteredHits, new Comparator<SequestHit>() {
            public int compare(SequestHit o1, SequestHit o2) {
                return Integer.valueOf(o1.getSearchSource().getId()).compareTo(o2.getSearchSource().getId());
            }});
        int lastRunSearchId = -1;
        for(SequestHit hit: allFilteredHits) {
            if(hit.getSearchSource().getId() != lastRunSearchId) {
                if(runSearchHits.size() > 0) {
                    getNrseqIdsAndAccessions(runSearchHits, lastRunSearchId);
                }
                runSearchHits.clear();
                lastRunSearchId = hit.getSearchSource().getId();
            }
            runSearchHits.add(hit);
        }
        if(runSearchHits.size() > 0) {
            getNrseqIdsAndAccessions(runSearchHits, lastRunSearchId);
        }
        end = System.currentTimeMillis();
        log.info("Assigned NR_SEQ ids in: "+getTime(start, end));
        
        
        
        // # of proteins before any filtering
        searchSummary.setAllProteins(allProteins.size());
        
        log.info("Total Filtered Hits: "+allFilteredHits.size());
        
        getModifiedSequenceForPeptides(allFilteredHits);
        
        assginIdsToProteinsAndPeptides(allFilteredHits);
        
        getScanNumbersForHits(allFilteredHits);
        
        // infer the protein list
        start = System.currentTimeMillis();
        List<InferredProtein<SequestSpectrumMatch>> proteins = inferProteinList(allFilteredHits, searchSummary, params);
        end = System.currentTimeMillis();
        log.info("# of Inferred Proteins: "+proteins.size()+". Time: "+getTime(start, end));
        
        // assign best fdr values
        assignBestFdrValues(proteins);
        
        // calculate protein sequence coverage
        start = System.currentTimeMillis();
        calculateProteinSequenceCoverage(proteins);
        end = System.currentTimeMillis();
        log.info("Calculated protein sequence coverage in: "+getTime(start, end));
        
        // save the results
        start = System.currentTimeMillis();
        ProteinferSaver.saveProteinInferenceResults(searchSummary, params, proteins);
        end = System.currentTimeMillis();
        log.info("Saved results in: "+getTime(start, end));
        
        
        // Group proteins and peptides
        Map<Integer, InferredProteinGroup<SequestSpectrumMatch>> protGroupList = new HashMap<Integer, InferredProteinGroup<SequestSpectrumMatch>>();
        Map<Integer, InferredPeptideGroup<SequestSpectrumMatch>> peptGroupList = new HashMap<Integer, InferredPeptideGroup<SequestSpectrumMatch>>();
        Map<Integer, Set<Integer>> proteinClusterIds = new HashMap<Integer, Set<Integer>>();
        
        Map<String, PeptideEvidence<SequestSpectrumMatch>> uniquePeptides = new HashMap<String, PeptideEvidence<SequestSpectrumMatch>>();
        
        for(InferredProtein<SequestSpectrumMatch> prot: proteins) {
            InferredProteinGroup<SequestSpectrumMatch> protGroup = protGroupList.get(prot.getProteinGroupId());
            if (protGroup == null) {
                protGroup = new InferredProteinGroup<SequestSpectrumMatch>(prot.getProteinGroupId());
                protGroupList.put(prot.getProteinGroupId(), protGroup);
            }
            
            // cluster
            Set<Integer> clusterGroups = proteinClusterIds.get(prot.getProteinClusterId());
            if(clusterGroups == null) {
                clusterGroups = new HashSet<Integer>();
                proteinClusterIds.put(prot.getProteinClusterId(), clusterGroups);
            }
            clusterGroups.add(prot.getProteinGroupId());
            
            protGroup.addInferredProtein(prot);
            
            for(PeptideEvidence<SequestSpectrumMatch> pev: prot.getPeptides()) {
                
                protGroup.addMatchingPeptideGroupId(pev.getPeptide().getPeptideGroupId());
                
                if(uniquePeptides.containsKey(pev.getPeptide().getModifiedSequence())) {
                    InferredPeptideGroup<SequestSpectrumMatch> peptGroup = peptGroupList.get(pev.getPeptide().getPeptideGroupId());
                    peptGroup.addMatchingProteinGroupId(prot.getProteinGroupId());
                }
                else {
                    uniquePeptides.put(pev.getPeptide().getModifiedSequence(), pev);
                    InferredPeptideGroup<SequestSpectrumMatch> peptGroup = peptGroupList.get(pev.getPeptide().getPeptideGroupId());
                    if(peptGroup == null) {
                        peptGroup = new InferredPeptideGroup<SequestSpectrumMatch>(pev.getPeptide().getPeptideGroupId());
                        peptGroupList.put(pev.getPeptide().getPeptideGroupId(), peptGroup);
                    }
                    peptGroup.addPeptideEvidence(pev);
                    peptGroup.addMatchingProteinGroupId(prot.getProteinGroupId());
                }
            }
        }
        
        
        long e = System.currentTimeMillis();
        log.info("Total time: "+getTime(s, e));
        
        request.getSession().setAttribute("inferredProteins", proteins);
        request.getSession().setAttribute("protGroupList", protGroupList);
        request.getSession().setAttribute("peptGroupList", peptGroupList);
        request.getSession().setAttribute("proteinClusterIds", proteinClusterIds);
        request.getSession().setAttribute("searchSummary", searchSummary);
        request.getSession().setAttribute("params", params);
        
        // Go!
        return mapping.findForward("Success");
    }
    
    private void getScanNumbersForHits(List<SequestHit> allFilteredHits) {
        DAOFactory fact = DAOFactory.instance();
        MsScanDAO scanDao = fact.getMsScanDAO();
        for(SequestHit hit: allFilteredHits) {
            int scanId = hit.getScanId();
            MsScan scan = scanDao.load(scanId);
            hit.setScanNumber(scan.getStartScanNum());
        }
    }

    private static void assignBestFdrValues(List<InferredProtein<SequestSpectrumMatch>> proteins) {
        
        for(InferredProtein<SequestSpectrumMatch> prot: proteins) {
            for(PeptideEvidence<SequestSpectrumMatch> pev: prot.getPeptides()) {
                double bestFdr = Double.MAX_VALUE;
                for(SequestSpectrumMatch psm: pev.getSpectrumMatchList()) {
                    bestFdr = Math.min(bestFdr, psm.getFdr());
                }
                pev.setBestFdr(bestFdr);
            }
        }
    }

    private static float getTime(long start, long end) {
        long time = end - start;
        float seconds = (float)time / (1000.0f);
        return seconds;
    }

    private static List<InferredProtein<SequestSpectrumMatch>> inferProteinList(
            List<SequestHit> filteredHits, SearchSummary summary, IDPickerParams params) {
        IDPickerExecutor executor = new IDPickerExecutor();
        return executor.inferProteins(filteredHits, summary, params);
    }

    private static List<SequestHit> filterHits(List<SequestHit> hits, IDPickerParams params) 
        throws FdrCalculatorException, FilterException {
        IDPickerExecutor executor = new IDPickerExecutor();
        return executor.filterSearchHits(hits, params);
    }
    
    public static void main(String[] args) {
        SearchSummary searchSummary = getSearchSummary(6);
        IDPickerParams params = new IDPickerParams();
        params.setDecoyRatio(1.0f);
        params.setDoParsimonyAnalysis(true);
        params.setMaxAbsoluteFdr(0.05f);
        params.setMaxRelativeFdr(0.05f);
        params.setMinDistinctPeptides(2);
        params.setDecoyPrefix("Reverse_");
        
        long s = System.currentTimeMillis();
        List<SequestHit> allFilteredHits = new ArrayList<SequestHit>();
        // get the search hits
        MsDataSearchResultsReader reader = new MsDataSearchResultsReader();
        for (RunSearch runSearch: searchSummary.getRunSearchList()) {
            long start = System.currentTimeMillis();
            List<SequestHit> hits = reader.getHitsForRunSearch(runSearch.getRunSearchId(), params.getDecoyPrefix());
            long end = System.currentTimeMillis();
            log.info("File: "+runSearch.getRunName()+"; ID: "+runSearch.getRunSearchId()+"; # hits: "+hits.size()+"; Time: "+getTime(start, end));
            // filter the search hits
            start = System.currentTimeMillis();
            List<SequestHit> filteredHits = null;
            try {
                filteredHits = filterHits(hits, params);
            }
            catch (FdrCalculatorException e) {
                e.printStackTrace();
            }
            catch (FilterException e) {
                e.printStackTrace();
            }
            end = System.currentTimeMillis();
            log.info("File: "+runSearch.getRunName()+"; ID: "+runSearch.getRunSearchId()+"; # Filterted hits: "+filteredHits.size()+"; Time: "+getTime(start, end));
            allFilteredHits.addAll(filteredHits);
            
            // get the nrseq protein ids
            start = System.currentTimeMillis();
            getNrseqIdsAndAccessions(filteredHits, runSearch.getRunSearchId());
            end = System.currentTimeMillis();
            log.info("Assigned NR_SEQ ids in: "+getTime(start, end));
        }
        
        log.info("Total Filtered Hits: "+allFilteredHits.size());
        
        getModifiedSequenceForPeptides(allFilteredHits);
        
        assginIdsToProteinsAndPeptides(allFilteredHits);
        
        
        // infer the protein list
        long start = System.currentTimeMillis();
        List<InferredProtein<SequestSpectrumMatch>> proteins = inferProteinList(allFilteredHits, searchSummary, params);
        long end = System.currentTimeMillis();
        log.info("# of Inferred Proteins: "+proteins.size()+". Time: "+getTime(start, end));
        

        // calculate protein sequence coverage
        start = System.currentTimeMillis();
        calculateProteinSequenceCoverage(proteins);
        end = System.currentTimeMillis();
        log.info("Calculated protein sequence coverage in: "+getTime(start, end));
        
        // save the results
        start = System.currentTimeMillis();
        ProteinferSaver.saveProteinInferenceResults(searchSummary, params, proteins);
        end = System.currentTimeMillis();
        log.info("Saved results in: "+getTime(start, end));
        
        long e = System.currentTimeMillis();
        log.info("Total time: "+getTime(s, e));
        
    }
    
   

    private static void calculateProteinSequenceCoverage(List<InferredProtein<SequestSpectrumMatch>> proteins) {
        
        for(InferredProtein<SequestSpectrumMatch> prot: proteins) {
            int nrseqId = prot.getProteinId();
            NrDbProtein dbProt = NrSeqLookupUtil.getDbProtein(nrseqId);
            NRProteinFactory nrpf = NRProteinFactory.getInstance();
            NRProtein protein = null;
            try {
                protein = (NRProtein)(nrpf.getProtein(dbProt.getProteinId()));
            }
            catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
                
            String parentSequence = protein.getPeptide().getSequenceString();
            List<String> peptides = new ArrayList<String>();
            for(PeptideEvidence<SequestSpectrumMatch> pev: prot.getPeptides()) {
                peptides.add(pev.getPeptideSeq());
            }
            int lengthCovered = StringUtils.getCoveredSequenceLength(parentSequence, peptides);
            float percCovered = ((float)lengthCovered/(float)parentSequence.length()) * 100.0f;
            //percCovered = (float) ((Math.round(percCovered*100.0))/100.0);
            prot.setPercentCoverage(percCovered);
        }
    }

    private static void getNrseqIdsAndAccessions(List<SequestHit> hits, int runSearchId) {
        DAOFactory fact = DAOFactory.instance();
        MsRunSearchDAO runSearchDao = fact.getMsRunSearchDAO();
        MsSearchDatabaseDAO dbDao = fact.getMsSequenceDatabaseDAO();
        
        MsRunSearch runSearch = runSearchDao.loadRunSearch(runSearchId);
        if(runSearch == null) {
            log.error("Could not load runSearch with id: "+runSearchId);
            return;
        }
        List<MsSearchDatabase> searchDbs = dbDao.loadSearchDatabases(runSearch.getSearchId());
        if(searchDbs.size() != 1) {
            log.warn("No search database found for searchID: "+runSearch.getSearchId());
            return;
        }
        int nrseqDbId = searchDbs.get(0).getSequenceDatabaseId();
        String dbname = searchDbs.get(0).getDatabaseFileName();
        
        Map<String, NrDbProtein> nrseqIdMap = new HashMap<String, NrDbProtein>();
       
        for(SequestHit hit: hits) {
           PeptideHit phit = hit.getPeptideHit();
//           if(phit.isDecoyPeptide())
//               continue;
           for(ProteinHit prHit: phit.getProteinList()) {
               Protein pr = prHit.getProtein();
               NrDbProtein nrDbProt = nrseqIdMap.get(pr.getAccession());
               if(nrDbProt == null) {
                   nrDbProt  = NrSeqLookupUtil.getDbProtein(nrseqDbId, pr.getAccession());
                   if(nrDbProt == null) {
                       List<Integer> ids = NrSeqLookupUtil.getDbProteinIdsLikeAccesion(dbname, pr.getAccession());
                       if(ids.size() != 1) {
                           ids = NrSeqLookupUtil.getDbProteinIdsForPeptidePartialAccession(nrseqDbId, pr.getAccession(),
                                   phit.getPeptide().getSequence());
                           if(ids.size() != 1) {
                               log.error("Could not find nrseq id for protein: "+pr.getAccession()+
                                           "; database: "+nrseqDbId+"; dbname: "+dbname);
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
               pr.setId(nrDbProt.getId());
               pr.setAccession(nrDbProt.getAccessionString());
           }
        }
    }

    private static void assginIdsToProteinsAndPeptides(List<SequestHit> allFilteredHits) {
        
        Map<String, Integer> peptideIds = new HashMap<String, Integer>(allFilteredHits.size());
        Map<String, Integer> proteinIds = new HashMap<String, Integer>(allFilteredHits.size());
        int lastProtId = 1;
        int lastPeptId = 1;
        for(SequestHit hit: allFilteredHits) {
            Peptide pept = hit.getPeptideHit().getPeptide();
            Integer id = peptideIds.get(pept.getModifiedSequence());
            if (id == null) {
                id = lastPeptId;
                peptideIds.put(pept.getModifiedSequence(), id);
                lastPeptId++;
            }
            pept.setId(id);
            
//            for(ProteinHit prot: hit.getPeptideHit().getProteinList()) {
//                Integer protId = proteinIds.get(prot.getAccession());
//                if (protId == null) {
//                    protId = lastProtId;
//                    proteinIds.put(prot.getAccession(), protId);
//                    lastProtId++;
//                }
//                prot.getProtein().setId(protId);
//            }
        }
    }

    private static void getModifiedSequenceForPeptides(List<SequestHit> allFilteredHits) {
        
        MsSearchModificationDAO modDao = DAOFactory.instance().getMsSearchModDAO();
        
        for (SequestHit hit: allFilteredHits) {
            List<MsResultResidueMod> resMods = modDao.loadDynamicResidueModsForResult(hit.getHitId());
            Peptide pept = hit.getPeptideHit().getPeptide();
            for(MsResultResidueMod mod: resMods) {
                pept.addModification(new PeptideModification(mod.getModifiedPosition(), mod.getModificationMass()));
            }
//            if(resMods.size() > 0) {
//                System.out.println(pept.getModifiedSequence()+" "+hit.getHitId()+" "+hit.getFdr());
//            }
        }
    }

    private static SearchSummary getSearchSummary(int searchId) {
        DAOFactory daoFactory = DAOFactory.instance();
        
        SearchSummary search = new SearchSummary(searchId);
        
        MsRunSearchDAO runSearchDao = daoFactory.getMsRunSearchDAO();
        List<Integer> runSearchIds = runSearchDao.loadRunSearchIdsForSearch(searchId);
        
        MsRunDAO runDao = daoFactory.getMsRunDAO();
        for (int id: runSearchIds) {
            MsRunSearch runSearch = runSearchDao.loadRunSearch(id);
            int runId = runSearch.getRunId();
            String filename = runDao.loadFilenameNoExtForRun(runId);
            int idx = filename.lastIndexOf('.');
            if (idx != -1)
                filename = filename.substring(0, idx);
            search.addRunSearch(new RunSearch(id, filename));
        }
        
        return search;
    }
}
