/**
 * MsTerminalModification.java
 * @author Vagisha Sharma
 * Aug 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search;


/**
 * 
 */
public interface MsTerminalModification extends MsModification {

    public enum Terminal {CTERM, NTERM};
    
    public Terminal getModifiedTerminal();
}
