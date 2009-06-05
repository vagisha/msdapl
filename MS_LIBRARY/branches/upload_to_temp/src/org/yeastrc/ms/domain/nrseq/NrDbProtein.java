package org.yeastrc.ms.domain.nrseq;

public class NrDbProtein {

    private int id;
    private int databaseId;
    private String accessionString;
    private int proteinId;
    private String description;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getDatabaseId() {
        return databaseId;
    }
    public void setDatabaseId(int databaseId) {
        this.databaseId = databaseId;
    }
    public String getAccessionString() {
        return accessionString;
    }
    public void setAccessionString(String accessionString) {
        this.accessionString = accessionString;
    }
    public int getProteinId() {
        return proteinId;
    }
    public void setProteinId(int proteinId) {
        this.proteinId = proteinId;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
