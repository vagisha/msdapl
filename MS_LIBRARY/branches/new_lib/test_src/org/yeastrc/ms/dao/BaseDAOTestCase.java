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
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

import org.yeastrc.ms.dao.MsRunDAOImplTest.MsRunTest;
import org.yeastrc.ms.dao.MsScanDAOImplTest.MsScanTest;
import org.yeastrc.ms.dao.MsSearchDAOImplTest.MsSearchTest;
import org.yeastrc.ms.dao.MsSearchResultDAOImplTest.MsSearchResultPeptideTest;
import org.yeastrc.ms.dao.MsSearchResultDAOImplTest.MsSearchResultTest;
import org.yeastrc.ms.dao.util.DynamicModLookupUtil;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.general.MsEnzymeDb;
import org.yeastrc.ms.domain.general.MsEnzyme.Sense;
import org.yeastrc.ms.domain.run.DataConversionType;
import org.yeastrc.ms.domain.run.MsRun;
import org.yeastrc.ms.domain.run.MsRunDb;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.MsScanDb;
import org.yeastrc.ms.domain.run.RunFileFormat;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsRunSearchDb;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsSearchModification;
import org.yeastrc.ms.domain.search.MsSearchModificationDb;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultDb;
import org.yeastrc.ms.domain.search.MsSearchResultModification;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultPeptideDb;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.ValidationStatus;
import org.yeastrc.ms.util.PeakConverterDouble;

/**
 * 
 */
public class BaseDAOTestCase extends TestCase {

    protected MsScanDAO<MsScan, MsScanDb> scanDao = DAOFactory.instance().getMsScanDAO();
    protected MsRunDAO<MsRun, MsRunDb> runDao = DAOFactory.instance().getMsRunDAO();
    protected MsExperimentDAO expDao = DAOFactory.instance().getMsExperimentDAO();

    protected MsSearchDAO<MsRunSearch, MsRunSearchDb> searchDao = DAOFactory.instance().getMsSearchDAO();
    protected MsSearchResultDAO<MsSearchResult, MsSearchResultDb> resultDao = 
        DAOFactory.instance().getMsSearchResultDAO();
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

    //-----------------------------------------------------------------------------------------------------
    // SEARCH DATABASE
    //-----------------------------------------------------------------------------------------------------
    protected MsSearchDatabase makeSequenceDatabase(final String serverAddress, final String serverPath,
            final Integer seqLength, final Integer proteinCount) {
        MsSearchDatabase db = new MsSearchDatabase(){
            public int getProteinCount() {
                return proteinCount == null ? 0 : proteinCount;
            }
            public long getSequenceLength() {
                return seqLength == null ? 0 : seqLength;
            }
            public String getServerAddress() {
                return serverAddress;
            }
            public String getServerPath() {
                return serverPath;
            }};
       
        return db;
    }

    //-----------------------------------------------------------------------------------------------------
    // SEARCH RESULT
    //-----------------------------------------------------------------------------------------------------
    protected MsSearchResult makeSearchResult(int searchId, int charge,String peptide, boolean addPrMatch, boolean addDynaMod) {

        //!!------------ RESET the dynamic mod lookup table --------------------------------
        DynamicModLookupUtil.instance().reset();
        //!!------------ RESET the dynamic mod lookup table --------------------------------
        
        
        MsSearchResultTest result = makeSearchResult(charge, peptide);
        MsSearchResultPeptideTest resultPeptide = new MsSearchResultPeptideTest();
        resultPeptide.setPeptideSequence(peptide);
        result.setResultPeptide(resultPeptide);
        
        // add protein matches
        if (addPrMatch)     addProteinMatches(result);

        // add dynamic modifications
        if (addDynaMod)     addResultDynamicModifications(resultPeptide, searchId);

        return result;
    }

    protected MsSearchResultTest makeSearchResult(int charge, String peptide) {
        MsSearchResultTest result = new MsSearchResultTest();
        result.setCharge(charge);
        return result;
    }

