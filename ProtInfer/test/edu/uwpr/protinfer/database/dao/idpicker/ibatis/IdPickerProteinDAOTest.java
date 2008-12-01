package edu.uwpr.protinfer.database.dao.idpicker.ibatis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import junit.framework.TestCase;
import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dto.ProteinUserValidation;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerCluster;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerPeptide;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerProtein;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerProteinGroup;

public class IdPickerProteinDAOTest extends TestCase {

    private static final ProteinferDAOFactory factory = ProteinferDAOFactory.testInstance();
    private static final IdPickerProteinDAO protDao = factory.getIdPickerProteinDao();
//    private static final IdPickerPeptideDAO peptDao = factory.getIdPickerPeptideDao();
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testSaveIdPickerProtein() {
        int id = protDao.saveIdPickerProtein(createIdPickerProtein(456, 123, 10.0, 12, 21, true, 2)); // clusterID 12; groupID 21
        assertEquals(1, id);
        
        id = protDao.saveIdPickerProtein(createIdPickerProtein(456, 124, 20.0, 12, 21, true, 3)); // clusterID 12; groupID 21
        assertEquals(2, id);
        
        id = protDao.saveIdPickerProtein(createIdPickerProtein(456, 125, 30.0, 12, 22, false, 4)); // clusterID 12; groupID 22
        assertEquals(3, id);
        
        id = protDao.saveIdPickerProtein(createIdPickerProtein(456, 125, 30.0, 13, 23, true, 4)); // clusterID 13; groupID 23
        assertEquals(4, id);
    }

    public final void testGetIdPickerClusterProteins() {
        assertEquals(3, protDao.getIdPickerClusterProteins(456, 12).size());
        assertEquals(1, protDao.getIdPickerClusterProteins(456, 13).size());
    }
    
    public final void testGetGroupProteins() {
        assertEquals(2, protDao.getProteinferGroupProteins(456, 21).size());
        assertEquals(1, protDao.getProteinferGroupProteins(456, 22).size());
        assertEquals(1, protDao.getProteinferGroupProteins(456, 23).size());
    }
    
    public final void testGetFilteredParsimoniousProteinCount() {
        assertEquals(3, protDao.getFilteredParsimoniousProteinCount(456));
    }
    
    public final void testGetProtein() {
        IdPickerProtein protein = protDao.getProtein(2);
        assertEquals(2, protein.getId());
        assertEquals(456, protein.getProteinferId());
        assertEquals(124, protein.getNrseqDbProteinId());
        assertEquals(20.0, protein.getCoverage());
        assertEquals(3, protein.getPeptideCount());
        assertEquals(6, protein.getSpectralCount());
        assertNull(protein.getUserAnnotation());
        assertNull(protein.getUserValidation());
        assertEquals(12, protein.getClusterId());
        assertEquals(21, protein.getGroupId());
        assertTrue(protein.getIsParsimonious());
        
        List<IdPickerPeptide> peptList = protein.getPeptides();
        Collections.sort(peptList, new Comparator<IdPickerPeptide>() {
            public int compare(IdPickerPeptide o1, IdPickerPeptide o2) {
                return Integer.valueOf(o1.getId()).compareTo(o2.getId());
            }});
        
        assertEquals(3, peptList.size());
        int i = 1;
        for(IdPickerPeptide pept: peptList) {
            assertEquals(456, pept.getProteinferId());
            assertEquals(i, pept.getSpectralCount());
            i++;
            assertEquals(1, pept.getMatchingProteinIds().size());
        }
    }

