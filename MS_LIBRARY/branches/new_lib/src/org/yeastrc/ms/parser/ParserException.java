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

    private int lineNum;
    private String line;
    
    public ParserException(int lineNum, String line, String message) {
        super(message);
        this.lineNum = lineNum;
        this.line = line;
    }
    
    public ParserException(int lineNum, String line, String message, Exception e) {
        super(message, e);
        this.lineNum = lineNum;
        this.line = line;
    }
    
    public int getLineNum() {
        return this.lineNum;
    }
    
    public String getLine() {
        return line;
    }
    
    public String getMessage() {
        return "!!!LINE# "+lineNum+" "+super.getMessage()+"\n\t"+line;
    }
}
