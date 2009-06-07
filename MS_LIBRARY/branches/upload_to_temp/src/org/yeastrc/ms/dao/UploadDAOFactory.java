/**
 * UploadDAOFactory.java
 * @author Vagisha Sharma
 * May 5, 2009
 * @version 1.0
 */
package org.yeastrc.ms.dao;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.MsSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.ibatis.MsRunSearchAnalysisDAOImpl;
import org.yeastrc.ms.dao.analysis.ibatis.MsSearchAnalysisDAOImpl;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorParamsDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.dao.analysis.percolator.ibatis.PercolatorParamsDAOImpl;
import org.yeastrc.ms.dao.analysis.percolator.ibatis.PercolatorResultDAOImpl;
import org.yeastrc.ms.dao.general.MsEnzymeDAO;
import org.yeastrc.ms.dao.general.MsExperimentDAO;
import org.yeastrc.ms.dao.general.ibatis.MsEnzymeDAOImpl;
import org.yeastrc.ms.dao.general.ibatis.MsExperimentDAOImpl;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.run.ibatis.MsRunDAOImpl;
import org.yeastrc.ms.dao.run.ibatis.MsScanDAOImpl;
import org.yeastrc.ms.dao.run.ms2file.MS2ChargeDependentAnalysisDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ChargeIndependentAnalysisDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2HeaderDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2RunDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ScanChargeDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ScanDAO;
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
import org.yeastrc.ms.service.MsDataUploadProperties;
import org.yeastrc.ms.upload.dao.analysis.MsRunSearchAnalysisUploadDAOImpl;
import org.yeastrc.ms.upload.dao.analysis.MsSearchAnalysisUploadDAOImpl;
import org.yeastrc.ms.upload.dao.analysis.percolator.PercolatorParamsUploadDAOImpl;
import org.yeastrc.ms.upload.dao.analysis.percolator.PercolatorResultUploadDAOImpl;
import org.yeastrc.ms.upload.dao.general.MsEnzymeUploadDAOImpl;
import org.yeastrc.ms.upload.dao.general.MsExperimentUploadDAOImpl;
import org.yeastrc.ms.upload.dao.run.MsRunUploadDAOImpl;
import org.yeastrc.ms.upload.dao.run.MsScanUploadDAOImpl;
import org.yeastrc.ms.upload.dao.run.ms2file.MS2RunUploadDAOImpl;
import org.yeastrc.ms.upload.dao.run.ms2file.MS2ScanChargeUploadDAOImpl;
import org.yeastrc.ms.upload.dao.run.ms2file.MS2ScanUploadDAOImpl;
import org.yeastrc.ms.upload.dao.search.MsRunSearchUploadDAOImpl;
import org.yeastrc.ms.upload.dao.search.MsSearchDatabaseUploadDAOImpl;
import org.yeastrc.ms.upload.dao.search.MsSearchModificationUploadDAOImpl;
import org.yeastrc.ms.upload.dao.search.MsSearchResultProteinUploadDAOImpl;
import org.yeastrc.ms.upload.dao.search.MsSearchResultUploadDAOImpl;
import org.yeastrc.ms.upload.dao.search.MsSearchUploadDAOImpl;
import org.yeastrc.ms.upload.dao.search.prolucid.ProlucidSearchResultUploadDAOImpl;
import org.yeastrc.ms.upload.dao.search.prolucid.ProlucidSearchUploadDAOImpl;
import org.yeastrc.ms.upload.dao.search.sequest.SequestSearchResultUploadDAOImpl;
import org.yeastrc.ms.upload.dao.search.sequest.SequestSearchUploadDAOImpl;
import org.yeastrc.ms.upload.dao.search.sqtfile.SQTHeaderUploadDAOImpl;
import org.yeastrc.ms.upload.dao.search.sqtfile.SQTRunSearchUploadDAOImpl;
import org.yeastrc.ms.upload.dao.search.sqtfile.SQTSearchScanUploadDAOImpl;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 * 
 */
public class UploadDAOFactory {

    private static final Logger log = Logger.getLogger(DAOFactory.class.getName());
    
    // initialize the SqlMapClient (for main database)
    private static SqlMapClient sqlMap;
    
    // initialize the SqlMapClient (for temp database)
    private static SqlMapClient tempSqlMap;
    
