package edu.uwpr.protinfer.database.dao.idpicker.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

import edu.uwpr.protinfer.database.dao.GenericProteinferIonDAO;
import edu.uwpr.protinfer.database.dto.GenericProteinferIon;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerIon;

public class IdPickerIonDAO extends BaseSqlMapDAO implements GenericProteinferIonDAO<IdPickerIon>{

    private static final String sqlMapNameSpace = "IdPickerIon";
    
    private final  GenericProteinferIonDAO<?> ionDao;
    
    public IdPickerIonDAO(SqlMapClient sqlMap, GenericProteinferIonDAO<?> ionDao) {
        super(sqlMap);
        this.ionDao = ionDao;
    }

    @Override
    public int save(GenericProteinferIon<?> ion) {
        return ionDao.save(ion);
    }
    
    @Override
    public IdPickerIon load(int pinferIonId) {
        return (IdPickerIon) queryForObject(sqlMapNameSpace+".select", pinferIonId);
    }

    @Override
    public List<IdPickerIon> loadIonsForPeptide(int pinferPeptideId) {
        return queryForList(sqlMapNameSpace+".selectIonsForPeptide", pinferPeptideId);
    }
}
