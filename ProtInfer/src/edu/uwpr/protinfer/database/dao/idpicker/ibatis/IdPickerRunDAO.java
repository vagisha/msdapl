package edu.uwpr.protinfer.database.dao.idpicker.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

import edu.uwpr.protinfer.ProteinInferenceProgram;
import edu.uwpr.protinfer.database.dao.GenericProteinferRun;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferRunDAO;
import edu.uwpr.protinfer.database.dto.BaseProteinferRun;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerInputSummary;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerRun;

public class IdPickerRunDAO extends BaseSqlMapDAO implements GenericProteinferRun<IdPickerInputSummary, IdPickerRun> {

    private static final String sqlMapNameSpace = "IdPickerRun";
    
    private final ProteinferRunDAO runDao;
    
    public IdPickerRunDAO(SqlMapClient sqlMap, ProteinferRunDAO runDao) {
        super(sqlMap);
        this.runDao = runDao;
    }

    @Override
    public IdPickerRun getProteinferRun(int proteinferId) {
        return (IdPickerRun) queryForObject(sqlMapNameSpace+".select", proteinferId);
    }
    
    @Override
    public void delete(int pinferId) {
        runDao.delete(pinferId);
    }

    @Override
    public List<Integer> getProteinferIdsForRunSearches(List<Integer> runSearchIds) {
        return runDao.getProteinferIdsForRunSearches(runSearchIds);
    }

    @Override
    public int save(BaseProteinferRun<?> run) {
        return runDao.save(run);
    }

    /**
     * Saves an entry only in the IDPickerInputSummary table
     * @param run
     */
    public void saveIdPickerRunSummary(IdPickerRun run) {
        super.save(sqlMapNameSpace+".insert", run);
    }
    
    @Override
    public int saveNewProteinferRun(ProteinInferenceProgram program) {
        return runDao.saveNewProteinferRun(program);
    }

    @Override
    public void update(BaseProteinferRun<?> run) {
        runDao.update(run);
    }
}
