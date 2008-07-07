package org.yeastrc.ms.parser.sqtFile;

/**
 * Represents a L line in the SQT file. 
 */
public class DbLocus {

    private String accession; // Locus in which this sequence is found
    private String description; // Description of this locus from database (optional) 

    public DbLocus(String accession, String description) {
        this.accession = accession;
        this.description = description;
    }

    /**
     * @return the accession
     */
    public String getAccession() {
        return accession;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("L\t");
        buf.append(accession);
        if (description != null) {
            buf.append("\t");
            buf.append(description);
        }
        return buf.toString();
    }

}
