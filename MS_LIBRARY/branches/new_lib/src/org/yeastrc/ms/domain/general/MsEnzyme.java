/**
 * MsEnzymeDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.general;


/**
 * 
 */
public interface MsEnzyme extends MsEnzymeI {

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
     * @return the database id for the enzyme
     */
    public abstract int getId();
}
