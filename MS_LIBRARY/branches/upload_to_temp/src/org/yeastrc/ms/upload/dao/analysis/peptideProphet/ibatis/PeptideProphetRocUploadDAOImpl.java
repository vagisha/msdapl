/**
 * PeptideProphetRocUploadDAOImpl.java
 * @author Vagisha Sharma
 * Jul 31, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.analysis.peptideProphet.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetROC;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetROCPoint;
import org.yeastrc.ms.upload.dao.analysis.peptideProphet.PeptideProphetRocUploadDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class PeptideProphetRocUploadDAOImpl extends BaseSqlMapDAO implements
        PeptideProphetRocUploadDAO {

    private static final String namespace = "PeptideProphetRoc";
    
    public PeptideProphetRocUploadDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    @Override
    public PeptideProphetROC loadRoc(int analysisId) {
        PeptideProphetROC roc = new PeptideProphetROC();
        roc.setSearchAnalysisId(analysisId);
        List<PeptideProphetROCPoint> points = super.queryForList(namespace+".select", analysisId);
        roc.setRocPoints(points);
        return roc;
    }

    @Override
    public void saveRoc(PeptideProphetROC roc) {
        for(PeptideProphetROCPoint point: roc.getRocPoints()) {
            save(namespace+".insert",point);
        }
    }
}
