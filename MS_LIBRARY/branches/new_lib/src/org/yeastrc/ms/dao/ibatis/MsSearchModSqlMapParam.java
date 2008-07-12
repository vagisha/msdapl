/**
 * MsSearchModSqlMapParam.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.ibatis;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.MsSearchModification;

//-------------------------------------------------------------------------------------------
// Class for inserting data into the database.  
// The save methods will be passed an object of type MsSearchModification, along with 
// a searchId.  We have 3 options: 
// 1. Use inline parameters
// 2. Use a parameter map of type java.util.Map
// 3. Create a class that holds the MsSearchModification and searchId and use this as the
// param class.  We use the 3rd options because using a bean in a parameter map helps us 
// catch any mismatches between the bean property and sql param map when the map is loaded,
// rather than when the map is first used. With a java.util.Map iBatis has no way of detecting 
// a name mismatch since a Map is built at runtime rather than compile time. 
// Usnig a bean is also supposed to have better performance.
//-------------------------------------------------------------------------------------------
public class MsSearchModSqlMapParam {

    private int searchId;
    private char modResidue;
    private char modSymbol;
    private BigDecimal modMass;
    
    public MsSearchModSqlMapParam(int searchId, MsSearchModification mod) {
        this.searchId = searchId;
        this.modResidue = mod.getModifiedResidue();
        this.modSymbol = mod.getModificationSymbol();
        this.modMass = mod.getModificationMass();
    }
    
    public int getSearchId() {
        return searchId;
    }

    public BigDecimal getModificationMass() {
        return modMass;
    }

    public String getModificationSymbolString() {
        return Character.toString(modSymbol);
    }

    public String getModifiedResidueString() {
        return Character.toString(modResidue);
    }
}
