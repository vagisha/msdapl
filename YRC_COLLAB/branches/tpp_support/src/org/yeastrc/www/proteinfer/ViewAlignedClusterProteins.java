/**
 * ViewAlignedProteins.java
 * @author Vagisha Sharma
 * Mar 6, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import java.util.Collections;
import java.util.Comparator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.www.proteinfer.alignment.AlignedProtein;
import org.yeastrc.www.proteinfer.alignment.AlignedProteins;
import org.yeastrc.www.proteinfer.alignment.SequenceAligner;
import org.yeastrc.www.proteinfer.idpicker.IdPickerResultsLoader;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerCluster;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class ViewAlignedClusterProteins extends Action{

    private static final Logger log = Logger.getLogger(ViewAlignedClusterProteins.class);
    
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

        // get the protein inference id
        int pinferId = 0;
        try {pinferId = Integer.parseInt(request.getParameter("pinferId"));}
        catch(NumberFormatException e){};
        // if we  do not have a valid protein inference run id
        // return an error.
        if(pinferId <= 0) {
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.proteinfer.invalid.pinferId", pinferId));
            saveErrors( request, errors );
            ActionForward failure = new ActionForward("standardHome", false) ;
            return failure;
        }
        
        // get the cluster id
        int clusterId = 0;
        try {clusterId = Integer.parseInt(request.getParameter("clusterId"));}
        catch(NumberFormatException e){};
        // if we  do not have a valid cluster id
        // return an error.
        if(clusterId <= 0) {
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.proteinfer.invalid.id", "cluster ID - "+clusterId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        WIdPickerCluster cluster = IdPickerResultsLoader.getIdPickerCluster(pinferId, clusterId, new PeptideDefinition());
        AlignedProteins aligned = SequenceAligner.instance().alignCluster(cluster);
        // sort the aligned proteins by groupId
        final int anchorGrpId = aligned.getAnchorProtein().getPinferProteinGroupId();
        Collections.sort(aligned.getAlignedProteins(), new Comparator<AlignedProtein>() {
            @Override
            public int compare(AlignedProtein o1, AlignedProtein o2) {
                if(o1.getPinferProteinGroupId() == anchorGrpId)
                    return -1;
                else if(o2.getPinferProteinGroupId() == anchorGrpId)
                    return 1;
                else
                    return Integer.valueOf(o1.getPinferProteinGroupId()).compareTo(o2.getPinferProteinGroupId());
                
            }});
        
        request.setAttribute("pinferId", pinferId);
        request.setAttribute("clusterId", clusterId);
        request.setAttribute("alignedProteins", aligned);
        
        // Go!
        return mapping.findForward( "Success" ) ;
    }
}
