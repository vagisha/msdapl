/**
 * CommonNameLookup.java
 * @author Vagisha Sharma
 * Apr 14, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

/**
 * 
 */
public class CommonNameLookupUtil {

    private static CommonNameLookupUtil instance;
    
    private CommonNameLookupUtil() {}
    
    public static CommonNameLookupUtil instance() {
        if(instance == null)
            instance = new CommonNameLookupUtil();
        return instance;
    }

    public CommonListing getCommonListing(int nrseqProteinId) {
        
        
    }
    
}
