package org.yeastrc.ms.dao.search;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.MsSearchResultProteinDb;

public class MsSearchResultProteinDAOImplTest extends BaseDAOTestCase {


    private MsSearchResultProtein match1_1;
    private MsSearchResultProtein match1_2;
    private MsSearchResultProtein match1_3;
    
    private MsSearchResultProtein match2_1;
    private MsSearchResultProtein match2_2;
    private MsSearchResultProtein match2_3;
    
    private static final String acc1 = "accession_string_1";
    private static final int prid1 = 25;
    private static final String acc2 = "accession_string_2";
    private static final int prid2 = 26;
    private static final String acc3 = "accession_string_3";
    private static final int prid3 = 27;
    private static final String acc4 = "accession_string_4";
    private static final int prid4 = 28;
    private static final String acc5 = "accession_string_5";
    private static final int prid5 = 29;
    private static final String acc6 = "accession_string_6";
    private static final int prid6 = 30;
    
    private static final String searchDb1 = "my/test/database";
    private static final String searchDb2 = "my/test/database2";
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    
    public void testSaveLoadAndDelete() {
        match1_1 = makeResultProtein(acc1, null);
        match1_2 = makeResultProtein(acc2, null);
        match1_3 = makeResultProtein(acc3, null);

        match2_1 = makeResultProtein(acc4, null);
        match2_2 = makeResultProtein(acc5, null);
        match2_3 = makeResultProtein(acc6, null);
       
       doTest(false);
    }

    private void doTest(boolean useNullDescription) {
        // save the result proteins
           matchDao.save(match1_1, searchDb1, 1); // resultId = 1
           matchDao.save(match1_2, searchDb1, 1);
           matchDao.save(match1_3, searchDb1, 1);
           matchDao.save(match2_1, searchDb2, 2); // resultId = 2
           matchDao.save(match2_2, searchDb2, 2);
           matchDao.save(match2_3, searchDb2, 2);
           
           // load them back
           List<MsSearchResultProteinDb> result1_matchList = matchDao.loadResultProteins(1);
           assertEquals(3, result1_matchList.size());
           
           List<MsSearchResultProteinDb> result2_matchList = matchDao.loadResultProteins(2);
           assertEquals(3, result2_matchList.size());
           
           // order results by proteinID
           Collections.sort(result1_matchList, new MsProteinMatchComparator());
           Collections.sort(result2_matchList, new MsProteinMatchComparator());
           
           
           //make sure the column values were saved and read back accurately
           compareMatches(searchDb1, 1, match1_1, result1_matchList.get(0));
           compareMatches(searchDb1, 1, match1_2, result1_matchList.get(1));
           compareMatches(searchDb1, 1, match1_3, result1_matchList.get(2));
           
           compareMatches(searchDb2, 2, match2_1, result2_matchList.get(0));
           compareMatches(searchDb2, 2, match2_2, result2_matchList.get(1));
           compareMatches(searchDb2, 2, match2_3, result2_matchList.get(2));
           
           
           // now delete the results
           matchDao.delete(1);
           result1_matchList = matchDao.loadResultProteins(1);
           assertEquals(0, result1_matchList.size());
           result2_matchList = matchDao.loadResultProteins(2);
           assertEquals(3, result2_matchList.size());
           
           matchDao.delete(2);
           result2_matchList = matchDao.loadResultProteins(2);
           assertEquals(0, result2_matchList.size());
    }
    
    private void compareMatches(String databaseName, int resultId, MsSearchResultProtein input, MsSearchResultProteinDb output) {
        assertEquals(resultId, output.getResultId());
        assertEquals(input.getAccession(), NrSeqLookupUtil.getProteinAccession(NrSeqLookupUtil.getDatabaseId(databaseName), output.getProteinId()));
    }
    
    private static final class MsProteinMatchComparator implements Comparator<MsSearchResultProteinDb> {
        public int compare(MsSearchResultProteinDb o1, MsSearchResultProteinDb o2) {
            return new Integer(o1.getProteinId()).compareTo(new Integer(o2.getProteinId()));
        }
    }
   
}
