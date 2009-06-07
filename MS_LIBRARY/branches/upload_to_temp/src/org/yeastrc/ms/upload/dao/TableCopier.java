/**
 * UploadDAO.java
 * @author Vagisha Sharma
 * Jun 2, 2009
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao;


/**
 * 
 */
public interface TableCopier {

    public abstract void copyToMainTable() throws TableCopyException;
}
