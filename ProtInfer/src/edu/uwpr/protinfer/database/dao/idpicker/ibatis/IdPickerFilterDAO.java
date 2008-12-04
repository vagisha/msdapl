package edu.uwpr.protinfer.database.dao.idpicker.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

import edu.uwpr.protinfer.database.dto.idpicker.IdPickerFilter;


public class IdPickerFilterDAO extends BaseSqlMapDAO {

    private static final String sqlMapNameSpace = "IdPickerFilter";
    
    public IdPickerFilterDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public int saveProteinferFilter(IdPickerFilter filter) {
        return super.saveAndReturnId(sqlMapNameSpace+".insert", filter);
    }
    
    public List<IdPickerFilter> getFiltersForProteinferRun(int pinferId) {
        return super.queryForList(sqlMapNameSpace+".selectFiltersForRun", pinferId);
    }
    
    public void deleteProteinferFilter(int id) {
        super.delete(sqlMapNameSpace+".delete", id);
    }
}
