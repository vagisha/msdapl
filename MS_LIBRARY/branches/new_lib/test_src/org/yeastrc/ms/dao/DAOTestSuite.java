package org.yeastrc.ms.dao;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.yeastrc.ms.dao.sqtFile.SQTSearchHeaderDAOImplTest;

public class DAOTestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for org.yeastrc.ms.dao");
        //$JUnit-BEGIN$
        suite.addTestSuite(MsExperimentDAOImplTest.class);
        suite.addTestSuite(MsDigestionEnzymeDAOImplTest.class);
        suite.addTestSuite(MsRunDAOImplTest.class);
        suite.addTestSuite(MsScanDAOImplTest.class);
        
        // search results
        suite.addTestSuite(MsProteinMatchDAOImplTest.class);
        suite.addTestSuite(MsSearchModDAOImplTest.class);
        suite.addTestSuite(MsPeptideSearchResultDAOImplTest.class);
        suite.addTestSuite(MsPeptideSearchDAOImplTest.class);
        
        // search
        suite.addTestSuite(MsSequenceDatabaseDAOImplTest.class);
        
        
        // SQT search
        suite.addTestSuite(SQTSearchHeaderDAOImplTest.class);
        
        
        //$JUnit-END$
        return suite;
    }

}
