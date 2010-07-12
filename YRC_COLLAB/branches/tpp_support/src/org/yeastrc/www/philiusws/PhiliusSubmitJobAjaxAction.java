/**
 * submitPhiliusJobAjaxAction.java
 * @author Vagisha Sharma
 * Feb 3, 2010
 * @version 1.0
 */
package org.yeastrc.www.philiusws;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.nrseq.dao.NrSeqLookupUtil;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class PhiliusSubmitJobAjaxAction extends Action {

    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {


        // User making this request
        User user = UserUtils.getUser(request);
        if (user == null) {
        	response.getWriter().write("You are not logged in!");
            response.setStatus(HttpServletResponse.SC_SEE_OTHER); // Status code (303) indicating that the response to the request can be found under a different URI.
            return null;
        }

        // get the NR_SEQ protein ID
        int nrseqId = 0;
        try {nrseqId = Integer.parseInt(request.getParameter("nrseqId"));}
        catch(NumberFormatException e){};
        // if we  do not have a valid protein inference run id
        // return an error.
        if(nrseqId <= 0) {
        	response.setContentType("text/html");
            response.getWriter().write("FAILED: Invalid protein ID: "+nrseqId);
            return null;
        }

        // get the sequence for the protein
        String sequence = NrSeqLookupUtil.getProteinSequence(nrseqId);
//        sequence = " 1          11         21         31         41         51         |          |          |          |          |          |";          
//        sequence += "1 MSDQESVVSF NSQNTSMVDV EGQQPQQYVP SKTNSRANQL KLTKTETVKS LQDLGVTSAA  60";
//        sequence += "61 PVPDINAPQT AKNNIFPEEY TMETPSGLVP VATLQSMGRT ASALSRTRTK QLNRTATNSS 120";
//        sequence += "121 STGKEEMEEE ETEEREDQSG ENELDPEIEF VTFVTGDPEN PHNWPSWVRW SYTVLLSILV 180";
//        sequence += "181 ICVAYGSACI SGGLGTVEKK YHVGMEAAIL SCSLMVIGFS LGPLIWSPVS DLYGRRVAYF 240";
//        sequence += "241 VSMGLYVIFN IPCALAPNLG CLLACRFLCG VWSSSGLCLV GGSIADMFPS ETRGKAIAFF 300";
//        sequence += "301 AFAPYVGPVV GPLVNGFISV STGRMDLIFW VNMAFAGVMW IISSAIPETY APVILKRKAA 360";
//        sequence += "361 RLRKETGNPK IMTEQEAQGV SMSEMMRACL LRPLYFAVTE PVLVATCFYV CLIYSLLYAF 420";
//        sequence += "421 FFAFPVIFGE LYGYKDNLVG LMFIPIVIGA LWALATTFYC ENKYLQIVKQ RKPTPEDRLL 480";
//        sequence += "481 GAKIGAPFAA IALWILGATA YKHIIWVGPA SAGLAFGFGM VLIYYSLNNY IIDCYVQYAS 540";
//        sequence += "541 SALATKVFLR SAGGAAFPLF TIQMYHKLNL HWGSWLLAFI STAMIALPFA FSYWGKGLRH 600";
//        sequence += "601 KLSKKDYSID SVEM";
        if(sequence == null) {
        	response.setContentType("text/html");
            response.getWriter().write("FAILED: No sequence found for protein ID: "+nrseqId);
            return null;
        }
        
        // submit a Philius job
        int token = 0;
        try {
            PhiliusPredictorService service = new PhiliusPredictorService();
            PhiliusPredictorDelegate port = service.getPhiliusPredictorPort();
            token = port.submitSequence(sequence);
            // Return the Philius job token
            response.setContentType("text/html");
            response.getWriter().write(""+token);
        }
        catch (PhiliusWSException_Exception e) {
            e.printStackTrace();
            response.setContentType("text/html");
            response.getWriter().write("FAILED: "+e.getMessage());
        }
        return null;
    }
}
