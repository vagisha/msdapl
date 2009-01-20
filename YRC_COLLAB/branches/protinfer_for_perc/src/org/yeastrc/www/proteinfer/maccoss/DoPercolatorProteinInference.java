package org.yeastrc.www.proteinfer.maccoss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.www.proteinfer.ProteinInferInputSummary;
import org.yeastrc.www.proteinfer.ProgramParameters;
import org.yeastrc.www.proteinfer.ProteinInferenceForm;
import org.yeastrc.www.proteinfer.ProteinferJobSaver;

public class DoPercolatorProteinInference extends Action {

    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {
        
        

        ProteinInferenceForm prinferForm = (ProteinInferenceForm) form;
        ProteinInferInputSummary searchSummary = prinferForm.getInputSummary();
        ProgramParameters params = prinferForm.getProgramParams();
        
        // TODO validate the parameters (should be done in form?)
        ProteinferJobSaver.instance().saveJobToDatabase(1811, searchSummary, params, prinferForm.getInputType());
        
        // Go!
        return mapping.findForward( "Success" ) ;

    }
    
    private static float getTime(long start, long end) {
        long time = end - start;
        float seconds = (float)time / (1000.0f);
        return seconds;
    }
}
