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
import org.yeastrc.experiment.ExperimentProteinProphetRun;
import org.yeastrc.experiment.ExperimentProteinferRun;
import org.yeastrc.experiment.ExperimentSearch;
import org.yeastrc.experiment.ProjectExperiment;
import org.yeastrc.experiment.ProjectExperimentDAO;
import org.yeastrc.experiment.SearchAnalysis;
import org.yeastrc.jobqueue.JobUtils;
import org.yeastrc.jobqueue.MSJob;
import org.yeastrc.jobqueue.MSJobFactory;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.analysis.MsSearchAnalysisDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferPeptideDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerProteinDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetProteinDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetRunDAO;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetAnalysis;
import org.yeastrc.ms.domain.general.MsExperiment;
import org.yeastrc.ms.domain.general.MsInstrument;
import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.ms.domain.protinfer.ProteinferRun;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetRun;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;
import org.yeastrc.project.Researcher;
import org.yeastrc.www.proteinfer.ProteinInferJobSearcher;
import org.yeastrc.www.proteinfer.ProteinferJob;
import org.yeastrc.www.proteinfer.idpicker.IdPickerResultsLoader;
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
			    strID = request.getParameter("projectId");
			    
			    if(strID == null || strID.equals("")) {
			        ActionErrors errors = new ActionErrors();
			        errors.add("username", new ActionMessage("error.project.noprojectid"));
			        saveErrors( request, errors );
			        return mapping.findForward("Failure");
			    }
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
		
		boolean writeAccess = false;
		if(project.checkAccess(user.getResearcher()))
		    writeAccess = true;
		request.setAttribute("writeAccess", writeAccess);
		
		// Check for experiment data for this project
		List<ProjectExperiment> experiments = getProjectExperiments(projectID, 5); // get the last 5 uploaded
		
		
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
		    request.setAttribute( "showMSDataUpload", true );


		// Set a list of available instruments in the request. 
		List<MsInstrument> instrumentList = DAOFactory.instance().getInstrumentDAO().loadAllInstruments();
		request.setAttribute("instrumentList", instrumentList);
		
		// Forward them on to the happy success view page!
		return mapping.findForward("Success");


	}


    private List<ProjectExperiment> getProjectExperiments(int projectId, int limitCount) throws Exception {
        
        List<Integer> experimentIds = ProjectExperimentDAO.instance().getExperimentIdsForProject(projectId);
        Collections.sort(experimentIds, Collections.reverseOrder());
        
        if(experimentIds.size() == 0)
            return new ArrayList<ProjectExperiment>(0);
        
        
        List<ProjectExperiment> experiments = new ArrayList<ProjectExperiment>(experimentIds.size());
        
        int count = 0;
        for(int experimentId: experimentIds) {
        	
            MsExperiment expt = daoFactory.getMsExperimentDAO().loadExperiment(experimentId);
            
            // First check if this experiment is still getting uploaded
            // Add to list only if the upload is failed or complete.
            MSJob job = null;
            int status = 0;
            try {
                job = MSJobFactory.getInstance().getJobForProjectExperiment(projectId, experimentId);
                status = job.getStatus();
                if(status == JobUtils.STATUS_QUEUED || status == JobUtils.STATUS_OUT_FOR_WORK
                   || status == JobUtils.STATUS_PENDING_UPLOAD)
                    continue;
            }
            catch(Exception e) {continue;} // because job with experimentID does not exist. Should not really happen.
            
            ProjectExperiment pExpt = new ProjectExperiment(expt);
            pExpt.setUploadJobId(job.getId());
            if(status != JobUtils.STATUS_COMPLETE)
                pExpt.setUploadSuccess(false);
            
            experiments.add(pExpt);
            
            count++;
            if(count > limitCount) {
            	pExpt.setHasFullInformation(false);
            	continue; // don't get the details if we have hit the limit
            }
            else
            	pExpt.setHasFullInformation(true);
            
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
            
            
            // load protein prophet results, if any
            List<Integer> piRunIds = ProteinInferJobSearcher.instance().getProteinferIdsForMsExperiment(experimentId);
            // loop over and see if any are ProteinProphet runs
            ProteinferRunDAO runDao = ProteinferDAOFactory.instance().getProteinferRunDao();
            ProteinProphetRunDAO pRunDao = ProteinferDAOFactory.instance().getProteinProphetRunDao();
            ProteinProphetProteinDAO prophetProtDao = ProteinferDAOFactory.instance().getProteinProphetProteinDao();
            ProteinferPeptideDAO peptDao = ProteinferDAOFactory.instance().getProteinferPeptideDao();
            
            List<ExperimentProteinProphetRun> prophetRunList = new ArrayList<ExperimentProteinProphetRun>();
            for(int piRunId: piRunIds) {
                ProteinferRun run = runDao.loadProteinferRun(piRunId);
                if(run.getProgram() == ProteinInferenceProgram.PROTEIN_PROPHET) {
                    ProteinProphetRun ppRun = pRunDao.loadProteinferRun(piRunId);
                    ExperimentProteinProphetRun eppRun = new ExperimentProteinProphetRun(ppRun);
                    eppRun.setNumParsimoniousProteins(prophetProtDao.getProteinferProteinIds(piRunId, true).size());
                    eppRun.setNumParsimoniousProteinGroups(prophetProtDao.getIndistinguishableGroupCount(piRunId, true));
                    eppRun.setUniqPeptideSequenceCount(peptDao.getUniquePeptideSequenceCountForRun(piRunId));
                    
                    prophetRunList.add(eppRun);
                }
            }
            pExpt.setProteinProphetRun(prophetRunList);
            
            
            // load the protein inference jobs, if any
            List<ProteinferJob> piJobs = ProteinInferJobSearcher.instance().getProteinferJobsForMsExperiment(experimentId);
            List<ExperimentProteinferRun> piRuns = new ArrayList<ExperimentProteinferRun>(piJobs.size());
            IdPickerProteinDAO protDao = ProteinferDAOFactory.instance().getIdPickerProteinDao();
            for(ProteinferJob piJob: piJobs) {
                ExperimentProteinferRun piRun = new ExperimentProteinferRun(piJob);
                piRun.setNumParsimoniousProteins(protDao.getIdPickerProteinIds(piJob.getPinferId(), true).size());
                piRun.setNumParsimoniousProteinGroups(protDao.getIdPickerGroupCount(piJob.getPinferId(), true));
                piRun.setUniqPeptideSequenceCount(IdPickerResultsLoader.getUniquePeptideCount(piJob.getPinferId()));
                
                piRuns.add(piRun);
            }
            pExpt.setProtInferRuns(piRuns);
            
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
        if(analysis.getAnalysisProgram() == Program.PEPTIDE_PROPHET) {
            System.out.println(analysis.getId());
           PeptideProphetAnalysis prophetAnalysis = daoFactory.getPeptideProphetAnalysisDAO().load(analysis.getId());
           if(prophetAnalysis != null)
               sAnalysis.setAnalysisName(prophetAnalysis.getFileName());
        }
        return sAnalysis;
    }
    
    private void linkExperimentsAndDtaSelect(List<ProjectExperiment> experiments, List<YatesRun> yatesRuns) throws SQLException {
        
        // We will get the searchIds from the tblYatesRun* tables
        // Create a map of searchId and experimentId
        Map<Integer, Integer> searchIdToExperimentId = new HashMap<Integer, Integer>();
        for(ProjectExperiment experiment: experiments) {
        	if(!experiment.getHasFullInformation())
        		continue;
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