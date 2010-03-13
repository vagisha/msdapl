/**
 * CommonNameLookup.java
 * @author Vagisha Sharma
 * Apr 14, 2009
 * @version 1.0
 */
package org.yeastrc.nrseq;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.databases.flybase.FlyBaseUtils;
import org.yeastrc.databases.sangerpombe.PombeUtils;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.sgd.SGDUtils;
import org.yeastrc.wormbase.WormbaseUtils;

/**
 * 
 */
public class CommonNameLookupUtil {

    private static CommonNameLookupUtil instance;
    
    private static final Logger log = Logger.getLogger(CommonNameLookupUtil.class.getName());
    
    private CommonNameLookupUtil() {}
    
    public static CommonNameLookupUtil getInstance() {
        if(instance == null)
            instance = new CommonNameLookupUtil();
        return instance;
    }

    public ProteinCommonReference getCommonReference(String accession, StandardDatabase db) {
		
    	String commonName = null;
    	String description = null;
    	if(db == StandardDatabase.SGD) {
    		try {
				commonName = SGDUtils.getStandardName(accession);
				description = SGDUtils.getDescription(accession);
			} catch (SQLException e) {
				log.error("Error using SGDUtils", e);
			}
    	}
    	else if(db == StandardDatabase.S_POMBE) {
    		try {
				commonName = PombeUtils.getStandardName(accession);
				description = PombeUtils.getDescription(accession);
			} catch (SQLException e) {
				log.error("Error using PombeUtils", e);
			}
    	}
    	else if (db == StandardDatabase.WORMBASE) {
    		try {
				commonName = WormbaseUtils.getStandardName(accession);
				description = WormbaseUtils.getDescription(accession);
			} catch (SQLException e) {
				log.error("Error using WormbaseUtils", e);
			}
    	}
    	else if (db == StandardDatabase.FLYBASE) {
    		try {
				commonName = FlyBaseUtils.getStandardName(accession);
				description = FlyBaseUtils.getDescription(accession);
			} catch (SQLException e) {
				log.error("Error using FlyBaseUtils", e);
			}
    		db = StandardDatabase.FLYBASE;
    	}
    	else if(db == StandardDatabase.HGNC) {
    		// TODO  For now there is not external database for human common name lookup
    		// the "HGNC (HUGO)" database in YRC_NRSEQ has common names in the
    		// accessionString column of tblProteinDatabase. That is what is being used. 
    	}
		if(commonName != null) {
			ProteinCommonReference ref = new ProteinCommonReference();
			ref.setName(commonName);
			ref.setDescription(description);
			ref.setDatabase(db);
			return ref;
		}
		return null;
	}
    
    public List<Integer> getProteinIdsFromCache(String commonName) throws SQLException {
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        List<Integer> proteinIds = new ArrayList<Integer>();
        
        try {
            conn = DAOFactory.instance().getConnection();
            String sql = "SELECT proteinID FROM nrseqProteinCache WHERE name LIKE '"+commonName+"%'";
            stmt = conn.createStatement();
            
            rs = stmt.executeQuery(sql);

            while(rs.next()) {
                proteinIds.add(rs.getInt(1));
            }
        }
        finally {
            if(conn != null) try {conn.close();}
            catch (SQLException e) {}
            if(stmt != null) try {stmt.close();}
            catch (SQLException e) {}
            if(rs != null) try {rs.close();}
            catch (SQLException e) {}
        }
        
        return proteinIds;
    }
}
