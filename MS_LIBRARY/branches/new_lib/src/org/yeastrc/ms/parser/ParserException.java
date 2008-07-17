/**
 * ParserException.java
 * @author Vagisha Sharma
 * Jul 14, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser;

/**
 * 
 */
public class ParserException extends Exception {

    int lineNum;
    
    public ParserException(int lineNum, String message) {
        super(message);
        this.lineNum = lineNum;
    }
    
    public ParserException(int lineNum, String message, Exception e) {
        super(message, e);
        this.lineNum = lineNum;
    }
    
    public int getLineNum() {
        return this.lineNum;
    }
    
    public String getMessage() {
        return "LINE NUM: "+lineNum+"\n"+super.getMessage();
    }
}
