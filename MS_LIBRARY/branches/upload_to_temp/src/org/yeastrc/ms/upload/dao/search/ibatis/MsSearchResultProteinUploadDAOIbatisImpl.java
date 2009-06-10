/**
 * MsSearchResultProteinUploadDAOIbatisImpl.java
 * @author Vagisha Sharma
 * Jul 3, 2008
 * @version 1.0
 */
package org.yeastrc.ms.upload.dao.search.ibatis;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.upload.dao.search.MsSearchResultProteinUploadDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MsSearchResultProteinUploadDAOIbatisImpl extends BaseSqlMapDAO implements MsSearchResultProteinUploadDAO {


    public MsSearchResultProteinUploadDAOIbatisImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    
    @Override
    public void saveAll(List<MsSearchResultProtein> proteinMatchList) {
        if (proteinMatchList.size() == 0)
            return;
        StringBuilder values = new StringBuilder();
        for (MsSearchResultProtein match: proteinMatchList) {
            values.append(",(");
            values.append(match.getResultId() == 0 ? "NULL" : match.getResultId());
            values.append(",");
            boolean hasAcc = match.getAccession() != null;
            if (hasAcc) values.append("\"");
            values.append(match.getAccession());
            if (hasAcc) values.append("\"");
            values.append(")");
        }
        values.deleteCharAt(0);
        
        save("MsResultProtein.insertAll", values.toString());
    }
    
    @Override
    public void disableKeys() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = super.getConnection();
            String sql = "ALTER TABLE msProteinMatch DISABLE KEYS";
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        }
        finally {
            try {if(conn != null) conn.close();}
            catch(SQLException e){}
            try {if(stmt != null) stmt.close();}
            catch(SQLException e){}
        }
    }

    @Override
    public void enableKeys() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = super.getConnection();
            String sql = "ALTER TABLE msProteinMatch ENABLE KEYS";
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        }
        finally {
            try {if(conn != null) conn.close();}
            catch(SQLException e){}
            try {if(stmt != null) stmt.close();}
            catch(SQLException e){}
        }
    }
}


