package org.yeastrc.www.proteinfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import edu.uwpr.protinfer.database.dao.DAOFactory;
import edu.uwpr.protinfer.database.dao.ProteinferProteinDAO;
import edu.uwpr.protinfer.database.dto.ProteinferCluster;
import edu.uwpr.protinfer.database.dto.ProteinferPeptide;
import edu.uwpr.protinfer.database.dto.ProteinferProtein;

public class ProteinClusterAjaxAction extends Action{

    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {


        // User making this request
        User user = UserUtils.getUser(request);
        if (user == null) {
            response.getWriter().write("You are not logged in!");
            response.setStatus(HttpServletResponse.SC_SEE_OTHER); // Status code (303) indicating that the response to the request can be found under a different URI.
            return null;
//            ActionErrors errors = new ActionErrors();
//            errors.add("username", new ActionMessage("error.login.notloggedin"));
//            saveErrors( request, errors );
//            return mapping.findForward("authenticate");
            
        }

        int pinferId = 0;
        try {pinferId = Integer.parseInt(request.getParameter("pinferId"));}
        catch(NumberFormatException e) {}

        if(pinferId == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid Protein Inference ID: "+pinferId+"</b>");
            return null;
        }

        int clusterId = 0;
        try {clusterId = Integer.parseInt(request.getParameter("clusterId"));}
        catch(NumberFormatException e) {}

        if(clusterId == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid Protein Cluster ID: "+clusterId+"</b>");
            return null;
        }

        System.out.println("Got request for clusterId: "+clusterId+" of protein inference run: "+pinferId);

        request.setAttribute("pinferId", pinferId);
        request.setAttribute("clusterId", clusterId);
        ProteinferCluster cluster = ProteinferLoader.getProteinferCluster(pinferId, clusterId);
        request.setAttribute("cluster", cluster);
        
//        String html = getClusterHtml(pinferId, clusterId);
        // Go!
//        response.setContentType("text/html");
//        response.getWriter().write(html);
        return mapping.findForward("Success");
    }

    private String getClusterHtml(int pinferId, int clusterId) {

        StringBuilder html = new StringBuilder();
        ProteinferProteinDAO protDao = DAOFactory.instance().getProteinferProteinDao();
        List<ProteinferProtein> clusterProteins = protDao.getProteinferClusterProteins(pinferId, clusterId);

        html.append(proteinGroupsTable(clusterId, clusterProteins));
        html.append("<br>");
        html.append(peptideGroupsTable(clusterId, clusterProteins));
        html.append("<br>");
        html.append(proteinPeptideAssociationTable(clusterId, clusterProteins));

        return html.toString();
    }

    // PEPTIDE TABLE
    private String peptideGroupsTable(int clusterId, List<ProteinferProtein> clusterProteins) {
        
        Map<Integer, ProteinferPeptide> uniqPeptideMap = new HashMap<Integer, ProteinferPeptide>();
        for(ProteinferProtein prot: clusterProteins) {
            for(ProteinferPeptide pept: prot.getPeptides()) {
               uniqPeptideMap.put(pept.getId(), pept);
            }
        }
        
        List<ProteinferPeptide> uniqPeptList = new ArrayList<ProteinferPeptide>(uniqPeptideMap.values());
        Collections.sort(uniqPeptList, new Comparator<ProteinferPeptide>() {
            public int compare(ProteinferPeptide o1, ProteinferPeptide o2) {
                return Integer.valueOf(o1.getGroupId()).compareTo(o2.getGroupId());
            }});
        
        
        StringBuilder buf = new StringBuilder();
        
        buf.append("<div style=\"background-color: #3D902A; color: #EBFFE6; padding: 2px\"><b>Peptides in Cluster "+clusterId+"</b></div><br>\n");
        buf.append("<table cellpadding=\"4\" cellspacing=\"2\" align=\"center\" width=\"90%\" id=\"pept_grp_table_"+clusterId+"\">\n");
        
        buf.append("\t<tr>\n");
        buf.append("\t\t<th><b><font size=\"2pt\">Peptide<br>Group ID</font></b></th>\n");
        buf.append("\t\t<th><b><font size=\"2pt\">Sequence(s)</font></b></th>\n");
        buf.append("\t\t<th><b><font size=\"2pt\"># Spectra</font></b></th>\n");
        buf.append("\t\t<th><b><font size=\"2pt\">Best FDR</font></b></th>\n");
        buf.append("\t</tr>\n");
        
        
        int lastGrpId = -1;
        List<ProteinferPeptide> grpPeptides = new ArrayList<ProteinferPeptide>();
        for(ProteinferPeptide pept: uniqPeptList) {
            if(pept.getGroupId() != lastGrpId) {
                if(lastGrpId != -1) {
                    buf.append(groupPeptideRow(lastGrpId, grpPeptides));
                }
                grpPeptides.clear();
                lastGrpId = pept.getGroupId();
            }
            grpPeptides.add(pept);
        }

        if(lastGrpId != -1) {
            buf.append(groupPeptideRow(lastGrpId, grpPeptides));
        }

        buf.append("</table>\n");
        
        return buf.toString();
    }
    
   

