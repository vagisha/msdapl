package edu.uwpr.protinfer.database.dao.idpicker.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

import edu.uwpr.protinfer.database.dao.GenericProteinferSpectrumMatchDAO;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferSpectrumMatchDAO;
import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerSpectrumMatch;

public class IdPickerSpectrumMatchDAO extends BaseSqlMapDAO implements GenericProteinferSpectrumMatchDAO<IdPickerSpectrumMatch> {

    private static final String sqlMapNameSpace = "IdPickerSpectrumMatch";
    private final ProteinferSpectrumMatchDAO psmDao;
    
    public IdPickerSpectrumMatchDAO(SqlMapClient sqlMap, ProteinferSpectrumMatchDAO psmDao) {
        super(sqlMap);
        this.psmDao = psmDao;
    }

    public int saveSpectrumMatch(IdPickerSpectrumMatch spectrumMatch) {
        int id = psmDao.saveSpectrumMatch(spectrumMatch);
        spectrumMatch.setId(id);
        save(sqlMapNameSpace+".insert", spectrumMatch);
        return id;
    }
    
    public IdPickerSpectrumMatch loadSpectrumMatch(int psmId) {
        return (IdPickerSpectrumMatch) queryForObject(sqlMapNameSpace+".selectSpectrumMatch", psmId);
    }
    
    @Override
    public List<IdPickerSpectrumMatch> loadSpectrumMatchesForIon(int pinferIonId) {
        return super.queryForList(sqlMapNameSpace+".selectMatchesForIon", pinferIonId);
    }
    
    @Override
    public IdPickerSpectrumMatch loadBestSpectrumMatchForIon(int pinferIonId) {
        return (IdPickerSpectrumMatch) queryForObject(sqlMapNameSpace+".selectBestMatchForIon", pinferIonId);
    }
    
    public List<IdPickerSpectrumMatch> loadSpectrumMatchesForPeptide(int pinferPeptideId) {
        return super.queryForList(sqlMapNameSpace+".selectMatchesForPeptide", pinferPeptideId);
    }

    public List<Integer> getSpectrumMatchIdsForPinferRun(int pinferId) {
        return psmDao.getSpectrumMatchIdsForPinferRun(pinferId);
    }

    public int getSpectrumCountForPinferRun(int pinferId) {
        return psmDao.getSpectrumCountForPinferRun(pinferId);
    }
    
    public int getMaxSpectrumCountForPinferRunProtein(int pinferId) {
       return psmDao.getMaxSpectrumCountForPinferRunProtein(pinferId);
    }
    
    public int getMinSpectrumCountForPinferRunProtein(int pinferId) {
        return psmDao.getMinSpectrumCountForPinferRunProtein(pinferId);
    }
    
    @Override
    public List<Integer> getSpectrumMatchIdsForPinferRunInput(int pinferId, int inputId) {
        return psmDao.getSpectrumMatchIdsForPinferRunInput(pinferId, inputId);
    }

    @Override
    public int update(ProteinferSpectrumMatch psm) {
        throw new UnsupportedOperationException("IdPickerSpectrumMatchDAO does not support update");
    }
}
