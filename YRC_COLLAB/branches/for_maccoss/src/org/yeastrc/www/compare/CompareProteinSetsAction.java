/**
 * CompareProtInferResultsAction.java
 * @author Vagisha Sharma
 * Apr 9, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
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
        
        
        // Get the selected protein inference run ids
        List<Integer> piRunIds = myForm.getSelectedProteinferRunIds();
        
        List<Integer> dtaRunIds = myForm.getSelectedDtaRunIds();

        int total = piRunIds.size() + dtaRunIds.size();
        
        // get the protein inference ids to compare
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
            Dataset dataset = new Dataset(piRunId, DatasetSource.PROT_INFER);
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
            Dataset dataset = new Dataset(dtaRunId, DatasetSource.DTA_SELECT);
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
        
        // Do the comparison
        ComparisonDataset comparison = ProteinDatasetComparer.instance().compareDatasets(datasets, false);
        ProteinDatasetComparer.instance().applyFilters(comparison, filters);
        comparison.setCurrentPage(myForm.getPageNum());
        request.setAttribute("comparison", comparison);
        request.setAttribute("proteinSetComparisonForm", myForm);
        
        
        
        // Create Venn Diagram only if 2 or 3 datasets are being compared
        if(comparison.getDatasetCount() == 2 || comparison.getDatasetCount() == 3) {
            String googleChartUrl = VennDiagramCreator.instance().getChartUrl(comparison);
            request.setAttribute("chart", googleChartUrl);
        }
        
        return mapping.findForward("Success");
    }
    
}
