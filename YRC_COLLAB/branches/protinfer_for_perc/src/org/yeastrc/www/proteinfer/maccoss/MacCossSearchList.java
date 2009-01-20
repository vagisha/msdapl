package org.yeastrc.www.proteinfer.maccoss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.MsSearchAnalysisDAO;
import org.yeastrc.ms.domain.search.Program;

import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferRunDAO;
import edu.uwpr.protinfer.database.dto.ProteinferRun;


public class MacCossSearchList extends Action{

    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {
        
        
        // get a list of ALL search ids in the database
        List<Integer> experimentIds = DAOFactory.instance().getMsExperimentDAO().getAllExperimentIds();
        List<Integer> searchIds = new ArrayList<Integer>();
        for(Integer exptId: experimentIds) {
            List<Integer> sIds = DAOFactory.instance().getMsSearchDAO().getSearchIdsForExperiment(exptId);
            searchIds.addAll(sIds);
        }
        
        // Associate Percolator analysis with each search id
        // Associate protein inference runs with each analysis
        MsSearchAnalysisDAO analysisDao = DAOFactory.instance().getMsSearchAnalysisDAO();
        MsRunSearchAnalysisDAO rsAnalysisDao = DAOFactory.instance().getMsRunSearchAnalysisDAO();
        
        ProteinferRunDAO pinferRunDao = ProteinferDAOFactory.instance().getProteinferRunDao();
        
        Map<Integer, Map<Integer, List<Integer>>> searchInfoMap = 
            new HashMap<Integer, Map<Integer,List<Integer>>>(searchIds.size());
        
        for(Integer searchId: searchIds) {
            
            List<Integer> analysisIds = analysisDao.getAnalysisIdsForSearch(searchId);
            Map<Integer, List<Integer>> analysisToProtInferMap = new HashMap<Integer, List<Integer>>();
            for(int analysisId: analysisIds) {
                List<Integer> rsAnalysisIds = rsAnalysisDao.getRunSearchAnalysisIdsForAnalysis(analysisId);
                List<Integer> pinferRunIds = pinferRunDao.loadProteinferIdsForInputIds(rsAnalysisIds);
                Iterator<Integer> iter = pinferRunIds.iterator();
                while(iter.hasNext()) {
                    int piRunId = iter.next();
                    ProteinferRun run = pinferRunDao.loadProteinferRun(piRunId);
                    if(!(run.getInputGenerator() == Program.PERCOLATOR))
                        iter.remove();
                }
                analysisToProtInferMap.put(analysisId, pinferRunIds);
            }
            searchInfoMap.put(searchId, analysisToProtInferMap);
        }
        
        System.out.println("Found "+searchIds.size()+" searches");
        request.setAttribute("searches", searchInfoMap);
        
        // Go!
        return mapping.findForward( "Success" ) ;

    }
}
