package org.yeastrc.ms.dao.protinfer.idpicker.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.protinfer.GenericProteinferRunDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunDAO;
import org.yeastrc.ms.domain.protinfer.GenericProteinferRun;
import org.yeastrc.ms.domain.protinfer.ProteinferInput.InputType;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerInput;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerRun;

import com.ibatis.sqlmap.client.SqlMapClient;

public class IdPickerRunDAO extends BaseSqlMapDAO implements GenericProteinferRunDAO<IdPickerInput, IdPickerRun> {

    private static final String sqlMapNameSpace = "IdPickerRun";
    
    private final ProteinferRunDAO runDao;
    
    public IdPickerRunDAO(SqlMapClient sqlMap, ProteinferRunDAO runDao) {
        super(sqlMap);
        this.runDao = runDao;
    }

    @Override
    public IdPickerRun loadProteinferRun(int proteinferId) {
        return (IdPickerRun) queryForObject(sqlMapNameSpace+".select", proteinferId);
    }
    
    @Override
    public void delete(int pinferId) {
        runDao.delete(pinferId);
    }

    @Override
    public List<Integer> loadProteinferIdsForInputIds(List<Integer> runSearchIds, InputType inputType) {
        return runDao.loadProteinferIdsForInputIds(runSearchIds, inputType);
    }

    @Override
    public List<Integer> loadSearchIdsForProteinferRun(int pinferId) {
        return runDao.loadSearchIdsForProteinferRun(pinferId);
    }
    
    @Override
    public int save(GenericProteinferRun<?> run) {
        return runDao.save(run);
    }

    @Override
    public void update(GenericProteinferRun<?> run) {
        runDao.update(run);
    }

    @Override
    public int getMaxProteinHitCount(int proteinferId) {
        return runDao.getMaxProteinHitCount(proteinferId);
    }
}
