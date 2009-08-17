package edu.uwpr.protinfer.database.dao.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

import edu.uwpr.protinfer.database.dao.GenericProteinferIonDAO;
import edu.uwpr.protinfer.database.dto.GenericProteinferIon;
import edu.uwpr.protinfer.database.dto.ProteinferIon;

public class ProteinferIonDAO extends BaseSqlMapDAO implements GenericProteinferIonDAO<ProteinferIon> {

    private static final String sqlMapNameSpace = "ProteinferIon";
    
    public ProteinferIonDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    @Override
    public int save(GenericProteinferIon<?> ion) {
        return saveAndReturnId(sqlMapNameSpace+".insert", ion);
    }
    
    @Override
    public List<ProteinferIon> loadIonsForPeptide(int pinferPeptideId) {
        return queryForList(sqlMapNameSpace+".selectIonsForPeptide", pinferPeptideId);
    }

    @Override
    public ProteinferIon load(int pinferIonId) {
        return (ProteinferIon) queryForObject(sqlMapNameSpace+".select", pinferIonId);
    }
}
