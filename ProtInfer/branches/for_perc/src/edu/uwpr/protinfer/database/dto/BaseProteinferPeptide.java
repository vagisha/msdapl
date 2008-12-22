package edu.uwpr.protinfer.database.dto;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseProteinferPeptide<S extends ProteinferSpectrumMatch, T extends BaseProteinferIon<S>> {

    private int id;
    private int pinferId;
    private String sequence;
    private List<T> ionList;
    private List<Integer> matchingProteinIds;

    public BaseProteinferPeptide() {
        ionList = new ArrayList<T>();
        matchingProteinIds = new ArrayList<Integer>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public int getProteinferId() {
        return pinferId;
    }

    public void setProteinferId(int pinferId) {
        this.pinferId = pinferId;
    }

    public List<T> getIonList() {
        return ionList;
    }

    public void setIonList(List<T> spectrumMatchList) {
        this.ionList = spectrumMatchList;
    }

    public void addIon(T ion) {
        ionList.add(ion);
    }
    
    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
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
        int count = 0;
        for(T ion: ionList) {
            count += ion.getSpectralCount();
        }
        return count;
    }

}