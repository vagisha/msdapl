/**
 * DatasetFiltersFormAction.java
 * @author Vagisha Sharma
 * Sep 7, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.experiment.ProjectExperimentDAO;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetRocDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.ms.domain.protinfer.ProteinferRun;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetROC;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class DatasetFiltersFormAction extends Action {

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
        // first, look for ids in the request parameters
        List<Integer> inputPiRunIds = null;
        boolean groupProteins;
        String idStr = request.getParameter("piRunIds");
        if(idStr != null && idStr.trim().length() > 0) {
            inputPiRunIds = new ArrayList<Integer>();
            String[] tokens = idStr.split(",");
            for(String tok: tokens) {
                int piRunId = Integer.parseInt(tok.trim());
                inputPiRunIds.add(piRunId);
            }
            groupProteins = Boolean.parseBoolean(request.getParameter("groupProteins"));
        }
        else {
            inputPiRunIds = ((DatasetSelectionForm)form).getSelectedProteinferRunIds();
            groupProteins = ((DatasetSelectionForm)form).getGroupProteins();
        }
        
        if(inputPiRunIds == null || inputPiRunIds.size() == 0) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "No protein inference IDs found to compare."));
            saveErrors(request, errors);
            return mapping.findForward("Failure");
        }
        
        ProteinSetComparisonForm myForm = new ProteinSetComparisonForm();
        myForm.setGroupIndistinguishableProteins(groupProteins); // are indistinguishable proteins being grouped
        
        ProteinferRunDAO runDao = ProteinferDAOFactory.instance().getProteinferRunDao();
        ProteinProphetRocDAO ppRocDao = ProteinferDAOFactory.instance().getProteinProphetRocDao();
        
        List<ProteinferRunFormBean> pinferDatasets = new ArrayList<ProteinferRunFormBean>();
        List<ProteinProphetRunFormBean> proteinProphetDatasets = new ArrayList<ProteinProphetRunFormBean>();
        
        for(Integer inputId: inputPiRunIds) {
            ProteinferRun run = runDao.loadProteinferRun(inputId);
            List<Integer> projectIds = getProjectIdsForRun(run.getId());
            if(ProteinInferenceProgram.isIdPicker(run.getProgram())) {
                ProteinferRunFormBean bean = new ProteinferRunFormBean(run, projectIds);
                bean.setSelected(true);
                pinferDatasets.add(bean);
            }
            else if(run.getProgram() == ProteinInferenceProgram.PROTEIN_PROPHET) {
                ProteinProphetRunFormBean bean = new ProteinProphetRunFormBean(run, projectIds);
                bean.setSelected(true);
                ProteinProphetROC roc = ppRocDao.loadRoc(run.getId());
                bean.setRoc(roc);
                double error = roc.getClosestError(0.01);
                bean.setErrorRate(error);
                bean.setProbability(roc.getMinProbabilityForError(error));
                proteinProphetDatasets.add(bean);
            }
        }
        myForm.setProteinferRunList(pinferDatasets);
        myForm.setProteinProphetRunList(proteinProphetDatasets);
        
        
        // Protein inference datasets (for AND, OR, NOT and XOR FILTERS)
        List<SelectableDataset> sdsList = new ArrayList<SelectableDataset>(inputPiRunIds.size());
        int datasetIndex = 0;
        for(Integer inputId: inputPiRunIds) {
            ProteinferRun run = runDao.loadProteinferRun(inputId);
            if(run == null) {
                ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", 
                        "No protein inference run found with ID: "+inputId+"."));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
            }
            Dataset dataset = DatasetBuilder.instance().buildDataset(inputId,
                                DatasetSource.getSourceForProtinferProgram(run.getProgram()));
            SelectableDataset sds = new SelectableDataset(dataset);
            sds.setSelected(false);
            sds.setDatasetIndex(datasetIndex++);
            sdsList.add(sds);
        }
        
        myForm.setAndList(sdsList);
        myForm.setOrList(sdsList);
        myForm.setNotList(sdsList);
        myForm.setXorList(sdsList);
        
        request.setAttribute("datasetFiltersForm", myForm);
        
        
        return mapping.findForward("Success");
    }

    private List<Integer> getProjectIdsForRun(int pinferId) throws SQLException {
        // Get a list of projects for this protein inference run.  
        List<Integer> searchIds = ProteinferDAOFactory.instance().getProteinferRunDao().loadSearchIdsForProteinferRun(pinferId);
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        ProjectExperimentDAO projExptDao = ProjectExperimentDAO.instance();
        List<Integer> projectIds = new ArrayList<Integer>();
        for(int searchId: searchIds) {
            MsSearch search = searchDao.loadSearch(searchId);
            int experimentId = search.getExperimentId();
            int projectId = projExptDao.getProjectIdForExperiment(experimentId);
            if(projectId > 0)
                projectIds.add(projectId);
        }
        return projectIds;
    }
}
