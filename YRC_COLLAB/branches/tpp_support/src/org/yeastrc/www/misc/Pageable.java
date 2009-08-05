/**
 * Pageable.java
 * @author Vagisha Sharma
 * Apr 5, 2009
 * @version 1.0
 */
package org.yeastrc.www.misc;

import java.util.List;

/**
 * 
 */
public interface Pageable {

    public abstract int getCurrentPage();
    
    public abstract int getLastPage();
    
    public abstract int getPageCount();
    
    public abstract List<Integer> getDisplayPageNumbers();
}
