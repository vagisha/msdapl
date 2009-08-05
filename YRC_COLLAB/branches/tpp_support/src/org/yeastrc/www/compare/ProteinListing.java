/**
 * CommonName.java
 * @author Vagisha Sharma
 * Apr 14, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.List;

/**
 * 
 */
public class ProteinListing {

    private int nrseqProteinId;
    private List<ProteinNameDescription> nameDescr;
    
    public void setCommonNameDescription(List<ProteinNameDescription> nameDescr) {
        this.nameDescr = nameDescr;
    }
    
    public List<ProteinNameDescription> getCommonNameDescription() {
        return nameDescr;
    }
    
    public String getOneName() {
        for(ProteinNameDescription cnd: nameDescr) {
            if(cnd.getName() != null && cnd.getName().trim().length() > 0)
                return cnd.getName();
        }
        return null;
    }
    
    public String getAllNames() {
        return getAllNames(",");
    }
    
    public String getAllNames(String separator) {
        if(separator == null)
            separator = ", ";
        StringBuilder buf = new StringBuilder();
        for(ProteinNameDescription cnd: nameDescr) {
            buf.append(separator);
            buf.append(cnd.getName());
        }
        if(buf.length() > 0)
            buf.delete(0, separator.length());
        return buf.toString();
    }
    
    public String getName() {
        return getName(15, ", ");
    }
    
    public String getName(int maxLength, String separator) {
        if(separator == null)
            separator = ", ";
        
        StringBuilder buf = new StringBuilder();
        for(ProteinNameDescription cnd: nameDescr) {
            buf.append(separator);
            buf.append(cnd.getName());
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
        return getDescription(50, ", ");
    }
    
    public String getDescription(int maxLength, String separator) {
        if(separator == null)
            separator = ", ";
        
        StringBuilder buf = new StringBuilder();
        for(ProteinNameDescription cnd: nameDescr) {
            if(cnd.getDescription() != null) {
                buf.append(separator);
                buf.append(cnd.getDescription());
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
            separator = ", ";
        StringBuilder buf = new StringBuilder();
        for(ProteinNameDescription cnd: nameDescr) {
            if(cnd.getDescription() != null) {
                buf.append(separator);
                buf.append(cnd.getDescription());
            }
        }
        if(buf.length() > 0)
            buf.delete(0, separator.length());
        
        return buf.toString();
    }
    
    public String getAllDescriptions() {
        return getAllDescriptions(", ");
    }
    
    
    public int getNrseqProteinId() {
        return nrseqProteinId;
    }
    public void setNrseqProteinId(int nrseqProteinId) {
        this.nrseqProteinId = nrseqProteinId;
    }
    
}
