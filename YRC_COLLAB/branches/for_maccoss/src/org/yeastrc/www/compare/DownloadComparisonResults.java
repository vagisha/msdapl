/**
 * DownloadComparisonResults.java
 * @author Vagisha Sharma
 * May 4, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
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
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;
import org.yeastrc.yates.YatesRun;

import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferRunDAO;
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
        
        System.out.println("IN DownloadComparisonResults");
        
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
        
        // ANY AND, OR, NOT filters
        if((myForm.getAndList().size() == 0) && 
           (myForm.getOrList().size() == 0) && 
           (myForm.getNotList().size() == 0)) {
            List<SelectableDataset> sdsList = new ArrayList<SelectableDataset>(datasets.size());
            for(Dataset dataset: datasets) {
                SelectableDataset sds = new SelectableDataset(dataset);
                sds.setSelected(false);
                sdsList.add(sds);
            }
            
            myForm.setAndList(sdsList);
            myForm.setOrList(sdsList);
            myForm.setNotList(sdsList);
        }
        List<SelectableDataset> andDataset = myForm.getAndList();
        List<SelectableDataset> orDataset = myForm.getOrList();
        List<SelectableDataset> notDataset = myForm.getNotList();
        
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
        
        ProteinDatasetComparisonFilters filters = new ProteinDatasetComparisonFilters();
        filters.setAndFilters(andFilters);
        filters.setOrFilters(orFilters);
        filters.setNotFilters(notFilters);
        
        
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition","attachment; filename=\"ProteinSetComparison.txt\"");
        response.setHeader("cache-control", "no-cache");
        PrintWriter writer = response.getWriter();
        writer.write("\n\n");
        writer.write("Date: "+new Date()+"\n\n");
        
        
        // Do the comparison
        long s = System.currentTimeMillis();
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
        
        // Sort by peptide count
        s = System.currentTimeMillis();
        ProteinDatasetSorter sorter = ProteinDatasetSorter.instance();
        sorter.sortByPeptideCount(comparison);
        e = System.currentTimeMillis();
        log.info("Time to sort results: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        comparison.initSummary(); // initialize the summary (totalProteinCount, # common proteins)
        
        s = System.currentTimeMillis();
        writeResults(writer, comparison, myForm);
        writer.close();
        e = System.currentTimeMillis();
        log.info("DownloadComparisonResults results in: "+TimeUtils.timeElapsedMinutes(s,e)+" minutes");
        return null;
    }

    
    private void writeResults(PrintWriter writer, ProteinComparisonDataset comparison, ProteinSetComparisonForm form) {
        
        writer.write("Total protein count: "+comparison.getTotalProteinCount()+"\n");
        writer.write("Filtered protein count: "+comparison.getFilteredProteinCount()+"\n");
        writer.write("\n\n");
        
        // Datasets
        writer.write("Datasets: \n");
        int idx = 0;
        for(Dataset dataset: comparison.getDatasets()) {
            writer.write(dataset.getSourceString()+" ID "+dataset.getDatasetId()+": "+comparison.getProteinCount(idx++)+"\n");
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
        writer.write("NumPept\t");
        for(Dataset dataset: comparison.getDatasets()) {
            writer.write(dataset.getSourceString()+"_"+dataset.getDatasetId()+"\t");
        }
        writer.write("Description\n");
        
        for(ComparisonProtein protein: comparison.getProteins()) {
            
            comparison.initializeProteinInfo(protein);
            
            writer.write(protein.getNrseqId()+"\t");
            writer.write(protein.getSystematicName()+"\t");
            writer.write(protein.getName()+"\t");
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
            
            writer.write(protein.getDescription()+"\n");
        }
        
        writer.write("\n\n");
    }
}