    private String groupPeptideRow(int grpId, List<ProteinferPeptide> grpPeptides) {
        
        StringBuilder buf = new StringBuilder();
        
        boolean first = true;
        for(ProteinferPeptide pept: grpPeptides) {
            buf.append("\t<tr>\n");
            if(first) {
                first = false;
                buf.append("\t\t<td rowspan=\""+grpPeptides.size()+"\" id=\"peptGrp_"+grpId+"\">"+grpId+"</td>\n");
            }
//            buf.append("\t\t<td>"+pept.getGroupId()+"</td>\n");
            String modifiedSequence = ProteinferLoader.getModifiedSequenceForPeptide(pept);
            buf.append("\t\t<td>"+modifiedSequence+"</td>\n");
            buf.append("\t\t<td>"+pept.getSpectralCount()+"</td>\n");
            buf.append("\t\t<td>"+pept.getBestFdr()+"</td>\n");
            buf.append("\t</tr>\n");
        }
        return buf.toString();
    }
    

    // PROTEIN TABLE
    private String proteinGroupsTable(int clusterId, List<ProteinferProtein> clusterProteins) {

        // sort proteins by group id
        Collections.sort(clusterProteins, new Comparator<ProteinferProtein>() {
            public int compare(ProteinferProtein o1, ProteinferProtein o2) {
                return Integer.valueOf(o1.getGroupId()).compareTo(o2.getGroupId());
            }});

        StringBuilder buf = new StringBuilder();

        buf.append("<br><div style=\"background-color: #3D902A; color: #EBFFE6; padding: 2px; cursor: pointer\" onclick=\"toggleProteinList()\">\n");
        buf.append("\t<b>Proteins in  Cluster "+clusterId+"</b>\n");
        buf.append("</div><br>\n");

        buf.append("<table cellpadding=\"2\" cellspacing=\"2\" align=\"center\" width=\"90%\"  id=\"prot_grp_table_"+clusterId+"\">\n");

        buf.append("\t<tr>\n");
        buf.append("\t\t<th><b><font size=\"2pt\">Protein<br>Group ID</font></b></th>\n");
        buf.append("\t\t<th><b><font size=\"2pt\">Accession(s)</font></b></th>\n");
        buf.append("\t\t<th><b><font size=\"2pt\"># Peptides<br>(Unique)</font></b></th>\n");
        buf.append("\t\t<th><b><font size=\"2pt\"># Spectra</font></b></th>\n");
        buf.append("\t</tr>\n");

        List<ProteinferProtein> groupProteins = new ArrayList<ProteinferProtein>();
        int lastGrpId = -1;
        for(ProteinferProtein prot: clusterProteins) {
            if(prot.getGroupId() != lastGrpId) {
                if(lastGrpId != -1) {
                    buf.append(groupProteinRow(lastGrpId, groupProteins, getUniquePeptideCountToProteinGroup(groupProteins)));
                }
                groupProteins.clear();
                lastGrpId = prot.getGroupId();
            }
            groupProteins.add(prot);
        }

        if(lastGrpId != -1) {
            buf.append(groupProteinRow(lastGrpId, groupProteins, getUniquePeptideCountToProteinGroup(groupProteins)));
        }

        buf.append("</table>\n");

        return buf.toString();
    }

    private int getUniquePeptideCountToProteinGroup(List<ProteinferProtein> groupProteins) {
        
        Set<Integer> grpProtids = new HashSet<Integer>(groupProteins.size());
        for(ProteinferProtein prot: groupProteins) {
            grpProtids.add(prot.getNrseqProteinId());
        }
        
        int uniqueCnt = 0;
        for(ProteinferProtein prot: groupProteins) {
            for(ProteinferPeptide pept: prot.getPeptides()) {
                boolean uniq = true;
                for(Integer protId: pept.getMatchingProteinIds()) {
                    if(!grpProtids.contains(protId)) {
                        uniq = false;
                        break;
                    }
                }
                if(uniq) uniqueCnt++;
            }
        }
        return uniqueCnt;
    }
    
