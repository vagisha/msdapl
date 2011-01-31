/**
 * Status.java
 * @author Vagisha Sharma
 * Jan 30, 2011
 */
package org.yeastrc.ms.domain.general;


/**
 * 
 */
public enum Status {
	
	QUEUED('Q'), RUNNING('R'), FAILED('F'), SUCCESS('S');

    private char statusChar;
    
    private Status(char statusChar) { this.statusChar = statusChar; }
    
    public static Status instance(char statusChar) {
        switch(statusChar) {
            case 'Q':   return QUEUED;
            case 'R':   return RUNNING;
            case 'F':   return FAILED;
            case 'S':   return SUCCESS;
            default:    return null;
        }
    }
    
    public static Status instance(String statusStr) {
        if (statusStr == null)
            return null;
        if (statusStr.length() != 1)
            throw new IllegalArgumentException("Cannot convert \""+statusStr+"\" to Status");
        Status status = instance(statusStr.charAt(0));
        if (status == null)
            throw new IllegalArgumentException("Unrecognized status: "+statusStr);
        return status; 
    }
    
    public char getStatusChar() { return statusChar; }

}
