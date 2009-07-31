/**
 * PeptideProphetResultDAOImpl.java
 * @author Vagisha Sharma
 * Jul 30, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.analysis.peptideProphet.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResult;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResultDataWId;
import org.yeastrc.ms.upload.dao.analysis.peptideProphet.PeptideProphetResultUploadDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class PeptideProphetResultUploadDAOImpl extends BaseSqlMapDAO implements
        PeptideProphetResultUploadDAO {

    private static final String namespace = "PeptideProphetResult"; 
    
    public PeptideProphetResultUploadDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    @Override
    public PeptideProphetResult load(int resultId) {
        return (PeptideProphetResult) queryForObject(namespace+".select", resultId);
    }

    @Override
    public void saveAllPeptideProphetResultData(
            List<PeptideProphetResultDataWId> dataList) {
        
        if(dataList == null || dataList.size() == 0)
            return;
        StringBuilder values = new StringBuilder();
        for ( PeptideProphetResultDataWId data: dataList) {
            values.append(",(");
            values.append(data.getResultId() == 0 ? "NULL" : data.getResultId());
            values.append(",");
            values.append(data.getRunSearchAnalysisId() == 0 ? "NULL" : data.getRunSearchAnalysisId());
            values.append(",");
            double probability = data.getProbability();
            values.append(probability == -1.0 ? "NULL" : probability);
            values.append(",");
            double fVal = data.getfVal();
            values.append(fVal == -1.0 ? "NULL" : fVal);
            values.append(",");
            int ntt = data.getNumTrypticTermini();
            values.append(ntt == -1 ? "NULL" : ntt);
            values.append(",");
            int nmc = data.getNumMissedCleavages();
            values.append(nmc == -1 ? "NULL" : nmc);
            values.append(",");
            values.append(data.getMassDifference());
            values.append(",");
            values.append(data.getAllNttProb() == null ? "NULL" : data.getProbabilityNet_0());
            values.append(",");
            values.append(data.getAllNttProb() == null ? "NULL" : data.getProbabilityNet_1());
            values.append(",");
            values.append(data.getAllNttProb() == null ? "NULL" : data.getProbabilityNet_2());
            values.append(")");
        }
        values.deleteCharAt(0);
        save(namespace+".insertAll", values.toString());
    }

}