    static {
        // configuration for main database
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
        System.out.println("Loaded Ibatis SQL map config -- "+ibatisConfigFile);
        
        // configuration for temp database
        if(MsDataUploadProperties.uploadToTempTables()) {
            reader = null;
            ibatisConfigFile = "SqlMapConfigTemp.xml";
            try {
                reader = Resources.getResourceAsReader(ibatisConfigFile);
                tempSqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
            }
            catch (IOException e) {
                log.error("Error reading Ibatis config xml: "+ibatisConfigFile, e);
                throw new RuntimeException("Error reading Ibatis config xml: "+ibatisConfigFile, e);
            }
            catch (Exception e) {
                log.error("Error initializing "+DAOFactory.class.getName()+" class: ", e);
                throw new RuntimeException("Error initializing "+DAOFactory.class.getName()+" class: ", e);
            }
            System.out.println("Loaded Ibatis SQL map config -- "+ibatisConfigFile);
        }
    }
    
    private static UploadDAOFactory instance = new UploadDAOFactory();
    
    // DAO for Experiment
    private MsExperimentDAO experimentDAO;
    
    // DAOs for enzyme related objects
    private MsEnzymeDAO enzymeDAO;
    
    // DAOs for run related objects
    private MsRunDAO runDAO;
    private MsScanDAO scanDAO;
    
    // DAOs related to MS2 files. 
    private MS2RunDAO ms2RunDAO;
    private MS2ScanDAO ms2ScanDAO;
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
    
    // DAOs related to post search analysis
    private MsSearchAnalysisDAO analysisDAO;
    private MsRunSearchAnalysisDAO rsAnalysisDAO;
    
    // DAOs related to Percolator post search analysis
    private PercolatorParamsDAO percSQTHeaderDAO;
    private PercolatorResultDAO percResultDAO;
    
    
    private UploadDAOFactory() {
        
        if(MsDataUploadProperties.uploadToTempTables()) {
            initializeForTempTableUpload();
        }
        else {
            initializeNormal();
        }
    }

