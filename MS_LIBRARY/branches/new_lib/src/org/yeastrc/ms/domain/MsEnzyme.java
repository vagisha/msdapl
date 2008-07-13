/**
 * MsEnzyme.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain;

public interface MsEnzyme {

    public static enum Sense {
        
        CTERM((short)0), NTERM((short)1), UNKNOWN((short)-1);
    
        private short shortVal;
        private Sense(short shortVal) {
            this.shortVal = shortVal;
        }
        public short getShortVal() {
            return shortVal;
        }
        public static Sense instance(short shortVal) {
            switch (shortVal) {
                case 0: return CTERM;
                case 1: return NTERM;
                default: return UNKNOWN;
            }
        }
    };
    /**
     * Name of the enzyme
     * @return the name
     */
    public abstract String getName();

    /**
     * Sense repesents the terminal (C term or N term) where the enzyme cleaves.
     * @return the sense
     */
    public abstract Sense getSense();

    /**
     * Amino acid residue(s) where the enzyme cleaves.
     * Example: KR for enyme Trypsin
     * @return 
     */
    public abstract String getCut();

    /**
     * Amino acid(s), which when present next to the cleavage site inhibit enzyme action.
     * @return the nocut
     */
    public abstract String getNocut();

    /**
     * @return the description, if any
     */
    public abstract String getDescription();

}