/**
 * CompareProtInferResultsAction.java
 * @author Vagisha Sharma
 * Apr 9, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

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
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;
import org.yeastrc.yates.YatesRun;

import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferRunDAO;

/**
 * 
 */
public class CompareProteinSetsAction extends Action {

    private static final Logger log = Logger.getLogger(CompareProteinSetsAction.class);

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

        
        // get the protein inference ids to compare
        ProteinSetComparisonForm myForm = (ProteinSetComparisonForm) form;
        
        
        // Get the selected protein inference run ids
        List<Integer> piRunIds = myForm.getSelectedProteinferRunIds();
        
        List<Integer> dtaRunIds = myForm.getSelectedDtaRunIds();

        int total = piRunIds.size() + dtaRunIds.size();
        
        // get the protein inference ids to compare
        if(total < 2) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Please select 2 or more datasets to compare."));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        ProteinferDAOFactory fact = ProteinferDAOFactory.instance();
        ProteinferRunDAO runDao = fact.getProteinferRunDao();
        
        List<Dataset> datasets = new ArrayList<Dataset>(total);
        
        // Protein inference datasets
        for(int piRunId: piRunIds) {
            if(runDao.loadProteinferRun(piRunId) == null) {
                ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", 
                        "No protein inference run found with ID: "+piRunId+"."));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
            }
            Dataset dataset = new Dataset(piRunId, DatasetSource.PROT_INFER);
            datasets.add(dataset);
        }
        
        // DTASelect datasets
        for(int dtaRunId: dtaRunIds) {
            YatesRun run = new YatesRun();
            try {
                run.load(dtaRunId);
            }
            catch(Exception e) {
                ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Error loading DTASelect dataset with ID: "+dtaRunId+"."));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
            }
            Dataset dataset = new Dataset(dtaRunId, DatasetSource.DTA_SELECT);
            datasets.add(dataset);
        }
        
        
        ComparisonDataset comparison = ProteinDatasetComparer.instance().compareDatasets(datasets, false);
        comparison.setCurrentPage(myForm.getPageNum());
        request.setAttribute("comparison", comparison);
        request.setAttribute("proteinSetComparisonForm", myForm);
        
        
        
        if(comparison.getDatasetCount() == 2) {
            
            int ds1 = comparison.getProteinCount(0);
            int ds2 = comparison.getProteinCount(1);
            int common1_2 = comparison.getCommonProteinCount(0, 1);
            
            StringBuilder googleChartUrl = createChartUrl(ds1, ds2,
                    common1_2, new String[]{"ID"+comparison.getDatasets().get(0).getDatasetId(), 
                                            "ID"+comparison.getDatasets().get(1).getDatasetId()});
            request.setAttribute("chart", googleChartUrl);
        }
        else if(comparison.getDatasetCount() == 3) {
            
            int ds1 = comparison.getProteinCount(0);
            int ds2 = comparison.getProteinCount(1);
            int ds3 = comparison.getProteinCount(2);
            
            int common1_2 = comparison.getCommonProteinCount(0, 1);
            int common1_3 = comparison.getCommonProteinCount(0, 2);
            int common2_3 = comparison.getCommonProteinCount(1, 2);
            int common1_2_3 = 0; // commonIds(nrseqIds1, nrseqIds2, nrseqIds3);
            
            
            StringBuilder googleChartUrl = createChartUrl(ds1, ds2, ds3,
                    common1_2, common1_3, common2_3, common1_2_3,
                    new String[]{"ID"+comparison.getDatasets().get(0).getDatasetId(), 
                                 "ID"+comparison.getDatasets().get(1).getDatasetId(), 
                                 "ID"+comparison.getDatasets().get(2).getDatasetId()});
            
            request.setAttribute("chart", googleChartUrl.toString());
        }
        return mapping.findForward("Success");
    }

    private static StringBuilder createChartUrl(int num1, int num2, int num3, 
                    int common1_2, int common1_3, int common2_3, int common1_2_3, String[] legends) {
        
        int maxNum = Math.max(num1, num2);
        maxNum = Math.max(maxNum, num3);
        
        int A = calcPercentage(num1, maxNum);
        int B = calcPercentage(num2, maxNum);
        int C = calcPercentage(num3, maxNum);
        int AB = calcPercentage(common1_2, maxNum);
        int AC = calcPercentage(common1_3, maxNum);
        int BC = calcPercentage(common2_3, maxNum);
        int ABC = calcPercentage(common1_2_3, maxNum);
        
        
        // http://chart.apis.google.com/chart?cht=v&chs=200x100&chd=t:100,80,0,30,0,0,0
        StringBuilder url = new StringBuilder();
        url.append("http://chart.apis.google.com/chart?cht=v");
        url.append("&chs=170x100");
        url.append("&chd=t:");
        url.append(A+",");      // A
        url.append(B+",");      // B
        url.append(C+",");      // C
        url.append(AB+",");     // A & B
        url.append(AC+",");     // A & C
        url.append(BC+",");     // B & C
        url.append(ABC);        // A & B & C
        
        // chart colors
        url.append("&chco="+DatasetColor.get(0).hexValue()+","+DatasetColor.get(1).hexValue()+","+DatasetColor.get(2).hexValue());
        
        // Chart legend
        // chdl=First|Second|Third
        // chco=ff0000,00ff00,0000ff
        // chdlp=t
//        url.append("&chdpl=t");
//        url.append("&chdl=");
//        
//        if(legends.length == 2) {
//            url.append(num1+": "+legends[0]);
//            url.append("|"+num2+": "+legends[1]);
//            url.append("|"+common1_2+": "+legends[0]+" AND "+legends[1]);
//            
//            url.append("&chco=ff0000,00ff00,AAAA00");
//        }
//        
//        if(legends.length == 3) {
//            url.append(num1+": "+legends[0]);
//            url.append("|"+num2+": "+legends[1]);
//            url.append("|"+num3+": "+legends[2]);
//            url.append("|"+common1_2+": "+legends[0]+" AND "+legends[1]);
//            url.append("|"+common1_3+": "+legends[0]+" AND "+legends[2]);
//            url.append("|"+common2_3+": "+legends[1]+" AND "+legends[2]);
//            url.append("|"+common1_2_3+": "+legends[0]+" AND "+legends[1]+" AND "+legends[2]);
//            
//            url.append("&chco=ff0000,00ff00,0000ff,AAAA00,AA00AA,00AAAA,AAAAFF");
//        }
        //url.append("&chf=bg,s,F2F2F2");
//        return url.toString();
        return url;
    }
    
    private static int calcPercentage(int num1, int num2) {
        return (int)((num1*100.0)/num2);
    }

    private static StringBuilder createChartUrl(int A, int B, int AB, String[] legends) {
        return createChartUrl(A, B, 0, AB, 0, 0, 0, legends);
    }
    
    
    public static void main(String[] args) {
        
        int A = 100;
        int B = 80;
        int C = 60;
        
        int AB = 30;
        int AC = 25;
        int BC = 20;
        int ABC = 10;
        
        String[] legends = new String[]{"ID1", "ID2", "ID3"};
        
        String url = createChartUrl(A, B, C, AB, AC, BC, ABC, legends).toString();
        System.out.println(url);
        
        C = 0;
        AC = 0;
        BC = 0;
        ABC = 0;
        legends = new String[]{"ID1", "ID2"};
        url = createChartUrl(A, B, C, AB, AC, BC, ABC, legends).toString();
        System.out.println(url);
    }
}
