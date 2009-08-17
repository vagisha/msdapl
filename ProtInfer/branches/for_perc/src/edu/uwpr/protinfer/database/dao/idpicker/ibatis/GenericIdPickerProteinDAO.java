package edu.uwpr.protinfer.database.dao.idpicker.ibatis;

import java.util.List;
import java.util.Map;

import edu.uwpr.protinfer.PeptideDefinition;
import edu.uwpr.protinfer.database.dao.GenericProteinferProteinDAO;
import edu.uwpr.protinfer.database.dto.ProteinFilterCriteria;
import edu.uwpr.protinfer.database.dto.idpicker.GenericIdPickerProtein;


public interface GenericIdPickerProteinDAO <P extends GenericIdPickerProtein<?>> extends GenericProteinferProteinDAO<P> {

    public abstract int saveIdPickerProtein(GenericIdPickerProtein<?> protein);
    
    public abstract int updateIdPickerProtein(GenericIdPickerProtein<?> protein);
    
    public abstract boolean proteinPeptideGrpAssociationExists(int pinferId, int proteinGrpId, int peptideGrpId);
    
    public abstract void saveProteinPeptideGroupAssociation(int pinferId, int proteinGrpId, int peptideGrpId);
    
    public abstract List<P> loadIdPickerClusterProteins(int pinferId,int clusterId);
    
    public abstract List<P> loadIdPickerGroupProteins(int pinferId,int groupId);
    
    public abstract List<Integer> getIdPickerGroupProteinIds(int pinferId, int groupId);
    
    public abstract List<Integer> getGroupIdsForCluster(int pinferId, int clusterId);
    
    public abstract List<Integer> getClusterIds(int pinferId);
    
    public abstract int getFilteredParsimoniousProteinCount(int proteinferId);
    
    public abstract int getIdPickerGroupCount(int pinferId, boolean parsimonious);
    
    public abstract List<Integer> getFilteredSortedProteinIds(int pinferId, ProteinFilterCriteria filterCriteria);

    public abstract List<Integer> sortProteinIdsByCoverage(int pinferId, boolean groupProteins);
    
    public abstract List<Integer> sortProteinsByNSAF(int pinferId, boolean groupProteins);
    
    public abstract List<Integer> sortProteinIdsByValidationStatus(int pinferId);
    
    public abstract List<Integer> sortProteinIdsBySpectrumCount(int pinferId, boolean groupProteins);
    
    public abstract List<Integer> sortProteinIdsByPeptideCount(int pinferId, PeptideDefinition peptideDef, boolean groupProteins);
    
    public abstract List<Integer> sortProteinIdsByUniquePeptideCount(int pinferId, PeptideDefinition peptideDef, boolean groupProteins);

    public abstract List<Integer> sortProteinIdsByCluster(int pinferId);
    
    public abstract List<Integer> sortProteinIdsByGroup(int pinferId);
    
    public abstract List<Integer> getIdPickerProteinIds(int pinferId, boolean isParsimonious);
    
    public  abstract List<Integer> getNrseqProteinIds(int pinferId);
    
    public abstract List<Integer> getNrseqProteinIds(int pinferId, boolean parsimonious, boolean nonParsimonious);
    
    public abstract Map<Integer, Integer> getProteinGroupIds(int pinferId, boolean parsimonious);
    
    public abstract boolean isNrseqProteinGrouped(int pinferId, int nrseqId);
    
    public abstract boolean isProteinGrouped(int pinferProteinId);
    
}
