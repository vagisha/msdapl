/**
 * ViewAlignedGroupProteins.java
 * @author Vagisha Sharma
 * Mar 6, 2009
 * @version 1.0
 */
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
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.www.proteinfer.alignment.AlignedProteins;
import org.yeastrc.www.proteinfer.alignment.SequenceAligner;
import org.yeastrc.www.proteinfer.idpicker.IdPickerResultsLoader;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerProtein;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class ViewAlignedGroupProteins extends Action {

    private static final Logger log = Logger.getLogger(ViewAlignedGroupProteins.class);
    
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
        
        // get the group id
        int groupId = 0;
        try {groupId = Integer.parseInt(request.getParameter("groupId"));}
        catch(NumberFormatException e){};
        // if we  do not have a valid cluster id
        // return an error.
        if(groupId <= 0) {
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.proteinfer.invalid.id", "group ID - "+groupId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        List<WIdPickerProtein> proteins = IdPickerResultsLoader.getGroupProteins(pinferId, groupId, new PeptideDefinition());
        AlignedProteins aligned = SequenceAligner.instance().alignProteins(proteins);
        request.setAttribute("pinferId", pinferId);
        request.setAttribute("groupId", groupId);
        request.setAttribute("alignedProteins", aligned);
        
        // Go!
        return mapping.findForward( "Success" ) ;
    }
}
