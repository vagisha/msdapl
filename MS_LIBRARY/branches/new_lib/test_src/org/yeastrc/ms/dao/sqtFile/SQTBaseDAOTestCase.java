package org.yeastrc.ms.dao.sqtFile;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.dao.ibatis.DAOFactory;
import org.yeastrc.ms.domain.db.MsPeptideSearch;
import org.yeastrc.ms.domain.sqtFile.db.SQTPeptideSearch;
import org.yeastrc.ms.domain.sqtFile.db.SQTSearchHeader;

public class SQTBaseDAOTestCase extends BaseDAOTestCase {

    protected SQTSearchHeaderDAO sqtHeaderDao = DAOFactory.instance().getSqtHeaderDAO();
    protected SQTSearchResultDAO sqtResDao = DAOFactory.instance().getSqtResultDAO();
    protected SQTPeptideSearchDAO sqtSearchDao = DAOFactory.instance().getSqtSearchDAO();
    protected SQTSpectrumDataDAO sqtSpectrumDao = DAOFactory.instance().getSqtSpectrumDAO();
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    protected SQTPeptideSearch makeSQTPeptideSearch(int runId, boolean addSeqDb,
            boolean addStaticMods, boolean addDynaMods,
            boolean addHeaders) {
        MsPeptideSearch msSearch = super.makePeptideSearch(runId, addSeqDb, addStaticMods, addDynaMods);
        SQTPeptideSearch sqtSearch = new SQTPeptideSearch(msSearch);
        sqtSearch.setOriginalFileType("SQT");
        sqtSearch.setSearchEngineName("Sequest");
        sqtSearch.setSearchEngineVersion("1.0");
        
        if (addHeaders) {
            sqtSearch.addHeader(makeHeader("header_1", "value_1"));
            sqtSearch.addHeader(makeHeader("header_2", "value_2"));
        }
        
        return sqtSearch;
    }

    
    protected SQTSearchHeader makeHeader(String name, String value) {
        SQTSearchHeader h = new SQTSearchHeader();
        h.setName(name);
        h.setValue(value);
        return h;
    }
    
}
