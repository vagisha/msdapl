/**
 * ValidationStatus.java
 * @author Vagisha Sharma
 * Jul 15, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain;

public enum ValidationStatus {VALID('V'), NOT_VALID('N'), MAYBE('M'), UNVALIDATED('U'), UNKNOWN('K');

    private char statusChar;
    
    private ValidationStatus(char statusChar) { this.statusChar = statusChar; }
    
    public static ValidationStatus instance(char statusChar) {
        switch(statusChar) {
            case 'V':   return VALID;
            case 'N':   return NOT_VALID;
            case 'M':   return MAYBE;
            case 'U':   return UNVALIDATED;
            default:    return UNKNOWN;
        }
    }
    
    public char getStatusChar() { return statusChar; }
}