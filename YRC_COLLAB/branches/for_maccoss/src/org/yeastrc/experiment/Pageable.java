/**
 * Pageable.java
 * @author Vagisha Sharma
 * Apr 5, 2009
 * @version 1.0
 */
package org.yeastrc.experiment;

import java.util.List;

/**
 * 
 */
public interface Pageable {

    public abstract int getCurrentPage();
    
    public abstract int getFirstPage();
    
    public abstract int getLastPage();
    
    public abstract List<Integer> getDisplayPageNumbers();
}
