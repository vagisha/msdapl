/**
 * ComparePeptidesAction.java
 * @author Vagisha Sharma
 * Apr 15, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class ComparePeptidesAjaxAction extends Action {

    
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

        
        // get the protein inference ids to compare
        ComparePeptidesForm myForm = (ComparePeptidesForm) form;
        
        
        // Get the selected nrseqProteinId
        int nrseqProteinId = myForm.getNrseqProteinId();
        if(nrseqProteinId <= 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid protein ID in request</b>");
            return null;
        }
        
        
        // Get the datasets being compared
        List<Dataset> datasets = myForm.getDatasetList();
        if(datasets.size() == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>No datasets found to compare</b>");
            return null;
        }
       
        
        // Do the comparison
        PeptideComparisonDataset comparison = DatasetPeptideComparer.instance().getComparisonPeptides(nrseqProteinId, datasets);
        request.setAttribute("pept_comparison", comparison);
        
        return mapping.findForward("Success");
    }
}
