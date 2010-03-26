/**
 * ProteinProphetViewAction.java
 * @author Vagisha Sharma
 * Mar 25, 2010
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.proteinProphet;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetFilterCriteria;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetROC;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetRun;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectDAO;
import org.yeastrc.www.misc.ResultsPager;
import org.yeastrc.www.proteinfer.GOSupportChecker;
import org.yeastrc.www.proteinfer.ProteinInferSessionManager;
import org.yeastrc.www.proteinfer.ProteinInferToProjectMapper;
import org.yeastrc.www.proteinfer.idpicker.ViewProteinInferenceResultAction;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class ProteinProphetViewAction extends Action {

	private static final Logger log = Logger.getLogger(ViewProteinInferenceResultAction.class);

	public ActionForward execute( ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response )
	throws Exception {

		// User making this request
        User user = UserUtils.getUser(request);

        // form for filtering and display options
        ProteinProphetFilterForm filterForm = (ProteinProphetFilterForm)form;
        request.setAttribute("proteinProphetFilterForm", filterForm);
        
        // look for the protein inference run id in the form first
        int pinferId = filterForm.getPinferId();
        request.setAttribute("pinferId", pinferId);
        
        
        // Get a list of projects for this protein inference run.  If the user making the request to view this
        // protein inference run is not affiliated with the projects, they should not be able to edit any of 
        // the editable fields
        List<Integer> projectIds = ProteinInferToProjectMapper.map(pinferId);
        boolean writeAccess = false;
        ProjectDAO projDao = ProjectDAO.instance();
        for(int projectId: projectIds) {
            Project project = projDao.load(projectId);
            if(project.checkAccess(user.getResearcher())) {
                writeAccess = true;
                break;
            }
        }
        request.setAttribute("writeAccess", writeAccess);
        
        
        long s = System.currentTimeMillis();
        
        
        // Get the peptide definition; We don't get peptide definition from ProteinProphet params so just
        // use a dummy one.
        PeptideDefinition peptideDef = new PeptideDefinition();
        peptideDef.setUseCharge(true);
        peptideDef.setUseMods(true);
        
        
        // Get the filtering criteria
        ProteinProphetFilterCriteria filterCriteria = filterForm.getFilterCriteria(peptideDef);
        
        
        // Get the protein Ids that fulfill the criteria.
        List<Integer> proteinIds = ProteinProphetResultsLoader.getProteinIds(pinferId, filterCriteria);
        
        // put the list of filtered and sorted protein IDs in the session, along with the filter criteria
        ProteinInferSessionManager.getInstance().putForProteinProphet(request, pinferId, filterCriteria, proteinIds);
        
        
        // page number is now 1
        int pageNum = 1;
        
        
        // limit to the proteins that will be displayed on this page
        List<Integer> proteinIdsPage = null;
        if(proteinIds.size() > 0) {
        	// get the index range that is to be displayed in this page
        	int[] pageIndices = ResultsPager.instance().getPageIndices(proteinIds, pageNum,false);

        	// sublist to be displayed
        	proteinIdsPage = ProteinProphetResultsLoader.getPageSublist(proteinIds, pageIndices,
        			filterCriteria.isGroupProteins(), false);
        }
        else {
        	proteinIdsPage = new ArrayList<Integer>(0);
        }
        
        // get the protein groups 
        List<WProteinProphetProteinGroup> proteinGroups = null;
        proteinGroups = ProteinProphetResultsLoader.getProteinProphetGroups(pinferId, proteinIdsPage, peptideDef);
        
        request.setAttribute("proteinGroups", proteinGroups);
        
        // get the list of page numbers to display
        int pageCount = ResultsPager.instance().getPageCount(proteinIds.size());
        List<Integer> pages = ResultsPager.instance().getPageList(proteinIds.size(), pageNum);
        
        
        request.setAttribute("currentPage", pageNum);
        request.setAttribute("onFirst", pageNum == 1);
        request.setAttribute("onLast", (pages.size() == 0 || (pageNum == pages.get(pages.size() - 1))));
        request.setAttribute("pages", pages);
        request.setAttribute("pageCount", pageCount);
        
        
        // Run summary
        ProteinProphetRun proteinProphetRun = ProteinferDAOFactory.instance().getProteinProphetRunDao().loadProteinferRun(pinferId);
        request.setAttribute("proteinProphetRun", proteinProphetRun);
        
        // Input summary
        request.setAttribute("filteredUniquePeptideCount", ProteinProphetResultsLoader.getUniquePeptideCount(pinferId));
        
        // Results summary
        WProteinProphetResultSummary summary = ProteinProphetResultsLoader.getProteinProphetResultSummary(pinferId, proteinIds);
        request.setAttribute("filteredProteinCount", summary.getFilteredProteinCount());
        request.setAttribute("parsimProteinCount", summary.getFilteredParsimoniousProteinCount());
        request.setAttribute("filteredProteinGrpCount", summary.getFilteredProteinGroupCount());
        request.setAttribute("parsimProteinGrpCount", summary.getFilteredParsimoniousProteinGroupCount());
        
        
        request.setAttribute("sortBy", filterCriteria.getSortBy());
        request.setAttribute("sortOrder", filterCriteria.getSortOrder());
        
        // ROC
        ProteinProphetROC rocSummary = proteinProphetRun.getRoc();
        request.setAttribute("rocSummary", rocSummary);
        
        
        if(GOSupportChecker.isSupported(pinferId)) {
        	request.setAttribute("showGoForm", true);
        }
        
        long e = System.currentTimeMillis();
        log.info("Total time (ProteinProphetViewAction): "+TimeUtils.timeElapsedSeconds(s, e));
        
        // Go!
        return mapping.findForward("Success");
	}
	
}
