package edu.uwpr.protinfer.database.dto.idpicker;

import java.util.ArrayList;
import java.util.List;

public class IdPickerProteinGroup {

    private final int pinferId;
    private final int groupId;
    private List<IdPickerProteinBase> proteins;
    
    private int numPeptides;
    private int numUniquePeptides;
//    private int numPeptides_S;
//    private int numUniquePeptides_S;
//    private int numPeptides_SM;
//    private int numUniquePeptides_SM;
//    private int numPeptides_SC;
//    private int numUniquePeptides_SC;
//    private int numPeptides_SCM;
//    private int numUniquePeptides_SCM;
    
//    private List<Integer> peptideGroupIds;
//    private List<Integer> uniqPeptideGroupIds;
    
    private int spectrumCount;
    
    public IdPickerProteinGroup(int pinferId, int groupId) {
        this.pinferId = pinferId;
        this.groupId = groupId;
        proteins = new ArrayList<IdPickerProteinBase>();
//        peptideGroupIds = new ArrayList<Integer>();
//        uniqPeptideGroupIds = new ArrayList<Integer>();
    }
    
    public int getProteinferId() {
        return pinferId;
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
    public void setProteins(List<IdPickerProteinBase> proteins) {
        if(proteins != null)
            this.proteins = proteins;
    }
    
    public void addProtein(IdPickerProteinBase protein) {
        this.proteins.add(protein);
    }
    
    public List<IdPickerProteinBase> getProteins() {
        return this.proteins;
    }
    
    public int getProteinCount() {
        return proteins.size();
    }
    
//    public List<Integer> getMatchingPeptideGroupIds() {
//        return this.peptideGroupIds;
//    }
//    
//    public void setMatchingPeptideGroupIds(List<Integer> peptGrpIds) {
//        this.peptideGroupIds = peptGrpIds;
//    }
    
//    public int getPeptide_SCount() {
//        return numPeptides_S;
//    }
//    
//    public int getUniquePeptide_SCount() {
//        return numUniquePeptides_S;
//    }
//    
//    public int getPeptide_SMCount() {
//        return numPeptides_SM;
//    }
//    
//    public int getUniquePeptide_SMCount() {
//        return numUniquePeptides_SM;
//    }
//    
//    public int getPeptide_SCCount() {
//        return numPeptides_SC;
//    }
//    
//    public int getUniquePeptide_SCCount() {
//        return numUniquePeptides_SC;
//    }
//    
//    public int getPeptide_SCMCount() {
//        return numPeptides_SCM;
//    }
//    
//    public int getUniquePeptide_SCMCount() {
//        return numUniquePeptides_SCM;
//    }
    
    public int getNumPeptides() {
        return numPeptides;
    }
    
    public void setNumPeptides(int num) {
        this.numPeptides = num;
    }
    
    public int getNumUniquePeptides() {
        return numUniquePeptides;
    }
    
    public void setNumUniquePeptides(int num) {
        this.numUniquePeptides = num;
    }
    
    public int getSpectrumCount() {
       return spectrumCount;
    }
    
    public void setSpectrumCount(int count) {
        this.spectrumCount = count;
    }
    
//    public String getNonUniqMatchingPeptideGroupIdsString() {
//        StringBuilder buf = new StringBuilder();
//        for(Integer grpId: peptideGroupIds) {
//            if(!uniqPeptideGroupIds.contains(grpId))
//                buf.append(","+grpId);
//        }
//        if(buf.length() > 0)    buf.deleteCharAt(0);
//        return buf.toString();
//    }
//    
//    public String getUniqMatchingPeptideGroupIdsString() {
//        StringBuilder buf = new StringBuilder();
//        for(Integer grpId: uniqPeptideGroupIds) {
//            buf.append(","+grpId);
//        }
//        if(buf.length() > 0)    buf.deleteCharAt(0);
//        return buf.toString();
//    }
}
