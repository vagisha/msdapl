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
import org.yeastrc.ms.domain.search.MsResultTerminalMod;
import org.yeastrc.ms.domain.search.MsTerminalModification.Terminal;

/**
 * 
 */
public class ModifiedSequenceBuilder {

    
    private ModifiedSequenceBuilder() {}
    
    public static String build(String sequence, List<MsResultResidueMod> dynamicResidueMods,
            List<MsResultTerminalMod> dynamicTerminalMods) throws ModifiedSequenceBuilderException {
        
        if((dynamicResidueMods == null || dynamicResidueMods.size() == 0) &&
           (dynamicTerminalMods == null || dynamicTerminalMods.size() == 0))
            return sequence;
        
        // map of dynamic residue modifications
        Map<Integer, List<MsResultResidueMod>> dynaResModMap = new HashMap<Integer, List<MsResultResidueMod>>();
        for(MsResultResidueMod mod: dynamicResidueMods) {
            List<MsResultResidueMod> mods = dynaResModMap.get(mod.getModifiedPosition());
            if(mods == null) {
                mods = new ArrayList<MsResultResidueMod>();
                dynaResModMap.put(mod.getModifiedPosition(), mods);
            }
            mods.add(mod);
        }
        
        
        // dynamic terminal modifications
        List<MsResultTerminalMod> ntermMods = new ArrayList<MsResultTerminalMod>();
        List<MsResultTerminalMod> ctermMods = new ArrayList<MsResultTerminalMod>();
        for(MsResultTerminalMod mod: dynamicTerminalMods) {
            if(mod.getModifiedTerminal() == Terminal.NTERM) {
                ntermMods.add(mod);
            }
            else if (mod.getModifiedTerminal() == Terminal.CTERM) {
                ctermMods.add(mod);
            }
        }
        
        // build the modified sequence
        StringBuilder buf = new StringBuilder();
        for(int i = 0; i < sequence.length(); i++) {
            
            buf.append(sequence.charAt(i));
            
            double mass = 0;

            // add any dynamic residue modifications for this amino acid in the sequence
            List<MsResultResidueMod> mods = dynaResModMap.get(i);
            if(mods != null) {
                for(MsResultResidueMod mod: mods) {
                    if(mod.getModifiedResidue() != sequence.charAt(i)) {
                        throw new ModifiedSequenceBuilderException("Amino acid at index: "+i+" of sequence: "+sequence+
                                " does not match modified residue: "+mod.getModifiedResidue());
                    }
                    mass += mod.getModificationMass().doubleValue();
                }
            }
            
            // add any dynamic terminal modifications
            if(i == 0 && ntermMods.size() > 0) {
                for(MsResultTerminalMod mod: ntermMods)
                    mass += mod.getModificationMass().doubleValue();
            }
            if(i == sequence.length() - 1 && ctermMods.size() > 0) {
                for(MsResultTerminalMod mod: ctermMods)
                    mass += mod.getModificationMass().doubleValue();
            }
            
            // If this position is modified add a string representing the mass of the residue at this position.
            if(mass != 0) {
                
                String modStr = ""+Math.round(mass); // (AminoAcidUtils.monoMass(sequence.charAt(i)) + mass);
                if(mass > 0)
                    modStr = "+"+modStr;
                buf.append("["+modStr+"]");
            }
        }
        
        return buf.toString();
    }
}
