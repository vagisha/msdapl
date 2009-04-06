/**
 * Tabular.java
 * @author Vagisha Sharma
 * Apr 4, 2009
 * @version 1.0
 */
package org.yeastrc.www.misc;

/**
 * 
 */
public interface Tabular {

    /**
     * Any pre-processing should be done in this method.
     * This method should be called before any or the other methods.
     */
    public abstract void tabulate();
    
    public abstract int columnCount();
    
    public abstract int rowCount();
    
    public String[] columnNames();
    
    public TableRow getRow(int row);
}
