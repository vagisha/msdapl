package org.yeastrc.www.proteinfer.alignment;

public class AlignedPair {

    private AlignedProtein protein1;
    private AlignedProtein protein2;
    
    public AlignedProtein getProtein1() {
        return protein1;
    }
    public void setProtein1(AlignedProtein protein1) {
        this.protein1 = protein1;
    }
    public AlignedProtein getProtein2() {
        return protein2;
    }
    public void setProtein2(AlignedProtein protein2) {
        this.protein2 = protein2;
    }
    
    public void insertGap(int index) throws AlignmentException {
        protein1.insertGap(index);
        protein2.insertGap(index);
    }
    
    public int getAlignedLength() {
        return protein1.getAlignedLength();
    }
}
