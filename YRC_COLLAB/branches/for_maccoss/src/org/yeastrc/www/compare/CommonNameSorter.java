/**
 * CommonNameSorter.java
 * @author Vagisha Sharma
 * Apr 14, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.db.DBConnectionManager;

/**
 * 
 */
public class CommonNameSorter {

    private static CommonNameSorter instance;
    
    private CommonNameSorter() {}
    
    public static CommonNameSorter instance () {
        if(instance == null)
            instance = new CommonNameSorter();
        return instance;
    }
    
    public List<CommonListing> sortNrseqIds(List<Integer> nrseqIds) {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        List<CommonListing> listings = new ArrayList<CommonListing>(nrseqIds.size());
        CommonNameLookupUtil lookupUtil = CommonNameLookupUtil.instance();
        
        try {
            conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
            String sql = "SELECT name, description from nrseqProteinCache WHERE proteinID=? ORDER BY name"; // We may have multiple entries for one protein Id
            stmt = conn.prepareStatement(sql);
            
            for(int id: nrseqIds) {
                
            }
            
            Collections.sort(listings, new NameCompartor());
            return listings;
        }
        finally {
            if(conn != null) try {
                conn.close();
            }
            catch (SQLException e) {}
            if(stmt != null) try {
                stmt.close();
            }
            catch (SQLException e) {}
            if(rs != null) try {
                rs.close();
            }
            catch (SQLException e) {}
        }
    }
    
    private static class NameCompartor implements Comparator<CommonListing> {

        @Override
        public int compare(CommonListing o1, CommonListing o2) {
            if(o1.getName() != null && o2 != null)
                return o1.getName().compareTo(o2.getName());
            else if(o1 != null)
                return -1;
            else
                return 1;
        }
        
    }
}
