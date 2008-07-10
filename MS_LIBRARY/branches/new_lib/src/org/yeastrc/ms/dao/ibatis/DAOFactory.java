package org.yeastrc.ms.dao.ibatis;

import java.io.IOException;
import java.io.Reader;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.MsDigestionEnzymeDAO;
import org.yeastrc.ms.dao.MsExperimentDAO;
import org.yeastrc.ms.dao.MsPeptideSearchDAO;
import org.yeastrc.ms.dao.MsPeptideSearchModDAO;
import org.yeastrc.ms.dao.MsPeptideSearchResultDAO;
import org.yeastrc.ms.dao.MsProteinMatchDAO;
import org.yeastrc.ms.dao.MsRunDAO;
import org.yeastrc.ms.dao.MsScanDAO;
import org.yeastrc.ms.dao.MsSequenceDatabaseDAO;
import org.yeastrc.ms.dao.ms2File.MS2FileChargeDependentAnalysisDAO;
import org.yeastrc.ms.dao.ms2File.MS2FileChargeIndependentAnalysisDAO;
import org.yeastrc.ms.dao.ms2File.MS2FileHeaderDAO;
import org.yeastrc.ms.dao.ms2File.MS2FileScanChargeDAO;
import org.yeastrc.ms.dao.ms2File.ibatis.MS2FileChargeDependentAnalysisDAOImpl;
import org.yeastrc.ms.dao.ms2File.ibatis.MS2FileChargeIndependentAnalysisDAOImpl;
import org.yeastrc.ms.dao.ms2File.ibatis.MS2FileHeaderDAOImpl;
import org.yeastrc.ms.dao.ms2File.ibatis.MS2FileRunDAOImpl;
import org.yeastrc.ms.dao.ms2File.ibatis.MS2FileScanChargeDAOImpl;
import org.yeastrc.ms.dao.ms2File.ibatis.MS2FileScanDAOImpl;
import org.yeastrc.ms.dao.sqtFile.SQTSearchHeaderDAO;
import org.yeastrc.ms.dao.sqtFile.SQTSpectrumDataDAO;
import org.yeastrc.ms.dao.sqtFile.ibatis.SQTPeptideSearchDAOImpl;
import org.yeastrc.ms.dao.sqtFile.ibatis.SQTSearchHeaderDAOImpl;
import org.yeastrc.ms.dao.sqtFile.ibatis.SQTSearchResultDAOImpl;
import org.yeastrc.ms.dao.sqtFile.ibatis.SQTSpectrumDataDAOImpl;
import org.yeastrc.ms.domain.IMsRun;
import org.yeastrc.ms.domain.IMsScan;
import org.yeastrc.ms.domain.IMsSearch;
import org.yeastrc.ms.domain.IMsSearchResult;
import org.yeastrc.ms.domain.db.MsPeptideSearch;
import org.yeastrc.ms.domain.db.MsPeptideSearchResult;
import org.yeastrc.ms.domain.db.MsRun;
import org.yeastrc.ms.domain.db.MsScan;
import org.yeastrc.ms.domain.ms2File.IMS2Run;
import org.yeastrc.ms.domain.ms2File.IMS2Scan;
import org.yeastrc.ms.domain.ms2File.db.MS2FileRun;
import org.yeastrc.ms.domain.ms2File.db.MS2FileScan;
import org.yeastrc.ms.domain.sqtFile.ISQTSearch;
import org.yeastrc.ms.domain.sqtFile.ISQTSearchResult;
import org.yeastrc.ms.domain.sqtFile.db.SQTPeptideSearch;
import org.yeastrc.ms.domain.sqtFile.db.SQTSearchResult;

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
    private MsRunDAO<IMsRun, MsRun> runDAO;
    private MsScanDAO<IMsScan, MsScan> scanDAO;
    
    // related to MS2 files. 
    private MsRunDAO<IMS2Run, MS2FileRun> ms2RunDAO;
    private MsScanDAO<IMS2Scan, MS2FileScan> ms2ScanDAO;
    private MS2FileScanChargeDAO ms2FileScanChargeDAO;
    private MS2FileHeaderDAO ms2FileHeadersDAO;
    private MS2FileChargeDependentAnalysisDAO ms2ChgDAnalysisDAO;
    private MS2FileChargeIndependentAnalysisDAO ms2ChgIAnalysisDAO;
    
    // DAOs for peptide search related objects
    private MsPeptideSearchDAO<IMsSearch, MsPeptideSearch> searchDAO;
    private MsPeptideSearchResultDAO <IMsSearchResult, MsPeptideSearchResult> searchResultDAO;
    private MsProteinMatchDAO resultProteinDAO;
    private MsPeptideSearchModDAO modDAO;
    private MsSequenceDatabaseDAO seqDbDao;
    
    // DAOs for SQT file related objects
    private MsPeptideSearchResultDAO <ISQTSearchResult, SQTSearchResult> sqtResultDAO;
    private SQTSpectrumDataDAO sqtSpectrumDAO;
    private SQTSearchHeaderDAO sqtHeaderDAO;
    private MsPeptideSearchDAO<ISQTSearch, SQTPeptideSearch> sqtSearchDAO;
    
    private DAOFactory() {
        
        // Run related
        expDAO = new MsExperimentDAOImpl(sqlMap);
        enzymeDAO = new MsDigestionEnzymeDAOImpl(sqlMap);
        scanDAO = new MsScanDAOImpl(sqlMap);
        runDAO = new MsRunDAOImpl(sqlMap, enzymeDAO, scanDAO);
        
        ms2FileHeadersDAO = new MS2FileHeaderDAOImpl(sqlMap);
        ms2ChgIAnalysisDAO = new MS2FileChargeIndependentAnalysisDAOImpl(sqlMap);
        ms2ChgDAnalysisDAO = new MS2FileChargeDependentAnalysisDAOImpl(sqlMap);
        ms2FileScanChargeDAO = new MS2FileScanChargeDAOImpl(sqlMap, ms2ChgDAnalysisDAO);
        ms2ScanDAO = new MS2FileScanDAOImpl(sqlMap, scanDAO, ms2ChgIAnalysisDAO, ms2FileScanChargeDAO);
        ms2RunDAO = new MS2FileRunDAOImpl(sqlMap, runDAO, ms2FileHeadersDAO, ms2ScanDAO);
        
        
        // Search related
        seqDbDao = new MsSequenceDatabaseDAOImpl(sqlMap);
        modDAO = new MsPeptideSearchModDAOImpl(sqlMap);
        resultProteinDAO = new MsProteinMatchDAOImpl(sqlMap);
        searchResultDAO = new MsPeptideSearchResultDAOImpl(sqlMap, resultProteinDAO, modDAO);
        searchDAO = new MsPeptideSearchDAOImpl(sqlMap, searchResultDAO, seqDbDao, modDAO);
        

        sqtResultDAO = new SQTSearchResultDAOImpl(sqlMap, searchResultDAO);
        sqtSpectrumDAO = new SQTSpectrumDataDAOImpl(sqlMap);
        sqtHeaderDAO = new SQTSearchHeaderDAOImpl(sqlMap);
        sqtSearchDAO = new SQTPeptideSearchDAOImpl(sqlMap, searchDAO, sqtHeaderDAO, sqtSpectrumDAO);
        
    }
    
    public static DAOFactory instance() {
        return instance;
    }
    
    public MsExperimentDAO getMsExperimentDAO() {
        return expDAO;
    }
    
    public MsRunDAO<IMsRun, MsRun> getMsRunDAO() {
        return runDAO;
    }
    
    public MsDigestionEnzymeDAO getEnzymeDAO() {
        return enzymeDAO;
    }
    
    public MsScanDAO<IMsScan, MsScan> getMsScanDAO() {
        return scanDAO;
    }
    
    public MsRunDAO<IMS2Run, MS2FileRun> getMS2FileRunDAO() {
        return ms2RunDAO;
    }
    
    public MsScanDAO<IMS2Scan, MS2FileScan> getMS2FileScanDAO() {
        return ms2ScanDAO;
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
    
    public MsPeptideSearchDAO<IMsSearch, MsPeptideSearch> getMsPeptideSearchDAO() {
        return searchDAO;
    }
    
    public MsPeptideSearchResultDAO <IMsSearchResult, MsPeptideSearchResult> getMsPeptideSearchResultDAO() {
        return searchResultDAO;
    }
    
    public MsProteinMatchDAO getMsProteinMatchDAO() {
        return resultProteinDAO;
    }
    
    public MsPeptideSearchModDAO getMsSearchModDAO() {
        return modDAO;
    }
    
    public MsSequenceDatabaseDAO getMsSequenceDatabaseDAO() {
        return seqDbDao;
    }
    
    public SQTSearchHeaderDAO getSqtHeaderDAO() {
        return sqtHeaderDAO;
    }
    
    public MsPeptideSearchResultDAO<ISQTSearchResult, SQTSearchResult> getSqtResultDAO() {
        return sqtResultDAO;
    }
    
    public MsPeptideSearchDAO<ISQTSearch, SQTPeptideSearch> getSqtSearchDAO() {
        return sqtSearchDAO;
    }
    
    public SQTSpectrumDataDAO getSqtSpectrumDAO() {
        return sqtSpectrumDAO;
    }
}
