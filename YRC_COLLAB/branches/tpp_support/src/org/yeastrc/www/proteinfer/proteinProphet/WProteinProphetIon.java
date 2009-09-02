/**
 * WProteinProphetIon.java
 * @author Vagisha Sharma
 * Aug 14, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.proteinProphet;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetProteinPeptideIon;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;

/**
 * 
 */
public class WProteinProphetIon {

    private ProteinProphetProteinPeptideIon ion;
    private MsSearchResult bestSpectrumMatch;
    private boolean uniqueToProteinGrp = false;
    
    private List<Character> ntermResidues = new ArrayList<Character>();
    private List<Character> cTermResidues = new ArrayList<Character>();
    
    public WProteinProphetIon(ProteinProphetProteinPeptideIon ion, MsSearchResult psm) {
        this.ion = ion;
        this.bestSpectrumMatch = psm;
    }

    public void addTerminalResidues(char nterm, char cterm) {
        this.ntermResidues.add(nterm);
        this.cTermResidues.add(cterm);
    }
    
    public int getScanId() {
        return bestSpectrumMatch.getScanId();
    }

    public ProteinProphetProteinPeptideIon getIon() {
        return ion;
    }
    
    public MsSearchResult getBestSpectrumMatch() {
        return bestSpectrumMatch;
    }

    public boolean getIsUniqueToProteinGroup() {
        return uniqueToProteinGrp;
    }
    
    public void setIsUniqueToProteinGroup(boolean isUnique) {
        this.uniqueToProteinGrp = isUnique;
    }
    
    public String getIonSequence() {
        
        if(getBestSpectrumMatch() == null)
            return null;
        String seq;
        try {
            seq = removeTerminalResidues(getBestSpectrumMatch().getResultPeptide().getModifiedPeptide());
        }
        catch (ModifiedSequenceBuilderException e) {
            return null;
        }
        if(ntermResidues.size() == 0 & cTermResidues.size() == 0)
            return seq;
        
        seq = "."+seq+".";
        for(int i = 0; i < ntermResidues.size(); i++) {
            seq = "("+ntermResidues.get(i)+")"+seq+"("+cTermResidues.get(i)+")";
        }
        return seq;
    }

//    public String getIonSequence() {
//        return removeTerminalResidues(bestSpectrumMatch.getResultPeptide().getModifiedPeptide());
//    }
    
    public int getCharge() {
        return ion.getCharge();
    }
    
    public int getSpectrumCount() {
        return ion.getSpectrumCount();
    }
    
    protected static String removeTerminalResidues(String peptide) {
        int f = peptide.indexOf('.');
        int l = peptide.lastIndexOf('.');
        f = f == -1 ? 0 : f+1;
        l = l == -1 ? peptide.length() : l;
        return peptide.substring(f, l);
    }
}
