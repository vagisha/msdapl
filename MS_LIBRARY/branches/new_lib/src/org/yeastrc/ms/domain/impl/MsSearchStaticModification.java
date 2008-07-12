/**
 * MsSearchStaticModification.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.impl;


public class MsSearchStaticModification extends MsSearchModificationDbImpl {

    /**
     * Static modifications are not associated with a symbol. This method will return a null character '\u0000'
     */
    public char getModificationSymbol() {
        return nullCharacter;
    }

    public ModificationType getModificationType() {
        return ModificationType.STATIC;
    }

}