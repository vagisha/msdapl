/**
 * CompareProtInferResultsAction.java
 * @author Vagisha Sharma
 * Apr 9, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import org.yeastrc.jobqueue.MSJob;
import org.yeastrc.jobqueue.MSJobFactory;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.SORT_ORDER;
import org.yeastrc.www.compare.graph.ComparisonProteinGroup;
import org.yeastrc.www.compare.graph.GraphBuilder;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;
import org.yeastrc.yates.YatesRun;

import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferRunDAO;
import edu.uwpr.protinfer.database.dto.ProteinFilterCriteria.SORT_BY;
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
        
        
        // Apply AND, OR, NOT, XOR filters
        s = System.currentTimeMillis();
        ProteinDatasetBooleanFilterer.instance().applyFilters(comparison, filters); // now apply all the filters
        e = System.currentTimeMillis();
        log.info("Time to filter results: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        
        request.setAttribute("proteinSetComparisonForm", myForm);
        
        // create a list of they dataset ids being compared
        request.setAttribute("piDatasetIds", makeCommaSeparated(piRunIds));
        request.setAttribute("dtaDatasetIds", makeCommaSeparated(dtaRunIds));
        
        SORT_BY sortBy = myForm.getSortBy();
        SORT_ORDER sortOrder = myForm.getSortOrder();
        
        // If the user is NOT interested in keeping protein groups intact after filtering 
        // OR proteins are not being grouped
        // apply filters now
        if(!myForm.isKeepProteinGroups() || !myForm.isGroupProteins()) {
            
            // If the user is searching for some proteins by name, filter the list
            String nameSearchString = myForm.getNameSearchString();
            if(nameSearchString != null && nameSearchString.trim().length() > 0) {
                ProteinDatasetPropertiesFilterer.instance().applyFastaNameFilter(comparison.getProteins(), nameSearchString);
            }
            
            // If the user is searching for some proteins by description, filter the list
            String descSearchString = myForm.getDescriptionSearchString();
            if(descSearchString != null && descSearchString.trim().length() > 0) {
                ProteinDatasetPropertiesFilterer.instance().applyDescriptionFilter(comparison.getProteins(), comparison.getFastaDatabaseIds(), descSearchString);
            }
            
            // If there are molecular wt. or pI filters apply them now
            if(myForm.getMinMolecularWtDouble() != null || myForm.getMaxMolecularWtDouble() != null) {
                double min = myForm.getMinMolecularWtDouble() == null ? 0.0 : myForm.getMinMolecularWtDouble();
                double max = myForm.getMaxMolecularWtDouble() == null ? Double.MAX_VALUE : myForm.getMaxMolecularWtDouble();
                ProteinDatasetPropertiesFilterer.instance().applyMolecularWtFilter(comparison.getProteins(), min, max);
            }
            
            if(myForm.getMinPiDouble() != null || myForm.getMaxPiDouble() != null) {
                double min = myForm.getMinPiDouble() == null ? 0.0 : myForm.getMinPiDouble();
                double max = myForm.getMaxPiDouble() == null ? Double.MAX_VALUE : myForm.getMaxPiDouble();
                ProteinDatasetPropertiesFilterer.instance().applyPiFilter(comparison.getProteins(), min, max);
            }
        }
        
        if(!myForm.isGroupProteins()) {
            
            // Sort the results
            s = System.currentTimeMillis();
            
            ProteinDatasetSorter sorter = ProteinDatasetSorter.instance();
            if(sortBy == SORT_BY.MOL_WT)
                sorter.sortByMolecularWeight(comparison, sortOrder);
            else if (sortBy == SORT_BY.PI)
                sorter.sortByPi(comparison, sortOrder);
            else
                sorter.sortByPeptideCount(comparison, sortOrder);
            
            e = System.currentTimeMillis();
            log.info("Time to sort results: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
            
            // sorting order
            comparison.setSortBy(myForm.getSortBy());
            comparison.setSortOrder(myForm.getSortOrder());
            
            // Set the page number
            comparison.setCurrentPage(myForm.getPageNum());
            
            comparison.initSummary(); // initialize the summary (totalProteinCount, # common proteins)
            
            // Create Venn Diagram only if 2 or 3 datasets are being compared
            if(comparison.getDatasetCount() == 2 || comparison.getDatasetCount() == 3) {
                String googleChartUrl = VennDiagramCreator.instance().getChartUrl(comparison);
                request.setAttribute("chart", googleChartUrl);
            }
            
            comparison.setShowFullDescriptions(myForm.isShowFullDescriptions());
            
            request.setAttribute("comparison", comparison);
            request.setAttribute("showGOForm", isSpeciesYeast(datasets));
            return mapping.findForward("ProteinList");
        }
        
        else {
            s = System.currentTimeMillis();
            GraphBuilder graphBuilder = new GraphBuilder();
            BipartiteGraph<ComparisonProteinGroup, PeptideVertex> graph = graphBuilder.buildGraph(comparison.getProteins(), piRunIds);
            log.info("BEFORE collapsing graph: "+graph.getLeftVertices().size());
            GraphCollapser<ComparisonProteinGroup, PeptideVertex> collapser = new GraphCollapser<ComparisonProteinGroup, PeptideVertex>();
            collapser.collapseGraph(graph);
            
            List<ComparisonProteinGroup> proteinGroups = graph.getLeftVertices();
            log.info("AFTER collapsing graph: "+proteinGroups.size());
            
            
            // assign group IDs
            int groupId = 1;
            for(ComparisonProteinGroup group: proteinGroups)
                group.setGroupId(groupId++);
            
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
            
            // If the user IS interested in keeping protein groups intact after filtering 
            // apply filters now
            if(myForm.isKeepProteinGroups()) {
                // If the user is searching for some proteins by name, filter the list
                String nameSearchString = myForm.getNameSearchString();
                if(nameSearchString != null && nameSearchString.trim().length() > 0) {
                    ProteinDatasetPropertiesFilterer.instance().applyFastaNameFilterToGroup(proteinGroups, nameSearchString);
                }

                // If the user is searching for some proteins by description, filter the list
                String descSearchString = myForm.getDescriptionSearchString();
                if(descSearchString != null && descSearchString.trim().length() > 0) {
                    ProteinDatasetPropertiesFilterer.instance().applyDescriptionFilterToGroup(proteinGroups, comparison.getFastaDatabaseIds(), descSearchString);
                }

                // If there are molecular wt. or pI filters apply them now
                if(myForm.getMinMolecularWtDouble() != null || myForm.getMaxMolecularWtDouble() != null) {
                    double min = myForm.getMinMolecularWtDouble() == null ? 0.0 : myForm.getMinMolecularWtDouble();
                    double max = myForm.getMaxMolecularWtDouble() == null ? Double.MAX_VALUE : myForm.getMaxMolecularWtDouble();
                    ProteinDatasetPropertiesFilterer.instance().applyMolecularWtFilterToGroup(proteinGroups, min, max);
                }

                if(myForm.getMinPiDouble() != null || myForm.getMaxPiDouble() != null) {
                    double min = myForm.getMinPiDouble() == null ? 0.0 : myForm.getMinPiDouble();
                    double max = myForm.getMaxPiDouble() == null ? Double.MAX_VALUE : myForm.getMaxPiDouble();
                    ProteinDatasetPropertiesFilterer.instance().applyPiFilterToGroup(proteinGroups, min, max);
                }
            }
            
            // Sort the results
            s = System.currentTimeMillis();
            
            ProteinDatasetSorter sorter = ProteinDatasetSorter.instance();
            if(sortBy == SORT_BY.MOL_WT)
                sorter.sortByMolecularWeight(proteinGroups, sortOrder);
            else if (sortBy == SORT_BY.PI)
                sorter.sortByPi(proteinGroups, sortOrder);
            else
                sorter.sortByPeptideCount(proteinGroups, sortOrder);
            
            e = System.currentTimeMillis();
            log.info("Time to sort results: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
            
            
            ProteinGroupComparisonDataset grpComparison = new ProteinGroupComparisonDataset();
            for(ComparisonProteinGroup grp: proteinGroups)
                grpComparison.addProteinGroup(grp);
            
            grpComparison.setDatasets(datasets);
            
            
            // Set the page number
            grpComparison.setCurrentPage(myForm.getPageNum());
            
            // sorting order
            grpComparison.setSortBy(myForm.getSortBy());
            grpComparison.setSortOrder(myForm.getSortOrder());
            
            grpComparison.initSummary(); // initialize the summary -- 
                                        // (totalProteinCount, # common proteins)
                                        // spectrum count normalization factors
                                        // calculate min/max normalized spectrum counts for scaling
            
            
            // Create Venn Diagram only if 2 or 3 datasets are being compared
            if(grpComparison.getDatasetCount() == 2 || grpComparison.getDatasetCount() == 3) {
                String googleChartUrl = VennDiagramCreator.instance().getChartUrl(grpComparison);
                request.setAttribute("chart", googleChartUrl);
            }
            
            grpComparison.setShowFullDescriptions(myForm.isShowFullDescriptions());
            
            request.setAttribute("comparison", grpComparison);
            request.setAttribute("showGOForm", isSpeciesYeast(datasets));
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
    
    private boolean isSpeciesYeast(List<Dataset> datasets) throws Exception {
        
        
        Set<Integer> notYeastExpts = new HashSet<Integer>();
        
        ProteinferRunDAO runDao = ProteinferDAOFactory.instance().getProteinferRunDao();
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        
        for(Dataset dataset: datasets) {
            if(dataset.getSource() == DatasetSource.PROT_INFER) {
                List<Integer> searchIds = runDao.loadSearchIdsForProteinferRun(dataset.getDatasetId());
                if(searchIds != null) {
                   for(int searchId: searchIds) {
                       
                       MsSearch search = searchDao.loadSearch(searchId);
                       
                       if(notYeastExpts.contains(search.getExperimentId())) // if we have already seen this and it is not yeast go on looking
                           continue;
                       
                       MSJob job = MSJobFactory.getInstance().getJobForExperiment(search.getExperimentId());
                       
                       if(job.getTargetSpecies() == 4932) {
                           return true;
                       }
                       else 
                           notYeastExpts.add(search.getExperimentId());
                   }
                }
            }
        }
        return false;
    }
    
}
