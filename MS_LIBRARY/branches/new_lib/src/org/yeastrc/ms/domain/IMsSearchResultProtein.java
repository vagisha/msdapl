package org.yeastrc.ms.domain;

public interface IMsSearchResultProtein {


    /**
     * @param the accession
     */
    public abstract String getAccession();
    
    /**
     * @return the description
     */
    public abstract String getDescription();

}