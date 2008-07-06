package org.yeastrc.ms.dao;

import java.io.IOException;
import java.io.Reader;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.ms2File.MS2FileChargeDependentAnalysisDAO;
import org.yeastrc.ms.dao.ms2File.MS2FileChargeDependentAnalysisDAOImpl;
import org.yeastrc.ms.dao.ms2File.MS2FileChargeIndependentAnalysisDAO;
import org.yeastrc.ms.dao.ms2File.MS2FileChargeIndependentAnalysisDAOImpl;
import org.yeastrc.ms.dao.ms2File.MS2FileHeaderDAO;
import org.yeastrc.ms.dao.ms2File.MS2FileHeaderDAOImpl;
import org.yeastrc.ms.dao.ms2File.MS2FileScanChargeDAO;
import org.yeastrc.ms.dao.ms2File.MS2FileScanChargeDAOImpl;
import org.yeastrc.ms.dao.sqtFile.SQTPeptideSearchDAO;
import org.yeastrc.ms.dao.sqtFile.SQTPeptideSearchDAOImpl;
import org.yeastrc.ms.dao.sqtFile.SQTSearchHeaderDAO;
import org.yeastrc.ms.dao.sqtFile.SQTSearchHeaderDAOImpl;
import org.yeastrc.ms.dao.sqtFile.SQTSearchResultDAO;
import org.yeastrc.ms.dao.sqtFile.SQTSearchResultDAOImpl;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

public class DAOFactory {

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
        System.out.println("Loaded Ibatis SQL map config");
    }
    
    private static DAOFactory instance = new DAOFactory();
    
    // DAOs for run related objects
    private MsExperimentDAO expDAO;
    private MsDigestionEnzymeDAO enzymeDAO;
    private MsRunDAO runDAO;
    private MsScanDAO scanDAO;
    
    // related to MS2 files. 
    private MS2FileScanChargeDAO ms2FileScanChargeDAO;
    private MS2FileHeaderDAO ms2FileHeadersDAO;
    private MS2FileChargeDependentAnalysisDAO ms2ChgDAnalysisDAO;
    private MS2FileChargeIndependentAnalysisDAO ms2ChgIAnalysisDAO;
    
    // DAOs for peptide search related objects
    private MsPeptideSearchDAO searchDAO;
    private MsPeptideSearchResultDAO searchResultDAO;
    private MsProteinMatchDAO resultProteinDAO;
    private MsSearchModDAO modDAO;
    private MsSequenceDatabaseDAO seqDbDao;
    
    // DAOs for SQT file related objects
    private SQTSearchHeaderDAO sqtHeaderDAO;
    private SQTSearchResultDAO sqtResultDAO;
    private SQTPeptideSearchDAO sqtSearchDAO;
    
    private DAOFactory() {
        
        expDAO = new MsExperimentDAOImpl(sqlMap);
        enzymeDAO = new MsDigestionEnzymeDAOImpl(sqlMap);
        runDAO = new MsRunDAOImpl(sqlMap);
        scanDAO = new MsScanDAOImpl(sqlMap);
        
        ms2FileScanChargeDAO = new MS2FileScanChargeDAOImpl(sqlMap);
        ms2FileHeadersDAO = new MS2FileHeaderDAOImpl(sqlMap);
        ms2ChgDAnalysisDAO = new MS2FileChargeDependentAnalysisDAOImpl(sqlMap);
        ms2ChgIAnalysisDAO = new MS2FileChargeIndependentAnalysisDAOImpl(sqlMap);
        
        searchDAO = new MsPeptideSearchDAOImpl(sqlMap);
        searchResultDAO = new MsPeptideSearchResultDAOImpl(sqlMap);
        resultProteinDAO = new MsProteinMatchDAOImpl(sqlMap);
        modDAO = new MsSearchModDAOImpl(sqlMap);
        seqDbDao = new MsSequenceDatabaseDAOImpl(sqlMap);
        
        sqtHeaderDAO = new SQTSearchHeaderDAOImpl(sqlMap);
        sqtResultDAO = new SQTSearchResultDAOImpl(sqlMap);
        sqtSearchDAO = new SQTPeptideSearchDAOImpl(sqlMap);
        
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
    
    public MsDigestionEnzymeDAO getEnzymeDAO() {
        return enzymeDAO;
    }
    
    public MsScanDAO getMsScanDAO() {
        return scanDAO;
    }
    
    public MS2FileScanChargeDAO getMS2FileScanChargeDAO() {
        return ms2FileScanChargeDAO;
    }
    
    public MS2FileHeaderDAO getMS2FileRunHeadersDAO() {
        return ms2FileHeadersDAO;
    }
    
    public MS2FileChargeDependentAnalysisDAO getMs2FileChargeDAnalysisDAO() {
        return ms2ChgDAnalysisDAO;
    }
    
    public MS2FileChargeIndependentAnalysisDAO getMs2FileChargeIAnalysisDAO() {
        return ms2ChgIAnalysisDAO;
    }
    
    public MsPeptideSearchDAO getMsPeptideSearchDAO() {
        return searchDAO;
    }
    
    public MsPeptideSearchResultDAO getMsPeptideSearchResultDAO() {
        return searchResultDAO;
    }
    
    public MsProteinMatchDAO getMsProteinMatchDAO() {
        return resultProteinDAO;
    }
    
    public MsSearchModDAO getMsSearchModDAO() {
        return modDAO;
    }
    
    public MsSequenceDatabaseDAO getMsSequenceDatabaseDAO() {
        return seqDbDao;
    }
    
    public SQTSearchHeaderDAO getSqtHeaderDAO() {
        return sqtHeaderDAO;
    }
    
    public SQTSearchResultDAO getSqtResultDAO() {
        return sqtResultDAO;
    }
    
    public SQTPeptideSearchDAO getSqtSearchDAO() {
        return sqtSearchDAO;
    }
}