    private void initializeForTempTableUpload() {
        
        // Experiment related
        MsExperimentDAO mainExptUpload = new MsExperimentDAOImpl(sqlMap);
        MsExperimentDAO tempExptUpload = new MsExperimentDAOImpl(tempSqlMap);
        experimentDAO  = new MsExperimentUploadDAOImpl(mainExptUpload, tempExptUpload, true);
        
        // Enzyme related
        MsEnzymeDAO mainEnzymeDao = new MsEnzymeDAOImpl(sqlMap);
        MsEnzymeDAO tempEnzymeDao = new MsEnzymeDAOImpl(tempSqlMap);
        enzymeDAO = new MsEnzymeUploadDAOImpl(mainEnzymeDao, tempEnzymeDao, true);
        
        // Run related
        MsScanDAO mainScanDao = new MsScanDAOImpl(sqlMap, MsDataUploadProperties.getPeakStorageType());
        MsScanDAO tempScanDao = new MsScanDAOImpl(tempSqlMap, MsDataUploadProperties.getPeakStorageType());
        scanDAO = new MsScanUploadDAOImpl(mainScanDao, tempScanDao, true, MsDataUploadProperties.getPeakStorageType());
        
        MsRunDAO mainRunDao = new MsRunDAOImpl(sqlMap, enzymeDAO);
        MsRunDAO tempRunDao = new MsRunDAOImpl(tempSqlMap, enzymeDAO);
        runDAO = new MsRunUploadDAOImpl(mainRunDao, tempRunDao, true);
        
        // ms2 file related
        ms2FileHeadersDAO = new MS2HeaderDAOImpl(tempSqlMap);
        ms2ChgIAnalysisDAO = new MS2ChargeIndependentAnalysisDAOImpl(tempSqlMap);
        ms2ChgDAnalysisDAO = new MS2ChargeDependentAnalysisDAOImpl(tempSqlMap);
        
        MS2ScanChargeDAO mainChgDao = new MS2ScanChargeDAOImpl(sqlMap, ms2ChgDAnalysisDAO);
        MS2ScanChargeDAO tempChgDao = new MS2ScanChargeDAOImpl(tempSqlMap, ms2ChgDAnalysisDAO);
        ms2FileScanChargeDAO = new MS2ScanChargeUploadDAOImpl(mainChgDao, tempChgDao, true);
        
        ms2ScanDAO = new MS2ScanUploadDAOImpl((MsScanUploadDAOImpl) scanDAO, true);
        
        MS2RunDAO mainMs2RunDao = new MS2RunDAOImpl(sqlMap, runDAO, ms2FileHeadersDAO);
        MS2RunDAO tempMs2RunDao = new MS2RunDAOImpl(tempSqlMap, runDAO, ms2FileHeadersDAO);
        ms2RunDAO = new MS2RunUploadDAOImpl(mainMs2RunDao, tempMs2RunDao, true);
        
        // Search related
        MsSearchDatabaseDAO mainSearchDbDao = new MsSearchDatabaseDAOImpl(sqlMap);
        MsSearchDatabaseDAO searchDbDao = new MsSearchDatabaseDAOImpl(tempSqlMap);
        seqDbDao = new MsSearchDatabaseUploadDAOImpl(mainSearchDbDao, searchDbDao, true);
        
        MsSearchModificationDAO mainModDao = new MsSearchModificationDAOImpl(sqlMap);
        MsSearchModificationDAO smodDao = new MsSearchModificationDAOImpl(tempSqlMap);
        modDAO = new MsSearchModificationUploadDAOImpl(mainModDao, smodDao, true);
        
        MsSearchResultProteinDAO protDao = new MsSearchResultProteinDAOImpl(tempSqlMap);
        resultProteinDAO = new MsSearchResultProteinUploadDAOImpl(protDao, true);
        
        MsSearchResultDAO mainResDao = new MsSearchResultDAOImpl(sqlMap, resultProteinDAO, modDAO);
        MsSearchResultDAO resDao = new MsSearchResultDAOImpl(tempSqlMap, resultProteinDAO, modDAO);
        searchResultDAO = new MsSearchResultUploadDAOImpl(mainResDao, resDao, true);
        
        MsRunSearchDAO mainRsDao = new MsRunSearchDAOImpl(sqlMap);
        MsRunSearchDAO rsDao = new MsRunSearchDAOImpl(tempSqlMap);
        runSearchDAO =  new MsRunSearchUploadDAOImpl(mainRsDao, rsDao, true);
        
        MsSearchDAO mainSDao = new MsSearchDAOImpl(sqlMap, seqDbDao, modDAO, enzymeDAO);
        MsSearchDAO sDao = new MsSearchDAOImpl(tempSqlMap, seqDbDao, modDAO, enzymeDAO);
        searchDAO = new MsSearchUploadDAOImpl(mainSDao, sDao, true);
        
        
        // sqt file related
        SQTSearchScanDAO scanDao = new SQTSearchScanDAOImpl(tempSqlMap);
        sqtSpectrumDAO = new SQTSearchScanUploadDAOImpl(scanDao, true);
        SQTHeaderDAO seqHeaderDao = new SQTHeaderDAOImpl(tempSqlMap);
        sqtHeaderDAO = new SQTHeaderUploadDAOImpl(seqHeaderDao, true);
        SQTRunSearchDAO seqRsDao = new SQTRunSearchDAOImpl(tempSqlMap, runSearchDAO, sqtHeaderDAO);
        sqtRunSearchDAO = new SQTRunSearchUploadDAOImpl(seqRsDao, true);
        
        // sequest search related
        SequestSearchResultDAO seqRDao = new SequestSearchResultDAOImpl(tempSqlMap, searchResultDAO, runSearchDAO, modDAO);
        sequestResultDAO = new SequestSearchResultUploadDAOImpl(seqRDao, true);
        SequestSearchDAO seqSDao = new SequestSearchDAOImpl(tempSqlMap, searchDAO);
        sequestSearchDAO = new SequestSearchUploadDAOImpl(seqSDao, true);
        
        // prolucid search related
        ProlucidSearchResultDAO proRDao = new ProlucidSearchResultDAOImpl(tempSqlMap, searchResultDAO, runSearchDAO, modDAO);
        prolucidResultDAO = new ProlucidSearchResultUploadDAOImpl(proRDao, true);
        ProlucidSearchDAO proSDao = new ProlucidSearchDAOImpl(sqlMap, searchDAO);
        prolucidSearchDAO = new ProlucidSearchUploadDAOImpl(proSDao, true);
        
        // post search analysis related
        MsSearchAnalysisDAO aDao = new MsSearchAnalysisDAOImpl(tempSqlMap);
        analysisDAO = new MsSearchAnalysisUploadDAOImpl(aDao, true);
        MsRunSearchAnalysisDAO rsaDao = new MsRunSearchAnalysisDAOImpl(tempSqlMap);
        rsAnalysisDAO = new MsRunSearchAnalysisUploadDAOImpl(rsaDao, true);
        
        // Percolator post search analysis related 
        PercolatorParamsDAO percParamsDao = new PercolatorParamsDAOImpl(tempSqlMap);
        percSQTHeaderDAO = new PercolatorParamsUploadDAOImpl(percParamsDao, true);
        PercolatorResultDAO percResDao = new PercolatorResultDAOImpl(tempSqlMap, rsAnalysisDAO, runSearchDAO, modDAO);
        percResultDAO = new PercolatorResultUploadDAOImpl(percResDao, true);
        
    }

