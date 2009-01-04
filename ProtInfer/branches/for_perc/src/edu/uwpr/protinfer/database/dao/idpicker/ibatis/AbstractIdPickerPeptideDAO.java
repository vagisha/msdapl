package edu.uwpr.protinfer.database.dao.idpicker.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

import edu.uwpr.protinfer.database.dao.ibatis.ProteinferPeptideDAO;
import edu.uwpr.protinfer.database.dto.GenericProteinferPeptide;
import edu.uwpr.protinfer.database.dto.idpicker.GenericIdPickerPeptide;

public abstract class AbstractIdPickerPeptideDAO <T extends GenericIdPickerPeptide<?,?>>
        extends BaseSqlMapDAO
        implements GenericIdPickerPeptideDAO<T> {

    private static final String sqlMapNameSpace = "IdPickerPeptide";
    
    private final ProteinferPeptideDAO peptDao;
    
    public AbstractIdPickerPeptideDAO(SqlMapClient sqlMap, ProteinferPeptideDAO peptDao) {
        super(sqlMap);
        this.peptDao = peptDao;
    }

    public int save(GenericProteinferPeptide<?,?> peptide) {
        return peptDao.save(peptide); 
     }
     
     public int saveIdPickerPeptide(GenericIdPickerPeptide<?,?> peptide) {
         int id = save(peptide);
         peptide.setId(id);
         save(sqlMapNameSpace+".insert", peptide);
         return id;
     }
     
     public List<Integer> getMatchingProtGroupIds(int pinferId, int peptideGroupId) {
         Map<String, Integer> map = new HashMap<String, Integer>(2);
         map.put("pinferId", pinferId);
         map.put("groupId", peptideGroupId);
         return super.queryForList(sqlMapNameSpace+".selectProtGrpIdsForPeptGrpId", map);
     }
     
     public void delete(int id) {
         peptDao.delete(id);
     }

     public List<Integer> getPeptideIdsForProteinferProtein(int pinferProteinId) {
         return peptDao.getPeptideIdsForProteinferProtein(pinferProteinId);
     }
     
     public List<Integer> getPeptideIdsForProteinferRun(int proteinferId) {
         return peptDao.getPeptideIdsForProteinferRun(proteinferId);
     }

}
