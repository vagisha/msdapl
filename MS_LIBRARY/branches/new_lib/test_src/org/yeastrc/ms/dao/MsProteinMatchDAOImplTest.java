package org.yeastrc.ms.dao;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.domain.MsSearchResultProtein;
import org.yeastrc.ms.domain.db.MsSearchResultProteinDbImpl;

public class MsProteinMatchDAOImplTest extends BaseDAOTestCase {


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
        match1_1 = getResultProtein(1, 1, false);
        match1_2 = getResultProtein(1, 2, false);
        match1_3 = getResultProtein(1, 3, false);

        match2_1 = getResultProtein(2, 1, false);
        match2_2 = getResultProtein(2, 2, false);
        match2_3 = getResultProtein(2, 3, false);
       
       doTest(false);
    }

    public void testSaveLoadAndDeleteWillNulls() {
        match1_1 = getResultProtein(1, 1, true);
        match1_2 = getResultProtein(1, 2, true);
        match1_3 = getResultProtein(1, 3, true);

        match2_1 = getResultProtein(2, 1, true);
        match2_2 = getResultProtein(2, 2, true);
        match2_3 = getResultProtein(2, 3, true);
       
       doTest(true);
    }

    private void doTest(boolean useNullDescription) {
        // save the result proteins
           matchDao.save(match1_1, 1);
           matchDao.save(match1_2, 1);
           matchDao.save(match1_3, 1);
           matchDao.save(match2_1, 2);
           matchDao.save(match2_2, 2);
           matchDao.save(match2_3, 2);
           
           // load them back
           List<MsSearchResultProteinDbImpl> result1_matchList = matchDao.loadResultProteins(1);
           assertEquals(3, result1_matchList.size());
           
           List<MsSearchResultProteinDbImpl> result2_matchList = matchDao.loadResultProteins(2);
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
    
    private void compareMatches(int resultId, MsSearchResultProtein original, MsSearchResultProteinDbImpl fromDb) {
        assertEquals(resultId, fromDb.getResultId());
        assertEquals(original.getAccession(), fromDb.getAccession());
        assertEquals(original.getDescription(), fromDb.getDescription());
    }
    
    private static final class MsProteinMatchComparator implements Comparator<MsSearchResultProteinDbImpl> {
        public int compare(MsSearchResultProteinDbImpl o1, MsSearchResultProteinDbImpl o2) {
            return new Integer(o1.getId()).compareTo(new Integer(o2.getId()));
        }
    }
    
    private MsSearchResultProtein getResultProtein(int resultId, int matchId, boolean useNullDescription) {
        if (useNullDescription)
            return getResultProtein(makeAccessionString(resultId, matchId), null);
        else
            return getResultProtein(makeAccessionString(resultId, matchId), makeDescriptionString(resultId, matchId));
    }
    
    private MsSearchResultProteinDbImpl getResultProtein(String acc, String desc) {
        MsSearchResultProteinDbImpl match = new MsSearchResultProteinDbImpl();
        match.setAccession(acc);
        match.setDescription(desc);
        return match;
    }

    private String makeAccessionString(int resultId, int matchId){ return "res_"+resultId+"_accession_"+matchId;}
    private String makeDescriptionString(int resultId, int matchId){ return "res_"+resultId+"_description_"+matchId;}
   
}
