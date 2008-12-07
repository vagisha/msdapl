/**
 * NewProteinInferenceAction.java
 * @author Vagisha Sharma
 * Nov 3, 2008
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.SearchProgram;
import org.yeastrc.www.proteinfer.MsSearchSummary.RunSearchFile;
import org.yeastrc.www.proteinfer.ProgramParameters.Param;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import edu.uwpr.protinfer.ProteinInferenceProgram;

/**
 * 
 */
public class NewProteinInferenceAction extends Action {

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

        int searchId = -1;
        if (request.getParameter("searchId") != null) {
            try {searchId = Integer.parseInt(request.getParameter("searchId"));}
            catch(NumberFormatException e) {searchId = -1;}
        }
        
        if (searchId == -1) {
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.proteinfer.invalid.searchId", searchId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        int projectId = -1;
        if (request.getParameter("projectId") != null) {
            try {projectId = Integer.parseInt(request.getParameter("projectId"));}
            catch(NumberFormatException e) {projectId = -1;}
        }
        
        if (projectId == -1) {
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.proteinfer.invalid.projectId", projectId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        request.setAttribute("projectId", projectId);
        
        // Create our ActionForm
        ProteinInferenceForm newForm = new ProteinInferenceForm();
        request.setAttribute("proteinInferenceForm", newForm);
        
        newForm.setProjectId(projectId);
        MsSearchSummary searchSummary = getSearchSummary(searchId);
        newForm.setSearchSummary(searchSummary);
        
        // we will be using IDPicker -- set the IDPicker parameters
        ProgramParameters params = new ProgramParameters(ProteinInferenceProgram.IDPICKER);
        setProgramDetails(params, searchSummary.getProgram());
        newForm.setProgramParams(params);
        

        // Go!
        return mapping.findForward("Success");

    }
    
    private void setProgramDetails(ProgramParameters params, String searchProgram) {
        if(searchProgram.equals(SearchProgram.SEQUEST.displayName()) || 
           searchProgram.equals(SearchProgram.EE_NORM_SEQUEST.displayName())) {
            for(Param p: params.getParamList()) {
                if(p.getName().equalsIgnoreCase("maxAbsFDR")) {
                    p.setNotes("For Score: XCorr");
                }
                else if (p.getName().equalsIgnoreCase("maxRelFDR")) {
                    p.setNotes("For Score: DeltaCN");
                }
                else if(p.getName().equalsIgnoreCase("decoyRatio")) {
                    p.setNotes("Decoy Ratio will not be used if using R/F for FDR calculation");
                }
            }
        }
        else if(searchProgram.equals(SearchProgram.PROLUCID.displayName())) {
            for(Param p: params.getParamList()) {
                if(p.getName().equalsIgnoreCase("maxAbsFDR")) {
                    p.setNotes("For Score: Primary Score"); // TODO what was the primary score used 
                    // for this Prolucid search?
                }
                else if (p.getName().equalsIgnoreCase("maxRelFDR")) {
                    p.setNotes("For Score: DeltaCN");
                }
                else if(p.getName().equalsIgnoreCase("decoyRatio")) {
                    p.setNotes("Decoy Ratio will not be used if using R/F for FDR calculation");
                }
            }
        }
    }

    private MsSearchSummary getSearchSummary(int searchId) {
        DAOFactory daoFactory = DAOFactory.instance();
        
        MsSearchSummary searchSummary = new MsSearchSummary();
        
        // get the name of the search program
        MsSearchDAO searchDao = daoFactory.getMsSearchDAO();
        MsSearch search = searchDao.loadSearch(searchId);
        searchSummary.setProgram(search.getSearchProgram().displayName());
        
        // get the name(s) of the search databases.
        StringBuilder databases = new StringBuilder();
        for(MsSearchDatabase db: search.getSearchDatabases()) {
            databases.append(", ");
            databases.append(db.getDatabaseFileName());
        }
        if(databases.length() > 0)  databases.deleteCharAt(0);
        searchSummary.setSearchDatabase(databases.toString());
        
        
        
        MsRunSearchDAO runSearchDao = daoFactory.getMsRunSearchDAO();
        List<Integer> runSearchIds = runSearchDao.loadRunSearchIdsForSearch(searchId);
        
        for (int id: runSearchIds) {
            String filename = runSearchDao.loadFilenameForRunSearch(id);
            RunSearchFile rs = new RunSearchFile(id, filename);
            rs.setIsSelected(true);
            searchSummary.addRunSearch(rs);
        }
        return searchSummary;
    }
}
