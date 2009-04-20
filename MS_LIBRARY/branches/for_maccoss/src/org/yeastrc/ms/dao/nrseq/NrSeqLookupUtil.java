/**
 * NrSeqLookupUtil.java
 * @author Vagisha Sharma
 * Aug 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.nrseq;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.domain.nrseq.NrDbProtein;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 * 
 */
public class NrSeqLookupUtil {

    
private static final Logger log = Logger.getLogger(DAOFactory.class);
    
    // initialize the SqlMapClient
    private static SqlMapClient sqlMap;
    
    static {
        Reader reader = null;
        String ibatisConfigFile = "NrSeqSqlMapConfig.xml";
        try {
            reader = Resources.getResourceAsReader(ibatisConfigFile);
            sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
        }
        catch (IOException e) {
            log.error("Error reading Ibatis config xml: "+ibatisConfigFile, e);
            throw new RuntimeException("Error reading Ibatis config xml: "+ibatisConfigFile, e);
        }
        catch (Exception e) {
            log.error("Error initializing "+DAOFactory.class.getName()+" class: ", e);
            throw new RuntimeException("Error initializing "+DAOFactory.class.getName()+" class: ", e);
        }
        System.out.println("Loaded Ibatis SQL map config");
    }
    
    
    private NrSeqLookupUtil() {}
    
    /**
     * Returns a match that matches the given accession exactly.
     * @param databaseName
     * @param accession
     * @return
     */
    public static int getDbProteinId(String databaseName, String accession){
        Map<String, String> map = new HashMap<String, String>(2);
        map.put("dbName", databaseName);
        map.put("accession", accession);
        Integer id = null;
        String statementName = "NrSeq.selectDbProteinIdForDbNameExactMatch";
        try {
            id = (Integer) sqlMap.queryForObject(statementName, map);
        }
        catch (SQLException e) {
            log.error("Failed to execute select statement: ", e);
            throw new RuntimeException("Failed to execute select statement: "+statementName, e);
        }
        if (id == null)
            return 0;
        return id;
    }
    
    public static NrDbProtein getDbProtein(String databaseName, String accession){
        Map<String, String> map = new HashMap<String, String>(2);
        map.put("dbName", databaseName);
        map.put("accession", accession);
        String statementName = "NrSeq.selectDbProteinForDbNameExactMatch";
        try {
            return (NrDbProtein) sqlMap.queryForObject(statementName, map);
        }
        catch (SQLException e) {
            log.error("Failed to execute select statement: ", e);
            throw new RuntimeException("Failed to execute select statement: "+statementName, e);
        }
    }
    
    /**
     * Returns a match that matches the given accession exactly.
     * @param databaseId
     * @param accession
     * @return
     */
    public static int getDbProteinId(int databaseId, String accession) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("dbId", databaseId);
        map.put("accession", accession);
        Integer id = null;
        String statementName = "NrSeq.selectDbProteinIdForDbIdExactMatch";
        try {
            id = (Integer) sqlMap.queryForObject(statementName, map);
        }
        catch (SQLException e) {
            log.error("Failed to execute select statement: ", e);
            throw new RuntimeException("Failed to execute select statement: "+statementName, e);
        }
        if (id == null)
            return 0;
        return id;
    }
    
    public static NrDbProtein getDbProtein(int databaseId, String accession) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("dbId", databaseId);
        map.put("accession", accession);
        String statementName = "NrSeq.selectDbProteinForDbIdExactMatch";
        try {
            return  (NrDbProtein) sqlMap.queryForObject(statementName, map);
        }
        catch (SQLException e) {
            log.error("Failed to execute select statement: ", e);
            throw new RuntimeException("Failed to execute select statement: "+statementName, e);
        }
    }
    
    /**
     * 
     * @param databaseId
     * @param accession
     * @return
     */
    public static List<Integer> getDbProteinIdsPartialAccession(int databaseId, String accession) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("dbId", databaseId);
        map.put("accession", accession+"%");
        String statementName = "NrSeq.selectDbProteinIdForDbIdPartialAcc";
        try {
            return sqlMap.queryForList(statementName, map);
        }
        catch (SQLException e) {
            log.error("Failed to execute select statement: ", e);
            throw new RuntimeException("Failed to execute select statement: "+statementName, e);
        }
    }
    
    /**
     * 
     * @param databaseId
     * @param accession
     * @return
     */
    public static List<Integer> getDbProteinIdsForPeptidePartialAccession(int databaseId, String accession, String peptide) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("dbId", databaseId);
        map.put("accession", accession+"%");
        map.put("sequence", "%"+peptide+"%");
        String statementName = "NrSeq.selectDbProteinIdForDbIdAndPeptideAndPartialAcc";
        try {
            return sqlMap.queryForList(statementName, map);
        }
        catch (SQLException e) {
            log.error("Failed to execute select statement: ", e);
            throw new RuntimeException("Failed to execute select statement: "+statementName, e);
        }
    }
    
    /**
     * 
     * @param databaseName
     * @return
     */
    public static int getDatabaseId(String databaseName) {
        String statementName = "NrSeq.selectDatabaseId";
        Integer id = null;
        try {
            id = (Integer) sqlMap.queryForObject(statementName, databaseName);
        }
        catch (SQLException e) {
            log.error("Failed to execute select statement: ", e);
            throw new RuntimeException("Failed to execute select statement: "+statementName, e);
        }
        if (id == null)
            return 0;
        return id;
    }
    
    /**
     * 
     * @param searchDatabaseId
     * @param proteinId
     * @return
     * @throws NrSeqLookupException if no matching database entry is found
     */
    public static String getProteinAccession(int searchDatabaseId, int proteinId) throws NrSeqLookupException {
        String statementName = "NrSeq.selectProteinAccession";
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("databaseId", searchDatabaseId);
        map.put("proteinId", proteinId);
        try {
            String acc = (String) sqlMap.queryForObject(statementName, map);
            if (acc == null)
                throw new NrSeqLookupException(searchDatabaseId, proteinId);
            return acc;
        }
        catch (SQLException e) {
            log.error("Failed to execute select statement: ", e);
            throw new RuntimeException("Failed to execute select statement: "+statementName, e);
        }
    }
    
    public static NrDbProtein getDbProtein(int dbProtId) {
        String statementName = "NrSeq.selectDbProtein";
        try {
            NrDbProtein dbProt = (NrDbProtein) sqlMap.queryForObject(statementName, dbProtId);
            if (dbProt == null)
                throw new NrSeqLookupException(dbProtId);
            return dbProt;
        }
        catch (SQLException e) {
            log.error("Failed to execute select statement: ", e);
            throw new RuntimeException("Failed to execute select statement: "+statementName, e);
        }
    }
    
