/**
 * MsTerminalModification.java
 * @author Vagisha Sharma
 * Aug 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search;

import org.yeastrc.ms.domain.search.MsTerminalModification.Terminal;


/**
 * 
 */
public interface MsTerminalModificationIn extends MsModification {

    public abstract Terminal getModifiedTerminal();
    
    public abstract void setModifiedTerminal(Terminal modTerminal);
}
