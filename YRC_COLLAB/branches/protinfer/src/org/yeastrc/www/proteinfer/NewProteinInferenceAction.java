/**
 * NewProteinInferenceAction.java
 * @author Vagisha Sharma
 * Nov 3, 2008
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import edu.uwpr.protinfer.idpicker.IDPickerParams;
import edu.uwpr.protinfer.idpicker.SearchSummary;
import edu.uwpr.protinfer.idpicker.SearchSummary.RunSearch;

/**
 * 
 */
public class NewProteinInferenceAction extends Action {

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

        int searchId = -1;
        if (request.getParameter("searchId") != null) {
            try {searchId = Integer.parseInt(request.getParameter("searchId"));}
            catch(NumberFormatException e) {searchId = -1;}
        }
        
        if (searchId == -1) {
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.proteinfer.invalid.searchId", searchId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        // Create our ActionForm
        ProteinInferenceForm newForm = new ProteinInferenceForm();
        request.setAttribute("proteinInferenceForm", newForm);
        
        newForm.setIdPickerParams(new IDPickerParams());
        newForm.setSearchSummary(getSearchSummary(searchId));

        // Go!
        return mapping.findForward("Success");

    }
    
    private SearchSummary getSearchSummary(int searchId) {
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
