/**
 * ReadClusteredSpectrumCountsAction.java
 * @author Vagisha Sharma
 * Apr 20, 2010
 * @version 1.0
 */
package org.yeastrc.www.compare.clustering;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.ms.util.StringUtils;
import org.yeastrc.www.compare.ProteinGroupComparisonDataset;
import org.yeastrc.www.compare.ProteinSetComparisonForm;
import org.yeastrc.www.compare.SpeciesChecker;
import org.yeastrc.www.compare.util.VennDiagramCreator;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class ReadClusteredSpectrumCountsAction extends Action {

	private static final Logger log = Logger.getLogger(ReadClusteredSpectrumCountsAction.class.getName());

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

		long jobToken = 0;
		String strId = (String)request.getParameter("token");
		try {
			if(strId != null) {
				jobToken = Long.parseLong(strId);
			}
		}
		catch (NumberFormatException e) {}
		if(jobToken <= 0) {
			ActionErrors errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", 
					"Invalid token in request: "+strId));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		
		// Check if we have a results directory matching this token
		String clustDir = request.getSession().getServletContext().getRealPath(ClusteringConstants.BASE_DIR);
		clustDir += File.separator+jobToken;

		if(!(new File(clustDir).exists())) {
			ActionErrors errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", 
					"No results found for token: "+strId));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}

		// Read the results
		String grpComparisonFile = clustDir+File.separator+ClusteringConstants.PROT_GRP_SER;
		ProteinGroupComparisonDataset grpComparison = null;
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(grpComparisonFile));
			grpComparison = (ProteinGroupComparisonDataset) ois.readObject();
		}
		catch (IOException e) {
			ActionErrors errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", 
					"Error reading result for ProteinGroupComparisonDataset. "+e.getMessage()));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		catch(ClassCastException e) {
			ActionErrors errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", 
					"Error reading result for ProteinGroupComparisonDataset. "+e.getMessage()));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		finally {
			if(ois != null) try {ois.close();} catch(IOException e){}
		}

		// Read the form
		String formFile = clustDir+File.separator+ClusteringConstants.FORM_SER;
		ProteinSetComparisonForm myForm = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(formFile));
			myForm = (ProteinSetComparisonForm) ois.readObject();
		}
		catch (IOException e) {
			ActionErrors errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", 
			"Error reading result for ProteinGroupComparisonDataset. "+e.getMessage()));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		catch(ClassCastException e) {
			ActionErrors errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", 
			"Error reading result for ProteinGroupComparisonDataset. "+e.getMessage()));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		finally {
			if(ois != null) try {ois.close();} catch(IOException e){}
		}
		
		// Get the requested page number
		int page = 0;
		strId = (String)request.getParameter("page");
		try {
			if(strId != null) {
				page = Integer.parseInt(strId);
			}
		}
		catch (NumberFormatException e) {}
		
		
		if(page <= 0) {
			ActionErrors errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", 
					"Invalid page number in request: "+strId));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		
		// Get the number of results to display per page
		int numPerPage = 50;
		strId = (String)request.getParameter("count");
		try {
			if(strId != null) {
				numPerPage = Integer.parseInt(strId);
			}
		}
		catch (NumberFormatException e) {}
		
		if(numPerPage <= 1) {
			ActionErrors errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", 
					"Invalid result count / page in request: "+strId));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		
		
		grpComparison.setRowCount(numPerPage);
		grpComparison.setCurrentPage(page);
		
		// R image output
		String imgUrl = request.getSession().getServletContext().getContextPath()+"/"+ClusteringConstants.BASE_DIR+"/"+jobToken+"/"+ClusteringConstants.IMG_FILE;
        request.setAttribute("clusteredImgUrl", imgUrl);
        
        // Create Venn Diagram only if 2 or 3 datasets are being compared
        if(grpComparison.getDatasetCount() == 2 || grpComparison.getDatasetCount() == 3) {
            String googleChartUrl = VennDiagramCreator.instance().getChartUrl(grpComparison);
            request.setAttribute("chart", googleChartUrl);
        }
        
		// create a list of the dataset ids being compared
		// Get the selected protein inference run ids
		List<Integer> allRunIds = myForm.getAllSelectedRunIds();
		request.setAttribute("datasetIds", StringUtils.makeCommaSeparated(allRunIds));

		request.setAttribute("comparison", grpComparison);
		request.setAttribute("proteinSetComparisonForm", myForm);
		request.setAttribute("speciesIsYeast", SpeciesChecker.isSpeciesYeast(grpComparison.getDatasets()));
		
		return mapping.findForward("ProteinGroupList");
	}
}
