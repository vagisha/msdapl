package org.yeastrc.www.proteinfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
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
import org.yeastrc.www.proteinfer.idpicker.WIdPickerInputSummary;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerProtein;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerProteinGroup;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import edu.uwpr.protinfer.PeptideDefinition;
import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerRun;

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

        // form for filtering and display options
        ProteinInferFilterForm filterForm = null;
        if(form != null) {
            filterForm = (ProteinInferFilterForm)form;
        }
        else {
            filterForm = new ProteinInferFilterForm();
            System.out.println("Creating a new filter form!!"); // this should never happen??
        }
        request.setAttribute("proteinInferFilterForm", filterForm);
        System.out.println("Sequence: "+filterForm.isPeptideDef_useSequence());
        System.out.println("Group Proteins: "+filterForm.isJoinGroupProteins());
        
        // look for the protein inference run id in the form first
        int pinferId = filterForm.getPinferId();
        
        // if this is a newly created form the id will be 0.  In this case
        // look for the pinferId in the request parameters
        if(pinferId == 0) {
            try {pinferId = Integer.parseInt(request.getParameter("inferId"));}
            catch(NumberFormatException e){};
        }
        
        // if we still do not have a valid protein inference run id
        // return an error.
        if(pinferId <= 0) {
            log.error("Invalid protein inference run id: "+pinferId);
            return mapping.findForward("Failure");
        }
        
        long s = System.currentTimeMillis();
        
        request.setAttribute("pinferId", pinferId);
        filterForm.setPinferId(pinferId);
        
        // get the IdPicker Protein groups for the given run
        PeptideDefinition peptideDef = new PeptideDefinition(filterForm.isPeptideDef_useMods(), filterForm.isPeptideDef_useCharge());
        List<WIdPickerProteinGroup> proteinGroups = IdPickerResultsLoader.getProteinferProteinGroups(pinferId, peptideDef);
        
        // filter based on the given thresholds
        filterProteins(proteinGroups, filterForm);
        
        request.setAttribute("proteinGroups", proteinGroups);
        
        // Get some summary
        IdPickerRun run = ProteinferDAOFactory.instance().getIdPickerRunDao().getProteinferRun(pinferId);
        request.setAttribute("unfilteredProteinCount", run.getNumUnfilteredProteins());
        request.setAttribute("filteredProteinGrpCount", proteinGroups.size());
        int parsimGrpCount = 0;
        int filteredProteinCount = 0;
        int parsimProteinCount = 0;
        for(WIdPickerProteinGroup group: proteinGroups) {
            if(group.getProteins().get(0).getProtein().getIsParsimonious())
                parsimGrpCount++;
            for(WIdPickerProtein prot: group.getProteins()) {
                filteredProteinCount++;
                if(prot.getProtein().getIsParsimonious())
                    parsimProteinCount++;
            }
        }
        request.setAttribute("filteredProteinCount", filteredProteinCount);
        request.setAttribute("parsimProteinGrpCount", parsimGrpCount);
        request.setAttribute("parsimProteinCount", parsimProteinCount);
        

        // Cluster IDs in this set
        Set<Integer> clusterIds = new HashSet<Integer>();
        for(WIdPickerProteinGroup protGrp: proteinGroups) {
            clusterIds.add(protGrp.getClusterId());
        }
        List<Integer> clusterIdList = new ArrayList<Integer>(clusterIds);
        Collections.sort(clusterIdList);
        request.setAttribute("clusterIds", clusterIdList);
        
        
        // Run summary
        IdPickerRun idpickerRun = ProteinferDAOFactory.instance().getIdPickerRunDao().getProteinferRun(pinferId);
        request.setAttribute("idpickerRun", idpickerRun);
        
        // Input summary
        List<WIdPickerInputSummary> inputSummary = IdPickerResultsLoader.getIDPickerInputSummary(pinferId);
        request.setAttribute("inputSummary", inputSummary);
        int totalDecoyHits = 0;
        int totalTargetHits = 0;
        int filteredTargetHits = 0;
        for(WIdPickerInputSummary input: inputSummary) {
            totalDecoyHits += input.getInput().getNumDecoyHits();
            totalTargetHits += input.getInput().getNumTargetHits();
            filteredTargetHits += input.getInput().getNumFilteredTargetHits();
        }
        request.setAttribute("totalDecoyHits", totalDecoyHits);
        request.setAttribute("totalTargetHits", totalTargetHits);
        request.setAttribute("filteredTargetHits", filteredTargetHits);
        
        
        
        long e = System.currentTimeMillis();
        log.info("Total time (ViewProteinInferenceResultAction): "+getTime(s, e));
        
        // Go!
        return mapping.findForward("Success");
    }
    
    private void filterProteins(List<WIdPickerProteinGroup> proteinGroups, ProteinInferFilterForm filterForm) {
        
        
        // If we filtering on coverage we will have to look at individual proteins in the group
        if(filterForm.getMinCoverage() > 0.0) {
            double minCoverage = filterForm.getMinCoverage();
            for(WIdPickerProteinGroup group: proteinGroups) {
                
                Iterator<WIdPickerProtein> protIter = group.getProteins().iterator();
                while(protIter.hasNext()) {
                    WIdPickerProtein prot = protIter.next();
                    if(prot.getProtein().getCoverage() < minCoverage)
                        protIter.remove();
                }
             }
            
            // if there are any protein groups with no proteins remove them
            Iterator<WIdPickerProteinGroup> grpIter = proteinGroups.iterator();
            while(grpIter.hasNext()) {
                WIdPickerProteinGroup grp = grpIter.next();
                if(grp.getProteinCount() == 0)
                    grpIter.remove();
            }
        }
        
        // rest of the filters can operate at the protein group level
        Iterator<WIdPickerProteinGroup> grpIter = proteinGroups.iterator();
        int minSpetrumMatches = filterForm.getMinSpectrumMatches();
        int minPeptides = filterForm.getMinPeptides();
        int minUniqPeptides = filterForm.getMinUniquePeptides();
        
        while(grpIter.hasNext()) {
            WIdPickerProteinGroup grp = grpIter.next();
            
            if(grp.getMatchingPeptideCount() < minPeptides)
                grpIter.remove();
            else if(grp.getUniqMatchingPeptideCount() < minUniqPeptides)
                grpIter.remove();
            else if(grp.getSpectrumCount() < minSpetrumMatches)
                grpIter.remove();
        }
    }

    private static float getTime(long start, long end) {
        long time = end - start;
        float seconds = (float)time / (1000.0f);
        return seconds;
    }
}
