package org.yeastrc.ms.parser.sqtFile;

import org.yeastrc.ms.parser.sqtFile.sequest.SequestResultPeptideBuilderTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SQTParserTests {

    public static Test suite() {
        TestSuite suite = new TestSuite(
                "Test for org.yeastrc.ms.parser.sqtFile");
        //$JUnit-BEGIN$
        suite.addTestSuite(HeaderTest.class);
        suite.addTestSuite(SQTParserTest.class);
        suite.addTestSuite(SequestResultPeptideBuilderTest.class);
        suite.addTestSuite(HeaderStaticModificationTest.class);
        suite.addTestSuite(HeaderDynamicModificationTest.class);
        //$JUnit-END$
        return suite;
    }

}
