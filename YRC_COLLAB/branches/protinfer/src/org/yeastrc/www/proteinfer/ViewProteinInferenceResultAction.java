package org.yeastrc.www.proteinfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerProteinGroup;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;

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
        
        
        request.setAttribute("pinferId", pinferId);
        
        // get the IdPickerRun
        ProteinferDAOFactory factory = ProteinferDAOFactory.instance();
//        IdPickerRunDAO runDao = factory.getIdPickerRunDao();
//        IdPickerRun run = runDao.getProteinferRun(pinferId);
//        
//        request.setAttribute("params", run.getFilters());
//        
//        IdPickerSummary summary = IdPickerResultsLoader.getIDPickerInputSummary(pinferId);
//        request.setAttribute("searchSummary", summary);
        List<WIdPickerProteinGroup> proteinGroups = IdPickerResultsLoader.getProteinferProteinGroups(pinferId);
        request.setAttribute("proteinGroups", proteinGroups);

        Set<Integer> clusterIds = new HashSet<Integer>();
        for(WIdPickerProteinGroup protGrp: proteinGroups) {
            clusterIds.add(protGrp.getClusterId());
        }
        List<Integer> clusterIdList = new ArrayList<Integer>(clusterIds);
        Collections.sort(clusterIdList);
        request.setAttribute("clusterIds", clusterIdList);
        
        
//        int maxClusterId = 0;
//        int maxPeptides = 0;
//        int minPeptides = params.getMinDistinctPeptides();
//        int maxUniqPeptides = 0;
//        int maxSpectra = 0;
//        
//        for(IdPickerProteinGroup protGrp: proteinGroups) {
//            for(BaseProteinferProtein<T> prot: protGrp.getProteins()) {
//                maxClusterId = Math.max(maxClusterId, prot.getClusterId());
//            }
//            maxPeptides = Math.max(maxPeptides, protGrp.getMatchingPeptideCount());
//            maxUniqPeptides = Math.max(maxUniqPeptides, protGrp.getUniqMatchingPeptideCount());
//            maxSpectra = Math.max(maxSpectra, protGrp.getSpectrumCount());
//        }
//        
//        request.setAttribute("minPeptides", minPeptides);
//        request.setAttribute("maxPeptides", maxPeptides);
//        request.setAttribute("maxUniqPeptides", maxUniqPeptides);
//        request.setAttribute("maxSpectra", maxSpectra);
//        request.setAttribute("clusterCount", maxClusterId);
//        

        
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
