/**
 * SpectrumCountClusterer.java
 * @author Vagisha Sharma
 * Apr 20, 2010
 * @version 1.0
 */
package org.yeastrc.www.compare.clustering;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.util.FileUtils;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.nrseq.ProteinListing;
import org.yeastrc.www.compare.ComparisonProtein;
import org.yeastrc.www.compare.ProteinComparisonDataset;
import org.yeastrc.www.compare.ProteinGroupComparisonDataset;
import org.yeastrc.www.compare.dataset.Dataset;
import org.yeastrc.www.compare.dataset.DatasetProteinInformation;
import org.yeastrc.www.compare.graph.ComparisonProteinGroup;

/**
 * 
 */
public class SpectrumCountClusterer {

	private static SpectrumCountClusterer instance = null;
	
	private static final Logger log = Logger.getLogger(SpectrumCountClusterer.class.getName());
	
	private SpectrumCountClusterer() {}
	
	public static synchronized SpectrumCountClusterer getInstance() {
		if(instance == null)
			instance = new SpectrumCountClusterer();
		return instance;
	}
	
	public ProteinGroupComparisonDataset clusterProteinGroupComparisonDataset(ProteinGroupComparisonDataset grpComparison,
			StringBuilder errorMesage, String dir) {
		
		long s = System.currentTimeMillis(); 
		for(ComparisonProteinGroup grpProtein: grpComparison.getProteinsGroups()) {
            for(ComparisonProtein protein: grpProtein.getProteins()) {
            	grpComparison.initializeProteinInfo(protein);
            }
        }
		long e = System.currentTimeMillis();
		log.info("Time to initialize protein info: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
		
		// Create the output directory. If it exists already remove it 
		if(new File(dir).exists()) {
			FileUtils.deleteFile(new File(dir)); 
		}
		if(!(new File(dir).mkdir()))  {
			errorMesage.append("Error creating directory: "+dir);
			return null;
		}
		
		// Write the input file
		if(!writeInputFile(grpComparison, errorMesage, dir)) {
			return null;
		}
		
		
		// write the R script
		String rscriptPath = null;
		log.info("Writing script file");
		try {
			rscriptPath = writeRScript(dir);
		} catch (IOException e1) {
			errorMesage.append("Error writing R script: "+e1.getMessage());
			return null;
		}
		
		// write the runner script
		String runScriptPath = null;
		log.info("Writing runner script");
		try {
			runScriptPath = writeShScript(dir, rscriptPath);
		} catch (IOException e1) {
			errorMesage.append("Error writing runner script: "+e1.getMessage());
			return null;
		}
		
		// run  R
		if(!runR(errorMesage, runScriptPath)) {
			return null;
		}
		
		log.info("Checking for output files");
		// make sure expected output files are present
		File output = new File(dir+File.separator+ClusteringConstants.OUTPUT_FILE);
		if(!output.exists()) {
			errorMesage.append("Output file "+output.getAbsolutePath()+" does not exist");
			return null;
		}
		
		List<Integer> datasetOrder;
		try {
			datasetOrder = readDatasetOrder(output);
		} catch (IOException e1) {
			errorMesage.append("Error reading output file: "+output.getAbsolutePath());
			return null;
		}
		
		List<Integer> groupOrder;
		try {
			groupOrder = readGroupOrder(output);
		} catch (IOException e1) {
			errorMesage.append("Error reading output file: "+output.getAbsolutePath());
			return null;
		}
		
		output = new File(dir+File.separator+ClusteringConstants.IMG_FILE);
		if(!output.exists()) {
			errorMesage.append("Output file "+output.getAbsolutePath()+" does not exist");
			return null;
		}
		
		return reorderComparison(grpComparison, datasetOrder, groupOrder);
		
	}
	
	private boolean runR(StringBuilder errorMesage, String scriptPath) {
		log.info("Executing script");
		Runtime rt = Runtime.getRuntime();
		Process process = null;
		try {
			//String cmdline = "/net/pr/vol1/ProteomicsResource/bin/R --vanilla --slave --file="+scriptPath;
			String cmdline = "sh "+scriptPath;
			log.info(cmdline);
			process = rt.exec(cmdline);
			// any error message?
            StreamGobbler errorGobbler = new 
                StreamGobbler(process.getErrorStream(), "ERROR");            
            
            // any output?
            StreamGobbler outputGobbler = new 
                StreamGobbler(process.getInputStream(), "OUTPUT");
            

            // kick them off
            outputGobbler.start();
            errorGobbler.start();
            
            
			int exitVal = process.waitFor();
			
			if(exitVal != 0) {
				errorMesage.append("Error running R -- exit value of "+exitVal+ " was returned.");
				return false;
			}
		} catch (IOException e1) {
			errorMesage.append("Error executing script: "+scriptPath+"\n"+e1.getMessage());
			return false;
		} catch (InterruptedException ex) {
			errorMesage.append("Error executing script: "+scriptPath+"\n"+ex.getMessage());
			return false;
		}
		return true;
	}

	private boolean writeInputFile(ProteinGroupComparisonDataset grpComparison,
			StringBuilder errorMesage, String dir) {
		String fileName = dir+File.separator+ClusteringConstants.INPUT_FILE;
		log.info("Writing input file: "+fileName);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(fileName));
			
			// write the IDs of the protein inferences being compared
			writer.write("Name");
			for(Dataset dataset: grpComparison.getDatasets()) {
				writer.write("\tID_"+dataset.getDatasetId());
			}
			writer.write("\n");
			
			// write the results so that we can cluster them
			for(ComparisonProteinGroup grpProtein: grpComparison.getProteinsGroups()) {
				
				// We only need to get the spectrum counts for one protein in this dataset
				ComparisonProtein prot = grpProtein.getProteins().get(0);
				
				String rowname = "";// grpProtein.getGroupId()+"_";
				ProteinListing listing = prot.getProteinListing();
				// Can't use common-names since two or more proteins can have the same common name
				rowname += listing.getFastaAccessions().get(0);
				
				writer.write(rowname);
				
				
				for(Dataset dataset: grpComparison.getDatasets()) {
					DatasetProteinInformation dpi = prot.getDatasetProteinInformation(dataset);
					if(dpi != null)
						writer.write("\t"+dpi.getNormalizedSpectrumCount());
					else
						writer.write("\t0");
				}
				writer.write("\n");
			}
		}
		catch(IOException ex) {
			errorMesage.append("Error creating input file: "+fileName+"\n"+ex.getMessage());
			return false;
		}
		finally {
			if(writer != null) try{writer.close();}catch(IOException ex){}
		}
		
