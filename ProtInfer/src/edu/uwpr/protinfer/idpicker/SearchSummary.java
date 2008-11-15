/**
 * ExperimentSummary.java
 * @author Vagisha Sharma
 * Nov 3, 2008
 * @version 1.0
 */
package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class SearchSummary {
    
    private int msSearchId;
    private List<RunSearch> runSearchList;
    private int allProteins;
    private int filteredProteinsFdr;
    private int filteredProteinsMinPeptCount;
    private int filteredProteinsParsimony;
    
    
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
    
    public int getTotalDecoyHits() {
       int cnt = 0;
       for(RunSearch rs: runSearchList)
           cnt+= rs.getTotalDecoyHits();
       return cnt;
    }
    
    public int getTotalTargetHits() {
        int cnt = 0;
        for(RunSearch rs: runSearchList)
            cnt+= rs.getTotalTargetHits();
        return cnt;
    }
    
    public int getFilteredTargetHits() {
        int cnt = 0;
        for(RunSearch rs: runSearchList)
            cnt+= rs.getFilteredTargetHits();
        return cnt;
    }
    
    public int getAllProteins() {
        return allProteins;
    }

    public void setAllProteins(int allProteins) {
        this.allProteins = allProteins;
    }

    public int getFilteredProteinsFdr() {
        return filteredProteinsFdr;
    }

    public void setFilteredProteinsFdr(int filteredProteinsFdr) {
        this.filteredProteinsFdr = filteredProteinsFdr;
    }

    public int getFilteredProteinsMinPeptCount() {
        return filteredProteinsMinPeptCount;
    }

    public void setFilteredProteinsMinPeptCount(int filteredProteinsMinPeptCount) {
        this.filteredProteinsMinPeptCount = filteredProteinsMinPeptCount;
    }

    public int getFilteredProteinsParsimony() {
        return filteredProteinsParsimony;
    }

    public void setFilteredProteinsParsimony(int filteredProteinsParsimony) {
        this.filteredProteinsParsimony = filteredProteinsParsimony;
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
        private int totalDecoyHits;
        private int totalTargetHits;
        private int filteredTargetHits;
        
        public int getTotalDecoyHits() {
            return totalDecoyHits;
        }
        
        public int getTotalTargetHits() {
            return totalTargetHits;
        }

        public void setTotalTargetHits(int totalHits) {
            this.totalTargetHits = totalHits;
        }
        
        public void setTotalDecoyHits(int totalHits) {
            this.totalDecoyHits = totalHits;
        }

        public int getFilteredTargetHits() {
            return filteredTargetHits;
        }

        public void setFilteredTargetHits(int filteredHits) {
            this.filteredTargetHits = filteredHits;
        }

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
