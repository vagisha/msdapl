/*
 * Y2HBaitMutation.java
 * Created on Apr 1, 2005
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.y2h;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Apr 1, 2005
 */

public class Y2HBaitMutation {

	public int hashCode() {
		return ( this.origAminoAcid + this.newAminoAcid + this.position ).hashCode();
	}
	
	public boolean equals(Object o) {
		if (this.hashCode() == ((Y2HBaitMutation)o).hashCode()) return true;
		return false;
	}
	
	
	
	/**
	 * @return Returns the id.
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return Returns the newAminoAcid.
	 */
	public String getNewAminoAcid() {
		return newAminoAcid;
	}
	/**
	 * @param newAminoAcid The newAminoAcid to set.
	 */
	public void setNewAminoAcid(String newAminoAcid) {
		this.newAminoAcid = newAminoAcid;
	}
	/**
	 * @return Returns the origAminoAcid.
	 */
	public String getOrigAminoAcid() {
		return origAminoAcid;
	}
	/**
	 * @param origAminoAcid The origAminoAcid to set.
	 */
	public void setOrigAminoAcid(String origAminoAcid) {
		this.origAminoAcid = origAminoAcid;
	}
	/**
	 * @return Returns the position.
	 */
	public int getPosition() {
		return position;
	}
	/**
	 * @param position The position to set.
	 */
	public void setPosition(int position) {
		this.position = position;
	}
	/**
	 * @return Returns the baitID.
	 */
	public int getBaitID() {
		return baitID;
	}
	/**
	 * @param baitID The baitID to set.
	 */
	public void setBaitID(int baitID) {
		this.baitID = baitID;
	}
	
	private int id;
	private int baitID;
	private int position;
	private String origAminoAcid;
	private String newAminoAcid;
}
