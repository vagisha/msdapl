package edu.uwpr.protinfer.database.dto;

import java.util.ArrayList;
import java.util.List;

public class ProteinferPeptide {

    private int id;
    private int pinferId;
    private int groupId;
    private String sequence;
    
    private List<ProteinferSpectrumMatch> spectrumMatchList;
    
    private List<Integer> matchingProteinIds;

    public ProteinferPeptide() {
        spectrumMatchList = new ArrayList<ProteinferSpectrumMatch>();
        matchingProteinIds = new ArrayList<Integer>();
    }
    
    public ProteinferPeptide(int proteinferId, int peptideGroupId, String sequence) {
        this.pinferId = proteinferId;
        this.groupId = peptideGroupId;
        this.sequence = sequence;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public List<ProteinferSpectrumMatch> getSpectrumMatchList() {
        return spectrumMatchList;
    }

    public void setSpectrumMatchList(List<ProteinferSpectrumMatch> spectrumMatchList) {
        this.spectrumMatchList = spectrumMatchList;
    }

    public int getProteinferId() {
        return pinferId;
    }

    public void setProteinferId(int pinferId) {
        this.pinferId = pinferId;
    }
    
    public List<Integer> getMatchingProteinIds() {
        return matchingProteinIds;
    }

    public void setMatchingProteinIds(List<Integer> matchingProteinIds) {
        this.matchingProteinIds = matchingProteinIds;
    }
    
    public boolean isUniqueToProtein() {
        return matchingProteinIds.size() == 1;
    }
    
    public int getSpectralCount() {
        return spectrumMatchList.size();
    }
   
    public float getBestFdr() {
        float best = Float.MAX_VALUE;
        for(ProteinferSpectrumMatch psm: spectrumMatchList) {
            best = (float) Math.min(best, psm.getFdr());
        }
        return (float) (Math.round(best*100.0) / 100.0);
    }
}
