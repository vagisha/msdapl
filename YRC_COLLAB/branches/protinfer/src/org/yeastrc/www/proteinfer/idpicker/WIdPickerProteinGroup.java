package org.yeastrc.www.proteinfer.idpicker;

import java.util.ArrayList;
import java.util.List;

import edu.uwpr.protinfer.database.dto.idpicker.IdPickerProtein;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerProteinGroup;

public class WIdPickerProteinGroup {

    private final int groupId;
    private final int clusterId;
    private final List<WIdPickerProtein> proteins;
    private int matchingPeptideCount;
    private int uniqMatchingPeptideCount;
    private int spectrumCount;
    
    public WIdPickerProteinGroup(IdPickerProteinGroup protGrp) {
        
        proteins = new ArrayList<WIdPickerProtein>(protGrp.getProteinCount());
        for(IdPickerProtein prot: protGrp.getProteins()) {
            proteins.add(new WIdPickerProtein(prot));
        }
        this.groupId = protGrp.getGroupId();
        this.clusterId = protGrp.getClusterId();
        this.matchingPeptideCount = protGrp.getMatchingPeptideCount();
        this.uniqMatchingPeptideCount = protGrp.getUniqMatchingPeptideCount();
        this.spectrumCount = protGrp.getSpectrumCount();
    }
    
    public int getGroupId() {
        return groupId;
    }
    
    public int getClusterId() {
        return clusterId;
    }
    
    public int getProteinCount() {
        return proteins.size();
    }
    
    public int getMatchingPeptideCount() {
        return matchingPeptideCount;
    }
    
    public int getUniqMatchingPeptideCount() {
        return uniqMatchingPeptideCount;
    }
    
    public int getSpectrumCount() {
        return spectrumCount;
    }
    
    public List<WIdPickerProtein> getProteins() {
        return proteins;
    }
   
}
