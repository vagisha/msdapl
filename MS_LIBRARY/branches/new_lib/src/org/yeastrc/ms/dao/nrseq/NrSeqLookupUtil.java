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
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;

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
        String ibatisConfigFile = "org/yeastrc/ms/sqlmap/nrseq/NrSeqSqlMapConfig.xml";
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
     * 
     * @param databaseName
     * @param accession
     * @return
     * @throws NrSeqLookupException if no matching database entry is found
     */
    public static int getProteinId(String databaseName, String accession) throws NrSeqLookupException {
        Map<String, String> map = new HashMap<String, String>(2);
        map.put("dbName", databaseName);
        map.put("accession", accession);
        Integer id = null;
        String statementName = "NrSeq.selectProteinId";
        try {
            id = (Integer) sqlMap.queryForObject(statementName, map);
        }
        catch (SQLException e) {
            log.error("Failed to execute select statement: ", e);
            throw new RuntimeException("Failed to execute select statement: "+statementName, e);
        }
        if (id == null)
            throw new NrSeqLookupException(databaseName, accession);
        return id;
    }
    
    /**
     * 
     * @param databaseName
     * @return
     * @throws NrSeqLookupException if no matching database entry is found
     */
    public static int getDatabaseId(String databaseName) throws NrSeqLookupException {
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
            throw new NrSeqLookupException(databaseName);
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
}
