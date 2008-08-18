package org.yeastrc.ms.dao;

import java.io.IOException;
import java.io.Reader;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.general.MsDeletionDAO;
import org.yeastrc.ms.dao.general.MsEnzymeDAO;
import org.yeastrc.ms.dao.general.ibatis.MsDeletionDAOImpl;
import org.yeastrc.ms.dao.general.ibatis.MsEnzymeDAOImpl;
import org.yeastrc.ms.dao.ibatis.MsExperimentDAOImpl;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.run.ibatis.MsRunDAOImpl;
import org.yeastrc.ms.dao.run.ibatis.MsScanDAOImpl;
import org.yeastrc.ms.dao.run.ms2file.MS2ChargeDependentAnalysisDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ChargeIndependentAnalysisDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2HeaderDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ScanChargeDAO;
import org.yeastrc.ms.dao.run.ms2file.ibatis.MS2ChargeDependentAnalysisDAOImpl;
import org.yeastrc.ms.dao.run.ms2file.ibatis.MS2ChargeIndependentAnalysisDAOImpl;
import org.yeastrc.ms.dao.run.ms2file.ibatis.MS2HeaderDAOImpl;
import org.yeastrc.ms.dao.run.ms2file.ibatis.MS2RunDAOImpl;
import org.yeastrc.ms.dao.run.ms2file.ibatis.MS2ScanChargeDAOImpl;
import org.yeastrc.ms.dao.run.ms2file.ibatis.MS2ScanDAOImpl;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDatabaseDAO;
import org.yeastrc.ms.dao.search.MsSearchModificationDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.dao.search.ibatis.MsSearchDAOImpl;
import org.yeastrc.ms.dao.search.ibatis.MsSearchDatabaseDAOImpl;
import org.yeastrc.ms.dao.search.ibatis.MsSearchModificationDAOImpl;
import org.yeastrc.ms.dao.search.ibatis.MsSearchResultDAOImpl;
import org.yeastrc.ms.dao.search.ibatis.MsSearchResultProteinDAOImpl;
import org.yeastrc.ms.dao.search.sequest.SQTSearchResultDAO;
import org.yeastrc.ms.dao.search.sequest.ibatis.SQTSearchDAOImpl;
import org.yeastrc.ms.dao.search.sequest.ibatis.SQTSearchResultDAOImpl;
import org.yeastrc.ms.dao.search.sqtfile.SQTHeaderDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTSearchScanDAO;
import org.yeastrc.ms.dao.search.sqtfile.ibatis.SQTHeaderDAOImpl;
import org.yeastrc.ms.dao.search.sqtfile.ibatis.SQTSearchScanDAOImpl;
import org.yeastrc.ms.domain.run.MsRun;
import org.yeastrc.ms.domain.run.MsRunDb;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.MsScanDb;
import org.yeastrc.ms.domain.run.ms2file.MS2Run;
import org.yeastrc.ms.domain.run.ms2file.MS2RunDb;
import org.yeastrc.ms.domain.run.ms2file.MS2Scan;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanDb;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsRunSearchDb;
import org.yeastrc.ms.domain.search.MsRunSearchResult;
import org.yeastrc.ms.domain.search.MsRunSearchResultDb;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearch;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearchDb;

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
    private MsSearchDAO<MsRunSearch, MsRunSearchDb> searchDAO;
    private MsSearchResultDAO <MsRunSearchResult, MsRunSearchResultDb> searchResultDAO;
    private MsSearchResultProteinDAO resultProteinDAO;
    private MsSearchModificationDAO modDAO;
    private MsSearchDatabaseDAO seqDbDao;
    
    // DAOs for SQT file related objects
    private SQTSearchResultDAO sqtResultDAO;
    private SQTSearchScanDAO sqtSpectrumDAO;
    private SQTHeaderDAO sqtHeaderDAO;
    private MsSearchDAO<SQTRunSearch, SQTRunSearchDb> sqtSearchDAO;
    
    // DAO for deleting experiments or searches
    private MsDeletionDAO deletionDAO;
    
    private DAOFactory() {
        
        // Run related
        enzymeDAO = new MsEnzymeDAOImpl(sqlMap);
        scanDAO = new MsScanDAOImpl(sqlMap);
        runDAO = new MsRunDAOImpl(sqlMap, enzymeDAO);
        
        ms2FileHeadersDAO = new MS2HeaderDAOImpl(sqlMap);
        ms2ChgIAnalysisDAO = new MS2ChargeIndependentAnalysisDAOImpl(sqlMap);
        ms2ChgDAnalysisDAO = new MS2ChargeDependentAnalysisDAOImpl(sqlMap);
        ms2FileScanChargeDAO = new MS2ScanChargeDAOImpl(sqlMap, ms2ChgDAnalysisDAO);
        ms2ScanDAO = new MS2ScanDAOImpl(sqlMap, scanDAO, ms2ChgIAnalysisDAO, ms2FileScanChargeDAO);
        ms2RunDAO = new MS2RunDAOImpl(sqlMap, runDAO);
        
        
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
        
        deletionDAO = new MsDeletionDAOImpl(sqlMap);
    }
    
    public static DAOFactory instance() {
        return instance;
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
    
    public MsSearchDAO<MsRunSearch, MsRunSearchDb> getMsSearchDAO() {
        return searchDAO;
    }
    
    public MsSearchResultDAO <MsRunSearchResult, MsRunSearchResultDb> getMsSearchResultDAO() {
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
    
    public MsSearchDAO<SQTRunSearch, SQTRunSearchDb> getSqtSearchDAO() {
        return sqtSearchDAO;
    }
    
    public SQTSearchScanDAO getSqtSpectrumDAO() {
        return sqtSpectrumDAO;
    }
    
    public MsDeletionDAO getDeletionDAO() {
        return deletionDAO;
    }
}
