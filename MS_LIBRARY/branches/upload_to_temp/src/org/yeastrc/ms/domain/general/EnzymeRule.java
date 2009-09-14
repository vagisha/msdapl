/**
 * EnzymeRule.java
 * @author Vagisha Sharma
 * Sep 13, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.general;

import org.yeastrc.ms.domain.general.MsEnzyme.Sense;

/**
 * 
 */
public class EnzymeRule {

    private Sense sense;
    private char[] cutResidues;
    private char[] noCutResidues;
    private int numEnzymaticTermini;
    
    public EnzymeRule(MsEnzyme enzyme, int numEnzymaticTermini) {
        this.sense = enzyme.getSense();
        cutResidues = enzyme.getCut().toCharArray();
        if(enzyme.getNocut() == null)
            noCutResidues = new char[0];
        else
            noCutResidues = enzyme.getNocut().toCharArray();
        
        this.numEnzymaticTermini = numEnzymaticTermini;
    }
    
    public boolean applyRule(String peptide, char nterm, char cterm) {
        
        if(numEnzymaticTermini == 0)
            return true;
        
        char cut_pre = 0;
        char cut_post = 0;
        char nocut_pre = 0;
        char nocut_post = 0;
        
        if(sense == Sense.CTERM) {
            cut_pre = nterm;
            cut_post = peptide.charAt(peptide.length() -1);
            nocut_pre = peptide.charAt(0);
            nocut_post = cterm;
        }
        else if(sense == Sense.NTERM) {
            cut_pre = peptide.charAt(0);
            cut_post = cterm;
            nocut_pre = nterm;
            nocut_post = peptide.charAt(peptide.length() -1);
        }
        
        if(numEnzymaticTermini == 2) {
            return (nterm == '-' ||  // beginning of protein sequence
                   (inCharArray(cutResidues, cut_pre) && !inCharArray(noCutResidues, nocut_pre)))
                    &&
                   (cterm == '-' || // end of protein sequence
                   (inCharArray(cutResidues, cut_post) && !inCharArray(noCutResidues, nocut_post)));
        }
        
        
        if(numEnzymaticTermini == 1) {
            return (inCharArray(cutResidues, cut_pre) && !inCharArray(noCutResidues, nocut_pre))
                    ||
                    (inCharArray(cutResidues, cut_post) && !inCharArray(noCutResidues, nocut_post));
        }
        
        return false;
    }
    
    private boolean inCharArray(char[] array, char myChar) {
        for(char c: array)
            if(c == myChar) 
                return true;
        return false;
    }
    
}
