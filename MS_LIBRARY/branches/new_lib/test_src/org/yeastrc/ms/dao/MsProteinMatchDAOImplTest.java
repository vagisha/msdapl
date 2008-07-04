package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.dto.MsProteinMatch;

import junit.framework.TestCase;

public class MsProteinMatchDAOImplTest extends TestCase {

    private MsProteinMatchDAO matchDao = DAOFactory.instance().getMsProteinmatchDAO();
    
    private MsProteinMatch match1_1;
    private MsProteinMatch match1_2;
    private MsProteinMatch match1_3;
    
    private MsProteinMatch match2_1;
    private MsProteinMatch match2_2;
    private MsProteinMatch match2_3;
    
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
           matchDao.save(match1_1);
           matchDao.save(match1_2);
           matchDao.save(match1_3);
           matchDao.save(match2_1);
           matchDao.save(match2_2);
           matchDao.save(match2_3);
           
           // load them back
           List<MsProteinMatch> result1_matchList = matchDao.loadResultProteins(1);
           assertEquals(3, result1_matchList.size());
           
           List<MsProteinMatch> result2_matchList = matchDao.loadResultProteins(2);
           assertEquals(3, result2_matchList.size());
           
           // results are ordered by id so this should work
           for (int resultId = 1; resultId < 3; resultId++) {
               List<MsProteinMatch> resultProteins = resultId == 1 ? result1_matchList : result2_matchList;
               for (int matchId = 1; matchId < 4; matchId++) {
                   MsProteinMatch match = resultProteins.get(matchId-1);
                   System.out.println(match.getAccession()+";  "+match.getDescription()+";  "+match.getId());
                   assertEquals(resultId, match.getResultId());
                   assertEquals("res_"+resultId+"_accession_"+matchId, match.getAccession());
                   if (useNullDescription)
                       assertNull(match.getDescription());
                   else
                       assertEquals("res_"+resultId+"_description_"+matchId, match.getDescription());
               }
           }
           
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
    
    private MsProteinMatch getResultProtein(int resultId, int matchId, boolean useNullDescription) {
        MsProteinMatch match = new MsProteinMatch();
        match.setResultId(resultId);
        match.setAccession("res_"+resultId+"_accession_"+matchId);
        if (useNullDescription)
            match.setDescription(null);
        else
            match.setDescription("res_"+resultId+"_description_"+matchId);
        return match;
    }
   
}
