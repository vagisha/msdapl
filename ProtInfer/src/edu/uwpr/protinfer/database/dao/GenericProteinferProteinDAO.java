package edu.uwpr.protinfer.database.dao;

import java.util.List;

import edu.uwpr.protinfer.database.dto.BaseProteinferPeptide;
import edu.uwpr.protinfer.database.dto.BaseProteinferProtein;
import edu.uwpr.protinfer.database.dto.ProteinUserValidation;
import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;

public interface GenericProteinferProteinDAO<
    S extends ProteinferSpectrumMatch,
    T extends BaseProteinferPeptide<S>, 
    P extends BaseProteinferProtein<S,T>> {

    public abstract int save(BaseProteinferProtein<?,?> protein);

    public abstract void saveProteinferPeptideProteinMatch(int pinferProteinId, int pinferPeptideId);
    
    public abstract void updateUserAnnotation(int pinferProteinId, String annotation);

    public abstract void updateUserValidation(int pinferProteinId, ProteinUserValidation validation);

    public abstract P getProtein(int pinferProteinId);

    public abstract List<Integer> getProteinferProteinIds(int proteinferId);

    public abstract List<P> getProteins(int proteinferId);

    public abstract int getFilteredProteinCount(int proteinferId);

    public abstract void delete(int pinferProteinId);

}