/**
 * SQTSearchHeaderDAOImplTest.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.sqtfile;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItemIn;
import org.yeastrc.ms.domain.search.sqtfile.impl.SQTHeaderItemImpl;

/**
 * 
 */
public class SQTHeaderDAOImplTest extends SQTBaseDAOTestCase {

    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOperationsOnSqtHeader() {
        
        // look for headers for a run_search that does not yet exist
        List<SQTHeaderItem> headers_1 = sqtHeaderDao.loadSQTHeadersForRunSearch(1);
        assertEquals(0, headers_1.size());
        
        // insert some headers for a couple of run_search ids
        SQTHeaderItemIn h1_1 = makeHeader(1, 1, false);
        sqtHeaderDao.saveSQTHeader(new SQTHeaderItemImpl(h1_1, 1)); // runSearchId = 1
        SQTHeaderItemIn h1_2 = makeHeader(1, 2, false);
        sqtHeaderDao.saveSQTHeader(new SQTHeaderItemImpl(h1_2, 1)); // runSearchId = 1;
        
        SQTHeaderItemIn h2_1 = makeHeader(2, 1, true);
        sqtHeaderDao.saveSQTHeader(new SQTHeaderItemImpl(h2_1, 2)); // runSearchId = 2;
        SQTHeaderItemIn h2_2 = makeHeader(2, 2, false);
        sqtHeaderDao.saveSQTHeader(new SQTHeaderItemImpl(h2_2, 2));
        SQTHeaderItemIn h2_3 = makeHeader(2, 3, false);
        sqtHeaderDao.saveSQTHeader(new SQTHeaderItemImpl(h2_3, 2));
        
        // check the number of headers saved
        headers_1 = sqtHeaderDao.loadSQTHeadersForRunSearch(1);
        assertEquals(2, headers_1.size());
        
        List<SQTHeaderItem> headers_2 = sqtHeaderDao.loadSQTHeadersForRunSearch(2);
        assertEquals(3, headers_2.size());
        
        
        // check what's in the headers
        Collections.sort(headers_1, new SQTSearchHeaderComparator());
        checkHeader(1, h1_1, headers_1.get(0));
        checkHeader(1, h1_2, headers_1.get(1));
        
        Collections.sort(headers_2, new SQTSearchHeaderComparator());
        checkHeader(2, h2_1, headers_2.get(0));
        checkHeader(2, h2_2, headers_2.get(1));
        checkHeader(2, h2_3, headers_2.get(2));
        
        // delete the headers
        sqtHeaderDao.deleteSQTHeadersForRunSearch(1);
        headers_1 = sqtHeaderDao.loadSQTHeadersForRunSearch(1);
        assertEquals(0, headers_1.size());
        
        headers_2 = sqtHeaderDao.loadSQTHeadersForRunSearch(2);
        assertEquals(3, headers_2.size());
        
        sqtHeaderDao.deleteSQTHeadersForRunSearch(2);
        headers_2 = sqtHeaderDao.loadSQTHeadersForRunSearch(2);
        assertEquals(0, headers_2.size());
        
    }
    
    private SQTHeaderItemIn makeHeader(int runSearchId, int itemId, boolean nullValue) {
        String name = "header"+runSearchId+"_"+itemId;
        String value = nullValue? null : "value"+runSearchId+"_"+itemId;
        return makeHeader(name, value);
    }
    
    private void checkHeader(int runSearchId, SQTHeaderItemIn input, SQTHeaderItem output) {
        assertEquals(runSearchId, output.getRunSearchId());
        assertEquals(input.getName(), output.getName());
        assertEquals(input.getValue(), output.getValue());
    }
    
    private static final class SQTSearchHeaderComparator implements Comparator<SQTHeaderItem> {
        public int compare(SQTHeaderItem o1, SQTHeaderItem o2) {
            return new Integer(o1.getId()).compareTo(new Integer(o2.getId()));
        }
    }
}
