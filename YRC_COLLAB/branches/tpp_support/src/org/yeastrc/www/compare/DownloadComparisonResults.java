/**
 * DownloadComparisonResults.java
 * @author Vagisha Sharma
 * May 4, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.nrseq.ProteinReference;
import org.yeastrc.www.compare.dataset.Dataset;
import org.yeastrc.www.compare.dataset.DatasetProteinInformation;
import org.yeastrc.www.compare.graph.ComparisonProteinGroup;

/**
 * 
 */
public class DownloadComparisonResults extends Action {

    private static final Logger log = Logger.getLogger(DownloadComparisonResults.class.getName());
    
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        
        log.info("Downloading comparison results");
        
        long startTime = System.currentTimeMillis();
        
        ProteinSetComparisonForm myForm = (ProteinSetComparisonForm) request.getAttribute("comparisonForm");
        if(myForm == null) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Comparison form not found in request"));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition","attachment; filename=\"ProteinSetComparison.txt\"");
        response.setHeader("cache-control", "no-cache");
        PrintWriter writer = response.getWriter();
        writer.write("\n\n");
        writer.write("Date: "+new Date()+"\n\n");
        // Is the data clustered
        if(myForm.isCluster()) {
        	writer.write("Clustered Spectrum Counts = TRUE\n\n");
        }
        
        
        if(!myForm.getGroupIndistinguishableProteins()) {
            ProteinComparisonDataset comparison = (ProteinComparisonDataset) request.getAttribute("comparisonDataset");
            if(comparison == null) {
                ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Comparison dataset not found in request"));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
            }
            
            comparison.setDisplayColumns(myForm.getDisplayColumns()); // columns we will print
            comparison.setDatasetOrder(myForm.getAllSelectedRunIdsOrdered()); // dataset order
            long s = System.currentTimeMillis();
            writeResults(writer, comparison, myForm.getSelectedBooleanFilters(), myForm);
            writer.close();
            long e = System.currentTimeMillis();
            log.info("Results written in: "+TimeUtils.timeElapsedMinutes(s,e)+" minutes");
        }
        else {
            ProteinGroupComparisonDataset grpComparison = (ProteinGroupComparisonDataset) request.getAttribute("comparisonGroupDataset");
            if(grpComparison == null) {
                ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Comparison dataset not found in request"));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
            }
            
            grpComparison.setDisplayColumns(myForm.getDisplayColumns()); // columns we will print
            grpComparison.setDatasetOrder(myForm.getAllSelectedRunIdsOrdered()); // dataset order
            long s = System.currentTimeMillis();
            writeResults(writer, grpComparison, myForm.getSelectedBooleanFilters(), myForm);
            writer.close();
            long e = System.currentTimeMillis();
            log.info("Results written in: "+TimeUtils.timeElapsedMinutes(s,e)+" minutes");
        }
        
        
        long e = System.currentTimeMillis();
        log.info("DownloadComparisonResults results in: "+TimeUtils.timeElapsedMinutes(startTime,e)+" minutes");
        return null;
    }

    
    private void writeResults(PrintWriter writer, ProteinComparisonDataset comparison, DatasetBooleanFilters filters,
            ProteinSetComparisonForm form) {
        
        writer.write("Total protein count: "+comparison.getTotalProteinCount()+"\n");
        writer.write("Filtered protein count: "+comparison.getFilteredProteinCount()+"\n");
        writer.write("\n\n");
        
        // Boolean Filters
        writeBooleanFilters(writer, filters);
        
        // Protein filters
        writeProteinFilters(writer, form.getProteinPropertiesFilters());
        
        
        // Datasets
        writer.write("Datasets: \n");
        int idx = 0;
        for(Dataset dataset: comparison.getDatasets()) {
            writer.write(dataset.getSourceString()+" ID "+dataset.getDatasetId()+
                    ": Proteins  "+comparison.getProteinCount(idx++)+
                    "; SpectrumCount(max.) "+dataset.getSpectrumCount()+"("+dataset.getMaxProteinSpectrumCount()+")\n");
        }
        writer.write("\n\n");
        
        // Common protein groups
        writer.write("Common Proteins:\n");
        writer.write("\t");
        for(Dataset dataset: comparison.getDatasets()) {
            writer.write("ID_"+dataset.getDatasetId()+"\t");
        }
        writer.write("\n");
        for(int i = 0; i < comparison.getDatasetCount(); i++) {
            writer.write("ID_"+comparison.getDatasets().get(i).getDatasetId()+"\t");
            for(int j = 0; j < comparison.getDatasetCount(); j++) 
                writer.write(comparison.getCommonProteinCount(i, j)+"\t");
            writer.write("\n");
        }
        writer.write("\n\n");
        
        // legend
        writeLegend(writer);
        
        // write the header
        writeHeader(writer, comparison.getDatasets(), comparison.getDisplayColumns(), form.isIncludeDescriptions(), false);
        
        // Remove any sorting criteria so that all fields get initialized properly.
        comparison.setSortBy(null);
        comparison.setSortOrder(null);
        
        // write information for each protein
        for(ComparisonProtein protein: comparison.getProteins()) {
            
            comparison.initializeProteinInfo(protein);
            writeComparisonProtein(writer, comparison.getDatasets(), comparison.getDisplayColumns(),
            		form.isIncludeDescriptions(), protein, false);
        }
        
        writer.write("\n\n");
    }


	private void writeHeader(PrintWriter writer, List<? extends Dataset> datasets, DisplayColumns displayColumns,
			boolean printDescription, boolean writeProteinGroupsHeader) {
		// print the header
        writer.write("ProteinID\t");
        if(writeProteinGroupsHeader)
        	writer.write("ProteinGroupID\t");
        
        if(displayColumns.isShowFastaId())
        	writer.write("Fasta ID\t");
        if(displayColumns.isShowCommonName())
        	writer.write("CommonName\t");
        if(displayColumns.isShowMolWt())
        	writer.write("Mol.Wt.\t");
        if(displayColumns.isShowPi()) 
        	writer.write("pI\t");
        if(displayColumns.isShowTotalSeq())
        	writer.write("NumSeq\t");
        
        if(displayColumns.isShowPresent()) {
        	for(Dataset dataset: datasets) {
        		writer.write(dataset.getSourceString()+"("+dataset.getDatasetId()+")\t");
        	}
        }
        // sequence, ion, unique ion, spectrum count column headers.
        for(Dataset dataset: datasets) {
        	if(displayColumns.isShowNumSeq())
        		writer.write("#Seq("+dataset.getDatasetId()+")\t");
        	if(displayColumns.isShowNumIons())
        		writer.write("#Ion("+dataset.getDatasetId()+")\t");
        	if(displayColumns.isShowNumUniqIons())
        		writer.write("#U.Ion("+dataset.getDatasetId()+")\t");
        	if(displayColumns.isShowSpectrumCount())
        		writer.write("SC("+dataset.getDatasetId()+")\t");
        	if(displayColumns.isShowNsaf()) // NSAF column headers.
        		writer.write("NSAF("+dataset.getDatasetId()+")\t");
        }
        
        if(printDescription)
            writer.write("Description\n");
        else
            writer.write("\n");
	}


	private void writeLegend(PrintWriter writer) {
		// legend
        // *  Present and Parsimonious
        // =  Present and NOT parsimonious
        // g  group protein
        // -  NOT present
        writer.write("\n\n");
        writer.write("*  Protein present and parsimonious\n");
        writer.write("=  Protein present and NOT parsimonious\n");
        writer.write("-  Protein NOT present\n");
        writer.write("g  Group protein\n");
        writer.write("NOTE: for ProteinProphet data-sets parsimonious = NOT subsumed.");
        writer.write("\n\n");
	}

	private void writeProteinFilters(PrintWriter writer, ProteinPropertiesFilters filters) {
		
		// Accession string filter
		if(filters.hasAccessionFilter()) {
            writer.write("Filtering for FASTA ID(s): "+filters.getAccessionLike()+"\n\n");
        }
        
        // Description string filter
		if(filters.hasDescriptionLikeFilter()) {
            writer.write("Filtering for description term(s) LIKE: "+filters.getDescriptionLike()+"\n\n");
        }
        
        // Description string filter
		if(filters.hasDescriptionNotLikeFilter()) {
            writer.write("Filtering for description term(s) NOT LIKE: "+filters.getDescriptionNotLike()+"\n\n");
        }
		
		// Was "Search All" checked for description search
		if(filters.hasDescriptionLikeFilter() || filters.hasDescriptionNotLikeFilter()) {
			if(filters.isSearchAllDescriptions()) {
				writer.write("Descriptions in Swiss-Prot and NCBI-NR were searched\n\n");
			}
		}
        
        // Molecular wt. filter
		if(filters.hasMolecularWtFilter()) {
            writer.write("Molecular Wt. Min: "+filters.getMinMolecularWt());
            if(filters.getMaxMolecularWt() < Double.MAX_VALUE)
            	writer.write("\tMax: "+filters.getMaxMolecularWt());
            writer.write("\n\n");
        }
        
        // pI filter
		if(filters.hasPiFilter()) {
            writer.write("pI Min: "+filters.getMinPi());
            if(filters.getMaxPi() < Double.MAX_VALUE)
            	writer.write("\tMax: "+filters.getMaxPi());
            writer.write("\n\n");
        }
		
		// Min and max peptides
		if(filters.hasPeptideCountFilter()) {
			writer.write("# Peptides Min: "+filters.getMinPeptideCount());
			if(filters.getMaxPeptideCount()< Integer.MAX_VALUE)
				writer.write("\tMax: "+filters.getMaxPeptideCount());
			writer.write("\n");
		}
		if(filters.hasUniquePeptideCountFilter()) {
			writer.write("# Uniqie Peptides Min: "+filters.getMinUniqPeptideCount());
			if(filters.getMaxUniqPeptideCount() < Integer.MAX_VALUE)
				writer.write("\tMax: "+filters.getMaxUniqPeptideCount());
			writer.write("\n");
		}
		
		//ProteinProphetFilters
		if(filters.hasProteinProphetFilters()) {
			writer.write("\nProteinProphet FILTERS\n");
			writer.write("ProteinProphetError: "+filters.getProteinProphetError()+"\n");
			writer.write("Use ProteinProphet group probability: "+filters.isUseGroupProbability()+"\n");
			writer.write("Min. peptide probability: "+filters.getPeptideProbability()+"\n");
			writer.write("Apply peptide probability to: ");
			if(filters.isApplyToPeptide())
				writer.write("# Peptides");
			if(filters.isApplyToUniqPeptide())
				writer.write("  # Uniq. Peptides");
			writer.write("\n\n");
		}
	}

    private void writeBooleanFilters(PrintWriter writer,
            DatasetBooleanFilters filters) {
        
        boolean filtersFound = false;
        writer.write("Boolean Filters: \n");
        
        
        if(filters.getAndFilters().size() > 0) {
            filtersFound = true;
            writer.write("AND:\n");
            for(Dataset ds: filters.getAndFilters()) {
                writer.write("\t"+ds.getDatasetId()+"  "+ds.getDatasetComments()+"\n");
            }
        }
        if(filters.getOrFilters().size() > 0) {
            filtersFound = true;
            writer.write("OR:\n");
            for(Dataset ds: filters.getOrFilters()) {
                writer.write("\t"+ds.getDatasetId()+"  "+ds.getDatasetComments()+"\n");
            }
        }
        if(filters.getNotFilters().size() > 0) {
            filtersFound = true;
            writer.write("NOT:\n");
            for(Dataset ds: filters.getNotFilters()) {
                writer.write("\t"+ds.getDatasetId()+"  "+ds.getDatasetComments()+"\n");
            }
        }
        if(filters.getXorFilters().size() > 0) {
            filtersFound = true;
            writer.write("XOR:\n");
            for(Dataset ds: filters.getXorFilters()) {
                writer.write("\t"+ds.getDatasetId()+"  "+ds.getDatasetComments()+"\n");
            }
        }
        if(filtersFound)
            writer.write("\n\n");
        else
            writer.write("No filters found\n\n");
    }
    
    private void writeResults(PrintWriter writer, ProteinGroupComparisonDataset comparison, DatasetBooleanFilters filters,
            ProteinSetComparisonForm form) {
        
      writer.write("Total Protein Groups (Total Proteins): "+comparison.getTotalProteinGroupCount()+" ("+comparison.getTotalProteinCount()+")\n");
      writer.write("\n\n");
      
      // Boolean Filters
      writeBooleanFilters(writer, filters);
      
      // Protein filters
      writeProteinFilters(writer, form.getProteinPropertiesFilters());
      
      
      // Datasets
      writer.write("Datasets: \n");
      int idx = 0;
      for(Dataset dataset: comparison.getDatasets()) {
          writer.write(dataset.getSourceString()+" ID "+dataset.getDatasetId()+
                  ": Proteins Groups (# Proteins)  "+comparison.getProteinGroupCount(idx)+" ("+comparison.getProteinCount(idx++)+") "+
                  "; SpectrumCount(max.) "+dataset.getSpectrumCount()+"("+dataset.getMaxProteinSpectrumCount()+")\n");
      }
      writer.write("\n\n");
      
      // Common protein groups
      writer.write("Common Proteins:\n");
      writer.write("\t");
      for(Dataset dataset: comparison.getDatasets()) {
          writer.write("ID_"+dataset.getDatasetId()+"\t");
      }
      writer.write("\n");
      for(int i = 0; i < comparison.getDatasetCount(); i++) {
          writer.write("ID_"+comparison.getDatasets().get(i).getDatasetId()+"\t");
          for(int j = 0; j < comparison.getDatasetCount(); j++) {
              writer.write(comparison.getCommonProteinGroupCount(i, j)+" ("+comparison.getCommonProteinGroupsPerc(i, j)+"%)\t");
          }
          writer.write("\n");
      }
      writer.write("\n\n");
      
      // legend
      writeLegend(writer);
      

      // print the proteins in each protein group
      writeHeader(writer, comparison.getDatasets(), comparison.getDisplayColumns(), form.isIncludeDescriptions(), true);
      
      // Remove any sorting criteria so that all fields get initialized properly.
      comparison.setSortBy(null);
      comparison.setSortOrder(null);
      
      if(!form.isCollapseProteinGroups())
          writeSplitProteinGroup(writer, comparison, form.isIncludeDescriptions());
      else
          writeCollapsedProteinGroup(writer, comparison, form.isIncludeDescriptions());
      
      writer.write("\n\n");
      
    }
    
    private void writeSplitProteinGroup(PrintWriter writer,
            ProteinGroupComparisonDataset comparison, boolean printDescription) {

        for(ComparisonProteinGroup grpProtein: comparison.getProteinsGroups()) {

            for(ComparisonProtein protein: grpProtein.getProteins()) {
                comparison.initializeProteinInfo(protein);

                writeComparisonProtein(writer, comparison.getDatasets(), comparison.getDisplayColumns(),
                		printDescription,
						protein, true);
            }
        }
    }


	private void writeComparisonProtein(PrintWriter writer,
			List<? extends Dataset> datasets, DisplayColumns displayColumns, boolean printDescription,
			ComparisonProtein protein, boolean printGroupId) {
		
		writer.write(protein.getNrseqId()+"\t");
		if(printGroupId)
			writer.write(protein.getGroupId()+"\t");
		
		if(displayColumns.isShowFastaId()) {
			try {
				writer.write(protein.getAccessionsCommaSeparated()+"\t");
			} catch (SQLException e) {
				log.error("Error getting accession", e);
				writer.write("ERROR\t");
			}
		}
		
		if(displayColumns.isShowCommonName()) {
			try {
				writer.write(protein.getCommonNamesCommaSeparated()+"\t");
			} catch (SQLException e) {
				log.error("Error getting common name", e);
				writer.write("ERROR\t");
			}
		}
		
		if(displayColumns.isShowMolWt())
			writer.write(protein.getMolecularWeight()+"\t");
		if(displayColumns.isShowPi())
			writer.write(protein.getPi()+"\t");
		if(displayColumns.isShowTotalSeq()) 
			writer.write(protein.getTotalPeptideSeqCount()+"\t");
        // writer.write(protein.getMaxPeptideIonCount()+"\t");
       
		if(displayColumns.isShowPresent()) {
			for(Dataset dataset: datasets) {
				DatasetProteinInformation dpi = protein.getDatasetProteinInformation(dataset);
				if(dpi == null || !dpi.isPresent()) {
					writer.write("-");
				}
				else {
					if(dpi.isParsimonious()) {
						writer.write("*");
					}
					else {
						writer.write("=");
					}
					if(dpi.isGrouped())
						writer.write("g");
				}
				writer.write("\t");
			}
		}
		
        // spectrum count information
        for(Dataset dataset: datasets) {
            
            DatasetProteinInformation dpi = protein.getDatasetProteinInformation(dataset);
            if(dpi == null || !dpi.isPresent()) {
            	if(displayColumns.isShowNumSeq())
            		writer.write("0\t"); // # seq.
            	if(displayColumns.isShowNumIons())
            		writer.write("0\t"); // #ions
            	if(displayColumns.isShowNumUniqIons())
            		writer.write("0\t"); // # uniq ions
            	if(displayColumns.isShowSpectrumCount())
            		writer.write("0\t"); // SC
            }
            else {
            	if(displayColumns.isShowNumSeq())
            		writer.write(dpi.getSequenceCount()+"\t");
            	if(displayColumns.isShowNumIons())
            		writer.write(dpi.getIonCount()+"\t");
            	if(displayColumns.isShowNumUniqIons())
            		writer.write(dpi.getUniqueIonCount()+"\t");
            	if(displayColumns.isShowSpectrumCount())
            		writer.write(dpi.getSpectrumCount()+"("+dpi.getNormalizedSpectrumCountRounded()+")\t");
            }
            
            // NSAF information
            if(displayColumns.isShowNsaf()) {

            	if(!dataset.getSource().isIdPicker()) {
            		writer.write("-1\t");
            		continue;  // NSAF information is available only for IDPicker results.
            	}
            	else {
            		if(dpi == null || !dpi.isPresent()) {
            			writer.write("0\t");
            		}
            		else {
            			writer.write(dpi.getNsafFormatted()+"\t");
            		}
            	}
            }
        }
        
        if(printDescription) {
        	List<ProteinReference> descRefs = protein.getProteinListing().getDescriptionReferences();
        	
        	if(descRefs != null && descRefs.size() > 0) {
        		// TODO Which descriptions do we want to print??
        		writer.write(descRefs.get(0).getDescription());
        	}
        }
        writer.write("\n");
	}
    
    private void writeCollapsedProteinGroup(PrintWriter writer,
            ProteinGroupComparisonDataset comparison, boolean includeDescription) {

    	DisplayColumns displayColumns = comparison.getDisplayColumns();
    	
        for(ComparisonProteinGroup grpProtein: comparison.getProteinsGroups()) {

            String nrseqIdString = "";
            String nameString = "";
            String commonNameString = "";
            String molWtString = "";
            String piString = "";
            String nsafStrings[] = new String[comparison.getDatasetCount()];
            for(int i = 0; i < comparison.getDatasetCount(); i++)
                nsafStrings[i] = "";
            String descriptionString = "";
            
                
            for(ComparisonProtein protein: grpProtein.getProteins()) {
                comparison.initializeProteinInfo(protein);

                nrseqIdString += ","+protein.getNrseqId();
                try {
					nameString += ","+protein.getAccessionsCommaSeparated();
				} catch (SQLException e1) {
					log.error("Error getting accession", e1);
					nameString += ",ERROR";
				}
                try {
					String cn = protein.getCommonNamesCommaSeparated();
                	if(cn != null && cn.trim().length() > 0)
                		commonNameString += ","+cn;
				} catch (SQLException e1) {
					log.error("Error getting common name", e1);
					commonNameString += ",ERROR";
				}
                molWtString += ","+protein.getMolecularWeight();
                piString += ","+protein.getPi();
                
                if(includeDescription) {
                	List<ProteinReference> descRefs = protein.getProteinListing().getDescriptionReferences();
                	
                	if(descRefs != null && descRefs.size() > 0) { 
                		// TODO Which descriptions do we want to print??
                		descriptionString += ","+descRefs.get(0).getDescription();
                	}
                }
                
                
                // NSAF information
                int dsIdx = 0;
                for(Dataset dataset: comparison.getDatasets()) {
                	
                	if(!dataset.getSource().isIdPicker()) {
                		nsafStrings[dsIdx] = ",-1"; // NSAF information is available only for IDPicker results
                	}
                	else {
                		DatasetProteinInformation dpi = protein.getDatasetProteinInformation(dataset);
                		if(dpi == null || !dpi.isPresent()) {
                			nsafStrings[dsIdx] += ",0";
                		}
                		else {
                			nsafStrings[dsIdx] += ","+dpi.getNsafFormatted();
                		}
                	}
                    dsIdx++;
                }
            }

            writer.write(nrseqIdString.substring(1)+"\t");
            writer.write(grpProtein.getGroupId()+"\t");
            
            if(displayColumns.isShowFastaId())
            	writer.write(nameString.substring(1)+"\t");
            
            if(displayColumns.isShowCommonName()) {
            	if(commonNameString.length() > 0)
            		writer.write(commonNameString.substring(1)+"\t");
            	else
            		writer.write("\t");
            }
            
            if(displayColumns.isShowMolWt())
            	writer.write(molWtString.substring(1)+"\t");
            if(displayColumns.isShowPi())
            	writer.write(piString.substring(1)+"\t");
            if(displayColumns.isShowTotalSeq())
            	writer.write(grpProtein.getTotalPeptideSeqCount()+"\t");
            
            ComparisonProtein oneProtein = grpProtein.getProteins().get(0);
            // The value of isParsimonious will be the same for all proteins in a group
            if(displayColumns.isShowPresent()) {
            	for(Dataset dataset: comparison.getDatasets()) {
            		DatasetProteinInformation dpi = oneProtein.getDatasetProteinInformation(dataset);

            		if(dpi == null || !dpi.isPresent()) {
            			writer.write("-");
            		}
            		else {
            			if(dpi.isParsimonious()) {
            				writer.write("*");
            			}
            			else {
            				writer.write("=");
            			}
            			if(dpi.isGrouped())
            				writer.write("g");
            		}
            		writer.write("\t");
            	}
            }
            // The spectrum count information will be the same for all proteins in a group
            int dsIndex = 0;
            for(Dataset dataset: comparison.getDatasets()) {

                DatasetProteinInformation dpi = oneProtein.getDatasetProteinInformation(dataset);
                if(dpi == null || !dpi.isPresent()) {
                	if(displayColumns.isShowNumSeq())
                		writer.write("0\t"); // # seq.
                	if(displayColumns.isShowNumIons())
                		writer.write("0\t"); // #ions
                	if(displayColumns.isShowNumUniqIons())
                		writer.write("0\t"); // # uniq ions
                	if(displayColumns.isShowSpectrumCount())
                		writer.write("0\t"); // SC
                }
                else {
                	if(displayColumns.isShowNumSeq())
                		writer.write(dpi.getSequenceCount()+"\t");
                	if(displayColumns.isShowNumIons())
                		writer.write(dpi.getIonCount()+"\t");
                	if(displayColumns.isShowNumUniqIons())
                		writer.write(dpi.getUniqueIonCount()+"\t");
                	if(displayColumns.isShowSpectrumCount())
                		writer.write(dpi.getSpectrumCount()+"("+dpi.getNormalizedSpectrumCountRounded()+")\t");
                }
                
                if(displayColumns.isShowNsaf()) {
                	String nsafStr = nsafStrings[dsIndex++];
                	writer.write(nsafStr.substring(1)+"\t");
                }
            }
            
            
            // print description, if required
            if(includeDescription)
                writer.write(descriptionString.substring(1)+"\n");
            else
                writer.write("\n");
        }
    }
      
}
