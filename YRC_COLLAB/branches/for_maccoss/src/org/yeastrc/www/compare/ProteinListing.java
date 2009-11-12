/**
 * CommonName.java
 * @author Vagisha Sharma
 * Apr 14, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 */
public class ProteinListing {

    private int nrseqProteinId;
    private Set<String> names;
    private Set<String> descriptions;
//    private List<ProteinNameDescription> nameDescr;
    
    public void setNameAndDescription(List<ProteinNameDescription> nameDescr) {
        names = new HashSet<String>(nameDescr.size() * 2);
        descriptions = new HashSet<String>(nameDescr.size() * 2);
        for(ProteinNameDescription nd: nameDescr) {
            if(nd.getName() != null)
                names.add(nd.getName());
            if(nd.getDescription() != null)
                descriptions.add(nd.getDescription());
        }
    }
    
    public int getNameCount() {
        return names.size();
    }
    
    public String getOneName() {
        for(String name: names) {
            if(names != null && name.trim().length() > 0)
                return name;
        }
        return null;
    }
    
    public String getAllNames() {
        return getAllNames(";");
    }
    
    public String getAllNames(String separator) {
        if(separator == null)
            separator = ";";
        StringBuilder buf = new StringBuilder();
        for(String name: names) {
            buf.append(separator);
            buf.append(name);
        }
        if(buf.length() > 0)
            buf.delete(0, separator.length());
        return buf.toString();
    }
    
    public String getName() {
        return getName(15, ";");
    }
    
    public String getName(int maxLength, String separator) {
        if(separator == null)
            separator = ";";
        
        StringBuilder buf = new StringBuilder();
        for(String name: names) {
            buf.append(separator);
            buf.append(name);
            if(buf.length() > maxLength+separator.length())
                break;
        }
        if(buf.length() > 0)
            buf.delete(0, separator.length());
        
        if(buf.length() > maxLength) {
            buf.delete(maxLength, buf.length());
            buf.append("...");
        }
        
        return buf.toString();
    }
    
    public String getDescription() {
        return getDescription(90, ", ");
    }
    
    public String getDescription(int maxLength, String separator) {
        if(separator == null)
            separator = "; ";
        
        StringBuilder buf = new StringBuilder();
        for(String desc: descriptions) {
            if(desc != null) {
                buf.append(separator);
                buf.append(desc);
                if(buf.length() > maxLength+separator.length())
                    break;
            }
        }
        if(buf.length() > 0)
            buf.delete(0, separator.length());
        
        if(buf.length() > maxLength) {
            buf.append("...");
        }
        
        return buf.toString();
    }
    
    public String getAllDescriptions(String separator) {
        if(separator == null)
            separator = "; ";
        StringBuilder buf = new StringBuilder();
        for(String desc: descriptions) {
            if(desc != null) {
                buf.append(separator);
                buf.append(desc);
            }
        }
        if(buf.length() > 0)
            buf.delete(0, separator.length());
        
        return buf.toString();
    }
    
    public String getAllDescriptions() {
        return getAllDescriptions("; ");
    }
    
    
    public int getNrseqProteinId() {
        return nrseqProteinId;
    }
    public void setNrseqProteinId(int nrseqProteinId) {
        this.nrseqProteinId = nrseqProteinId;
    }
    
}
