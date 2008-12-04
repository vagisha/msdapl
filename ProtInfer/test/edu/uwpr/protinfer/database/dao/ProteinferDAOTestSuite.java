package edu.uwpr.protinfer.database.dao;

import java.io.IOException;

import edu.uwpr.protinfer.database.dao.ibatis.ProteinferSpectrumMatchDAOTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ProteinferDAOTestSuite {

    private static Runtime runtime = Runtime.getRuntime();
    
    public static Test suite() {
        TestSuite suite = new TestSuite("Test for edu.uwpr.protinfer.database.dao");
        //$JUnit-BEGIN$
        resetDatabase();
        suite.addTestSuite(ProteinferSpectrumMatchDAOTest.class);
//        suite.addTestSuite(ProteinferProteinDAOTest.class);
        //$JUnit-END$
        return suite;
    }
    
    private static void resetDatabase() {
        String cmd = "/bin/sh /Users/silmaril/WORK/UW/PROT_INFER/test/resetDatabase.sh";
        try {
            Process process = runtime.exec(cmd);
            process.waitFor();
            System.out.println("reset database: exit status"+process.exitValue());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
