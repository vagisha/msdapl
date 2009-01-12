package org.yeastrc.www.proteinfer.idpicker;

import java.util.ArrayList;
import java.util.List;

import edu.uwpr.protinfer.database.dto.idpicker.IdPickerProteinBase;

public class WIdPickerProteinGroup {

    private int groupId;
    private int clusterId;
    private List<WIdPickerProtein> proteins;
    private int matchingPeptideCount;
    private int uniqMatchingPeptideCount;
    private int spectrumCount;
    
    public WIdPickerProteinGroup(List<WIdPickerProtein> groupProteins) {
       if(groupProteins != null)
           proteins = groupProteins;
       if(groupProteins == null)
           groupProteins = new ArrayList<WIdPickerProtein>(0);
       if(groupProteins.size() > 0) {
           IdPickerProteinBase prot = groupProteins.get(0).getProtein();
           this.groupId = prot.getGroupId();
           this.clusterId = prot.getClusterId();
           this.spectrumCount = prot.getSpectrumCount();
           this.matchingPeptideCount = prot.getPeptideCount();
           this.uniqMatchingPeptideCount = prot.getUniquePeptideCount();
       }
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