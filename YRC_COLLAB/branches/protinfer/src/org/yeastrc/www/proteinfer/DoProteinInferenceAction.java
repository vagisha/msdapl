/**
 * DoProteinInferenceAction.java
 * @author Vagisha Sharma
 * Nov 3, 2008
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import java.util.ArrayList;
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
import org.yeastrc.proteinfer.IDPickerExecutor;
import org.yeastrc.proteinfer.IDPickerParams;
import org.yeastrc.proteinfer.SearchSummary;
import org.yeastrc.proteinfer.SearchSummary.RunSearch;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import edu.uwpr.protinfer.SequestHit;
import edu.uwpr.protinfer.SequestSpectrumMatch;
import edu.uwpr.protinfer.filter.FilterException;
import edu.uwpr.protinfer.filter.fdr.FdrCalculatorException;
import edu.uwpr.protinfer.infer.InferredProtein;
import edu.uwpr.protinfer.msdata.MsDataSearchResultsReader;

/**
 * 
 */
public class DoProteinInferenceAction extends Action {

    private static final Logger log = Logger.getLogger(DoProteinInferenceAction.class);
    
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

        ProteinInferenceForm prinferForm = (ProteinInferenceForm) form;
        SearchSummary searchSummary = prinferForm.getSearchSummary();
        IDPickerParams params = prinferForm.getIdPickerParams();
        
        request.setAttribute("searchSummary", prinferForm.getSearchSummary());
        request.setAttribute("params", prinferForm.getIdPickerParams());
        
        List<SequestHit> allFilteredHits = new ArrayList<SequestHit>();
        // get the search hits
        MsDataSearchResultsReader reader = new MsDataSearchResultsReader();
        for (RunSearch runSearch: searchSummary.getRunSearchList()) {
            List<SequestHit> hits = reader.getHitsForRunSearch(runSearch.getRunSearchId());
            // filter the search hits
            List<SequestHit> filteredHits = filterHits(hits, params);
            allFilteredHits.addAll(filteredHits);
        }
        
        // get the proteins matching the filtered proteins
        reader.loadProteinsForHits(allFilteredHits);
        
        // infer the protein list
        List<InferredProtein<SequestSpectrumMatch>> proteins = inferProteinList(allFilteredHits, params);
        
        request.setAttribute("inferredProteins", proteins);
        
        // Go!
        return mapping.findForward("Success");
    }

    private List<InferredProtein<SequestSpectrumMatch>> inferProteinList(
            List<SequestHit> filteredHits, IDPickerParams params) {
        IDPickerExecutor executor = new IDPickerExecutor();
        return executor.inferProteins(filteredHits, params);
    }

    private List<SequestHit> filterHits(List<SequestHit> hits, IDPickerParams params) 
        throws FdrCalculatorException, FilterException {
        IDPickerExecutor executor = new IDPickerExecutor();
        return executor.filterSearchHits(hits, params);
    }
    
    public static void main(String[] args) {
        
    }
}
