/**
 * MsSearchDynamicMod.java
 * @author Vagisha Sharma
 * Jul 3, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dto;


/**
 * 
 */
public class MsSearchDynamicMod extends MsSearchMod {

    private char modificationSymbol = '\u0000';
    
    /**
     * @return the modificationSymbol
     */
    public char getModificationSymbol() {
        return modificationSymbol;
    }
    
    /**
     * @param modificationSymbol the modificationSymbol to set
     */
    public void setModificationSymbol(char modificationSymbol) {
        this.modificationSymbol = modificationSymbol;
    }
    
    /**
     * JDBC does not understand Character, so we need to return a String
     * @return
     */
    public String getModificationSymbolString() {
        return new Character(modificationSymbol).toString();
    }
    /**
     * JDBC does not understand Character so we will get the value as String
     * @param modificationSymbol
     */
    public void setModificationSymbolString(String modificationSymbol) {
        if (modificationSymbol.length() > 0)
            this.modificationSymbol = modificationSymbol.charAt(0);
    }
}
