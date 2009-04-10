/**
 * CompareProtInferResultsAction.java
 * @author Vagisha Sharma
 * Apr 9, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.compare;

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

import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferRunDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerProteinBaseDAO;
import edu.uwpr.protinfer.database.dto.ProteinferRun;

/**
 * 
 */
public class CompareProtInferResultsAction extends Action {

    private static final Logger log = Logger.getLogger(CompareProtInferResultsAction.class);

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


        // get the protein inference ids to compare
        String pinferIds = request.getParameter("piRunIds");
        if(pinferIds == null) {
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.proteinfer.invalid.pinferId", pinferIds));
            saveErrors( request, errors );
            ActionForward failure = new ActionForward("standardHome", false) ;
            return failure;
        }
        
        String[] ids = pinferIds.trim().split(",");
        int[] piRunIds = new int[ids.length];
        int index = 0;
        for(String id: ids) {
            try {
                int piId = Integer.parseInt(id);
                piRunIds[index++] = piId;
            }
            catch(NumberFormatException e) {
                ActionErrors errors = new ActionErrors();
                errors.add("proteinfer", new ActionMessage("error.proteinfer.invalid.pinferId", pinferIds));
                saveErrors( request, errors );
                ActionForward failure = new ActionForward("standardHome", false) ;
                return failure;
            }
        }
        
        if(piRunIds.length < 2 || piRunIds.length > 3) {
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.proteinfer.invalid.pinferId", 
                    "Cannot compare "+piRunIds.length+" protein inference runs"));
            saveErrors( request, errors );
            ActionForward failure = new ActionForward("standardHome", false) ;
            return failure;
        }
        
        ProteinferDAOFactory fact = ProteinferDAOFactory.instance();
        ProteinferRunDAO runDao = fact.getProteinferRunDao();
        ProteinferRun run1 = null;
        ProteinferRun run2 = null;
        ProteinferRun run3 = null;
        
        run1 = runDao.loadProteinferRun(piRunIds[0]);
        run2 = runDao.loadProteinferRun(piRunIds[1]);
        if(piRunIds.length > 2)
            run3 = runDao.loadProteinferRun(piRunIds[2]);
        
        IdPickerProteinBaseDAO protDao = fact.getIdPickerProteinBaseDao();
        List<Integer> nrseqIds1 = protDao.getNrseqProteinIds(piRunIds[0], true);
        List<Integer> nrseqIds2 = protDao.getNrseqProteinIds(piRunIds[1], true);
        List<Integer> nrseqIds3 = null;
        if(piRunIds.length > 3)
            nrseqIds3 = protDao.getNrseqProteinIds(piRunIds[1], true);
        
        
        
        
        return mapping.findForward("Success");

    }
}
