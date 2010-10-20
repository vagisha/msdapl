package edu.uwpr.protinfer.infer;

public class Protein {

    private String accession;
    private String description = "";
    private int id; // could be a database id
    private boolean isDecoy = false;
    private boolean isAccepted = false;
    private boolean isSubset = false;
    private int proteinClusterId;
    private int proteinGroupId;
    
    /**
     * @param accession
     * @param id unique id for this protein
     */
    public Protein(String accession, int id) {
        this.accession = accession;
        this.id = id;
    }
    
    public String getAccession() {
        return accession;
    }
    
    public void setAccession(String accession) {
        this.accession = accession;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setDecoy() {
        this.isDecoy = true;
    }
    
    public boolean isDecoy() {
        return isDecoy;
    }
    
    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean isAccepted) {
        this.isAccepted = isAccepted;
    }
    
    public boolean isSubset() {
		return isSubset;
	}

	public void setSubset(boolean isSubset) {
		this.isSubset = isSubset;
	}

	public void setProteinClusterId(int proteinClusterId) {
        this.proteinClusterId = proteinClusterId;
    }

    public int getProteinClusterId() {
        return proteinClusterId;
    }
    
    public int getProteinGroupId() {
        return proteinGroupId;
    }

    public void setProteinGroupId(int proteinGroupId) {
        this.proteinGroupId = proteinGroupId;
    }
    
    public String toString() {
        return accession+"\tID:"+id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if(description != null)
            this.description = description;
    }
}
