/**
 * GoEnrichmentAction.java
 * @author Vagisha Sharma
 * Jun 14, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
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
import org.yeastrc.bio.go.GOUtils;
import org.yeastrc.bio.taxonomy.Species;
import org.yeastrc.www.go.GOEnrichmentCalculator;
import org.yeastrc.www.go.GOEnrichmentInput;
import org.yeastrc.www.go.GOEnrichmentOutput;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;
import org.yeastrc.yates.YatesRun;

import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferRunDAO;
import edu.uwpr.protinfer.util.TimeUtils;

/**
 * 
 */
public class CompareGOEnrichmentAction extends Action {

    private static final Logger log = Logger.getLogger(CompareGOEnrichmentAction.class.getName());
    
    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {
        
        // User making this request
        User user = UserUtils.getUser(request);
        if (user == null) {
            ActionErrors errors = new ActionErrors();
            errors.add("username", new ActionMessage("error.login.notloggedin"));
            saveErrors( request, errors );
            return mapping.findForward("authenticate");
        }
        
        ProteinSetComparisonForm myForm = (ProteinSetComparisonForm) form;
        
        // Get the selected protein inference run ids
        List<Integer> piRunIds = myForm.getSelectedProteinferRunIds();
        
        List<Integer> dtaRunIds = myForm.getSelectedDtaRunIds();

        int total = piRunIds.size() + dtaRunIds.size();
        
        // Need atleast two datasets to compare.
        if(total < 2) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Please select 2 or more datasets to compare."));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        ProteinferDAOFactory fact = ProteinferDAOFactory.instance();
        ProteinferRunDAO runDao = fact.getProteinferRunDao();
        
        List<Dataset> datasets = new ArrayList<Dataset>(total);
        
        long s = System.currentTimeMillis();
        
        // Protein inference datasets
        for(int piRunId: piRunIds) {
            if(runDao.loadProteinferRun(piRunId) == null) {
                ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", 
                        "No protein inference run found with ID: "+piRunId+"."));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
            }
            Dataset dataset = DatasetBuilder.instance().buildDataset(piRunId, DatasetSource.PROT_INFER);
            datasets.add(dataset);
        }
        
        // DTASelect datasets
        for(int dtaRunId: dtaRunIds) {
            YatesRun run = new YatesRun();
            try {
                run.load(dtaRunId);
            }
            catch(Exception e) {
                ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Error loading DTASelect dataset with ID: "+dtaRunId+"."));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
            }
            Dataset dataset = DatasetBuilder.instance().buildDataset(dtaRunId, DatasetSource.DTA_SELECT);
            datasets.add(dataset);
        }
        
        // ANY AND, OR, NOT filters
        if((myForm.getAndList().size() == 0) && 
                (myForm.getOrList().size() == 0) && 
                (myForm.getNotList().size() == 0) &&
                myForm.getXorList().size() == 0) {
            List<SelectableDataset> sdsList = new ArrayList<SelectableDataset>(datasets.size());
            for(Dataset dataset: datasets) {
                SelectableDataset sds = new SelectableDataset(dataset);
                sds.setSelected(false);
                sdsList.add(sds);
            }

            myForm.setAndList(sdsList);
            myForm.setOrList(sdsList);
            myForm.setNotList(sdsList);
            myForm.setXorList(sdsList);
        }
        List<SelectableDataset> andDataset = myForm.getAndList();
        List<SelectableDataset> orDataset = myForm.getOrList();
        List<SelectableDataset> notDataset = myForm.getNotList();
        List<SelectableDataset> xorDataset = myForm.getXorList();

        List<Dataset> andFilters = new ArrayList<Dataset>();
        for(SelectableDataset sds: andDataset) {
            if(sds.isSelected())    andFilters.add(new Dataset(sds.getDatasetId(), sds.getSource()));
        }

        List<Dataset> orFilters = new ArrayList<Dataset>();
        for(SelectableDataset sds: orDataset) {
            if(sds.isSelected())    orFilters.add(new Dataset(sds.getDatasetId(), sds.getSource()));
        }

        List<Dataset> notFilters = new ArrayList<Dataset>();
        for(SelectableDataset sds: notDataset) {
            if(sds.isSelected())    notFilters.add(new Dataset(sds.getDatasetId(), sds.getSource()));
        }

        List<Dataset> xorFilters = new ArrayList<Dataset>();
        for(SelectableDataset sds: xorDataset) {
            if(sds.isSelected())    xorFilters.add(new Dataset(sds.getDatasetId(), sds.getSource()));
        }

        ProteinDatasetComparisonFilters filters = new ProteinDatasetComparisonFilters();
        filters.setAndFilters(andFilters);
        filters.setOrFilters(orFilters);
        filters.setNotFilters(notFilters);
        filters.setXorFilters(xorFilters);
        
        
        // Do the comparison
        ProteinComparisonDataset comparison = ProteinDatasetComparer.instance().compareDatasets(datasets, false);
        long e = System.currentTimeMillis();
        log.info("Time to compare datasets: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        // If the user is searching for some proteins by name, filter the list
        String searchString = myForm.getSearchString();
        if(searchString != null && searchString.trim().length() > 0) {
            ProteinDatasetComparer.instance().applySearchNameFilter(comparison, searchString);
        }
        
        // Apply AND, OR, NOT filters
        s = System.currentTimeMillis();
        ProteinDatasetComparer.instance().applyFilters(comparison, filters); // now apply all the filters
        e = System.currentTimeMillis();
        log.info("Time to filter results: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        comparison.initSummary();
        request.setAttribute("comparison", comparison);
        request.setAttribute("goEnrichmentView", true);
       
        GOEnrichmentOutput enrichment = doGoEnrichmentAnalysis(comparison, myForm);
        // Biological Process
        if(myForm.getGoAspect() == GOUtils.BIOLOGICAL_PROCESS) {
            
            if(myForm.isGoEnrichmentGraph()) {
                request.setAttribute("enrichedTerms", enrichment.getBiologicalProcessEnriched());
                return mapping.findForward("CreateGraph");
            }
            
            GOEnrichmentTabular bpTabular = new GOEnrichmentTabular();
            bpTabular.setEnrichedTerms(enrichment.getBiologicalProcessEnriched());
            bpTabular.setTitle("Biological Process");
            bpTabular.setNumProteinsInUniverse(enrichment.getTotalAnnotatedBiologicalProcess());
            bpTabular.setNumProteinsInSet(enrichment.getNumSpeciesProteins());
            request.setAttribute("bioProcessTerms", bpTabular);
        }
        
        // Cellular Component
        if(myForm.getGoAspect() == GOUtils.CELLULAR_COMPONENT) {
            
            if(myForm.isGoEnrichmentGraph()) {
                request.setAttribute("enrichedTerms", enrichment.getCellularComponentEnriched());
                return mapping.findForward("CreateGraph");
            }
            
            GOEnrichmentTabular ccTabular = new GOEnrichmentTabular();
            ccTabular.setEnrichedTerms(enrichment.getCellularComponentEnriched());
            ccTabular.setTitle("Cellular Component");
            ccTabular.setNumProteinsInUniverse(enrichment.getTotalAnnotatedCellularComponent());
            ccTabular.setNumProteinsInSet(enrichment.getNumSpeciesProteins());
            request.setAttribute("cellComponentTerms", ccTabular);
        }
        
        // Molecular Function
        if(myForm.getGoAspect() == GOUtils.MOLECULAR_FUNCTION) {
            
            if(myForm.isGoEnrichmentGraph()) {
                request.setAttribute("enrichedTerms", enrichment.getMolecularFunctionEnriched());
                return mapping.findForward("CreateGraph");
            }
            
            GOEnrichmentTabular mfTabular = new GOEnrichmentTabular();
            mfTabular.setEnrichedTerms(enrichment.getMolecularFunctionEnriched());
            mfTabular.setTitle("Molecular Function");
            mfTabular.setNumProteinsInUniverse(enrichment.getTotalAnnotatedMolecularFunction());
            mfTabular.setNumProteinsInSet(enrichment.getNumSpeciesProteins());
            request.setAttribute("molFunctionTerms", mfTabular);
        }
        
        
        request.setAttribute("enrichment", enrichment);
        request.setAttribute("species", Species.getInstance(myForm.getSpeciesId()));
        
        
        e = System.currentTimeMillis();
        log.info("CompareGOEnrichmentAction results in: "+TimeUtils.timeElapsedMinutes(s,e)+" minutes");
        return mapping.findForward("Success");
    }
    
    private GOEnrichmentOutput doGoEnrichmentAnalysis(ProteinComparisonDataset comparison, ProteinSetComparisonForm myForm) throws Exception {
        
        int speciesId = myForm.getSpeciesId();
        
        List<Integer> nrseqIds = new ArrayList<Integer>(comparison.getProteins().size());
        log.info(nrseqIds.size()+" proteins for GO enrichment analysis");
        for(ComparisonProtein protein: comparison.getProteins()) {
            nrseqIds.add(protein.getNrseqId());
        }
        
        GOEnrichmentInput input = new GOEnrichmentInput(speciesId);
        if(myForm.getGoAspect() == GOUtils.BIOLOGICAL_PROCESS)
            input.setUseBiologicalProcess(true);
        if(myForm.getGoAspect() == GOUtils.CELLULAR_COMPONENT)
            input.setUseCellularComponent(true);
        if(myForm.getGoAspect() == GOUtils.MOLECULAR_FUNCTION)
            input.setUseMolecularFunction(true);
        
        input.setPValCutoff(Double.parseDouble(myForm.getGoEnrichmentPVal()));
        input.setProteinIds(nrseqIds);
        
        GOEnrichmentOutput enrichment = GOEnrichmentCalculator.calculate(input);
        return enrichment;
    }
}
