/**
 * TableCell.java
 * @author Vagisha Sharma
 * Apr 5, 2009
 * @version 1.0
 */
package org.yeastrc.www.misc;

public class TableCell {

    private String data;
    private String hyperlink;
    
    public TableCell(){}
    
    public TableCell(String data) {
        this.data = data;
    }
    
    public TableCell(String data, String hyperlink) {
        this.data = data;
        this.hyperlink = hyperlink;
    }
    
    public String getData() {
        return data;
    }
    
    public void setData(String data) {
        this.data = data;
    }
    
    public String getHyperlink() {
        return hyperlink;
    }
    
    public void setHyperlink(String hyperlink) {
        this.hyperlink = hyperlink;
    }
}
