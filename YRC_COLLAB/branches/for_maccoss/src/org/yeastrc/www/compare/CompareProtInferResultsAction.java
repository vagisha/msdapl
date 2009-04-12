/**
 * CompareProtInferResultsAction.java
 * @author Vagisha Sharma
 * Apr 9, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.Collections;
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

import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferRunDAO;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerProteinBaseDAO;
import edu.uwpr.protinfer.database.dto.ProteinferRun;

/**
 * 
 */
public class CompareProtInferResultsAction extends Action {

    private static final Logger log = Logger.getLogger(CompareProtInferResultsAction.class);

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
        ProteinferRunComparisonForm myForm = (ProteinferRunComparisonForm) form;
        List<ProteinferRunFormBean> runs = myForm.getPiRuns();
        
        List<Integer> piRunIds = myForm.getSelectedProteinferRunIds();

        // get the protein inference ids to compare
        if(piRunIds == null || piRunIds.size() < 2 || piRunIds.size() > 3) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Please select 2 or 3 experiments to compare."));
            saveErrors( request, errors );
            return mapping.findForward("authenticate");
        }
        
        ProteinferDAOFactory fact = ProteinferDAOFactory.instance();
        ProteinferRunDAO runDao = fact.getProteinferRunDao();
        IdPickerProteinBaseDAO protDao = fact.getIdPickerProteinBaseDao();
        
        ProteinferRun run1 = runDao.loadProteinferRun(piRunIds.get(0));
        ProteinferRun run2 = runDao.loadProteinferRun(piRunIds.get(1));
        List<Integer> nrseqIds1 = protDao.getNrseqProteinIds(run1.getId(), true); // get only parsimonious proteins
        List<Integer> nrseqIds2 = protDao.getNrseqProteinIds(run2.getId(), true);
        
        // sort by nrseq ids
        Collections.sort(nrseqIds1);
        Collections.sort(nrseqIds2);
        
        if(piRunIds.size() == 2) {
            
            int common1_2 = commonIds(nrseqIds1, nrseqIds2);
            
            StringBuilder googleChartUrl = createChartUrl(nrseqIds1.size(), nrseqIds2.size(),
                    common1_2, new String[]{"ID"+run1.getId(), "ID"+run2.getId()});
            request.setAttribute("chart", googleChartUrl);
        }
        else if(piRunIds.size() == 3) {
            
            ProteinferRun run3 = runDao.loadProteinferRun(piRunIds.get(2));
            List<Integer> nrseqIds3 = protDao.getNrseqProteinIds(piRunIds.get(2), true);
            // sort by nrseq ids
            Collections.sort(nrseqIds3);
            
            int common1_2 = commonIds(nrseqIds1, nrseqIds2);
            int common1_3 = commonIds(nrseqIds1, nrseqIds3);
            int common2_3 = commonIds(nrseqIds2, nrseqIds3);
            int common1_2_3 = commonIds(nrseqIds1, nrseqIds2, nrseqIds3);
            
            
            StringBuilder googleChartUrl = createChartUrl(nrseqIds1.size(), nrseqIds2.size(), nrseqIds3.size(),
                    common1_2, common1_3, common2_3, common1_2_3,
                    new String[]{"ID"+run1.getId(), "ID"+run2.getId(), "ID"+run3.getId()});
            
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
        url.append("&chs=300x200");
        url.append("&chd=t:");
        url.append(A+",");      // A
        url.append(B+",");      // B
        url.append(C+",");      // C
        url.append(AB+",");     // A & B
        url.append(AC+",");     // A & C
        url.append(BC+",");     // B & C
        url.append(ABC);        // A & B & C
        // Chart legend
        // chdl=First|Second|Third
        // chco=ff0000,00ff00,0000ff
        // chdlp=t
        url.append("&chdpl=t");
        url.append("&chdl=");
        
        if(legends.length == 2) {
            url.append(num1+": "+legends[0]);
            url.append("|"+num2+": "+legends[1]);
            url.append("|"+common1_2+": "+legends[0]+" AND "+legends[1]);
            
            url.append("&chco=ff0000,00ff00,AAAA00");
        }
        
        if(legends.length == 3) {
            url.append(num1+": "+legends[0]);
            url.append("|"+num2+": "+legends[1]);
            url.append("|"+num3+": "+legends[1]);
            url.append("|"+common1_2+": "+legends[0]+" AND "+legends[1]);
            url.append("|"+common1_3+": "+legends[0]+" AND "+legends[2]);
            url.append("|"+common2_3+": "+legends[1]+" AND "+legends[2]);
            url.append("|"+common1_2_3+": "+legends[0]+" AND "+legends[1]+" AND "+legends[2]);
            
            url.append("&chco=ff0000,00ff00,0000ff,AAAA00,AA00AA,00AAAA,AAAAFF");
        }
//        return url.toString();
        return url;
    }
    
    private static int calcPercentage(int num1, int num2) {
        return (int)((num1*100.0)/num2);
    }

    private static StringBuilder createChartUrl(int A, int B, int AB, String[] legends) {
        return createChartUrl(A, B, 0, AB, 0, 0, 0, legends);
    }
    
    /**
     * Find the number of common ids in two sorted lists of integers
     * @param list1
     * @param list2
     * @return
     */
    private static int commonIds(List<Integer> list1, List<Integer> list2) {
        
        if(list1 == null || list1.size() ==0)
            return 0;
        if(list2 == null || list2.size() == 0)
            return 0;
        
        int commonNum = 0;
        for(int id: list1) {
            if(Collections.binarySearch(list2, id) >= 0)
                commonNum++;
        }
        return commonNum;
    }
    
    /**
     * Find the number of common ids in three sorted lists of integers
     * @param list1
     * @param list2
     * @return
     */
    private static int commonIds(List<Integer> list1, List<Integer> list2, List<Integer> list3) {
        
        if(list1 == null || list1.size() ==0)
            return 0;
        if(list2 == null || list2.size() == 0)
            return 0;
        if(list3 == null || list3.size() == 0)
            return 0;
        
        int commonNum = 0;
        for(int id: list1) {
            if((Collections.binarySearch(list2, id) >= 0) && (Collections.binarySearch(list3, id) >= 0))
                commonNum++;
        }
        return commonNum;
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
