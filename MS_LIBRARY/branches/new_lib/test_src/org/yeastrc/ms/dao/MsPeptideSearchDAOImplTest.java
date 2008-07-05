package org.yeastrc.ms.dao;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.yeastrc.ms.dto.MsPeptideSearch;
import org.yeastrc.ms.dto.MsPeptideSearchResult;
import org.yeastrc.ms.dto.MsSearchDynamicMod;
import org.yeastrc.ms.dto.MsSearchMod;
import org.yeastrc.ms.dto.MsSequenceDatabase;

public class MsPeptideSearchDAOImplTest extends BaseDAOTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOperationsOnMsPeptideSearch() {
        
        assertEquals(0, searchDao.loadSearchesForRun(1).size());
        assertEquals(0, searchDao.loadSearchIdsForRun(1).size());
        
        // create and save a search with no seq. db information or modifications
        MsPeptideSearch search_1 = makePeptideSearch(1, false, false, false);
        int searchId_1 = searchDao.saveSearch(search_1);
        List<MsPeptideSearch> searchList = searchDao.loadSearchesForRun(1);
        assertEquals(1, searchList.size());
        assertEquals(search_1.getSearchDate().toString(), searchList.get(0).getSearchDate().toString());
        assertEquals(167, searchList.get(0).getSearchDuration());
        assertEquals(0, seqDbDao.loadSearchDatabases(searchId_1).size());
        assertEquals(0, modDao.loadStaticModificationsForSearch(searchId_1).size());
        assertEquals(0, modDao.loadDynamicModificationsForSearch(searchId_1).size());
        assertEquals(0, resultDao.loadResultIdsForSearch(searchId_1).size());
        
        // create and save a search with seq. db information and modifications
        MsPeptideSearch search_2 = makePeptideSearch(1, true, true, true);
        int searchId_2 = searchDao.saveSearch(search_2);
        searchList = searchDao.loadSearchesForRun(1);
        assertEquals(2, searchList.size());
        assertEquals(2, seqDbDao.loadSearchDatabases(searchId_2).size());
        assertEquals(2, modDao.loadStaticModificationsForSearch(searchId_2).size());
        assertEquals(3, modDao.loadDynamicModificationsForSearch(searchId_2).size());
        
        // add results for the search
        MsPeptideSearchResult r1 = makeSearchResult(searchId_2, 1, 3, "PEPTIDE1", true, true);
        MsPeptideSearchResult r2 = makeSearchResult(searchId_2, 1, 3, "PEPTIDE1", true, true);
        int r1_id = resultDao.save(r1);
        int r2_id = resultDao.save(r2);
        assertEquals(2, resultDao.loadResultIdsForSearch(searchId_2).size());
        assertEquals(r1.getProteinMatchList().size(), matchDao.loadResultProteins(r1_id).size());
        assertEquals(r1.getDynamicModifications().size(), modDao.loadDynamicModificationsForSearchResult(r1_id).size());
        assertEquals(r2.getProteinMatchList().size(), matchDao.loadResultProteins(r2_id).size());
        assertEquals(r2.getDynamicModifications().size(), modDao.loadDynamicModificationsForSearchResult(r2_id).size());
        
        // delete the searches
        searchDao.deleteSearch(searchId_1);
        searchDao.deleteSearch(searchId_2);
        
        testSearchDeleted(1, searchId_1, new int[0] );
        testSearchDeleted(1, searchId_2, new int[]{r1_id, r2_id});
        
    }

    private void testSearchDeleted(int runId, int searchId, int[] resultIds) {
        assertEquals(0, searchDao.loadSearchIdsForRun(runId).size());
        assertEquals(0, seqDbDao.loadSearchDatabases(searchId).size());
        assertEquals(0, modDao.loadStaticModificationsForSearch(searchId).size());
        assertEquals(0, modDao.loadDynamicModificationsForSearch(searchId).size());
        assertEquals(0, resultDao.loadResultIdsForSearch(searchId).size());
        for (int id: resultIds) {
            assertEquals(0, matchDao.loadResultProteins(id).size());
            assertEquals(0, modDao.loadDynamicModificationsForSearchResult(id).size());
        }
    }
    
    private MsPeptideSearch makePeptideSearch(int runId, boolean addSeqDb, boolean addStaticMods,
            boolean addDynaMods) {
        
       MsPeptideSearch search = new MsPeptideSearch();
       search.setRunId(runId);
       search.setOriginalFileType("SQT");
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
           MsSequenceDatabase db1 = makeSequenceDatabase("serverAddress", "path1", 100, 20);
           MsSequenceDatabase db2 = makeSequenceDatabase("serverAddress", "path2", 200, 40);
           search.addSearchDatabase(db1);
           search.addSearchDatabase(db2);
       }
       
       if (addStaticMods) {
           MsSearchMod mod1 = makeStaticMod(null, 'C', "50.0");
           MsSearchMod mod2 = makeStaticMod(null, 'S', "80.0");
           List<MsSearchMod> staticMods = new ArrayList<MsSearchMod>(2);
           staticMods.add(mod1);
           staticMods.add(mod2);
           search.setStaticModifications(staticMods);
       }
       
       if (addDynaMods) {
           MsSearchDynamicMod dmod1 = makeDynamicMod(null, 'A', "10.0", '*');
           MsSearchDynamicMod dmod2 = makeDynamicMod(null, 'B', "20.0", '#');
           MsSearchDynamicMod dmod3 = makeDynamicMod(null, 'C', "30.0", '@');
           List<MsSearchDynamicMod> dynaMods = new ArrayList<MsSearchDynamicMod>(2);
           dynaMods.add(dmod1);
           dynaMods.add(dmod2);
           dynaMods.add(dmod3);
           search.setDynamicModifications(dynaMods);
       }
       
       return search;
    }
    
    private int  searchTimeMinutes(long startTime, long endTime) {
        assertTrue(endTime > startTime);
        return (int)((endTime - startTime)/(1000*60));
    }

    
    private long getTime(String string, boolean justDate) {
        // example: 01/29/2008, 03:34 AM
        Calendar cal = GregorianCalendar.getInstance();
        string = string.replaceAll("\\s", "");
        String[] tok = string.split(",");
        String date = tok[0];
        String time = tok[1];
        
        String[] dateTok = date.split("\\/");
        cal.set(Calendar.MONTH, Integer.valueOf(dateTok[0]));
        cal.set(Calendar.DATE, Integer.valueOf(dateTok[1]));
        cal.set(Calendar.YEAR, Integer.valueOf(dateTok[2]));
        
        if (justDate) {
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        }
        else {
            String ampm = time.substring(time.length() - 2, time.length());
            String justTime = time.substring(0, time.length() -2);

            String[] justTimeTok = justTime.split(":");
            cal.set(Calendar.AM_PM, (ampm.equalsIgnoreCase("AM") ?  Calendar.AM : Calendar.PM));
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(justTimeTok[0]));
            cal.set(Calendar.MINUTE, Integer.parseInt(justTimeTok[1]));
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        }
        return cal.getTimeInMillis();
    }
    
}
