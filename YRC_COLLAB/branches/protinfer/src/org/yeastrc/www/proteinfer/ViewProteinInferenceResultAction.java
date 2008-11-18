package org.yeastrc.www.proteinfer;

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
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import edu.uwpr.protinfer.database.dto.ProteinferProtein;
import edu.uwpr.protinfer.idpicker.IDPickerParams;
import edu.uwpr.protinfer.idpicker.SearchSummary;

public class ViewProteinInferenceResultAction extends Action {

    private static final Logger log = Logger.getLogger(ViewProteinInferenceResultAction.class);
    
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

        int pinferId = 0;
        try {pinferId = Integer.parseInt(request.getParameter("inferId"));}
        catch(NumberFormatException e){};
        
        if(pinferId == 0)
            return mapping.findForward("Failure");
        
        long s = System.currentTimeMillis();
        
        // get the ProteinferRun
        IDPickerParams params = ProteinferLoader.getIDPickerParams(pinferId);
        request.setAttribute("params", params);
        SearchSummary summary = ProteinferLoader.getIDPickerInputSummary(pinferId);
        request.setAttribute("searchSummary", summary);
        List<ProteinferProtein> inferredProteins = ProteinferLoader.getProteinferProteins(pinferId);
        request.setAttribute("inferredProteins", inferredProteins);
        int maxClusterId = 0;
        for(ProteinferProtein prot: inferredProteins)
            maxClusterId = Math.max(maxClusterId, prot.getClusterId());
        request.setAttribute("clusterCount", maxClusterId);
        request.setAttribute("pinferId", pinferId);

        
        long e = System.currentTimeMillis();
        log.info("Total time: "+getTime(s, e));
        
        // Go!
        return mapping.findForward("Success");
    }
    
    private static float getTime(long start, long end) {
        long time = end - start;
        float seconds = (float)time / (1000.0f);
        return seconds;
    }
}
