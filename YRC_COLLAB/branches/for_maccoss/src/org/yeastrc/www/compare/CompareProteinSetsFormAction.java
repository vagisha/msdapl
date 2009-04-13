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
import org.yeastrc.jobqueue.JobUtils;
import org.yeastrc.project.Project;
import org.yeastrc.www.proteinfer.ProteinferJob;
import org.yeastrc.www.proteinfer.ProteinferRunSearcher;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;
import org.yeastrc.yates.YatesRun;
import org.yeastrc.yates.YatesRunSearcher;

import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferRunDAO;

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
        
        // If a DTASelect run id was sent with the request get it now
        int dtaRunId = 0;
        try {
            String idStr = request.getParameter("dtaRunId");
            if(idStr != null) {
                dtaRunId = Integer.parseInt(idStr);
            }
        }
        catch(Exception e) {
            dtaRunId = 0;
        }
        
        
        // Get a list of the user's projects
        List<Project> projects = user.getProjects();
        
        // For each experiment in the project get a list of the protein inference id
        // and DTASelect IDs
        
        Set<Integer> piRunIds = new HashSet<Integer>();
        
        List<ProteinferRunFormBean> piRuns = new ArrayList<ProteinferRunFormBean>();
        List<DTASelectRunFormBean> dtaRuns = new ArrayList<DTASelectRunFormBean>();
        
        ProjectExperimentDAO prExpDao = ProjectExperimentDAO.instance();
        ProteinferRunDAO runDao = ProteinferDAOFactory.instance().getProteinferRunDao();
        
        for(Project project: projects) {
            
            
            // Get the protein inference runs for this project
            List<Integer> experimentIds = prExpDao.getExperimentIdsForProject(project.getID());
            
            for(int experimentId: experimentIds) {
                
                List<ProteinferJob> piJobs = ProteinferRunSearcher.getProteinferJobsForMsExperiment(experimentId);
                
                for(ProteinferJob job: piJobs) {
                    if(job.getStatus() != JobUtils.STATUS_COMPLETE)
                        continue;
                    if(!piRunIds.contains(job.getPinferId())) {
                        piRunIds.add(job.getPinferId());
                        
                        ProteinferRunFormBean bean = new ProteinferRunFormBean(job, project.getID());
                        if(job.getPinferId() == pinferRunId)
                            bean.setSelected(true);
                        piRuns.add(bean);
                    }
                }
            }
            
            // Get the DTASelect runs for this project
            YatesRunSearcher searcher = new YatesRunSearcher();
            searcher.setProjectID(project.getID());
            List<YatesRun> yatesRuns = searcher.search();
            for(YatesRun run: yatesRuns) {
                DTASelectRunFormBean bean = new DTASelectRunFormBean(run);
                if(run.getId() == dtaRunId)
                    bean.setSelected(true);
                dtaRuns.add(bean);
            }
        }
        
        ProteinSetComparisonForm myForm = new ProteinSetComparisonForm();
        myForm.setProteinferRunList(piRuns);
        myForm.setDtaRunList(dtaRuns);
        request.setAttribute("proteinSetComparisonForm", myForm);
        
        
        return mapping.findForward("Success");

    }
}
