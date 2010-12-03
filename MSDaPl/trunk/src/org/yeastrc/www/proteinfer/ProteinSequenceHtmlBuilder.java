/**
 * ProteinSequenceHtmlBuilder.java
 * @author Vagisha Sharma
 * Feb 3, 2010
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 */
public class ProteinSequenceHtmlBuilder {

    private static ProteinSequenceHtmlBuilder instance = null;
    
    private ProteinSequenceHtmlBuilder() {}
    
    public static ProteinSequenceHtmlBuilder getInstance() {
        if(instance == null) {
            instance = new ProteinSequenceHtmlBuilder();
        }
        return instance;
    }
    
    public String build(String sequence, Set<String> peptideSequences) throws ProteinSequenceHtmlBuilderException {
        
        char[] reschars = sequence.toCharArray();
        String[] residues = new String[reschars.length];        // the array of strings, which are the residues of the matched protein
        for (int i = 0; i < reschars.length; i++) { residues[i] = String.valueOf(reschars[i]); }
        reschars = null;

        // structure of these maps is: Integer=>Integer (Residue index (0..residues.length))=>(number of peptides marking that residue thusly)
        Map<Integer, Integer> starResidues = new HashMap<Integer, Integer>();       // residues marked with a *
        Map<Integer, Integer> atResidues = new HashMap<Integer, Integer>();         // residues marked with a @
        Map<Integer, Integer> hashResidues = new HashMap<Integer, Integer>();       // residues marked with a #

        // remove '*' characters from the protein sequence
        sequence = sequence.replaceAll("\\*", "");
        
        String altSequence = sequence;
        // Replace all 'L' with '1'
        altSequence = altSequence.replaceAll("L", "1");
        // Replace all "I" with "1"
        altSequence = altSequence.replaceAll("I", "1");
        
        //  Add in font tags for labelling covered sequences in the parent sequence
        for( String peptideSequence : peptideSequences ) {

            if (peptideSequence == null || peptideSequence.length() == 0) continue;   
            
            String altPeptide = peptideSequence;
        	altPeptide = altPeptide.replaceAll("L", "1");
        	altPeptide = altPeptide.replaceAll("I", "1");
        	
        	int lastMatchIndex = 0;
        	
        	while(lastMatchIndex <= sequence.length() - peptideSequence.length()) {
        		
        		int pepIndex = altSequence.indexOf(altPeptide, lastMatchIndex);
        		
        		if(lastMatchIndex == 0 && pepIndex == -1) 
        			throw new ProteinSequenceHtmlBuilderException("Peptide "+peptideSequence+" not found in protein!");
        		
        		if(pepIndex == -1)
        			break;
        		
        		lastMatchIndex = pepIndex + altPeptide.length();
            

        		if (pepIndex > residues.length - 1)  { //shouldn't happen
        			throw new ProteinSequenceHtmlBuilderException("Matching index out of bounds for peptide: "+peptideSequence);
        		}

        		// Place a red font start at beginning of this sub sequence in main sequence
        		residues[pepIndex] = "<span class=\"covered_peptide\">" + residues[pepIndex];

        		// this means that the sub-peptide extends beyond the main peptide's sequence... shouldn't happen but check for it
        		if (pepIndex + peptideSequence.length() > residues.length) {
        			throw new ProteinSequenceHtmlBuilderException("Peptide ("+peptideSequence+") match extends beyond length of protein");
        			// just stop the red font at the end of the main sequence string
        			//residues[residues.length - 1] = residues[residues.length - 1] + "</span>";
        		} else {

        			// add the font end tag after the last residue in the sub sequence
        			residues[pepIndex + peptideSequence.length() - 1] = residues[pepIndex + peptideSequence.length() - 1] + "</span>";
        		}
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
    
    public static void main(String[] args) throws ProteinSequenceHtmlBuilderException {
    	String protein = "WNLASSRVPFLFRVL*SA*R*SRHQKSSAVPMGRLRAVR*IVSVLCSSAKPSSPAACRRGSCRSLTATTPCPLFQPTS*KILLPGCLMENRVHTTPNCVLSPRKRKGLGHHLTLPLSKMALQKLS*RSPKRT*TGSLSLNLPFVVTVLLMCLSQKKLDKNQNTKQEGTGRGA*NMTPCWSRALSKQLLCLRSRVLQIKRFQLRKSLVQTLEETKTTC*NTMSVIWCGPKCRVTLGGLAWFLQIHSFTAIPNLKVRKRVHASITYSSLVTPQKELGYLRRAS*LLKEKDSLKNYARKVPSRHPRKLRKLSY*NLFQGN*GPSGKWALFKQKKLQACQWRSGKPSSPFYTWGTSFISTLK*PRRLALLQSLSEKWQSPQESVKKLLKTPSLREKRAFP*REGGGPNCVALQRPWRVTPM**RVLLKRRQRLTPEEE*RLLLGGRRPQPLRHEAGREMQHPSFWSSVKNTGMRWWLSTQMLQVRRLKSCSGHSGVC*VRSRERATTPSLPWWPLSRLKKTLVT*MGKKETTQRGHRTLQKMLRLRTHPGKDSGQTSTVFGXXXXXXXXXXEQALTRPWRQPPRSRARQQRKICLMHANH*RSEIGLPRQHLQLLGLAKVHLLLHP*LRMRSRTARETSPRSPHMKVQMKHKLKCLSHPKSLSEELLPKRSTCASCVRSQAASCSVKDPAAELSTSPALGFPGGQKGGSPAASVPQGFTHVSCVKRARQMLSAVW*LSVENFTMRLV*KNTL*LYLRAEVSAAPFTAV*AATLPTLQTQGHQKVK*CGVSAAPLPITAGMLVWQQDVQ*SPPTASSALPTSLLGRGSDTTPTST*AGALCAPKGGAFCAASPAQRPSTLTA*TSRCLMAAGSATTAGLGRSCTSRISFG*NLGTTDGGRQKFAIPKMFPQIFRK*STRLENSLCFSLGLKIITGRIRRECSRTWRGTGAAATRGSEGSEESSKTHCKKLKLVFVKLSFRGKPEKHRRASASPHHTSTSR*ISLTGKSRSTQRIFQKSLSATASPQMRILVALIRSV*TGC*CLSATRRCVPQASSARTSASPSASTQRPRSSRQMAKGGAWSPRGISERENLLTSTSGS*SMRRSAWRESSTHMRTTSPTSTCSL*TXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXPQRPFLQRKRAXXXXXXXXXXXXXXXXXXSQRTSASAAAMAGSWCCVTASSAPRPTTCPAWALASGPSGSGNVLGIIVTCVANLQLHFATSAPIHSVRSTRTGQPSAAPRTGRSYCCEHDLGAASVRSAKTEKPPPEPGKPKGKRRRRRGWRRVTEGK";
    	Set<String> peptides = new HashSet<String>();
    	peptides.add("VKCGVSAAPLPITAGMLVWQQ");
    	
    	ProteinSequenceHtmlBuilder builder = ProteinSequenceHtmlBuilder.getInstance();
    	String formatted = builder.build(protein, peptides);
    	System.out.println(formatted);
    }
}
