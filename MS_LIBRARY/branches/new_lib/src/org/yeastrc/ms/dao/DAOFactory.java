package org.yeastrc.ms.dao;

import java.io.IOException;
import java.io.Reader;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.ibatis.MsEnzymeDAOImpl;
import org.yeastrc.ms.dao.ibatis.MsExperimentDAOImpl;
import org.yeastrc.ms.dao.ibatis.MsRunDAOImpl;
import org.yeastrc.ms.dao.ibatis.MsScanDAOImpl;
import org.yeastrc.ms.dao.ibatis.MsSearchDAOImpl;
import org.yeastrc.ms.dao.ibatis.MsSearchDatabaseDAOImpl;
import org.yeastrc.ms.dao.ibatis.MsSearchModificationDAOImpl;
import org.yeastrc.ms.dao.ibatis.MsSearchResultDAOImpl;
import org.yeastrc.ms.dao.ibatis.MsSearchResultProteinDAOImpl;
import org.yeastrc.ms.dao.ms2File.MS2ChargeDependentAnalysisDAO;
import org.yeastrc.ms.dao.ms2File.MS2ChargeIndependentAnalysisDAO;
import org.yeastrc.ms.dao.ms2File.MS2HeaderDAO;
import org.yeastrc.ms.dao.ms2File.MS2ScanChargeDAO;
import org.yeastrc.ms.dao.ms2File.ibatis.MS2ChargeDependentAnalysisDAOImpl;
import org.yeastrc.ms.dao.ms2File.ibatis.MS2ChargeIndependentAnalysisDAOImpl;
import org.yeastrc.ms.dao.ms2File.ibatis.MS2HeaderDAOImpl;
import org.yeastrc.ms.dao.ms2File.ibatis.MS2RunDAOImpl;
import org.yeastrc.ms.dao.ms2File.ibatis.MS2ScanChargeDAOImpl;
import org.yeastrc.ms.dao.ms2File.ibatis.MS2ScanDAOImpl;
import org.yeastrc.ms.dao.sqtFile.SQTHeaderDAO;
import org.yeastrc.ms.dao.sqtFile.SQTSearchResultDAO;
import org.yeastrc.ms.dao.sqtFile.SQTSearchScanDAO;
import org.yeastrc.ms.dao.sqtFile.ibatis.SQTHeaderDAOImpl;
import org.yeastrc.ms.dao.sqtFile.ibatis.SQTSearchDAOImpl;
import org.yeastrc.ms.dao.sqtFile.ibatis.SQTSearchResultDAOImpl;
import org.yeastrc.ms.dao.sqtFile.ibatis.SQTSearchScanDAOImpl;
import org.yeastrc.ms.domain.MsRun;
import org.yeastrc.ms.domain.MsRunDb;
import org.yeastrc.ms.domain.MsScan;
import org.yeastrc.ms.domain.MsScanDb;
import org.yeastrc.ms.domain.MsSearch;
import org.yeastrc.ms.domain.MsSearchDb;
import org.yeastrc.ms.domain.MsSearchResult;
import org.yeastrc.ms.domain.MsSearchResultDb;
import org.yeastrc.ms.domain.ms2File.MS2Run;
import org.yeastrc.ms.domain.ms2File.MS2RunDb;
import org.yeastrc.ms.domain.ms2File.MS2Scan;
import org.yeastrc.ms.domain.ms2File.MS2ScanDb;
import org.yeastrc.ms.domain.sqtFile.SQTSearch;
import org.yeastrc.ms.domain.sqtFile.SQTSearchDb;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

public class DAOFactory {

    private static final Logger log = Logger.getLogger(DAOFactory.class);
    
    // initialize the SqlMapClient
    private static SqlMapClient sqlMap;
    
