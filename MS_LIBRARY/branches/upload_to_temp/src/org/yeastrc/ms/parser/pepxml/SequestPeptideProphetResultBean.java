/**
 * SequestPeptideProphetResultBean.java
 * @author Vagisha Sharma
 * Aug 3, 2009
 * @version 1.0
 */
package org.yeastrc.ms.parser.pepxml;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.analysis.peptideProphet.SequestPeptideProphetResultIn;
import org.yeastrc.ms.domain.analysis.peptideProphet.impl.PeptideProphetResultBean;
import org.yeastrc.ms.domain.protinfer.proteinProphet.Modification;
import org.yeastrc.ms.domain.search.MsModification;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.domain.search.ValidationStatus;
import org.yeastrc.ms.domain.search.impl.ResultResidueModBean;
import org.yeastrc.ms.domain.search.impl.SearchResultPeptideBean;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.parser.sqtFile.sequest.SequestResult;
import org.yeastrc.ms.util.AminoAcidUtils;

/**
 * 
 */
public class SequestPeptideProphetResultBean implements
        SequestPeptideProphetResultIn {

    private PeptideProphetResultBean ppRes;
    private SequestResult seqRes;
    private List<Modification> dynamicModList = new ArrayList<Modification>();
    private SearchResultPeptideBean peptide  = null;
    private String strippedSequence;
    private char preResidue = MsModification.EMPTY_CHAR;
    private char postResidue = MsModification.EMPTY_CHAR;
    
    
    @Override
    public MsSearchResultPeptide getResultPeptide() {
        if(peptide != null)
            return peptide;
        peptide = new SearchResultPeptideBean();
        peptide.setPeptideSequence(strippedSequence);
        peptide.setPreResidue(preResidue);
        peptide.setPostResidue(postResidue);
        List<MsResultResidueMod> mods = new ArrayList<MsResultResidueMod>(dynamicModList.size());
        for(Modification mod: dynamicModList) {
            ResultResidueModBean mb = new ResultResidueModBean();
            char modRes = strippedSequence.charAt(mod.getPosition() - 1);
            mb.setModificationMass(BigDecimal.valueOf(mod.getMass().doubleValue() - AminoAcidUtils.monoMass(modRes)));
            mb.setModifiedPosition(mod.getPosition() - 1);
            mb.setModifiedResidue(modRes);
            mods.add(mb);
        }
        peptide.setDynamicResidueModifications(mods);
        return peptide;
    }
    
    public void setStrippedSequence(String seq) {
        this.strippedSequence = seq;
    }
    
    public void setPreResidue(char pre) {
        this.preResidue = pre;
    }
    
    public void setPostResidue(char post) {
        this.postResidue = post;
    }
    
    public List<Modification> getDynamicResidueModifications() {
        return dynamicModList;
    }
    
    public void addModification(Modification mod) {
        dynamicModList.add(mod);
    }
    
    public void setSequestResult(SequestResult result) {
        seqRes = result;
    }
    public void setPeptideProphetResult(PeptideProphetResultBean ppRes) {
        this.ppRes = ppRes;
    }
    
    @Override
    public SequestResultData getSequestResultData() {
        return seqRes.getSequestResultData();
    }

    @Override
    public List<MsSearchResultProteinIn> getProteinMatchList() {
        return seqRes.getProteinMatchList();
    }

    @Override
    public int getScanNumber() {
        return seqRes.getScanNumber();
    }

    @Override
    public int getCharge() {
        return seqRes.getCharge();
    }

    @Override
    public BigDecimal getObservedMass() {
        return seqRes.getObservedMass();
    }

    @Override
    public ValidationStatus getValidationStatus() {
        return seqRes.getValidationStatus();
    }

    @Override
    public double getProbabilityNet_0() {
        return ppRes.getProbabilityNet_0();
    }

    @Override
    public double getProbabilityNet_1() {
        return ppRes.getProbabilityNet_1();
    }

    @Override
    public double getProbabilityNet_2() {
        return ppRes.getProbabilityNet_2();
    }
    
    @Override
    public double getMassDifference() {
        return ppRes.getMassDifference();
    }

    @Override
    public int getNumMissedCleavages() {
        return ppRes.getNumMissedCleavages();
    }

    @Override
    public int getNumEnzymaticTermini() {
        return ppRes.getNumEnzymaticTermini();
    }

    @Override
    public double getProbability() {
        return ppRes.getProbability();
    }

    @Override
    public double getfVal() {
        return ppRes.getfVal();
    }

}
