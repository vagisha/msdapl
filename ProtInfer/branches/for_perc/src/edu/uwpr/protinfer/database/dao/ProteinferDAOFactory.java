package edu.uwpr.protinfer.database.dao;

import java.io.IOException;
import java.io.Reader;

import org.apache.log4j.Logger;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

import edu.uwpr.protinfer.database.dao.ibatis.ProteinferInputDAO;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferIonDAO;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferPeptideDAO;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferProteinDAO;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferRunDAO;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferSpectrumMatchDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerInputDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerIonDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerParamDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerPeptideBaseDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerPeptideDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerProteinBaseDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerProteinDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerRunDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerSpectrumMatchDAO;

public class ProteinferDAOFactory {

    private static final Logger log = Logger.getLogger(ProteinferDAOFactory.class);
    
    // initialize the SqlMapClient
    private static SqlMapClient sqlMap;
    
    static {
        Reader reader = null;
        String ibatisConfigFile = "ProteinferSqlMapConfig.xml";
        
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
    }
    
    private static ProteinferDAOFactory instance = new ProteinferDAOFactory();
    
    private ProteinferRunDAO pinferRunDao;
    private IdPickerParamDAO pinferParamDao;
    private ProteinferInputDAO pinferInputDao;
    private ProteinferPeptideDAO peptideDao;
    private ProteinferProteinDAO proteinDao;
    private ProteinferIonDAO ionDao;
    private ProteinferSpectrumMatchDAO spectrumMatchDao;
    
    // IDPicker related
    private IdPickerSpectrumMatchDAO idpSpectrumMatchDao;
    private IdPickerIonDAO idpIonDao;
    private IdPickerPeptideDAO idpPeptideDao;
    private IdPickerPeptideBaseDAO idpPeptideBaseDao;
    private IdPickerProteinDAO idpProteinDao;
    private IdPickerProteinBaseDAO idpProteinBaseDao;
    private IdPickerInputDAO idpInputDao;
    private IdPickerRunDAO idpRunDao;
    
    
    private ProteinferDAOFactory() {
        
       pinferRunDao = new ProteinferRunDAO(sqlMap);
       pinferParamDao = new IdPickerParamDAO(sqlMap);
       pinferInputDao = new ProteinferInputDAO(sqlMap);
       spectrumMatchDao = new ProteinferSpectrumMatchDAO(sqlMap, pinferRunDao);
       ionDao = new ProteinferIonDAO(sqlMap);
       peptideDao = new ProteinferPeptideDAO(sqlMap);
       proteinDao = new ProteinferProteinDAO(sqlMap);
       
       
       // IDPicker related
       idpSpectrumMatchDao = new IdPickerSpectrumMatchDAO(sqlMap, spectrumMatchDao);
       idpIonDao = new IdPickerIonDAO(sqlMap, ionDao);
       idpPeptideDao = new IdPickerPeptideDAO(sqlMap, peptideDao);
       idpPeptideBaseDao = new IdPickerPeptideBaseDAO(sqlMap, peptideDao);
       idpProteinDao = new IdPickerProteinDAO(sqlMap, proteinDao);
       idpProteinBaseDao = new IdPickerProteinBaseDAO(sqlMap, proteinDao);
       idpInputDao = new IdPickerInputDAO(sqlMap, pinferInputDao);
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
    
    //---------------------------------------------------------------------
    // Protein Inference run related
    //---------------------------------------------------------------------
    public ProteinferRunDAO getProteinferRunDao() {
        return pinferRunDao;
    }

    public IdPickerParamDAO getProteinferParamDao() {
        return pinferParamDao;
    }

    public ProteinferInputDAO getProteinferInputDao() {
        return pinferInputDao;
    }
    
    public ProteinferProteinDAO getProteinferProteinDao() {
        return proteinDao;
    }

    public ProteinferPeptideDAO getProteinferPeptideDao() {
        return peptideDao;
    }
        
    public ProteinferIonDAO getProteinferIonDao() {
        return ionDao;
    }
    
    public ProteinferSpectrumMatchDAO getProteinferSpectrumMatchDao() {
        return spectrumMatchDao;
    }
    
    //---------------------------------------------------------------------
    // IDPicker related
    //---------------------------------------------------------------------
    public IdPickerSpectrumMatchDAO getIdPickerSpectrumMatchDao() {
        return idpSpectrumMatchDao;
    }
    
    public IdPickerIonDAO getIdPickerIonDao() {
        return idpIonDao;
    }
    
    public IdPickerPeptideBaseDAO getIdPickerPeptideBaseDao() {
        return idpPeptideBaseDao;
    }
    
    public IdPickerPeptideDAO getIdPickerPeptideDao() {
        return idpPeptideDao;
    }
    
    public IdPickerProteinDAO getIdPickerProteinDao() {
        return idpProteinDao;
    }
    
    public IdPickerProteinBaseDAO getIdPickerProteinBaseDao() {
        return idpProteinBaseDao;
    }
    
    public IdPickerInputDAO getIdPickerInputDao() {
        return idpInputDao;
    }
    
    public IdPickerRunDAO getIdPickerRunDao() {
        return idpRunDao;
    }
}
