/**
 * UploadDAOFactory.java
 * @author Vagisha Sharma
 * May 5, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.service.MsDataUploadProperties;
import org.yeastrc.ms.upload.dao.analysis.MsRunSearchAnalysisUploadDAO;
import org.yeastrc.ms.upload.dao.analysis.MsSearchAnalysisUploadDAO;
import org.yeastrc.ms.upload.dao.analysis.dualdb.MsRunSearchAnalysisUploadDAOImpl;
import org.yeastrc.ms.upload.dao.analysis.dualdb.MsSearchAnalysisUploadDAOImpl;
import org.yeastrc.ms.upload.dao.analysis.ibatis.MsRunSearchAnalysisUploadDAOIbatisImpl;
import org.yeastrc.ms.upload.dao.analysis.ibatis.MsSearchAnalysisUploadDAOIbatisImpl;
import org.yeastrc.ms.upload.dao.analysis.peptideProphet.PeptideProphetAnalysisUploadDAO;
import org.yeastrc.ms.upload.dao.analysis.peptideProphet.PeptideProphetResultUploadDAO;
import org.yeastrc.ms.upload.dao.analysis.peptideProphet.PeptideProphetRocUploadDAO;
import org.yeastrc.ms.upload.dao.analysis.peptideProphet.ibatis.PeptideProphetAnalysisUploadDAOImpl;
import org.yeastrc.ms.upload.dao.analysis.peptideProphet.ibatis.PeptideProphetResultUploadDAOImpl;
import org.yeastrc.ms.upload.dao.analysis.peptideProphet.ibatis.PeptideProphetRocUploadDAOImpl;
import org.yeastrc.ms.upload.dao.analysis.percolator.PercolatorParamsUploadDAO;
import org.yeastrc.ms.upload.dao.analysis.percolator.PercolatorResultUploadDAO;
import org.yeastrc.ms.upload.dao.analysis.percolator.dualdb.PercolatorParamsUploadDAOImpl;
import org.yeastrc.ms.upload.dao.analysis.percolator.dualdb.PercolatorResultUploadDAOImpl;
import org.yeastrc.ms.upload.dao.analysis.percolator.ibatis.PercolatorParamsUploadDAOIbatisImpl;
import org.yeastrc.ms.upload.dao.analysis.percolator.ibatis.PercolatorResultUploadDAOIbatisImpl;
import org.yeastrc.ms.upload.dao.general.MsEnzymeUploadDAO;
import org.yeastrc.ms.upload.dao.general.MsExperimentUploadDAO;
import org.yeastrc.ms.upload.dao.general.dualdb.MsEnzymeUploadDAOImpl;
import org.yeastrc.ms.upload.dao.general.dualdb.MsExperimentUploadDAOImpl;
import org.yeastrc.ms.upload.dao.general.ibatis.MsEnzymeUploadDAOIbatisImpl;
import org.yeastrc.ms.upload.dao.general.ibatis.MsExperimentUploadDAOIbatisImpl;
import org.yeastrc.ms.upload.dao.run.MsRunUploadDAO;
import org.yeastrc.ms.upload.dao.run.MsScanUploadDAO;
import org.yeastrc.ms.upload.dao.run.dualdb.MsRunUploadDAOImpl;
import org.yeastrc.ms.upload.dao.run.dualdb.MsScanUploadDAOImpl;
import org.yeastrc.ms.upload.dao.run.ibatis.MsRunUploadDAOIbatisImpl;
import org.yeastrc.ms.upload.dao.run.ibatis.MsScanUploadDAOIbatisImpl;
import org.yeastrc.ms.upload.dao.run.ms2file.MS2ChargeDependentAnalysisUploadDAO;
import org.yeastrc.ms.upload.dao.run.ms2file.MS2ChargeIndependentAnalysisUploadDAO;
import org.yeastrc.ms.upload.dao.run.ms2file.MS2HeaderUploadDAO;
import org.yeastrc.ms.upload.dao.run.ms2file.MS2RunUploadDAO;
import org.yeastrc.ms.upload.dao.run.ms2file.MS2ScanChargeUploadDAO;
import org.yeastrc.ms.upload.dao.run.ms2file.MS2ScanUploadDAO;
import org.yeastrc.ms.upload.dao.run.ms2file.dualdb.MS2RunUploadDAOImpl;
import org.yeastrc.ms.upload.dao.run.ms2file.dualdb.MS2ScanChargeUploadDAOImpl;
import org.yeastrc.ms.upload.dao.run.ms2file.dualdb.MS2ScanUploadDAOImpl;
import org.yeastrc.ms.upload.dao.run.ms2file.ibatis.MS2ChargeDependentAnalysisUploadDAOIbatisImpl;
import org.yeastrc.ms.upload.dao.run.ms2file.ibatis.MS2ChargeIndependentAnalysisUploadDAOIbatisImpl;
import org.yeastrc.ms.upload.dao.run.ms2file.ibatis.MS2HeaderUploadDAOIbatisImpl;
import org.yeastrc.ms.upload.dao.run.ms2file.ibatis.MS2RunUploadDAOIbatisImpl;
import org.yeastrc.ms.upload.dao.run.ms2file.ibatis.MS2ScanChargeUploadDAOIbatisImpl;
import org.yeastrc.ms.upload.dao.run.ms2file.ibatis.MS2ScanUploadDAOIbatisImpl;
import org.yeastrc.ms.upload.dao.search.MsRunSearchUploadDAO;
import org.yeastrc.ms.upload.dao.search.MsSearchDatabaseUploadDAO;
import org.yeastrc.ms.upload.dao.search.MsSearchModificationUploadDAO;
import org.yeastrc.ms.upload.dao.search.MsSearchResultProteinUploadDAO;
import org.yeastrc.ms.upload.dao.search.MsSearchResultUploadDAO;
import org.yeastrc.ms.upload.dao.search.MsSearchUploadDAO;
import org.yeastrc.ms.upload.dao.search.dualdb.MsRunSearchUploadDAOImpl;
import org.yeastrc.ms.upload.dao.search.dualdb.MsSearchDatabaseUploadDAOImpl;
import org.yeastrc.ms.upload.dao.search.dualdb.MsSearchModificationUploadDAOImpl;
import org.yeastrc.ms.upload.dao.search.dualdb.MsSearchResultProteinUploadDAOImpl;
import org.yeastrc.ms.upload.dao.search.dualdb.MsSearchResultUploadDAOImpl;
import org.yeastrc.ms.upload.dao.search.dualdb.MsSearchUploadDAOImpl;
import org.yeastrc.ms.upload.dao.search.ibatis.MsRunSearchUploadDAOIbatisImpl;
import org.yeastrc.ms.upload.dao.search.ibatis.MsSearchDatabaseUploadDAOIbatisImpl;
import org.yeastrc.ms.upload.dao.search.ibatis.MsSearchModificationUploadDAOIbatisImpl;
import org.yeastrc.ms.upload.dao.search.ibatis.MsSearchResultProteinUploadDAOIbatisImpl;
import org.yeastrc.ms.upload.dao.search.ibatis.MsSearchResultUploadDAOIbatisImpl;
import org.yeastrc.ms.upload.dao.search.ibatis.MsSearchUploadDAOIbatisImpl;
import org.yeastrc.ms.upload.dao.search.mascot.MascotSearchResultUploadDAO;
import org.yeastrc.ms.upload.dao.search.mascot.MascotSearchUploadDAO;
import org.yeastrc.ms.upload.dao.search.mascot.dualdb.MascotSearchResultUploadDAOImpl;
import org.yeastrc.ms.upload.dao.search.mascot.dualdb.MascotSearchUploadDAOImpl;
import org.yeastrc.ms.upload.dao.search.mascot.ibatis.MascotSearchResultUploadDAOIbatisImpl;
import org.yeastrc.ms.upload.dao.search.mascot.ibatis.MascotSearchUploadDAOIbatisImpl;
import org.yeastrc.ms.upload.dao.search.prolucid.ProlucidSearchResultUploadDAO;
import org.yeastrc.ms.upload.dao.search.prolucid.ProlucidSearchUploadDAO;
import org.yeastrc.ms.upload.dao.search.prolucid.dualdb.ProlucidSearchResultUploadDAOImpl;
import org.yeastrc.ms.upload.dao.search.prolucid.dualdb.ProlucidSearchUploadDAOImpl;
import org.yeastrc.ms.upload.dao.search.prolucid.ibatis.ProlucidSearchResultUploadDAOIbatisImpl;
import org.yeastrc.ms.upload.dao.search.prolucid.ibatis.ProlucidSearchUploadDAOIbatisImpl;
import org.yeastrc.ms.upload.dao.search.sequest.SequestSearchResultUploadDAO;
import org.yeastrc.ms.upload.dao.search.sequest.SequestSearchUploadDAO;
import org.yeastrc.ms.upload.dao.search.sequest.dualdb.SequestSearchResultUploadDAOImpl;
import org.yeastrc.ms.upload.dao.search.sequest.dualdb.SequestSearchUploadDAOImpl;
import org.yeastrc.ms.upload.dao.search.sequest.ibatis.SequestSearchResultUploadDAOIbatisImpl;
import org.yeastrc.ms.upload.dao.search.sequest.ibatis.SequestSearchUploadDAOIbatisImpl;
import org.yeastrc.ms.upload.dao.search.sqtfile.SQTHeaderUploadDAO;
import org.yeastrc.ms.upload.dao.search.sqtfile.SQTRunSearchUploadDAO;
import org.yeastrc.ms.upload.dao.search.sqtfile.SQTSearchScanUploadDAO;
import org.yeastrc.ms.upload.dao.search.sqtfile.dualdb.SQTHeaderUploadDAOImpl;
import org.yeastrc.ms.upload.dao.search.sqtfile.dualdb.SQTRunSearchUploadDAOImpl;
import org.yeastrc.ms.upload.dao.search.sqtfile.dualdb.SQTSearchScanUploadDAOImpl;
import org.yeastrc.ms.upload.dao.search.sqtfile.ibatis.SQTHeaderUploadDAOIbatisImpl;
import org.yeastrc.ms.upload.dao.search.sqtfile.ibatis.SQTRunSearchUploadDAOIbatisImpl;
import org.yeastrc.ms.upload.dao.search.sqtfile.ibatis.SQTSearchScanUploadDAOIbatisImpl;
import org.yeastrc.ms.upload.dao.search.xtandem.XtandemSearchResultUploadDAO;
import org.yeastrc.ms.upload.dao.search.xtandem.XtandemSearchUploadDAO;
import org.yeastrc.ms.upload.dao.search.xtandem.dualdb.XtandemSearchResultUploadDAOImpl;
import org.yeastrc.ms.upload.dao.search.xtandem.dualdb.XtandemSearchUploadDAOImpl;
import org.yeastrc.ms.upload.dao.search.xtandem.ibatis.XtandemSearchResultUploadDAOIbatisImpl;
import org.yeastrc.ms.upload.dao.search.xtandem.ibatis.XtandemSearchUploadDAOIbatisImpl;

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
            log.error("Error initializing "+UploadDAOFactory.class.getName()+" class: ", e);
            throw new RuntimeException("Error initializing "+UploadDAOFactory.class.getName()+" class: ", e);
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
                log.error("Error initializing "+UploadDAOFactory.class.getName()+" class: ", e);
                throw new RuntimeException("Error initializing "+UploadDAOFactory.class.getName()+" class: ", e);
            }
            System.out.println("Loaded Ibatis SQL map config -- "+ibatisConfigFile);
        }
    }
    
    private static UploadDAOFactory instance = new UploadDAOFactory();
    
    // DAO for Experiment
    private MsExperimentUploadDAO experimentDAO;
    
    // DAOs for enzyme related objects
    private MsEnzymeUploadDAO enzymeDAO;
    
    // DAOs for run related objects
    private MsRunUploadDAO runDAO;
    private MsScanUploadDAO scanDAO;
    
    // DAOs related to MS2 files. 
    private MS2RunUploadDAO ms2RunDAO;
    private MS2ScanUploadDAO ms2ScanDAO;
    private MS2ScanChargeUploadDAO ms2FileScanChargeDAO;
    private MS2HeaderUploadDAO ms2FileHeadersDAO;
    private MS2ChargeDependentAnalysisUploadDAO ms2ChgDAnalysisDAO;
    private MS2ChargeIndependentAnalysisUploadDAO ms2ChgIAnalysisDAO;
    
    // DAOs for search related objects
    private MsSearchUploadDAO searchDAO;
    private MsRunSearchUploadDAO runSearchDAO;
    private MsSearchResultUploadDAO searchResultDAO;
    private MsSearchResultProteinUploadDAO resultProteinDAO;
    private MsSearchModificationUploadDAO modDAO;
    private MsSearchDatabaseUploadDAO seqDbDao;
    
    // DAOs for SQT file related objects
    private SQTSearchScanUploadDAO sqtSpectrumDAO;
    private SQTHeaderUploadDAO sqtHeaderDAO;
    private SQTRunSearchUploadDAO sqtRunSearchDAO;
    
    // DAOs for Sequest related objects
    private SequestSearchResultUploadDAO sequestResultDAO;
    private SequestSearchUploadDAO sequestSearchDAO;
    
    // DAOs for Mascot related objects
    private MascotSearchResultUploadDAO mascotResultDAO;
    private MascotSearchUploadDAO mascotSearchDAO;
    
    // DAOs for Xtandem related objects
    private XtandemSearchResultUploadDAO xtandemResultDAO;
    private XtandemSearchUploadDAO xtandemSearchDAO;
    
    // DAOs for Prolucid related objects
    private ProlucidSearchResultUploadDAO prolucidResultDAO;
    private ProlucidSearchUploadDAO prolucidSearchDAO;
    
    // DAOs related to post search analysis
    private MsSearchAnalysisUploadDAO analysisDAO;
    private MsRunSearchAnalysisUploadDAO rsAnalysisDAO;
    
    // DAOs related to Percolator post search analysis
    private PercolatorParamsUploadDAO percSQTHeaderDAO;
    private PercolatorResultUploadDAO percResultDAO;
    
    // DAOs related to PeptideProphet post search analysis
    private PeptideProphetAnalysisUploadDAO pprophAnalysisDAO;
    private PeptideProphetRocUploadDAO ppRocDAO;
    private PeptideProphetResultUploadDAO pprophResultDAO;
    
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
        MsExperimentUploadDAO mainExptUpload = new MsExperimentUploadDAOIbatisImpl(sqlMap);
        MsExperimentUploadDAO tempExptUpload = new MsExperimentUploadDAOIbatisImpl(tempSqlMap);
        experimentDAO  = new MsExperimentUploadDAOImpl(mainExptUpload, tempExptUpload, true);
        
        // Enzyme related
        MsEnzymeUploadDAO mainEnzymeDao = new MsEnzymeUploadDAOIbatisImpl(sqlMap);
        MsEnzymeUploadDAO tempEnzymeDao = new MsEnzymeUploadDAOIbatisImpl(tempSqlMap);
        enzymeDAO = new MsEnzymeUploadDAOImpl(mainEnzymeDao, tempEnzymeDao, true);
        
        // Run related
        MsScanUploadDAO mainScanDao = new MsScanUploadDAOIbatisImpl(sqlMap, MsDataUploadProperties.getPeakStorageType());
        MsScanUploadDAO tempScanDao = new MsScanUploadDAOIbatisImpl(tempSqlMap, MsDataUploadProperties.getPeakStorageType());
        scanDAO = new MsScanUploadDAOImpl(mainScanDao, tempScanDao, true, MsDataUploadProperties.getPeakStorageType());
        
        MsRunUploadDAO mainRunDao = new MsRunUploadDAOIbatisImpl(sqlMap, enzymeDAO);
        MsRunUploadDAO tempRunDao = new MsRunUploadDAOIbatisImpl(tempSqlMap, enzymeDAO);
        runDAO = new MsRunUploadDAOImpl(mainRunDao, tempRunDao, true);
        
        // ms2 file related
        ms2FileHeadersDAO = new MS2HeaderUploadDAOIbatisImpl(tempSqlMap);
        ms2ChgIAnalysisDAO = new MS2ChargeIndependentAnalysisUploadDAOIbatisImpl(tempSqlMap);
        ms2ChgDAnalysisDAO = new MS2ChargeDependentAnalysisUploadDAOIbatisImpl(tempSqlMap);
        
        MS2ScanChargeUploadDAO mainChgDao = new MS2ScanChargeUploadDAOIbatisImpl(sqlMap, ms2ChgDAnalysisDAO);
        MS2ScanChargeUploadDAO tempChgDao = new MS2ScanChargeUploadDAOIbatisImpl(tempSqlMap, ms2ChgDAnalysisDAO);
        ms2FileScanChargeDAO = new MS2ScanChargeUploadDAOImpl(mainChgDao, tempChgDao, true);
        
        MS2ScanUploadDAO ms2sDao = new MS2ScanUploadDAOIbatisImpl(sqlMap, scanDAO, ms2ChgIAnalysisDAO, ms2FileScanChargeDAO);
        ms2ScanDAO = new MS2ScanUploadDAOImpl(ms2sDao, true);
        
        MS2RunUploadDAO tempMs2RunDao = new MS2RunUploadDAOIbatisImpl(tempSqlMap, runDAO, ms2FileHeadersDAO);
        ms2RunDAO = new MS2RunUploadDAOImpl(tempMs2RunDao, runDAO, true);
        
        // Search related
        MsSearchDatabaseUploadDAO mainSearchDbDao = new MsSearchDatabaseUploadDAOIbatisImpl(sqlMap);
        MsSearchDatabaseUploadDAO searchDbDao = new MsSearchDatabaseUploadDAOIbatisImpl(tempSqlMap);
        seqDbDao = new MsSearchDatabaseUploadDAOImpl(mainSearchDbDao, searchDbDao, true);
        
        MsSearchModificationUploadDAO mainModDao = new MsSearchModificationUploadDAOIbatisImpl(sqlMap);
        MsSearchModificationUploadDAO smodDao = new MsSearchModificationUploadDAOIbatisImpl(tempSqlMap);
        modDAO = new MsSearchModificationUploadDAOImpl(mainModDao, smodDao, true);
        
        MsSearchResultProteinUploadDAO protDao = new MsSearchResultProteinUploadDAOIbatisImpl(tempSqlMap);
        resultProteinDAO = new MsSearchResultProteinUploadDAOImpl(protDao, true);
        
        MsSearchResultUploadDAO mainResDao = new MsSearchResultUploadDAOIbatisImpl(sqlMap);
        MsSearchResultUploadDAO resDao = new MsSearchResultUploadDAOIbatisImpl(tempSqlMap);
        searchResultDAO = new MsSearchResultUploadDAOImpl(mainResDao, resDao, true);
        
        MsRunSearchUploadDAO mainRsDao = new MsRunSearchUploadDAOIbatisImpl(sqlMap);
        MsRunSearchUploadDAO rsDao = new MsRunSearchUploadDAOIbatisImpl(tempSqlMap);
        runSearchDAO =  new MsRunSearchUploadDAOImpl(mainRsDao, rsDao, true);
        
        MsSearchUploadDAO mainSDao = new MsSearchUploadDAOIbatisImpl(sqlMap, seqDbDao, modDAO, enzymeDAO);
        MsSearchUploadDAO sDao = new MsSearchUploadDAOIbatisImpl(tempSqlMap, seqDbDao, modDAO, enzymeDAO);
        searchDAO = new MsSearchUploadDAOImpl(mainSDao, sDao, true);
        
        
        // sqt file related
        SQTSearchScanUploadDAO scanDao = new SQTSearchScanUploadDAOIbatisImpl(tempSqlMap);
        sqtSpectrumDAO = new SQTSearchScanUploadDAOImpl(scanDao, true);
        SQTHeaderUploadDAO seqHeaderDao = new SQTHeaderUploadDAOIbatisImpl(tempSqlMap);
        sqtHeaderDAO = new SQTHeaderUploadDAOImpl(seqHeaderDao, true);
        SQTRunSearchUploadDAO seqRsDao = new SQTRunSearchUploadDAOIbatisImpl(tempSqlMap, runSearchDAO, sqtHeaderDAO);
        sqtRunSearchDAO = new SQTRunSearchUploadDAOImpl(seqRsDao, true);
        
        // sequest search related
        SequestSearchResultUploadDAO seqRDao = new SequestSearchResultUploadDAOIbatisImpl(tempSqlMap, searchResultDAO);
        sequestResultDAO = new SequestSearchResultUploadDAOImpl(seqRDao, true);
        SequestSearchUploadDAO seqSDao = new SequestSearchUploadDAOIbatisImpl(tempSqlMap, searchDAO);
        sequestSearchDAO = new SequestSearchUploadDAOImpl(seqSDao, true);
        
        // mascot search related
        MascotSearchResultUploadDAO mascotRDao = new MascotSearchResultUploadDAOIbatisImpl(tempSqlMap, searchResultDAO);
        mascotResultDAO = new MascotSearchResultUploadDAOImpl(mascotRDao, true);
        MascotSearchUploadDAO mascotSDao = new MascotSearchUploadDAOIbatisImpl(tempSqlMap, searchDAO);
        mascotSearchDAO = new MascotSearchUploadDAOImpl(mascotSDao, true);
        
        // xtandem search related
        XtandemSearchResultUploadDAO xtandemRDao = new XtandemSearchResultUploadDAOIbatisImpl(tempSqlMap, searchResultDAO);
        xtandemResultDAO = new XtandemSearchResultUploadDAOImpl(xtandemRDao, true);
        XtandemSearchUploadDAO xtandemSDao = new XtandemSearchUploadDAOIbatisImpl(tempSqlMap, searchDAO);
        xtandemSearchDAO = new XtandemSearchUploadDAOImpl(xtandemSDao, true);
        
        // prolucid search related
        ProlucidSearchResultUploadDAO proRDao = new ProlucidSearchResultUploadDAOIbatisImpl(tempSqlMap, searchResultDAO);
        prolucidResultDAO = new ProlucidSearchResultUploadDAOImpl(proRDao, true);
        ProlucidSearchUploadDAO proSDao = new ProlucidSearchUploadDAOIbatisImpl(sqlMap, searchDAO);
        prolucidSearchDAO = new ProlucidSearchUploadDAOImpl(proSDao, true);
        
        // post search analysis related
        MsSearchAnalysisUploadDAO aDao = new MsSearchAnalysisUploadDAOIbatisImpl(tempSqlMap);
        analysisDAO = new MsSearchAnalysisUploadDAOImpl(aDao, true);
        MsRunSearchAnalysisUploadDAO rsaDao = new MsRunSearchAnalysisUploadDAOIbatisImpl(tempSqlMap);
        rsAnalysisDAO = new MsRunSearchAnalysisUploadDAOImpl(rsaDao, true);
        
        // Percolator post search analysis related 
        PercolatorParamsUploadDAO percParamsDao = new PercolatorParamsUploadDAOIbatisImpl(tempSqlMap);
        percSQTHeaderDAO = new PercolatorParamsUploadDAOImpl(percParamsDao, true);
        PercolatorResultUploadDAO percResDao = new PercolatorResultUploadDAOIbatisImpl(tempSqlMap);
        percResultDAO = new PercolatorResultUploadDAOImpl(percResDao, true);
        
        // DAOs related to PeptideProphet post search analysis
        pprophAnalysisDAO = new PeptideProphetAnalysisUploadDAOImpl(tempSqlMap, analysisDAO);
        ppRocDAO = new PeptideProphetRocUploadDAOImpl(tempSqlMap);
        pprophResultDAO = new PeptideProphetResultUploadDAOImpl(tempSqlMap);
        
    }

    private void initializeNormal() {
        // Experiment related
        experimentDAO  = new MsExperimentUploadDAOIbatisImpl(sqlMap);
        
        // Enzyme related
        enzymeDAO = new MsEnzymeUploadDAOIbatisImpl(sqlMap);
        
        // Run related
        scanDAO = new MsScanUploadDAOIbatisImpl(sqlMap, MsDataUploadProperties.getPeakStorageType());
        runDAO = new MsRunUploadDAOIbatisImpl(sqlMap, enzymeDAO);
        
        // ms2 file related
        ms2FileHeadersDAO = new MS2HeaderUploadDAOIbatisImpl(sqlMap);
        ms2ChgIAnalysisDAO = new MS2ChargeIndependentAnalysisUploadDAOIbatisImpl(sqlMap);
        ms2ChgDAnalysisDAO = new MS2ChargeDependentAnalysisUploadDAOIbatisImpl(sqlMap);
        ms2FileScanChargeDAO = new MS2ScanChargeUploadDAOIbatisImpl(sqlMap, ms2ChgDAnalysisDAO);
        ms2ScanDAO = new MS2ScanUploadDAOIbatisImpl(sqlMap, scanDAO, ms2ChgIAnalysisDAO, ms2FileScanChargeDAO);
        ms2RunDAO = new MS2RunUploadDAOIbatisImpl(sqlMap, runDAO, ms2FileHeadersDAO);
        
        // Search related
        seqDbDao = new MsSearchDatabaseUploadDAOIbatisImpl(sqlMap);
        modDAO = new MsSearchModificationUploadDAOIbatisImpl(sqlMap);
        resultProteinDAO = new MsSearchResultProteinUploadDAOIbatisImpl(sqlMap);
        searchResultDAO = new MsSearchResultUploadDAOIbatisImpl(sqlMap);
        runSearchDAO = new MsRunSearchUploadDAOIbatisImpl(sqlMap);
        searchDAO = new MsSearchUploadDAOIbatisImpl(sqlMap, seqDbDao, modDAO, enzymeDAO);
        
        // sqt file related
        sqtSpectrumDAO = new SQTSearchScanUploadDAOIbatisImpl(sqlMap);
        sqtHeaderDAO = new SQTHeaderUploadDAOIbatisImpl(sqlMap);
        sqtRunSearchDAO = new SQTRunSearchUploadDAOIbatisImpl(sqlMap, runSearchDAO, sqtHeaderDAO);
        
        // sequest search related
        sequestResultDAO = new SequestSearchResultUploadDAOIbatisImpl(sqlMap, searchResultDAO);
        sequestSearchDAO = new SequestSearchUploadDAOIbatisImpl(sqlMap, searchDAO);
        
        // mascot search related
        mascotResultDAO = new MascotSearchResultUploadDAOIbatisImpl(sqlMap, searchResultDAO);
        mascotSearchDAO = new MascotSearchUploadDAOIbatisImpl(sqlMap, searchDAO);
        
        // xtandem search related
        xtandemResultDAO = new XtandemSearchResultUploadDAOIbatisImpl(sqlMap, searchResultDAO);
        xtandemSearchDAO = new XtandemSearchUploadDAOIbatisImpl(sqlMap, searchDAO);
        
        // prolucid search related
        prolucidResultDAO = new ProlucidSearchResultUploadDAOIbatisImpl(sqlMap, searchResultDAO);
        prolucidSearchDAO = new ProlucidSearchUploadDAOIbatisImpl(sqlMap, searchDAO);
        
        // post search analysis related
        analysisDAO = new MsSearchAnalysisUploadDAOIbatisImpl(sqlMap);
        rsAnalysisDAO = new MsRunSearchAnalysisUploadDAOIbatisImpl(sqlMap);
        
        // Percolator post search analysis related 
        percSQTHeaderDAO = new PercolatorParamsUploadDAOIbatisImpl(sqlMap);
        percResultDAO = new PercolatorResultUploadDAOIbatisImpl(sqlMap);
        
        // DAOs related to PeptideProphet post search analysis
        pprophAnalysisDAO = new PeptideProphetAnalysisUploadDAOImpl(sqlMap, analysisDAO);
        ppRocDAO = new PeptideProphetRocUploadDAOImpl(sqlMap);
        pprophResultDAO = new PeptideProphetResultUploadDAOImpl(sqlMap);
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
    public MsExperimentUploadDAO getMsExperimentDAO() {
        return experimentDAO;
    }
    
    
    //-------------------------------------------------------------------------------------------
    // ENZYME related
    //-------------------------------------------------------------------------------------------
    public MsEnzymeUploadDAO getEnzymeDAO() {
        return enzymeDAO;
    }
    
    //-------------------------------------------------------------------------------------------
    // RUN related
    //-------------------------------------------------------------------------------------------
    public MsRunUploadDAO getMsRunDAO() {
        return runDAO;
    }
    
    public MsScanUploadDAO getMsScanDAO() {
        return scanDAO;
    }
    
    //-------------------------------------------------------------------------------------------
    // MS2 RUN related
    //-------------------------------------------------------------------------------------------
    public MS2RunUploadDAO getMS2FileRunDAO() {
        return ms2RunDAO;
    }
    
    public MS2ScanUploadDAO getMS2FileScanDAO() {
        return ms2ScanDAO;
    }
    
    public MS2ScanChargeUploadDAO getMS2FileScanChargeDAO() {
        return ms2FileScanChargeDAO;
    }
    
    public MS2HeaderUploadDAO getMS2FileRunHeadersDAO() {
        return ms2FileHeadersDAO;
    }
    
    public MS2ChargeDependentAnalysisUploadDAO getMs2FileChargeDAnalysisDAO() {
        return ms2ChgDAnalysisDAO;
    }
    
    public MS2ChargeIndependentAnalysisUploadDAO getMs2FileChargeIAnalysisDAO() {
        return ms2ChgIAnalysisDAO;
    }
    
    //-------------------------------------------------------------------------------------------
    // SEARCH related
    //-------------------------------------------------------------------------------------------
    public MsSearchUploadDAO getMsSearchDAO() {
        return searchDAO;
    }
    
    public MsRunSearchUploadDAO getMsRunSearchDAO() {
        return runSearchDAO;
    }
    
    public MsSearchResultUploadDAO getMsSearchResultDAO() {
        return searchResultDAO;
    }
    
    public MsSearchResultProteinUploadDAO getMsProteinMatchDAO() {
        return resultProteinDAO;
    }
    
    public MsSearchModificationUploadDAO getMsSearchModDAO() {
        return modDAO;
    }
    
    public MsSearchDatabaseUploadDAO getMsSequenceDatabaseDAO() {
        return seqDbDao;
    }
    
    //-------------------------------------------------------------------------------------------
    // SQT file related
    //-------------------------------------------------------------------------------------------
    public SQTHeaderUploadDAO getSqtHeaderDAO() {
        return sqtHeaderDAO;
    }
    
    public SQTRunSearchUploadDAO getSqtRunSearchDAO() {
        return sqtRunSearchDAO;
    }
    
    public SQTSearchScanUploadDAO getSqtSpectrumDAO() {
        return sqtSpectrumDAO;
    }
    
    //-------------------------------------------------------------------------------------------
    // Sequest SEARCH related
    //-------------------------------------------------------------------------------------------
    public SequestSearchResultUploadDAO getSequestResultDAO() {
        return sequestResultDAO;
    }
    
    public SequestSearchUploadDAO getSequestSearchDAO() {
        return sequestSearchDAO;
    }
    
    //-------------------------------------------------------------------------------------------
    // Mascot SEARCH related
    //-------------------------------------------------------------------------------------------
    public MascotSearchResultUploadDAO getMascotResultDAO() {
        return mascotResultDAO;
    }
    
    public MascotSearchUploadDAO getMascotSearchDAO() {
        return mascotSearchDAO;
    }
    
    //-------------------------------------------------------------------------------------------
    // Xtandem SEARCH related
    //-------------------------------------------------------------------------------------------
    public XtandemSearchResultUploadDAO getXtandemResultDAO() {
        return xtandemResultDAO;
    }
    
    public XtandemSearchUploadDAO getXtandemSearchDAO() {
        return xtandemSearchDAO;
    }
    
    //-------------------------------------------------------------------------------------------
    // ProLuCID SEARCH related
    //-------------------------------------------------------------------------------------------
    public ProlucidSearchResultUploadDAO getProlucidResultDAO() {
        return prolucidResultDAO;
    }
    
    public ProlucidSearchUploadDAO getProlucidSearchDAO() {
        return prolucidSearchDAO;
    }
    
    //-------------------------------------------------------------------------------------------
    // Post search analysis related
    //-------------------------------------------------------------------------------------------
    public MsSearchAnalysisUploadDAO getMsSearchAnalysisDAO() {
        return analysisDAO;
    }
    
    public MsRunSearchAnalysisUploadDAO getMsRunSearchAnalysisDAO(){
        return rsAnalysisDAO;
    }
    
    //-------------------------------------------------------------------------------------------
    // Percolator related
    //-------------------------------------------------------------------------------------------
    public PercolatorParamsUploadDAO getPercoltorParamsDAO() {
        return percSQTHeaderDAO;
    }
    
    public PercolatorResultUploadDAO getPercolatorResultDAO() {
        return percResultDAO;
    }
    
    //-------------------------------------------------------------------------------------------
    // PeptideProphet related
    //-------------------------------------------------------------------------------------------
    public PeptideProphetAnalysisUploadDAO getPeptideProphetAnalysisDAO() {
        return pprophAnalysisDAO;
    }
    public PeptideProphetResultUploadDAO getPeptideProphetResultDAO() {
        return pprophResultDAO;
    }
    
    public PeptideProphetRocUploadDAO getPeptideProphetRocDAO() {
        return ppRocDAO;
    }
}
