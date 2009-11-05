package org.yeastrc.www.proteinfer;
import java.util.ArrayList;
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
import org.yeastrc.bio.go.GOUtils;
import org.yeastrc.bio.taxonomy.Species;
import org.yeastrc.www.go.GOEnrichmentCalculator;
import org.yeastrc.www.go.GOEnrichmentInput;
import org.yeastrc.www.go.GOEnrichmentOutput;
import org.yeastrc.www.go.GOEnrichmentTabular;
import org.yeastrc.www.proteinfer.idpicker.IdPickerResultsLoader;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import edu.uwpr.protinfer.PeptideDefinition;
import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferProteinDAO;
import edu.uwpr.protinfer.database.dto.ProteinFilterCriteria;
import edu.uwpr.protinfer.database.dto.ProteinferProtein;
import edu.uwpr.protinfer.database.dto.ProteinFilterCriteria.SORT_BY;
import edu.uwpr.protinfer.database.dto.ProteinFilterCriteria.SORT_ORDER;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerRun;
import edu.uwpr.protinfer.idpicker.IDPickerParams;
import edu.uwpr.protinfer.idpicker.IdPickerParamsMaker;
import edu.uwpr.protinfer.util.TimeUtils;

/**
 * ProteinInferGOEnrichmentAction.java
 * @author Vagisha Sharma
 * Jun 10, 2009
 * @version 1.0
 */

/**
 * 
 */
public class ProteinInferGOEnrichmentAction extends Action {

    private static final Logger log = Logger.getLogger(ProteinInferGOEnrichmentAction.class.getName());
    
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
        
        int speciesId = filterForm.getSpeciesId();
        GOEnrichmentOutput enrichment = doGoEnrichmentAnalysis(pinferId, speciesId, filterForm);
        // Biological Process
        if(filterForm.getGoAspect() == GOUtils.BIOLOGICAL_PROCESS) {
            GOEnrichmentTabular bpTabular = new GOEnrichmentTabular();
            bpTabular.setEnrichedTerms(enrichment.getBiologicalProcessEnriched());
            bpTabular.setTitle("Biological Process");
            bpTabular.setNumProteinsInUniverse(enrichment.getTotalAnnotatedBiologicalProcess());
            bpTabular.setNumProteinsInSet(enrichment.getNumSpeciesProteins());
            request.setAttribute("bioProcessTerms", bpTabular);
        }
        
        // Cellular Component
        if(filterForm.getGoAspect() == GOUtils.CELLULAR_COMPONENT) {
            GOEnrichmentTabular ccTabular = new GOEnrichmentTabular();
            ccTabular.setEnrichedTerms(enrichment.getCellularComponentEnriched());
            ccTabular.setTitle("Cellular Component");
            ccTabular.setNumProteinsInUniverse(enrichment.getTotalAnnotatedCellularComponent());
            ccTabular.setNumProteinsInSet(enrichment.getNumSpeciesProteins());
            request.setAttribute("cellComponentTerms", ccTabular);
        }
        
        // Molecular Function
        if(filterForm.getGoAspect() == GOUtils.MOLECULAR_FUNCTION) {
            GOEnrichmentTabular mfTabular = new GOEnrichmentTabular();
            mfTabular.setEnrichedTerms(enrichment.getMolecularFunctionEnriched());
            mfTabular.setTitle("Molecular Function");
            mfTabular.setNumProteinsInUniverse(enrichment.getTotalAnnotatedMolecularFunction());
            mfTabular.setNumProteinsInSet(enrichment.getNumSpeciesProteins());
            request.setAttribute("molFunctionTerms", mfTabular);
        }
        
        request.setAttribute("enrichment", enrichment);
        request.setAttribute("pinferId", pinferId);
        request.setAttribute("species", Species.getInstance(speciesId));
        request.setAttribute("proteinInferFilterForm", filterForm);
        request.setAttribute("showGoForm", true);
        
        
        long e = System.currentTimeMillis();
        log.info("ProteinInferGOEnrichmentAction results in: "+TimeUtils.timeElapsedMinutes(s,e)+" minutes");
        return mapping.findForward("Success");
    }
    
    private GOEnrichmentOutput doGoEnrichmentAnalysis(int pinferId, int speciesId, ProteinInferFilterForm filterForm) throws Exception {
        
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
        filterCriteria.setShowParsimonious(!filterForm.isShowAllProteins());
        filterCriteria.setExcludeIndistinGroups(filterForm.isExcludeIndistinProteinGroups());
        filterCriteria.setValidationStatus(filterForm.getValidationStatus());
        filterCriteria.setAccessionLike(filterForm.getAccessionLike());
        filterCriteria.setDescriptionLike(filterForm.getDescriptionLike());
        filterCriteria.setDescriptionNotLike(filterForm.getDescriptionNotLike());
        filterCriteria.setPeptide(filterForm.getPeptide());
        filterCriteria.setExactPeptideMatch(filterForm.getExactPeptideMatch());
        
        // Get the protein Ids that fulfill the criteria.
        List<Integer> proteinIds = IdPickerResultsLoader.getProteinIds(pinferId, filterCriteria);
        log.info(proteinIds.size()+" proteins for GO enrichment analysis");
        List<Integer> nrseqIds = new ArrayList<Integer>(proteinIds.size());
        ProteinferDAOFactory factory = ProteinferDAOFactory.instance();
        ProteinferProteinDAO protDao = factory.getProteinferProteinDao();
        for(int proteinId: proteinIds) {
            ProteinferProtein protein = protDao.loadProtein(proteinId);
            nrseqIds.add(protein.getNrseqProteinId());
        }
        
        GOEnrichmentInput input = new GOEnrichmentInput(speciesId);
        input.setProteinIds(nrseqIds);
        if(filterForm.getGoAspect() == GOUtils.BIOLOGICAL_PROCESS)
            input.setUseBiologicalProcess(true);
        if(filterForm.getGoAspect() == GOUtils.CELLULAR_COMPONENT)
            input.setUseCellularComponent(true);
        if(filterForm.getGoAspect() == GOUtils.MOLECULAR_FUNCTION)
            input.setUseMolecularFunction(true);
        input.setPValCutoff(Double.parseDouble(filterForm.getGoEnrichmentPVal()));
        
        GOEnrichmentOutput enrichment = GOEnrichmentCalculator.calculate(input);
        return enrichment;
        
    }
}
