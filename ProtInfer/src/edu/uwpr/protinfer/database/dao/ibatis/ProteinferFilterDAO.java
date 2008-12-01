package edu.uwpr.protinfer.database.dao.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

import edu.uwpr.protinfer.database.dto.ProteinferFilter;


public class ProteinferFilterDAO extends BaseSqlMapDAO {

    private static final String sqlMapNameSpace = "ProteinferFilter";
    
    public ProteinferFilterDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public int saveProteinferFilter(ProteinferFilter filter) {
        return super.saveAndReturnId(sqlMapNameSpace+".insert", filter);
    }
    
    public List<ProteinferFilter> getFiltersForProteinferRun(int pinferId) {
        return super.queryForList(sqlMapNameSpace+".selectFiltersForRun", pinferId);
    }
    
    public void deleteProteinferFilter(int id) {
        super.delete(sqlMapNameSpace+".delete", id);
    }
}
