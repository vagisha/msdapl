package org.yeastrc.ms.domain;

public interface IMsEnzyme {

    public static enum Sense {
        
        CTERM((byte)0), NTERM((byte)1), UNKNOWN((byte)-1);
    
        private byte byteVal;
        private Sense(byte byteVal) {
            this.byteVal = byteVal;
        }
        public byte getByteVal() {
            return byteVal;
        }
        public static Sense getSenseForByteVal(byte byteVal) {
            switch (byteVal) {
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
     * Example: KR
     * @return 
     */
    public abstract String getCut();

    /**
     * Amino acid(s), which when present next to the cleavage site inhibit enzyme action.
     * @return the nocut
     */
    public abstract String getNocut();

    /**
     * @return the description
     */
    public abstract String getDescription();

}