package org.yeastrc.www.proteinfer.idpicker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerProteinBase;

public class WIdPickerProteinGroup {

    private int groupId;
    private int clusterId;
    private List<WIdPickerProtein> proteins;
    private int matchingPeptideCount;
    private int uniqMatchingPeptideCount;
    private int spectrumCount;
    private Set<Integer> nonUniqPeptGrpIds;
    private Set<Integer> uniqPeptGrpIds;
    
//    private String nonUniqMatchingPeptideGroupIdsString = "NONE";
//    private String uniqMatchingPeptideGroupIdsString = "NONE";
    
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
       nonUniqPeptGrpIds = new HashSet<Integer>();
       uniqPeptGrpIds = new HashSet<Integer>();
    }
    
    public String getNonUniqMatchingPeptideGroupIdsString() {
        StringBuilder buf = new StringBuilder();
        for(Integer grpId: nonUniqPeptGrpIds) {
//            if(!uniqPeptideGroupIds.contains(grpId))
            buf.append(","+grpId);
        }
        if(buf.length() > 0)    buf.deleteCharAt(0);
        return buf.toString();
    }

    public String getUniqMatchingPeptideGroupIdsString() {
        StringBuilder buf = new StringBuilder();
        for(Integer grpId: uniqPeptGrpIds) {
            buf.append(","+grpId);
        }
        if(buf.length() > 0)    buf.deleteCharAt(0);
        return buf.toString();
    }
    
    public void addNonUniqPeptideGrpId(int id) {
        nonUniqPeptGrpIds.add(id);
    }
    
    public void addUniqPeptideGrpId(int id) {
        uniqPeptGrpIds.add(id);
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