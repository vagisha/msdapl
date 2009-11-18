/**
 * DownloadComparisonResults.java
 * @author Vagisha Sharma
 * May 4, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
import edu.uwpr.protinfer.infer.graph.BipartiteGraph;
import edu.uwpr.protinfer.infer.graph.GraphCollapser;
import edu.uwpr.protinfer.infer.graph.PeptideVertex;
import edu.uwpr.protinfer.util.TimeUtils;

/**
 * 
 */
public class DownloadComparisonResults extends Action {

    private static final Logger log = Logger.getLogger(DownloadComparisonResults.class.getName());
    
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        
        log.info("Downloading comparison results");
        
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
        
        
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition","attachment; filename=\"ProteinSetComparison.txt\"");
        response.setHeader("cache-control", "no-cache");
        PrintWriter writer = response.getWriter();
        writer.write("\n\n");
        writer.write("Date: "+new Date()+"\n\n");
        
        long startTime = System.currentTimeMillis();
        
        // Do the comparison
        long s = System.currentTimeMillis();
        ProteinComparisonDataset comparison = ProteinDatasetComparer.instance().compareDatasets(datasets, false);
        long e = System.currentTimeMillis();
        log.info("Time to compare datasets: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        // If the user is searching for some proteins by name, filter the list
        String searchString = myForm.getNameSearchString();
        if(searchString != null && searchString.trim().length() > 0) {
            ProteinDatasetComparer.instance().applySearchNameFilter(comparison, searchString);
        }
        
        // If the user is searching for some proteins by description, filter the list
        String descSearchString = myForm.getDescriptionSearchString();
        if(descSearchString != null && descSearchString.trim().length() > 0) {
            ProteinDatasetComparer.instance().applyDescriptionFilter(comparison, descSearchString);
        }
        
        // Apply AND, OR, NOT, XOR filters
        s = System.currentTimeMillis();
        ProteinDatasetComparer.instance().applyFilters(comparison, filters); // now apply all the filters
        e = System.currentTimeMillis();
        log.info("Time to filter results: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        
        if(!myForm.isGroupProteins()) {
            
            // Sort by peptide count
            s = System.currentTimeMillis();
            ProteinDatasetSorter sorter = ProteinDatasetSorter.instance();
            sorter.sortByPeptideCount(comparison);
            e = System.currentTimeMillis();
            log.info("Time to sort results: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
            
            comparison.initSummary(); // initialize the summary (totalProteinCount, # common proteins)
            
            s = System.currentTimeMillis();
            writeResults(writer, comparison, filters, myForm);
            writer.close();
            log.info("Results written in: "+TimeUtils.timeElapsedMinutes(s,e)+" minutes");
            
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
            
            grpComparison.initSummary(); // initialize the summary -- 
                                         // (totalProteinCount, # common proteins)
                                         // spectrum count normalization factors
                                         // calculate min/max normalized spectrum counts for scaling
            
            s = System.currentTimeMillis();
            writeResults(writer, grpComparison, filters, myForm);
            writer.close();
            log.info("Results written in: "+TimeUtils.timeElapsedMinutes(s,e)+" minutes");
        }
        
        e = System.currentTimeMillis();
        log.info("DownloadComparisonResults results in: "+TimeUtils.timeElapsedMinutes(startTime,e)+" minutes");
        return null;
    }

    
    private void writeResults(PrintWriter writer, ProteinComparisonDataset comparison, ProteinDatasetComparisonFilters filters,
            ProteinSetComparisonForm form) {
        
        writer.write("Total protein count: "+comparison.getTotalProteinCount()+"\n");
        writer.write("Filtered protein count: "+comparison.getFilteredProteinCount()+"\n");
        writer.write("\n\n");
        
        // Boolean Filters
        writeFilters(writer, filters);
        
        
        // Accession string filter
        String searchString = form.getNameSearchString();
        if(searchString != null && searchString.trim().length() > 0) {
            writer.write("Filtering for FASTA ID(s): "+searchString+"\n\n");
        }
        
        // Description string filter
        String descString = form.getDescriptionSearchString();
        if(descString != null && descString.trim().length() > 0) {
            writer.write("Filtering for description term(s): "+descString+"\n\n");
        }
        
        
        // Datasets
        writer.write("Datasets: \n");
        int idx = 0;
        for(Dataset dataset: comparison.getDatasets()) {
            writer.write(dataset.getSourceString()+" ID "+dataset.getDatasetId()+
                    ": Proteins  "+comparison.getProteinCount(idx++)+
                    "; SpectrumCount(max.) "+dataset.getSpectrumCount()+"("+dataset.getMaxProteinSpectrumCount()+")\n");
        }
        writer.write("\n\n");
        
        // Common protein groups
        writer.write("Common Proteins:\n");
        writer.write("\t");
        for(Dataset dataset: comparison.getDatasets()) {
            writer.write("ID_"+dataset.getDatasetId()+"\t");
        }
        writer.write("\n");
        for(int i = 0; i < comparison.getDatasetCount(); i++) {
            writer.write("ID_"+comparison.getDatasets().get(i).getDatasetId()+"\t");
            for(int j = 0; j < comparison.getDatasetCount(); j++) 
                writer.write(comparison.getCommonProteinCount(i, j)+"\t");
            writer.write("\n");
        }
        writer.write("\n\n");
        
        
        // legend
        // *  Present and Parsimonious
        // =  Present and NOT parsimonious
        // g  group protein
        // -  NOT present
        writer.write("\n\n");
        writer.write("*  Protein present and parsimonious\n");
        writer.write("=  Protein present and NOT parsimonious\n");
        writer.write("-  Protein NOT present\n");
        writer.write("g  Group protein\n");
        writer.write("\n\n");
        

        // print each protein
        writer.write("ProteinID\t");
        writer.write("Name\t");
        writer.write("CommonName\t");
        writer.write("Mol.Wt.\tpI\t");
        writer.write("NumPept\t");
        for(Dataset dataset: comparison.getDatasets()) {
            writer.write(dataset.getSourceString()+"("+dataset.getDatasetId()+")\t");
        }
        // spectrum count column headers.
        for(Dataset dataset: comparison.getDatasets()) {
            writer.write("SC("+dataset.getDatasetId()+")\t");
        }
        // NSAF column headers.
        for(Dataset dataset: comparison.getDatasets()) {
            writer.write("NSAF("+dataset.getDatasetId()+")\t");
        }
        writer.write("Description\n");
        
        for(ComparisonProtein protein: comparison.getProteins()) {
            
            comparison.initializeProteinInfo(protein);
            
            writer.write(protein.getNrseqId()+"\t");
            writer.write(protein.getFastaName()+"\t");
            writer.write(protein.getCommonName()+"\t");
            writer.write(protein.getMolecularWeight()+"\t");
            writer.write(protein.getPi()+"\t");
            writer.write(protein.getMaxPeptideCount()+"\t");
           
            for(Dataset dataset: comparison.getDatasets()) {
                DatasetProteinInformation dpi = protein.getDatasetProteinInformation(dataset);
                if(dpi == null || !dpi.isPresent()) {
                    writer.write("-");
                }
                else {
                    if(dpi.isParsimonious()) {
                        writer.write("*");
                    }
                    else {
                        writer.write("=");
                    }
                    if(dpi.isGrouped())
                        writer.write("g");
                }
                writer.write("\t");
            }
            // spectrum count information
            for(Dataset dataset: comparison.getDatasets()) {
                
                DatasetProteinInformation dpi = protein.getDatasetProteinInformation(dataset);
                if(dpi == null || !dpi.isPresent()) {
                    writer.write("0\t");
                }
                else {
                    writer.write(dpi.getSpectrumCount()+"("+comparison.getScaledSpectrumCount(dpi.getNormalizedSpectrumCount())+")\t");
                }
            }
            // NSAF information
            for(Dataset dataset: comparison.getDatasets()) {
                
                DatasetProteinInformation dpi = protein.getDatasetProteinInformation(dataset);
                if(dpi == null || !dpi.isPresent()) {
                    writer.write("-1.0\t");
                }
                else {
                    writer.write(dpi.getNsafFormatted()+"\t");
                }
            }
            writer.write(protein.getDescription()+"\n");
        }
        
        writer.write("\n\n");
    }


    private void writeFilters(PrintWriter writer,
            ProteinDatasetComparisonFilters filters) {
        
        boolean filtersFound = false;
        writer.write("Boolean Filters: \n");
        
        
        if(filters.getAndFilters().size() > 0) {
            filtersFound = true;
            writer.write("AND:\n");
            for(Dataset ds: filters.getAndFilters()) {
                writer.write("\t"+ds.getDatasetId()+"  "+ds.getDatasetComments()+"\n");
            }
        }
        if(filters.getOrFilters().size() > 0) {
            filtersFound = true;
            writer.write("OR:\n");
            for(Dataset ds: filters.getOrFilters()) {
                writer.write("\t"+ds.getDatasetId()+"  "+ds.getDatasetComments()+"\n");
            }
        }
        if(filters.getNotFilters().size() > 0) {
            filtersFound = true;
            writer.write("NOT:\n");
            for(Dataset ds: filters.getNotFilters()) {
                writer.write("\t"+ds.getDatasetId()+"  "+ds.getDatasetComments()+"\n");
            }
        }
        if(filters.getXorFilters().size() > 0) {
            filtersFound = true;
            writer.write("XOR:\n");
            for(Dataset ds: filters.getXorFilters()) {
                writer.write("\t"+ds.getDatasetId()+"  "+ds.getDatasetComments()+"\n");
            }
        }
        if(filtersFound)
            writer.write("\n\n");
        else
            writer.write("No filters found\n\n");
    }
    
    private void writeResults(PrintWriter writer, ProteinGroupComparisonDataset comparison, ProteinDatasetComparisonFilters filters,
            ProteinSetComparisonForm form) {
        
      writer.write("Total Protein Groups (Total Proteins): "+comparison.getTotalProteinGroupCount()+" ("+comparison.getTotalProteinCount()+")\n");
      writer.write("\n\n");
      
      // Boolean Filters
      writeFilters(writer, filters);
      
      
      // Accession string filter
      String searchString = form.getNameSearchString();
      if(searchString != null && searchString.trim().length() > 0) {
          writer.write("Filtering for FASTA ID(s): "+searchString+"\n\n");
      }
      
      // Description string filter
      String descString = form.getDescriptionSearchString();
      if(descString != null && descString.trim().length() > 0) {
          writer.write("Filtering for description term(s): "+descString+"\n\n");
      }
      
      // Datasets
      writer.write("Datasets: \n");
      int idx = 0;
      for(Dataset dataset: comparison.getDatasets()) {
          writer.write(dataset.getSourceString()+" ID "+dataset.getDatasetId()+
                  ": Proteins Groups (# Proteins)  "+comparison.getProteinGroupCount(idx)+" ("+comparison.getProteinCount(idx++)+") "+
                  "; SpectrumCount(max.) "+dataset.getSpectrumCount()+"("+dataset.getMaxProteinSpectrumCount()+")\n");
      }
      writer.write("\n\n");
      
      // Common protein groups
      writer.write("Common Proteins:\n");
      writer.write("\t");
      for(Dataset dataset: comparison.getDatasets()) {
          writer.write("ID_"+dataset.getDatasetId()+"\t");
      }
      writer.write("\n");
      for(int i = 0; i < comparison.getDatasetCount(); i++) {
          writer.write("ID_"+comparison.getDatasets().get(i).getDatasetId()+"\t");
          for(int j = 0; j < comparison.getDatasetCount(); j++) {
              writer.write(comparison.getCommonProteinGroupCount(i, j)+" ("+comparison.getCommonProteinGroupsPerc(i, j)+"%)\t");
          }
          writer.write("\n");
      }
      writer.write("\n\n");
      
      
      // legend
      // *  Present and Parsimonious
      // =  Present and NOT parsimonious
      // g  group protein
      // -  NOT present
      writer.write("\n\n");
      writer.write("*  Protein present and parsimonious\n");
      writer.write("=  Protein present and NOT parsimonious\n");
      writer.write("-  Protein NOT present\n");
      writer.write("g  Group protein\n");
      writer.write("\n\n");
      

      // print the proteins in each protein group
      writer.write("ProteinID\t");
      writer.write("ProteinGroupID\t");
      writer.write("Name\t");
      writer.write("CommonName\t");
      writer.write("Mol.Wt.\tpI\t");
      writer.write("NumPept\t");
      for(Dataset dataset: comparison.getDatasets()) {
          writer.write(dataset.getSourceString()+"("+dataset.getDatasetId()+")\t");
      }
      // spectrum count column headers.
      for(Dataset dataset: comparison.getDatasets()) {
          writer.write("SC("+dataset.getDatasetId()+")\t");
      }
      // NSAF column headers.
      for(Dataset dataset: comparison.getDatasets()) {
          writer.write("NSAF("+dataset.getDatasetId()+")\t");
      }
      if(form.isIncludeDescriptions())
          writer.write("Description\n");
      else
          writer.write("\n");
      
      if(!form.isCollapseProteinGroups())
          writeSplitProteinGroup(writer, comparison, form.isIncludeDescriptions());
      else
          writeCollapsedProteinGroup(writer, comparison, form.isIncludeDescriptions());
      
      writer.write("\n\n");
  }


    private void writeSplitProteinGroup(PrintWriter writer,
            ProteinGroupComparisonDataset comparison, boolean printDescription) {

        for(ComparisonProteinGroup grpProtein: comparison.getProteinsGroups()) {

            for(ComparisonProtein protein: grpProtein.getProteins()) {
                comparison.initializeProteinInfo(protein);

                writer.write(protein.getNrseqId()+"\t");
                writer.write(protein.getGroupId()+"\t");
                writer.write(protein.getFastaName()+"\t");
                writer.write(protein.getCommonName()+"\t");
                writer.write(protein.getMolecularWeight()+"\t");
                writer.write(protein.getPi()+"\t");
                writer.write(protein.getMaxPeptideCount()+"\t");

                for(Dataset dataset: comparison.getDatasets()) {
                    DatasetProteinInformation dpi = protein.getDatasetProteinInformation(dataset);
                    if(dpi == null || !dpi.isPresent()) {
                        writer.write("-");
                    }
                    else {
                        if(dpi.isParsimonious()) {
                            writer.write("*");
                        }
                        else {
                            writer.write("=");
                        }
                        if(dpi.isGrouped())
                            writer.write("g");
                    }
                    writer.write("\t");
                }
                // spectrum count information
                for(Dataset dataset: comparison.getDatasets()) {

                    DatasetProteinInformation dpi = protein.getDatasetProteinInformation(dataset);
                    if(dpi == null || !dpi.isPresent()) {
                        writer.write("0\t");
                    }
                    else {
                        writer.write(dpi.getSpectrumCount()+"("+comparison.getScaledSpectrumCount(dpi.getNormalizedSpectrumCount())+")\t");
                    }
                }
                // NSAF information
                for(Dataset dataset: comparison.getDatasets()) {

                    DatasetProteinInformation dpi = protein.getDatasetProteinInformation(dataset);
                    if(dpi == null || !dpi.isPresent()) {
                        writer.write("-1.0\t");
                    }
                    else {
                        writer.write(dpi.getNsafFormatted()+"\t");
                    }
                }
                
                if(printDescription)
                    writer.write(protein.getDescription()+"\n");
                else
                    writer.write("\n");
            }
        }
    }
    
    private void writeCollapsedProteinGroup(PrintWriter writer,
            ProteinGroupComparisonDataset comparison, boolean includeDescription) {

        for(ComparisonProteinGroup grpProtein: comparison.getProteinsGroups()) {

            String nrseqIdString = "";
            String nameString = "";
            String commonNameString = "";
            String molWtString = "";
            String piString = "";
            String nsafStrings[] = new String[comparison.getDatasetCount()];
            for(int i = 0; i < comparison.getDatasetCount(); i++)
                nsafStrings[i] = "";
            String descriptionString = "";
            
                
            for(ComparisonProtein protein: grpProtein.getProteins()) {
                comparison.initializeProteinInfo(protein);

                nrseqIdString += ","+protein.getNrseqId();
                nameString += ","+protein.getFastaName();
                commonNameString += ","+protein.getCommonName();
                molWtString += ","+protein.getMolecularWeight();
                piString += ","+protein.getPi();
                descriptionString += ","+protein.getDescription();
                
                // NSAF information
                int dsIdx = 0;
                for(Dataset dataset: comparison.getDatasets()) {

                    DatasetProteinInformation dpi = protein.getDatasetProteinInformation(dataset);
                    if(dpi == null || !dpi.isPresent()) {
                        nsafStrings[dsIdx] += ",-1.0";
                    }
                    else {
                        nsafStrings[dsIdx] += ","+dpi.getNsafFormatted();
                    }
                    dsIdx++;
                }
            }

            writer.write(nrseqIdString.substring(1)+"\t");
            writer.write(grpProtein.getGroupId()+"\t");
            writer.write(nameString.substring(1)+"\t");
            writer.write(commonNameString.substring(1)+"\t");
            writer.write(molWtString.substring(1)+"\t");
            writer.write(piString.substring(1)+"\t");
            writer.write(grpProtein.getMaxPeptideCount()+"\t");
            
            ComparisonProtein oneProtein = grpProtein.getProteins().get(0);
            // The value of isParsimonious will be the same for all proteins in a group
            for(Dataset dataset: comparison.getDatasets()) {
                DatasetProteinInformation dpi = oneProtein.getDatasetProteinInformation(dataset);
                if(dpi == null || !dpi.isPresent()) {
                    writer.write("-");
                }
                else {
                    if(dpi.isParsimonious()) {
                        writer.write("*");
                    }
                    else {
                        writer.write("=");
                    }
                    if(dpi.isGrouped())
                        writer.write("g");
                }
                writer.write("\t");
            }
            // The spectrum count information will be the same for all proteins in a group
            for(Dataset dataset: comparison.getDatasets()) {

                DatasetProteinInformation dpi = oneProtein.getDatasetProteinInformation(dataset);
                if(dpi == null || !dpi.isPresent()) {
                    writer.write("0\t");
                }
                else {
                    writer.write(dpi.getSpectrumCount()+"("+comparison.getScaledSpectrumCount(dpi.getNormalizedSpectrumCount())+")\t");
                }
            }
            
            // print the NSAF information
            for(String nsafStr: nsafStrings) {
                writer.write(nsafStr.substring(1)+"\t");
            }
            
            // print description, if required
            if(includeDescription)
                writer.write(descriptionString.substring(1)+"\n");
            else
                writer.write("\n");
        }
    }
}
