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
import java.util.Comparator;
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
import org.yeastrc.experiment.AnalysisFile;
import org.yeastrc.experiment.ExperimentSearch;
import org.yeastrc.experiment.MsFile;
import org.yeastrc.experiment.ProjectExperiment;
import org.yeastrc.experiment.ProjectExperimentDAO;
import org.yeastrc.experiment.SearchAnalysis;
import org.yeastrc.experiment.SearchFile;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.MsSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.domain.analysis.MsRunSearchAnalysis;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.general.MsExperiment;
import org.yeastrc.ms.domain.run.MsRun;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;
import org.yeastrc.project.Researcher;
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
            ProjectExperiment pExpt = new ProjectExperiment(expt);
            
            // load the ms2 file names and the number of spectra in each file
            List<Integer> runIds = daoFactory.getMsExperimentDAO().getRunIdsForExperiment(experimentId);
            List<MsFile> files = new ArrayList<MsFile>(runIds.size());
            MsRunDAO runDao = daoFactory.getMsRunDAO();
            MsScanDAO scanDao = daoFactory.getMsScanDAO();
            for(Integer runId: runIds) {
                MsRun run = runDao.loadRun(runId);
                int numScans = scanDao.numScans(runId);
                MsFile file = new MsFile(run, numScans);
                files.add(file);
            }
            pExpt.setMs2Files(files);
            
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
            
            
            
            experiments.add(pExpt);
        }
        return experiments;
    }
    
    
    private ExperimentSearch getExperimentSearch(int searchId) {
        
        MsSearch search = daoFactory.getMsSearchDAO().loadSearch(searchId);
        ExperimentSearch eSearch = new ExperimentSearch(search);
        
        MsRunSearchDAO rsDao = daoFactory.getMsRunSearchDAO();
        
        List<Integer> runSearchIds = rsDao.loadRunSearchIdsForSearch(searchId);
        List<SearchFile> files = new ArrayList<SearchFile>(runSearchIds.size());
        
        for(int runSearchId: runSearchIds) {
            MsRunSearch rs = rsDao.loadRunSearch(runSearchId);
            String filename = rsDao.loadFilenameForRunSearch(runSearchId);
            SearchFile file = new SearchFile(rs, filename);
            file.setNumResults(rsDao.numResults(runSearchId));
            files.add(file);
        }
        eSearch.setFiles(files);
        return eSearch;
    }
    
    private SearchAnalysis getSearchAnalysis(int searchAnalysisId) {
        
        MsSearchAnalysis analysis = daoFactory.getMsSearchAnalysisDAO().load(searchAnalysisId);
        SearchAnalysis sAnalysis = new SearchAnalysis(analysis);
        
        MsRunSearchAnalysisDAO rsaDao = daoFactory.getMsRunSearchAnalysisDAO();
        
        List<Integer> rsAnalysisIds = rsaDao.getRunSearchAnalysisIdsForAnalysis(searchAnalysisId);
        List<AnalysisFile> files = new ArrayList<AnalysisFile>(rsAnalysisIds.size());
        for(int id: rsAnalysisIds) {
            MsRunSearchAnalysis rsa = rsaDao.load(id);
            String filename = rsaDao.loadFilenameForRunSearchAnalysis(id);
            AnalysisFile file = new AnalysisFile(rsa, filename);
            files.add(file);
        }
        
        // If this is Percolator analysis we know how to get the number of 
        // results for each file.
        if(analysis.getAnalysisProgram() == Program.PERCOLATOR) {
            PercolatorResultDAO prDao = daoFactory.getPercolatorResultDAO();
            for(AnalysisFile file: files) {
                file.setNumResults(prDao.numRunAnalysisResults(file.getId()));
            }
        }
        
        sAnalysis.setFiles(files);
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
        
        
        // put the DTASelect results in the appropriate experiments
        ExptComparator comparator = new ExptComparator();
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
    
    private static class ExptComparator implements Comparator<ProjectExperiment> {

        @Override
        public int compare(ProjectExperiment o1, ProjectExperiment o2) {
            return Integer.valueOf(o1.getId()).compareTo(o2.getId());
        }
    }
}