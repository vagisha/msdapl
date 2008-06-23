/**
 * 
 */
package org.yeastrc.progress;

import java.util.Comparator;

import org.yeastrc.project.Project;

/**
 * @author Mike
 *
 */
public class Funding implements Comparable {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compareTo(Object arg0) {
		Funding f = (Funding)arg0;
		
		Integer x;
		Integer y;
		
		if ( !this.isFederal() ) x = 0;
		else if ( !this.isPHS() ) x = 1;
		else x = 2;
		
		if ( !f.isFederal() ) y = 0;
		else if ( !f.isPHS() ) y = 1;
		else y = 2;
		
		return x.compareTo( y );
	}
	
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
	
	public boolean isFederal() {
		return this.getSourceType().equals( "FED" );
	}
	
	public boolean isPHS() {
		if ( !this.isFederal() ) return false;
		
		for( String name : this.PHS_FUNDING_AGENCIES ) {
			if ( this.sourceName.contains( name ) )
				return true;
		}
		
		return false;
	}
	
	private String[] PHS_FUNDING_AGENCIES = {
			"AHRQ",
			"CDC",
			"FDA",
			"HRSA",
			"NIH",
			"OASH",
			"SAMHSA",
		};
	
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
