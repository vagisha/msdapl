/**
 * SequestSearchDbImpl.java
 * @author Vagisha Sharma
 * Aug 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.sequest.impl;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.impl.SearchBean;
import org.yeastrc.ms.domain.search.sequest.SequestParam;
import org.yeastrc.ms.domain.search.sequest.SequestSearch;


/**
 * 
 */
public class SequestSearchBean extends SearchBean implements SequestSearch {

    private List<SequestParam> paramList;
    
    public SequestSearchBean() {
        paramList = new ArrayList<SequestParam>();
    }
    
    @Override
    public List<SequestParam> getSequestParams() {
        return paramList;
    }
    
    public void setSequestParams(List<SequestParam> paramList) {
        this.paramList = paramList;
    }
}
