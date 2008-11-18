package org.yeastrc.www.proteinfer;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import edu.uwpr.protinfer.database.dao.DAOFactory;
import edu.uwpr.protinfer.database.dao.ProteinferProteinDAO;
import edu.uwpr.protinfer.database.dto.ProteinferPeptide;
import edu.uwpr.protinfer.database.dto.ProteinferProtein;
import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;

public class ProteinDetailsAjaxAction extends Action {

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
        try {pinferId = Integer.parseInt(request.getParameter("pinferId"));}
        catch(NumberFormatException e) {}

        if(pinferId == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid Protein Inference ID: "+pinferId+"</b>");
            return null;
        }

        int nrseqProtId = 0;
        try {nrseqProtId = Integer.parseInt(request.getParameter("nrseqProtId"));}
        catch(NumberFormatException e) {}

        if(nrseqProtId == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid Protein Cluster ID: "+nrseqProtId+"</b>");
            return null;
        }

        System.out.println("Got request for nrseq protein ID: "+nrseqProtId+" of protein inference run: "+pinferId);

        String html = getProteinDetailsHtml(pinferId, nrseqProtId);
        // Go!
        response.setContentType("text/html");
        response.getWriter().write(html);
        return null;
    }

    private String getProteinDetailsHtml(int pinferId, int nrseqProtId) {
        
        ProteinferProtein protein = getProtein(pinferId, nrseqProtId);
        
        StringBuilder buf = new StringBuilder();
        
        buf.append("<button onclick=\"toggleProteinSequence("+nrseqProtId+", '"+getPeptidesForProtein(protein)+"')\""+
                        " id=\"protseqbutton_"+nrseqProtId+"\">View Protein Sequence</button><br>\n");
        
        buf.append("<table  align=\"center\" width=\"90%\">"+
                    "\n\t<tr><td style=\"background-color: #D4FECA;\" id=\"protsequence_"+nrseqProtId+"\"></td></tr>"+
                    "\n</table>\n");
        
        buf.append("<br><br>\n");
        buf.append("<table width=\"95%\" id=\"protdetailstbl_"+nrseqProtId+"\">\n");
        buf.append("<tr>\n");
        buf.append("<th width=\"10%\" align=\"left\"><b><font size=\"2pt\">Group ID</font></b></th>\n");
        buf.append("<th align=\"left\"><b><font size=\"2pt\">Sequence</font></b></th>\n");
        buf.append("<th width=\"10%\" align=\"left\"><b><font size=\"2pt\"># Spectra</font></b></th>\n");
        buf.append("<th width=\"10%\" align=\"left\"><b><font size=\"2pt\">Best FDR</font></b></th>\n");
        buf.append("<th width=\"10%\" align=\"left\"><b><font size=\"2pt\">Unique</b></th>\n");
        buf.append("</tr>\n");
        
        List<ProteinferPeptide> peptList = protein.getPeptides();
        Collections.sort(peptList, new Comparator<ProteinferPeptide>() {
            public int compare(ProteinferPeptide o1, ProteinferPeptide o2) {
                return Integer.valueOf(o1.getGroupId()).compareTo(o2.getGroupId());
            }});
        
        for(ProteinferPeptide pept: peptList) {
            buf.append("<tr>\n");
            buf.append("<td>"+pept.getGroupId()+"</td>\n");
            buf.append("<td><b>"+ProteinferLoader.getModifiedSequenceForPeptide(pept)+"</b></td>\n");
            buf.append("<td>"+pept.getSpectralCount()+"</td>\n");
            buf.append("<td>"+pept.getBestFdr()+"</td>\n");
            buf.append("<td>"+pept.isUniqueToProtein()+"</td>\n");
            buf.append("</tr>\n");
            
            buf.append(spectrumMatchesForPeptide(pept));
        }
        
        buf.append("</table>\n");
        return buf.toString();
    }
    
    
    private Object spectrumMatchesForPeptide(ProteinferPeptide pept) {
        StringBuilder buf = new StringBuilder();
        
        buf.append("<tr><td colspan=\"5\">\n");
        
        buf.append("<table align=\"center\" width=\"70%\"\n");
        buf.append("style=\"border: 1px dashed gray; border-spacing: 4px; margin-top: 6px; margin-bottom: 6px;\" >\n");
        buf.append("<tr>\n");
        buf.append("<td style=\"text-decoration: underline;\">Scan Number</td>\n");
        buf.append("<td style=\"text-decoration: underline;\">Charge</td>\n");
        buf.append("<td style=\"text-decoration: underline;\">XCorr</td>\n");
        buf.append("<td style=\"text-decoration: underline;\">DeltaCN</td>\n");
        buf.append("<td style=\"text-decoration: underline;\">FDR</td>\n");
        buf.append("<td style=\"text-decoration: underline;\">Spectrum</td>\n");
        buf.append("</tr>\n");
        
        org.yeastrc.ms.dao.DAOFactory fact = org.yeastrc.ms.dao.DAOFactory.instance();
        SequestSearchResultDAO resDao = fact.getSequestResultDAO();
        MsScanDAO scanDao = fact.getMsScanDAO();
        
        for(ProteinferSpectrumMatch psm: pept.getSpectrumMatchList()) {
            
            int runSearchResultId = psm.getMsRunSearchResultId();
            SequestSearchResult res = resDao.load(runSearchResultId);
            int scanNum = scanDao.load(res.getScanId()).getStartScanNum();
            
            buf.append("<tr>\n");
            buf.append("<td>"+scanNum+"</td>\n");
            buf.append("<td>"+res.getCharge()+"</td>\n");
            double xcorr = Math.round(res.getSequestResultData().getxCorr().doubleValue() * 1000.0) / 1000.0;
            buf.append("<td>"+xcorr+"</td>\n");
            double deltacn = Math.round(res.getSequestResultData().getDeltaCN().doubleValue() * 1000.0) / 1000.0;
            buf.append("<td>"+deltacn+"</td>\n");
            buf.append("<td>"+psm.getFdrRounded()+"</td>\n");
            buf.append("<td><span style=\"text-decoration: underline; cursor: pointer;\" "+
                    "onclick=\"viewSpectrum("+res.getScanId()+", "+runSearchResultId+")\">View</span></td>\n");
            buf.append("</tr>\n");
        }
        
        buf.append("</table>\n");
        buf.append("</td></tr>\n");
        
        return buf.toString();
    }

    private ProteinferProtein getProtein(int pinferId, int nrseqProtId) {
        DAOFactory fact = DAOFactory.instance();
        ProteinferProteinDAO protDao = fact.getProteinferProteinDao();
        return protDao.getProteinferProtein(pinferId, nrseqProtId);
    }
    
    private String getPeptidesForProtein(ProteinferProtein prot) {
        StringBuilder buf = new StringBuilder();
        for(ProteinferPeptide pept: prot.getPeptides()) {
            buf.append(","+ProteinferLoader.getModifiedSequenceForPeptide(pept));
        }
        if(buf.length() > 0)
            buf.deleteCharAt(0);
        return buf.toString();
    }
}
