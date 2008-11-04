/**
 * IDPickerExecutor.java
 * @author Vagisha Sharma
 * Nov 3, 2008
 * @version 1.0
 */
package org.yeastrc.proteinfer;

import java.util.List;

import edu.uwpr.protinfer.SequestSpectrumMatch;
import edu.uwpr.protinfer.infer.InferredProtein;


/**
 * 
 */
public class IDPickerExecutor {

    private final IDPickerParams params;
    private final SearchSummary searchSummary;
    
    public IDPickerExecutor (SearchSummary searchSummary, IDPickerParams params) {
        this.params = params;
        this.searchSummary = searchSummary;
    }
    
    public List<InferredProtein<SequestSpectrumMatch>> execute() {
        
        
        return null;
    }
}
