/*
 *
 * Created on February 5, 2004
 *
 * Created by Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.www.project;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.experiment.ExperimentSearch;
import org.yeastrc.experiment.ProjectExperiment;
import org.yeastrc.experiment.ProjectExperimentDAO;
import org.yeastrc.experiment.SearchAnalysis;
import org.yeastrc.jobqueue.JobUtils;
import org.yeastrc.jobqueue.MSJob;
import org.yeastrc.jobqueue.MSJobFactory;
import org.yeastrc.jobqueue.MsJobSearcher;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsSearchAnalysisDAO;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.general.MsExperiment;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;
import org.yeastrc.project.Researcher;
import org.yeastrc.www.proteinfer.ProteinferJob;
import org.yeastrc.www.proteinfer.ProteinInferJobSearcher;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;
import org.yeastrc.yates.YatesRun;
import org.yeastrc.yates.YatesRunMsSearchLinker;
import org.yeastrc.yates.YatesRunSearcher;

/**
 * Implements the logic to register a user
 */
public class ViewProjectAction extends Action {

    
    private static DAOFactory daoFactory = DAOFactory.instance();
    
	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		// Get the projectID they're after
		int projectID;
		
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
				errors.add("username", new ActionMessage("error.project.noprojectid"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}

			projectID = Integer.parseInt(strID);

		} catch (NumberFormatException nfe) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.project.invalidprojectid"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}

		// Load our project
		Project project;
		
		try {
			project = ProjectFactory.getProject(projectID);
			if (!project.checkReadAccess(user.getResearcher())) {
				
				// This user doesn't have access to this project.
				ActionErrors errors = new ActionErrors();
				errors.add("username", new ActionMessage("error.project.noaccess"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}
		} catch (Exception e) {
			
			// Couldn't load the project.
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.project.projectnotfound"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");	
		}

		// Set this project in the request, as a bean to be displayed on the view
		request.setAttribute("project", project);
		
		
		
		// Check for experiment data for this project
		List<ProjectExperiment> experiments = getProjectExperiments(projectID);
		
		
		// load the dtaselect results;
		// Check for yates MS data
        YatesRunSearcher yrs = new YatesRunSearcher();
        yrs.setProjectID(project.getID());
        yrs.setMostRecent( true );
        List<YatesRun> yatesRuns = yrs.search();
        
        
        // Associate the DTASelect runs with the respective experiments
        linkExperimentsAndDtaSelect(experiments, yatesRuns);
		
        
        request.setAttribute("experiments", experiments);
		
		
		// TODO Check uploads for a project

		
		// Should the user be able to upload data to this project.
		Groups groupMan = Groups.getInstance();
		boolean showUpload = false;
		if(groupMan.isMember( user.getResearcher().getID(), "administrators" ) ) {
		    showUpload = true;
		}
		else {
		    int userId = user.getResearcher().getID();
		    for(Researcher researcher: project.getResearchers()) {
		        
		        if(researcher.getID() == userId) {
		            showUpload = true; 
		            break;
		        }
		    }
		}

		if(showUpload)
		    request.setAttribute( "showMacCossUpload", true );


		// Forward them on to the happy success view page!
		return mapping.findForward("Success");


	}


