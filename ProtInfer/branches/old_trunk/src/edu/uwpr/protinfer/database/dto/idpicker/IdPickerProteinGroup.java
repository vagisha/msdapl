package edu.uwpr.protinfer.database.dto.idpicker;

import java.util.ArrayList;
import java.util.List;

public class IdPickerProteinGroup {

    private final int groupId;
    private List<IdPickerProtein> proteins;
    private List<IdPickerPeptideGroup> matchingPeptideGroups;
    
    public IdPickerProteinGroup(int groupId) {
        this.groupId = groupId;
        proteins = new ArrayList<IdPickerProtein>();
        matchingPeptideGroups = new ArrayList<IdPickerPeptideGroup>();
    }
    
    public int getGroupId() {
        return groupId;
    }
    
    public int getClusterId() {
        if(proteins.size() > 0)
            return proteins.get(0).getClusterId();
        else
            return 0;
    }
    public void setProteins(List<IdPickerProtein> proteins) {
        if(proteins != null)
            this.proteins = proteins;
    }
    
    public void addProtein(IdPickerProtein protein) {
        this.proteins.add(protein);
    }
    
    public List<IdPickerProtein> getProteins() {
        return this.proteins;
    }
    
    public int getProteinCount() {
        return proteins.size();
    }
    
    public List<IdPickerPeptideGroup> getMatchingPeptideGroups() {
        return matchingPeptideGroups;
    }
    
    public List<Integer> getMatchingPeptideGroupIds() {
        List<Integer> ids = new ArrayList<Integer>(matchingPeptideGroups.size());
        for(IdPickerPeptideGroup grp: matchingPeptideGroups)
            ids.add(grp.getGroupId());
        return ids;
    }
    
    public void setMatchingPeptideGroups(List<IdPickerPeptideGroup> peptGrps) {
        this.matchingPeptideGroups = peptGrps;
    }
    
    public void addMatchingPeptideGroup(IdPickerPeptideGroup peptGrp) {
        this.matchingPeptideGroups.add(peptGrp);
    }
    
    public int getMatchingPeptideCount() {
        int cnt = 0;
        for(IdPickerPeptideGroup grp: matchingPeptideGroups) {
            cnt += grp.getPeptideCount();
        }
        return cnt;
    }
    
    public int getUniqMatchingPeptideCount() {
        int cnt = 0;
        for(IdPickerPeptideGroup grp: matchingPeptideGroups) {
            if(grp.isUniqueToProteinGroup()) {
                cnt += grp.getPeptideCount();
            }
        }
        return cnt;
    }
    
    public int getSpectrumCount() {
        int cnt = 0;
        for(IdPickerPeptideGroup grp: matchingPeptideGroups) {
            cnt += grp.getSpectrumCount();
        }
        return cnt;
    }
    
    public String getNonUniqMatchingPeptideGroupIdsString() {
        StringBuilder buf = new StringBuilder();
        for(IdPickerPeptideGroup grp: matchingPeptideGroups) {
            if(!grp.isUniqueToProteinGroup())
                buf.append(","+grp.getGroupId());
        }
        if(buf.length() > 0)    buf.deleteCharAt(0);
        return buf.toString();
    }
    
    public String getUniqMatchingPeptideGroupIdsString() {
        StringBuilder buf = new StringBuilder();
        for(IdPickerPeptideGroup grp: matchingPeptideGroups) {
            if(grp.isUniqueToProteinGroup())
                buf.append(","+grp.getGroupId());
        }
        if(buf.length() > 0)    buf.deleteCharAt(0);
        return buf.toString();
    }
}
