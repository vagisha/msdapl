/**
 * ProteinProphetProteinDAO.java
 * @author Vagisha Sharma
 * Jul 29, 2009
 * @version 1.0
 */
package org.yeastrc.ms.dao.protinfer.proteinProphet;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.protinfer.GenericProteinferProteinDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.domain.protinfer.GenericProteinferProtein;
import org.yeastrc.ms.domain.protinfer.ProteinUserValidation;
import org.yeastrc.ms.domain.protinfer.ProteinferProtein;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetProtein;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class ProteinProphetProteinDAO extends BaseSqlMapDAO
    implements GenericProteinferProteinDAO<ProteinProphetProtein> {

    
    private static final String sqlMapNameSpace = "ProteinProphetProtein";
    
    private final ProteinferProteinDAO protDao;
    
    public ProteinProphetProteinDAO(SqlMapClient sqlMap, ProteinferProteinDAO protDao) {
        super(sqlMap);
        this.protDao = protDao;
    }

    @Override
    public int save(GenericProteinferProtein<?> protein) {
        return protDao.save(protein);
    }
    
    public int saveProteinProphetProtein(ProteinProphetProtein protein) {
        int proteinId = save(protein);
        protein.setId(proteinId);
        save(sqlMapNameSpace+".insert", protein); // save entry in the ProteinProphetProtein table
        return proteinId;
    }

    @Override
    public int update(GenericProteinferProtein<?> protein) {
        return protDao.update(protein);
    }
    
    @Override
    public void saveProteinferProteinPeptideMatch(int pinferProteinId,
            int pinferPeptideId) {
        protDao.saveProteinferProteinPeptideMatch(pinferProteinId, pinferPeptideId);
    }

    @Override
    public void updateUserAnnotation(int pinferProteinId, String annotation) {
        protDao.updateUserAnnotation(pinferProteinId, annotation);
    }

    @Override
    public void updateUserValidation(int pinferProteinId,
            ProteinUserValidation validation) {
        protDao.updateUserValidation(pinferProteinId, validation);
    }
    
    @Override
    public void delete(int pinferProteinId) {
        protDao.delete(pinferProteinId);
    }

    @Override
    public List<Integer> getNrseqIdsForRun(int proteinferId) {
        return protDao.getNrseqIdsForRun(proteinferId);
    }

    @Override
    public int getPeptideCountForProtein(int nrseqId, List<Integer> pinferIds) {
        // TODO may need to override -- 
        // 1. check if any of these is a ProteinProphet run
        // 2. only return peptides that have at least one ion as "contributing_evidence"
        return protDao.getPeptideCountForProtein(nrseqId, pinferIds);
    }

    @Override
    public List<String> getPeptidesForProtein(int nrseqId,
            List<Integer> pinferIds) {
        // TODO may need to override -- 
        // 1. check if any of these is a ProteinProphet run
        // 2. only return peptides that have at least one ion as "contributing_evidence"
        return protDao.getPeptidesForProtein(nrseqId, pinferIds);
    }

    @Override
    public int getProteinCount(int proteinferId) {
        return protDao.getProteinCount(proteinferId);
    }

    @Override
    public List<Integer> getProteinIdsForNrseqIds(int proteinferId,
            ArrayList<Integer> nrseqIds) {
        return protDao.getProteinIdsForNrseqIds(proteinferId, nrseqIds);
    }

    @Override
    public List<Integer> getProteinferProteinIds(int proteinferId) {
        return protDao.getProteinferProteinIds(proteinferId);
    }

    @Override
    public ProteinProphetProtein loadProtein(int pinferProteinId) {
        return (ProteinProphetProtein) super.queryForObject(sqlMapNameSpace+".select", pinferProteinId);
    }

    @Override
    public ProteinferProtein loadProtein(int proteinferId, int nrseqProteinId) {
        return protDao.loadProtein(proteinferId, nrseqProteinId);
    }

    @Override
    public List<ProteinProphetProtein> loadProteins(int proteinferId) {
        return super.queryForList(sqlMapNameSpace+".selectProteinsForProteinferRun", proteinferId);
    }

}
