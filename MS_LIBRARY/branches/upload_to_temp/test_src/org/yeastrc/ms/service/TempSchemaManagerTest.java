package org.yeastrc.ms.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.domain.general.MsExperiment;
import org.yeastrc.ms.domain.general.impl.ExperimentBean;
import org.yeastrc.ms.service.database.TempSchemaManager;
import org.yeastrc.ms.service.database.TempSchemaManagerException;
import org.yeastrc.ms.upload.dao.UploadDAOFactory;

public class TempSchemaManagerTest extends TestCase {

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
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testGetTableNames() {
        
        resetDatabase();
        
        TempSchemaManager manager = TempSchemaManager.getInstance();
        try {
            List<String> tableNames = manager.getMainTableNames();
            assertNotNull(tableNames);
            assertTrue(tableNames.size() > 0);
        }
        catch (SQLException e) {
            fail(e.getMessage());
        }
    }
    
    public final void testCreateTempSchema1() {
        
        resetDatabase();
        
        TempSchemaManager manager = TempSchemaManager.getInstance();
        
        List<String> tableNames = null;
        List<String> tempSchemaTables = null;
        
        try {
            tempSchemaTables = manager.getTempTableNames();
        }
        catch (SQLException e1) {
            assertEquals(e1.getMessage(), "Cannot create PoolableConnectionFactory (Unknown database 'msdata_junit_temp')");
        }
        
        
        try {
            tableNames = manager.getMainTableNames();
        }
        catch(SQLException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        
        
        assertTrue(tableNames.size() > 0);
        
        try {
            manager.createTempSchema();
            tempSchemaTables = manager.getTempTableNames();
        }
        catch (SQLException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (TempSchemaManagerException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        
        assertEquals(tableNames.size(), tempSchemaTables.size());
        Collections.sort(tableNames);
        Collections.sort(tempSchemaTables);
        for(int i = 0; i < tableNames.size(); i++) {
            assertEquals(tableNames.get(i), tempSchemaTables.get(i));
        }
    }
    
    public final void testCreateTempSchema2() {
        
        resetDatabase();
        
        TempSchemaManager manager = TempSchemaManager.getInstance();
        
        // create the temporary database
        try {
            manager.createTempSchema();
        }
        catch (SQLException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (TempSchemaManagerException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        
        // insert some experiment in the temp table.  The returned ID should be 1
        // since this database was created from an empty main database
        UploadDAOFactory uploadFactory = UploadDAOFactory.getInstance();
        assertEquals(1, uploadFactory.getMsExperimentDAO().saveExperiment(makeExperiment()));
        assertEquals(2, uploadFactory.getMsExperimentDAO().saveExperiment(makeExperiment()));
        assertEquals(3, uploadFactory.getMsExperimentDAO().saveExperiment(makeExperiment()));
        
        
        // reset the database
        resetDatabase();
        
        // put some data in the main database
        DAOFactory mainFactory = DAOFactory.instance();
        assertEquals(1, mainFactory.getMsExperimentDAO().saveExperiment(makeExperiment()));
        assertEquals(2, mainFactory.getMsExperimentDAO().saveExperiment(makeExperiment()));
        
        
        // create the temporary database AGAIN
        try {
            manager.createTempSchema();
        }
        catch (SQLException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (TempSchemaManagerException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        
        // insert an experiment in the temp table.  The returned ID should be 3
        assertEquals(3, uploadFactory.getMsExperimentDAO().saveExperiment(makeExperiment()));
        
    }

//    public final void testFlushToMainDatabase() {
//        
//        TempSchemaManager manager = TempSchemaManager.getInstance();
//        try {
//            manager.createTempSchema();
//        }
//        catch (SQLException e) {
//            e.printStackTrace();
//            fail(e.getMessage());
//        }
//        catch (TempSchemaManagerException e) {
//            e.printStackTrace();
//            fail(e.getMessage());
//        }
//        
//        try {
//            manager.flushToMainDatabase();
//        }
//        catch (SQLException e) {
//            e.printStackTrace();
//            fail(e.getMessage());
//        }
//        catch (TempSchemaManagerException e) {
//            e.printStackTrace();
//            fail(e.getMessage());
//        }
//    }

    private MsExperiment makeExperiment() {
        ExperimentBean expt = new ExperimentBean();
        expt.setComments("some comments");
        expt.setServerAddress("remote");
        expt.setServerDirectory("directory");
        expt.setUploadDate(new java.sql.Date(new java.util.Date().getTime()));
        return expt;
    }
}
