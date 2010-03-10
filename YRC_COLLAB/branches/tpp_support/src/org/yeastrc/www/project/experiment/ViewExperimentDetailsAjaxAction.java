/**
 * ViewExperimentDetailsAjaxAction.java
 * @author Vagisha Sharma
 * Mar 10, 2010
 * @version 1.0
 */
package org.yeastrc.www.project.experiment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.experiment.ExperimentProteinProphetRun;
import org.yeastrc.experiment.ExperimentProteinferRun;
import org.yeastrc.experiment.ExperimentSearch;
import org.yeastrc.experiment.ProjectExperiment;
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
import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.ms.domain.protinfer.ProteinferRun;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetRun;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.www.proteinfer.ProteinInferJobSearcher;
import org.yeastrc.www.proteinfer.ProteinferJob;
import org.yeastrc.www.proteinfer.idpicker.IdPickerResultsLoader;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;
import org.yeastrc.yates.YatesRun;
import org.yeastrc.yates.YatesRunMsSearchLinker;
import org.yeastrc.yates.YatesRunSearcher;

/**
 * 
 */
public class ViewExperimentDetailsAjaxAction extends Action {

	private static DAOFactory daoFactory = DAOFactory.instance();
	
	public ActionForward execute( ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response )
	throws Exception {


		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			response.getWriter().write("You are not logged in!");
			response.setStatus(HttpServletResponse.SC_SEE_OTHER); // Status code (303) indicating that the response to the request can be found under a different URI.
			return null;
		}


		// Get the experimentId they're after
		int experimentId = 0;
		try {
			String strID = request.getParameter("experimentId");

			if (strID == null || strID.equals("")) {
				response.setContentType("text/html");
				response.getWriter().write("<b>Invalid Experiment ID: "+experimentId+"</b>");
				return null;
			}

			experimentId = Integer.parseInt(strID);

		} catch (NumberFormatException nfe) {
			response.setContentType("text/html");
			response.getWriter().write("<b>Invalid Experiment ID: "+experimentId+"</b>");
			return null;
		}
		
		// Get the projectId
		int projectId = 0;
		try {
			String strID = request.getParameter("projectId");

			if (strID == null || strID.equals("")) {
				response.setContentType("text/html");
				response.getWriter().write("<b>Invalid Project ID: "+projectId+"</b>");
				return null;
			}

			projectId = Integer.parseInt(strID);

		} catch (NumberFormatException nfe) {
			response.setContentType("text/html");
			response.getWriter().write("<b>Invalid Project ID: "+projectId+"</b>");
			return null;
		}


		// Get the data for this experiment
		ProjectExperiment experiment = getProjectExperiment(projectId, experimentId);

		// load the dtaselect results;
		// Check for yates MS data
        YatesRunSearcher yrs = new YatesRunSearcher();
        yrs.setProjectID(projectId);
        yrs.setMostRecent( true );
        List<YatesRun> yatesRuns = yrs.search();
        
        
        // Associate the DTASelect runs with the respective experiments
        linkExperimentAndDtaSelect(experiment, yatesRuns);
		

		request.setAttribute("experiment", experiment);

		// Forward them on to the happy success view page!
		return mapping.findForward("Success");

	}
	
	 private ProjectExperiment getProjectExperiment(int projectId, int experimentId) throws Exception {
	        
	        
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
				 return null;
		 }
		 catch(Exception e) {return null;} // because job with experimentID does not exist. Should not really happen.

		 ProjectExperiment pExpt = new ProjectExperiment(expt);
		 pExpt.setUploadJobId(job.getId());
		 if(status != JobUtils.STATUS_COMPLETE)
			 pExpt.setUploadSuccess(false);
	            
	            
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
	     
		 return pExpt;
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
	    
	 private void linkExperimentAndDtaSelect(ProjectExperiment experiment, List<YatesRun> yatesRuns) throws SQLException {

		 // We will get the searchIds from the tblYatesRun* tables
		 // Create a map of searchId and experimentId
		 Map<Integer, Integer> searchIdToExperimentId = new HashMap<Integer, Integer>();
		 if(!experiment.getHasFullInformation())
			 return;
		 
		 for(ExperimentSearch search: experiment.getSearches())
			 searchIdToExperimentId.put(search.getId(), experiment.getId());


		 // put the DTASelect results in the experiment if it belongs to it.
		 for(YatesRun run: yatesRuns) {
			 int runId = run.getId();
			 int searchId = YatesRunMsSearchLinker.linkYatesRunToMsSearch(runId);

			 if(searchId > 0) {
				 Integer experimentId = searchIdToExperimentId.get(searchId);
				 if(experimentId != null) {

					 if(experiment.getId() == experimentId) {
						 experiment.setDtaSelect(run);
					 }
				 }
			 }
		 }
	 }

}
