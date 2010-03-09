/**
 * ProteinPropertiesFilters.java
 * @author Vagisha Sharma
 * Mar 5, 2010
 * @version 1.0
 */
package org.yeastrc.www.compare;

/**
 * 
 */
public class ProteinPropertiesFilters {

	private String accessionLike;
    private String descriptionLike;
    private String descriptionNotLike;
    
    private double minMolWt = 0.0;
    private double maxMolWt = Double.MAX_VALUE;
    
    private double minPi = 0.0;
    private double maxPi = Double.MAX_VALUE;
    
    public ProteinPropertiesFilters() {}
    
    public String getAccessionLike() {
        return accessionLike;
    }

    public void setAccessionLike(String accessionLike) {
        this.accessionLike = accessionLike;
    }
    
    public boolean hasAccessionFilter() {
    	return (this.accessionLike != null && this.accessionLike.trim().length() > 0);
    }
    
    public String getDescriptionLike() {
        return descriptionLike;
    }
    
    public void setDescriptionLike(String descriptionLike) {
        this.descriptionLike = descriptionLike;
    }
    
    public boolean hasDescriptionLikeFilter() {
    	return (this.descriptionLike != null && this.descriptionLike.trim().length() > 0);
    }
    
    public String getDescriptionNotLike() {
        return descriptionNotLike;
    }
    
    public void setDescriptionNotLike(String descriptionNotLike) {
        this.descriptionNotLike = descriptionNotLike;
    }
    
    public boolean hasDescriptionNotLikeFilter() {
    	return (this.descriptionNotLike != null && this.descriptionNotLike.trim().length() > 0);
    }
    
    public void setMinMolecularWt(double molWt) {
        this.minMolWt = molWt;
    }
    
    public double getMinMolecularWt() {
        return minMolWt;
    }
    
    public void setMaxMolecularWt(double molWt) {
        this.maxMolWt = molWt;
    }
    
    public double getMaxMolecularWt() {
        return maxMolWt;
    }
    
    public boolean hasMolecularWtFilter() {
        return (minMolWt != 0 || maxMolWt != Double.MAX_VALUE);
    }
    
    
    public void setMinPi(double pi) {
        this.minPi = pi;
    }
    
    public double getMinPi() {
        return minPi;
    }
    
    public void setMaxPi(double pi) {
        this.maxPi = pi;
    }
    
    public double getMaxPi() {
        return maxPi;
    }
    
    public boolean hasPiFilter() {
        return (minPi != 0 || maxPi != Double.MAX_VALUE);
    }
}
