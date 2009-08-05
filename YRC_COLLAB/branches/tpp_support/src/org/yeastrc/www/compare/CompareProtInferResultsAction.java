/**
 * CompareProtInferResultsAction.java
 * @author Vagisha Sharma
 * Jun 14, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

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
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class CompareProtInferResultsAction extends Action {

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
        // If a protein inference run id was sent with the request get it now
        List<ProteinferRunFormBean> inputPiRuns = new ArrayList<ProteinferRunFormBean>();
        try {
            String idStr = request.getParameter("piRunIds");
            if(idStr != null) {
                String[] tokens = idStr.split(",");
                for(String tok: tokens) {
                    int piRunId = Integer.parseInt(tok.trim());
                    ProteinferRunFormBean bean = new ProteinferRunFormBean();
                    bean.setRunId(piRunId);
                    bean.setSelected(true);
                    inputPiRuns.add(bean);
                }
            }
        }
        catch(Exception e) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Invalid protein inference IDs"));
            saveErrors(request, errors);
            return mapping.findForward("Failure");
        }
        
        boolean groupProteins = Boolean.parseBoolean(request.getParameter("groupProteins"));
        ((ProteinSetComparisonForm)form).setPiRuns(inputPiRuns);
        ((ProteinSetComparisonForm)form).setGroupProteins(groupProteins);
        
        return mapping.findForward("Success");
    }
}
