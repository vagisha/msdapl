package org.yeastrc.grant;

import org.yeastrc.project.Researcher;

public class Grant {

	private int ID;
	private String title;
	private Researcher grantPI;
	private FundingSource fundingSource;
	private String grantNumber;
	private String grantAmount;
	
	/**
	 * @return the fundingSource
	 */
	public FundingSource getFundingSource() {
		return fundingSource;
	}
	/**
	 * @param fundingSource the fundingSource to set
	 */
	public void setFundingSource(FundingSource fundingSource) {
		this.fundingSource = fundingSource;
	}
	
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the grantPI
	 */
	public Researcher getGrantPI() {
		return grantPI;
	}
	/**
	 * @param grantPI the grantPI to set
	 */
	public void setGrantPI(Researcher grantPI) {
		this.grantPI = grantPI;
	}
	
	public int getPIID() {
		return grantPI.getID();
	}
	
	public String getPILastName() {
		return grantPI.getLastName();
	}
	
	/**
	 * @return the ID
	 */
	public int getID() {
		return ID;
	}
	/**
	 * @param ID the ID to set
	 */
	public void setID(int ID) {
		
		this.ID = ID;
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
}
