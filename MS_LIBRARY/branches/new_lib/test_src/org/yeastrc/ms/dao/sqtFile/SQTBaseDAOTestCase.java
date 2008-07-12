package org.yeastrc.ms.dao.sqtFile;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.dao.ibatis.DAOFactory;
import org.yeastrc.ms.domain.db.MsSearchDbImpl;
import org.yeastrc.ms.domain.sqtFile.db.SQTSearchDbImpl;
import org.yeastrc.ms.domain.sqtFile.db.SQTHeaderDbImpl;

public class SQTBaseDAOTestCase extends BaseDAOTestCase {

    protected SQTHeaderDAO sqtHeaderDao = DAOFactory.instance().getSqtHeaderDAO();
    protected SQTSearchResultDAO sqtResDao = DAOFactory.instance().getSqtResultDAO();
    protected SQTPeptideSearchDAO sqtSearchDao = DAOFactory.instance().getSqtSearchDAO();
    protected SQTSearchScanDAO sqtSpectrumDao = DAOFactory.instance().getSqtSpectrumDAO();
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    protected SQTSearchDbImpl makeSQTPeptideSearch(int runId, boolean addSeqDb,
            boolean addStaticMods, boolean addDynaMods,
            boolean addHeaders) {
        MsSearchDbImpl msSearch = super.makePeptideSearch(runId, addSeqDb, addStaticMods, addDynaMods);
        SQTSearchDbImpl sqtSearch = new SQTSearchDbImpl(msSearch);
        sqtSearch.setOriginalFileType("SQT");
        sqtSearch.setSearchEngineName("Sequest");
        sqtSearch.setSearchEngineVersion("1.0");
        
        if (addHeaders) {
            sqtSearch.addHeader(makeHeader("header_1", "value_1"));
            sqtSearch.addHeader(makeHeader("header_2", "value_2"));
        }
        
        return sqtSearch;
    }

    
    protected SQTHeaderDbImpl makeHeader(String name, String value) {
        SQTHeaderDbImpl h = new SQTHeaderDbImpl();
        h.setName(name);
        h.setValue(value);
        return h;
    }
    
}
