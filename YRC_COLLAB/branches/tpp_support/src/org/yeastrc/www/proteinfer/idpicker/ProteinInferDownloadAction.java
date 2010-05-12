package org.yeastrc.www.proteinfer.idpicker;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferPeptideDAO;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.ms.domain.protinfer.ProteinferPeptide;
import org.yeastrc.ms.domain.protinfer.SORT_BY;
import org.yeastrc.ms.domain.protinfer.SORT_ORDER;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerParam;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerRun;
import org.yeastrc.ms.util.StringUtils;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.nrseq.ProteinReference;
import org.yeastrc.www.compare.ProteinDatabaseLookupUtil;
import org.yeastrc.www.protein.ProteinAbundanceDao;
import org.yeastrc.www.protein.ProteinAbundanceDao.YeastOrfAbundance;
import org.yeastrc.www.proteinfer.ProteinInferToSpeciesMapper;
import org.yeastrc.www.util.RoundingUtils;

import edu.uwpr.protinfer.idpicker.IDPickerParams;
import edu.uwpr.protinfer.idpicker.IdPickerParamsMaker;

public class ProteinInferDownloadAction extends Action {

    private static final Logger log = Logger.getLogger(ProteinInferDownloadAction.class.getName());
    
    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {
        
        
        IdPickerFilterForm filterForm = (IdPickerFilterForm)form;
        
        // get the protein inference id
        int pinferId = filterForm.getPinferId();
        
        long s = System.currentTimeMillis();
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition","attachment; filename=\"ProtInfer_"+pinferId+".txt\"");
        response.setHeader("cache-control", "no-cache");
        PrintWriter writer = response.getWriter();
        writer.write("Date: "+new Date()+"\n\n");
        writeResults(writer, pinferId, filterForm);
        writer.close();
        long e = System.currentTimeMillis();
        log.info("DownloadProteinferResultsAction results in: "+TimeUtils.timeElapsedMinutes(s,e)+" minutes");
        return null;
    }

    private void writeFilteringOptions(PrintWriter writer, IdPickerFilterForm filterForm) {
        
        writer.write("Min. Peptides: "+filterForm.getMinPeptides()+"\n");
        writer.write("Max. Peptides: "+filterForm.getMaxPeptides()+"\n");
        writer.write("Min. Unique Peptides: "+filterForm.getMinUniquePeptides()+"\n");
        writer.write("Max. Unique Peptides: "+filterForm.getMaxUniquePeptides()+"\n");
        writer.write("Min. Spectrum Matches: "+filterForm.getMinSpectrumMatches()+"\n");
        writer.write("Max. Spectrum Matches: "+filterForm.getMaxSpectrumMatches()+"\n");
        writer.write("Min. Coverage(%): "+filterForm.getMinCoverage()+"\n");
        writer.write("Max. Coverage(%): "+filterForm.getMaxCoverage()+"\n");
        writer.write("Min. Molecular Wt.: "+filterForm.getMinMolecularWt()+"\n");
        writer.write("Max. Molecular Wt.: "+filterForm.getMaxMolecularWt()+"\n");
        writer.write("Min. pI: "+filterForm.getMinPi()+"\n");
        writer.write("Max. pI: "+filterForm.getMaxPi()+"\n");
        writer.write("Show Non-parsimonious Proteins: "+filterForm.isShowAllProteins()+"\n");
        writer.write("Exclude Indistinguishable Groups: "+filterForm.isExcludeIndistinProteinGroups()+"\n");
        writer.write("Validation Status: "+filterForm.getValidationStatusString()+"\n");
        writer.write("Include proteins with peptide charge states: "+filterForm.getChargeStatesString()+"\n");
        writer.write("Fasta ID filter: "+filterForm.getAccessionLike()+"\n");
        writer.write("Description filter (Like): "+filterForm.getDescriptionLike()+"\n");
        writer.write("Description filter (Not Like): "+filterForm.getDescriptionNotLike()+"\n");
        writer.write("Search Swiss-Prot and NCBI-NR: "+filterForm.isSearchAllDescriptions()+"\n");
    }

    private void writeResults(PrintWriter writer, int pinferId, IdPickerFilterForm filterForm) {
        
        
        IdPickerRun idpRun = ProteinferDAOFactory.instance().getIdPickerRunDao().loadProteinferRun(pinferId);
        IDPickerParams idpParams = IdPickerParamsMaker.makeIdPickerParams(idpRun.getParams());
        PeptideDefinition peptideDef = idpParams.getPeptideDefinition();
        
        // Get the filtering criteria
        ProteinFilterCriteria filterCriteria = filterForm.getFilterCriteria(peptideDef);
        filterCriteria.setSortBy(SORT_BY.GROUP_ID);
        filterCriteria.setSortOrder(SORT_ORDER.ASC);
        
        // Get the protein Ids that fulfill the criteria.
        List<Integer> proteinIds = IdPickerResultsLoader.getProteinIds(pinferId, filterCriteria);
        
        // print the parameters used for the protein inference run
        writer.write("Program Version: "+idpRun.getProgramVersion()+"\n");
        writer.write("Parameters used for Protein Inference ID: "+idpRun.getId()+"\n");
        ProteinInferenceProgram program = idpRun.getProgram();
        for(IdPickerParam param: idpRun.getSortedParams()) {
            writer.write(program.getDisplayNameForParam(param.getName())+": "+param.getValue()+"\n");
        }
        writer.write("\n\n");
        
        // print the filtering options being used
        writer.write("Filtering Options: \n");
        writeFilteringOptions(writer, filterForm);
        writer.write("\n\n");
        
        
        // print summary
        WIdPickerResultSummary summary = IdPickerResultsLoader.getIdPickerResultSummary(pinferId, proteinIds);
        writer.write("Filtered Proteins:\t"+summary.getFilteredProteinCount()+"\n");
        writer.write("Filtered Protein Groups:\t"+summary.getFilteredProteinGroupCount()+"\n");
        writer.write("Parsimonious Proteins:\t"+summary.getFilteredParsimoniousProteinCount()+"\n");
        writer.write("Parsimonious Protein Groups:\t"+summary.getFilteredParsimoniousProteinGroupCount()+"\n");
        writer.write("\n\n");
        
        
        // print input summary
        List<WIdPickerInputSummary> inputSummary = IdPickerResultsLoader.getIDPickerInputSummary(pinferId);
        writer.write("File\tNumHits\tNumFilteredHits\t%Filtered\n");
        int totalTargetHits = 0;
        int filteredTargetHits = 0;
        for(WIdPickerInputSummary input: inputSummary) {
            writer.write(input.getFileName()+"\t");
            writer.write(input.getNumHits()+"\t");
            writer.write(input.getNumFilteredHits()+"\t");
            writer.write(input.getPercentFilteredHits()+"%\n");
            
            totalTargetHits += input.getInput().getNumTargetHits();
            filteredTargetHits += input.getInput().getNumFilteredTargetHits();
        }
        writer.write("TOTAL\t");
        writer.write(totalTargetHits+"\t");
        writer.write(filteredTargetHits+"\t");
        writer.write(RoundingUtils.getInstance().roundTwo((filteredTargetHits*100.0)/(double)totalTargetHits)+"%\n");
        writer.write("\n\n");
        
        
        if(!filterForm.isCollapseGroups()) {
            // print each protein
            printIndividualProteins(writer, pinferId, peptideDef, proteinIds, filterForm.isPrintPeptides(), filterForm.isPrintDescriptions());
        }
        else {
            // user wants to see only one line for each protein group; all members of the group will be displayed comma-separated
            printCollapsedProteinGroups(writer, pinferId, peptideDef, proteinIds, filterForm.isPrintPeptides(), filterForm.isPrintDescriptions());
        }
    }

    private void printIndividualProteins(PrintWriter writer, int pinferId,
            PeptideDefinition peptideDef, List<Integer> proteinIds, boolean printPeptides, boolean printDescriptions) {
    	
    	boolean isYeast = ProteinInferToSpeciesMapper.isSpeciesYeast(pinferId);
    	
        writer.write("ProteinGroupID\t");
        writer.write("Parsimonious\t");
        writer.write("FastaID\tCommonName\t");
        writer.write("Coverage\tNumSpectra\tNSAF\t");
        if(isYeast) {
        	writer.write("#Copies/Cell\t");
        }
        writer.write("NumPeptides\tNumUniquePeptides\t");
        writer.write("Mol.Wt\tpI");
        if(printPeptides)
            writer.write("\tPeptides");
        if(printDescriptions)
            writer.write("\tDescription");
        writer.write("\n");

        // Only for yeast
        ProteinAbundanceDao adundanceDao = ProteinAbundanceDao.getInstance();
        
        List<Integer> fastaDatabaseIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
        for(int i = 0; i < proteinIds.size(); i++) {
            int proteinId = proteinIds.get(i);
            WIdPickerProtein wProt = IdPickerResultsLoader.getIdPickerProtein(proteinId, peptideDef, fastaDatabaseIds);
            writer.write(wProt.getProtein().getGroupId()+"\t");
            if(wProt.getProtein().getIsParsimonious())
                writer.write("P\t");
            else
                writer.write("\t");
            try {
				writer.write(wProt.getAccessionsCommaSeparated()+"\t");
			} catch (SQLException e) {
				log.error("Error getting accessions", e);
				writer.write("ERROR\t");
			}
            try {
				writer.write(wProt.getCommonNamesCommaSeparated()+"\t");
			} catch (SQLException e) {
				log.error("Error getting common names", e);
				writer.write("ERROR\t");
			}
            writer.write(wProt.getProtein().getCoverage()+"\t");
            writer.write(wProt.getProtein().getSpectrumCount()+"\t");
            writer.write(wProt.getProtein().getNsafFormatted()+"\t");
            
            if(isYeast) {
            	try {
					List<YeastOrfAbundance> abundances = adundanceDao.getAbundance(wProt.getProtein().getNrseqProteinId());
					if(abundances == null || abundances.size() == 0) {
						writer.write("UNKNOWN\t");
					}
					else if(abundances.size() == 1) {
						writer.write(abundances.get(0).getAbundanceToPrint()+"\t");
					}
					else {
						
						boolean allUnknown = true;
				    	for(YeastOrfAbundance oa: abundances) {
				    		if(!oa.isAbundanceNull()) {
				    			allUnknown = false;
				    			break;
				    		}
				    	}
				    	if(allUnknown) {
				    		writer.write("UNKNOWN\t");
				    	}
				    	else {
				    		List<String> toPrint = new ArrayList<String>(abundances.size());
				    		for(YeastOrfAbundance abundance: abundances) {
				    			toPrint.add(abundance.getOrfName()+":"+abundance.getAbundanceToPrint());
				    		}
				    		writer.write(StringUtils.makeCommaSeparated(toPrint)+"\t");
				    	}
					}
				} catch (SQLException e) {
					log.error("Exception getting protein copies / cell for protein: "+wProt.getProtein().getNrseqProteinId(), e);
					writer.write("ERROR\t");
				}
            }
            writer.write(wProt.getProtein().getPeptideCount()+"\t");
            writer.write(wProt.getProtein().getUniquePeptideCount()+"\t");
            writer.write(wProt.getMolecularWeight()+"\t");
            writer.write(wProt.getPi()+"");
            
            if(printPeptides) {
                writer.write("\t"+getPeptides(proteinId));
            }
            if(printDescriptions) {
            	try {
					ProteinReference ref = wProt.getOneDescriptionReference();
					if(ref != null)
						writer.write("\t"+wProt.getOneDescriptionReference().getDescription());
				} catch (SQLException e) {
					log.error("Error getting description", e);
					writer.write("\tERROR");
				}
            }
            writer.write("\n");
        }
    }

    private String getPeptides(int proteinId) {
        ProteinferPeptideDAO peptDao = ProteinferDAOFactory.instance().getProteinferPeptideDao();
        List<ProteinferPeptide> peptides = peptDao.loadPeptidesForProteinferProtein(proteinId);
        StringBuilder buf = new StringBuilder();
        for(ProteinferPeptide pept: peptides) 
            buf.append(","+pept.getSequence());
        if(buf.length() > 0)
            buf.deleteCharAt(0); // remove first comma
        return buf.toString();
    }

    private void printCollapsedProteinGroups(PrintWriter writer, int pinferId,
            PeptideDefinition peptideDef, List<Integer> proteinIds, boolean printPeptides, boolean printDescriptions) {
        writer.write("ProteinGroupID\t");
        writer.write("Parsimonious\t");
        writer.write("FastaID\tCommonName\t");
        writer.write("Coverage\tNumSpectra\tNSAF\t");
        writer.write("NumPeptides\tNumUniquePeptides\t");
        writer.write("Mol.Wt\tpI");
        if(printPeptides)
            writer.write("\tPeptides");
        writer.write("\n");
        
        int currentGroupId = -1;
        boolean parsimonious = false;
        String fastaIds = "";
        String commonNames = "";
        String descStr = "";
        String coverageStr = "";
        String NsafStr = "";
        String molWtStr = "";
        String piStr = "";
        String peptides = "";
        int spectrumCount = 0;
        int numPept = 0;
        int numUniqPept = 0;
        
        List<Integer> fastaDatabaseIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
        
        for(int i = 0; i < proteinIds.size();  i++) {
            int proteinId = proteinIds.get(i);
            WIdPickerProtein wProt = IdPickerResultsLoader.getIdPickerProtein(proteinId, peptideDef, fastaDatabaseIds);
            if(wProt.getProtein().getGroupId() != currentGroupId) {
                if(currentGroupId != -1) {
                    writer.write(currentGroupId+"\t");
                    if(parsimonious)
                        writer.write("P\t");
                    else
                        writer.write("\t");
                    if(fastaIds.length() > 0)
                        fastaIds = fastaIds.substring(1); // remove first comma
                    writer.write(fastaIds+"\t");
                    // Common names
                    if(commonNames.length() > 0)
                        commonNames = commonNames.substring(1);
                    writer.write(commonNames+"\t");
                    
                    // Coverages
                    if(coverageStr.length() > 0)
                        coverageStr = coverageStr.substring(1);
                    writer.write(coverageStr+"\t");
                    
                    writer.write(spectrumCount+"\t");
                    
                    // NSAFs
                    if(NsafStr.length() > 0)
                        NsafStr = NsafStr.substring(1);
                    writer.write(NsafStr+"\t");
                    
                    writer.write(numPept+"\t");
                    writer.write(numUniqPept+"\t");
                    
                    // Molecular weights
                    if(molWtStr.length() > 0)
                        molWtStr = molWtStr.substring(1);
                    writer.write(molWtStr+"\t");
                    
                    // pIs
                    if(piStr.length() > 0)
                        piStr = piStr.substring(1);
                    writer.write(piStr+"");
                    
                    if(printPeptides)
                        writer.write("\t"+peptides);
                    
                    if(printDescriptions) {
                        if(descStr.length() > 0)
                            descStr = descStr.substring(1);
                        writer.write("\t"+descStr);
                    }
                    
                    writer.write("\n");
                }
                fastaIds = "";
                peptides = "";
                commonNames = "";
                descStr = "";
                coverageStr = "";
                NsafStr = "";
                molWtStr = "";
                piStr = "";
                currentGroupId = wProt.getProtein().getGroupId();
                parsimonious = wProt.getProtein().getIsParsimonious();
                spectrumCount = wProt.getProtein().getSpectrumCount();
                numPept = wProt.getProtein().getPeptideCount();
                numUniqPept = wProt.getProtein().getUniquePeptideCount();
                if(printPeptides) {
                    peptides = getPeptides(proteinId);
                }
            }
            try {
				fastaIds += ";"+wProt.getAccessionsCommaSeparated();
			} catch (SQLException e) {
				log.error("Error getting accessions", e);
				fastaIds += ",ERROR";
			}
			try {
				String cn = wProt.getCommonNamesCommaSeparated();
				if(cn.trim().length() > 0)
					commonNames += ";"+cn;
			} catch (SQLException e) {
				log.error("Error getting common names", e);
				fastaIds += ",ERROR";
			}
			try {
				ProteinReference ref = wProt.getOneDescriptionReference();
				if(ref != null)
					descStr += ", "+wProt.getOneDescriptionReference().getDescription();
			} catch (SQLException e) {
				log.error("Error getting description", e);
				descStr += ", ERROR";
			}
			coverageStr += ","+wProt.getProtein().getCoverage()+"%";
            NsafStr += ","+wProt.getProtein().getNsafFormatted();
            molWtStr += ","+wProt.getMolecularWeight();
            piStr += ","+wProt.getPi();
        }
        // write the last one
        writer.write(currentGroupId+"\t");
        if(parsimonious)
            writer.write("P\t");
        else
            writer.write("\t");
        // Fasta IDs
        if(fastaIds.length() > 0)
            fastaIds = fastaIds.substring(1); // remove first comma
        writer.write(fastaIds+"\t");
        
        // Common names
        if(commonNames.length() > 0)
            commonNames = commonNames.substring(1);
        writer.write(commonNames+"\t");
        
        // Coverages
        if(coverageStr.length() > 0)
            coverageStr = coverageStr.substring(1);
        writer.write(coverageStr+"\t");
        
        writer.write(spectrumCount+"\t");
        
        // NSAFs
        if(NsafStr.length() > 0)
            NsafStr = NsafStr.substring(1);
        writer.write(NsafStr+"\t");
        
        writer.write(numPept+"\t");
        writer.write(numUniqPept+"\t");
        
        // Molecular weights
        if(molWtStr.length() > 0)
            molWtStr = molWtStr.substring(1);
        writer.write(molWtStr+"\t");
        
        // pIs
        if(piStr.length() > 0)
            piStr = piStr.substring(1);
        writer.write(piStr+"");
        
        if(printPeptides)
            writer.write("\t"+peptides);
        
        if(printDescriptions)
            writer.write("\t"+descStr);
        
        writer.write("\n");
    }
}
