package edu.uwpr.protinfer.database.dao;

import java.util.List;

import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;

public interface GenericProteinferSpectrumMatchDAO <T extends ProteinferSpectrumMatch> {

    public abstract int saveSpectrumMatch(T spectrumMatch);

    public abstract T getSpectrumMatch(int psmId);

    public abstract List<T> getSpectrumMatchesForPeptide(int pinferPeptideId);

    public abstract List<Integer> getSpectrumMatchIdsForPinferRun(int pinferId);

    public abstract List<ProteinferSpectrumMatch> getSpectrumMatchesForPinferRunAndRunSearch(
            int pinferId, int runSearchId);

}