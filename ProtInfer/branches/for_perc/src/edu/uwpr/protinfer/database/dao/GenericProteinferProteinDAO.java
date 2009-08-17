package edu.uwpr.protinfer.database.dao;

import java.util.ArrayList;
import java.util.List;

import edu.uwpr.protinfer.database.dto.GenericProteinferProtein;
import edu.uwpr.protinfer.database.dto.ProteinUserValidation;
import edu.uwpr.protinfer.database.dto.ProteinferProtein;

public interface GenericProteinferProteinDAO  <P extends GenericProteinferProtein<?>> {

    public abstract int save(GenericProteinferProtein<?> protein);
    
    public abstract void saveProteinferProteinPeptideMatch(int pinferProteinId, int pinferPeptideId);
    
    public abstract int update(GenericProteinferProtein<?> protein);
    
    public abstract void updateUserAnnotation(int pinferProteinId, String annotation);

    public abstract void updateUserValidation(int pinferProteinId, ProteinUserValidation validation);

    public abstract P loadProtein(int pinferProteinId);
    
    public abstract ProteinferProtein loadProtein(int proteinferId, int nrseqProteinId);

    public abstract List<Integer> getProteinferProteinIds(int proteinferId);
    
    public abstract List<Integer> getNrseqIdsForRun(int proteinferId);
    
    public abstract int getPeptideCountForProtein(int nrseqId, List<Integer> pinferIds);
    
    public abstract List<String> getPeptidesForProtein(int nrseqId, List<Integer> pinferIds);

    public abstract List<P> loadProteins(int proteinferId);
    
//    public abstract List<ProteinferProtein> loadProteinsN(int proteinferId);
    
    public abstract List<Integer> getProteinIdsForNrseqIds(int proteinferId, ArrayList<Integer> nrseqIds);

    public abstract int getProteinCount(int proteinferId);

    public abstract void delete(int pinferProteinId);

}