    public final void testGetProteins() {
        List<IdPickerProtein> protList = protDao.getProteins(456);
        assertEquals(4, protList.size());
        Collections.sort(protList, new Comparator<IdPickerProtein>() {
            public int compare(IdPickerProtein o1, IdPickerProtein o2) {
                return Integer.valueOf(o1.getId()).compareTo(o2.getId());
            }});
        int i = 0;
        assertEquals(12, protList.get(i).getClusterId());
        assertEquals(21, protList.get(i).getGroupId());
        i++;
        assertEquals(12, protList.get(i).getClusterId());
        assertEquals(21, protList.get(i).getGroupId());
        i++;
        assertEquals(12, protList.get(i).getClusterId());
        assertEquals(22, protList.get(i).getGroupId());
        i++;
        assertEquals(13, protList.get(i).getClusterId());
        assertEquals(23, protList.get(i).getGroupId());
    }

    public final void testSaveProtein2() {
        IdPickerProtein protein = new IdPickerProtein();
        protein.setCoverage(50.0);
        protein.setNrseqDbProteinId(66);
        protein.setProteinferId(789);
        protein.setUserAnnotation("Not Annotated");
        protein.setUserValidation(ProteinUserValidation.REJECTED);
        protein.setClusterId(13);
        protein.setGroupId(23);
        protein.setIsParsimonious(true);
        
        // get the other protein that is in the same group
        IdPickerProtein p = protDao.getProtein(4);
        // set the peptides for our new protein. Since these peptides are already in the db
        // new entries should not be created
        List<IdPickerPeptide> oldPeptList = p.getPeptides();
        assertTrue(oldPeptList.size() > 0);
        Collections.sort(oldPeptList, new Comparator<IdPickerPeptide>() {
            public int compare(IdPickerPeptide o1, IdPickerPeptide o2) {
                return Integer.valueOf(o1.getId()).compareTo(o2.getId());
            }});
        protein.setPeptides(oldPeptList);
        
        
        int id = protDao.saveIdPickerProtein(protein);
        assertEquals(5, id);
        
        IdPickerProtein prot = protDao.getProtein(5);
        List<IdPickerPeptide> plist = prot.getPeptides();
        assertEquals(oldPeptList.size(), plist.size());
        Collections.sort(plist, new Comparator<IdPickerPeptide>() {
            public int compare(IdPickerPeptide o1, IdPickerPeptide o2) {
                return Integer.valueOf(o1.getId()).compareTo(o2.getId());
            }});
        
        for(int i = 0; i < oldPeptList.size(); i++) {
            assertEquals(oldPeptList.get(i).getId(), plist.get(i).getId());
            assertEquals(2, plist.get(i).getMatchingProteinIds().size());
        }
    }

    public final void testGetIdPickerProteinGroup() {
        IdPickerProteinGroup group = protDao.getIdPickerProteinGroup(456, 21);
        assertNotNull(group);
        assertEquals(21, group.getGroupId());
        assertEquals(2, group.getProteinCount());
        assertEquals(5, group.getMatchingPeptideCount());
        assertEquals(1, group.getMatchingPeptideGroups().size());
    }

    public final void testGetIdPickerCluster() {
        IdPickerCluster cluster = protDao.getIdPickerCluster(456, 12);
        assertEquals(2, cluster.getPeptideGroups().size());
        assertEquals(2, cluster.getPeptideGroups().size());
    }

    public static final IdPickerProtein createIdPickerProtein(int pinferId, int nrseqId, double coverage, 
            int clusterId, int groupId, boolean parsim, int numPept) {
        IdPickerProtein protein = new IdPickerProtein();
        protein.setProteinferId(pinferId);
        protein.setNrseqDbProteinId(nrseqId);
        protein.setCoverage(coverage);
        protein.setClusterId(clusterId);
        protein.setGroupId(groupId);
        protein.setIsParsimonious(parsim);
        
        List<IdPickerPeptide> peptList = new ArrayList<IdPickerPeptide>(numPept);
        for(int i = 1; i <= numPept; i++) {
            IdPickerPeptide pept = IdPickerPeptideDAOTest.createIdPickerPeptide(pinferId, groupId, i); // group ID = 98
            peptList.add(pept);
        }
        protein.setPeptides(peptList);
        
        return protein;
    }
}
