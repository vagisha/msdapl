/**
 * StatusTypeHandler.java
 * @author Vagisha Sharma
 * Jan 30, 2011
 */
package org.yeastrc.ms.dao.general.ibatis;

import java.sql.SQLException;

import org.yeastrc.ms.domain.general.Status;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * 
 */
public class StatusTypeHandler implements TypeHandlerCallback {

	public Object getResult(ResultGetter getter) throws SQLException {
        return stringToStatus(getter.getString());
    }

    public void setParameter(ParameterSetter setter, Object parameter)
            throws SQLException {
        Status status = (Status) parameter;
        if (status == null)
            setter.setNull(java.sql.Types.CHAR);
        else
            setter.setString(String.valueOf(status.getStatusChar()));
    }

    public Object valueOf(String s) {
        return stringToStatus(s);
    }
    
    private Status stringToStatus(String statusStr) {
        if (statusStr == null || statusStr.length() != 1)
            throw new IllegalArgumentException("Cannot convert "+statusStr+" to Status");
        Status status = Status.instance(Character.valueOf(statusStr.charAt(0)));
        if (status == null)
            throw new IllegalArgumentException("Invalid Status value: "+statusStr);
        return status;
    }
}
