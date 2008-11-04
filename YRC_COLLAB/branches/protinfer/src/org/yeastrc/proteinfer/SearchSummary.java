/**
 * ExperimentSummary.java
 * @author Vagisha Sharma
 * Nov 3, 2008
 * @version 1.0
 */
package org.yeastrc.proteinfer;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class SearchSummary {
    
    private int msSearchId;
    private List<RunSearch> runSearchList;
    
    public SearchSummary() {
        runSearchList = new ArrayList<RunSearch>();
    }
    
    public SearchSummary(int msSearchId) {
        this.msSearchId = msSearchId;
        this.runSearchList = new ArrayList<RunSearch>();
    }
    
    public List<RunSearch> getRunSearchList() {
        return runSearchList;
    }
    
    public void setRunSearchList(List<RunSearch> runSearchList) {
        this.runSearchList = runSearchList;
    }
    
    public void addRunSearch(RunSearch runSearch) {
        this.runSearchList.add(runSearch);
    }
    
    public void setMsSearchId(int msSearchId) {
        this.msSearchId = msSearchId;
    }
    
    public int getMsSearchId() {
        return msSearchId;
    }
    
    public RunSearch getRunSearch(int index) {
        while(index >= runSearchList.size())
            runSearchList.add(new RunSearch());
        return runSearchList.get(index);
    }
 
    public static final class RunSearch {
        private int runSearchId;
        private String runName;
        private boolean selected = false;
        
        public RunSearch() {}
        
        public RunSearch(int runSearchId, String runName) {
            this.runSearchId = runSearchId;
            this.runName = runName;
        }
        
        public void setRunSearchId(int runSearchId) {
            this.runSearchId = runSearchId;
        }

        public void setRunName(String runName) {
            this.runName = runName;
        }

        public boolean getIsSelected() {
            return selected;
        }

        public void setIsSelected(boolean selected) {
            this.selected = selected;
        }

        public int getRunSearchId() {
            return runSearchId;
        }

        public String getRunName() {
            return runName;
        }
    }
}
