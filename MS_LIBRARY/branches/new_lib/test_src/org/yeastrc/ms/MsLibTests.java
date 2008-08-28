package org.yeastrc.ms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.yeastrc.ms.dao.DAOTestSuite;
import org.yeastrc.ms.domain.impl.MsScanDbImplTest;
import org.yeastrc.ms.parser.ms2File.Ms2FileReaderTest;
import org.yeastrc.ms.parser.prolucidParams.ProlucidParamsParserTest;
import org.yeastrc.ms.parser.sequestParams.SequestParamsParserTest;
import org.yeastrc.ms.parser.sqtFile.SQTParserTests;
import org.yeastrc.ms.service.MsExperimentUploaderTest;
import org.yeastrc.ms.util.PeakStringBuilderTest;
import org.yeastrc.ms.util.PeakUtilsTest;
import org.yeastrc.ms.util.Sha1SumCalculatorTest;

public class MsLibTests {

    public static Test suite() {
        
        resetDatabase();
        
        TestSuite suite = new TestSuite("Test for org.yeastrc.ms");
        //$JUnit-BEGIN$
        
//        // domain classes
//        suite.addTestSuite(MsScanDbImplTest.class);
//        
//        // dao classes
//        suite.addTest(DAOTestSuite.suite());
        
        // parser classes
        suite.addTest(SQTParserTests.suite());
        suite.addTestSuite(Ms2FileReaderTest.class);
        suite.addTestSuite(SequestParamsParserTest.class);
        suite.addTestSuite(ProlucidParamsParserTest.class);
        
        // utility classes
//        suite.addTestSuite(PeakStringBuilderTest.class);
//        suite.addTestSuite(PeakUtilsTest.class);
//        suite.addTestSuite(Sha1SumCalculatorTest.class);
//        
//        // upload classes
//        suite.addTestSuite(MsExperimentUploaderTest.class);
        
        //$JUnit-END$
        return suite;
    }
    
    public static void resetDatabase() {
        System.out.println("Resetting database");
        String script = "src/resetDatabase.sh";
        try {
            Process proc = Runtime.getRuntime().exec("sh "+script);
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            String line = reader.readLine();
            while(line != null) {
                System.out.println(line);
                line = reader.readLine();
            }
            reader.close();
            proc.waitFor();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public static void addEnzymes() {
        System.out.println("Adding enzymes");
        String script = "src/addEnzymes.sh";
        try {
            Process proc = Runtime.getRuntime().exec("sh "+script);
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            String line = reader.readLine();
            while(line != null) {
                System.out.println(line);
                line = reader.readLine();
            }
            reader.close();
            proc.waitFor();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
