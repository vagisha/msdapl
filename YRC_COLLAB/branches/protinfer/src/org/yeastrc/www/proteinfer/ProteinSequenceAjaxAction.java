package org.yeastrc.www.proteinfer;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.domain.nrseq.NrDbProtein;
import org.yeastrc.nr_seq.NRProtein;
import org.yeastrc.nr_seq.NRProteinFactory;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

public class ProteinSequenceAjaxAction extends Action {

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
        
        int nrseqProtId = 0;
        try {nrseqProtId = Integer.parseInt(request.getParameter("nrseqid"));}
        catch(NumberFormatException e) {}

        if(nrseqProtId == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid protein ID: "+nrseqProtId+"</b>");
            return null;
        }

//        String peptideList = request.getParameter("peptides");
//        String[] peptides = peptideList.split(",");
        List<String> peptideList = ProteinferLoader.getUnmodifiedPeptidesForProtein(pinferId, nrseqProtId);
        String[] peptides = new String[peptideList.size()];
        peptides = peptideList.toArray(peptides);

        System.out.println("Got request for accession: "+nrseqProtId);
        System.out.println("Peptides are: "+peptideList);

        String html = getHtmlForProtein(nrseqProtId, peptides);
        // Go!
        response.setContentType("text/html");
        response.getWriter().write("<pre>"+html+"</pre>");
        return null;
    }

    private String getHtmlForProtein(int nrseqid, String[] peptides) {
        
        NrDbProtein dbProt = NrSeqLookupUtil.getDbProtein(nrseqid);
        if(dbProt == null) {
            return "<b>Could not find protein with ID: "+nrseqid+"</b>";
        }
        NRProteinFactory nrpf = NRProteinFactory.getInstance();
        NRProtein protein = null;
        try {
            protein = (NRProtein)(nrpf.getProtein(dbProt.getProteinId()));
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        if(protein == null) {
            return "<b>Could not find protein with ID: "+nrseqid+"</b>";
        }

        String parentSequence = protein.getPeptide().getSequenceString();

        char[] reschars = parentSequence.toCharArray();
        String[] residues = new String[reschars.length];        // the array of strings, which are the residues of the matched protein
        for (int i = 0; i < reschars.length; i++) { residues[i] = String.valueOf(reschars[i]); }
        reschars = null;

        // structure of these maps is: Integer=>Integer (Residue index (0..residues.length))=>(number of peptides marking that residue thusly)
        Map<Integer, Integer> starResidues = new HashMap<Integer, Integer>();       // residues marked with a *
        Map<Integer, Integer> atResidues = new HashMap<Integer, Integer>();         // residues marked with a @
        Map<Integer, Integer> hashResidues = new HashMap<Integer, Integer>();       // residues marked with a #

        for( String peptideSequence : peptides ) {

            if (peptideSequence == null) continue;          
            int pepIndex = parentSequence.indexOf( peptideSequence );

            // skip this peptide if it's not in the parent protein sequence
            if (pepIndex == -1)
                continue;

//            if (ypep.getSequence().indexOf( "*" ) != -1 || ypep.getSequence().indexOf("@") != -1 || ypep.getSequence().indexOf("#") != -1) {
//                char[] aas = ypep.getSequence().toCharArray();
//                int modCount = 0;
//                for (int k = 3; k < aas.length; k++) {
//                    Integer residueIndex = new Integer( pepIndex + k - 3 - modCount );
//                    Map<Integer, Integer> countMap = null;
//
//                    if (aas[k] == '*') {
//                        countMap = starResidues;
//                        modCount++;
//                    } else if (aas[k] == '@') {                     
//                        countMap = atResidues;
//                        modCount++;
//                    } else if (aas[k] == '#') {                     
//                        countMap = hashResidues;
//                        modCount++;
//                    } else {
//                        continue;
//                    }
//
//                    if (!countMap.containsKey( residueIndex ))
//                        countMap.put( residueIndex, new Integer( 1 ) );
//                    else {
//                        int count = ((Integer)countMap.get( residueIndex)).intValue();
//                        countMap.remove( residueIndex );
//                        countMap.put( residueIndex, new Integer( count + 1 ) );
//                    }                   
//                }
//            }
        }

        /* 
         * at this point, the 3 residues maps should contain a count of the number of peptides reporting the
         * respective modifications for each reportedly modified residues.
         * Go through and label each of those with styled <SPAN> tags for labelling
         * 
         */
        for ( int index : starResidues.keySet() ) {
            int count = starResidues.get( index );

            if (count == 1)
                residues[index] = "<span class=\"single_star_residue\">" + residues[index] + "</span>";
            else
                residues[index] = "<span class=\"multiple_star_residue\">" + residues[index] + "</span>";
        }


        for ( int index : atResidues.keySet() ) {
            int count = atResidues.get( index );

            if (count == 1)
                residues[index] = "<span class=\"single_at_residue\">" + residues[index] + "</span>";
            else
                residues[index] = "<span class=\"multiple_at_residue\">" + residues[index] + "</span>";
        }


        for ( int index : hashResidues.keySet() ) {
            int count = hashResidues.get( index );

            if (count == 1)
                residues[index] = "<span class=\"single_hash_residue\">" + residues[index] + "</span>";
            else
                residues[index] = "<span class=\"multiple_hash_residue\">" + residues[index] + "</span>";
        }   

        /*
         * All modified residues in the residues array should be surrounded by appropriately classed <span> tags
         */

        // clean up
        starResidues = null;
        atResidues = null;
        hashResidues = null;

        /*
         * Now add in font tags for labelling covered sequences in the parent sequence
         */

        for ( String pseq : peptides ) {
            if (pseq == null) continue;

            int index = parentSequence.indexOf(pseq);
            if (index == -1) continue;                  //shouldn't happen
            if (index > residues.length - 1) continue;  //shouldn't happen

            // Place a red font start at beginning of this sub sequence in main sequence
            residues[index] = "<span class=\"covered_peptide\">" + residues[index];

            // this means that the sub-peptide extends beyond the main peptide's sequence... shouldn't happen but check for it
            if (index + pseq.length() > residues.length - 1) {

                // just stop the red font at the end of the main sequence string
                residues[residues.length - 1] = residues[residues.length - 1] + "</span>";
            } else {

                // add the font end tag after the last residue in the sub sequence
                residues[index + pseq.length() - 1] = residues[index + pseq.length() - 1] + "</span>";
            }
        }


        // String array should be set up appropriately with red font tags for sub peptide overlaps, format it into a displayable peptide sequence
        String retStr = "      1          11         21         31         41         51         \n";
        retStr +=       "      |          |          |          |          |          |          \n";
        retStr +=       "    1 ";

        int counter = 0;

        // retStr += "RESIDUE 0: [" + residues[0] + "]";

        for (int i = 0; i < residues.length; i++ ) {
            retStr += residues[i];

            counter++;
            if (counter % 60 == 0) {
                if (counter < 1000) retStr += " ";
                if (counter< 100) retStr += " ";

                retStr += "<font style=\"color:black;\">" + String.valueOf(counter) + "</font>";
                retStr += "\n ";

                if (counter < 100) retStr += " ";
                if (counter < 1000) retStr += " ";
                retStr += "<font style=\"color:black;\">" + String.valueOf(counter + 1) + "</font> ";

            } else if (counter % 10 == 0) {
                retStr += " ";
            }

        }

        return retStr;
    }

}
