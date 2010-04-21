/**
 * NrProtein.java
 * @author Vagisha Sharma
 * Mar 5, 2010
 * @version 1.0
 */
package org.yeastrc.ms.domain.nrseq;

import java.io.Serializable;

/**
 * 
 */
public class NrProtein implements Serializable {

	private int id;
	private int sequenceId;
    private int speciesId;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getSequenceId() {
        return sequenceId;
    }
    public void setSequenceId(int sequenceId) {
        this.sequenceId = sequenceId;
    }
    public int getSpeciesId() {
        return speciesId;
    }
    public void setSpeciesId(int speciesId) {
        this.speciesId = speciesId;
    }
}
