package org.yeastrc.www.proteinfer;

import java.io.PrintWriter;
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
import org.yeastrc.www.compare.ProteinDatabaseLookupUtil;
import org.yeastrc.www.proteinfer.idpicker.IdPickerResultsLoader;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerInputSummary;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerProtein;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerResultSummary;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import edu.uwpr.protinfer.PeptideDefinition;
import edu.uwpr.protinfer.ProteinInferenceProgram;
import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferPeptideDAO;
import edu.uwpr.protinfer.database.dto.ProteinFilterCriteria;
import edu.uwpr.protinfer.database.dto.ProteinferPeptide;
import edu.uwpr.protinfer.database.dto.ProteinFilterCriteria.SORT_BY;
import edu.uwpr.protinfer.database.dto.ProteinFilterCriteria.SORT_ORDER;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerParam;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerRun;
import edu.uwpr.protinfer.idpicker.IDPickerParams;
import edu.uwpr.protinfer.idpicker.IdPickerParamsMaker;
import edu.uwpr.protinfer.util.TimeUtils;

public class DownloadProteinferResultsAction extends Action {

    private static final Logger log = Logger.getLogger(DownloadProteinferResultsAction.class.getName());
    
    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {
        
        // User making this request
        User user = UserUtils.getUser(request);
        if (user == null) {
            ActionErrors errors = new ActionErrors();
            errors.add("username", new ActionMessage("error.login.notloggedin"));
            saveErrors( request, errors );
            return mapping.findForward("authenticate");
        }
        
        ProteinInferFilterForm filterForm = (ProteinInferFilterForm) form;
        // get the protein inference id
        int pinferId = filterForm.getPinferId();
        // if we  do not have a valid protein inference run id
        // return an error.
        if(pinferId <= 0) {
            log.error("Invalid protein inference run id: "+pinferId);
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.proteinfer.invalid.pinferId", pinferId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        
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

    private void writeFilteringOptions(PrintWriter writer, ProteinInferFilterForm filterForm) {
        
        writer.write("Min. Peptides: "+filterForm.getMinPeptides()+"\n");
        writer.write("Max. Peptides: "+filterForm.getMaxPeptides()+"\n");
        writer.write("Min. Unique Peptides: "+filterForm.getMinUniquePeptides()+"\n");
        writer.write("Max. Unique Peptides: "+filterForm.getMaxUniquePeptides()+"\n");
        writer.write("Charge States: "+filterForm.getChargeStatesString()+"\n");
        writer.write("Min. Spectrum Matches: "+filterForm.getMinSpectrumMatches()+"\n");
        writer.write("Max. Spectrum Matches: "+filterForm.getMaxSpectrumMatches()+"\n");
        writer.write("Min. Coverage(%): "+filterForm.getMinCoverage()+"\n");
        writer.write("Max. Coverage(%): "+filterForm.getMaxCoverage()+"\n");
        writer.write("Min. Molecular Wt.: "+filterForm.getMinMolecularWt()+"\n");
        writer.write("Max. Molecular Wt.: "+filterForm.getMaxMolecularWt()+"\n");
        writer.write("Min. PI: "+filterForm.getMinPi()+"\n");
        writer.write("Max. PI: "+filterForm.getMaxPi()+"\n");
        writer.write("Show all Proteins: "+filterForm.isShowAllProteins()+"\n");
        writer.write("Exclude indistinguishable protein groups: "+filterForm.isExcludeIndistinProteinGroups()+"\n");
        writer.write("Validation Status: "+filterForm.getValidationStatusString()+"\n");
        String filterStr = filterForm.getAccessionLike() == null ? "NONE" : filterForm.getAccessionLike();
        writer.write("Fasta ID filter: "+filterStr+"\n");
        filterStr = filterForm.getDescriptionLike() == null ? "NONE" : filterForm.getDescriptionLike();
        writer.write("Description (includes): "+filterStr+"\n");
        filterStr = filterForm.getDescriptionNotLike() == null ? "NONE" : filterForm.getDescriptionNotLike();
        writer.write("Description (excludes): "+filterStr+"\n");
    }

    private void writeResults(PrintWriter writer, int pinferId, ProteinInferFilterForm filterForm) {
        
        
        IdPickerRun idpRun = ProteinferDAOFactory.instance().getIdPickerRunDao().loadProteinferRun(pinferId);
        IDPickerParams idpParams = IdPickerParamsMaker.makeIdPickerParams(idpRun.getParams());
        PeptideDefinition peptideDef = idpParams.getPeptideDefinition();
        
        // Get the filtering criteria
        ProteinFilterCriteria filterCriteria = new ProteinFilterCriteria();
        filterCriteria.setCoverage(filterForm.getMinCoverageDouble());
        filterCriteria.setMaxCoverage(filterForm.getMaxCoverageDouble());
        filterCriteria.setMinMolecularWt(filterForm.getMinMolecularWtDouble());
        filterCriteria.setMaxMolecularWt(filterForm.getMaxMolecularWtDouble());
        filterCriteria.setMinPi(filterForm.getMinPiDouble());
        filterCriteria.setMaxPi(filterForm.getMaxPiDouble());
        filterCriteria.setNumPeptides(filterForm.getMinPeptidesInteger());
        filterCriteria.setNumMaxPeptides(filterForm.getMaxPeptidesInteger());
        filterCriteria.setNumUniquePeptides(filterForm.getMinUniquePeptidesInteger());
        filterCriteria.setNumMaxUniquePeptides(filterForm.getMaxUniquePeptidesInteger());
        filterCriteria.setNumSpectra(filterForm.getMinSpectrumMatchesInteger());
        filterCriteria.setNumMaxSpectra(filterForm.getMaxSpectrumMatchesInteger());
        filterCriteria.setPeptideDefinition(peptideDef);
        if(filterForm.isCollapseGroups()) 
            filterCriteria.setSortBy(SORT_BY.GROUP_ID);
        else
            filterCriteria.setSortBy(SORT_BY.defaultSortBy());
        filterCriteria.setSortOrder(SORT_ORDER.defaultSortOrder());
        filterCriteria.setGroupProteins(true);
        filterCriteria.setExcludeIndistinGroups(filterForm.isExcludeIndistinProteinGroups());
        filterCriteria.setShowParsimonious(!filterForm.isShowAllProteins());
        filterCriteria.setValidationStatus(filterForm.getValidationStatus());
        filterCriteria.setChargeStates(filterForm.getChargeStateList());
        filterCriteria.setChargeGreaterThan(filterForm.getChargeGreaterThan());
        filterCriteria.setAccessionLike(filterForm.getAccessionLike());
        filterCriteria.setDescriptionLike(filterForm.getDescriptionLike());
        filterCriteria.setDescriptionNotLike(filterForm.getDescriptionNotLike());
        filterCriteria.setPeptide(filterForm.getPeptide());
        filterCriteria.setExactPeptideMatch(filterForm.getExactPeptideMatch());
        
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
            printIndividualProteins(writer, pinferId, peptideDef, proteinIds, filterForm.isPrintPeptides(), filterForm.isPrintDescriptions());
        }
        else {
            // user wants to see only one line for each protein group; all members of the group will be displayed comma-separated
            printCollapsedProteinGroups(writer, pinferId, peptideDef, proteinIds, filterForm.isPrintPeptides(), filterForm.isPrintDescriptions());
        }
    }

    private void printIndividualProteins(PrintWriter writer, int pinferId,
            PeptideDefinition peptideDef, List<Integer> proteinIds, boolean printPeptides, boolean printDescriptions) {
        writer.write("ProteinGroupID\t");
        writer.write("Parsimonious\t");
        writer.write("FastaID\tCommonName\t");
        writer.write("Coverage\tNumSpectra\tNSAF\t");
        writer.write("NumPeptides\tNumUniquePeptides\t");
        writer.write("Mol.Wt\tpI");
        if(printPeptides)
            writer.write("\tPeptides");
        if(printDescriptions)
            writer.write("\tDescription");
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
            writer.write(wProt.getAccession()+"\t");
            writer.write(wProt.getCommonName()+"\t");
            writer.write(wProt.getProtein().getCoverage()+"\t");
            writer.write(wProt.getProtein().getSpectrumCount()+"\t");
            writer.write(wProt.getProtein().getNsafFormatted()+"\t");
            writer.write(wProt.getProtein().getPeptideCount()+"\t");
            writer.write(wProt.getProtein().getUniquePeptideCount()+"\t");
            writer.write(wProt.getMolecularWeight()+"\t");
            writer.write(wProt.getPi()+"");
            
            if(printPeptides) {
                writer.write("\t"+getPeptides(proteinId));
            }
            if(printDescriptions)
                writer.write("\t"+wProt.getDescription());
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
            PeptideDefinition peptideDef, List<Integer> proteinIds, boolean printPeptides,
            boolean printDescriptions) {
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
            fastaIds += ","+wProt.getAccession();
            commonNames += ","+wProt.getCommonName();
            descStr += ", "+wProt.getDescription();
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
