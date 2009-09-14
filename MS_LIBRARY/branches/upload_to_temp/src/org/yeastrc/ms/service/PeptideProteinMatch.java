/**
 * PeptideProteinMatch.java
 * @author Vagisha Sharma
 * Sep 11, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service;

import org.yeastrc.ms.domain.nrseq.NrDbProtein;

/**
 * 
 */
public class PeptideProteinMatch {

    private String peptide;
    private char preResidue;
    private char postResidue;
    private NrDbProtein protein;
    
    public String getPeptide() {
        return peptide;
    }
    public void setPeptide(String peptide) {
        this.peptide = peptide;
    }
    public char getPreResidue() {
        return preResidue;
    }
    public void setPreResidue(char preResidue) {
        this.preResidue = preResidue;
    }
    public char getPostResidue() {
        return postResidue;
    }
    public void setPostResidue(char postResidue) {
        this.postResidue = postResidue;
    }
    public NrDbProtein getProtein() {
        return protein;
    }
    public void setProtein(NrDbProtein protein) {
        this.protein = protein;
    }
   
}
