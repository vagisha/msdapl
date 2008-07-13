package org.yeastrc.ms.dao;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.yeastrc.ms.dao.ms2File.MS2ChargeDependentAnalysisDAOImplTest;
import org.yeastrc.ms.dao.ms2File.MS2ChargeIndependentAnalysisDAOImplTest;
import org.yeastrc.ms.dao.ms2File.MS2RunDAOImplTest;
import org.yeastrc.ms.dao.ms2File.MS2ScanChargeDAOImplTest;
import org.yeastrc.ms.dao.sqtFile.SQTPeptideSearchDAOImplTest;
import org.yeastrc.ms.dao.sqtFile.SQTSearchHeaderDAOImplTest;
import org.yeastrc.ms.dao.sqtFile.SQTSearchResultDAOImplTest;
import org.yeastrc.ms.dao.sqtFile.SQTSpectrumDataDAOImplTest;

public class DAOTestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for org.yeastrc.ms.dao");
        //$JUnit-BEGIN$
        suite.addTestSuite(MsExperimentDAOImplTest.class);
        suite.addTestSuite(MsEnzymeDAOImplTest.class);
        suite.addTestSuite(MsRunDAOImplTest.class);
        suite.addTestSuite(MsScanDAOImplTest.class);
        
//        // MS2 file data
        suite.addTestSuite(MS2ChargeDependentAnalysisDAOImplTest.class);
        suite.addTestSuite(MS2ScanChargeDAOImplTest.class);
        suite.addTestSuite(MS2ChargeIndependentAnalysisDAOImplTest.class);
        suite.addTestSuite(MS2ScanChargeDAOImplTest.class);
        suite.addTestSuite(MS2RunDAOImplTest.class);
//        
//        
//        // search 
//        suite.addTestSuite(MsProteinMatchDAOImplTest.class);
//        suite.addTestSuite(MsPeptideSearchModDAOImplTest.class);
//        suite.addTestSuite(MsPeptideSearchResultDAOImplTest.class);
//        suite.addTestSuite(MsPeptideSearchDAOImplTest.class);
//        suite.addTestSuite(MsSequenceDatabaseDAOImplTest.class);
//        
//        
//        // SQT search
//        suite.addTestSuite(SQTSearchHeaderDAOImplTest.class);
//        suite.addTestSuite(SQTSpectrumDataDAOImplTest.class);
//        suite.addTestSuite(SQTSearchResultDAOImplTest.class);
//        suite.addTestSuite(SQTPeptideSearchDAOImplTest.class);
        
        
        //$JUnit-END$
        return suite;
    }

}