		return true;
	}
	
	private ProteinGroupComparisonDataset reorderComparison(ProteinGroupComparisonDataset grpComparison,
			List<Integer> datasetOrder, List<Integer> groupOrder) {
		
		
		log.info("Reordering ProteinGroupComparisonDataset");
		long s = System.currentTimeMillis();
		
		ProteinGroupComparisonDataset clustered = new ProteinGroupComparisonDataset();
		
		// reorder the datasets
		List<Dataset> orderedDS = new ArrayList<Dataset>(grpComparison.getDatasetCount());
		List<? extends Dataset> originalDS = grpComparison.getDatasets();
		for(int i = datasetOrder.size() - 1; i >= 0; i--) {
			orderedDS.add(originalDS.get(datasetOrder.get(i) - 1)); // R returns 1-based indexes
		}
		clustered.setDatasets(orderedDS);
		
		// reorder the groups
		List<ComparisonProteinGroup> originalGrps = grpComparison.getProteinsGroups();
		for(int i = groupOrder.size() - 1; i >= 0; i--) {
			clustered.addProteinGroup(originalGrps.get(groupOrder.get(i) - 1)); // R returns 1-based indexes
		}
		
		clustered.initSummary();
		clustered.setRowCount(clustered.getTotalProteinCount());
		clustered.setCurrentPage(1);
		
		//clustered.setDisplayPageNumbers(pageNums);
		//clustered.setLastPage(1);
		clustered.setClustered(true);
		clustered.setInitialized(true);
		clustered.setSortBy(null);
		clustered.setSortOrder(null);
		
		long e = System.currentTimeMillis();
		log.info("Reordered ProteinGroupComparisonDataset in "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
		return clustered;
		
	}

	private List<Integer> readDatasetOrder(File output) throws IOException {
		return readOrder(output, 2);
	}
	
	private List<Integer> readGroupOrder(File output) throws IOException {
		return readOrder(output, 1);
	}
	
	private List<Integer> readOrder(File output, int readLine) throws IOException {
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(output));
			String line = reader.readLine();
			if(readLine == 2)
				line = reader.readLine();
			
			String[] tokens = line.trim().split(",");
			List<Integer> order = new ArrayList<Integer>(tokens.length);
			for(String tok: tokens)
				order.add(Integer.parseInt(tok));
			return order;
		}
		finally {
			if(reader != null) try{reader.close();}catch(IOException e){}
		}
		
	}
	
	private String writeShScript(String dir, String pathToRScript) throws IOException {
		
		String file = dir+File.separator+ClusteringConstants.SH_SCRIPT;
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write("#!/bin/sh\n");
			// read this: http://www.uni-koeln.de/rrzk/server/documentation/modules.html
			// writer.write(". /etc/profile.d/modules.sh\n");
			writer.write("module load modules modules-init modules-gs R\n");
			writer.write("R --vanilla --slave --file="+pathToRScript+"\n");
		}
		finally {
			if(writer != null) try{writer.close();}catch(IOException e){}
		}
		return file;
	}

	private String writeRScript(String dir) throws IOException {
		
		String file = dir+File.separator+ClusteringConstants.R_SCRIPT;
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write("test=read.table(\""+dir+File.separator+ClusteringConstants.INPUT_FILE+"\", header=T)\n");
			writer.write("test_sc=test[,-1]\n");
			writer.write("rownames(test_sc) <- test[,1]\n");
			writer.write("source(\"http://faculty.ucr.edu/~tgirke/Documents/R_BioCond/My_R_Scripts/my.colorFct.R\")\n");
			String devType = ClusteringConstants.IMG_FILE.substring(ClusteringConstants.IMG_FILE.lastIndexOf(".")+1);
			writer.write(devType+"(\""+dir+File.separator+ClusteringConstants.IMG_FILE+"\")\n");
			writer.write("options(expressions=10000)\n");
			writer.write("hm <- heatmap(as.matrix(test_sc), col=my.colorFct(), scale=\"none\")\n");
			writer.write("dev.off()\n");
			writer.write("write(hm$rowInd, file=\""+dir+File.separator+ClusteringConstants.OUTPUT_FILE+"\", sep=\",\", append=FALSE, length(ncolumns=hm$rowInd))\n");
			writer.write("write(hm$colInd, file=\""+dir+File.separator+ClusteringConstants.OUTPUT_FILE+"\", sep=\",\", append=TRUE, length(ncolumns=hm$colInd))\n");
		}
		finally {
			if(writer != null) try{writer.close();}catch(IOException e){}
		}
		return file;
	}
	
	public ProteinComparisonDataset clusterProteinComparisonDataset(
			ProteinComparisonDataset comparison) {
		// TODO Auto-generated method stub
		return null;
	}
}
