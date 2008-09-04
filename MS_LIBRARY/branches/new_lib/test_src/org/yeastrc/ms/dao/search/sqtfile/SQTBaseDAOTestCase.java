package org.yeastrc.ms.dao.search.sqtfile;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAOImplTest.MsRunSearchTest;
import org.yeastrc.ms.dao.search.sequest.SequestSearchDAOImplTest.SequestSearchTest;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.SearchProgram;
import org.yeastrc.ms.domain.search.MsTerminalModification.Terminal;
import org.yeastrc.ms.domain.search.sequest.SequestParam;
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
    protected MsRunSearchDAO<SQTRunSearch, SQTRunSearchDb> sqtRunSearchDao = DAOFactory.instance().getSqtRunSearchDAO();
    protected MsSearchDAO<SequestSearch, SequestSearchDb> sequestSearchDao = DAOFactory.instance().getSequestSearchDAO();
    protected SQTSearchScanDAO sqtSpectrumDao = DAOFactory.instance().getSqtSpectrumDAO();

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    protected SequestSearch makeSequestSearch(boolean addSeqDb, boolean addStaticMods, boolean addDynaMods) {
        SequestSearchTest search = new SequestSearchTest();
        search.setSearchProgram(SearchProgram.SEQUEST);
        search.setAnalysisProgramVersion("1.0");
        search.setSearchDate(new Date(getTime("01/29/2008, 03:34 AM", true)));
        
        if (addSeqDb) {
            MsSearchDatabase db1 = makeSequenceDatabase("serverAddress", "path1", 100, 20);
            MsSearchDatabase db2 = makeSequenceDatabase("serverAddress", "path2", 200, 40);
            search.setSearchDatabases(Arrays.asList(new MsSearchDatabase[]{db1, db2}));
        }

        if (addStaticMods) {
            MsResidueModification mod1 = makeStaticResidueMod('C', "50.0");
            MsResidueModification mod2 = makeStaticResidueMod('S', "80.0");
            search.setStaticResidueMods(Arrays.asList(new MsResidueModification[]{mod1, mod2}));
            
            MsTerminalModification tmod1 = makeStaticTerminalMod(Terminal.NTERM, "95.0", '\u0000');
            search.setStaticTerminalMods(Arrays.asList(new MsTerminalModification[]{tmod1}));
        }

        if (addDynaMods) {
            MsResidueModification dmod1 = makeDynamicResidueMod('A', "10.0", '*');
            MsResidueModification dmod2 = makeDynamicResidueMod('B', "20.0", '#');
            MsResidueModification dmod3 = makeDynamicResidueMod('C', "30.0", '@');
            search.setDynamicResidueMods(Arrays.asList(new MsResidueModification[]{dmod1, dmod2, dmod3}));
            
            MsTerminalModification tmod1 = makeDynamicTerminalMod(Terminal.CTERM, "79.9876", '^');
            MsTerminalModification tmod2 = makeDynamicTerminalMod(Terminal.NTERM, "0.76543", '$');
            search.setDynamicTerminalMods(Arrays.asList(new MsTerminalModification[]{tmod1, tmod2}));
        }
        
        List<SequestParam> params = new ArrayList<SequestParam>(10);
        for (int i = 0; i < 10; i++) {
            final int idx = i;
            SequestParam param = new SequestParam(){
                public String getParamName() {return "param_"+idx;}
                public String getParamValue() {return "value_"+idx;}
                };
           params.add(param);
        }
        search.setSequestParams(params);
        return search;
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
    
    public static final class SQTRunSearchTest extends MsRunSearchTest implements SQTRunSearch {

        private List<SQTField> headers = new ArrayList<SQTField>();

        public List<SQTField> getHeaders() {
            return headers ;
        }

        public void setHeaders(List<SQTField> headers) {
            this.headers = headers;
        }

        public void addHeader(SQTField header) {
            headers.add(header);
        }
    }

}
