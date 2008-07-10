package org.yeastrc.ms.dto;

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