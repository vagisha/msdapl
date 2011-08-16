/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml;

import org.yeastrc.ms.writer.mzidentml.jaxb.AnalysisProtocolCollectionType;

/**
 * AnalysisProtocolCollectionMaker.java
 * @author Vagisha Sharma
 * Aug 4, 2011
 * 
 */
public interface AnalysisProtocolCollectionMaker {

	public static final String SEQUEST_PROTOCOL_ID = "sequest_protocol";
	public static final String FRAGMENT_ION_TOLERANCE = "fragment_ion_tolerance";
	public static final String PEPTIDE_MASS_UNITS = "peptide_mass_units";
	public static final String PEPTIDE_MASS_TOLERANCE = "peptide_mass_tolerance";
	
	public AnalysisProtocolCollectionType getSequestAnalysisProtocol() throws MzIdentMlWriterException;
}
