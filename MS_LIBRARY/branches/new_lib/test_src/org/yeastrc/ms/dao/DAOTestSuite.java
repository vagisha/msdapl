package org.yeastrc.ms.dao;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.yeastrc.ms.dao.ms2File.MS2ChargeDependentAnalysisDAOImplTest;
import org.yeastrc.ms.dao.ms2File.MS2ChargeIndependentAnalysisDAOImplTest;
import org.yeastrc.ms.dao.ms2File.MS2RunDAOImplTest;
import org.yeastrc.ms.dao.ms2File.MS2ScanChargeDAOImplTest;
import org.yeastrc.ms.dao.sqtFile.SQTSearchDAOImplTest;
import org.yeastrc.ms.dao.sqtFile.SQTHeaderDAOImplTest;
import org.yeastrc.ms.dao.sqtFile.SQTSearchResultDAOImplTest;
import org.yeastrc.ms.dao.sqtFile.SQTSearchScanDAOImplTest;

public class DAOTestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for org.yeastrc.ms.dao");
        //$JUnit-BEGIN$
//        suite.addTestSuite(MsExperimentDAOImplTest.class);
//        suite.addTestSuite(MsEnzymeDAOImplTest.class);
//        suite.addTestSuite(MsRunDAOImplTest.class);
//        suite.addTestSuite(MsScanDAOImplTest.class);
        
        // MS2 file data
        suite.addTestSuite(MS2ChargeDependentAnalysisDAOImplTest.class);
        suite.addTestSuite(MS2ScanChargeDAOImplTest.class);
        suite.addTestSuite(MS2ChargeIndependentAnalysisDAOImplTest.class);
        suite.addTestSuite(MS2ScanChargeDAOImplTest.class);
        suite.addTestSuite(MS2RunDAOImplTest.class);
        
        
        // search 
//        suite.addTestSuite(MsSearchResultProteinDAOImplTest.class);
//        suite.addTestSuite(MsSearchModificationDAOImplTest.class);
//        suite.addTestSuite(MsSearchResultDAOImplTest.class);
//        suite.addTestSuite(MsSearchDAOImplTest.class);
//        suite.addTestSuite(MsSearchDatabaseDAOImplTest.class);
//        
//        
//        // SQT search
//        suite.addTestSuite(SQTHeaderDAOImplTest.class);
//        suite.addTestSuite(SQTSearchScanDAOImplTest.class);
//        suite.addTestSuite(SQTSearchResultDAOImplTest.class);
//        suite.addTestSuite(SQTSearchDAOImplTest.class);
        
        
        //$JUnit-END$
        return suite;
    }

}
