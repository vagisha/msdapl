package org.yeastrc.ms.dao;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.yeastrc.ms.dao.ms2File.MS2FileChargeDependentAnalysisDAOImplTest;
import org.yeastrc.ms.dao.ms2File.MS2FileChargeIndependentAnalysisDAOImplTest;
import org.yeastrc.ms.dao.ms2File.MS2FileRunDAOImplTest;
import org.yeastrc.ms.dao.ms2File.MS2FileScanChargeDAOImplTest;
import org.yeastrc.ms.dao.sqtFile.SQTPeptideSearchDAOImplTest;
import org.yeastrc.ms.dao.sqtFile.SQTSearchHeaderDAOImplTest;
import org.yeastrc.ms.dao.sqtFile.SQTSearchResultDAOImplTest;
import org.yeastrc.ms.dao.sqtFile.SQTSpectrumDataDAOImplTest;

public class DAOTestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for org.yeastrc.ms.dao");
        //$JUnit-BEGIN$
        suite.addTestSuite(MsExperimentDAOImplTest.class);
        suite.addTestSuite(MsDigestionEnzymeDAOImplTest.class);
        suite.addTestSuite(MsRunDAOImplTest.class);
        suite.addTestSuite(MsScanDAOImplTest.class);
        
        // MS2 file data
        suite.addTestSuite(MS2FileChargeDependentAnalysisDAOImplTest.class);
        suite.addTestSuite(MS2FileScanChargeDAOImplTest.class);
        suite.addTestSuite(MS2FileChargeIndependentAnalysisDAOImplTest.class);
        suite.addTestSuite(MS2FileScanChargeDAOImplTest.class);
        suite.addTestSuite(MS2FileRunDAOImplTest.class);
        
        
        // search 
        suite.addTestSuite(MsProteinMatchDAOImplTest.class);
        suite.addTestSuite(MsSearchModDAOImplTest.class);
        suite.addTestSuite(MsPeptideSearchResultDAOImplTest.class);
        suite.addTestSuite(MsPeptideSearchDAOImplTest.class);
        suite.addTestSuite(MsSequenceDatabaseDAOImplTest.class);
        
        
        // SQT search
        suite.addTestSuite(SQTSearchHeaderDAOImplTest.class);
        suite.addTestSuite(SQTSpectrumDataDAOImplTest.class);
        suite.addTestSuite(SQTSearchResultDAOImplTest.class);
        suite.addTestSuite(SQTPeptideSearchDAOImplTest.class);
        
        
        //$JUnit-END$
        return suite;
    }

}
