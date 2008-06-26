package org.yeastrc.ms.dao;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.yeastrc.ms.dao.ms2File.MS2FileRunHeadersDAO;
import org.yeastrc.ms.dao.ms2File.Ms2FileChargeDependentAnalysisDAO;
import org.yeastrc.ms.dao.ms2File.Ms2FileChargeIndependentAnalysisDAO;

public class DAOFactory {

    private ApplicationContext ctx;
    
    private MsExperimentDAO expDAO;
    private MsRunDAO runDAO;
    private MsScanDAO scanDAO;
    
    // related to MS2 files. 
    private MS2FileScanChargeDAO ms2FilecanChargeDAO;
    private MS2FileRunHeadersDAO ms2FileHeadersDAO;
    private Ms2FileChargeDependentAnalysisDAO ms2ChgDAnalysisDAO;
    private Ms2FileChargeIndependentAnalysisDAO ms2ChgIAnalysisDAO;
    
    
    private static DAOFactory instance = new DAOFactory();
    
    private DAOFactory() {
        // load spring beans
        ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        System.out.println("Classpath loaded");
        
        expDAO = (MsExperimentDAO)ctx.getBean("msExperimentDAO");
        runDAO = (MsRunDAO)ctx.getBean("msRunDAO");
        scanDAO = (MsScanDAO)ctx.getBean("msScanDAO");
        ms2FilecanChargeDAO = (MS2FileScanChargeDAO)ctx.getBean("ms2FileScanChargeDAO");
        ms2FileHeadersDAO = (MS2FileRunHeadersDAO)ctx.getBean("ms2FileRunHeadersDAO");
        ms2ChgDAnalysisDAO = (Ms2FileChargeDependentAnalysisDAO)ctx.getBean("ms2FileChargeDependentAnalysisDAO");
        ms2ChgIAnalysisDAO = (Ms2FileChargeIndependentAnalysisDAO)ctx.getBean("ms2FileChargeIndependentAnalysisDAO");
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
        return ms2FilecanChargeDAO;
    }
    
    public MS2FileRunHeadersDAO getMS2FileRunHeadersDAO() {
        return ms2FileHeadersDAO;
    }
    
    public Ms2FileChargeDependentAnalysisDAO getMs2FileChargeDAnalysisDAO() {
        return ms2ChgDAnalysisDAO;
    }
    
    public Ms2FileChargeIndependentAnalysisDAO getMs2FileChargeIAnalysisDAO() {
        return ms2ChgIAnalysisDAO;
    }
}
