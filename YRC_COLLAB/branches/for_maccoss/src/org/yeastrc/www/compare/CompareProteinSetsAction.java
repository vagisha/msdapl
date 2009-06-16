/**
 * CompareProtInferResultsAction.java
 * @author Vagisha Sharma
 * Apr 9, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.www.compare.graph.ComparisonProteinGroup;
import org.yeastrc.www.compare.graph.GraphBuilder;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;
import org.yeastrc.yates.YatesRun;

import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferRunDAO;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferSpectrumMatchDAO;
import edu.uwpr.protinfer.infer.graph.BipartiteGraph;
import edu.uwpr.protinfer.infer.graph.GraphCollapser;
import edu.uwpr.protinfer.infer.graph.PeptideVertex;
import edu.uwpr.protinfer.util.TimeUtils;

/**
 * 
 */
public class CompareProteinSetsAction extends Action {

    private static final Logger log = Logger.getLogger(CompareProteinSetsAction.class);

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

        
        // get the protein inference ids to compare
        ProteinSetComparisonForm myForm = (ProteinSetComparisonForm) form;
        
        // IS THE USER DOWNLOADING?
        if(myForm.isDownload()) {
            log.info("DOWNLOADING......");
            return mapping.findForward("Download");
        }
        
        // GO ENRICHMENT ANALYSIS?
        if(myForm.isGoEnrichment()) {
            log.info("DOING GENE ONTOLOGY ENRICHMENT ANALYSIS...");
            return mapping.findForward("GOAnalysis");
        }
        
        
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

        // TODO this is temporary till results from DTASelect are fully supported.
        if(dtaRunIds.size() > 0) {
            request.setAttribute("dtasWarning", true);
        }
        
        // ANY AND, OR, NOT, XOR filters
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
        long s = System.currentTimeMillis();
        ProteinComparisonDataset comparison = ProteinDatasetComparer.instance().compareDatasets(datasets, myForm.isOnlyParsimonious());
        long e = System.currentTimeMillis();
        log.info("Time to compare datasets: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        // If the user is searching for some proteins by name, filter the list
        String searchString = myForm.getSearchString();
        if(searchString != null && searchString.trim().length() > 0) {
            ProteinDatasetComparer.instance().applySearchNameFilter(comparison, searchString);
        }
        
        // Apply AND, OR, NOT, XOR filters
        s = System.currentTimeMillis();
        ProteinDatasetComparer.instance().applyFilters(comparison, filters); // now apply all the filters
        e = System.currentTimeMillis();
        log.info("Time to filter results: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        
        request.setAttribute("proteinSetComparisonForm", myForm);
        
        // create a list of they dataset ids being compared
        request.setAttribute("piDatasetIds", makeCommaSeparated(piRunIds));
        request.setAttribute("dtaDatasetIds", makeCommaSeparated(dtaRunIds));
        
        
        if(!myForm.isGroupProteins()) {
            // Sort by peptide count
            s = System.currentTimeMillis();
            ProteinDatasetSorter sorter = ProteinDatasetSorter.instance();
            sorter.sortByPeptideCount(comparison);
            e = System.currentTimeMillis();
            log.info("Time to sort results: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
            
            // Set the page number
            comparison.setCurrentPage(myForm.getPageNum());
            
            comparison.initSummary(); // initialize the summary (totalProteinCount, # common proteins)
            
            // Create Venn Diagram only if 2 or 3 datasets are being compared
            if(comparison.getDatasetCount() == 2 || comparison.getDatasetCount() == 3) {
                String googleChartUrl = VennDiagramCreator.instance().getChartUrl(comparison);
                request.setAttribute("chart", googleChartUrl);
            }
            
            request.setAttribute("comparison", comparison);
            return mapping.findForward("ProteinList");
        }
        
        else {
            s = System.currentTimeMillis();
            GraphBuilder graphBuilder = new GraphBuilder();
            BipartiteGraph<ComparisonProteinGroup, PeptideVertex> graph = 
                graphBuilder.buildGraph(comparison.getProteins(), piRunIds);
            log.info("BEFORE collapsing graph: "+graph.getLeftVertices().size());
            GraphCollapser<ComparisonProteinGroup, PeptideVertex> collapser = new GraphCollapser<ComparisonProteinGroup, PeptideVertex>();
            collapser.collapseGraph(graph);
            
            List<ComparisonProteinGroup> proteinGroups = graph.getLeftVertices();
            log.info("AFTER collapsing graph: "+proteinGroups.size());
            
            
            // assign group IDs
            int groupId = 1;
            for(ComparisonProteinGroup group: proteinGroups)
                group.setGroupId(groupId++);
            // sort
            Collections.sort(proteinGroups, new Comparator<ComparisonProteinGroup>() {
                @Override
                public int compare(ComparisonProteinGroup o1,
                        ComparisonProteinGroup o2) {
                    return Integer.valueOf(o2.getMaxPeptideCount()).compareTo(o1.getMaxPeptideCount());
                }});
            
            // remove protein groups that do not have any parsimonious proteins
            Iterator<ComparisonProteinGroup> iter = proteinGroups.iterator();
            while(iter.hasNext()) {
                ComparisonProteinGroup proteinGroup = iter.next();
                if(!proteinGroup.hasParsimoniousProtein())
                    iter.remove();
            }
            log.info("AFTER removing non-parsimonious groups: "+proteinGroups.size());
            
            e = System.currentTimeMillis();
            log.info("Time to do graph analysis: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
            
            ProteinGroupComparisonDataset grpComparison = new ProteinGroupComparisonDataset();
            for(ComparisonProteinGroup grp: proteinGroups)
                grpComparison.addProteinGroup(grp);
            
            grpComparison.setDatasets(datasets);
            
            // Set the page number
            grpComparison.setCurrentPage(myForm.getPageNum());
            
            // set the information for displaying normalized spectrum counts
            ProteinferSpectrumMatchDAO specDao = ProteinferDAOFactory.instance().getProteinferSpectrumMatchDao();
            for(Dataset dataset: grpComparison.getDatasets()) {
                if(dataset.getSource() == DatasetSource.PROT_INFER) {
                    int spectrumCount = specDao.getSpectrumCountForPinferRun(dataset.getDatasetId());
                    dataset.setSpectrumCount(spectrumCount);
                }
            }
            
            grpComparison.initSummary(); // initialize the summary -- 
                                        // (totalProteinCount, # common proteins)
                                        // spectrum count normalization factors
                                        // calculate min/max normalized spectrum counts for scaling
            
            
            // Create Venn Diagram only if 2 or 3 datasets are being compared
            if(grpComparison.getDatasetCount() == 2 || grpComparison.getDatasetCount() == 3) {
                String googleChartUrl = VennDiagramCreator.instance().getChartUrl(grpComparison);
                request.setAttribute("chart", googleChartUrl);
            }
            
            request.setAttribute("comparison", grpComparison);
            return mapping.findForward("ProteinGroupList");
        }
    }

    private String makeCommaSeparated(List<Integer> ids) {
        StringBuilder buf = new StringBuilder();
        for(int id: ids)
            buf.append(","+id);
        if(buf.length() > 0)
            buf.deleteCharAt(0);
        return buf.toString();
    }
    
}
