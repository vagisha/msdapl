/**
 * ViewPercolatorResults.java
 * @author Vagisha Sharma
 * Apr 5, 2009
 * @version 1.0
 */
package org.yeastrc.www.project.experiment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.experiment.TabularPercolatorResults;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class ViewPercolatorResults extends Action {

    
    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {
        
        int runSearchAnalysisId = 0;

        // User making this request
        User user = UserUtils.getUser(request);
        if (user == null) {
            ActionErrors errors = new ActionErrors();
            errors.add("username", new ActionMessage("error.login.notloggedin"));
            saveErrors( request, errors );
            return mapping.findForward("authenticate");
        }


        try {
            String strID = request.getParameter("ID");

            if (strID == null || strID.equals("")) {
                ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.invalid.id", "Percolator Output"));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
            }

            runSearchAnalysisId = Integer.parseInt(strID);

        } catch (NumberFormatException nfe) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.invalid.id", "Percolator Output"));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }

        
        // TODO Does the user have access to look at these results? 
        PercolatorResultDAO presDao = DAOFactory.instance().getPercolatorResultDAO();
        List<Integer> resultIds = presDao.loadResultIdsForRunSearchAnalysis(runSearchAnalysisId);
        
        List<PercolatorResult> results = new ArrayList<PercolatorResult>(50);
        Collections.sort(resultIds);
        for(int i = 0; i < 50; i++) {
            int resultId = resultIds.get(i);
            PercolatorResult result = presDao.load(resultId);
            results.add(result);
        }

        TabularPercolatorResults tabResults = new TabularPercolatorResults(results);
        tabResults.setCurrentPage(1);
        tabResults.setLastPage(1);


        request.setAttribute("results", tabResults);


//      Forward them on to the happy success view page!
        return mapping.findForward("Success");


    }
}
