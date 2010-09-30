package org.yeastrc.ms.service.database.fasta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.yeastrc.ms.domain.general.EnzymeRule;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.nrseq.NrDbProtein;

public class PeptideProteinMatchingServiceTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetPeptideProteinMatch2() throws SQLException {
    	
		String proteinSequence = "MQIFVKTLTGKTITLEVESSDTIDNVKSKIQDKEGIPPDQQRLIFAGKQLEDGRTLSDYNIQKESTLHLVLRLRGGIIEPSLKALASKYNCDKSVCRKCYARLPPRATNCRKRKCGHTNQLRPKKKLK";
        int minEnzymaticTermini = 1;
        String peptide = "IQDKEGIPPDQQR";
        
        MsEnzyme enzyme = new MsEnzyme() {
            @Override
            public int getId() {
                return 0;
            }
            @Override
            public String getCut() {
                return "KR";
            }
            @Override
            public String getDescription() {
                return null;
            }
            @Override
            public String getName() {
                return "Trypsin_K";
            }
            @Override
            public String getNocut() {
                return "P";
            }

            @Override
            public Sense getSense() {
                return Sense.CTERM;
            }};
            
        EnzymeRule rule = new EnzymeRule(enzyme);
        List<EnzymeRule> rules = new ArrayList<EnzymeRule>(1);
        rules.add(rule);
        
        NrDbProtein dbProt = new NrDbProtein();
        dbProt.setAccessionString("YKR094C");
        dbProt.setDatabaseId(194);
        dbProt.setProteinId(531326);
        
        PeptideProteinMatchingService service = new PeptideProteinMatchingService(194);
        service.setEnzymeRules(rules);
        service.setNumEnzymaticTermini(1);
        
        List<PeptideProteinMatch> matches = service.getMatchingProteins(peptide);
        Set<String> accessions = new HashSet<String>();
        for(PeptideProteinMatch match: matches) {
        	accessions.add(match.getProtein().getAccessionString());
        }
        assertTrue(accessions.contains("YKR094C"));
        assertTrue(accessions.contains("YLL039C"));
        assertTrue(accessions.contains("YIL148W"));
        assertTrue(accessions.contains("YLR167W"));
        
        PeptideProteinMatch match = service.getPeptideProteinMatch(dbProt, peptide, rules, minEnzymaticTermini, proteinSequence);
        
