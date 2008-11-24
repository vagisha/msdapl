package edu.uwpr.protinfer.database.dto;

import java.util.ArrayList;
import java.util.List;

public class ProteinferProteinGroup {

    private final int groupId;
    private List<ProteinferProtein> proteins;
    private List<ProteinferPeptideGroup> matchingPeptideGroups;
    
    public ProteinferProteinGroup(int groupId) {
        this.groupId = groupId;
        proteins = new ArrayList<ProteinferProtein>();
        matchingPeptideGroups = new ArrayList<ProteinferPeptideGroup>();
    }
    
    public int getGroupId() {
        return groupId;
    }
    
    public void setProteins(List<ProteinferProtein> proteins) {
        if(proteins != null)
            this.proteins = proteins;
    }
    
    public void addProtein(ProteinferProtein protein) {
        this.proteins.add(protein);
    }
    
    public List<ProteinferProtein> getProteins() {
        return this.proteins;
    }
    
    public int getProteinCount() {
        return proteins.size();
    }
    
    public List<ProteinferPeptideGroup> getMatchingPeptideGroups() {
        return matchingPeptideGroups;
    }
    
    public List<Integer> getMatchingPeptideGroupIds() {
        List<Integer> ids = new ArrayList<Integer>(matchingPeptideGroups.size());
        for(ProteinferPeptideGroup grp: matchingPeptideGroups)
            ids.add(grp.getGroupId());
        return ids;
    }
    
    public void setMatchingPeptideGroups(List<ProteinferPeptideGroup> peptGrps) {
        this.matchingPeptideGroups = peptGrps;
    }
    
    public void addMatchingPeptideGroup(ProteinferPeptideGroup peptGrp) {
        this.matchingPeptideGroups.add(peptGrp);
    }
    
    public int getMatchingPeptideCount() {
        int cnt = 0;
        for(ProteinferPeptideGroup grp: matchingPeptideGroups) {
            cnt += grp.getPeptideCount();
        }
        return cnt;
    }
    
    public int getUniqMatchingPeptideCount() {
        int cnt = 0;
        for(ProteinferPeptideGroup grp: matchingPeptideGroups) {
            if(grp.isUniqueToProteinGroup()) {
                cnt += grp.getPeptideCount();
            }
        }
        return cnt;
    }
    
    public int getSpectrumCount() {
        int cnt = 0;
        for(ProteinferPeptideGroup grp: matchingPeptideGroups) {
            cnt += grp.getSpectrumCount();
        }
        return cnt;
    }
    
    public String getNonUniqMatchingPeptideGroupIdsString() {
        StringBuilder buf = new StringBuilder();
        for(ProteinferPeptideGroup grp: matchingPeptideGroups) {
            if(!grp.isUniqueToProteinGroup())
                buf.append(","+grp.getGroupId());
        }
        if(buf.length() > 0)    buf.deleteCharAt(0);
        return buf.toString();
    }
    
    public String getUniqMatchingPeptideGroupIdsString() {
        StringBuilder buf = new StringBuilder();
        for(ProteinferPeptideGroup grp: matchingPeptideGroups) {
            if(grp.isUniqueToProteinGroup())
                buf.append(","+grp.getGroupId());
        }
        if(buf.length() > 0)    buf.deleteCharAt(0);
        return buf.toString();
    }
}