    protected void addProteinMatches(MsSearchResultTest result) {

        List<MsSearchResultProtein> matchProteins = new ArrayList<MsSearchResultProtein>(2);
      
        matchProteins.add(makeResultProtein("Accession_"+result.getResultPeptide().getPeptideSequence()+"_1", 
                "Description_"+result.getResultPeptide().getPeptideSequence()+"_1"));

        matchProteins.add(makeResultProtein("Accession_"+result.getResultPeptide().getPeptideSequence()+"_2", null));
        
        result.setProteinMatchList(matchProteins);
    }

    protected void addResultDynamicModifications(MsSearchResultPeptideTest peptide, int searchId) {

        List<MsSearchModificationDb> dynaMods = modDao.loadDynamicModificationsForSearch(searchId);

        List<MsSearchResultModification> resultDynaMods = new ArrayList<MsSearchResultModification>(dynaMods.size());
        int pos = 1;
        for (MsSearchModificationDb mod: dynaMods) {
            MsSearchResultModification resMod = makeResultDynamicMod(mod.getModifiedResidue(), 
                    mod.getModificationMass().toString(), 
                    mod.getModificationSymbol(), 
                    pos++);
            resultDynaMods.add(resMod);
        }
        peptide.setDynamicModifications(resultDynaMods);
    }

    protected void checkSearchResult(MsSearchResult input, MsSearchResultDb output) {
        if(input.getCalculatedMass() != null)
            assertEquals(input.getCalculatedMass().doubleValue(), output.getCalculatedMass().doubleValue());
        assertEquals(input.getCharge(), output.getCharge());
        assertEquals(input.getNumIonsMatched(), output.getNumIonsMatched());
        assertEquals(input.getNumIonsPredicted(), output.getNumIonsPredicted());
        assertNull(input.getValidationStatus());
        assertEquals(ValidationStatus.UNKNOWN, output.getValidationStatus());
        assertEquals(input.getProteinMatchList().size(), output.getProteinMatchList().size());
        checkResultPeptide(input.getResultPeptide(), output.getResultPeptide());
    }
    
    protected void checkResultPeptide(MsSearchResultPeptide input, MsSearchResultPeptideDb output) {
        assertEquals(input.getPeptideSequence(), output.getPeptideSequence());
        assertEquals(input.getPreResidue(), output.getPreResidue());
        assertEquals(input.getPostResidue(), output.getPostResidue());
        assertEquals(input.getSequenceLength(), output.getSequenceLength());
        assertEquals(input.getDynamicModifications().size(), output.getDynamicModifications().size());
    }
    
    //-----------------------------------------------------------------------------------------------------
    // SEARCH RESULT PROTEIN
    //-----------------------------------------------------------------------------------------------------
    protected MsSearchResultProtein makeResultProtein(int resultId, int matchId, boolean useNullDescription) {
        if (useNullDescription)
            return makeResultProtein(makeAccessionString(resultId, matchId), null);
        else
            return makeResultProtein(makeAccessionString(resultId, matchId), makeDescriptionString(resultId, matchId));
    }
    
    protected MsSearchResultProtein makeResultProtein(final String acc, final String desc) {
        MsSearchResultProtein match = new MsSearchResultProtein() {
            public String getAccession() {
                return acc;
            }

            public String getDescription() {
                return desc;
            }};
        return match;
    }

    private String makeAccessionString(int resultId, int matchId){ return "res_"+resultId+"_accession_"+matchId;}
    private String makeDescriptionString(int resultId, int matchId){ return "res_"+resultId+"_description_"+matchId;}
    
    
    //-----------------------------------------------------------------------------------------------------
    // MODIFICATIONS
    //-----------------------------------------------------------------------------------------------------
    protected MsSearchModification makeStaticMod(final char modChar, final String modMass) {
        MsSearchModification mod = new MsSearchModification() {
            public BigDecimal getModificationMass() {
                return new BigDecimal(modMass);
            }
            public char getModificationSymbol() {
                return MsSearchModification.nullCharacter;
            }
            public ModificationType getModificationType() {
                return ModificationType.STATIC;
            }
            public char getModifiedResidue() {
                return modChar;
            }};
        return mod;
    }

