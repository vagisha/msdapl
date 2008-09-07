package org.yeastrc.ms.dao;

import java.io.IOException;
import java.io.Reader;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.general.MsEnzymeDAO;
import org.yeastrc.ms.dao.general.ibatis.MsEnzymeDAOImpl;
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
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDatabaseDAO;
import org.yeastrc.ms.dao.search.MsSearchModificationDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.dao.search.ibatis.MsRunSearchDAOImpl;
import org.yeastrc.ms.dao.search.ibatis.MsSearchDAOImpl;
import org.yeastrc.ms.dao.search.ibatis.MsSearchDatabaseDAOImpl;
import org.yeastrc.ms.dao.search.ibatis.MsSearchModificationDAOImpl;
import org.yeastrc.ms.dao.search.ibatis.MsSearchResultDAOImpl;
import org.yeastrc.ms.dao.search.ibatis.MsSearchResultProteinDAOImpl;
import org.yeastrc.ms.dao.search.prolucid.ProlucidSearchDAO;
import org.yeastrc.ms.dao.search.prolucid.ProlucidSearchResultDAO;
import org.yeastrc.ms.dao.search.prolucid.ibatis.ProlucidSearchDAOImpl;
import org.yeastrc.ms.dao.search.prolucid.ibatis.ProlucidSearchResultDAOImpl;
import org.yeastrc.ms.dao.search.sequest.SequestSearchDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.dao.search.sequest.ibatis.SequestSearchDAOImpl;
import org.yeastrc.ms.dao.search.sequest.ibatis.SequestSearchResultDAOImpl;
import org.yeastrc.ms.dao.search.sqtfile.SQTHeaderDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTRunSearchDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTSearchScanDAO;
import org.yeastrc.ms.dao.search.sqtfile.ibatis.SQTHeaderDAOImpl;
import org.yeastrc.ms.dao.search.sqtfile.ibatis.SQTRunSearchDAOImpl;
import org.yeastrc.ms.dao.search.sqtfile.ibatis.SQTSearchScanDAOImpl;
import org.yeastrc.ms.domain.run.MsRun;
import org.yeastrc.ms.domain.run.MsRunDb;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.MsScanDb;
import org.yeastrc.ms.domain.run.ms2file.MS2Run;
import org.yeastrc.ms.domain.run.ms2file.MS2RunDb;
import org.yeastrc.ms.domain.run.ms2file.MS2Scan;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanDb;

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
    
    // DAOs for enzyme related objects
    private MsEnzymeDAO enzymeDAO;
    
    // DAOs for run related objects
    private MsRunDAO<MsRun, MsRunDb> runDAO;
    private MsScanDAO<MsScan, MsScanDb> scanDAO;
    
    // DAOs related to MS2 files. 
    private MsRunDAO<MS2Run, MS2RunDb> ms2RunDAO;
    private MsScanDAO<MS2Scan, MS2ScanDb> ms2ScanDAO;
    private MS2ScanChargeDAO ms2FileScanChargeDAO;
    private MS2HeaderDAO ms2FileHeadersDAO;
    private MS2ChargeDependentAnalysisDAO ms2ChgDAnalysisDAO;
    private MS2ChargeIndependentAnalysisDAO ms2ChgIAnalysisDAO;
    
    // DAOs for search related objects
    private MsSearchDAO searchDAO;
    private MsRunSearchDAO runSearchDAO;
    private MsSearchResultDAO searchResultDAO;
    private MsSearchResultProteinDAO resultProteinDAO;
    private MsSearchModificationDAO modDAO;
    private MsSearchDatabaseDAO seqDbDao;
    
    // DAOs for SQT file related objects
    private SQTSearchScanDAO sqtSpectrumDAO;
    private SQTHeaderDAO sqtHeaderDAO;
    private SQTRunSearchDAO sqtRunSearchDAO;
    
    // DAOs for Sequest related objects
    private SequestSearchResultDAO sequestResultDAO;
    private SequestSearchDAO sequestSearchDAO;
    
    // DAOs for Prolucid related objects
    private ProlucidSearchResultDAO prolucidResultDAO;
    private ProlucidSearchDAO prolucidSearchDAO;
    
    
    private DAOFactory() {
        
        // Enzyme related
        enzymeDAO = new MsEnzymeDAOImpl(sqlMap);
        
        // Run related
        scanDAO = new MsScanDAOImpl(sqlMap);
        runDAO = new MsRunDAOImpl(sqlMap, enzymeDAO);
        
        // ms2 file related
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
        runSearchDAO = new MsRunSearchDAOImpl(sqlMap);
        searchDAO = new MsSearchDAOImpl(sqlMap, seqDbDao, modDAO, enzymeDAO);
        
        // sqt file related
        sqtSpectrumDAO = new SQTSearchScanDAOImpl(sqlMap);
        sqtHeaderDAO = new SQTHeaderDAOImpl(sqlMap);
        sqtRunSearchDAO = new SQTRunSearchDAOImpl(sqlMap, runSearchDAO, sqtHeaderDAO);
        
        // sequest search related
        sequestResultDAO = new SequestSearchResultDAOImpl(sqlMap, searchResultDAO);
        sequestSearchDAO = new SequestSearchDAOImpl(sqlMap, searchDAO);
        
        // prolucid search related
        prolucidResultDAO = new ProlucidSearchResultDAOImpl(sqlMap, searchResultDAO);
        prolucidSearchDAO = new ProlucidSearchDAOImpl(sqlMap, searchDAO);
        
    }
    
    public static DAOFactory instance() {
        return instance;
    }
    
    //-------------------------------------------------------------------------------------------
    // ENZYME related
    //-------------------------------------------------------------------------------------------
    public MsEnzymeDAO getEnzymeDAO() {
        return enzymeDAO;
    }
    
    //-------------------------------------------------------------------------------------------
    // RUN related
    //-------------------------------------------------------------------------------------------
    public MsRunDAO<MsRun, MsRunDb> getMsRunDAO() {
        return runDAO;
    }
    
    public MsScanDAO<MsScan, MsScanDb> getMsScanDAO() {
        return scanDAO;
    }
    
    //-------------------------------------------------------------------------------------------
    // MS2 RUN related
    //-------------------------------------------------------------------------------------------
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
    
    //-------------------------------------------------------------------------------------------
    // SEARCH related
    //-------------------------------------------------------------------------------------------
    public MsSearchDAO getMsSearchDAO() {
        return searchDAO;
    }
    
    public MsRunSearchDAO getMsRunSearchDAO() {
        return runSearchDAO;
    }
    
    public MsSearchResultDAO getMsSearchResultDAO() {
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
    
    //-------------------------------------------------------------------------------------------
    // SQT file related
    //-------------------------------------------------------------------------------------------
    public SQTHeaderDAO getSqtHeaderDAO() {
        return sqtHeaderDAO;
    }
    
    public SQTRunSearchDAO getSqtRunSearchDAO() {
        return sqtRunSearchDAO;
    }
    
    public SQTSearchScanDAO getSqtSpectrumDAO() {
        return sqtSpectrumDAO;
    }
    
    //-------------------------------------------------------------------------------------------
    // Sequest SEARCH related
    //-------------------------------------------------------------------------------------------
    public SequestSearchResultDAO getSequestResultDAO() {
        return sequestResultDAO;
    }
    
    public SequestSearchDAO getSequestSearchDAO() {
        return sequestSearchDAO;
    }
    
    //-------------------------------------------------------------------------------------------
    // ProLuCID SEARCH related
    //-------------------------------------------------------------------------------------------
    public ProlucidSearchResultDAO getProlucidResultDAO() {
        return prolucidResultDAO;
    }
    
    public ProlucidSearchDAO getProlucidSearchDAO() {
        return prolucidSearchDAO;
    }
    
}
