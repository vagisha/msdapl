package org.yeastrc.www.proteinfer;

import java.io.PrintWriter;
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
import org.yeastrc.www.proteinfer.idpicker.IdPickerResultsLoader;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerInputSummary;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerProtein;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerResultSummary;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import edu.uwpr.protinfer.PeptideDefinition;
import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dto.ProteinFilterCriteria;
import edu.uwpr.protinfer.database.dto.ProteinFilterCriteria.SORT_BY;
import edu.uwpr.protinfer.database.dto.ProteinFilterCriteria.SORT_ORDER;
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
        response.setHeader("Content-Disposition","inline; filename=\"ProtInfer_"+pinferId+".txt\"");
        response.setHeader("cache-control", "no-cache");
        PrintWriter writer = response.getWriter();
        writeResults(writer, pinferId, filterForm);
        writer.close();
        long e = System.currentTimeMillis();
        log.info("DownloadProteinferResultsAction results in: "+TimeUtils.timeElapsedMinutes(s,e)+" minutes");
        return null;
    }

    private void writeResults(PrintWriter writer, int pinferId, ProteinInferFilterForm filterForm) {
        
        IdPickerRun idpRun = ProteinferDAOFactory.instance().getIdPickerRunDao().loadProteinferRun(pinferId);
        IDPickerParams idpParams = IdPickerParamsMaker.makeIdPickerParams(idpRun.getParams());
        PeptideDefinition peptideDef = idpParams.getPeptideDefinition();
        
        // Get the filtering criteria
        ProteinFilterCriteria filterCriteria = new ProteinFilterCriteria();
        filterCriteria.setCoverage(filterForm.getMinCoverage());
        filterCriteria.setNumPeptides(filterForm.getMinPeptides());
        filterCriteria.setNumUniquePeptides(filterForm.getMinUniquePeptides());
        filterCriteria.setNumSpectra(filterForm.getMinSpectrumMatches());
        filterCriteria.setPeptideDefinition(peptideDef);
        filterCriteria.setSortBy(SORT_BY.defaultSortBy());
        filterCriteria.setSortOrder(SORT_ORDER.defaultSortOrder());
        filterCriteria.setGroupProteins(true);
        filterCriteria.setShowParsimonious(!filterForm.isShowAllProteins());
        filterCriteria.setValidationStatus(filterForm.getValidationStatus());
        filterCriteria.setAccessionLike(filterForm.getAccessionLike());
        filterCriteria.setDescriptionLike(filterForm.getDescriptionLike());
        
        // Get the protein Ids that fulfill the criteria.
        List<Integer> proteinIds = IdPickerResultsLoader.getProteinIds(pinferId, filterCriteria);
        
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
        

        // print each protein
        writer.write("ProteinGroupID\t");
        writer.write("Parsimonious\t");
        writer.write("FastaID\tCommonName\t");
        writer.write("Coverage\tNumSpectra\tNSAF\t");
        writer.write("NumPeptides\tNumUniquePeptides\t");
        writer.write("Description\n");
        
        for(int i = proteinIds.size() - 1; i >=0; i--) {
            int proteinId = proteinIds.get(i);
            WIdPickerProtein wProt = IdPickerResultsLoader.getIdPickerProtein(pinferId, proteinId, peptideDef);
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
            writer.write(wProt.getDescription()+"\n");
        }
    }
}
