package edu.uwpr.protinfer.database.dto;

public class ProteinferFilter {

    private int id;
    private int pinferId;
    private String filterName;
    private String filterValue;
    
    public ProteinferFilter() {}
    
    public ProteinferFilter(String filterName, String filterValue){
        this.filterName = filterName;
        this.filterValue = filterValue;
    }
    
    public ProteinferFilter (String filterName, String filterValue, int pinferId) {
        this(filterName, filterValue);
        this.pinferId = pinferId;
    }
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getFilterName() {
        return filterName;
    }
    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }
    public String getFilterValue() {
        return filterValue;
    }
    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }
    public int getProteinferId() {
        return pinferId;
    }
    public void setProteinferId(int pinferId) {
        this.pinferId = pinferId;
    }
}
