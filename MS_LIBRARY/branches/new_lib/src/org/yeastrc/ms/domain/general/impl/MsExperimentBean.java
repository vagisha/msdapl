/**
 * MsExperimentBean.java
 * @author Vagisha Sharma
 * Sep 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.general.impl;

import java.sql.Date;

import org.yeastrc.ms.domain.general.MsExperiment;

/**
 * 
 */
public class MsExperimentBean implements MsExperiment {

    private int id;
    private String serverAddress;
    private Date uploadDate;
    private Date lastUpdateDate;
    
    
    public void setId(int id) {
        this.id = id;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    @Override
    public String getServerAddress() {
        return serverAddress;
    }

    @Override
    public Date getUploadDate() {
        return uploadDate;
    }
}
