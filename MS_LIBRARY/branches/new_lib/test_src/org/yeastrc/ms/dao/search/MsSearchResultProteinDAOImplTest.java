package org.yeastrc.ms.dao.search;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.MsSearchResultProteinDb;

public class MsSearchResultProteinDAOImplTest extends BaseDAOTestCase {


    private MsSearchResultProtein match1_1;
    private MsSearchResultProtein match1_2;
    private MsSearchResultProtein match1_3;
    
    private MsSearchResultProtein match2_1;
    private MsSearchResultProtein match2_2;
    private MsSearchResultProtein match2_3;
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    
    public void testSaveLoadAndDelete() {
        match1_1 = makeResultProtein(1, 1, false);
        match1_2 = makeResultProtein(1, 2, false);
        match1_3 = makeResultProtein(1, 3, false);

        match2_1 = makeResultProtein(2, 1, false);
        match2_2 = makeResultProtein(2, 2, false);
        match2_3 = makeResultProtein(2, 3, false);
       
       doTest(false);
    }

    public void testSaveLoadAndDeleteWillNulls() {
        match1_1 = makeResultProtein(1, 1, true);
        match1_2 = makeResultProtein(1, 2, true);
        match1_3 = makeResultProtein(1, 3, true);

        match2_1 = makeResultProtein(2, 1, true);
        match2_2 = makeResultProtein(2, 2, true);
        match2_3 = makeResultProtein(2, 3, true);
       
       doTest(true);
    }

    private void doTest(boolean useNullDescription) {
        // save the result proteins
           matchDao.save(match1_1, 1); // resultId = 1
           matchDao.save(match1_2, 1);
           matchDao.save(match1_3, 1);
           matchDao.save(match2_1, 2); // resultId = 2
           matchDao.save(match2_2, 2);
           matchDao.save(match2_3, 2);
           
           // load them back
           List<MsSearchResultProteinDb> result1_matchList = matchDao.loadResultProteins(1);
           assertEquals(3, result1_matchList.size());
           
           List<MsSearchResultProteinDb> result2_matchList = matchDao.loadResultProteins(2);
           assertEquals(3, result2_matchList.size());
           
           // order results by id
           Collections.sort(result1_matchList, new MsProteinMatchComparator());
           Collections.sort(result2_matchList, new MsProteinMatchComparator());
           
           
           //make sure the column values were saved and read back accurately
           compareMatches(1, match1_1, result1_matchList.get(0));
           compareMatches(1, match1_2, result1_matchList.get(1));
           compareMatches(1, match1_3, result1_matchList.get(2));
           
           compareMatches(2, match2_1, result2_matchList.get(0));
           compareMatches(2, match2_2, result2_matchList.get(1));
           compareMatches(2, match2_3, result2_matchList.get(2));
           
           
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
    
    private void compareMatches(int resultId, MsSearchResultProtein input, MsSearchResultProteinDb output) {
        assertEquals(resultId, output.getResultId());
        assertEquals(input.getAccession(), output.getAccession());
        assertEquals(input.getDescription(), output.getDescription());
    }
    
    private static final class MsProteinMatchComparator implements Comparator<MsSearchResultProteinDb> {
        public int compare(MsSearchResultProteinDb o1, MsSearchResultProteinDb o2) {
            return new Integer(o1.getId()).compareTo(new Integer(o2.getId()));
        }
    }
   
}
