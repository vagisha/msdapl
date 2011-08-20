/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml;

/**
 * SequestMzidWriterApp.java
 * @author Vagisha Sharma
 * Aug 16, 2011
 * 
 */
public class SequestMzidWriterApp {

	public static void main(String[] args) throws MzIdentMlWriterException {
		
		// int experimentId = Integer.parseInt(args[0]);
		int experimentId = 105;
		
		SequestMsDataMzidDataProvider dataProvider = new SequestMsDataMzidDataProvider();
		dataProvider.setExperimentId(experimentId);
		
		MzidWriter writer = new MzidWriter();
		writer.setOutputFilePath("/Users/vagisha/WORK/MSDaPl_data/two-ms2/sequest/expt105.mzid");
		writer.setDataProvider(dataProvider);
		
		writer.start();
		writer.end();
		
		// To validate against schema:
		// xmllint --noout --schema /Users/vagisha/Desktop/mzIdentML/svn/schema/mzIdentML1.1.0.xsd expt105.mzid
	}
}
