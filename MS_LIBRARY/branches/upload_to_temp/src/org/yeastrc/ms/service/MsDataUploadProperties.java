/**
 * MsDataUploadProperties.java
 * @author Vagisha Sharma
 * Jun 1, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service;

import java.io.IOException;
import java.io.Reader;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.run.PeakStorageType;

import com.ibatis.common.resources.Resources;

/**
 * 
 */
public class MsDataUploadProperties {

    private static final Logger log = Logger.getLogger(MsDataUploadProperties.class.getName());
    
    private static boolean uploadToTempTables = false;
    private static boolean uploadOneExperiment = false;
    private static int uploadSyncHour;
    private static int uploadSyncMinutes;
    
    private static String mysqlTempDirectory;
    
    private static String backupDirectory;
    
    private static PeakStorageType peakStorageType;
    
    private static boolean checkPeptideProteinMatches = false;
    
    private static boolean useNrseqSuffixTables = true;
    private static boolean useNrseqSuffixInMemory = false;
    private static boolean useSingleQuery = false; // TEMPORARY
    
    static {
        Properties props = new Properties();
        try {
            Reader reader = Resources.getResourceAsReader("msDataDB.properties");
            props.load(reader);
        }
        catch (IOException e) {
            log.error("Error reading properties file msDataDB.properties", e);
        }
        String value = props.getProperty("db.peakdata.storage");
        peakStorageType = PeakStorageType.instance(value);
        log.info("PeakStorageType is "+peakStorageType.name());
        
        value = props.getProperty("upload.usetemp");
        uploadToTempTables = Boolean.valueOf(value);
        log.info("Upload to temp tables: "+uploadToTempTables);
        
        value = props.getProperty("upload.sync.single_experiment");
        uploadOneExperiment = Boolean.valueOf(value);
        log.info("Uploader will sync databases after one experiment: "+uploadOneExperiment);
        
        value = props.getProperty("upload.sync.hour");
        uploadSyncHour = Integer.parseInt(value);
        
        value = props.getProperty("upload.sync.minutes");
        uploadSyncMinutes = Integer.parseInt(value);
        
        mysqlTempDirectory = props.getProperty("upload.temp.dir");
        
        backupDirectory = props.getProperty("backup.dir");
        
        value = props.getProperty("interact.pepxml.checkpeptideproteinmatches");
        checkPeptideProteinMatches = Boolean.parseBoolean(value);
        
        value = props.getProperty("use.nrseq.suffix.tables");
        useNrseqSuffixTables = Boolean.parseBoolean(value);
        
        value = props.getProperty("use.nrseq.suffix.in.memory");
        useNrseqSuffixInMemory = Boolean.parseBoolean(value);
        
        value = props.getProperty("use.single.query");
        useSingleQuery = Boolean.parseBoolean(value);
        
        log.info("Uploader will sync databases at time: "+uploadSyncHour+":"+uploadSyncMinutes);
        
    }
    
    private MsDataUploadProperties() {}
    
    public static boolean uploadToTempTables() {
        return uploadToTempTables;
    }
    
    public static boolean uploadOneExperiment() {
        return uploadOneExperiment;
    }
    
    public static Timestamp uploadTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, uploadSyncHour);
        calendar.set(Calendar.MINUTE, uploadSyncMinutes);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }
    
    public static PeakStorageType getPeakStorageType() {
        return peakStorageType;
    }
    
    public static String getMysqlTempDirectory() {
        return mysqlTempDirectory;
    }
    
    public static String getBackupDirectory() {
        return backupDirectory;
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
