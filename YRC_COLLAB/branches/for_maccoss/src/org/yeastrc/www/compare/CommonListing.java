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
public class CommonListing {

    private int nrseqProteinId;
    private List<CommonNameDescription> nameDescr;
    
    public void setCommonNameDescription(List<CommonNameDescription> nameDescr) {
        this.nameDescr = nameDescr;
    }
    
    public List<CommonNameDescription> getCommonNameDescription() {
        return nameDescr;
    }
    
    public String getOneName() {
        for(CommonNameDescription cnd: nameDescr) {
            if(cnd.getName() != null && cnd.getName().trim().length() > 0)
                return cnd.getName();
        }
        return null;
    }
    
    public String getName() {
        StringBuilder buf = new StringBuilder();
        for(CommonNameDescription cnd: nameDescr) {
            buf.append(", ");
            buf.append(cnd.getName());
            if(buf.length() > 50)
                break;
        }
        if(buf.length() > 0)
            buf.deleteCharAt(0);
        
        if(buf.length() > 50) {
            buf.delete(50, buf.length());
            buf.append("...");
        }
        
        
        return buf.toString();
    }
    
   
    public String getDescription() {
        StringBuilder buf = new StringBuilder();
        for(CommonNameDescription cnd: nameDescr) {
            buf.append(", ");
            buf.append(cnd.getDescription());
            if(buf.length() > 50)
                break;
        }
        if(buf.length() > 0)
            buf.deleteCharAt(0);
        
        if(buf.length() > 100) {
            buf.delete(100, buf.length());
            buf.append("...");
        }
        
        return buf.toString();
    }
    
    
    public int getNrseqProteinId() {
        return nrseqProteinId;
    }
    public void setNrseqProteinId(int nrseqProteinId) {
        this.nrseqProteinId = nrseqProteinId;
    }
    
}
