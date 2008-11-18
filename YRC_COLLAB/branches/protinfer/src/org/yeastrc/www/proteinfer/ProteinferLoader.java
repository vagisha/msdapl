package org.yeastrc.www.proteinfer;

import java.sql.SQLException;
import java.util.List;

import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.nr_seq.NRProtein;
import org.yeastrc.nr_seq.NRProteinFactory;

import edu.uwpr.protinfer.database.dao.DAOFactory;
import edu.uwpr.protinfer.database.dao.ProteinferProteinDAO;
import edu.uwpr.protinfer.database.dao.ProteinferRunDAO;
import edu.uwpr.protinfer.database.dto.ProteinferFilter;
import edu.uwpr.protinfer.database.dto.ProteinferInput;
import edu.uwpr.protinfer.database.dto.ProteinferPeptide;
import edu.uwpr.protinfer.database.dto.ProteinferProtein;
import edu.uwpr.protinfer.database.dto.ProteinferRun;
import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;
import edu.uwpr.protinfer.idpicker.IDPickerParams;
import edu.uwpr.protinfer.idpicker.SearchSummary;
import edu.uwpr.protinfer.idpicker.SearchSummary.RunSearch;

public class ProteinferLoader {

    private static final DAOFactory pinferDaoFactory = DAOFactory.instance();
    private static final org.yeastrc.ms.dao.DAOFactory msDataDaoFactory = org.yeastrc.ms.dao.DAOFactory.instance();
    
    private ProteinferLoader(){}
    
    public static IDPickerParams getIDPickerParams(int pinferId) {
        IDPickerParams params = new IDPickerParams();
        ProteinferRunDAO runDao = pinferDaoFactory.getProteinferRunDao();
        ProteinferRun inferRun = runDao.getProteinferRun(pinferId);
        List<ProteinferFilter> filters = inferRun.getFilters();
        for(ProteinferFilter filter: filters) {
            if(filter.getFilterName().equalsIgnoreCase("decoyRatio"))
                params.setDecoyRatio(Float.parseFloat(filter.getFilterValue()));
            else if (filter.getFilterName().equalsIgnoreCase("decoyPrefix"))
                params.setDecoyPrefix(filter.getFilterValue());
            else if (filter.getFilterName().equalsIgnoreCase("parsimonyAnalysis"))
                params.setDoParsimonyAnalysis(Boolean.parseBoolean(filter.getFilterValue()));
            else if (filter.getFilterName().equalsIgnoreCase("maxAbsFDR"))
                params.setMaxAbsoluteFdr(Float.parseFloat(filter.getFilterValue()));
            else if (filter.getFilterName().equalsIgnoreCase("maxRelativeFDR"))
                params.setMaxRelativeFdr(Float.parseFloat(filter.getFilterValue()));
                
        }
        return params;
    }
    
    public static SearchSummary getIDPickerInputSummary(int pinferId) {
        
        SearchSummary summary = new SearchSummary();
        
        ProteinferRunDAO inferRunDao = pinferDaoFactory.getProteinferRunDao();
        ProteinferRun inferRun = inferRunDao.getProteinferRun(pinferId);
        
        summary.setAllProteins(inferRun.getUnfilteredProteins());
        
        ProteinferProteinDAO protDao = pinferDaoFactory.getProteinferProteinDao();
        summary.setFilteredProteinsMinPeptCount(protDao.getFilteredProteinCount(pinferId));
        summary.setFilteredProteinsParsimony(protDao.getFilteredParsimoniousProteinCount(pinferId));
        
        
        List<ProteinferInput> inputList = inferRun.getInputSummaryList();
        MsRunSearchDAO runSearchDao = msDataDaoFactory.getMsRunSearchDAO();
        MsRunDAO runDao = msDataDaoFactory.getMsRunDAO();
        
        for(ProteinferInput input: inputList) {
            MsRunSearch runSearch = runSearchDao.loadRunSearch(input.getRunSearchId());
            String filename = runDao.loadFilenameNoExtForRun(runSearch.getRunId());
            RunSearch rs = new RunSearch();
            rs.setRunSearchId(input.getRunSearchId());
            rs.setRunName(filename);
            rs.setTotalTargetHits(input.getNumTargetHits());
            rs.setTotalDecoyHits(input.getNumDecoyHits());
            rs.setFilteredTargetHits(input.getNumFilteredTargetHits());
            rs.setIsSelected(true);
            summary.addRunSearch(rs);
        }
        return summary;
    }
    
    public static List<ProteinferProtein> getProteinferProteins(int pinferId) {
        ProteinferProteinDAO protDao = pinferDaoFactory.getProteinferProteinDao();
        List<ProteinferProtein> proteins = protDao.getProteinferProteins(pinferId);
        // set the description for the proteins.  This requires querying the 
        // NRSEQ database
        NRProteinFactory nrpf = NRProteinFactory.getInstance();
        for(ProteinferProtein prot: proteins) {
            prot.getPeptides();
            prot.getUniquePeptideCount();
            NRProtein nrseqProt = null;
            try {
                nrseqProt = (NRProtein)(nrpf.getProtein(prot.getNrseqProteinId()));
            }
            catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                prot.setDescription(nrseqProt.getDescription());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return proteins;
    }
    
    public static String getModifiedSequenceForPeptide(ProteinferPeptide peptide) {
        // get the first hit
        ProteinferSpectrumMatch psm = peptide.getSpectrumMatchList().get(0);
        org.yeastrc.ms.dao.DAOFactory fact = org.yeastrc.ms.dao.DAOFactory.instance();
        MsSearchResultDAO resDao = fact.getMsSearchResultDAO();
        MsSearchResult res = resDao.load(psm.getMsRunSearchResultId());
        String seq = res.getResultPeptide().getModifiedPeptideSequence();
        int f = seq.indexOf('.');
        int l = seq.lastIndexOf('.');
        f = f == -1 ? 0 : f+1;
        l = l == -1 ? seq.length() : l;
        return seq.substring(f, l);
    }
}
