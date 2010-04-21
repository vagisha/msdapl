/**
 * CommonNameDescription.java
 * @author Vagisha Sharma
 * Apr 18, 2009
 * @version 1.0
 */
package org.yeastrc.nrseq;

import java.io.Serializable;
import java.sql.SQLException;

import org.yeastrc.ms.domain.nrseq.NrDatabase;
import org.yeastrc.ms.domain.nrseq.NrDbProtein;

/**
 * 
 */
public class ProteinReference implements Serializable {

	private int databaseId;
	private String databaseName;
	private boolean isCurrent;
	private String url;
	private String accession;
	private String description;
	
	private ProteinCommonReference commonReference;
    
	public ProteinReference() {}
	

	public ProteinReference(NrDbProtein protein) {
    	this.databaseId = protein.getDatabaseId();
    	this.isCurrent = protein.isCurrent();
    	this.url = protein.getUrl();
    	this.accession = protein.getAccessionString();
    	this.description = protein.getDescription();
    }
	
	public ProteinReference(NrDbProtein protein, StandardDatabase sdb) {
    	this.databaseId = protein.getDatabaseId();
    	this.databaseName = sdb.getDatabaseName();
    	this.isCurrent = protein.isCurrent();
    	this.url = protein.getUrl();
    	this.accession = protein.getAccessionString();
    	this.description = protein.getDescription();
    }
    
	public boolean isCurrent() {
		return isCurrent;
	}

	public int getDatabaseId() {
		return databaseId;
	}
	
	public String getDatabaseName() throws SQLException {
		if(databaseName == null) {
			NrDatabase database = NrseqDatabaseDAO.getInstance().getDatabase(getDatabaseId());
			if(database != null)
				databaseName = database.getName();
		}
		return databaseName;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public boolean getHasExternalLink() {
		return getUrl() != null;
	}
	
	public String getAccession() {
        return this.accession;
    }
	
	public String getShortAccession() {
		if(accession != null) {
			if(accession.length() > 15)
				return accession.substring(0,15)+"...";
			else
				return accession;
		}
		return "";
	}
	
    public String getDescription() {
        return this.description;
    }
    
    public String getShortDescription() {
    	if(description != null) {
			if(description.length() > 90)
				return description.substring(0,90)+"...";
			else
				return description;
		}
		return "";
    }
    
    public void setDatabaseId(int databaseId) {
		this.databaseId = databaseId;
	}

	public void setCurrent(boolean isCurrent) {
		this.isCurrent = isCurrent;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ProteinCommonReference getCommonReference() {
		return commonReference;
	}

	public void setCommonReference(ProteinCommonReference commonReference) {
		this.commonReference = commonReference;
	}
	
	public boolean hasCommonReference() {
		return commonReference != null;
	}
}
