package edu.uwpr.protinfer.database.dao;

import java.io.IOException;
import java.io.Reader;

import org.apache.log4j.Logger;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

import edu.uwpr.protinfer.database.dao.ibatis.ProteinferFilterDAO;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferInputDAO;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferPeptideDAO;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferProteinDAO;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferRunDAO;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferSpectrumMatchDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerPeptideDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerProteinDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerRunDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerSpectrumMatchDAO;

public class ProteinferDAOFactory {

    private static final Logger log = Logger.getLogger(ProteinferDAOFactory.class);
    
    // initialize the SqlMapClient
    private static SqlMapClient sqlMap;
    
    static {
//        Reader reader = null;
//        String ibatisConfigFile = "edu/uwpr/protinfer/database/sqlmap/ProteinferSqlMapConfig.xml";
//        try {
//            reader = Resources.getResourceAsReader(ibatisConfigFile);
//            sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
//        }
//        catch (IOException e) {
//            log.error("Error reading Ibatis config xml: "+ibatisConfigFile, e);
//            throw new RuntimeException("Error reading Ibatis config xml: "+ibatisConfigFile, e);
//        }
//        catch (Exception e) {
//            log.error("Error initializing "+ProteinferDAOFactory.class.getName()+" class: ", e);
//            throw new RuntimeException("Error initializing "+ProteinferDAOFactory.class.getName()+" class: ", e);
//        }
//        System.out.println("Loaded Ibatis SQL map config: "+ibatisConfigFile);
    }
    
    private static ProteinferDAOFactory instance = new ProteinferDAOFactory();
    
    private ProteinferRunDAO pinferRunDao;
    private ProteinferFilterDAO pinferFilterDao;
    private ProteinferInputDAO pinferInputDao;
    private ProteinferPeptideDAO peptideDao;
    private ProteinferProteinDAO proteinDao;
    private ProteinferSpectrumMatchDAO spectrumMatchDao;
    
    // IDPicker related
    private IdPickerSpectrumMatchDAO idpSpectrumMatchDao;
    private IdPickerPeptideDAO idpPeptideDao;
    private IdPickerProteinDAO idpProteinDao;
    private IdPickerRunDAO idpRunDao;
    
    
    private ProteinferDAOFactory() {
        
       pinferRunDao = new ProteinferRunDAO(sqlMap);
       pinferFilterDao = new ProteinferFilterDAO(sqlMap);
       pinferInputDao = new ProteinferInputDAO(sqlMap);
       spectrumMatchDao = new ProteinferSpectrumMatchDAO(sqlMap);
       peptideDao = new ProteinferPeptideDAO(sqlMap, spectrumMatchDao);
       proteinDao = new ProteinferProteinDAO(sqlMap, peptideDao);
       
       
       // IDPicker related
       idpSpectrumMatchDao = new IdPickerSpectrumMatchDAO(sqlMap, spectrumMatchDao);
       idpPeptideDao = new IdPickerPeptideDAO(sqlMap, peptideDao, idpSpectrumMatchDao);
       idpProteinDao = new IdPickerProteinDAO(sqlMap, proteinDao, idpPeptideDao);
       idpRunDao = new IdPickerRunDAO(sqlMap, pinferRunDao);
    }
    
    public static ProteinferDAOFactory instance() {
        return instance;
    }

    public static ProteinferDAOFactory testInstance() {
        Reader reader = null;
        String ibatisConfigFile = "edu/uwpr/protinfer/database/sqlmap/TestProteinferSqlMapConfig.xml";
        try {
            reader = Resources.getResourceAsReader(ibatisConfigFile);
            sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
        }
        catch (IOException e) {
            log.error("Error reading Ibatis config xml: "+ibatisConfigFile, e);
            throw new RuntimeException("Error reading Ibatis config xml: "+ibatisConfigFile, e);
        }
        catch (Exception e) {
            log.error("Error initializing "+ProteinferDAOFactory.class.getName()+" class: ", e);
            throw new RuntimeException("Error initializing "+ProteinferDAOFactory.class.getName()+" class: ", e);
        }
        System.out.println("Loaded Ibatis SQL map config: "+ibatisConfigFile);
        return new ProteinferDAOFactory();
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
    
    // IDPicker related
    public IdPickerPeptideDAO getIdPickerPeptideDao() {
        return idpPeptideDao;
    }
    
    public IdPickerProteinDAO getIdPickerProteinDao() {
        return idpProteinDao;
    }
    
    public IdPickerSpectrumMatchDAO getIdPickerSpectrumMatchDao() {
        return idpSpectrumMatchDao;
    }
    
    public IdPickerRunDAO getIdPickerRunDao() {
        return idpRunDao;
    }
}
