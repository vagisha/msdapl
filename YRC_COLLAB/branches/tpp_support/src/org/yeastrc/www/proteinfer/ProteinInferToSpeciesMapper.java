package org.yeastrc.www.proteinfer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.jobqueue.MSJob;
import org.yeastrc.jobqueue.MSJobFactory;

public class ProteinInferToSpeciesMapper {

	private ProteinInferToSpeciesMapper() {}
	
	private static final Logger log = Logger.getLogger(ProteinInferToSpeciesMapper.class.getName());
	
	public static List<Integer> map (int pinferId) {
		
		Set<Integer> speciesIds = new HashSet<Integer>();
		
		List<Integer> experimentIds = ProteinInferToExperimentMapper.map(pinferId);
		
		for(int experimentId: experimentIds) {
            int speciesId = 0;
            
            try {
                MSJob job = MSJobFactory.getInstance().getJobForExperiment(experimentId);
                speciesId = job.getTargetSpecies();
            }
            catch(Exception e) {
            	log.error("Error getting speciesId for experimentID: "+experimentId, e);
            	continue;
            } 
				
            if(speciesId > 0)
            	speciesIds.add(speciesId);
        }
		
		return new ArrayList<Integer>(speciesIds);
	}
}
