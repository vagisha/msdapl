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
    private boolean searchAllDescriptions = false;
    
    private double minMolWt = 0.0;
    private double maxMolWt = Double.MAX_VALUE;
    
    private double minPi = 0.0;
    private double maxPi = Double.MAX_VALUE;
    
    private int minPeptideCount = 0;
    private int maxPeptideCount = Integer.MAX_VALUE;
    private int minUniqPeptideCount = 0;
    private int maxUniqPeptideCount = Integer.MAX_VALUE;
    
    // ProteinProphet related
    private boolean hasProteinProphetFilters = false;
	private double peptideProbability = 0.0;
    private boolean applyToPeptide = true;
    private boolean applyToUniqPeptide = false;
    private double proteinProphetError = 0.01;
    private boolean useGroupProbability = true;
    
    public boolean hasProteinProphetFilters() {
		return hasProteinProphetFilters;
	}

	public void setHasProteinProphetFilters(boolean hasProteinProphetFilters) {
		this.hasProteinProphetFilters = hasProteinProphetFilters;
	}

	public double getPeptideProbability() {
		return peptideProbability;
	}

	public void setPeptideProbability(double peptideProbability) {
		this.peptideProbability = peptideProbability;
	}

	public boolean isApplyToPeptide() {
		return applyToPeptide;
	}

	public void setApplyToPeptide(boolean applyToPeptide) {
		this.applyToPeptide = applyToPeptide;
	}

	public boolean isApplyToUniqPeptide() {
		return applyToUniqPeptide;
	}

	public void setApplyToUniqPeptide(boolean applyToUniqPeptide) {
		this.applyToUniqPeptide = applyToUniqPeptide;
	}

	public double getProteinProphetError() {
		return proteinProphetError;
	}

	public void setProteinProphetError(double proteinProphetError) {
		this.proteinProphetError = proteinProphetError;
	}

	public boolean isUseGroupProbability() {
		return useGroupProbability;
	}

	public void setUseGroupProbability(boolean useGroupProbability) {
		this.useGroupProbability = useGroupProbability;
	}
	
    public int getMinPeptideCount() {
		return minPeptideCount;
	}

	public void setMinPeptideCount(int minPeptideCount) {
		this.minPeptideCount = minPeptideCount;
	}

	public int getMaxPeptideCount() {
		return maxPeptideCount;
	}

	public void setMaxPeptideCount(int maxPeptideCount) {
		this.maxPeptideCount = maxPeptideCount;
	}

	public int getMinUniqPeptideCount() {
		return minUniqPeptideCount;
	}

	public void setMinUniqPeptideCount(int minUniqPeptideCount) {
		this.minUniqPeptideCount = minUniqPeptideCount;
	}

	public int getMaxUniqPeptideCount() {
		return maxUniqPeptideCount;
	}

	public void setMaxUniqPeptideCount(int maxUniqPeptideCount) {
		this.maxUniqPeptideCount = maxUniqPeptideCount;
	}

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
    
    public boolean isSearchAllDescriptions() {
		return searchAllDescriptions;
	}

	public void setSearchAllDescriptions(boolean searchAllDescriptions) {
		this.searchAllDescriptions = searchAllDescriptions;
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
    
    public boolean hasPeptideCountFilter() {
    	return (minPeptideCount != 0 || maxPeptideCount != Integer.MAX_VALUE);
    }
    
    public boolean hasUniquePeptideCountFilter() {
    	return (minUniqPeptideCount != 0 || maxUniqPeptideCount != Integer.MAX_VALUE);
    }
}
