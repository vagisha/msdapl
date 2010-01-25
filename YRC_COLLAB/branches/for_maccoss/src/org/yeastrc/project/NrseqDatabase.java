/**
 * NrseqDatabase.java
 * @author Vagisha Sharma
 * Jan 25, 2010
 * @version 1.0
 */
package org.yeastrc.project;

/**
 * 
 */
public class NrseqDatabase {

    private int id;
    private String fastaFileName;
    private String description;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getFastaFileName() {
        return fastaFileName;
    }
    public void setFastaFileName(String fastaFileName) {
        this.fastaFileName = fastaFileName;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
