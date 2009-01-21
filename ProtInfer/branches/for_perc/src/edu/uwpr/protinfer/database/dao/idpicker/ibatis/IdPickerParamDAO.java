package edu.uwpr.protinfer.database.dao.idpicker.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

import edu.uwpr.protinfer.database.dto.idpicker.IdPickerParam;


public class IdPickerParamDAO extends BaseSqlMapDAO {

    private static final String sqlMapNameSpace = "IdPickerParam";
    
    public IdPickerParamDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public int saveIdPickerParam(IdPickerParam param) {
        return super.saveAndReturnId(sqlMapNameSpace+".insert", param);
    }
    
    public List<IdPickerParam> getParamsForIdPickerRun(int pinferId) {
        return super.queryForList(sqlMapNameSpace+".selectParamsForRun", pinferId);
    }
    
    public void deleteIdPickerParam(int id) {
        super.delete(sqlMapNameSpace+".delete", id);
    }
}
