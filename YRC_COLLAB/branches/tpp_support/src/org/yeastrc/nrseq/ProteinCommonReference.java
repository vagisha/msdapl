/**
 * ProteinCommonReference.java
 * @author Vagisha Sharma
 * Mar 6, 2010
 * @version 1.0
 */
package org.yeastrc.nrseq;

import java.sql.SQLException;

/**
 * 
 */
public class ProteinCommonReference {

	private StandardDatabase database;
	private String name;
	private String description;
    

	public ProteinCommonReference() {}
	
	public String getDatabaseName() throws SQLException {
		if(database != null)
			return database.getDatabaseName();
		else
			return null;
	}
	
	public StandardDatabase getDatabase() {
		return database;
	}
	
	public void setDatabase(StandardDatabase database) {
		this.database = database;
	}

	public String getName() {
        return this.name;
    }
	
    public String getDescription() {
        return this.description;
    }
    
	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
