package org.yeastrc.ms;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.yeastrc.ms.dao.DAOTestSuite;
import org.yeastrc.ms.domain.impl.MsScanDbImplTest;
import org.yeastrc.ms.parser.sqtFile.SQTParserTests;
import org.yeastrc.ms.util.PeakStringBuilderTest;
import org.yeastrc.ms.util.PeakUtilsTest;
import org.yeastrc.ms.util.Sha1SumCalculatorTest;

public class MsLibTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for org.yeastrc.ms");
        //$JUnit-BEGIN$
        
        // domain classes
        suite.addTestSuite(MsScanDbImplTest.class);
        
        // dao classes
        suite.addTest(DAOTestSuite.suite());
        
        // parser classes
        suite.addTest(SQTParserTests.suite());
        
        // utility classes
        suite.addTestSuite(PeakStringBuilderTest.class);
        suite.addTestSuite(PeakUtilsTest.class);
        suite.addTestSuite(Sha1SumCalculatorTest.class);
        
        //$JUnit-END$
        return suite;
    }

}
