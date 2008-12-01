package edu.uwpr.protinfer.database.dto;

import java.util.ArrayList;
import java.util.List;

public class BaseProteinferPeptide<T extends ProteinferSpectrumMatch> {

    private int id;
    private int pinferId;
    private List<T> spectrumMatchList;
    private List<Integer> matchingProteinIds;

    public BaseProteinferPeptide() {
        spectrumMatchList = new ArrayList<T>();
        matchingProteinIds = new ArrayList<Integer>();
    }

    public BaseProteinferPeptide(int proteinferId) {
        this();
        this.pinferId = proteinferId;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<T> getSpectrumMatchList() {
        return spectrumMatchList;
    }

    public void setSpectrumMatchList(List<T> spectrumMatchList) {
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

    public ProteinferSpectrumMatch getBestSpectrumMatch() {
        ProteinferSpectrumMatch bestPsm = null;
        for(ProteinferSpectrumMatch psm: spectrumMatchList) {
            if(bestPsm == null) {
                bestPsm = psm;
            }
            else {
                bestPsm = bestPsm.getRank() < psm.getRank() ? bestPsm : psm;
            }
        }
        return bestPsm;
    }

}