    private List<ProjectExperiment> getProjectExperiments(int projectId) throws Exception {
        
        List<Integer> experimentIds = ProjectExperimentDAO.instance().getProjectExperimentIds(projectId);
        Collections.sort(experimentIds);
        
        if(experimentIds.size() == 0)
            return new ArrayList<ProjectExperiment>(0);
        
        
        List<ProjectExperiment> experiments = new ArrayList<ProjectExperiment>(experimentIds.size());
        
        for(int experimentId: experimentIds) {
            MsExperiment expt = daoFactory.getMsExperimentDAO().loadExperiment(experimentId);
            
            // First check if this experiment is still getting uploaded
            // Add to list only if the upload is failed or complete.
            MSJob job = null;
            int status = 0;
            try {
                job = MSJobFactory.getInstance().getJobForExperiment(experimentId);
                status = job.getStatus();
                if(status == JobUtils.STATUS_QUEUED || status == JobUtils.STATUS_OUT_FOR_WORK)
                    continue;
            }
            catch(Exception e) {continue;} // because job with experimentID does not exist. Should not really happen.
            
            ProjectExperiment pExpt = new ProjectExperiment(expt);
            pExpt.setUploadJobId(job.getId());
            if(status != JobUtils.STATUS_COMPLETE)
                pExpt.setUploadSuccess(false);
            
            // load the searches
            List<Integer> searchIds = daoFactory.getMsSearchDAO().getSearchIdsForExperiment(experimentId);
            List<ExperimentSearch> searches = new ArrayList<ExperimentSearch>(searchIds.size());
            for(int searchId: searchIds) {
                searches.add(getExperimentSearch(searchId));
            }
            pExpt.setSearches(searches);
            
            // load the analyses
            Set<Integer> analysisIds = new HashSet<Integer>();
            MsSearchAnalysisDAO saDao = daoFactory.getMsSearchAnalysisDAO();
            for(int searchId: searchIds) {
                List<Integer> aIds = saDao.getAnalysisIdsForSearch(searchId);
                analysisIds.addAll(aIds);
            }
            List<SearchAnalysis> analyses = new ArrayList<SearchAnalysis>(analysisIds.size());
            for(int analysisId: analysisIds) {
                analyses.add(getSearchAnalysis(analysisId));
            }
            pExpt.setAnalyses(analyses);
            
            
            // load the protein inference jobs, if any
            List<ProteinferJob> piJobs = ProteinInferJobSearcher.instance().getProteinferJobsForMsExperiment(experimentId);
            pExpt.setProtInferRuns(piJobs);
            
            experiments.add(pExpt);
        }
        return experiments;
    }
    
    
    private ExperimentSearch getExperimentSearch(int searchId) {
        
        MsSearch search = daoFactory.getMsSearchDAO().loadSearch(searchId);
        ExperimentSearch eSearch = new ExperimentSearch(search);
        return eSearch;
    }
    
    private SearchAnalysis getSearchAnalysis(int searchAnalysisId) {
        
        MsSearchAnalysis analysis = daoFactory.getMsSearchAnalysisDAO().load(searchAnalysisId);
        SearchAnalysis sAnalysis = new SearchAnalysis(analysis);
        return sAnalysis;
    }
    
    private void linkExperimentsAndDtaSelect(List<ProjectExperiment> experiments, List<YatesRun> yatesRuns) throws SQLException {
        
        // We will get the searchIds from the tblYatesRun* tables
        // Create a map of searchId and experimentId
        Map<Integer, Integer> searchIdToExperimentId = new HashMap<Integer, Integer>();
        for(ProjectExperiment experiment: experiments) {
            for(ExperimentSearch search: experiment.getSearches())
                searchIdToExperimentId.put(search.getId(), experiment.getId());
        }
        
        List<Integer> experimentIds = new ArrayList<Integer>(experiments.size());
        for(ProjectExperiment pe: experiments)
            experimentIds.add(pe.getId());
        
        
        // put the DTASelect results in the appropriate experiments
        for(YatesRun run: yatesRuns) {
            int runId = run.getId();
            int searchId = YatesRunMsSearchLinker.linkYatesRunToMsSearch(runId);

            if(searchId > 0) {
                Integer experimentId = searchIdToExperimentId.get(searchId);
                if(experimentId != null) {
                    
                    int idx = Collections.binarySearch(experimentIds, experimentId);
                    if(idx != -1) {
                        ProjectExperiment pe = experiments.get(idx);
                        pe.setDtaSelect(run);
                    }
                }
            }
        }
    }
}