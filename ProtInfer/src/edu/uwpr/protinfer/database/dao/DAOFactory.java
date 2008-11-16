package edu.uwpr.protinfer.database.dao;

import java.io.IOException;
import java.io.Reader;

import org.apache.log4j.Logger;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

public class DAOFactory {

    private static final Logger log = Logger.getLogger(DAOFactory.class);
    
    // initialize the SqlMapClient
    private static SqlMapClient sqlMap;
    
    static {
        Reader reader = null;
        String ibatisConfigFile = "edu/uwpr/protinfer/database/sqlmap/ProteinferSqlMapConfig.xml";
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
        System.out.println("Loaded Ibatis SQL map config: "+ibatisConfigFile);
    }
    
    private static DAOFactory instance = new DAOFactory();
    
    private ProteinferRunDAO pinferRunDao;
    private ProteinferFilterDAO pinferFilterDao;
    private ProteinferInputDAO pinferInputDao;
    private ProteinferPeptideDAO peptideDao;
    private ProteinferProteinDAO proteinDao;
    private ProteinferSpectrumMatchDAO spectrumMatchDao;
    
    
    private DAOFactory() {
        
       pinferRunDao = new ProteinferRunDAO(sqlMap);
       pinferFilterDao = new ProteinferFilterDAO(sqlMap);
       pinferInputDao = new ProteinferInputDAO(sqlMap);
       peptideDao = new ProteinferPeptideDAO(sqlMap);
       proteinDao = new ProteinferProteinDAO(sqlMap);
       spectrumMatchDao = new ProteinferSpectrumMatchDAO(sqlMap);
    }
    
    public static DAOFactory instance() {
        return instance;
    }

    public ProteinferRunDAO getProteinferRunDao() {
        return pinferRunDao;
    }

    public ProteinferFilterDAO getProteinferFilterDao() {
        return pinferFilterDao;
    }

    public ProteinferInputDAO getProteinferInputDao() {
        return pinferInputDao;
    }

    public ProteinferPeptideDAO getProteinferPeptideDao() {
        return peptideDao;
    }

    public ProteinferProteinDAO getProteinferProteinDao() {
        return proteinDao;
    }
    
    public ProteinferSpectrumMatchDAO getProteinferSpectrumMatchDao() {
        return spectrumMatchDao;
    }
    
}
