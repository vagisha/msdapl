package org.yeastrc.ms.dao.search.sqtfile;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Arrays;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.sequest.SQTSearchDAOImplTest.SQTRunSearchTest;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.sequest.SequestSearch;
import org.yeastrc.ms.domain.search.sequest.SequestSearchDb;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResultDb;
import org.yeastrc.ms.domain.search.sqtfile.SQTField;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearch;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearchDb;

public class SQTBaseDAOTestCase extends BaseDAOTestCase {

    protected SQTHeaderDAO sqtHeaderDao = DAOFactory.instance().getSqtHeaderDAO();
    protected MsSearchResultDAO<SequestSearchResult, SequestSearchResultDb> sequestResDao = DAOFactory.instance().getSequestResultDAO();
    protected MsRunSearchDAO<SQTRunSearch, SQTRunSearchDb> sqtSearchDao = DAOFactory.instance().getSqtRunSerachDAO();
    protected MsSearchDAO<SequestSearch, SequestSearchDb> sequestSearchDao = DAOFactory.instance().getSequestSearchDAO();
    protected SQTSearchScanDAO sqtSpectrumDao = DAOFactory.instance().getSqtSpectrumDAO();

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    protected SequestSearch makeSequestSeach(boolean addSeqDb, boolean addStaticMods, boolean addDynaMods, boolean addHeaders) {
//        SQTSearchTest search = new SQTSearchTest();
//        search.setSearchFileFormat(SearchFileFormat.SQT_SEQ);
//        search.setAnalysisProgramName("Sequest");
//        search.setAnalysisProgramVersion("1.0");
//        long startTime = getTime("01/29/2008, 03:34 AM", false);
//        long endTime = getTime("01/29/2008, 06:21 AM", false);
//        search.setSearchDate(new Date(getTime("01/29/2008, 03:34 AM", true)));
//        search.setSearchDuration(searchTimeMinutes(startTime, endTime));
//        search.setPrecursorMassType("AVG");
//        search.setPrecursorMassTolerance(new BigDecimal("3.000"));
//        search.setFragmentMassType("MONO");
//        search.setFragmentMassTolerance(new BigDecimal("0.0"));

        
//        if (addSeqDb) {
//            MsSearchDatabase db1 = makeSequenceDatabase("serverAddress", "path1", 100, 20);
//            MsSearchDatabase db2 = makeSequenceDatabase("serverAddress", "path2", 200, 40);
//            search.setSearchDatabases(Arrays.asList(new MsSearchDatabase[]{db1, db2}));
//        }
//
//        if (addStaticMods) {
//            MsSearchModification mod1 = makeStaticMod('C', "50.0");
//            MsSearchModification mod2 = makeStaticMod('S', "80.0");
//            search.setStaticResidueMods(Arrays.asList(new MsSearchModification[]{mod1, mod2}));
//        }
//
//        if (addDynaMods) {
//            MsSearchModification dmod1 = makeDynamicMod('A', "10.0", '*');
//            MsSearchModification dmod2 = makeDynamicMod('B', "20.0", '#');
//            MsSearchModification dmod3 = makeDynamicMod('C', "30.0", '@');
//            search.setResultDynamicResidueMods(Arrays.asList(new MsSearchModification[]{dmod1, dmod2, dmod3}));
//        }
        return null;
    }
    
    protected SQTRunSearch makeSQTRunSearch(boolean addHeaders) {

        SQTRunSearchTest runSearch = new SQTRunSearchTest();
        runSearch.setFileFormat(SearchFileFormat.SQT_SEQ);
        long startTime = getTime("01/29/2008, 03:34 AM", false);
        long endTime = getTime("01/29/2008, 06:21 AM", false);
        runSearch.setSearchDate(new Date(getTime("01/29/2008, 03:34 AM", true)));
        runSearch.setSearchDuration(searchTimeMinutes(startTime, endTime));

        if (addHeaders) {
            runSearch.addHeader(makeHeader("header_1", "value_1"));
            runSearch.addHeader(makeHeader("header_2", "value_2"));
        }
        return runSearch;
    }


    protected SQTField makeHeader(final String name, final String value) {
        SQTField h = new SQTField() {
            public String getName() {
                return name;
            }
            public String getValue() {
                return value;
            }};
            return h;
    }

}