    private String groupProteinRow(int grpId, List<ProteinferProtein> groupProteins, int uniqueGrpPeptideCnt) {

        String matchingPeptGrpIds = "";
        String matchingUniquePeptGrpIds = "";

        Set<String> peptGrpIds = new HashSet<String>();
        Set<String> uniqPeptGrpIds = new HashSet<String>();

        ProteinferProtein reprProt = groupProteins.get(0);
        for(ProteinferPeptide pept: reprProt.getPeptides()) {
            if(pept.isUniqueToProtein())
                uniqPeptGrpIds.add(String.valueOf(pept.getGroupId()));
            else
                peptGrpIds.add(String.valueOf(pept.getGroupId()));
        }

        for(String pgrpId: peptGrpIds)   matchingPeptGrpIds += ","+pgrpId;
        if(matchingPeptGrpIds.length() > 0) matchingPeptGrpIds = matchingPeptGrpIds.substring(1);

        for(String pgrpId: uniqPeptGrpIds)   matchingUniquePeptGrpIds += ","+pgrpId;
        if(matchingUniquePeptGrpIds.length() > 0) matchingUniquePeptGrpIds = matchingUniquePeptGrpIds.substring(1);


        StringBuilder buf = new StringBuilder();

        buf.append("\t<tr id=\"protGrp_"+grpId+"\">\n");

        // group id cell
        buf.append("\t\t<td valign=\"middle\">\n");
        buf.append("\t\t\t<span onclick=\"highlightProteinAndPeptides('"+grpId+"', '"+matchingPeptGrpIds+"', '"+matchingUniquePeptGrpIds+"')\"\n");
        buf.append("\t\t\t\tstyle=\"cursor:pointer;text-decoration:underline\">"+grpId);
        buf.append("\t\t\t</span>\n");
        buf.append("\t\t</td>\n");

        // all proteins in this group
        buf.append("\t\t<td>\n");
        for(ProteinferProtein prot: groupProteins) {
            if(prot.getIsParsimonious())    buf.append("\t\t\t<b>\n");
            buf.append("\t\t\t<div onclick=\"showProteinDetails("+prot.getNrseqProteinId()+")\"\n");
            buf.append("\t\t\t\tstyle=\"text-decoration: underline; cursor: pointer\">\n");
            buf.append("\t\t\t\t"+prot.getAccession()+"\n");
            buf.append("\t\t\t</div>\n");
            if(prot.getIsParsimonious())    buf.append("\t\t\t</b>\n");
        }
        buf.append("\t\t</td>\n");

        // peptide count cell.
        buf.append("\t\t<td>"+reprProt.getPeptideCount()+"("+uniqueGrpPeptideCnt+")</td>\n");

        // spectral count cell
        buf.append("\t\t<td>"+reprProt.getSpectralCount()+"</td>\n");

        buf.append("\t</tr>\n");

        return buf.toString();
    }

    // PEPTIDE-PROTEIN ASSOCIATION TABLE
    private String proteinPeptideAssociationTable(int clusterId,  List<ProteinferProtein> clusterProteins) {
        StringBuilder buf = new StringBuilder();
        Map<Integer, ProteinferProtein> protGrpRepr = new HashMap<Integer, ProteinferProtein>();
        Map<Integer, ProteinferPeptide> peptGrpRepr = new HashMap<Integer, ProteinferPeptide>();
        for(ProteinferProtein prot: clusterProteins) {
            if(protGrpRepr.get(prot.getGroupId()) == null) 
                protGrpRepr.put(prot.getGroupId(), prot);

            for(ProteinferPeptide pept: prot.getPeptides()) {
                if(peptGrpRepr.get(pept.getGroupId()) == null)
                    peptGrpRepr.put(pept.getGroupId(), pept);
            }
        }

        // if we found only one protein group we don't need to create an association table
        if(protGrpRepr.size() < 2)
            return "";

        buf.append("<br><div style=\"background-color: #3D902A; color: #EBFFE6; padding: 2px\" ><b>Protein - Peptide Association</b></div><br>\n");
        buf.append("\t<table id=\"assoctable_"+clusterId+
        "\" cellpadding=\"4\" cellspacing=\"2\" align=\"center\" >\n");



        List<Integer> protGrpIds = new ArrayList<Integer>(protGrpRepr.keySet());
        Collections.sort(protGrpIds);

        List<Integer> peptGrpIds = new ArrayList<Integer>(peptGrpRepr.keySet());
        Collections.sort(peptGrpIds);

        buf.append("\t\t<tr>\n");
        buf.append("\t\t\t<th><b><font size=\"2pt\">Group ID <br>(Peptide / Protein)</font></b></th>\n");
        for(Integer protGrpId: protGrpIds) {
            buf.append("\t\t\t<th><b><font size=\"2pt\">"+protGrpId+"</font></b></th>\n");
        }
        buf.append("\t\t</tr>\n");

        for(Integer peptGrpId: peptGrpIds) {
            buf.append("\t\t<tr>\n");
            buf.append("\t\t\t<th><b><font size=\"2pt\">"+peptGrpId+"</font></b></th>\n");
            for(Integer protGrpId: protGrpIds) {
                buf.append("\t\t\t<td id=\"peptEvFor_"+protGrpId+"_"+peptGrpId+"\">");
                if(protGrpRepr.get(protGrpId).matchesPeptideGroup(peptGrpId)) {
                    buf.append("x");
                }
                else {
                    buf.append("&nbsp;");
                }
                buf.append("</td>\n");
            }
            buf.append("\t\t</tr>\n");
        }

        buf.append("\t</table>\n");
        return buf.toString();
    }


}
