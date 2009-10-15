package org.yeastrc.www.proteinfer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.experiment.MascotResultPlus;
import org.yeastrc.experiment.SequestResultPlus;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.GenericSearchDAO.MassType;
import org.yeastrc.ms.dao.search.mascot.MascotSearchDAO;
import org.yeastrc.ms.dao.search.mascot.MascotSearchResultDAO;
import org.yeastrc.ms.dao.search.prolucid.ProlucidSearchDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.domain.run.MsRun;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.RunFileFormat;
import org.yeastrc.ms.domain.run.ms2file.MS2Scan;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanCharge;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.SORT_BY;
import org.yeastrc.ms.domain.search.mascot.MascotSearchResult;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.parser.sqtFile.sequest.SequestResultPeptideBuilder;
import org.yeastrc.ms.util.AminoAcidUtils;
import org.yeastrc.www.misc.TableCell;
import org.yeastrc.www.misc.TableHeader;
import org.yeastrc.www.misc.TableRow;
import org.yeastrc.www.misc.Tabular;
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
            List<String> params = makeAppletParams(scanID, runSearchResultID, mapping, request);
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
        
        
        // get other results for this scan
        // we have the scanID and the runSearchResultID
        // get the results for this scanID from the search results file represented by the runSearchID
        setOtherResultsForScan(scanID, runSearchResultID, request);
        
        
        // viewSpectrum(129794, 1656821)
        return mapping.findForward("Success");
    
    }
    
    
    private void setOtherResultsForScan(int scanId, int runSearchResultId, HttpServletRequest request) {
        
        request.setAttribute("thisResult", runSearchResultId);
        MsSearchResultDAO resultDao = DAOFactory.instance().getMsSearchResultDAO();
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        
        MsSearchResult result = resultDao.load(runSearchResultId);
        
        int runSearchId = result.getRunSearchId();
        MsRunSearchDAO runSearchDao = DAOFactory.instance().getMsRunSearchDAO();
        MsRunSearch runSearch = runSearchDao.loadRunSearch(runSearchId);
        int searchId = runSearch.getSearchId();
        
        // get the resultIds
//        List<Integer> resultIds = resultDao.loadResultIdsForSearchScan(runSearchId, scanId);
        Set<Integer> resultIds = new HashSet<Integer>(resultDao.loadResultIdsForSearchScan(runSearchId, scanId));
        
        // get the search
        MsSearch search = searchDao.loadSearch(searchId);
        
        
        MsScanDAO scanDao = DAOFactory.instance().getMsScanDAO();
        
        // load the results for the appropriate program
        if(search.getSearchProgram() == Program.SEQUEST) {
            
            TabularSequestResults tabRes = new TabularSequestResults();
            SequestSearchDAO seqSearchDao = DAOFactory.instance().getSequestSearchDAO();
            String eValValue = seqSearchDao.getSearchParamValue(searchId, "print_expect_score");
            if(eValValue != null && eValValue.trim().equals("1"))
                tabRes.useEValue();
            
            SequestSearchResultDAO seqDao = DAOFactory.instance().getSequestResultDAO();
            
            for(int resultId: resultIds) {
                SequestSearchResult sres = seqDao.load(resultId);
                MsScan scan = scanDao.load(sres.getScanId());
                boolean highlight = runSearchResultId == sres.getId() ? true : false;
                tabRes.addResult(new SequestResultPlus(sres, scan), highlight);
            }
            if(resultIds.size() > 0) {
                request.setAttribute("results", tabRes);
            }
        }
        
        else if(search.getSearchProgram() == Program.MASCOT) {
            
            TabularMascotResults tabRes = new TabularMascotResults();
            MascotSearchResultDAO masResDao = DAOFactory.instance().getMascotResultDAO();
            
            for(int resultId: resultIds) {
                MascotSearchResult sres = masResDao.load(resultId);
                MsScan scan = scanDao.load(sres.getScanId());
                boolean highlight = runSearchResultId == sres.getId() ? true : false;
                tabRes.addResult(new MascotResultPlus(sres, scan), highlight);
            }
            if(resultIds.size() > 0) {
                request.setAttribute("results", tabRes);
            }
        }
        
        // TODO fix this 
//        else if (search.getSearchProgram() == Program.PROUCID) {
//            ProlucidSearchResultDAO plDao = DAOFactory.instance().getProlucidResultDAO();
//            List<ProlucidSearchResult> results = new ArrayList<ProlucidSearchResult>(resultIds.size());
//            for(int resultId: resultIds) {
//                ProlucidSearchResult pres = plDao.load(resultId);
//                results.add(pres);
//            }
//          if(results.size() > 0)
//              request.setAttribute("results", results);
//        }
    }
    
    private static class TabularSequestResults implements Tabular {

        
        private static SORT_BY[] columns = new SORT_BY[] {
            SORT_BY.MASS, 
            SORT_BY.CALC_MASS_SEQ,
            SORT_BY.CHARGE, 
            SORT_BY.RT, 
            SORT_BY.XCORR_RANK,
            SORT_BY.XCORR, 
            SORT_BY.DELTACN,
            SORT_BY.SP,
            SORT_BY.PEPTIDE
        };
        
        private int highlightedRow = -1;
        private boolean useEvalue = false;
        
        private final List<SequestResultPlus> results;
        
        public TabularSequestResults() {
            this.results = new ArrayList<SequestResultPlus>();
        }
        
        public void useEValue() {
            this.useEvalue = true;
            columns = new SORT_BY[] {
                    SORT_BY.MASS, 
                    SORT_BY.CALC_MASS_SEQ,
                    SORT_BY.CHARGE, 
                    SORT_BY.RT, 
                    SORT_BY.XCORR_RANK,
                    SORT_BY.XCORR, 
                    SORT_BY.DELTACN,
                    SORT_BY.EVAL,
                    SORT_BY.PEPTIDE
                };
        }
        
        public void addResult(SequestResultPlus result, boolean highlight) {
            if(highlight)
                highlightedRow = results.size();
            results.add(result);
        }
        @Override
        public int columnCount() {
            return columns.length;
        }
        @Override
        public TableRow getRow(int index) {
            if(index >= results.size())
                return null;
            SequestResultPlus result = results.get(index);
            TableRow row = new TableRow();
            
            row.addCell(new TableCell(String.valueOf(round(result.getObservedMass()))));
            row.addCell(new TableCell(String.valueOf(round(result.getSequestResultData().getCalculatedMass())), null));
            row.addCell(new TableCell(String.valueOf(result.getCharge())));
            
            // Retention time
            BigDecimal temp = result.getRetentionTime();
            if(temp == null) {
                row.addCell(new TableCell("", null));
            }
            else
                row.addCell(new TableCell(String.valueOf(round(temp)), null));
            
            row.addCell(new TableCell(String.valueOf(result.getSequestResultData().getxCorrRank()), null));
            row.addCell(new TableCell(String.valueOf(round(result.getSequestResultData().getxCorr())), null));
            row.addCell(new TableCell(String.valueOf(result.getSequestResultData().getDeltaCN()), null));
            if(!useEvalue)
                row.addCell(new TableCell(String.valueOf(round(result.getSequestResultData().getSp())), null));
            else {
                if(result.getSequestResultData().getEvalue() != null)
                    row.addCell(new TableCell(String.valueOf(round(result.getSequestResultData().getEvalue())), null));
                else
                    row.addCell(new TableCell("NULL", null));
            }
            
            String url = "viewSpectrum.do?scanID="+result.getScanId()+"&runSearchResultID="+result.getId();
            TableCell cell = new TableCell(String.valueOf(result.getResultPeptide().getFullModifiedPeptidePS()), url, true);
            cell.setTargetName("SPECTRUM_WINDOW");
            cell.setClassName("left_align");
            row.addCell(cell);
            
            if(highlightedRow == index)
                row.setRowHighighted(true);
            return row;
        }
        @Override
        public int rowCount() {
            return results.size();
        }
        @Override
        public List<TableHeader> tableHeaders() {
            List<TableHeader> headers = new ArrayList<TableHeader>(columns.length);
            for(SORT_BY col: columns) {
                TableHeader header = new TableHeader(col.getDisplayName(), col.name());
                headers.add(header);
            }
            return headers;
        }
        @Override
        public void tabulate() {
            // nothing to do here
        }
    }
    
    
    private static class TabularMascotResults implements Tabular {

        
        private static SORT_BY[] columns = new SORT_BY[] {
            SORT_BY.MASS, 
            SORT_BY.CALC_MASS_SEQ,
            SORT_BY.CHARGE, 
            SORT_BY.RT, 
            SORT_BY.MASCOT_RANK,
            SORT_BY.ION_SCORE, 
            SORT_BY.IDENTITY_SCORE,
            SORT_BY.HOMOLOGY_SCORE,
            SORT_BY.EXPECT,
            SORT_BY.PEPTIDE
        };
        
        private int highlightedRow = -1;
        
        private final List<MascotResultPlus> results;
        
        public TabularMascotResults() {
            this.results = new ArrayList<MascotResultPlus>();
        }
        
        public void addResult(MascotResultPlus result, boolean highlight) {
            if(highlight)
                highlightedRow = results.size();
            results.add(result);
        }
        @Override
        public int columnCount() {
            return columns.length;
        }
        @Override
        public TableRow getRow(int index) {
            if(index >= results.size())
                return null;
            MascotResultPlus result = results.get(index);
            TableRow row = new TableRow();
            
            row.addCell(new TableCell(String.valueOf(round(result.getObservedMass()))));
            row.addCell(new TableCell(String.valueOf(round(result.getMascotResultData().getCalculatedMass())), null));
            row.addCell(new TableCell(String.valueOf(result.getCharge())));
            
            // Retention time
            BigDecimal temp = result.getRetentionTime();
            if(temp == null) {
                row.addCell(new TableCell("", null));
            }
            else
                row.addCell(new TableCell(String.valueOf(round(temp)), null));
            
            row.addCell(new TableCell(String.valueOf(result.getMascotResultData().getRank()), null));
            row.addCell(new TableCell(String.valueOf(round(result.getMascotResultData().getIonScore())), null));
            row.addCell(new TableCell(String.valueOf(result.getMascotResultData().getIdentityScore()), null));
            row.addCell(new TableCell(String.valueOf(result.getMascotResultData().getHomologyScore()), null));
            row.addCell(new TableCell(String.valueOf(result.getMascotResultData().getExpect()), null));
            
            String url = "viewSpectrum.do?scanID="+result.getScanId()+"&runSearchResultID="+result.getId();
            TableCell cell = new TableCell(String.valueOf(result.getResultPeptide().getFullModifiedPeptidePS()), url, true);
            cell.setTargetName("SPECTRUM_WINDOW");
            cell.setClassName("left_align");
            row.addCell(cell);
            
            if(highlightedRow == index)
                row.setRowHighighted(true);
            return row;
        }
        @Override
        public int rowCount() {
            return results.size();
        }
        @Override
        public List<TableHeader> tableHeaders() {
            List<TableHeader> headers = new ArrayList<TableHeader>(columns.length);
            for(SORT_BY col: columns) {
                TableHeader header = new TableHeader(col.getDisplayName(), col.name());
                headers.add(header);
            }
            return headers;
        }
        @Override
        public void tabulate() {
            // nothing to do here
        }
    }

    private static double round(BigDecimal number) {
        return round(number.doubleValue());
    }
    private static double round(double num) {
        return Math.round(num*100.0)/100.0;
    }
    
    private List<String> makeAppletParams(int scanId, int runSearchResultId, ActionMapping mapping, HttpServletRequest request) 
        throws Exception {
        
        MsSearchResultDAO resultDao = DAOFactory.instance().getMsSearchResultDAO();
        MsSearchResult result = resultDao.load(runSearchResultId);
        int runSearchId = result.getRunSearchId();
        MsRunSearchDAO runSearchDao = DAOFactory.instance().getMsRunSearchDAO();
        MsRunSearch runSearch = runSearchDao.loadRunSearch(runSearchId);
        int searchId = runSearch.getSearchId();
        // get the search
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        MsSearch search = searchDao.loadSearch(searchId);
        
        List<MsResidueModification> dynaResMods = search.getDynamicResidueMods();
        if(dynaResMods.size() > 3) {
            throw new Exception("SpectrumApplet cannot handle > 3 variable modifications");
        }
        // TODO SpectrumApplet only works with Sequest style modifications. Need to rewrite it.
        if(search.getSearchProgram() != Program.SEQUEST) {
            char[] symbols = new char[] {'*', '#', '@'};
            int i = 0;
            for(MsResidueModification mod: dynaResMods) {
                mod.setModificationSymbol(symbols[i++]);
            }
        }
        
        String Sq = result.getResultPeptide().getModifiedPeptidePS();
        Sq = result.getResultPeptide().getPreResidue()+"."+Sq+"."+result.getResultPeptide().getPostResidue();
        request.setAttribute("peptideSeq", Sq);
        if(search.getSearchProgram() != Program.SEQUEST) {
            MsSearchResultPeptide seqStylePeptide = SequestResultPeptideBuilder.instance().build(
                    result.getResultPeptide().getPeptideSequence(), 
                    dynaResMods, null);
            Sq = seqStylePeptide.getFullModifiedPeptidePS();
        }
        
        MsRunDAO runDao = DAOFactory.instance().getMsRunDAO();
        String filename = runDao.loadFilenameForRun(runSearch.getRunId());

        MsScanDAO msScanDao = DAOFactory.instance().getMsScanDAO();
        MsScan scan = msScanDao.load(scanId);
        int scanNumber = scan.getStartScanNum();
        
        
        int charge = result.getCharge();
        BigDecimal massFrmScanCharge = null;
        // get the precursor mass for the given charge state for the MS2 tables if this was a MS2 run
        MsRun run = DAOFactory.instance().getMsRunDAO().loadRun(scan.getRunId());
        if(run.getRunFileFormat() == RunFileFormat.MS2 || run.getRunFileFormat() == RunFileFormat.CMS2) {
        
            // get the scan
          MS2Scan ms2scan = DAOFactory.instance().getMS2FileScanDAO().load(scanId);
            List<MS2ScanCharge> scChargeList = ms2scan.getScanChargeList();
            
            for (MS2ScanCharge sc: scChargeList) {
                if (sc.getCharge() == charge) {
                    massFrmScanCharge = sc.getMass();
                    break;
                }
            }
        }
        // If we did not find the mass, calculate it from what we have
        double mass = 0.0;
        if (massFrmScanCharge == null) {
            mass = (scan.getPrecursorMz().doubleValue() - AminoAcidUtils.HYDROGEN) * charge + AminoAcidUtils.HYDROGEN;
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
            //search.getSearchProgram() == Program.EE_NORM_SEQUEST ||
            search.getSearchProgram() == Program.PERCOLATOR) { // NOTE: Percolator in run on Sequest results so we will 
                                                               // look at the sequest parameters. 
            SequestSearchDAO seqSearchDao = DAOFactory.instance().getSequestSearchDAO();
            fragMassType = seqSearchDao.getFragmentMassType(searchId);
            parentMassType = seqSearchDao.getParentMassType(searchId);
        }
        else if(search.getSearchProgram() == Program.MASCOT) {
            MascotSearchDAO masSearchDao = DAOFactory.instance().getMascotSearchDAO();
            fragMassType = masSearchDao.getFragmentMassType(searchId);
            parentMassType = masSearchDao.getParentMassType(searchId);
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
        List<String[]> peakList = scan.getPeaksString();
        i = 0;
        for (String[] peak: peakList) {
            params.add("<PARAM NAME=\"MZ" + i + "\" VALUE=\"" + peak[0] + "\">");
            params.add("<PARAM NAME=\"Int" +(i++) + "\" VALUE=\"" + peak[1] + "\">");
        }
        
        return params;
    }
}
