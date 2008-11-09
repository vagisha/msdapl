/**
 * DoProteinInferenceAction.java
 * @author Vagisha Sharma
 * Nov 3, 2008
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchModificationDAO;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.proteinfer.IDPickerExecutor;
import org.yeastrc.proteinfer.IDPickerParams;
import org.yeastrc.proteinfer.SearchSummary;
import org.yeastrc.proteinfer.SearchSummary.RunSearch;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import edu.uwpr.protinfer.SequestHit;
import edu.uwpr.protinfer.SequestSpectrumMatch;
import edu.uwpr.protinfer.filter.FilterException;
import edu.uwpr.protinfer.filter.fdr.FdrCalculatorException;
import edu.uwpr.protinfer.infer.InferredProtein;
import edu.uwpr.protinfer.infer.Peptide;
import edu.uwpr.protinfer.infer.PeptideModification;
import edu.uwpr.protinfer.infer.ProteinHit;
import edu.uwpr.protinfer.msdata.MsDataSearchResultsReader;

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
        
        Date s = new Date();
        List<SequestHit> allFilteredHits = new ArrayList<SequestHit>();
        // get the search hits
        MsDataSearchResultsReader reader = new MsDataSearchResultsReader();
        
        for (RunSearch runSearch: searchSummary.getRunSearchList()) {
            if (!runSearch.getIsSelected())
                continue;
            Date start = new Date();
            List<SequestHit> hits = reader.getHitsForRunSearch(runSearch.getRunSearchId(), params.getDecoyPrefix());
            
            int targetCount = 0;
            int decoyCount = 0;
            for(SequestHit hit: hits) {
                if (hit.isDecoyMatch())
                    decoyCount++;
                else if (hit.isTargetMatch())
                    targetCount++;
            }
            runSearch.setTotalDecoyHits(decoyCount);
            runSearch.setTotalTargetHits(targetCount);
            
            Date end = new Date();
            log.info("File: "+runSearch.getRunName()+"; ID: "+runSearch.getRunSearchId()+"; # hits: "+hits.size()+"; Time: "+getTime(start, end));
            // filter the search hits
            start = new Date();
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
            runSearch.setFilteredTargetHits(filteredHits.size());
            end = new Date();
            log.info("File: "+runSearch.getRunName()+"; ID: "+runSearch.getRunSearchId()+"; # Filterted hits: "+filteredHits.size()+"; Time: "+getTime(start, end));
            allFilteredHits.addAll(filteredHits);
        }
        
        log.info("Total Filtered Hits: "+allFilteredHits.size());
        
        getModifiedSequenceForPeptides(searchSummary.getMsSearchId(), allFilteredHits);
        
        assginIdsToProteinsAndPeptides(allFilteredHits);
        
        // infer the protein list
        Date start = new Date();
        List<InferredProtein<SequestSpectrumMatch>> proteins = inferProteinList(allFilteredHits, params);
        Date end = new Date();
        log.info("# of Inferred Proteins: "+proteins.size()+". Time: "+getTime(start, end));
        Date e = new Date();
        log.info("Total time: "+getTime(s, e));
        
        request.getSession().setAttribute("inferredProteins", proteins);
        request.getSession().setAttribute("searchSummary", searchSummary);
        request.getSession().setAttribute("params", params);
        
        // Go!
        return mapping.findForward("Success");
    }
    
    private static float getTime(Date start, Date end) {
        long time = end.getTime() - start.getTime();
        float seconds = (float)time / (1000.0f);
        return seconds;
    }

    private static List<InferredProtein<SequestSpectrumMatch>> inferProteinList(
            List<SequestHit> filteredHits, IDPickerParams params) {
        IDPickerExecutor executor = new IDPickerExecutor();
        return executor.inferProteins(filteredHits, params);
    }

    private static List<SequestHit> filterHits(List<SequestHit> hits, IDPickerParams params) 
        throws FdrCalculatorException, FilterException {
        IDPickerExecutor executor = new IDPickerExecutor();
        return executor.filterSearchHits(hits, params);
    }
    
    public static void main(String[] args) {
        SearchSummary searchSummary = getSearchSummary(4);
        IDPickerParams params = new IDPickerParams();
        params.setDecoyRatio(1.0f);
        params.setDoParsimonyAnalysis(true);
        params.setMaxAbsoluteFdr(0.25f);
        params.setMaxRelativeFdr(0.25f);
        params.setMinDistinctPeptides(2);
        params.setRevDatabasePrefix("Reverse_");
        
        Date s = new Date();
        List<SequestHit> allFilteredHits = new ArrayList<SequestHit>();
        // get the search hits
        MsDataSearchResultsReader reader = new MsDataSearchResultsReader();
        for (RunSearch runSearch: searchSummary.getRunSearchList()) {
            Date start = new Date();
            List<SequestHit> hits = reader.getHitsForRunSearch(runSearch.getRunSearchId(), params.getDecoyPrefix());
            Date end = new Date();
            log.info("File: "+runSearch.getRunName()+"; ID: "+runSearch.getRunSearchId()+"; # hits: "+hits.size()+"; Time: "+getTime(start, end));
            // filter the search hits
            start = new Date();
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
            end = new Date();
            log.info("File: "+runSearch.getRunName()+"; ID: "+runSearch.getRunSearchId()+"; # Filterted hits: "+filteredHits.size()+"; Time: "+getTime(start, end));
            allFilteredHits.addAll(filteredHits);
            
        }
        
        log.info("Total Filtered Hits: "+allFilteredHits.size());
        
        getModifiedSequenceForPeptides(searchSummary.getMsSearchId(), allFilteredHits);
        
        assginIdsToProteinsAndPeptides(allFilteredHits);
        
        
        // infer the protein list
        Date start = new Date();
        List<InferredProtein<SequestSpectrumMatch>> proteins = inferProteinList(allFilteredHits, params);
        Date end = new Date();
        log.info("# of Inferred Proteins: "+proteins.size()+". Time: "+getTime(start, end));
        Date e = new Date();
        log.info("Total time: "+getTime(s, e));
        
    }
    
    private static void assginIdsToProteinsAndPeptides(List<SequestHit> allFilteredHits) {
        
        Map<String, Integer> peptideIds = new HashMap<String, Integer>(allFilteredHits.size());
        Map<String, Integer> proteinIds = new HashMap<String, Integer>(allFilteredHits.size());
        int lastProtId = 0;
        int lastPeptId = 0;
        for(SequestHit hit: allFilteredHits) {
            Peptide pept = hit.getPeptideHit().getPeptide();
            Integer id = peptideIds.get(pept.getModifiedSequence());
            if (id == null) {
                id = lastPeptId;
                peptideIds.put(pept.getModifiedSequence(), id);
                lastPeptId++;
            }
            pept.setId(id);
            
            for(ProteinHit prot: hit.getPeptideHit().getProteinList()) {
                Integer protId = proteinIds.get(prot.getAccession());
                if (protId == null) {
                    protId = lastProtId;
                    proteinIds.put(prot.getAccession(), protId);
                    lastProtId++;
                }
                prot.getProtein().setId(protId);
            }
        }
    }

    private static void getModifiedSequenceForPeptides(int searchId, List<SequestHit> allFilteredHits) {
        
        MsSearchModificationDAO modDao = DAOFactory.instance().getMsSearchModDAO();
        
        // load the modifications for this search
        List<MsResidueModification> dynaMods = modDao.loadDynamicResidueModsForSearch(searchId);
        // create a map for easy lookup
        Map<Integer, MsResidueModification> modMap = new HashMap<Integer, MsResidueModification>(dynaMods.size());
        for(MsResidueModification mod: dynaMods) {
            modMap.put(mod.getId(), mod);
        }
        
        for (SequestHit hit: allFilteredHits) {
            List<MsResultResidueMod> resMods = modDao.loadDynamicResidueModsForResult(hit.getHitId());
            Peptide pept = hit.getPeptideHit().getPeptide();
            for(MsResultResidueMod mod: resMods) {
                pept.addModification(new PeptideModification(mod.getModifiedPosition(), mod.getModificationMass()));
            }
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
