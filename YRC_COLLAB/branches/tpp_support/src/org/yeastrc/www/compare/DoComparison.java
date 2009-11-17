/**
 * DoComparison.java
 * @author Vagisha Sharma
 * Nov 15, 2009
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
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferSpectrumMatchDAO;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetFilterCriteria;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.www.compare.dataset.Dataset;
import org.yeastrc.www.compare.dataset.DatasetBuilder;
import org.yeastrc.www.compare.dataset.DatasetSource;
import org.yeastrc.www.compare.dataset.FilterableDataset;
import org.yeastrc.www.compare.dataset.ProteinProphetDataset;
import org.yeastrc.www.compare.dataset.ProteinferDataset;
import org.yeastrc.www.compare.graph.ComparisonProteinGroup;
import org.yeastrc.www.compare.graph.GraphBuilder;
import org.yeastrc.www.compare.util.VennDiagramCreator;

import edu.uwpr.protinfer.infer.graph.BipartiteGraph;
import edu.uwpr.protinfer.infer.graph.GraphCollapser;
import edu.uwpr.protinfer.infer.graph.PeptideVertex;

/**
 * 
 */
public class DoComparison extends Action {

    private static final Logger log = Logger.getLogger(DoComparison.class);
    
    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {
        
        
        // Form we will use
        ProteinSetComparisonForm myForm = (ProteinSetComparisonForm) form; // request.getAttribute("comparisonFrom");
        if(myForm == null) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "No comparison form in request."));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        // Get the selected protein inference run ids
        List<Integer> allRunIds = myForm.getAllSelectedRunIds();
        
        // Need atleast two datasets to compare.
        if(allRunIds.size() < 2) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Please select 2 or more datasets to compare."));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        // Get the datasets we will be comparing
        List<FilterableDataset> datasets = new ArrayList<FilterableDataset>(allRunIds.size());
        
        for(int piRunId: allRunIds) {
            
            FilterableDataset dataset = DatasetBuilder.instance().buildFilterableDataset(piRunId);
            if(dataset == null) {
                ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", 
                        "No protein inference run found with ID: "+piRunId+"."));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
            }
            
            if(dataset.getSource() == DatasetSource.PROTINFER) {
                ((ProteinferDataset)dataset).setProteinFilterCriteria(myForm.getFilterCriteria());
            }
            else if(dataset.getSource() == DatasetSource.PROTEIN_PROPHET) {
                ProteinProphetFilterCriteria filterCriteria = new ProteinProphetFilterCriteria(myForm.getFilterCriteria());
                double minProbability = ((ProteinProphetDataset)dataset).getRoc().getMinProbabilityForError(myForm.getErrorRateDouble());
                if(myForm.getUseProteinGroupProbability())
                    filterCriteria.setMinGroupProbability(minProbability);
                else filterCriteria.setMinProteinProbability(minProbability);
                ((ProteinProphetDataset)dataset).setProteinFilterCriteria(filterCriteria);
            }
            datasets.add(dataset);
        }
        
        // ANY AND, OR, NOT, XOR filters
        ProteinDatasetComparisonFilters filters = myForm.getSelectedBooleanFilters();
        
        
        // Do the comparison
        long s = System.currentTimeMillis();
        ProteinComparisonDataset comparison = ProteinDatasetComparer.instance().compareDatasets(datasets, myForm.getParsimoniousParam());
        long e = System.currentTimeMillis();
        log.info("Time to compare datasets: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        // If the user is searching for some proteins by name, filter the list
        String searchString = myForm.getAccessionLike();
        if(searchString != null && searchString.trim().length() > 0) {
            ProteinDatasetComparer.instance().applySearchNameFilter(comparison, searchString);
        }
        
        // Apply AND, OR, NOT, XOR filters
        s = System.currentTimeMillis();
        ProteinDatasetComparer.instance().applyFilters(comparison, filters); // now apply all the filters
        e = System.currentTimeMillis();
        log.info("Time to filter results: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        
        // GO ENRICHMENT ANALYSIS?
        if(myForm.isGoEnrichment()) {
            log.info("DOING GENE ONTOLOGY ENRICHMENT ANALYSIS...");
            comparison.initSummary(); // initialize the summary (totalProteinCount, # common proteins)
            request.setAttribute("comparisonForm", myForm);
            request.setAttribute("comparisonDataset", comparison);
            return mapping.findForward("GOAnalysis");
        }
        
        
        if(!myForm.getGroupIndistinguishableProteins()) {
            // Sort by peptide count
            s = System.currentTimeMillis();
            ProteinDatasetSorter sorter = ProteinDatasetSorter.instance();
            sorter.sortByPeptideCount(comparison);
            e = System.currentTimeMillis();
            log.info("Time to sort results: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
            
            comparison.initSummary(); // initialize the summary (totalProteinCount, # common proteins)
            
            // IS THE USER DOWNLOADING?
            if(myForm.isDownload()) {
                log.info("DOWNLOADING......");
                request.setAttribute("comparisonForm", myForm);
                request.setAttribute("comparisonDataset", comparison);
                return mapping.findForward("Download");
            }
            
            // Set the page number
            comparison.setCurrentPage(myForm.getPageNum());
            
            // Create Venn Diagram only if 2 or 3 datasets are being compared
            if(comparison.getDatasetCount() == 2 || comparison.getDatasetCount() == 3) {
                String googleChartUrl = VennDiagramCreator.instance().getChartUrl(comparison);
                request.setAttribute("chart", googleChartUrl);
            }
            
            // create a list of the dataset ids being compared
            request.setAttribute("datasetIds", makeCommaSeparated(allRunIds));
            
            request.setAttribute("comparison", comparison);
            return mapping.findForward("ProteinList");
        }
        
        
        
        else {
            s = System.currentTimeMillis();
            GraphBuilder graphBuilder = new GraphBuilder();
            BipartiteGraph<ComparisonProteinGroup, PeptideVertex> graph = 
                graphBuilder.buildGraph(comparison.getProteins(), allRunIds);
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
            
            
            // set the information for displaying normalized spectrum counts
            ProteinferSpectrumMatchDAO specDao = ProteinferDAOFactory.instance().getProteinferSpectrumMatchDao();
            for(Dataset dataset: grpComparison.getDatasets()) {
                if(dataset.getSource() != DatasetSource.DTA_SELECT) {
                    int spectrumCount = specDao.getSpectrumCountForPinferRun(dataset.getDatasetId());
                    dataset.setSpectrumCount(spectrumCount);
                }
            }
            
            grpComparison.initSummary(); // initialize the summary -- 
                                        // (totalProteinCount, # common proteins)
                                        // spectrum count normalization factors
                                        // calculate min/max normalized spectrum counts for scaling
            
            // IS THE USER DOWNLOADING?
            if(myForm.isDownload()) {
                log.info("DOWNLOADING......");
                request.setAttribute("comparisonForm", myForm);
                request.setAttribute("comparisonGroupDataset", grpComparison);
                return mapping.findForward("Download");
            }
            
            // Set the page number
            grpComparison.setCurrentPage(myForm.getPageNum());
            
            
            // Create Venn Diagram only if 2 or 3 datasets are being compared
            if(grpComparison.getDatasetCount() == 2 || grpComparison.getDatasetCount() == 3) {
                String googleChartUrl = VennDiagramCreator.instance().getChartUrl(grpComparison);
                request.setAttribute("chart", googleChartUrl);
            }
            
            // create a list of the dataset ids being compared
            request.setAttribute("datasetIds", makeCommaSeparated(allRunIds));
            
            request.setAttribute("comparison", grpComparison);
            return mapping.findForward("ProteinGroupList");
        }
    }

    private String makeCommaSeparated(List<Integer> ... idLists) {
        StringBuilder buf = new StringBuilder();
        for(List<Integer> ids: idLists) {
            if(ids == null)
                continue;
            for(int id: ids)
                buf.append(","+id);
            if(buf.length() > 0)
                buf.deleteCharAt(0);
        }
        return buf.toString();
    }
}