    static {
        Reader reader = null;
        String ibatisConfigFile = "org/yeastrc/ms/sqlmap/SqlMapConfig.xml";
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
    private MsEnzymeDAO enzymeDAO;
    private MsRunDAO<MsRun, MsRunDb> runDAO;
    private MsScanDAO<MsScan, MsScanDb> scanDAO;
    
    // related to MS2 files. 
    private MsRunDAO<MS2Run, MS2RunDb> ms2RunDAO;
    private MsScanDAO<MS2Scan, MS2ScanDb> ms2ScanDAO;
    private MS2ScanChargeDAO ms2FileScanChargeDAO;
    private MS2HeaderDAO ms2FileHeadersDAO;
    private MS2ChargeDependentAnalysisDAO ms2ChgDAnalysisDAO;
    private MS2ChargeIndependentAnalysisDAO ms2ChgIAnalysisDAO;
    
    // DAOs for peptide search related objects
    private MsSearchDAO<MsSearch, MsSearchDb> searchDAO;
    private MsSearchResultDAO <MsSearchResult, MsSearchResultDb> searchResultDAO;
    private MsSearchResultProteinDAO resultProteinDAO;
    private MsSearchModificationDAO modDAO;
    private MsSearchDatabaseDAO seqDbDao;
    
    // DAOs for SQT file related objects
    private SQTSearchResultDAO sqtResultDAO;
    private SQTSearchScanDAO sqtSpectrumDAO;
    private SQTHeaderDAO sqtHeaderDAO;
    private MsSearchDAO<SQTSearch, SQTSearchDb> sqtSearchDAO;
    
    private DAOFactory() {
        
        // Run related
        expDAO = new MsExperimentDAOImpl(sqlMap);
        enzymeDAO = new MsEnzymeDAOImpl(sqlMap);
        scanDAO = new MsScanDAOImpl(sqlMap);
        runDAO = new MsRunDAOImpl(sqlMap, enzymeDAO, scanDAO);
        
        ms2FileHeadersDAO = new MS2HeaderDAOImpl(sqlMap);
        ms2ChgIAnalysisDAO = new MS2ChargeIndependentAnalysisDAOImpl(sqlMap);
        ms2ChgDAnalysisDAO = new MS2ChargeDependentAnalysisDAOImpl(sqlMap);
        ms2FileScanChargeDAO = new MS2ScanChargeDAOImpl(sqlMap, ms2ChgDAnalysisDAO);
        ms2ScanDAO = new MS2ScanDAOImpl(sqlMap, scanDAO, ms2ChgIAnalysisDAO, ms2FileScanChargeDAO);
        ms2RunDAO = new MS2RunDAOImpl(sqlMap, runDAO, ms2FileHeadersDAO, ms2ScanDAO);
        
        
        // Search related
        seqDbDao = new MsSearchDatabaseDAOImpl(sqlMap);
        modDAO = new MsSearchModificationDAOImpl(sqlMap);
        resultProteinDAO = new MsSearchResultProteinDAOImpl(sqlMap);
        searchResultDAO = new MsSearchResultDAOImpl(sqlMap, resultProteinDAO, modDAO);
        searchDAO = new MsSearchDAOImpl(sqlMap, searchResultDAO, seqDbDao, modDAO, enzymeDAO);
        

        sqtResultDAO = new SQTSearchResultDAOImpl(sqlMap, searchResultDAO);
        sqtSpectrumDAO = new SQTSearchScanDAOImpl(sqlMap);
        sqtHeaderDAO = new SQTHeaderDAOImpl(sqlMap);
        sqtSearchDAO = new SQTSearchDAOImpl(sqlMap, searchDAO, sqtHeaderDAO, sqtSpectrumDAO, sqtResultDAO);
        
    }
    
    public static DAOFactory instance() {
        return instance;
    }
    
    public MsExperimentDAO getMsExperimentDAO() {
        return expDAO;
    }
    
    public MsRunDAO<MsRun, MsRunDb> getMsRunDAO() {
        return runDAO;
    }
    
    public MsEnzymeDAO getEnzymeDAO() {
        return enzymeDAO;
    }
    
    public MsScanDAO<MsScan, MsScanDb> getMsScanDAO() {
        return scanDAO;
    }
    
    public MsRunDAO<MS2Run, MS2RunDb> getMS2FileRunDAO() {
        return ms2RunDAO;
    }
    
    public MsScanDAO<MS2Scan, MS2ScanDb> getMS2FileScanDAO() {
        return ms2ScanDAO;
    }
    
    public MS2ScanChargeDAO getMS2FileScanChargeDAO() {
        return ms2FileScanChargeDAO;
    }
    
    public MS2HeaderDAO getMS2FileRunHeadersDAO() {
        return ms2FileHeadersDAO;
    }
    
    public MS2ChargeDependentAnalysisDAO getMs2FileChargeDAnalysisDAO() {
        return ms2ChgDAnalysisDAO;
    }
    
    public MS2ChargeIndependentAnalysisDAO getMs2FileChargeIAnalysisDAO() {
        return ms2ChgIAnalysisDAO;
    }
    
    public MsSearchDAO<MsSearch, MsSearchDb> getMsSearchDAO() {
        return searchDAO;
    }
    
    public MsSearchResultDAO <MsSearchResult, MsSearchResultDb> getMsSearchResultDAO() {
        return searchResultDAO;
    }
    
    public MsSearchResultProteinDAO getMsProteinMatchDAO() {
        return resultProteinDAO;
    }
    
    public MsSearchModificationDAO getMsSearchModDAO() {
        return modDAO;
    }
    
    public MsSearchDatabaseDAO getMsSequenceDatabaseDAO() {
        return seqDbDao;
    }
    
    public SQTHeaderDAO getSqtHeaderDAO() {
        return sqtHeaderDAO;
    }
    
    public SQTSearchResultDAO getSqtResultDAO() {
        return sqtResultDAO;
    }
    
    public MsSearchDAO<SQTSearch, SQTSearchDb> getSqtSearchDAO() {
        return sqtSearchDAO;
    }
    
    public SQTSearchScanDAO getSqtSpectrumDAO() {
        return sqtSpectrumDAO;
    }
}
