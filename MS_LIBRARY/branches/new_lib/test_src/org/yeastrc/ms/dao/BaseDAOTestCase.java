/**
 * BaseDAOTestCase.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

import org.yeastrc.ms.dao.ibatis.DAOFactory;
import org.yeastrc.ms.domain.MsEnzyme;
import org.yeastrc.ms.domain.MsRun;
import org.yeastrc.ms.domain.impl.MsDigestionEnzymeDb;
import org.yeastrc.ms.domain.impl.MsRunDbImpl;
import org.yeastrc.ms.domain.impl.MsScanDbImpl;
import org.yeastrc.ms.domain.impl.MsSearchDatabaseDbImpl;
import org.yeastrc.ms.domain.impl.MsSearchDbImpl;
import org.yeastrc.ms.domain.impl.MsSearchResultDbImpl;
import org.yeastrc.ms.domain.impl.MsSearchResultDynamicModDbImpl;
import org.yeastrc.ms.domain.impl.MsSearchResultProteinDbImpl;
import org.yeastrc.ms.domain.impl.MsSearchStaticModification;

/**
 * 
 */
public class BaseDAOTestCase extends TestCase {

    protected MsScanDAO<MsScanDbImpl> scanDao = DAOFactory.instance().getMsScanDAO();
    protected MsRunDAO<MsRun> runDao = DAOFactory.instance().getMsRunDAO();
    
