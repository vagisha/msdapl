/**
 * PeptideProphetResultPeptideBuilder.java
 * @author Vagisha Sharma
 * Oct 6, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.analysis.peptideProphet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.protinfer.proteinProphet.Modification;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.impl.ResultResidueModBean;
import org.yeastrc.ms.domain.search.impl.SearchResultPeptideBean;
import org.yeastrc.ms.util.AminoAcidUtils;

/**
 * 
 */
public class PeptideProphetResultPeptideBuilder {

    private static PeptideProphetResultPeptideBuilder instance;
    
    private PeptideProphetResultPeptideBuilder() {}
    
    public static PeptideProphetResultPeptideBuilder getInstance() {
        if(instance == null) {
            instance = new PeptideProphetResultPeptideBuilder();
        }
        return instance;
    }
    
    public MsSearchResultPeptide buildResultPeptide(String strippedSequence, char preResidue,
            char postResidue, List<Modification> modificationList) {
        
        SearchResultPeptideBean peptide = new SearchResultPeptideBean();
        peptide.setPeptideSequence(strippedSequence);
        peptide.setPreResidue(preResidue);
        peptide.setPostResidue(postResidue);
        List<MsResultResidueMod> mods = new ArrayList<MsResultResidueMod>(modificationList.size());
        for(Modification mod: modificationList) {
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
}
