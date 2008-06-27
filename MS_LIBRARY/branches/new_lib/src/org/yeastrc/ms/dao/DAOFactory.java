package org.yeastrc.ms.dao;

import java.io.IOException;
import java.io.Reader;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.ms2File.MS2FileChargeDependentAnalysisDAO;
import org.yeastrc.ms.dao.ms2File.MS2FileHeaderDAO;
import org.yeastrc.ms.dao.ms2File.MS2FileHeaderDAOImpl;
import org.yeastrc.ms.dao.ms2File.MS2FileScanChargeDAO;
import org.yeastrc.ms.dao.ms2File.MS2FileScanChargeDAOImpl;
import org.yeastrc.ms.dao.ms2File.Ms2FileChargeDependentAnalysisDAOImpl;
import org.yeastrc.ms.dao.ms2File.Ms2FileChargeIndependentAnalysisDAO;
import org.yeastrc.ms.dao.ms2File.Ms2FileChargeIndependentAnalysisDAOImpl;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

public class DAOFactory {

    private MsExperimentDAO expDAO;
    private MsRunDAO runDAO;
    private MsScanDAO scanDAO;
    
    // related to MS2 files. 
    private MS2FileScanChargeDAO ms2FileScanChargeDAO;
    private MS2FileHeaderDAO ms2FileHeadersDAO;
    private MS2FileChargeDependentAnalysisDAO ms2ChgDAnalysisDAO;
    private Ms2FileChargeIndependentAnalysisDAO ms2ChgIAnalysisDAO;
    
    
    private static final Logger log = Logger.getLogger(DAOFactory.class);
    
    // initialize the SqlMapClient
    private static SqlMapClient sqlMap;
    
    static {
        Reader reader = null;
        String ibatisConfigFile = "SqlMapConfig.xml";
        try {
            reader = Resources.getResourceAsReader(ibatisConfigFile);
            sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
        }
        catch (IOException e) {
            log.error("Error reading Ibatis config xml: "+ibatisConfigFile, e);
            throw new RuntimeException("Error reading Ibatis config xml: "+ibatisConfigFile, e);
        }
        catch (Exception e) {
            log.error("Error initializing "+DAOFactory.class.getName()+" class: ", e);
            throw new RuntimeException("Error initializing "+DAOFactory.class.getName()+" class: ", e);
        }
    }
    
    private static DAOFactory instance = new DAOFactory();
    
    private DAOFactory() {
        
        expDAO = new MsExperimentDAOImpl(sqlMap);
        runDAO = new MsRunDAOImpl(sqlMap);
        scanDAO = new MsScanDAOImpl(sqlMap);
        
        ms2FileScanChargeDAO = new MS2FileScanChargeDAOImpl(sqlMap);
        ms2FileHeadersDAO = new MS2FileHeaderDAOImpl(sqlMap);
        ms2ChgDAnalysisDAO = new Ms2FileChargeDependentAnalysisDAOImpl(sqlMap);
        ms2ChgIAnalysisDAO = new Ms2FileChargeIndependentAnalysisDAOImpl(sqlMap);
    }
    
    public static DAOFactory instance() {
        return instance;
    }
    
    public MsExperimentDAO getMsExperimentDAO() {
        return expDAO;
    }
    
    public MsRunDAO getMsRunDAO() {
        return runDAO;
    }
    
    public MsScanDAO getMsScanDAO() {
        return scanDAO;
    }
    
    public MS2FileScanChargeDAO getMsScanChargeDAO() {
        return ms2FileScanChargeDAO;
    }
    
    public MS2FileHeaderDAO getMS2FileRunHeadersDAO() {
        return ms2FileHeadersDAO;
    }
    
    public MS2FileChargeDependentAnalysisDAO getMs2FileChargeDAnalysisDAO() {
        return ms2ChgDAnalysisDAO;
    }
    
    public Ms2FileChargeIndependentAnalysisDAO getMs2FileChargeIAnalysisDAO() {
        return ms2ChgIAnalysisDAO;
    }
}
