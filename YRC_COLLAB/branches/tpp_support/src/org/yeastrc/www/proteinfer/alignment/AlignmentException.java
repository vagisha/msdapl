/**
 * AlignmentException.java
 * @author Vagisha Sharma
 * Mar 5, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.alignment;

/**
 * 
 */
public class AlignmentException extends Exception {

    public AlignmentException(String msg) {
        super(msg);
    }
    
    public AlignmentException(String msg, Throwable e) {
        super(msg, e);
    }
    
    
}
