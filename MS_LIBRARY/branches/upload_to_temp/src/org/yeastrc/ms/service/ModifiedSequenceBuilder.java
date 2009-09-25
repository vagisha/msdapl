/**
 * ModifiedSequenceBuilder.java
 * @author Vagisha Sharma
 * Jul 30, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.util.AminoAcidUtils;

/**
 * 
 */
public class ModifiedSequenceBuilder {

    
    private ModifiedSequenceBuilder() {}
    
    public static String build(String sequence, List<MsResultResidueMod> modifications) throws ModifiedSequenceBuilderException {
        
        if(modifications == null || modifications.size() == 0)
            return sequence;
        
        Map<Integer, List<MsResultResidueMod>> modMap = new HashMap<Integer, List<MsResultResidueMod>>();
        for(MsResultResidueMod mod: modifications) {
            List<MsResultResidueMod> mods = modMap.get(mod.getModifiedPosition());
            if(mods == null) {
                mods = new ArrayList<MsResultResidueMod>();
                modMap.put(mod.getModifiedPosition(), mods);
            }
            mods.add(mod);
        }
        
        StringBuilder buf = new StringBuilder();
        for(int i = 0; i < sequence.length(); i++) {
            List<MsResultResidueMod> mods = modMap.get(i);
            buf.append(sequence.charAt(i));
            
            if(mods != null) {
                String modStr = "";
                for(MsResultResidueMod mod: mods) {
                    if(mod.getModifiedResidue() != sequence.charAt(i)) {
                        throw new ModifiedSequenceBuilderException("Amino acid at index: "+i+" of sequence: "+sequence+
                                " does not match modified residue: "+mod.getModifiedResidue());
                    }
                    int mass = (int) (AminoAcidUtils.monoMass(mod.getModifiedResidue()) + mod.getModificationMass().doubleValue());
                    modStr += "+"+mass;
                }
                buf.append("["+modStr+"]");
            }
        }
        
        return buf.toString();
    }
}
