package org.yeastrc.ms.domain.search;

public interface MsSearchResultProtein {


    /**
     * @param the accession
     */
    public abstract String getAccession();
    
    /**
     * @return the description
     */
    public abstract String getDescription();

}