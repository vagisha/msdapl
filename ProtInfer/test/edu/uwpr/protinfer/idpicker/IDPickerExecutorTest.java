package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
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
        
        List<PeptideSpectrumMatchIDP> searchHits = makeSequestHits();
        
        IDPickerExecutor executor = new IDPickerExecutor();
        List<InferredProtein<SpectrumMatchIDP>> proteins = executor.inferProteins(searchHits, params);
        
        assertEquals(9, proteins.size());
        int parsimonious = 0;
        for(InferredProtein<SpectrumMatchIDP> prot: proteins) {
            if(prot.getIsAccepted())    parsimonious++;
        }
        assertEquals(5, parsimonious);
        
        Collections.sort(proteins, new Comparator<InferredProtein<SpectrumMatchIDP>>() {
            public int compare(InferredProtein<SpectrumMatchIDP> o1,
                    InferredProtein<SpectrumMatchIDP> o2) {
                return Integer.valueOf(o1.getProtein().getId()).compareTo(o2.getProtein().getId());
            }});
        
        int minCluster = Integer.MAX_VALUE;
        int maxCluster = 0;
        for (InferredProtein<SpectrumMatchIDP> prot: proteins) {
            minCluster = Math.min(minCluster, prot.getProteinClusterId());
            maxCluster = Math.max(maxCluster, prot.getProteinClusterId());
            System.out.println(prot.getAccession()+"; cluster: "+prot.getProteinClusterId()+"; group: "+prot.getProteinGroupId());
        }
        
        // create a map for the proteins
        Map<String, InferredProtein<SpectrumMatchIDP>>  map = new HashMap<String, InferredProtein<SpectrumMatchIDP>>();
        for (InferredProtein<SpectrumMatchIDP> prot: proteins) {
           map.put(prot.getAccession(), prot);
        }
        
        // CHECK THE CLUSTERS
        // proteins 1, 2, 5, and 8 should be in the same cluster
        int clusterId1 = map.get("protein_1").getProteinClusterId();
        assertTrue(clusterId1 > 0);
        assertEquals(clusterId1, map.get("protein_2").getProteinClusterId());
        assertEquals(clusterId1, map.get("protein_5").getProteinClusterId());
        assertEquals(clusterId1, map.get("protein_8").getProteinClusterId());

        // proteins 3, 4, 6, and 9 should be in the same cluster
        int clusterId2 = map.get("protein_3").getProteinClusterId();
        assertTrue(clusterId2 > 0);
        assertNotSame(clusterId1, clusterId2);
        assertEquals(clusterId2, map.get("protein_4").getProteinClusterId());
        assertEquals(clusterId2, map.get("protein_6").getProteinClusterId());
        assertEquals(clusterId2, map.get("protein_9").getProteinClusterId());

        // protein 7 should be in a cluster by itself
        int clusterId3 = map.get("protein_7").getProteinClusterId();
        assertTrue(clusterId3 > 0);
        assertNotSame(clusterId1, clusterId3);
        assertNotSame(clusterId2, clusterId3);

        // CHECK THE PROTEIN GROUPS
        // protein_1
        int groupId1 = map.get("protein_1").getProteinGroupId();
        assertTrue(groupId1 > 0);
        
        // protein_2, protein_8
        int groupId2 = map.get("protein_2").getProteinGroupId();
        assertTrue(groupId2 > 0);
        assertNotSame(groupId2, groupId1);
        assertEquals(groupId2, map.get("protein_8").getProteinGroupId());
        
        // protein_5
        int groupId3 = map.get("protein_5").getProteinGroupId();
        assertTrue(groupId3 > 0);
        assertNotSame(groupId3, groupId1);
        assertNotSame(groupId3, groupId2);
        
        // protein_3
        int groupId4 = map.get("protein_3").getProteinGroupId();
        assertTrue(groupId4 > 0);
        assertNotSame(groupId4, groupId1);
        assertNotSame(groupId4, groupId2);
        assertNotSame(groupId4, groupId3);

        // protein_4, protein_9
        int groupId5 = map.get("protein_4").getProteinGroupId();
        assertTrue(groupId5 > 0);
        assertNotSame(groupId5, groupId1);
        assertNotSame(groupId5, groupId2);
        assertNotSame(groupId5, groupId3);
        assertNotSame(groupId5, groupId4);
        assertEquals(groupId5, map.get("protein_9").getProteinGroupId());

        // protein_6
        int groupId6 = map.get("protein_6").getProteinGroupId();
        assertTrue(groupId6 > 0);
        assertNotSame(groupId6, groupId1);
        assertNotSame(groupId6, groupId2);
        assertNotSame(groupId6, groupId3);
        assertNotSame(groupId6, groupId4);
        assertNotSame(groupId6, groupId5);
        
        // protein_7
        int groupId7 = map.get("protein_7").getProteinGroupId();
        assertTrue(groupId7 > 0);
        assertNotSame(groupId7, groupId1);
        assertNotSame(groupId7, groupId2);
        assertNotSame(groupId7, groupId3);
        assertNotSame(groupId7, groupId4);
        assertNotSame(groupId7, groupId5);
        assertNotSame(groupId7, groupId6);

        
        InferredProtein<SpectrumMatchIDP> prot = map.get("protein_1");
        assertEquals(1, prot.getProtein().getId());
        assertEquals("protein_1", prot.getAccession());
        assertEquals(5, prot.getPeptides().size());
        assertTrue(prot.getIsAccepted());
        
        prot = map.get("protein_2");
        assertEquals(2, prot.getProtein().getId());
        assertEquals("protein_2", prot.getAccession());
        assertEquals(1, prot.getPeptides().size());
        assertFalse(prot.getIsAccepted());
        
        prot = map.get("protein_3");
        assertEquals(3, prot.getProtein().getId());
        assertEquals("protein_3", prot.getAccession());
        assertEquals(1, prot.getPeptides().size());
        assertTrue(prot.getIsAccepted());
        
        prot = map.get("protein_4");
        assertEquals(4, prot.getProtein().getId());
        assertEquals("protein_4", prot.getAccession());
        assertEquals(2, prot.getPeptides().size());
        assertTrue(prot.getIsAccepted());
        
        prot = map.get("protein_5");
        assertEquals(5, prot.getProtein().getId());
        assertEquals("protein_5", prot.getAccession());
        assertEquals(2, prot.getPeptides().size());
        assertFalse(prot.getIsAccepted());
        
        prot = map.get("protein_6");
        assertEquals(6, prot.getProtein().getId());
        assertEquals("protein_6", prot.getAccession());
        assertEquals(2, prot.getPeptides().size());
        assertFalse(prot.getIsAccepted());
        
        prot = map.get("protein_7");
        assertEquals(7, prot.getProtein().getId());
        assertEquals("protein_7", prot.getAccession());
        assertEquals(2, prot.getPeptides().size());
        assertTrue(prot.getIsAccepted());
        
        prot = map.get("protein_8");
        assertEquals(8, prot.getProtein().getId());
        assertEquals("protein_8", prot.getAccession());
        assertEquals(1, prot.getPeptides().size());
        assertFalse(prot.getIsAccepted());
        
        prot = map.get("protein_9");
        assertEquals(9, prot.getProtein().getId());
        assertEquals("protein_9", prot.getAccession());
        assertEquals(2, prot.getPeptides().size());
        assertTrue(prot.getIsAccepted());
        
        assertEquals(1, minCluster);
        assertEquals(3, maxCluster);
    }
    
    private List<PeptideSpectrumMatchIDP> makeSequestHits() {
        List<PeptideSpectrumMatchIDP> hits = new ArrayList<PeptideSpectrumMatchIDP>();
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
    
    private void addSearchHits(int peptideId, List<PeptideSpectrumMatchIDP> hits, SearchSource source, int scanId, Protein[] proteins) {
        Peptide p = new Peptide("peptide_"+peptideId, peptideId);
        PeptideHit peptHit = new PeptideHit(p);
        for(Protein prot: proteins) {
            peptHit.addProteinHit(new ProteinHit(prot, '\u0000', '\u0000'));
        }
        PeptideSpectrumMatchIDPImpl h1 = new PeptideSpectrumMatchIDPImpl(); //(source, scanId++, 2, peptHit);
        SpectrumMatchIDPImpl sm = new SpectrumMatchIDPImpl();
        sm.setScanId(scanId++);
        sm.setCharge(2);
        sm.setSourceId(source.getId());
        h1.setPeptide(peptHit);
        h1.setSpectrumMatchMatch(sm);
        
        hits.add(h1);
        peptHit = new PeptideHit(p);
        for(Protein prot: proteins) {
            peptHit.addProteinHit(new ProteinHit(prot, '\u0000', '\u0000'));
        }
//        SequestHit h2 = new SequestHit(source, scanId, 3, peptHit);
        PeptideSpectrumMatchIDPImpl h2 = new PeptideSpectrumMatchIDPImpl(); //(source, scanId++, 2, peptHit);
        sm = new SpectrumMatchIDPImpl();
        sm.setScanId(scanId++);
        sm.setCharge(2);
        sm.setSourceId(source.getId());
        h2.setPeptide(peptHit);
        h2.setSpectrumMatchMatch(sm);
        hits.add(h2);
    }

}
