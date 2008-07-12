/**
 * SQTSearchHeaderDAOImplTest.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.sqtFile;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.domain.sqtFile.db.SQTHeaderDbImpl;

/**
 * 
 */
public class SQTSearchHeaderDAOImplTest extends SQTBaseDAOTestCase {

    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOperationsOnSqtSearchHeader() {
        
        // look for headers for a search that does not yet exist
        List<SQTHeaderDbImpl> headers_1 = sqtHeaderDao.loadSQTHeadersForSearch(1);
        assertEquals(0, headers_1.size());
        
        // insert some headers for a couple of search ids
        SQTHeaderDbImpl h1_1 = makeHeader(1, 1, false);
        sqtHeaderDao.saveSQTHeader(h1_1);
        SQTHeaderDbImpl h1_2 = makeHeader(1, 2, false);
        sqtHeaderDao.saveSQTHeader(h1_2);
        
        SQTHeaderDbImpl h2_1 = makeHeader(2, 1, true);
        sqtHeaderDao.saveSQTHeader(h2_1);
        SQTHeaderDbImpl h2_2 = makeHeader(2, 2, false);
        sqtHeaderDao.saveSQTHeader(h2_2);
        SQTHeaderDbImpl h2_3 = makeHeader(2, 3, false);
        sqtHeaderDao.saveSQTHeader(h2_3);
        
        // check the number of headers saved
        headers_1 = sqtHeaderDao.loadSQTHeadersForSearch(1);
        assertEquals(2, headers_1.size());
        
        List<SQTHeaderDbImpl> headers_2 = sqtHeaderDao.loadSQTHeadersForSearch(2);
        assertEquals(3, headers_2.size());
        
        
        // check what's in the headers
        Collections.sort(headers_1, new SQTSearchHeaderComparator());
        checkHeader(headers_1.get(0), h1_1.getSearchId(), h1_1.getName(), h1_1.getValue());
        checkHeader(headers_1.get(1), h1_2.getSearchId(), h1_2.getName(), h1_2.getValue());
        
        Collections.sort(headers_2, new SQTSearchHeaderComparator());
        checkHeader(headers_2.get(0), h2_1.getSearchId(), h2_1.getName(), h2_1.getValue());
        checkHeader(headers_2.get(1), h2_2.getSearchId(), h2_2.getName(), h2_2.getValue());
        checkHeader(headers_2.get(2), h2_3.getSearchId(), h2_3.getName(), h2_3.getValue());
        
        // delete the headers
        sqtHeaderDao.deleteSQTHeadersForSearch(1);
        headers_1 = sqtHeaderDao.loadSQTHeadersForSearch(1);
        assertEquals(0, headers_1.size());
        
        headers_2 = sqtHeaderDao.loadSQTHeadersForSearch(2);
        assertEquals(3, headers_2.size());
        
        sqtHeaderDao.deleteSQTHeadersForSearch(2);
        headers_2 = sqtHeaderDao.loadSQTHeadersForSearch(2);
        assertEquals(0, headers_2.size());
        
    }
    
    private SQTHeaderDbImpl makeHeader(int searchId, int itemId, boolean nullValue) {
        String name = "header"+searchId+"_"+itemId;
        String value = nullValue? null : "value"+searchId+"_"+itemId;
        SQTHeaderDbImpl h = makeHeader(name, value);
        h.setSearchId(searchId);
        return h;
    }
    
    private void checkHeader(SQTHeaderDbImpl header, int searchId, String name, String value) {
        assertEquals(searchId, header.getSearchId());
        assertEquals(name, header.getName());
        assertEquals(value, header.getValue());
    }
    
    private static final class SQTSearchHeaderComparator implements
    Comparator<SQTHeaderDbImpl> {
        public int compare(SQTHeaderDbImpl o1, SQTHeaderDbImpl o2) {
            return new Integer(o1.getId()).compareTo(new Integer(o2.getId()));
        }
    }
}
