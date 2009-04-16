/**
 * TableHeader.java
 * @author Vagisha Sharma
 * Apr 7, 2009
 * @version 1.0
 */
package org.yeastrc.www.misc;

import org.yeastrc.ms.domain.search.SORT_ORDER;

/**
 * 
 */
public class TableHeader {

    private String headerName;
    private String headerId;
    private boolean sortable = true;
    private boolean isSorted;
    private SORT_ORDER sortOrder = SORT_ORDER.ASC;
    private int width;
    private int colspan = 0;
    
    public int getColspan() {
        return colspan;
    }

    public void setColspan(int colspan) {
        this.colspan = colspan;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public TableHeader() {}
    
    public TableHeader(String headerName) {
        this(headerName, null);
    }
    
    public TableHeader(String headerName, String headerId) {
        this.headerName = headerName;
        this.headerId = headerId;
    }
    
    public String getHeaderName() {
        return headerName;
    }
    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }
    public String getHeaderId() {
        return headerId;
    }
    public void setHeaderId(String headerId) {
        this.headerId = headerId;
    }
    
    public boolean isSorted() {
        return isSorted;
    }
    
    public void setSorted(boolean sorted) {
        this.isSorted = sorted;
    }
    
    public SORT_ORDER getSortOrder() {
        return this.sortOrder;
    }
    
    public void setSortOrder(SORT_ORDER sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isSortable() {
        return sortable;
    }

    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }
    
}
