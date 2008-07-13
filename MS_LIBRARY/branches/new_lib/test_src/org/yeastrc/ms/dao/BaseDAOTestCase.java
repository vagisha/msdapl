/**
 * BaseDAOTestCase.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

import org.yeastrc.ms.dao.MsRunDAOImplTest.MsRunTest;
import org.yeastrc.ms.dao.MsScanDAOImplTest.MsScanTest;
import org.yeastrc.ms.dao.ibatis.DAOFactory;
import org.yeastrc.ms.domain.MsEnzyme;
import org.yeastrc.ms.domain.MsEnzymeDb;
import org.yeastrc.ms.domain.MsRun;
import org.yeastrc.ms.domain.MsRunDb;
import org.yeastrc.ms.domain.MsScan;
import org.yeastrc.ms.domain.MsScanDb;
import org.yeastrc.ms.domain.MsSearch;
import org.yeastrc.ms.domain.MsSearchDatabase;
import org.yeastrc.ms.domain.MsSearchDb;
import org.yeastrc.ms.domain.MsSearchResult;
import org.yeastrc.ms.domain.MsSearchResultDb;
import org.yeastrc.ms.domain.MsEnzyme.Sense;
import org.yeastrc.ms.domain.MsRun.RunFileFormat;
import org.yeastrc.ms.domain.impl.MsSearchDatabaseDbImpl;

/**
 * 
 */
public class BaseDAOTestCase extends TestCase {

    protected MsScanDAO<MsScan, MsScanDb> scanDao = DAOFactory.instance().getMsScanDAO();
    protected MsRunDAO<MsRun, MsRunDb> runDao = DAOFactory.instance().getMsRunDAO();

    protected MsSearchDAO<MsSearch, MsSearchDb> searchDao = DAOFactory.instance().getMsPeptideSearchDAO();
    protected MsSearchResultDAO<MsSearchResult, MsSearchResultDb> resultDao = 
        DAOFactory.instance().getMsPeptideSearchResultDAO();
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

