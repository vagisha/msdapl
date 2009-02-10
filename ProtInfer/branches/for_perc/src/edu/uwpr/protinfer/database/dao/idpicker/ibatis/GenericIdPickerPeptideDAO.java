package edu.uwpr.protinfer.database.dao.idpicker.ibatis;

import java.util.List;

import edu.uwpr.protinfer.database.dao.GenericProteinferPeptideDAO;
import edu.uwpr.protinfer.database.dto.idpicker.GenericIdPickerPeptide;

public interface GenericIdPickerPeptideDAO <T extends GenericIdPickerPeptide<?,?>> extends GenericProteinferPeptideDAO<T> {

    public abstract int saveIdPickerPeptide(GenericIdPickerPeptide<?,?> peptide);
    
    public abstract int updateIdPickerPeptide(GenericIdPickerPeptide<?,?> peptide);
    
    public abstract List<T> loadIdPickerGroupPeptides(int pinferId, int groupId);
    
    public abstract List<Integer> getMatchingPeptGroupIds(int pinferId, int proteinGroupId); 
}
