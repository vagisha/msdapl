package edu.uwpr.protinfer.database.dao;

import java.util.List;

import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;

public interface GenericProteinferSpectrumMatchDAO <T extends ProteinferSpectrumMatch> {

    public abstract int saveSpectrumMatch(T spectrumMatch);

    public abstract T loadSpectrumMatch(int psmId);

    public abstract List<T> loadSpectrumMatchesForPeptide(int pinferPeptideId);
    
    public abstract List<T> loadSpectrumMatchesForIon(int pinferIonId);
    
    public abstract T loadBestSpectrumMatchForIon(int pinferIonId);

    public abstract List<Integer> getSpectrumMatchIdsForPinferRun(int pinferId);
    
    public abstract int getSpectrumCountForPinferRun(int pinferId);
    
    public abstract int getMaxSpectrumCountForPinferRunProtein(int pinferId);
    
    public abstract int getMinSpectrumCountForPinferRunProtein(int prinferId);
    
    public abstract List<Integer> getSpectrumMatchIdsForPinferRunInput(int pinferId, int inputId);
    
    public abstract int update(ProteinferSpectrumMatch psm);
}