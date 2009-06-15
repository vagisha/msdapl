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
    private boolean absoluteHyperlink = false;
    private boolean newWindow = false;
    private String targetName = null;
    private String className = null;
    private String id = null;
    private String name = null;
    private int rowSpan = 0;
    private String backgroundColor = null;
    private String textColor = "0 0 0"; // black
    
    public int getRowSpan() {
        return rowSpan;
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public TableCell(){}
    
    public TableCell(String data) {
        this.data = data;
    }
    
    /**
     * If the full url is http://localhost:8080/viewProject.do?ID=20
     * the value of the <code>hyperlink</code> parameter should be
     * viewProject.do?ID=20
     * @param data
     * @param hyperlink
     */
    public TableCell(String data, String hyperlink) {
        this(data, hyperlink, false);
    }
    
    public TableCell(String data, String hyperlink, boolean newWindow) {
        this(data, hyperlink, false, newWindow);
    }
    
    public TableCell(String data, String hyperlink, boolean absoluteLink, boolean newWindow) {
        this.data = data;
        this.hyperlink = hyperlink;
        this.absoluteHyperlink = absoluteLink;
        this.newWindow = newWindow;
    }
    
    public String getData() {
        return data;
    }
    
    public void setData(String data) {
        this.data = data;
    }
    
    public boolean isAbsoluteHyperlink() {
        return this.absoluteHyperlink;
    }
    
    public String getHyperlink() {
        return hyperlink;
    }
    
    /**
     * If the full url is http://localhost:8080/viewProject.do?ID=20
     * the value of the <code>hyperlink</code> parameter should be
     * viewProject.do?ID=20
     * @param hyperlink
     */
    public void setHyperlink(String hyperlink) {
        this.setHyperlink(hyperlink, false, false);
    }
    
    public void setAbsoluteHyperLink(String hyperlink) {
        this.setHyperlink(hyperlink, true, false);
    }
    
    public void setHyperlink(String hyperlink, boolean newWindow) {
        setHyperlink(hyperlink, false, newWindow);
    }
    
    public void setAbsoluteHyperlink(String hyperlink, boolean newWindow) {
        setHyperlink(hyperlink, true, newWindow);
    }
    
    public void setHyperlink(String hyperlink, boolean absolute, boolean newWindow) {
        this.hyperlink = hyperlink;
        this.newWindow = newWindow;
        this.absoluteHyperlink = absolute;
    }
    
    public boolean openLinkInNewWindow() {
        return this.newWindow;
    }

    public void setBackgroundColor(String color) {
        this.backgroundColor = color;
    }
    
    public String getBackgroundColor() {
        return this.backgroundColor;
    }
    
    public void setTextColor(String color) {
        this.textColor = color;
    }
    
    public String getTextColor() {
        return this.textColor;
    }
    
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