    protected MsSearchModification makeDynamicMod(final char modChar, final String modMass, final char modSymbol) {
        MsSearchModification mod = new MsSearchModification() {
            public BigDecimal getModificationMass() {
                return new BigDecimal(modMass);
            }
            public char getModificationSymbol() {
                return modSymbol;
            }
            public ModificationType getModificationType() {
                return ModificationType.DYNAMIC;
            }
            public char getModifiedResidue() {
                return modChar;
            }};
        return mod;
    }
    
    protected MsSearchResultModification makeResultDynamicMod(final char modChar, final String modMass,
            final char modSymbol, final int modPos) {
        MsSearchResultModification mod = new MsSearchResultModification() {
            public BigDecimal getModificationMass() {
                return new BigDecimal(modMass);
            }
            public char getModificationSymbol() {
                return modSymbol;
            }
            public ModificationType getModificationType() {
                return ModificationType.DYNAMIC;
            }
            public char getModifiedResidue() {
                return modChar;
            }
            public int getModifiedPosition() {
                return modPos;
            }};
        return mod;
    }

    //-----------------------------------------------------------------------------------------------------
    // SEARCH
    //-----------------------------------------------------------------------------------------------------
    protected MsRunSearch makePeptideSearch(SearchFileFormat format, boolean addSeqDb,
            boolean addStaticMods, boolean addDynaMods, boolean addEnzymes) {

        MsSearchTest search = new MsSearchTest();
        search.setSearchFileFormat(format);
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

        if (addEnzymes) {
            MsEnzyme enzyme1 = makeDigestionEnzyme("TestEnzyme", Sense.UNKNOWN, null, null);
            MsEnzyme enzyme2 = makeDigestionEnzyme("Trypsin", null, null, null);
            search.setEnzymeList(Arrays.asList(new MsEnzyme[]{enzyme1, enzyme2}));
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
    
    protected void checkSearch(MsRunSearch input, MsRunSearchDb output) {
        assertEquals(input.getSearchDatabases().size(), output.getSearchDatabases().size());
        assertEquals(input.getStaticModifications().size(), output.getStaticModifications().size());
        assertEquals(input.getDynamicModifications().size(), output.getDynamicModifications().size());
        assertEquals(input.getEnzymeList().size(), output.getEnzymeList().size());
        assertEquals(input.getFragmentMassTolerance().doubleValue(), output.getFragmentMassTolerance().doubleValue());
        assertEquals(input.getFragmentMassType(), output.getFragmentMassType());
        assertEquals(input.getPrecursorMassTolerance().doubleValue(), output.getPrecursorMassTolerance().doubleValue());
        assertEquals(input.getPrecursorMassType(), output.getPrecursorMassType());
        assertEquals(input.getSearchDate().toString(), output.getSearchDate().toString());
        assertEquals(input.getSearchDuration(), output.getSearchDuration());
        assertEquals(input.getAnalysisProgramName(), output.getAnalysisProgramName());
        assertEquals(input.getAnalysisProgramVersion(), output.getAnalysisProgramVersion());
        assertEquals(input.getSearchFileFormat(), output.getSearchFileFormat());
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
    protected MsScan makeMsScan(int scanNum, int precursorScanNum, DataConversionType convType) {
        MsScanTest scan = new MsScanTest();
        scan.setStartScanNum(scanNum);
        scan.setEndScanNum(scanNum);
        scan.setFragmentationType("ETD");
        scan.setMsLevel(2);
        scan.setPrecursorMz(new BigDecimal("123.45"));
        scan.setPrecursorScanNum(precursorScanNum);
        scan.setRetentionTime(new BigDecimal("98.7"));
        scan.setDataConversionType(convType);
        return scan;
    }

    protected MsScan makeMsScanWithPeakData(int scanNum, int precursorScanNum, DataConversionType convType) {
        MsScanTest scan = (MsScanTest) makeMsScan(scanNum, precursorScanNum, convType);
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
            MsScan scan = makeMsScanWithPeakData(scanNum, 26, DataConversionType.CENTROID);
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
        assertEquals(input.getDataConversionType(), output.getDataConversionType());
        assertEquals(input.getPeakCount(), output.getPeakCount());
        Iterator<String[]> ipiter = input.peakIterator();
        List<double[]> peakList = new PeakConverterDouble().convert(output.peakDataString());
        Iterator<double[]> opiter = peakList.iterator();
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