//    public static NrDbProtein getDbProtein(int databaseId, int proteinId) {
//        Map<String, Integer> map = new HashMap<String, Integer>(2);
//        map.put("dbId", databaseId);
//        map.put("proteinId", proteinId);
//        String statementName = "NrSeq.selectDbProteinForDbIdProteinId";
//        try {
//            return  (NrDbProtein) sqlMap.queryForObject(statementName, map);
//        }
//        catch (SQLException e) {
//            log.error("Failed to execute select statement: ", e);
//            throw new RuntimeException("Failed to execute select statement: "+statementName, e);
//        }
//    }
    
    public static List<NrDbProtein> getProtein(int proteinId, List<Integer> dbIds) {
        String statementName = "NrSeq.selectProtein";
        
        if(dbIds == null || dbIds.size() == 0)
            throw new IllegalArgumentException("At least one database ID is required to search for protein");
        
        String dbIdStr = "";
        for(int id: dbIds)
            dbIdStr += ","+id;
        dbIdStr = dbIdStr.substring(1); // remove first comma
        dbIdStr = "("+dbIdStr+")";
        
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("databaseIds", dbIdStr);
        map.put("proteinId", proteinId);
        try {
            return sqlMap.queryForList(statementName, map);
        }
        catch (SQLException e) {
            log.error("Failed to execute select statement: ", e);
            throw new RuntimeException("Failed to execute select statement: "+statementName, e);
        }
    }
    
    public static String getProteinSequence(int proteinId) {
        String statementName = "NrSeq.selectProteinSequence";
        try {
            return (String) sqlMap.queryForObject(statementName, proteinId);
        }
        catch (SQLException e) {
            log.error("Failed to execute select statement: ", e);
            throw new RuntimeException("Failed to execute select statement: "+statementName, e);
        }
    }
    
    public static String getProteinSequenceForNrSeqDbProtId(int nrDbProtId) {
        String statementName = "NrSeq.selectProteinSequenceForNrDbProt";
        try {
            return (String) sqlMap.queryForObject(statementName, nrDbProtId);
        }
        catch (SQLException e) {
            log.error("Failed to execute select statement: ", e);
            throw new RuntimeException("Failed to execute select statement: "+statementName, e);
        }
    }
    
    public static List<NrDbProtein> getDbProteinsForDescription(List<Integer> dbIds, String description) {
        
        if(dbIds == null || dbIds.size() == 0)
            throw new IllegalArgumentException("At least one database ID is required to search for protein description");
        
        String dbIdStr = "";
        for(int id: dbIds)
            dbIdStr += ","+id;
        dbIdStr = dbIdStr.substring(1); // remove first comma
        dbIdStr = "("+dbIdStr+")";
        
        String statementName = "NrSeq.selectDbProteinIdsForDescription";
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("databaseIds", dbIdStr);
        map.put("description", "%"+description+"%");
        try {
            return sqlMap.queryForList(statementName, map);
        }
        catch (SQLException e) {
            log.error("Failed to execute select statement: ", e);
            throw new RuntimeException("Failed to execute select statement: "+statementName, e);
        }
    }
    
    public static List<NrDbProtein> getDbProteinsForAccession(List<Integer> dbIds, String accession) {
        
        if(dbIds == null || dbIds.size() == 0)
            throw new IllegalArgumentException("At least one database ID is required to search for protein description");
        
        String dbIdStr = "";
        for(int id: dbIds)
            dbIdStr += ","+id;
        dbIdStr = dbIdStr.substring(1); // remove first comma
        dbIdStr = "("+dbIdStr+")";
        
        String statementName = "NrSeq.selectDbProteinIdsForAccession";
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("databaseIds", dbIdStr);
//        map.put("accession", "%"+accession+"%");
        map.put("accession", accession+"%");
        
        try {
            return sqlMap.queryForList(statementName, map);
        }
        catch (SQLException e) {
            log.error("Failed to execute select statement: ", e);
            throw new RuntimeException("Failed to execute select statement: "+statementName, e);
        }
    }
    
    public static List<Integer> getProteinIdsForAccession(String accession) {
        
        String statementName = "NrSeq.selectProteinIdsForAccession";
        String acc = accession+"%";
        
        try {
            return sqlMap.queryForList(statementName, acc);
        }
        catch (SQLException e) {
            log.error("Failed to execute select statement: ", e);
            throw new RuntimeException("Failed to execute select statement: "+statementName, e);
        }
    }
    
}
