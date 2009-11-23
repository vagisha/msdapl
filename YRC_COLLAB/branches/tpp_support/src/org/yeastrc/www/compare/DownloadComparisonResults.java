/**
 * DownloadComparisonResults.java
 * @author Vagisha Sharma
 * May 4, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.io.PrintWriter;
import java.util.Date;

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
        
        ProteinDatasetComparisonFilters filters = myForm.getSelectedBooleanFilters();
        
        if(!myForm.getGroupIndistinguishableProteins()) {
            ProteinComparisonDataset comparison = (ProteinComparisonDataset) request.getAttribute("comparisonDataset");
            if(comparison == null) {
                ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Comparison dataset not found in request"));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
            }
            
            long s = System.currentTimeMillis();
            writeResults(writer, comparison, filters, myForm);
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
            
            long s = System.currentTimeMillis();
            writeResults(writer, grpComparison, filters, myForm);
            writer.close();
            long e = System.currentTimeMillis();
            log.info("Results written in: "+TimeUtils.timeElapsedMinutes(s,e)+" minutes");
        }
        
        
        long e = System.currentTimeMillis();
        log.info("DownloadComparisonResults results in: "+TimeUtils.timeElapsedMinutes(startTime,e)+" minutes");
        return null;
    }

    
    private void writeResults(PrintWriter writer, ProteinComparisonDataset comparison, ProteinDatasetComparisonFilters filters,
            ProteinSetComparisonForm form) {
        
        writer.write("Total protein count: "+comparison.getTotalProteinCount()+"\n");
        writer.write("Filtered protein count: "+comparison.getFilteredProteinCount()+"\n");
        writer.write("\n\n");
        
        // Boolean Filters
        writeFilters(writer, filters);
        
        
        // Accession string filter
        String searchString = form.getAccessionLike();
        if(searchString != null && searchString.trim().length() > 0) {
            writer.write("Filtering for FASTA ID(s): "+searchString+"\n\n");
        }
        
        // Description string filter
//        String descString = form.getDescriptionSearchString();
//        if(descString != null && descString.trim().length() > 0) {
//            writer.write("Filtering for description term(s): "+descString+"\n\n");
//        }
        
        
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
        // *  Present and Parsimonious
        // =  Present and NOT parsimonious
        // g  group protein
        // -  NOT present
        writer.write("\n\n");
        writer.write("*  Protein present and parsimonious\n");
        writer.write("=  Protein present and NOT parsimonious\n");
        writer.write("-  Protein NOT present\n");
        writer.write("g  Group protein\n");
        writer.write("\n\n");
        

        // print each protein
        writer.write("ProteinID\t");
        writer.write("Name\t");
        writer.write("CommonName\t");
//        writer.write("Mol.Wt.\tpI\t");
        writer.write("NumPept\t");
        for(Dataset dataset: comparison.getDatasets()) {
            writer.write(dataset.getSourceString()+"("+dataset.getDatasetId()+")\t");
        }
        // spectrum count column headers.
        for(Dataset dataset: comparison.getDatasets()) {
            writer.write("SC("+dataset.getDatasetId()+")\t");
        }
        // NSAF column headers.
//        for(Dataset dataset: comparison.getDatasets()) {
//            writer.write("NSAF("+dataset.getDatasetId()+")\t");
//        }
        writer.write("Description\n");
        
        for(ComparisonProtein protein: comparison.getProteins()) {
            
            comparison.initializeProteinInfo(protein);
            
            writer.write(protein.getNrseqId()+"\t");
            writer.write(protein.getFastaName()+"\t");
            writer.write(protein.getCommonName()+"\t");
//            writer.write(protein.getMolecularWeight()+"\t");
//            writer.write(protein.getPi()+"\t");
            writer.write(protein.getMaxPeptideCount()+"\t");
           
            for(Dataset dataset: comparison.getDatasets()) {
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
            // spectrum count information
            for(Dataset dataset: comparison.getDatasets()) {
                
                DatasetProteinInformation dpi = protein.getDatasetProteinInformation(dataset);
                if(dpi == null || !dpi.isPresent()) {
                    writer.write("0\t");
                }
                else {
                    writer.write(dpi.getSpectrumCount()+"("+dpi.getNormalizedSpectrumCountRounded()+")\t");
                }
            }
            // NSAF information
//            for(Dataset dataset: comparison.getDatasets()) {
//                
//                DatasetProteinInformation dpi = protein.getDatasetProteinInformation(dataset);
//                if(dpi == null || !dpi.isPresent()) {
//                    writer.write("-1.0\t");
//                }
//                else {
//                    writer.write(dpi.getNsafFormatted()+"\t");
//                }
//            }
            writer.write(protein.getDescription()+"\n");
        }
        
        writer.write("\n\n");
    }


    private void writeFilters(PrintWriter writer,
            ProteinDatasetComparisonFilters filters) {
        
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
    
    private void writeResults(PrintWriter writer, ProteinGroupComparisonDataset comparison, ProteinDatasetComparisonFilters filters,
            ProteinSetComparisonForm form) {
        
      writer.write("Total Protein Groups (Total Proteins): "+comparison.getTotalProteinGroupCount()+" ("+comparison.getTotalProteinCount()+")\n");
      writer.write("\n\n");
      
      // Boolean Filters
      writeFilters(writer, filters);
      
      
      // Accession string filter
      String searchString = form.getAccessionLike();
      if(searchString != null && searchString.trim().length() > 0) {
          writer.write("Filtering for FASTA ID(s): "+searchString+"\n\n");
      }
      
      // Description string filter
//      String descString = form.getDescriptionSearchString();
//      if(descString != null && descString.trim().length() > 0) {
//          writer.write("Filtering for description term(s): "+descString+"\n\n");
//      }
      
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
      // *  Present and Parsimonious
      // =  Present and NOT parsimonious
      // g  group protein
      // -  NOT present
      writer.write("\n\n");
      writer.write("*  Protein present and parsimonious\n");
      writer.write("=  Protein present and NOT parsimonious\n");
      writer.write("-  Protein NOT present\n");
      writer.write("g  Group protein\n");
      writer.write("\n\n");
      

      // print the proteins in each protein group
      writer.write("ProteinID\t");
      writer.write("ProteinGroupID\t");
      writer.write("Name\t");
      writer.write("CommonName\t");
//      writer.write("Mol.Wt.\tpI\t");
      writer.write("NumPept\t");
      for(Dataset dataset: comparison.getDatasets()) {
          writer.write(dataset.getSourceString()+"("+dataset.getDatasetId()+")\t");
      }
      // spectrum count column headers.
      for(Dataset dataset: comparison.getDatasets()) {
          writer.write("SC("+dataset.getDatasetId()+")\t");
      }
      // NSAF column headers.
//      for(Dataset dataset: comparison.getDatasets()) {
//          writer.write("NSAF("+dataset.getDatasetId()+")\t");
//      }
      writer.write("Description\n");
      
      for(ComparisonProteinGroup grpProtein: comparison.getProteinsGroups()) {
          
          for(ComparisonProtein protein: grpProtein.getProteins()) {
              comparison.initializeProteinInfo(protein);
          
              writer.write(protein.getNrseqId()+"\t");
              writer.write(protein.getGroupId()+"\t");
              writer.write(protein.getFastaName()+"\t");
              writer.write(protein.getCommonName()+"\t");
//              writer.write(protein.getMolecularWeight()+"\t");
//              writer.write(protein.getPi()+"\t");
              writer.write(protein.getMaxPeptideCount()+"\t");
         
              for(Dataset dataset: comparison.getDatasets()) {
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
              // spectrum count information
              for(Dataset dataset: comparison.getDatasets()) {

                  DatasetProteinInformation dpi = protein.getDatasetProteinInformation(dataset);
                  if(dpi == null || !dpi.isPresent()) {
                      writer.write("0\t");
                  }
                  else {
                      writer.write(dpi.getSpectrumCount()+"("+dpi.getNormalizedSpectrumCountRounded()+")\t");
                  }
              }
              // NSAF information
//              for(Dataset dataset: comparison.getDatasets()) {
//                  
//                  DatasetProteinInformation dpi = protein.getDatasetProteinInformation(dataset);
//                  if(dpi == null || !dpi.isPresent()) {
//                      writer.write("-1.0\t");
//                  }
//                  else {
//                      writer.write(dpi.getNsafFormatted()+"\t");
//                  }
//              }
          
              writer.write(protein.getDescription()+"\n");
          }
      }
      
      writer.write("\n\n");
  }
    
}
