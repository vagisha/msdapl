package edu.uwpr.protinfer.infer;



public class Peptide {

    private final String sequence;
//    private String modifiedSequence;
    
//    private List<PeptideModification> modifications;
    
    private int id;
    private int peptideGroupId;
    
    
    /**
     * @param sequence
     * @param id unique id for this peptide
     */
    public Peptide(String sequence, int id) {
        this.sequence = sequence;
        this.id = id;
//        modifications = new ArrayList<PeptideModification>();
    }
    
    public String getSequence() {
        return sequence;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getPeptideGroupId() {
        return peptideGroupId;
    }

    public void setPeptideGroupId(int peptideGroupId) {
        this.peptideGroupId = peptideGroupId;
    }
    
//    public void addModification(PeptideModification modification) {
//        modifications.add(modification);
//        // if the modified sequence has been initialized, set it to null
//        modifiedSequence = null;
//    }
    
    /**
     * Returns the sequence of the peptide with modifications. E.g. PEP(80.0)TIDE
     * @return
     */
//    public String getModifiedSequence() {
//        
//        if (modifiedSequence != null)
//            return modifiedSequence;
//        
//        if (modifications.size() == 0) {
//            modifiedSequence = sequence;
//        }
//        else {
//            int lastIdx = 0;
//            StringBuilder seq = new StringBuilder();
//            sortModifications();
//            for (PeptideModification mod: modifications) {
//                seq.append(sequence.subSequence(lastIdx, mod.getModifiedIndex()+1)); // get sequence up to an including the modified position.
//                seq.append("["+Math.round(mod.getMassShift().doubleValue())+"]");
//                lastIdx = mod.getModifiedIndex()+1;
//            }
//            if (lastIdx < sequence.length())
//                seq.append(sequence.subSequence(lastIdx, sequence.length()));
//            modifiedSequence = seq.toString();
//        }
//        return modifiedSequence;
//    }
//    
//    public int getModificationCount() {
//        return this.modifications.size();
//    }
//    
//    private void sortModifications() {
//        Collections.sort(modifications, new Comparator<PeptideModification>(){
//            public int compare(PeptideModification o1, PeptideModification o2) {
//                return Integer.valueOf(o1.getModifiedIndex()).compareTo(Integer.valueOf(o2.getModifiedIndex()));
//            }});
//    }
    
    public String toString() {
       return sequence;
    }
}
