package edu.uwpr.protinfer.database.dao;

import java.util.List;

import edu.uwpr.protinfer.database.dto.GenericProteinferPeptide;
import edu.uwpr.protinfer.database.dto.ProteinferPeptide;

public interface GenericProteinferPeptideDAO<T extends GenericProteinferPeptide<?,?>> {

    public abstract int save(GenericProteinferPeptide<?,?> peptide);

    public abstract List<Integer> getPeptideIdsForProteinferProtein(int pinferProteinId);
    
    public abstract List<Integer> getUniquePeptideIdsForProteinferProtein(int pinferProteinId);

    public abstract List<T> loadPeptidesForProteinferProtein(int pinferProteinId);

    public abstract List<Integer> getPeptideIdsForProteinferRun(int proteinferId);
    
    public abstract int getUniquePeptideSequenceCountForRun(int proteinferId);
    
    public abstract T load(int pinferPeptideId);
    
    public abstract ProteinferPeptide loadPeptide(int pinferId, String peptideSequence);

    public abstract int update(GenericProteinferPeptide<?,?> peptide);
    
    public abstract void delete(int id);

}