/**
 * GOEnrichmentTabular.java
 * @author Vagisha Sharma
 * Jun 11, 2009
 * @version 1.0
 */
package org.yeastrc.www.go;

import java.util.List;

/**
 * 
 */
public class GOEnrichmentTabular {

    private String title;
    private int numInputProteins;
    private int numProteinsInSet; // this is also the number of input proteins that come from the target species.
    private int numProteinsInUniverse;
    
    private List<EnrichedGOTerm> enrichedTerms;
    
    
    public GOEnrichmentTabular() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public int getNumInputProteins() {
		return numInputProteins;
	}

	public void setNumInputProteins(int numInputProteins) {
		this.numInputProteins = numInputProteins;
	}

	public int getNumProteinsInSet() {
        return numProteinsInSet;
    }

    public void setNumProteinsInSet(int numProteinsInSet) {
        this.numProteinsInSet = numProteinsInSet;
    }

    public int getNumProteinsInUniverse() {
        return numProteinsInUniverse;
    }

    public void setNumProteinsInUniverse(int numProteinsInUniverse) {
        this.numProteinsInUniverse = numProteinsInUniverse;
    }

    public List<EnrichedGOTerm> getEnrichedTerms() {
        return enrichedTerms;
    }

    public int getEnrichedTermCount() {
        return this.enrichedTerms.size();
    }
    
    public void setEnrichedTerms(List<EnrichedGOTerm> enrichedTerms) {
        this.enrichedTerms = enrichedTerms;
    }
}
