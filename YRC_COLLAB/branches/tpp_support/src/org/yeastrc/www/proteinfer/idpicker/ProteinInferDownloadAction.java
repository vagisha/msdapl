package org.yeastrc.www.proteinfer.idpicker;

import java.io.PrintWriter;
import java.sql.SQLException;
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
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerParam;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerRun;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.www.compare.ProteinDatabaseLookupUtil;

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
        writer.write("File\tNumHits\tNumFilteredHits\n");
        int totalDecoyHits = 0;
        int totalTargetHits = 0;
        int filteredTargetHits = 0;
        for(WIdPickerInputSummary input: inputSummary) {
            writer.write(input.getFileName()+"\t");
//            writer.write(input.getInput().getNumDecoyHits()+"\t");
            writer.write(input.getInput().getNumTargetHits()+"\t");
            writer.write(input.getInput().getNumFilteredTargetHits()+"\n");
            
//            totalDecoyHits += input.getInput().getNumDecoyHits();
            totalTargetHits += input.getInput().getNumTargetHits();
            filteredTargetHits += input.getInput().getNumFilteredTargetHits();
        }
        writer.write("TOTAL\t");
//        writer.write(totalDecoyHits+"\t");
        writer.write(totalTargetHits+"\t");
        writer.write(filteredTargetHits+"\n");
        writer.write("\n\n");
        
        
        if(!filterForm.isCollapseGroups()) {
            // print each protein
            printIndividualProteins(writer, pinferId, peptideDef, proteinIds, filterForm.isPrintPeptides());
        }
        else {
            // user wants to see only one line for each protein group; all members of the group will be displayed comma-separated
            printCollapsedProteinGroups(writer, pinferId, peptideDef, proteinIds, filterForm.isPrintPeptides());
        }
    }

    private void printIndividualProteins(PrintWriter writer, int pinferId,
            PeptideDefinition peptideDef, List<Integer> proteinIds, boolean printPeptides) {
        writer.write("ProteinGroupID\t");
        writer.write("Parsimonious\t");
        writer.write("FastaID\tCommonName\t");
        writer.write("Coverage\tNumSpectra\tNSAF\t");
        writer.write("NumPeptides\tNumUniquePeptides");
        if(printPeptides)
            writer.write("\tPeptides");
//            writer.write("Description\n");
        writer.write("\n");

        List<Integer> fastaDatabaseIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
        for(int i = proteinIds.size() - 1; i >=0; i--) {
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
				writer.write("ERROR");
			}
            try {
				writer.write(wProt.getCommonNamesCommaSeparated()+"\t");
			} catch (SQLException e) {
				log.error("Error getting common names", e);
				writer.write("ERROR");
			}
            writer.write(wProt.getProtein().getCoverage()+"\t");
            writer.write(wProt.getProtein().getSpectrumCount()+"\t");
            writer.write(wProt.getProtein().getNsafFormatted()+"\t");
            writer.write(wProt.getProtein().getPeptideCount()+"\t");
            writer.write(wProt.getProtein().getUniquePeptideCount()+"");
            
            if(printPeptides) {
                writer.write("\t"+getPeptides(proteinId));
            }
//                writer.write(wProt.getDescription()+"\n");
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
            PeptideDefinition peptideDef, List<Integer> proteinIds, boolean printPeptides) {
        writer.write("ProteinGroupID\t");
        writer.write("Parsimonious\t");
        writer.write("FastaID(s)\t");
        writer.write("NumSpectra\t");
        writer.write("NumPeptides\tNumUniquePeptides");
        if(printPeptides)
            writer.write("\tPeptides");
        writer.write("\n");
        
        int currentGroupId = -1;
        boolean parsimonious = false;
        String fastaIds = "";
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
                    writer.write(spectrumCount+"\t");
                    writer.write(numPept+"\t");
                    writer.write(numUniqPept+"");
                    if(printPeptides)
                        writer.write("\t"+peptides);
                    writer.write("\n");
                }
                fastaIds = "";
                peptides = "";
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
				fastaIds += ","+wProt.getAccessionsCommaSeparated();
			} catch (SQLException e) {
				log.error("Error getting accessions", e);
				fastaIds += ",ERROR";
			}
        }
        // write the last one
        writer.write(currentGroupId+"\t");
        if(parsimonious)
            writer.write("P\t");
        else
            writer.write("\t");
        if(fastaIds.length() > 0)
            fastaIds = fastaIds.substring(1); // remove first comma
        writer.write(fastaIds+"\t");
        writer.write(spectrumCount+"\t");
        writer.write(numPept+"\t");
        writer.write(numUniqPept+"");
        if(printPeptides)
            writer.write("\t"+peptides);
        writer.write("\n");
    }
}
