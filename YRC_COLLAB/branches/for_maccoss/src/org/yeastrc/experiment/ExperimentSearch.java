/**
 * ExperimentSearch.java
 * @author Vagisha Sharma
 * Apr 2, 2009
 * @version 1.0
 */
package org.yeastrc.experiment;

import java.sql.Date;
import java.util.List;

import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.Program;

/**
 * 
 */
public class ExperimentSearch implements MsSearch{

    private final MsSearch search;
    private List<SearchFile> files; 
    
    public ExperimentSearch(MsSearch search) {
        this.search = search;
    }
    
    public List<SearchFile> getFiles() {
        return files;
    }
    
    public void setFiles(List<SearchFile> files) {
        this.files = files;
    }
    
    @Override
    public int getId() {
        return search.getId();
    }
    
    @Override
    public List<MsResidueModification> getDynamicResidueMods() {
        return search.getDynamicResidueMods();
    }

    @Override
    public List<MsTerminalModification> getDynamicTerminalMods() {
        return search.getDynamicTerminalMods();
    }

    @Override
    public List<MsEnzyme> getEnzymeList() {
        return search.getEnzymeList();
    }

    @Override
    public int getExperimentId() {
        return search.getExperimentId();
    }

    @Override
    public List<MsSearchDatabase> getSearchDatabases() {
        return search.getSearchDatabases();
    }

    @Override
    public List<MsResidueModification> getStaticResidueMods() {
        return search.getStaticResidueMods();
    }

    @Override
    public List<MsTerminalModification> getStaticTerminalMods() {
        return search.getStaticTerminalMods();
    }

    @Override
    public Date getUploadDate() {
        return search.getUploadDate();
    }

    @Override
    public Date getSearchDate() {
        return search.getSearchDate();
    }

    @Override
    public Program getSearchProgram() {
        return search.getSearchProgram();
    }

    @Override
    public String getSearchProgramVersion() {
        return search.getSearchProgramVersion();
    }

    @Override
    public String getServerDirectory() {
        return search.getServerDirectory();
    }

}
