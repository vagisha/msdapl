package edu.uwpr.protinfer.database.dto;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseProteinferPeptide<T extends ProteinferSpectrumMatch> {

    private int id;
    private String sequence;
    private List<T> spectrumMatchList;
    private List<Integer> matchingProteinIds;

    public BaseProteinferPeptide() {
        spectrumMatchList = new ArrayList<T>();
        matchingProteinIds = new ArrayList<Integer>();
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
        return spectrumMatchList.size();
    }

    public abstract T getBestSpectrumMatch();
//        ProteinferSpectrumMatch bestPsm = null;
//        for(ProteinferSpectrumMatch psm: spectrumMatchList) {
//            if(bestPsm == null) {
//                bestPsm = psm;
//            }
//            else {
//                bestPsm = bestPsm.getRank() < psm.getRank() ? bestPsm : psm;
//            }
//        }
//        return bestPsm;
//    }

}