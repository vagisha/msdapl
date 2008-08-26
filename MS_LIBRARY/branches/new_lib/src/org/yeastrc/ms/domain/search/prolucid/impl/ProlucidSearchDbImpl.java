/**
 * ProlucidSearchDbImpl.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.prolucid.impl;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.impl.MsSearchDbImpl;
import org.yeastrc.ms.domain.search.prolucid.ProlucidParamDb;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchDb;

/**
 * 
 */
public class ProlucidSearchDbImpl extends MsSearchDbImpl implements ProlucidSearchDb {

    private List<ProlucidParamDb> paramList;

    public ProlucidSearchDbImpl() {
        paramList = new ArrayList<ProlucidParamDb>();
    }

    @Override
    public List<ProlucidParamDb> getProlucidParams() {
        return paramList;
    }

    public void setProlucidParams(List<ProlucidParamDb> paramList) {
        this.paramList = paramList;
    }

}
