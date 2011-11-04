/**
 * PercolatorXmlPsmId.java
 * @author Vagisha Sharma
 * Sep 18, 2010
 */
package org.yeastrc.ms.parser.percolator;

/**
 * 
 */
public class PercolatorXmlPsmId {

	private String fileName;
	private int scanNumber;
	private int charge;
	
	public PercolatorXmlPsmId(String fileName, int scanNumber, int charge) {
		this.fileName = fileName;
		this.scanNumber = scanNumber;
		this.charge = charge;
	}

	public String getFileName() {
		return fileName;
	}

	public int getScanNumber() {
		return scanNumber;
	}

	public int getCharge() {
		return charge;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setScanNumber(int scanNumber) {
		this.scanNumber = scanNumber;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

	public String toString() {
		return fileName+"_"+scanNumber+"_"+charge;
	}
	
}
