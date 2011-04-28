/**
 * MsDataUploadProperties.java
 * @author Vagisha Sharma
 * Jun 1, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.run.PeakStorageType;

import com.ibatis.common.resources.Resources;

/**
 * 
 */
public class MsDataUploadProperties {

    private static final Logger log = Logger.getLogger(MsDataUploadProperties.class.getName());
    
    private static boolean doSqtBackup;
    private static String backupDirectory;
    
    private static String percRunDirectory;
    
    private static PeakStorageType peakStorageType;
    
    private static boolean checkPeptideProteinMatches = false;
    
    private static boolean useNrseqSuffixTables = true;
    private static boolean useNrseqSuffixInMemory = false;
    private static boolean useSingleQuery = false; // TEMPORARY
    
    static {
        Properties props = new Properties();
        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader("msDataDB.properties");
            props.load(reader);
            String value = props.getProperty("db.peakdata.storage");
            peakStorageType = PeakStorageType.instance(value);
            log.info("PeakStorageType is "+peakStorageType.name());
            
            backupDirectory = props.getProperty("backup.dir");
            doSqtBackup = Boolean.parseBoolean(props.getProperty("backup.sqt"));
            
            percRunDirectory = props.getProperty("perc.run.dir");
            
            value = props.getProperty("interact.pepxml.checkpeptideproteinmatches");
            checkPeptideProteinMatches = Boolean.parseBoolean(value);
            
            value = props.getProperty("use.nrseq.suffix.tables");
            useNrseqSuffixTables = Boolean.parseBoolean(value);
            
            value = props.getProperty("use.nrseq.suffix.in.memory");
            useNrseqSuffixInMemory = Boolean.parseBoolean(value);
            
            value = props.getProperty("use.single.query");
            useSingleQuery = Boolean.parseBoolean(value);
            
        }
        catch (IOException e) {
            log.error("Error reading properties file msDataDB.properties", e);
        }
        finally {
        	if(reader != null) try {reader.close();} catch(IOException e){}
        }
        
    }
    
    private MsDataUploadProperties() {}
    
    public static PeakStorageType getPeakStorageType() {
        return peakStorageType;
    }
    
    public static String getBackupDirectory() {
        return backupDirectory;
    }
    
    public static boolean doSqtBackup() {
    	return doSqtBackup;
    }
    
    public static String getPercolatorRunDirectory() {
        return percRunDirectory;
    }
    
    public static boolean getCheckPeptideProteinMatches() {
        return checkPeptideProteinMatches;
    }
    
    public static boolean useNrseqSuffixTables() {
        return useNrseqSuffixTables;
    }
    
    public static boolean useNrseqSuffixInMemory() {
        return useNrseqSuffixInMemory;
    }
    
    public static boolean useSingleQuery() {
        return useSingleQuery;
    }
}
