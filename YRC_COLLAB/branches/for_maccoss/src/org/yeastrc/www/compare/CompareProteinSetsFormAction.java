/**
 * CompareProInferResultsFormAction.java
 * @author Vagisha Sharma
 * Apr 10, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.experiment.ProjectExperimentDAO;
import org.yeastrc.project.Project;
import org.yeastrc.www.proteinfer.ProteinferRunSearcher;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferRunDAO;
import edu.uwpr.protinfer.database.dto.ProteinferRun;

/**
 * 
 */
public class CompareProteinSetsFormAction extends org.apache.struts.action.Action {

    private static final Logger log = Logger.getLogger(CompareProteinSetsFormAction.class);
    
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

        
        // If a protein inference run id was sent with the request get it now
        int pinferRunId = 0;
        try {
            String idStr = request.getParameter("piRunId");
            if(idStr != null) {
                pinferRunId = Integer.parseInt(idStr);
            }
        }
        catch(Exception e) {
            pinferRunId = 0;
        }
        
        
        // Get a list of the user's projects
        List<Project> projects = user.getProjects();
        
        // For each experiment in the project get a list of the protein inference id
        
        Set<Integer> piRunIds = new HashSet<Integer>();
        List<ProteinferRunFormBean> piRuns = new ArrayList<ProteinferRunFormBean>();
        
        ProjectExperimentDAO prExpDao = ProjectExperimentDAO.instance();
        ProteinferRunDAO runDao = ProteinferDAOFactory.instance().getProteinferRunDao();
        
        for(Project project: projects) {
            List<Integer> experimentIds = prExpDao.getExperimentIdsForProject(project.getID());
            
            for(int experimentId: experimentIds) {
                List<Integer> runIds = ProteinferRunSearcher.getProteinferIdsForMsExperiment(experimentId);
                
                for(int runId: runIds) {
                    if(!piRunIds.contains(runId)) {
                        piRunIds.add(runId);
                        
                        ProteinferRun run = runDao.loadProteinferRun(runId);
                        ProteinferRunFormBean bean = new ProteinferRunFormBean(run, project.getID());
                        if(runId == pinferRunId)
                            bean.setSelected(true);
                        piRuns.add(bean);
                    }
                }
            }
        }
        
        ProteinSetComparisonForm myForm = new ProteinSetComparisonForm();
        myForm.setProteinferRunList(piRuns);
        request.setAttribute("proteinSetComparisonForm", myForm);
        
        
        return mapping.findForward("Success");

    }
}