        assertNotNull(match);
        assertEquals(2, match.getNumEnzymaticTermini());
	}
    
	public void testGetPeptideProteinMatch1() throws SQLException {
		String proteinSequence = "MLPSWKAFKAHNILRILTRFQSTKIPDAVIGIDLGTTNSAVAIMEGKVPRIIENAEGSRTTPSVVAFTKDGERLVGEPAKRQSVINSENTLFATKRLIGRRFEDAEVQRDINQVPFKIVKHSNGDAWVEARNRTYSPAQIGGFILNKMKETAEAYLAKSVKNAVVTVPAYFNDAQRQATKDAGQIIGLNVLRVVNEPTAAALAYGLDKSEPKVIAVFDLGGGTFDISILDIDNGIFEVKSTNGDTHLGGEDFDIYLLQEIISHFKKETGIDLSNDRMAVQRIREAAEKAKIELSSTLSTEINLPFITADAAGPKHIRMPFSRVQLENITAPLIDRTVDPVKKALKDARITASDISDVLLVGGMSRMPKVADTVKKLFGKDASKAVNPDEAVALGAAIQAAVLSGEVTDVLLLDVTPLSLGIETLGGVFTKLIPRNSTIPNKKSQIFSTAASGQTSVEVKVFQGERELVKDNKLIGNFTLAGIPPAPKGTPQIEVTFDIDANGIINVSAKDLASHKDSSITVAGASGLSDTEIDRMVNEAERYKNQDRARRNAIETANKADQLANDTENSIKEFEGKLDKTDSQRLKDQISSLRELVSRSQAGDEVNDDDVGTKIDNLRTSSMKLFEQLYKNSDNPETKNGRENK";
//        String proteinSequence = "WFPCWGIYHQCLNEVSTAQQILKDVQEPVDLDNFRRIDNGTLKDTIRKLVLMARANRIANKHENEVRQVEEETIAGNSLLENANMVPLQIGTEEEIKKTPLDFGWNILPDFAFAELIAMLSGKNDRLVKMVNECTIRFSGEIGSVEMAYTLMRTLRFPVKEPFKERLIAAEFCDGFDIHIVKGTIRDLMLNSPHRDGLGLIYGTMSMVALSRTYTTRRELWTESSRSKLWLVKYLDQGETNNLAYTFVEVKQLLTLNDYDPAMQLMVWHEINLPIKKAERHERILVHFTDSNPVWGLLGSKPSLPIAPYQQIDLHRRFCEADNQLLTNVLGFLQMVLSDQRIDEHGKLVYKYDKGDSGKICFKRPRQKSSIVSFVPEFKSIKVIPKGGSARTGPVALELDHASLLKPSVHQLELTQLQPLQKGIKRFVNYYIDWAQNLNSVDKSKKYNMLWEYADNLDRGFSNQFSIERLTEPGRKLMEYLPELAAFMKETNHEGFFQRSADDLGEYWQEHWLVAMRILEHSVLEAQDVLVPSHIRMKEIISLAAKQRSLSESKIAVMLPYVLAQPHAKGLDSLLSLLSRSVIQNPQHIRSILQPLVELWTGIQILNFGEHMAQTAEPIGGFTFWLTLLRLADQLSSSESLSISHFFGKIAPIVHRHILNSSYHVEKADFTNVGIMGNDFENIDTVSSADSGEQKKKSVSTLMSIVEFNALAWNHWAKYWTNDFHTALLYSGLISDPNSLRWKPQLCVRWEGQKLFCRALLKTYDEVHRPVRKSQQPVSQAIMNNPDLGLDHAMRSTFNILQKLAEDQLGTAWLYKLQAYVVPPSAKATNPHDPDDTEELLTNLVKKALAMRGSKRCLNAFKIRVQADEKPKIVLSRVRLIRQWVDINKQCGLLRTNWTERMTLRKDSNQPLKKYKIIEELEAIIQARVVVNYARNYSENVLASLETVLLDRANFIHVEAKKFNNRHLCLIADYFEKDPSQSKMVSTYQAIEDWQELGWAAGAALPAMAKKVEPKATGWKESALKSLEEWEGLAYLSRLKGMMVEVSDEGAAEKENYAALADEWRQLKEYWTEKLQLENHQQAHKLIGIASDTQHLQNNISILAEITSNKPEELFEVEKYHLAKAFAHCKQAYKGLTHIPIPLPKDDHEMFEVLNLLMQYIEPPNESSSLAKCLAQILDEQYSTQLEVWCSSFSANFLERALPYYVSVLSSCSRLCASPSEKLLQISLRRIWEQWDEKTKQQSCYWANKLINQNVPLKTVQMEDEYNKREPVENEKDFIINTPLCENNLLKNVLQDYVSHQIRNRLLAKNIVPVFVVFDTGLQLLLLSLTNMTAKTLERDGNNLIRVLAQVIRSSMESLNINKALRGLTIISIKKLSGASYETMRVVIPMILHSYDELNPGFTVLSKLIRIPVIRKNSQDNELIDLFFTLTEPVFRKFEGELAKSISEIVSIITIQLKIIPFFERIVGYIKEVHPRIHQKVISILSGLQQFYFDLQSPPCSRMVLIIGPIIQDLFSVCRLGLNQFIHMIAQIAATHHISLSPDNLIKMLNHIVVTPYYEDNSPSVGQMLLAIDISPANQEVSSKSNSTVEIERHKYPDLAGLIGILRVTGRRIHPNNETKLINILIGLLEPYDLLPGVVYGSSAALQGLTTLAADRKFSNSQDQFTNIILPMLEKLYRTMEKGGVVSLEGLVKLATSAVASSADQCKPLIVDLIPDIYPKAVEDSSNILTCLLTASEEKKKPMNSFKLQTLLELLTKRLSPVVYAPNVSSLRGIIKIAELQIGFIEDNLAMFLLRLNDPQALQPDFNSGLHQLIELRIEAVPDTIAIMLLKSLVESVSHLAHVSTQKCIDDKIFLDCSTLAALKRVSSDEHEIYSITILRVFETLSYQHHILQLMKFCQILIQADTIDDNSEGTKKMFSQNRSKRAKEISFQNNFDYQNSQIFKEGSLSISLLNLIRSNVTSELSPIKENLIMLTEQMHDSMPCNLMLNLLDKNLHKAFAPGLACALKGICYFLDKEFQKRVKFKTRLGERINDLILTMYPSISSGVEFAIDGISVLIFPKDSNNAANMDINKLYRLYHVMIRDLYKKTFIAPDFAALLPLIAYVERRIVDFKYEKYKMTSKYIDDYKDRLYPAKLSLLERFVLLTAHVSDNTNLSLGHTCGQFLRQFWQKGLAPDRDQIITLCKGLAVAADLRIILKADRLPVWINDLISNVYPYLLYPSNDALAKIILLAAHRRYELKSSSSNNDATLTLWDICTRVEFEVFDSTLTGGPVTLRGLTNAALRMVEIDSSPILVRLYNALRSTQNPLEETSLYFSILTDVALIGGIKESSTFGHILEFIKNNLSNSFRQFQEASVERALSTLTTSLENAGSAREQPVDSKLKDFILNLTSFTTDLESDVFSIKGIHGASGTIVRGSDNPGNHNSDTNSTTSMEDDHSHSKHTLKKRTRHKGEARQRLSLLNPPTTYKNIYKNM";
        int minEnzymaticTermini = 1;
        String peptide = "IIENAEGSRTTPS";
//        String peptide = "LLTNVLGFLQMVLSDQRIDEHG";
        MsEnzyme enzyme = new MsEnzyme() {
            @Override
            public int getId() {
                return 0;
            }
            @Override
            public String getCut() {
                return "KR";
            }
            @Override
            public String getDescription() {
                return null;
            }
            @Override
            public String getName() {
                return "Trypsin_K";
            }
            @Override
            public String getNocut() {
                return "P";
            }

            @Override
            public Sense getSense() {
                return Sense.CTERM;
            }};
        EnzymeRule rule = new EnzymeRule(enzyme);
        List<EnzymeRule> rules = new ArrayList<EnzymeRule>(1);
        rules.add(rule);
        
        NrDbProtein dbProt = new NrDbProtein();
        dbProt.setAccessionString("YEL030W");
        dbProt.setDatabaseId(178);
        dbProt.setProteinId(529942);
        
        PeptideProteinMatchingService service = new PeptideProteinMatchingService(178);
        PeptideProteinMatch match = service.getPeptideProteinMatch(dbProt, peptide, rules, minEnzymaticTermini, proteinSequence);
        
        assertNotNull(match);
        assertEquals(1, match.getNumEnzymaticTermini());
	}

}
