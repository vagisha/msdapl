package org.yeastrc.ms.domain.search;

import java.sql.Date;
import java.util.List;

import org.yeastrc.ms.domain.general.MsEnzyme;

public interface MsRunSearch extends MsRunSearchBase {

    /**
     * @return the searchDatabases
     */
    public abstract List<MsSearchDatabase> getSearchDatabases();

    /**
     * @return the staticModifications
     */
    public abstract List<MsSearchModification> getStaticModifications();

    /**
     * @return the dynamicModifications
     */
    public abstract List<MsSearchModification> getDynamicModifications();
    
    /**
     * @return the enzymes used for this search
     */
    public abstract List<MsEnzyme> getEnzymeList();
    
}

interface MsRunSearchBase {
    
    /**
     * @return the originalFileType
     */
    public abstract SearchFileFormat getSearchFileFormat();

    /**
     * @return the searchDate
     */
    public abstract Date getSearchDate();

    /**
     * @return the time taken for the search (in minutes)
     */
    public abstract int getSearchDuration();

}