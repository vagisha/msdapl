package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import junit.framework.TestCase;
import edu.uwpr.protinfer.SequestHit;
import edu.uwpr.protinfer.SequestSpectrumMatch;
import edu.uwpr.protinfer.infer.InferredProtein;
import edu.uwpr.protinfer.infer.Peptide;
import edu.uwpr.protinfer.infer.PeptideHit;
import edu.uwpr.protinfer.infer.Protein;
import edu.uwpr.protinfer.infer.ProteinHit;
import edu.uwpr.protinfer.infer.SearchSource;

public class IDPickerExecutorTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testFilterSearchHits() {
//        fail("Not yet implemented"); // TODO
    }

    public final void testInferProteins() {
        
        IDPickerParams params = new IDPickerParams();
        params.setDecoyRatio(1.0f);
        params.setDoParsimonyAnalysis(true);
        params.setMaxAbsoluteFdr(0.05f);
        params.setMaxRelativeFdr(0.05f);
        
        List<SequestHit> searchHits = makeSequestHits();
        SearchSummary summary = new SearchSummary();
        
        IDPickerExecutor executor = new IDPickerExecutor();
        List<InferredProtein<SequestSpectrumMatch>> proteins = executor.inferProteins(searchHits, summary, params);
        
        assertEquals(6, proteins.size());
        int parsimonious = 0;
        for(InferredProtein<SequestSpectrumMatch> prot: proteins) {
            if(prot.getIsAccepted())    parsimonious++;
        }
        assertEquals(5, parsimonious);
        
        Collections.sort(proteins, new Comparator<InferredProtein<SequestSpectrumMatch>>() {
            public int compare(InferredProtein<SequestSpectrumMatch> o1,
                    InferredProtein<SequestSpectrumMatch> o2) {
                return Integer.valueOf(o1.getProtein().getId()).compareTo(o2.getProtein().getId());
            }});
        
        int minCluster = Integer.MAX_VALUE;
        int maxCluster = 0;
        for (InferredProtein<SequestSpectrumMatch> prot: proteins) {
            minCluster = Math.min(minCluster, prot.getProteinClusterId());
            maxCluster = Math.max(maxCluster, prot.getProteinClusterId());
            System.out.println(prot.getAccession()+"; cluster: "+prot.getProteinClusterId()+"; group: "+prot.getProteinGroupId());
        }
        
        InferredProtein<SequestSpectrumMatch> prot = proteins.get(0);
        assertEquals(1, prot.getProtein().getId());
        assertEquals("protein_1", prot.getAccession());
        assertEquals(5, prot.getPeptides().size());
        assertTrue(prot.getIsAccepted());
        
        prot = proteins.get(1);
        assertEquals(4, prot.getProtein().getId());
        assertEquals("protein_4", prot.getAccession());
        assertEquals(2, prot.getPeptides().size());
        assertTrue(prot.getIsAccepted());
        
        prot = proteins.get(2);
        assertEquals(5, prot.getProtein().getId());
        assertEquals("protein_5", prot.getAccession());
        assertEquals(2, prot.getPeptides().size());
        assertFalse(prot.getIsAccepted());
        
        prot = proteins.get(3);
        assertEquals(6, prot.getProtein().getId());
        assertEquals("protein_6", prot.getAccession());
        assertEquals(2, prot.getPeptides().size());
        assertTrue(prot.getIsAccepted());
        
        prot = proteins.get(4);
        assertEquals(7, prot.getProtein().getId());
        assertEquals("protein_7", prot.getAccession());
        assertEquals(2, prot.getPeptides().size());
        assertTrue(prot.getIsAccepted());
        
        prot = proteins.get(5);
        assertEquals(9, prot.getProtein().getId());
        assertEquals("protein_9", prot.getAccession());
        assertEquals(2, prot.getPeptides().size());
        assertTrue(prot.getIsAccepted());
        
        assertEquals(1, minCluster);
        assertEquals(3, maxCluster);
    }
    
    private List<SequestHit> makeSequestHits() {
        List<SequestHit> hits = new ArrayList<SequestHit>();
        SearchSource source = new SearchSource("test");
        
        Protein[] proteins = new Protein[10];
        for (int i = 1; i < proteins.length; i++) {
            proteins[i] = new Protein("protein_"+i, i);
        }
        
        int proteinId = 1;
        int scanId = 1;
        // peptide_1: matches protein 7
        addSearchHits(proteinId++, hits, source, scanId, new Protein[]{proteins[7]});
        
        // peptide_2: matches protein 4, 6, 9
        addSearchHits(proteinId++, hits, source, scanId+=2, new Protein[]{proteins[4], proteins[6], proteins[9]});
        
        // peptide_3: matches protein 1
        addSearchHits(proteinId++, hits, source, scanId+=2, new Protein[]{proteins[1]});
        
        // peptide_4: matches protein 1,5
        addSearchHits(proteinId++, hits, source, scanId+=2, new Protein[]{proteins[1], proteins[5]});
        
        // peptide_5: matches protein 7
        addSearchHits(proteinId++, hits, source, scanId+=2, new Protein[]{proteins[7]});
        
        // peptide_6: matches protein 3,6
        addSearchHits(proteinId++, hits, source, scanId+=2, new Protein[]{proteins[3], proteins[6]});
        
        // peptide_7: matches protein 1
        addSearchHits(proteinId++, hits, source, scanId+=2, new Protein[]{proteins[1]});
        
        // peptide_8: matches protein 1,2,5,8
        addSearchHits(proteinId++, hits, source, scanId+=2, new Protein[]{proteins[1], proteins[2], proteins[5], proteins[8]});
        
        // peptide_9: matches protein 1
        addSearchHits(proteinId++, hits, source, scanId+=2, new Protein[]{proteins[1]});
        
        // peptide_10: matches protein 4, 9
        addSearchHits(proteinId++, hits, source, scanId+=2, new Protein[]{proteins[4], proteins[9]});
        
        return hits;
    }
    
    private void addSearchHits(int peptideId, List<SequestHit> hits, SearchSource source, int scanId, Protein[] proteins) {
        Peptide p = new Peptide("peptide_"+peptideId, peptideId);
        PeptideHit peptHit = new PeptideHit(p);
        for(Protein prot: proteins) {
            peptHit.addProteinHit(new ProteinHit(prot, '\u0000', '\u0000'));
        }
        SequestHit h1 = new SequestHit(source, scanId++, 2, peptHit);
        hits.add(h1);
        peptHit = new PeptideHit(p);
        for(Protein prot: proteins) {
            peptHit.addProteinHit(new ProteinHit(prot, '\u0000', '\u0000'));
        }
        SequestHit h2 = new SequestHit(source, scanId, 3, peptHit);
        hits.add(h2);
    }

}
