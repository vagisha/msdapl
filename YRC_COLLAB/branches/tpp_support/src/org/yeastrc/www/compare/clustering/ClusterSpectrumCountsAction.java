/**
 * ClusterSpectrumCountsAction.java
 * @author Vagisha Sharma
 * Apr 18, 2010
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.jobqueue.MSJob;
import org.yeastrc.jobqueue.MSJobFactory;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.util.StringUtils;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.nrseq.ProteinListing;
import org.yeastrc.www.compare.ComparisonProtein;
import org.yeastrc.www.compare.ProteinComparisonDataset;
import org.yeastrc.www.compare.ProteinGroupComparisonDataset;
import org.yeastrc.www.compare.ProteinSetComparisonForm;
import org.yeastrc.www.compare.dataset.Dataset;
import org.yeastrc.www.compare.dataset.DatasetProteinInformation;
import org.yeastrc.www.compare.graph.ComparisonProteinGroup;
import org.yeastrc.www.compare.util.VennDiagramCreator;

/**
 * 
 */
public class ClusterSpectrumCountsAction extends Action {

private static final Logger log = Logger.getLogger(ClusterSpectrumCountsAction.class.getName());
    
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        
        log.info("Clustering spectrum counts for comparison results");
        
        ProteinSetComparisonForm myForm = (ProteinSetComparisonForm) request.getAttribute("comparisonForm");
        if(myForm == null) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Comparison form not found in request"));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        
        if(!myForm.getGroupIndistinguishableProteins()) {
            ProteinComparisonDataset comparison = (ProteinComparisonDataset) request.getAttribute("comparisonDataset");
            if(comparison == null) {
                ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Comparison dataset not found in request"));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
            }
            
            long s = System.currentTimeMillis();
            clusterProteinComparisonDataset(comparison);
            long e = System.currentTimeMillis();
            log.info("Time to culster ProteinComparisonDataset: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
            
        }
        else {
            ProteinGroupComparisonDataset grpComparison = (ProteinGroupComparisonDataset) request.getAttribute("comparisonGroupDataset");
            if(grpComparison == null) {
                ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Comparison dataset not found in request"));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
            }
            
            long s = System.currentTimeMillis();
            StringBuilder errorMessage = new StringBuilder();
            String baseDir = request.getSession().getServletContext().getRealPath("clustering");
            String dir = String.valueOf(System.currentTimeMillis());
            baseDir = baseDir+File.separator+dir;
            
            ProteinGroupComparisonDataset clusteredGrpComparison = clusterProteinGroupComparisonDataset(grpComparison, errorMessage, baseDir);
            if(clusteredGrpComparison == null) {
            	ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Clustering error: "+errorMessage.toString()));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
            }
            long e = System.currentTimeMillis();
            log.info("Time to culster ProteinGroupComparisonDataset: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
            
            String imgUrl = request.getSession().getServletContext().getContextPath()+"/clustering/"+dir+"/clustered.png";
            request.setAttribute("clusteredImgUrl", imgUrl);
            
            // Create Venn Diagram only if 2 or 3 datasets are being compared
            if(clusteredGrpComparison.getDatasetCount() == 2 || clusteredGrpComparison.getDatasetCount() == 3) {
                String googleChartUrl = VennDiagramCreator.instance().getChartUrl(clusteredGrpComparison);
                request.setAttribute("chart", googleChartUrl);
            }
            
            // create a list of the dataset ids being compared
            // Get the selected protein inference run ids
            List<Integer> allRunIds = myForm.getAllSelectedRunIds();
            request.setAttribute("datasetIds", StringUtils.makeCommaSeparated(allRunIds));
            
            request.setAttribute("comparison", clusteredGrpComparison);
            request.setAttribute("speciesIsYeast", isSpeciesYeast(clusteredGrpComparison.getDatasets()));
            return mapping.findForward("ProteinGroupList");
        }
        
        return mapping.findForward("Failure");
    }

	private ProteinGroupComparisonDataset clusterProteinGroupComparisonDataset(ProteinGroupComparisonDataset grpComparison,
			StringBuilder errorMesage, String dir) {
		
		long s = System.currentTimeMillis(); 
		for(ComparisonProteinGroup grpProtein: grpComparison.getProteinsGroups()) {
            for(ComparisonProtein protein: grpProtein.getProteins()) {
            	grpComparison.initializeProteinInfo(protein);
            }
        }
		long e = System.currentTimeMillis();
		log.info("Time to initialize protein info: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
		
		// Create the output directory
		if(!(new File(dir).mkdir()))  {
			errorMesage.append("Error creating directory: "+dir);
			return null;
		}
		
		// Write the input file
		if(!writeInputFile(grpComparison, errorMesage, dir)) {
			return null;
		}
		
		
		// write the script
		String scriptPath = null;
		log.info("Writing script file");
		try {
			scriptPath = writeRScript(dir);
		} catch (IOException e1) {
			errorMesage.append("Error writing R script: "+e1.getMessage());
			return null;
		}
		
		// run  R
		if(!runR(errorMesage, scriptPath)) {
			return null;
		}
		
		log.info("Checking for output files");
		// make sure expected output files are present
		File output = new File(dir+File.separator+"output.txt");
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
		
		output = new File(dir+File.separator+"clustered.png");
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
			String cmdline = "/usr/bin/R --vanilla --slave --file="+scriptPath;
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
		String fileName = dir+File.separator+"input.txt";
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
				if(listing.getCommonNames().size() > 0) {
					rowname += listing.getCommonNames().get(0);
				}
				else {
					rowname += listing.getFastaAccessions().get(0);
				}
				
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
		clustered.setIsClustered(true);
		clustered.setIsInitialized(true);
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

	private String writeRScript(String dir) throws IOException {
		
		String file = dir+File.separator+"r.script.txt";
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write("test=read.table(\""+dir+File.separator+"input.txt\", header=T)\n");
			writer.write("test_sc=test[,-1]\n");
			writer.write("rownames(test_sc) <- test[,1]\n");
			writer.write("source(\"http://faculty.ucr.edu/~tgirke/Documents/R_BioCond/My_R_Scripts/my.colorFct.R\")\n");
			writer.write("png(\""+dir+File.separator+"clustered.png\")\n");
			writer.write("hm <- heatmap(as.matrix(test_sc), col=my.colorFct(), scale=\"none\")\n");
			writer.write("dev.off()\n");
			writer.write("write(hm$rowInd, file=\""+dir+File.separator+"output.txt\", sep=\",\", append=FALSE, length(ncolumns=hm$rowInd))\n");
			writer.write("write(hm$colInd, file=\""+dir+File.separator+"output.txt\", sep=\",\", append=TRUE, length(ncolumns=hm$colInd))\n");
		}
		finally {
			if(writer != null) try{writer.close();}catch(IOException e){}
		}
		return file;
	}

	private void clusterProteinComparisonDataset(
			ProteinComparisonDataset comparison) {
		// TODO Auto-generated method stub
		
	}
	
	private boolean isSpeciesYeast(List<? extends Dataset> datasets) throws Exception {
        
        
        Set<Integer> notYeastExpts = new HashSet<Integer>();
        
        ProteinferRunDAO runDao = ProteinferDAOFactory.instance().getProteinferRunDao();
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        
        for(Dataset dataset: datasets) {
            List<Integer> searchIds = runDao.loadSearchIdsForProteinferRun(dataset.getDatasetId());
            if(searchIds != null) {
                for(int searchId: searchIds) {

                    MsSearch search = searchDao.loadSearch(searchId);

                    if(notYeastExpts.contains(search.getExperimentId())) // if we have already seen this and it is not yeast go on looking
                        continue;

                    MSJob job = MSJobFactory.getInstance().getJobForExperiment(search.getExperimentId());

                    if(job.getTargetSpecies() == 4932) {
                        return true;
                    }
                    else 
                        notYeastExpts.add(search.getExperimentId());
                }
            }
        }
        return false;
    }
}
