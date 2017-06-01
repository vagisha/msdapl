/**
 * EditInstrumentAction.java
 * @author Vagisha Sharma
 * Oct 28, 2009
 * @version 1.0
 */
package org.yeastrc.www.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.uwpr.instrumentlog.InstrumentColors;
import org.uwpr.instrumentlog.MsInstrument;
import org.uwpr.instrumentlog.MsInstrumentUtils;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class EditInstrumentAction extends Action {

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

        // Restrict access to administrators
        Groups groupMan = Groups.getInstance();
        if (!groupMan.isMember(user.getResearcher().getID(), "administrators")) {
            ActionErrors errors = new ActionErrors();
            errors.add("access", new ActionMessage("error.access.invalidgroup"));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        int instrumentId = 0;
        try {
            instrumentId = Integer.parseInt(request.getParameter("instrumentId"));
        }
        catch(NumberFormatException e){}
        
        if(instrumentId == 0) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.invalid.id", "instrument: "+instrumentId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        MsInstrument instrument = MsInstrumentUtils.instance().getMsInstrument(instrumentId);
        if(instrument == null) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, 
                    new ActionMessage("error.general.invalid.id", "instrument: "+instrumentId+
                            ". No instrument found with this ID."));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }

        AddInstrumentForm myForm = new AddInstrumentForm();
        myForm.setId(instrument.getID());
        myForm.setName(instrument.getNameOnly());
        myForm.setDescription(instrument.getDescription());
        myForm.setActive(instrument.isActive());
        String color = instrument.getHexColor(); // Colors stored in the database do not start with #.
        myForm.setColor(color == null ? InstrumentColors.getColor(instrument.getID()) : color);
        request.setAttribute("addInstrumentForm", myForm);
        // Kick it to the view page
        return mapping.findForward("Success");

    }
}
