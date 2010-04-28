/**
 * StandardDatabaseCache.java
 * @author Vagisha Sharma
 * Mar 24, 2010
 * @version 1.0
 */
package org.yeastrc.nrseq;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.nrseq.NrDatabase;

/**
 * 
 */
public class StandardDatabaseCache {

	private static Map<String, NrDatabase> cache = new HashMap<String, NrDatabase>();
	
	private static final Logger log = Logger.getLogger(StandardDatabaseCache.class.getName());
	
	public static NrDatabase getNrDatabase(StandardDatabase sdb) {
		
		NrDatabase db = cache.get(sdb.getDatabaseName());
		
		if(db == null) {
			NrseqDatabaseDAO dbDao = NrseqDatabaseDAO.getInstance();
			try {
				db = dbDao.getDatabase(sdb.getDatabaseName());
				cache.put(sdb.getDatabaseName(), db);
			} catch (SQLException e) {
				log.error("Lookup of standard database "+sdb.getDatabaseName()+" failed", e);
			}
		}
		
		return db;
	}
}
