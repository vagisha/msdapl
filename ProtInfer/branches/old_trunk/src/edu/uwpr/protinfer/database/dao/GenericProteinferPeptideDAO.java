package edu.uwpr.protinfer.database.dao;

import java.util.List;

import edu.uwpr.protinfer.database.dto.BaseProteinferPeptide;
import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;

public interface GenericProteinferPeptideDAO<S extends ProteinferSpectrumMatch, T extends BaseProteinferPeptide<S>> {

    public abstract int save(BaseProteinferPeptide<?> peptide);

    public abstract List<Integer> getPeptideIdsForProteinferProtein(int pinferProteinId);

    public abstract List<T> getPeptidesForProteinferProtein(int pinferProteinId);

    public abstract List<Integer> getPeptideIdsForProteinferRun(int proteinferId);

    public abstract List<T> getPeptidesForProteinferRun(int proteinferId);

    public abstract T getPeptide(int pinferPeptideId);

    public abstract void deleteProteinferPeptide(int id);

}