/**
 * ProteinferProteinPager.java
 * @author Vagisha Sharma
 * Jan 7, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class ProteinferResultsPager {

    private static final ProteinferResultsPager instance = new ProteinferResultsPager();
    
    private ProteinferResultsPager() {}
    
    public static ProteinferResultsPager instance() {
        return instance;
    }
    
    /**
     * page numbers start with 1
     * @param proteinIds
     * @param pageNum
     * @param numPerPage
     * @param forward
     * @return
     */
    public List<Integer> page(List<Integer> proteinIds, int pageNum, int numPerPage, boolean forward) {
        
        List<Integer> sublist = new ArrayList<Integer>(numPerPage);
        if(forward) {
            int s = (pageNum - 1) * numPerPage;
            if(s >= proteinIds.size())
                return new ArrayList<Integer>(0);
            int e = Math.min(proteinIds.size(), s+numPerPage);
            for(int i = s; i < e; i++)
                sublist.add(proteinIds.get(i));
        }
        else {
            int s = proteinIds.size() - ((pageNum - 1) * numPerPage) - 1;
            if(s <= 0)
                return new ArrayList<Integer>(0);
            int e = Math.max(0, (s - numPerPage));
            for(int i = s; i > e; i--) 
                sublist.add(proteinIds.get(i));
        }
        return sublist;
    }
    
    /**
     * Page numbers start with 1; Default number of results per page is 50.
     * @param proteinIds
     * @param pageNum
     * @param descending
     * @return
     */
    public List<Integer> page(List<Integer> proteinIds, int pageNum, boolean descending) {
        return page(proteinIds, pageNum, 50, descending);
    }
    
    
    public List<Integer> getPageList(int resultCount, int currentPage, int numPerPage) {
        
        int pageCount = getPageCount(resultCount, numPerPage);
        if(currentPage < 1 || currentPage > pageCount)
            return new ArrayList<Integer>(0);
        
        int displayPageCount = 20;
        List<Integer> displayPageList = new ArrayList<Integer>(20);
        
        if(pageCount <= displayPageCount) {
            for(int i = 1; i <= pageCount; i++)  displayPageList.add(i);
            return displayPageList;
        }
        
        int numBeforeCurrent = Math.min(9, currentPage - 1);
        int numAfterCurrent = Math.min(10, pageCount - currentPage);
        if(numAfterCurrent < 10)
            numBeforeCurrent = Math.min((9+(10 - numAfterCurrent)), currentPage - 1);
        if(numBeforeCurrent < 9)
            numAfterCurrent = Math.min((10+(9 - numBeforeCurrent)), pageCount - currentPage);
        
        int firstPage = currentPage - numBeforeCurrent;
        int lastPage = currentPage + numAfterCurrent;
        for (int i = firstPage; i <= lastPage; i++) displayPageList.add(i);
        return displayPageList;
    }

    public List<Integer> getPageList(int resultCount, int currentPage) {
        return getPageList(resultCount, currentPage, 50);
    }
    
    public int getPageCount(int resultCount, int numPerPage) {
        return (int)Math.ceil((double)resultCount/(double)numPerPage);
    }
    
    public int getPageCount(int resultCount) {
        return getPageCount(resultCount, 50);
    }
}
