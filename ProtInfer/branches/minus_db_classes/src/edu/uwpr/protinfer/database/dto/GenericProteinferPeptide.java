package edu.uwpr.protinfer.database.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uwpr.protinfer.PeptideDefinition;

public abstract class GenericProteinferPeptide <S extends ProteinferSpectrumMatch, T extends GenericProteinferIon<S>> {

    private int id;
    private int pinferId;
    private String sequence;
    private boolean uniqueToProtein;
    private List<T> ionList;

    public GenericProteinferPeptide() {
        ionList = new ArrayList<T>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public int getProteinferId() {
        return pinferId;
    }

    public void setProteinferId(int pinferId) {
        this.pinferId = pinferId;
    }

    public List<T> getIonList() {
        return ionList;
    }

    public void setIonList(List<T> spectrumMatchList) {
        this.ionList = spectrumMatchList;
    }

    public void addIon(T ion) {
        ionList.add(ion);
    }
    
    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public boolean isUniqueToProtein() {
        return uniqueToProtein;
    }
    
    public void setUniqueToProtein(boolean unique) {
        this.uniqueToProtein = unique;
    }

    public int getSpectrumCount() {
        int count = 0;
        for(T ion: ionList) {
            count += ion.getSpectrumCount();
        }
        return count;
    }
    
    public S getBestSpectrumMatch() {
        S best = null;
        for(T ion: ionList) {
            if(best == null)
                best = ion.getBestSpectrumMatch();
            else {
                S s = ion.getBestSpectrumMatch();
                best = best.getRank() < s.getRank() ? best : s;
            }
        }
        return best;
    }
    /**
     * Returns the number of distinct ions this peptide represents based on the 
     * given PeptideDefinition. 
     * @param peptideDef
     * @return
     */
    public int getNumDistinctPeptides(PeptideDefinition peptideDef) {
        
        // peptide uniquely defined by sequence
        if(!peptideDef.isUseCharge() && !peptideDef.isUseMods())
            return 1;
        
        // peptide uniquely defined by sequence and charge
        if(peptideDef.isUseCharge() && !peptideDef.isUseMods()) {
            Set<Integer> chgStates = new HashSet<Integer>();
            for(T ion: ionList) {
                chgStates.add(ion.getCharge());
            }
            return chgStates.size();
        }
        // peptide uniquely defined by sequence and modification state
        if(!peptideDef.isUseCharge() && peptideDef.isUseMods()) {
            Set<Integer> modStates = new HashSet<Integer>();
            for(T ion: ionList) {
                modStates.add(ion.getModificationStateId());
            }
            return modStates.size();
        }
        // peptide uniquely defined by sequence, charge and modification state
        if(peptideDef.isUseCharge() && peptideDef.isUseMods()) {
            return ionList.size();
        }
        
        // this should never happen
        return 0;
    }
    
    /**
     * Returns the number of distinct ions this peptide represents based on the 
     * given PeptideDefinition. 
     * @param peptideDef
     * @return
     */
//    public List<GenericProteinferPeptide<S,T>> getDistinctPeptides(PeptideDefinition peptideDef) {
//        
//        // peptide uniquely defined by sequence
//        if(!peptideDef.isUseCharge() && !peptideDef.isUseMods()) {
//           List<GenericProteinferPeptide<S,T>> list = new ArrayList<GenericProteinferPeptide<S,T>>(1);
//           list.add(this);
//           return list;
//        }
//        
//        // peptide uniquely defined by sequence and charge
//        if(peptideDef.isUseCharge() && !peptideDef.isUseMods()) {
//            Map<Integer, GenericProteinferPeptide<S,T>> peptMap = new HashMap<Integer, GenericProteinferPeptide<S,T>>();
//            for(T ion: ionList) {
//                GenericProteinferPeptide<S,T> peptide = peptMap.get(ion.getCharge());
//                if(peptide == null) {
//                    peptide = newPeptide();
//                    peptide.setId(this.id);
//                    peptide.setUniqueToProtein(this.uniqueToProtein);
//                    peptide.setProteinferId(this.pinferId);
//                    peptide.setSequence(ion.getSequence());
//                    peptMap.put(ion.getCharge(), peptide);
//                }
//                peptide.addIon(ion);
//            }
//            List<GenericProteinferPeptide<S,T>> list = new ArrayList<GenericProteinferPeptide<S,T>>(ionList.size());
//            list.addAll(peptMap.values());
//            return list;
//        }
//        // peptide uniquely defined by sequence and modification state
//        if(!peptideDef.isUseCharge() && peptideDef.isUseMods()) {
//            Map<Integer, GenericProteinferPeptide<S,T>> peptMap = new HashMap<Integer, GenericProteinferPeptide<S,T>>();
//            for(T ion: ionList) {
//                GenericProteinferPeptide<S,T> peptide = peptMap.get(ion.getModificationStateId());
//                if(peptide == null) {
//                    peptide = newPeptide();
//                    peptide.setId(this.id);
//                    peptide.setUniqueToProtein(this.uniqueToProtein);
//                    peptide.setProteinferId(this.pinferId);
//                    peptide.setSequence(ion.getSequence());
//                    peptMap.put(ion.getModificationStateId(), peptide);
//                }
//                peptide.addIon(ion);
//            }
//            List<GenericProteinferPeptide<S,T>> list = new ArrayList<GenericProteinferPeptide<S,T>>(ionList.size());
//            list.addAll(peptMap.values());
//            return list;
//        }
//        // peptide uniquely defined by sequence, charge and modification state
//        if(peptideDef.isUseCharge() && peptideDef.isUseMods()) {
//            List<GenericProteinferPeptide<S,T>> list = new ArrayList<GenericProteinferPeptide<S,T>>(ionList.size());
//            for(T ion: ionList) {
//                GenericProteinferPeptide<S,T> peptide = newPeptide();
//                peptide.setId(this.id);
//                peptide.addIon(ion);
//                peptide.setUniqueToProtein(this.uniqueToProtein);
//                peptide.setProteinferId(this.pinferId);
//                peptide.setSequence(ion.getSequence());
//                list.add(peptide);
//            }
//            return list;
//        }
//        
//        // this should never happen
//        return null;
//    }
    
    protected abstract GenericProteinferPeptide<S,T>  newPeptide();
    
}