package edu.uwpr.protinfer.database.dao;

import java.util.List;

import edu.uwpr.protinfer.database.dto.GenericProteinferProtein;
import edu.uwpr.protinfer.database.dto.ProteinUserValidation;
import edu.uwpr.protinfer.database.dto.ProteinferProtein;

public interface GenericProteinferProteinDAO  <P extends GenericProteinferProtein<?>> {

    public abstract int save(GenericProteinferProtein<?> protein);

    public abstract void saveProteinferProteinPeptideMatch(int pinferProteinId, int pinferPeptideId);
    
    public abstract void updateUserAnnotation(int pinferProteinId, String annotation);

    public abstract void updateUserValidation(int pinferProteinId, ProteinUserValidation validation);

    public abstract P loadProtein(int pinferProteinId);

    public abstract List<Integer> getProteinferProteinIds(int proteinferId);

    public abstract List<P> loadProteins(int proteinferId);
    
//    public abstract List<ProteinferProtein> loadProteinsN(int proteinferId);

    public abstract int getProteinCount(int proteinferId);

    public abstract void delete(int pinferProteinId);

}