    protected MsSearchDAO searchDao = DAOFactory.instance().getMsPeptideSearchDAO();
    protected MsSearchResultDAO resultDao = DAOFactory.instance().getMsPeptideSearchResultDAO();
    protected MsSearchDatabaseDAO seqDbDao = DAOFactory.instance().getMsSequenceDatabaseDAO();
    protected MsSearchModificationDAO modDao = DAOFactory.instance().getMsSearchModDAO();
    protected MsSearchResultProteinDAO matchDao = DAOFactory.instance().getMsProteinMatchDAO();
    protected MsEnzymeDAO enzymeDao = DAOFactory.instance().getEnzymeDAO();

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }



    protected MsSearchDatabaseDbImpl makeSequenceDatabase(String serverAddress, String serverPath,
            Integer seqLength, Integer proteinCount) {
        MsSearchDatabaseDbImpl db = new MsSearchDatabaseDbImpl();
        if (serverAddress != null)
            db.setServerAddress(serverAddress);
        if (serverPath != null)
            db.setServerPath(serverPath);
        if (seqLength != null)
            db.setSequenceLength(seqLength);
        if (proteinCount != null)
            db.setProteinCount(proteinCount);
        return db;
    }

    protected MsSearchResultDbImpl makeSearchResult(int searchId, int scanId, int charge,
            String peptide, boolean addPrMatch, boolean addDynaMod) {

        MsSearchResultDbImpl result = makeSearchResult(searchId, scanId, charge, peptide);

        // add protein matches
        if (addPrMatch)     addProteinMatches(result);

        // add dynamic modifications
        if (addDynaMod)     addResultDynamicModifications(result, searchId);

        return result;
    }

    protected MsSearchResultDbImpl makeSearchResult(int searchId, int scanId, int charge, String peptide) {
        MsSearchResultDbImpl result = new MsSearchResultDbImpl();
        result.setSearchId(searchId);
        result.setScanId(scanId);
        result.setCharge(charge);
        result.setPeptide(peptide);

        return result;
    }

    protected void addProteinMatches(MsSearchResultDbImpl result) {
        MsSearchResultProteinDbImpl match1 = new MsSearchResultProteinDbImpl();
        match1.setAccession("Accession_"+result.getPeptide()+"_1");
        match1.setDescription("Description_"+result.getPeptide()+"_1");

        result.addProteinMatch(match1);

        MsSearchResultProteinDbImpl match2 = new MsSearchResultProteinDbImpl();
        match2.setAccession("Accession_"+result.getPeptide()+"_2");

        result.addProteinMatch(match2);
    }

    protected void addResultDynamicModifications(MsSearchResultDbImpl result, int searchId) {

        List<MsPeptideSearchDynamicMod> dynaMods = modDao.loadDynamicModificationsForSearch(searchId);

        List<MsSearchResultDynamicModDbImpl> resultDynaMods = new ArrayList<MsSearchResultDynamicModDbImpl>(dynaMods.size());
        int pos = 1;
        for (MsPeptideSearchDynamicMod mod: dynaMods) {
            MsSearchResultDynamicModDbImpl resMod = new MsSearchResultDynamicModDbImpl();
            resMod.setModificationId(mod.getId());
            resMod.setModificationMass(mod.getModificationMass());
            resMod.setModifiedPosition(pos++);
            resMod.setModificationSymbol(mod.getModificationSymbol());
            resMod.setModifiedResidue(mod.getModifiedResidue());
            resultDynaMods.add(resMod);
        }

        result.setDynamicModifications(resultDynaMods);
    }

    protected MsSearchStaticModification makeStaticMod(Integer searchId, char modChar, String modMass) {
        MsSearchStaticModification mod = new MsSearchStaticModification();
        if (searchId != null)
            mod.setSearchId(searchId);
        mod.setModifiedResidue(modChar);
        mod.setModificationMass(new BigDecimal(modMass));
        return mod;
    }

    protected MsPeptideSearchDynamicMod makeDynamicMod(Integer searchId, char modChar, String modMass,
            char modSymbol) {
        MsPeptideSearchDynamicMod mod = new MsPeptideSearchDynamicMod();
        if (searchId != null)
            mod.setSearchId(searchId);
        mod.setModifiedResidue(modChar);
        mod.setModificationMass(new BigDecimal(modMass));
        mod.setModificationSymbol(modSymbol);
        return mod;
    }

    protected MsSearchDbImpl makePeptideSearch(int runId, boolean addSeqDb,
            boolean addStaticMods, boolean addDynaMods) {

        MsSearchDbImpl search = new MsSearchDbImpl();
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
            MsSearchDatabaseDbImpl db1 = makeSequenceDatabase("serverAddress", "path1", 100, 20);
            MsSearchDatabaseDbImpl db2 = makeSequenceDatabase("serverAddress", "path2", 200, 40);
            search.addSearchDatabase(db1);
            search.addSearchDatabase(db2);
        }

        if (addStaticMods) {
            MsSearchStaticModification mod1 = makeStaticMod(null, 'C', "50.0");
            MsSearchStaticModification mod2 = makeStaticMod(null, 'S', "80.0");
            List<MsSearchStaticModification> staticMods = new ArrayList<MsSearchStaticModification>(2);
            staticMods.add(mod1);
            staticMods.add(mod2);
            search.setStaticModifications(staticMods);
        }

        if (addDynaMods) {
            MsPeptideSearchDynamicMod dmod1 = makeDynamicMod(null, 'A', "10.0", '*');
            MsPeptideSearchDynamicMod dmod2 = makeDynamicMod(null, 'B', "20.0", '#');
            MsPeptideSearchDynamicMod dmod3 = makeDynamicMod(null, 'C', "30.0", '@');
            List<MsPeptideSearchDynamicMod> dynaMods = new ArrayList<MsPeptideSearchDynamicMod>(2);
            dynaMods.add(dmod1);
            dynaMods.add(dmod2);
            dynaMods.add(dmod3);
            search.setDynamicModifications(dynaMods);
        }

        return search;
    }
    
    protected int  searchTimeMinutes(long startTime, long endTime) {
        assertTrue(endTime > startTime);
        return (int)((endTime - startTime)/(1000*60));
    }

    /**
     * date/time string should look like: 01/29/2008, 03:34 AM
     * @param string
     * @param justDate
     * @return
     */
    protected long getTime(String string, boolean justDate) {
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

    protected MsDigestionEnzymeDb makeDigestionEnzyme(String name, int sense,
            String cut, String nocut) {
                MsDigestionEnzymeDb enzyme;
                enzyme = new MsDigestionEnzymeDb();
                enzyme.setName(name);
                enzyme.setCut(cut);
                enzyme.setSenseByteVal((short)sense);
                enzyme.setNocut(nocut);
                return enzyme;
            }

    protected MsScanDbImpl makeMsScan(int runId, int scanNum) {
        MsScanDbImpl scan = new MsScanDbImpl();
        scan.setRunId(runId);
        scan.setStartScanNum(scanNum);
        return scan;
    }

    //---------------------------------------------------------------------------------
    // RUN
    //---------------------------------------------------------------------------------
    protected void checkRun(MsRun r1, MsRun r2) {
        assertEquals(r1.getMsExperimentId(), r2.getMsExperimentId());
        assertEquals(r1.getFileFormatString(), r2.getFileFormatString());
        assertEquals(r1.getFileName(), r2.getFileName());
        assertEquals(r1.getSha1Sum(), r2.getSha1Sum());
        assertEquals(r1.getCreationDate(), r2.getCreationDate());
        assertEquals(r1.getConversionSW(), r2.getConversionSW());
        assertEquals(r1.getConversionSWOptions(), r2.getConversionSWOptions());
        assertEquals(r1.getConversionSWVersion(), r2.getConversionSWVersion());
        assertEquals(r1.getInstrumentModel(), r2.getInstrumentModel());
        assertEquals(r1.getInstrumentSN(), r2.getInstrumentSN());
        assertEquals(r1.getInstrumentVendor(), r2.getInstrumentVendor());
        assertEquals(r1.getDataType(), r2.getDataType());
        assertEquals(r1.getAcquisitionMethod(), r2.getAcquisitionMethod());
        assertEquals(r1.getComment(), r2.getComment());
    }

    protected MsRun createRunWEnzymeInfo(int msExperimentId, List<MsDigestionEnzymeDb> enzymes) {
        
        MsRun run = createRun(msExperimentId);
        for (MsEnzyme e: enzymes)
            run.addEnzyme(e);
        return run;
    }

    protected MsRun createRun(int msExperimentId) {
        MsRun run = new MsRun();
        run.setExperimentId(msExperimentId);
        run.setRunFileFormatString(org.yeastrc.ms.domain.MS2.toString());
        run.setFileName("my_file1.ms2");
        run.setAcquisitionMethod("Data dependent");
        run.setComment("Dummy run");
        run.setConversionSW("ms2Convert");
        run.setConversionSWVersion("1.0");
        run.setConversionSWOptions("options string");
        run.setDataType("profile");
        run.setInstrumentModel("ETD");
        run.setInstrumentVendor("Thermo");
        run.setSha1Sum("sha1sum");
        return run;
    }

    protected void saveScansForRun(int runId, int scanCount) {
        Random random = new Random();
        for (int i = 0; i < scanCount; i++) {
            int scanNum = random.nextInt(100);
            MsScanDbImpl scan = new MsScanDbImpl();
            scan.setRunId(runId);
            scan.setStartScanNum(scanNum);
            scanDao.save(scan);
        }
    }
}
