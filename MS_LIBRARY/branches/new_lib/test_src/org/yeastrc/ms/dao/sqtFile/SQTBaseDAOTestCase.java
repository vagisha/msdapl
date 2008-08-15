package org.yeastrc.ms.dao.sqtFile;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Arrays;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.MsSearchDAO;
import org.yeastrc.ms.dao.MsSearchResultDAO;
import org.yeastrc.ms.dao.sqtFile.SQTSearchDAOImplTest.SQTSearchTest;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsSearchModification;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.sequest.SQTSearchResult;
import org.yeastrc.ms.domain.search.sequest.SQTSearchResultDb;
import org.yeastrc.ms.domain.search.sqtfile.SQTField;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearch;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearchDb;

public class SQTBaseDAOTestCase extends BaseDAOTestCase {

    protected SQTHeaderDAO sqtHeaderDao = DAOFactory.instance().getSqtHeaderDAO();
    protected MsSearchResultDAO<SQTSearchResult, SQTSearchResultDb> sqtResDao = DAOFactory.instance().getSqtResultDAO();
    protected MsSearchDAO<SQTRunSearch, SQTRunSearchDb> sqtSearchDao = DAOFactory.instance().getSqtSearchDAO();
    protected SQTSearchScanDAO sqtSpectrumDao = DAOFactory.instance().getSqtSpectrumDAO();

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    protected SQTRunSearch makeSQTSearch(boolean addSeqDb,boolean addStaticMods, boolean addDynaMods,boolean addHeaders) {

        SQTSearchTest search = new SQTSearchTest();
        search.setSearchFileFormat(SearchFileFormat.SQT_SEQ);
        search.setSearchEngineName("Sequest");
        search.setSearchEngineVersion("1.0");
        long startTime = getTime("01/29/2008, 03:34 AM", false);
        long endTime = getTime("01/29/2008, 06:21 AM", false);
        search.setSearchDate(new Date(getTime("01/29/2008, 03:34 AM", true)));
        search.setSearchDuration(searchTimeMinutes(startTime, endTime));
        search.setPrecursorMassType("AVG");
        search.setPrecursorMassTolerance(new BigDecimal("3.000"));
        search.setFragmentMassType("MONO");
        search.setFragmentMassTolerance(new BigDecimal("0.0"));

        if (addSeqDb) {
            MsSearchDatabase db1 = makeSequenceDatabase("serverAddress", "path1", 100, 20);
            MsSearchDatabase db2 = makeSequenceDatabase("serverAddress", "path2", 200, 40);
            search.setSearchDatabases(Arrays.asList(new MsSearchDatabase[]{db1, db2}));
        }

        if (addStaticMods) {
            MsSearchModification mod1 = makeStaticMod('C', "50.0");
            MsSearchModification mod2 = makeStaticMod('S', "80.0");
            search.setStaticModifications(Arrays.asList(new MsSearchModification[]{mod1, mod2}));
        }

        if (addDynaMods) {
            MsSearchModification dmod1 = makeDynamicMod('A', "10.0", '*');
            MsSearchModification dmod2 = makeDynamicMod('B', "20.0", '#');
            MsSearchModification dmod3 = makeDynamicMod('C', "30.0", '@');
            search.setDynamicModifications(Arrays.asList(new MsSearchModification[]{dmod1, dmod2, dmod3}));
        }


        if (addHeaders) {
            search.addHeader(makeHeader("header_1", "value_1"));
            search.addHeader(makeHeader("header_2", "value_2"));
        }

        return search;
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
