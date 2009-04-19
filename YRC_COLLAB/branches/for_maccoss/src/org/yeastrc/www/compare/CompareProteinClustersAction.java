/**
 * CompareProteinClustersAction.java
 * @author Vagisha Sharma
 * Apr 18, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.www.proteinfer.idpicker.IdPickerResultsLoader;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerCluster;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import edu.uwpr.protinfer.PeptideDefinition;
import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerProteinBaseDAO;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerProteinBase;

/**
 * 
 */
public class CompareProteinClustersAction extends Action {

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
        String piDatasetIdStr = request.getParameter("piDatasetIds");
        List<Dataset> piDatasets = getDatasets(piDatasetIdStr, DatasetSource.PROT_INFER);
        
        // get the DTASelect ids to compare
        String dtaDatasetIdStr = request.getParameter("dtaDatasetIds");
        List<Dataset> dtaDatasets = getDatasets(dtaDatasetIdStr, DatasetSource.DTA_SELECT);
        
        
        // Get the selected nrseqProteinId
        int nrseqProteinId = 0;
        if(request.getParameter("nrseqProteinId") != null) {
            try {nrseqProteinId = Integer.parseInt(request.getParameter("nrseqProteinId"));}
            catch(NumberFormatException e){}
        }
        if(nrseqProteinId <= 0) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Invalid protein ID in request."));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        
        // Combine the datasets
        List<Dataset> datasets = new ArrayList<Dataset>(piDatasets.size() + dtaDatasets.size());
        datasets.addAll(piDatasets);
        // TODO Don't know how to do this for DTAselect.
        //datasets.addAll(dtaDatasets); 
        if(datasets.size() == 0) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "No datasets found to compare."));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
       
        // Get the protein clusters for this protein in the given datasets.
        IdPickerProteinBaseDAO protDao = ProteinferDAOFactory.instance().getIdPickerProteinBaseDao();
        ArrayList<Integer> nrseqIds = new ArrayList<Integer>(1);
        nrseqIds.add(nrseqProteinId);
        PeptideDefinition peptDef = new PeptideDefinition();
        for(Dataset dataset: datasets) {
            List<Integer> pinferProteinIds = protDao.getProteinIdsForNrseqIds(dataset.getDatasetId(), nrseqIds);
            if(pinferProteinIds.size() == 0)
                continue;
            int pinferProteinId = pinferProteinIds.get(0);
            IdPickerProteinBase idpProtein = protDao.loadProtein(pinferProteinId);
            
            WIdPickerCluster cluster = IdPickerResultsLoader.getIdPickerCluster(dataset.getDatasetId(), 
                    idpProtein.getClusterId(), peptDef, false);
        }
        
        
        return mapping.findForward("Success");
    }

    private List<Integer> parseCommaSeparated(String idString) {
        String[] tokens = idString.split(",");
        List<Integer> ids = new ArrayList<Integer>(tokens.length);
        for(String tok: tokens) {
            String trimTok = tok.trim();
            if(trimTok.length() > 0)
                ids.add(Integer.parseInt(trimTok));
        }
        return ids;
    }
    private List<Dataset> getDatasets(String idString, DatasetSource source) {
        List<Integer> ids = parseCommaSeparated(idString);
        List<Dataset> datasets = new ArrayList<Dataset>(ids.size());
        for(int id: ids)
            datasets.add(new Dataset(id, source));
        return datasets;
    }
}