    private void initializeNormal() {
        // Experiment related
        experimentDAO  = new MsExperimentDAOImpl(sqlMap);
        
        // Enzyme related
        enzymeDAO = new MsEnzymeDAOImpl(sqlMap);
        
        // Run related
        scanDAO = new MsScanDAOImpl(sqlMap, MsDataUploadProperties.getPeakStorageType());
        runDAO = new MsRunDAOImpl(sqlMap, enzymeDAO);
        
        // ms2 file related
        ms2FileHeadersDAO = new MS2HeaderDAOImpl(sqlMap);
        ms2ChgIAnalysisDAO = new MS2ChargeIndependentAnalysisDAOImpl(sqlMap);
        ms2ChgDAnalysisDAO = new MS2ChargeDependentAnalysisDAOImpl(sqlMap);
        ms2FileScanChargeDAO = new MS2ScanChargeDAOImpl(sqlMap, ms2ChgDAnalysisDAO);
        ms2ScanDAO = new MS2ScanDAOImpl(sqlMap, scanDAO, ms2ChgIAnalysisDAO, ms2FileScanChargeDAO);
        ms2RunDAO = new MS2RunDAOImpl(sqlMap, runDAO, ms2FileHeadersDAO);
        
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
        sequestResultDAO = new SequestSearchResultDAOImpl(sqlMap, searchResultDAO, runSearchDAO, modDAO);
        sequestSearchDAO = new SequestSearchDAOImpl(sqlMap, searchDAO);
        
        // prolucid search related
        prolucidResultDAO = new ProlucidSearchResultDAOImpl(sqlMap, searchResultDAO, runSearchDAO, modDAO);
        prolucidSearchDAO = new ProlucidSearchDAOImpl(sqlMap, searchDAO);
        
        // post search analysis related
        analysisDAO = new MsSearchAnalysisDAOImpl(sqlMap);
        rsAnalysisDAO = new MsRunSearchAnalysisDAOImpl(sqlMap);
        
        // Percolator post search analysis related 
        percSQTHeaderDAO = new PercolatorParamsDAOImpl(sqlMap);
        percResultDAO = new PercolatorResultDAOImpl(sqlMap, rsAnalysisDAO, runSearchDAO, modDAO);
    }
    
    public static UploadDAOFactory getInstance() {
        return instance;
    }
    
    public Connection getConnection() throws SQLException {
        return sqlMap.getDataSource().getConnection();
    }
    
    public Connection getTempDbConnection() throws SQLException {
        return tempSqlMap.getDataSource().getConnection();
    }
    
  //-------------------------------------------------------------------------------------------
    // EXPERIMENT related
    //-------------------------------------------------------------------------------------------
    public MsExperimentDAO getMsExperimentDAO() {
        return experimentDAO;
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
    public MsRunDAO getMsRunDAO() {
        return runDAO;
    }
    
    public MsScanDAO getMsScanDAO() {
        return scanDAO;
    }
    
    //-------------------------------------------------------------------------------------------
    // MS2 RUN related
    //-------------------------------------------------------------------------------------------
    public MS2RunDAO getMS2FileRunDAO() {
        return ms2RunDAO;
    }
    
    public MS2ScanDAO getMS2FileScanDAO() {
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
    
    //-------------------------------------------------------------------------------------------
    // Post search analysis related
    //-------------------------------------------------------------------------------------------
    public MsSearchAnalysisDAO getMsSearchAnalysisDAO() {
        return analysisDAO;
    }
    
    public MsRunSearchAnalysisDAO getMsRunSearchAnalysisDAO(){
        return rsAnalysisDAO;
    }
    
    //-------------------------------------------------------------------------------------------
    // Percolator related
    //-------------------------------------------------------------------------------------------
    public PercolatorParamsDAO getPercoltorParamsDAO() {
        return percSQTHeaderDAO;
    }
    
    public PercolatorResultDAO getPercolatorResultDAO() {
        return percResultDAO;
    }
}
