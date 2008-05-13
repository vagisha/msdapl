/**
 * 
 */
package org.yeastrc.progress;

/**
 * @author Mike
 *
 */
public class Funding {

	public Funding() {
		this.sourceName = null;
		this.grantNumber = null;
		this.grantAmount = null;
	}
	
	public int hashCode() {
		String codeString = "";
		
		if (this.sourceName != null)
			codeString += sourceName;
		
		if (this.grantNumber != null)
			codeString += grantNumber;
		
		if (this.grantAmount != null)
			codeString += grantAmount;
		
		if (this.sourceType != null)
			codeString += sourceType;
		
		return codeString.hashCode();
	}
	
	public boolean equals(Object o) {
		if ( ((Funding)o).hashCode() == this.hashCode() ) return true;
		return false;
	}
	
	private String sourceName;
	private String grantNumber;
	private String grantAmount;
	private String sourceType;
	
	/**
	 * @return the grantAmount
	 */
	public String getGrantAmount() {
		return grantAmount;
	}

	/**
	 * @param grantAmount the grantAmount to set
	 */
	public void setGrantAmount(String grantAmount) {
		this.grantAmount = grantAmount;
	}

	/**
	 * @return the grantNumber
	 */
	public String getGrantNumber() {
		return grantNumber;
	}

	/**
	 * @param grantNumber the grantNumber to set
	 */
	public void setGrantNumber(String grantNumber) {
		this.grantNumber = grantNumber;
	}

	/**
	 * @return the sourceName
	 */
	public String getSourceName() {
		return sourceName;
	}

	/**
	 * @param sourceName the sourceName to set
	 */
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	/**
	 * @return the type
	 */
	public String getSourceType() {
		return sourceType;
	}

	/**
	 * @param type the type to set
	 */
	public void setSourceType(String type) {
		this.sourceType = type;
	}
}
