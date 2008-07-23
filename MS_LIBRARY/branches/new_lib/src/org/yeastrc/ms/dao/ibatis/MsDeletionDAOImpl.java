package org.yeastrc.ms.dao.ibatis;

import org.yeastrc.ms.dao.MsDeletionDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

public class MsDeletionDAOImpl extends BaseSqlMapDAO implements MsDeletionDAO {

    public MsDeletionDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    @Override
    public void deleteExperiment(int experimentId) {
        
        // delete charge independent analysis
        
        // delete charge dependent analysis
        
        // delete scan charges
        
        // delete scan data
        
        // delete scans
        
        // delete run enzymes
        
        // delete MS2 file headers
        
        
        // get a list of searchIds for the runs in this experiment
        
        // delete the runs
        
        // for each search id in the 
        
        delete(statementName, parameterObject)
    }

    @Override
    public void deleteSearch(int searchId) {

    }

}
