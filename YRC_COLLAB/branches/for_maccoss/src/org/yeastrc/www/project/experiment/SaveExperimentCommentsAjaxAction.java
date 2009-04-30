/**
 * SaveExperimentCommentsAjaxAction.java
 * @author Vagisha Sharma
 * Apr 30, 2009
 * @version 1.0
 */
package org.yeastrc.www.project.experiment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.general.MsExperimentDAO;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class SaveExperimentCommentsAjaxAction extends Action {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        
        // User making this request
        User user = UserUtils.getUser(request);
        if (user == null) {
            response.getWriter().write("You are not logged in!");
            response.setStatus(HttpServletResponse.SC_SEE_OTHER); // Status code (303) indicating that the response to the request can be found under a different URI.
            return null;
        }
        
        int experimentId = 0;
        try {experimentId = Integer.parseInt(request.getParameter("experimentId"));}
        catch(NumberFormatException e) {}

        if(experimentId == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid Experiment ID: "+experimentId+"</b>");
            return null;
        }

        int projectId = 0;
        try {projectId = Integer.parseInt(request.getParameter("projectId"));}
        catch(NumberFormatException e) {}

        if(projectId == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid project ID: "+projectId+"</b>");
            return null;
        }
        
        String comments = request.getParameter("comments");
        if(comments == null)
            comments = "";
        
        // Save
        try {
            MsExperimentDAO exptDao = DAOFactory.instance().getMsExperimentDAO();
            exptDao.updateComments(experimentId, comments);
        }
        catch(Exception e) {
            response.setContentType("text/html");
            response.getWriter().write("FAIL "+e.getMessage());
            return null;
        }
        
        // Everything went well.
        response.setContentType("text/html");
        response.getWriter().write("OK");
        return null;
       
    }

}
