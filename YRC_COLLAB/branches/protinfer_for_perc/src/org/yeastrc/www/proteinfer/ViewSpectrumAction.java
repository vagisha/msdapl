package org.yeastrc.www.proteinfer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.GenericSearchDAO.MassType;
import org.yeastrc.ms.dao.search.prolucid.ProlucidSearchDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchDAO;
import org.yeastrc.ms.domain.run.ms2file.MS2Scan;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanCharge;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.util.PeakConverterString;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

public class ViewSpectrumAction extends Action {

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


        int scanID = 0;
        int runSearchResultID = 0;
        String scanIDStr = request.getParameter("scanID");
        String runSearchResultIDStr = request.getParameter("runSearchResultID");
        
        try {scanID = Integer.parseInt(scanIDStr);}
        catch(NumberFormatException e){}
        
        try {runSearchResultID = Integer.parseInt(runSearchResultIDStr);}
        catch(NumberFormatException e){}
        
        if(scanID == 0) {
            ActionErrors errors = new ActionErrors();
            errors.add("yates", new ActionMessage("error.yates.peptide.invalidid"));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        if(runSearchResultID == 0) {
            ActionErrors errors = new ActionErrors();
            errors.add("yates", new ActionMessage("error.yates.peptide.invalidid"));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        try {
            List<String> params = useMsData(scanID, runSearchResultID, mapping, request);
            if (params == null) {
                // any errors have already been set. 
                return mapping.findForward("Failure");
            }
            request.setAttribute("params", params);
        }
        catch(Exception e) {
            ActionErrors errors = new ActionErrors();
            errors.add("spectra", new ActionMessage("error.msdata.spectra.dataerror", e.getMessage()));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        // viewSpectrum(129794, 1656821)
        return mapping.findForward("Success");
    
    }
    
    private List<String> useMsData(int scanId, int runSearchResultId, ActionMapping mapping, HttpServletRequest request) {
        
        MsSearchResultDAO resultDao = DAOFactory.instance().getMsSearchResultDAO();
        MsSearchResult result = resultDao.load(runSearchResultId);
        int runSearchId = result.getRunSearchId();
        MsRunSearchDAO runSearchDao = DAOFactory.instance().getMsRunSearchDAO();
        MsRunSearch runSearch = runSearchDao.loadRunSearch(runSearchId);
        int searchId = runSearch.getSearchId();
        // get the search
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        MsSearch search = searchDao.loadSearch(searchId);
        
        String Sq = result.getResultPeptide().getModifiedPeptide();
        Sq = result.getResultPeptide().getPreResidue()+"."+Sq+"."+result.getResultPeptide().getPostResidue();
        request.setAttribute("peptideSeq", Sq);
        
        MsRunDAO runDao = DAOFactory.instance().getMsRunDAO();
        String filename = runDao.loadFilenameNoExtForRun(runSearch.getRunId());

        MsScanDAO msScanDao = DAOFactory.instance().getMsScanDAO();
        int scanNumber = msScanDao.load(result.getScanId()).getStartScanNum();
        
        // get the scan
        MS2ScanDAO scanDao = DAOFactory.instance().getMS2FileScanDAO();
        MS2Scan scan = scanDao.load(scanId);
        
        // get the precursor mass for the given charge state
        List<MS2ScanCharge> scChargeList = scan.getScanChargeList();
        BigDecimal massFrmScanCharge = null;
        int charge = result.getCharge();
        for (MS2ScanCharge sc: scChargeList) {
            if (sc.getCharge() == charge) {
                massFrmScanCharge = sc.getMass();
                break;
            }
        }
        // If we did not find the mass, calculate it from what we have
        double mass = 0.0;
        if (massFrmScanCharge == null) {
            mass = (scan.getPrecursorMz().doubleValue() - 1.00794) * charge + 1.00794;
        }
        else
            mass = massFrmScanCharge.doubleValue();
        
        
        // things that are required to be in the request
        request.setAttribute("filename", filename);
        request.setAttribute("firstMass", mass);
        request.setAttribute("firstCharge", charge);
        request.setAttribute("scanNumber", scanNumber);
        request.setAttribute("database", search.getSearchDatabases().get(0).getDatabaseFileName());
        
        // parameters for the applet
        List <String>params = new ArrayList<String>();
        
        params.add("<PARAM NAME=\"MatchSeq\" VALUE=\"" + Sq + "\">");
        params.add("<PARAM NAME=\"PreMPlusH\" VALUE=\"" +mass + "\">");
        params.add("<PARAM NAME=\"PreZ\" VALUE=\"" + charge + "\">");
        
        // dynamic modifications mod1, mod2, etc; mod1residues, mod2residues etc
        List<MsResidueModification> dynaResMods = search.getDynamicResidueMods();
        Map<String, String> modMassResidueMap = new HashMap<String, String>();
        for (MsResidueModification mod: dynaResMods) {
            String chars = modMassResidueMap.get(mod.getModificationMass().toString());
            if (chars == null)
                chars = String.valueOf(mod.getModifiedResidue());
            else
                chars += mod.getModifiedResidue();
            modMassResidueMap.put(mod.getModificationMass().toString(), chars);
        }
        int i = 1;
        for (String massKey: modMassResidueMap.keySet()) {
            params.add("<PARAM NAME=\"mod"+(i)+"\" VALUE=\"" + massKey + "\">");
            params.add("<PARAM NAME=\"mod"+(i++)+"residues\" VALUE=\"" + modMassResidueMap.get(massKey) + "\">");
        }
        
        
        // static residue modifications
        List<MsResidueModification> residueStaticMods = search.getStaticResidueMods();
        for (MsResidueModification mod: residueStaticMods) {
            params.add("<PARAM NAME=\"static_mod_"+mod.getModifiedResidue()+"\" VALUE=\"" + mod.getModificationMass().toString() + "\">");
        }
        
        
        // Need these values from the search parameters 
        MassType fragMassType = null;
        MassType parentMassType = null;
        if (search.getSearchProgram() == Program.SEQUEST || 
            search.getSearchProgram() == Program.EE_NORM_SEQUEST) {
            SequestSearchDAO seqSearchDao = DAOFactory.instance().getSequestSearchDAO();
            fragMassType = seqSearchDao.getFragmentMassType(searchId);
            parentMassType = seqSearchDao.getParentMassType(searchId);
        }
        else if (search.getSearchProgram() == Program.PROLUCID) {
            ProlucidSearchDAO psearchDao = DAOFactory.instance().getProlucidSearchDAO();
            fragMassType = psearchDao.getFragmentMassType(searchId);
            parentMassType = psearchDao.getParentMassType(searchId);
        }
        if (fragMassType == null) {
            ActionErrors errors = new ActionErrors();
            errors.add("spectra", new ActionMessage("error.msdata.spectra.massTypeError"));
            saveErrors( request, errors );
            return null;
        }
        else {
            params.add("<PARAM NAME=\"AvgForFrag\" VALUE=\"" + (fragMassType == MassType.AVG ? "true" : "false") + "\">");
        }
        if (parentMassType == null) {
            ActionErrors errors = new ActionErrors();
            errors.add("spectra", new ActionMessage("error.msdata.spectra.massTypeError"));
            saveErrors( request, errors );
            return null;
        }
        else {
            params.add("<PARAM NAME=\"AvgForParent\" VALUE=\"" + (parentMassType == MassType.AVG ? "true" : "false") + "\">"); 
        }
        
        // set the m/z intensity pairs
        String peaks = scan.peakDataString();
        PeakConverterString converter = new PeakConverterString();
        List<String[]> peakList = converter.convert(peaks);
        i = 0;
        for (String[] peak: peakList) {
            params.add("<PARAM NAME=\"MZ" + i + "\" VALUE=\"" + peak[0] + "\">");
            params.add("<PARAM NAME=\"Int" +(i++) + "\" VALUE=\"" + peak[1] + "\">");
        }
        
        return params;
    }
}
