package org.yeastrc.ms.service.sqtfile;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.domain.general.MsEnzymeDb;
import org.yeastrc.ms.domain.general.MsEnzyme.Sense;
import org.yeastrc.ms.domain.search.MsResidueModificationDb;
import org.yeastrc.ms.domain.search.MsSearchDatabaseDb;
import org.yeastrc.ms.domain.search.MsTerminalModificationDb;
import org.yeastrc.ms.domain.search.SearchProgram;
import org.yeastrc.ms.domain.search.MsTerminalModification.Terminal;
import org.yeastrc.ms.domain.search.sequest.SequestParam;
import org.yeastrc.ms.domain.search.sequest.SequestSearch;
import org.yeastrc.ms.domain.search.sequest.SequestSearchDb;
import org.yeastrc.ms.service.MsDataUploader;
import org.yeastrc.ms.service.UploadException;

public class SequestSQTDataUploadServiceTest extends BaseDAOTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        resetDatabase();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testUploadSequestData() {
        String dir = "test_resources/validSequestData_dir";
        MsDataUploader uploader = new MsDataUploader();
        java.util.Date experimentDate = new java.util.Date();
        
        int searchId = 0;
        try {
            searchId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir, experimentDate);
            assertNotSame(0, searchId);
        }
        catch (UploadException e) {
            e.printStackTrace();
            fail("Data is valid");
        }
        assertEquals(0, uploader.getUploadExceptionList().size());
        
        // make sure all the data got uploaded
        int runId1 = getRunId("1.ms2");
        int runId2 = getRunId("2.ms2");
        
        checkSearch(searchId, experimentDate);
        
        checkRunSearch(searchId, runId1);
        checkRunSearch(searchId, runId2);
        
    }
    
    private void checkSearch(int searchId, java.util.Date date) {
        MsSearchDAO<SequestSearch, SequestSearchDb> searchDao = DAOFactory.instance().getSequestSearchDAO();
        SequestSearchDb search = searchDao.loadSearch(searchId);
        assertNotNull(search);
        assertNull(searchDao.loadSearch(24));
        
        assertEquals("remoteServer", search.getServerAddress());
        assertEquals("remoteDirectory", search.getServerDirectory());
        assertEquals(SearchProgram.SEQUEST, search.getSearchProgram());
        assertEquals("3.0", search.getSearchProgramVersion());
//        System.out.println(date.toString());
//        System.out.println(search.getSearchDate().toString());
//        assertEquals(date.getTime(), search.getSearchDate().getTime()); TODO
        assertNotNull(search.getUploadDate());
        
        // check search databases
        List<MsSearchDatabaseDb> dbs = search.getSearchDatabases();
        assertEquals(1, dbs.size());
        assertEquals("/net/maccoss/vol2/software/pipeline/dbase/mouse-contam.fasta", dbs.get(0).getServerPath());
        assertEquals("remoteServer", dbs.get(0).getServerAddress());
        assertEquals(0, dbs.get(0).getProteinCount());
        assertEquals(0, dbs.get(0).getSequenceLength());
        
        // check search enzymes
        List<MsEnzymeDb> enzymes = search.getEnzymeList();
        assertEquals(1, enzymes.size());
        assertEquals("Elastase/Tryp/Chymo", enzymes.get(0).getName());
        assertEquals(Sense.NTERM, enzymes.get(0).getSense());
        assertEquals("ALIVKRWFY", enzymes.get(0).getCut());
        assertEquals("P", enzymes.get(0).getNocut());
        
        // check the static residue modifications
        List<MsResidueModificationDb> staticResMods = search.getStaticResidueMods();
        assertEquals(1, staticResMods.size());
        assertEquals(160.1390, staticResMods.get(0).getModificationMass().doubleValue());
        assertEquals('C', staticResMods.get(0).getModifiedResidue());
        assertEquals(0, staticResMods.get(0).getModificationSymbol());
        
        // check the static terminal modifications
        List<MsTerminalModificationDb> staticTermMods = search.getStaticTerminalMods();
        assertEquals(2, staticTermMods.size());
        Collections.sort(staticTermMods, new Comparator<MsTerminalModificationDb>(){
            @Override
            public int compare(MsTerminalModificationDb o1,
                    MsTerminalModificationDb o2) {
                return Integer.valueOf(o1.getId()).compareTo(Integer.valueOf(o2.getId()));
            }});
        assertEquals(Terminal.CTERM, staticTermMods.get(0).getModifiedTerminal());
        assertEquals(123.4567, staticTermMods.get(0).getModificationMass().doubleValue());
        assertEquals(0, staticTermMods.get(0).getModificationSymbol());
        assertEquals(searchId, staticTermMods.get(0).getSearchId());
        
        assertEquals(Terminal.NTERM, staticTermMods.get(1).getModifiedTerminal());
        assertEquals(987.6543, staticTermMods.get(1).getModificationMass().doubleValue());
        assertEquals(0, staticTermMods.get(1).getModificationSymbol());
        assertEquals(searchId, staticTermMods.get(1).getSearchId());
        
        // check the dynamic residue modifications
        List<MsResidueModificationDb> dynaResMods = search.getDynamicResidueMods();
        assertEquals(6, dynaResMods.size());
        Collections.sort(dynaResMods, new Comparator<MsResidueModificationDb>(){
            @Override
            public int compare(MsResidueModificationDb o1,
                    MsResidueModificationDb o2) {
                return Integer.valueOf(o1.getId()).compareTo(Integer.valueOf(o2.getId()));
            }});
        String modChars = "STYGVD";
        String s = "";
        for (int i = 0; i < dynaResMods.size(); i++)
            s += dynaResMods.get(i).getModifiedResidue();
        assertEquals(modChars, s);
        for (int i = 0; i < 3; i++) {
            assertEquals('*', dynaResMods.get(i).getModificationSymbol());
            assertEquals(79.9876, dynaResMods.get(i).getModificationMass().doubleValue());
        }
        for (int i = 3; i < 6; i++) {
            assertEquals('#', dynaResMods.get(i).getModificationSymbol());
            assertEquals(-99.9, dynaResMods.get(i).getModificationMass().doubleValue());
        }
        
        // check the dynamic terminal modifications
        assertEquals(0, search.getDynamicTerminalMods().size());
        
        // check sequest params
        List<SequestParam> params = search.getSequestParams();
        String[] paramArr = new String[] {
                "database_name = /net/maccoss/vol2/software/pipeline/dbase/mouse-contam.fasta",
                "peptide_mass_tolerance = 3.000",
                "create_output_files = 1",
                "ion_series = 0 1 1 0.0 1.0 0.0 0.0 0.0 0.0 0.0 1.0 0.0",
                "fragment_ion_tolerance = 0.0",
                "num_output_lines = 5",
                "num_description_lines = 3",
                "show_fragment_ions = 0",
                "print_duplicate_references = 1",
                "enzyme_number = 13",
                "xcorr_mode = 0",
                "print_expect_score = 1",
                "diff_search_options = +79.9876 STY -99.9 GVD 0.0 X",
                "max_num_differential_AA_per_mod = 0",
                "nucleotide_reading_frame = 0",
                "mass_type_parent = 0",
                "remove_precursor_peak = 1",
                "mass_type_fragment = 1",
                "ion_cutoff_percentage = 0.1",
                "match_peak_count = 0",
                "match_peak_allowed_error = 1",
                "match_peak_tolerance = 1.0",
                "max_num_internal_cleavage_sites = 1",
                "partial_sequence = ",
                "protein_mass_filter = 0 0",
                "sequence_header_filter =",
                "add_C_terminus = 123.4567",
                "add_N_terminus = 987.6543",
                "add_G_Glycine = 0.0000",
                "add_A_Alanine = 0.0000",
                "add_S_Serine = 0.0000",
                "add_P_Proline = 0.0000",
                "add_V_Valine = 0.0000",
                "add_T_Threonine = 0.0000",
                "add_C_Cysteine = 160.1390",
                "add_L_Leucine = 0.0000",
                "add_I_Isoleucine = 0.0000",
                "add_X_LorI = 0.0000",
                "add_N_Asparagine = 0.0000",
                "add_O_Ornithine = 0.0000",
                "add_B_avg_NandD = 0.0000",
                "add_D_Aspartic_Acid = 0.0000",
                "add_Q_Glutamine = 0.0000",
                "add_K_Lysine = 0.0000",
                "add_Z_avg_QandE = 0.0000",
                "add_E_Glutamic_Acid = 0.0000",
                "add_M_Methionine = 0.0000",
                "add_H_Histidine = 0.0000",
                "add_F_Phenyalanine = 0.0000",
                "add_R_Arginine = 0.0000",
                "add_Y_Tyrosine = 0.0000",
                "add_W_Tryptophan = 0.0000"
        };
        assertEquals(paramArr.length, params.size());
        for (int i = 0; i < params.size(); i++) {
            SequestParam p = params.get(i);
            checkParam(p, paramArr[i]);
        }
    }
    
    private void checkParam(SequestParam param, String origStr) {
       String[] tokens = origStr.trim().split("=");
       assertEquals(param.getParamName(), tokens[0].trim());
       if (tokens.length == 2)
           assertEquals(param.getParamValue(), tokens[1].trim());
       else
           assertEquals("", param.getParamValue());
    }
    
    private void checkRunSearch(int searchId, int runId) {
        
    }
    
    private int getRunId(String runFileName) {
        List<Integer> runIds = runDao.loadRunIdsForFileName(runFileName);
        assertEquals(1, runIds.size());
        int runId = runIds.get(0);
        assertNotSame(0, runId);
        return runId;
    }
}
