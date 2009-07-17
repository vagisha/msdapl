/**
 * ProteinProphetGroup.java
 * @author Vagisha Sharma
 * Jul 16, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.protinfer.proteinprophet;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class ProteinProphetGroup {

    private double probability;
    private int groupNumber;

    private List<ProteinProphetProtein> proteinList;
    
    
    public ProteinProphetGroup() {
        proteinList = new ArrayList<ProteinProphetProtein>();
    }
    
    public int getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(int groupNumber) {
        this.groupNumber = groupNumber;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public List<ProteinProphetProtein> getProteinList() {
        return proteinList;
    }

    public void addProtein(ProteinProphetProtein protein) {
        this.proteinList.add(protein);
    }
}