    protected MsSearchDatabase makeSequenceDatabase(String serverAddress, String serverPath,
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

//  protected MsSearchResult makeSearchResult(int charge,String peptide, boolean addPrMatch, boolean addDynaMod) {

//  MsSearchResultDbImpl result = makeSearchResult(charge, peptide);

//  // add protein matches
//  if (addPrMatch)     addProteinMatches(result);

//  // add dynamic modifications
//  if (addDynaMod)     addResultDynamicModifications(result, searchId);

//  return result;
//  }

//  protected MsSearchResultDbImpl makeSearchResult(int charge, String peptide) {
//  MsSearchResultDbImpl result = new MsSearchResultDbImpl();
//  result.setCharge(charge);
//  MsSearchResultPeptideDbImpl peptideObj = new MsSearchResultPeptideDbImpl();
//  peptideObj.setPeptideSequence(peptide);
//  result.setResultPeptide(peptideObj);
//  return result;
//  }

//  protected void addProteinMatches(MsSearchResultDbImpl result) {
//  MsSearchResultProteinDbImpl match1 = new MsSearchResultProteinDbImpl();
//  match1.setAccession("Accession_"+result.getPeptideSequence()+"_1");
//  match1.setDescription("Description_"+result.getPeptideSequence()+"_1");

//  result.addProteinMatch(match1);

//  MsSearchResultProteinDbImpl match2 = new MsSearchResultProteinDbImpl();
//  match2.setAccession("Accession_"+result.getPeptideSequence()+"_2");

//  result.addProteinMatch(match2);
//  }

//  protected void addResultDynamicModifications(MsSearchResultDbImpl result, int searchId) {

//  List<MsSearchModificationDb> dynaMods = modDao.loadDynamicModificationsForSearch(searchId);

//  List<MsSearchResultDynamicModDbImpl> resultDynaMods = new ArrayList<MsSearchResultDynamicModDbImpl>(dynaMods.size());
//  int pos = 1;
//  for (MsSearchModificationDb mod: dynaMods) {
//  MsSearchResultDynamicModDbImpl resMod = new MsSearchResultDynamicModDbImpl();
//  resMod.setModificationId(mod.getId());
//  resMod.setModificationMass(mod.getModificationMass());
//  resMod.setModifiedPosition(pos++);
//  resMod.setModificationSymbol(mod.getModificationSymbol());
//  resMod.setModifiedResidue(mod.getModifiedResidue());
//  resultDynaMods.add(resMod);
//  }

//  result.setDynamicModifications(resultDynaMods);
//  }

//  protected MsSearchStaticModification makeStaticMod(Integer searchId, char modChar, String modMass) {
//  MsSearchStaticModification mod = new MsSearchStaticModification();
//  if (searchId != null)
//  mod.setSearchId(searchId);
//  mod.setModifiedResidue(modChar);
//  mod.setModificationMass(new BigDecimal(modMass));
//  return mod;
//  }

//  protected MsPeptideSearchDynamicMod makeDynamicMod(Integer searchId, char modChar, String modMass,
//  char modSymbol) {
//  MsPeptideSearchDynamicMod mod = new MsPeptideSearchDynamicMod();
//  if (searchId != null)
//  mod.setSearchId(searchId);
//  mod.setModifiedResidue(modChar);
//  mod.setModificationMass(new BigDecimal(modMass));
//  mod.setModificationSymbol(modSymbol);
//  return mod;
//  }

//  protected MsSearchDbImpl makePeptideSearch(int runId, boolean addSeqDb,
//  boolean addStaticMods, boolean addDynaMods) {

//  MsSearchDbImpl search = new MsSearchDbImpl();
//  search.setRunId(runId);
//  search.setOriginalFileType("SQT");
//  search.setSearchEngineName("Sequest");
//  search.setSearchEngineVersion("1.0");
//  long startTime = getTime("01/29/2008, 03:34 AM", false);
//  long endTime = getTime("01/29/2008, 06:21 AM", false);
//  search.setSearchDate(new Date(getTime("01/29/2008, 03:34 AM", true)));
//  search.setSearchDuration(searchTimeMinutes(startTime, endTime));
//  search.setPrecursorMassType("AVG");
//  search.setPrecursorMassTolerance(new BigDecimal("3.000"));
//  search.setFragmentMassType("MONO");
//  search.setFragmentMassTolerance(new BigDecimal("0.0"));

//  if (addSeqDb) {
//  MsSearchDatabaseDbImpl db1 = makeSequenceDatabase("serverAddress", "path1", 100, 20);
//  MsSearchDatabaseDbImpl db2 = makeSequenceDatabase("serverAddress", "path2", 200, 40);
//  search.addSearchDatabase(db1);
//  search.addSearchDatabase(db2);
//  }

//  if (addStaticMods) {
//  MsSearchStaticModification mod1 = makeStaticMod(null, 'C', "50.0");
//  MsSearchStaticModification mod2 = makeStaticMod(null, 'S', "80.0");
//  List<MsSearchStaticModification> staticMods = new ArrayList<MsSearchStaticModification>(2);
//  staticMods.add(mod1);
//  staticMods.add(mod2);
//  search.setStaticModifications(staticMods);
//  }

//  if (addDynaMods) {
//  MsPeptideSearchDynamicMod dmod1 = makeDynamicMod(null, 'A', "10.0", '*');
//  MsPeptideSearchDynamicMod dmod2 = makeDynamicMod(null, 'B', "20.0", '#');
//  MsPeptideSearchDynamicMod dmod3 = makeDynamicMod(null, 'C', "30.0", '@');
//  List<MsPeptideSearchDynamicMod> dynaMods = new ArrayList<MsPeptideSearchDynamicMod>(2);
//  dynaMods.add(dmod1);
//  dynaMods.add(dmod2);
//  dynaMods.add(dmod3);
//  search.setDynamicModifications(dynaMods);
//  }

//  return search;
//  }

//  protected int  searchTimeMinutes(long startTime, long endTime) {
//  assertTrue(endTime > startTime);
//  return (int)((endTime - startTime)/(1000*60));
//  }

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

    //---------------------------------------------------------------------------------
    // ENZYME
    //---------------------------------------------------------------------------------
    protected MsEnzyme makeDigestionEnzyme(String name, Sense sense,String cut, String nocut) {
        return new MsEnzymeDAOImplTest.MsEnzymeTest(name, sense, cut, nocut);
    }

    protected void checkEnzyme(MsEnzyme inputEnzyme, MsEnzymeDb outputEnzyme) {
        assertEquals(inputEnzyme.getName(), outputEnzyme.getName());
        assertEquals(inputEnzyme.getSense(), outputEnzyme.getSense());
        assertEquals(inputEnzyme.getCut(), outputEnzyme.getCut());
        assertEquals(inputEnzyme.getNocut(), outputEnzyme.getNocut());
        assertEquals(inputEnzyme.getDescription(), outputEnzyme.getDescription());
    }
    
    //---------------------------------------------------------------------------------
    // SCAN
    //---------------------------------------------------------------------------------
    protected MsScan makeMsScan(int scanNum, int precursorScanNum) {
        MsScanTest scan = new MsScanTest();
        scan.setStartScanNum(scanNum);
        scan.setEndScanNum(scanNum);
        scan.setFragmentationType("ETD");
        scan.setMsLevel(2);
        scan.setPrecursorMz(new BigDecimal("123.45"));
        scan.setPrecursorScanNum(precursorScanNum);
        scan.setRetentionTime(new BigDecimal("98.7"));
        return scan;
    }

    protected MsScan makeMsScanWithPeakData(int scanNum, int precursorScanNum) {
        MsScanTest scan = (MsScanTest) makeMsScan(scanNum, precursorScanNum);
        List<String[]> peaks = new ArrayList<String[]>(10);
        Random r = new Random();
        for (int i = 0; i < 10; i++) {
            String[] peak = new String[2];
            peak[0] = Double.toString(r.nextDouble());
            peak[1] = Double.toString(r.nextDouble());
            peaks.add(peak);
        }
        scan.setPeaks(peaks);
        assertEquals(10, scan.getPeakCount());
        return scan;
    }
    
    protected void saveScansForRun(int runId, int scanCount) {
        Random random = new Random();
        for (int i = 0; i < scanCount; i++) {
            int scanNum = random.nextInt(100);
            MsScan scan = makeMsScanWithPeakData(scanNum, 26);
            scanDao.save(scan, runId);
        }
    }
    
    protected void checkScan (MsScan input, MsScanDb output) {
        assertEquals(input.getStartScanNum(), output.getStartScanNum());
        assertEquals(input.getFragmentationType(), output.getFragmentationType());
        assertEquals(input.getMsLevel(), output.getMsLevel());
        assertEquals(input.getPrecursorMz().doubleValue(), output.getPrecursorMz().doubleValue());
        assertEquals(input.getPrecursorScanNum(), output.getPrecursorScanNum());
        assertEquals(input.getRetentionTime().doubleValue(), output.getRetentionTime().doubleValue());
        assertEquals(input.getEndScanNum(), output.getEndScanNum());
        Iterator<String[]> ipiter = input.peakIterator();
        Iterator<double[]> opiter = output.peakIterator();
        while(ipiter.hasNext()) {
            String[] ipeak = ipiter.next();
            double[] opeak = opiter.next();
            assertEquals(Double.parseDouble(ipeak[0]), opeak[0]);
            assertEquals(Double.parseDouble(ipeak[1]), opeak[1]);
        }
    }
    
    //---------------------------------------------------------------------------------
    // RUN
    //---------------------------------------------------------------------------------
    protected MsRun createRunWEnzymeInfo(List<MsEnzyme> enzymes) {
        MsRunTest run = createDefaultRun();
        run.setEnzymeList(enzymes);
        return run;
    }

    protected MsRunTest createDefaultRun() {
        return createRunForFormat(RunFileFormat.MS2);
    }
    
    protected MsRunTest createRunForFormat(RunFileFormat format) {
        MsRunTest run = new MsRunTest();
        run.setRunFileFormat(format);
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

    protected void checkRun(MsRun inputRun, MsRunDb outputRun) {
        assertEquals(inputRun.getAcquisitionMethod(), outputRun.getAcquisitionMethod());
        assertEquals(inputRun.getComment(), outputRun.getComment());
        assertEquals(inputRun.getConversionSW(), outputRun.getConversionSW());
        assertEquals(inputRun.getConversionSWOptions(), outputRun.getConversionSWOptions());
        assertEquals(inputRun.getConversionSWVersion(), outputRun.getConversionSWVersion());
        assertEquals(inputRun.getCreationDate(), outputRun.getCreationDate());
        assertEquals(inputRun.getDataType(), outputRun.getDataType());
        assertEquals(inputRun.getFileName(), outputRun.getFileName());
        assertEquals(inputRun.getInstrumentModel(), outputRun.getInstrumentModel());
        assertEquals(inputRun.getInstrumentSN(), outputRun.getInstrumentSN());
        assertEquals(inputRun.getInstrumentVendor(), outputRun.getInstrumentVendor());
        assertEquals(inputRun.getFileName(), outputRun.getFileName());
        assertEquals(inputRun.getSha1Sum(), outputRun.getSha1Sum());
        assertEquals(inputRun.getRunFileFormat(), outputRun.getRunFileFormat());
        assertEquals(inputRun.getEnzymeList().size(), outputRun.getEnzymeList().size());
    }
}
