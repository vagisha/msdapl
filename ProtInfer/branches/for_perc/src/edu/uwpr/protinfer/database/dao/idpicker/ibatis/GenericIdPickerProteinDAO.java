package edu.uwpr.protinfer.database.dao.idpicker.ibatis;

import java.util.List;

import edu.uwpr.protinfer.database.dao.GenericProteinferProteinDAO;
import edu.uwpr.protinfer.database.dto.idpicker.GenericIdPickerProtein;


public interface GenericIdPickerProteinDAO <P extends GenericIdPickerProtein<?>> extends GenericProteinferProteinDAO<P> {

    public abstract int saveIdPickerProtein(GenericIdPickerProtein<?> protein);
    
    public abstract boolean proteinPeptideGrpAssociationExists(int pinferId, int proteinGrpId, int peptideGrpId);
    
    public abstract void saveProteinPeptideGroupAssociation(int pinferId, int proteinGrpId, int peptideGrpId);
    
    public abstract List<P> loadIdPickerClusterProteins(int pinferId,int clusterId);
    
    public abstract List<P> loadIdPickerGroupProteins(int pinferId,int groupId);
    
    public abstract List<Integer> getGroupIdsForCluster(int pinferId, int clusterId);
    
    public abstract int getFilteredParsimoniousProteinCount(int proteinferId);
}
