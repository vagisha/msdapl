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

import org.yeastrc.ms.domain.search.sqtfile.SQTField;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderDb;

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
        
        // look for headers for a search that does not yet exist
        List<SQTHeaderDb> headers_1 = sqtHeaderDao.loadSQTHeadersForSearch(1);
        assertEquals(0, headers_1.size());
        
        // insert some headers for a couple of search ids
        SQTField h1_1 = makeHeader(1, 1, false);
        sqtHeaderDao.saveSQTHeader(h1_1, 1); // searchId = 1
        SQTField h1_2 = makeHeader(1, 2, false);
        sqtHeaderDao.saveSQTHeader(h1_2, 1); // searchId = 1;
        
        SQTField h2_1 = makeHeader(2, 1, true);
        sqtHeaderDao.saveSQTHeader(h2_1, 2); // searchId = 2;
        SQTField h2_2 = makeHeader(2, 2, false);
        sqtHeaderDao.saveSQTHeader(h2_2, 2);
        SQTField h2_3 = makeHeader(2, 3, false);
        sqtHeaderDao.saveSQTHeader(h2_3, 2);
        
        // check the number of headers saved
        headers_1 = sqtHeaderDao.loadSQTHeadersForSearch(1);
        assertEquals(2, headers_1.size());
        
        List<SQTHeaderDb> headers_2 = sqtHeaderDao.loadSQTHeadersForSearch(2);
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
        sqtHeaderDao.deleteSQTHeadersForSearch(1);
        headers_1 = sqtHeaderDao.loadSQTHeadersForSearch(1);
        assertEquals(0, headers_1.size());
        
        headers_2 = sqtHeaderDao.loadSQTHeadersForSearch(2);
        assertEquals(3, headers_2.size());
        
        sqtHeaderDao.deleteSQTHeadersForSearch(2);
        headers_2 = sqtHeaderDao.loadSQTHeadersForSearch(2);
        assertEquals(0, headers_2.size());
        
    }
    
    private SQTField makeHeader(int searchId, int itemId, boolean nullValue) {
        String name = "header"+searchId+"_"+itemId;
        String value = nullValue? null : "value"+searchId+"_"+itemId;
        return makeHeader(name, value);
    }
    
    private void checkHeader(int searchId, SQTField input, SQTHeaderDb output) {
        assertEquals(searchId, output.getSearchId());
        assertEquals(input.getName(), output.getName());
        assertEquals(input.getValue(), output.getValue());
    }
    
    private static final class SQTSearchHeaderComparator implements Comparator<SQTHeaderDb> {
        public int compare(SQTHeaderDb o1, SQTHeaderDb o2) {
            return new Integer(o1.getId()).compareTo(new Integer(o2.getId()));
        }
    }
}
