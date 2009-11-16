/**
 * CommonNameLookup.java
 * @author Vagisha Sharma
 * Apr 14, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.nr_seq.NRProtein;
import org.yeastrc.nr_seq.NRProteinFactory;

/**
 * 
 */
public class CommonNameLookupUtil {

    private static CommonNameLookupUtil instance;
    
    private CommonNameLookupUtil() {}
    
    public static CommonNameLookupUtil getInstance() {
        if(instance == null)
            instance = new CommonNameLookupUtil();
        return instance;
    }

    public ProteinListing getProteinListing(int nrseqProteinId) throws Exception {
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        ProteinListing listing = new ProteinListing();
        listing.setNrseqProteinId(nrseqProteinId);
        
        try {
            conn = DAOFactory.instance().getConnection();
            String sql = "SELECT name, description FROM nrseqProteinCache WHERE proteinID="+nrseqProteinId+" ORDER BY name ";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            List<ProteinNameDescription> cndList = new ArrayList<ProteinNameDescription>();
            
            while(rs.next()) {
                String name = rs.getString("name");
                String description = rs.getString("description");
                ProteinNameDescription cnd = new ProteinNameDescription();
                cnd.setName(name);
                cnd.setDescription(description);
                cndList.add(cnd);
            }
            
            listing.setCommonNameDescription(cndList);
        }
        finally {
            if(conn != null) try {conn.close(); conn = null;}
            catch (SQLException e) {}
            if(stmt != null) try {stmt.close(); stmt = null;}
            catch (SQLException e) {}
            if(rs != null) try {rs.close(); rs = null;}
            catch (SQLException e) {}
        }
        
        // If we did not find anything in the cache table look for it the old way
        if(listing.getCommonNameDescription().size() == 0) {
          NRProteinFactory nrpf = NRProteinFactory.getInstance();
          NRProtein nrseqProt = null;
          nrseqProt = (NRProtein)(nrpf.getProtein(nrseqProteinId));
          ProteinNameDescription cnd = new ProteinNameDescription();
          cnd.setName(nrseqProt.getListing());
          cnd.setDescription(nrseqProt.getDescription());
          List<ProteinNameDescription> list = new ArrayList<ProteinNameDescription>();
          list.add(cnd);
          listing.setCommonNameDescription(list);
        }
        
        return listing;
    }
    
    public List<ProteinListing> getCommonListings(List<Integer> nrseqProteinIds) throws SQLException {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        List<ProteinListing> listings = new ArrayList<ProteinListing>(nrseqProteinIds.size());
        
        try {
            conn = DAOFactory.instance().getConnection();
            String sql = "SELECT name, description FROM nrseqProteinCache WHERE proteinID=? ORDER BY name ";
            stmt = conn.prepareStatement(sql);
            
            
            for(int nrseqProteinId: nrseqProteinIds) {
                stmt.setInt(1, nrseqProteinId);
                rs = stmt.executeQuery();
                ProteinListing listing = new ProteinListing();
                listing.setNrseqProteinId(nrseqProteinId);
                listings.add(listing);
                
                List<ProteinNameDescription> cndList = new ArrayList<ProteinNameDescription>();
                
                while(rs.next()) {
                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    ProteinNameDescription cnd = new ProteinNameDescription();
                    cnd.setName(name);
                    cnd.setDescription(description);
                    cndList.add(cnd);
                }
                
                listing.setCommonNameDescription(cndList);
                
                rs.close();
            }
        }
        finally {
            if(conn != null) try {conn.close();}
            catch (SQLException e) {}
            if(stmt != null) try {stmt.close();}
            catch (SQLException e) {}
            if(rs != null) try {rs.close();}
            catch (SQLException e) {}
        }
        
        return listings;
    }
    
    public List<Integer> getProteinIds(String commonName) throws SQLException {
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        List<Integer> proteinIds = new ArrayList<Integer>();
        
        try {
            conn = DAOFactory.instance().getConnection();
            String sql = "SELECT proteinID FROM YRC_NRSEQ.tblProteinDatabase WHERE accessionString LIKE '"+commonName+"%'";
            stmt = conn.createStatement();
            
            rs = stmt.executeQuery(sql);

            while(rs.next()) {
                proteinIds.add(rs.getInt(1));
            }
        }
        finally {
            if(conn != null) try {conn.close();}
            catch (SQLException e) {}
            if(stmt != null) try {stmt.close();}
            catch (SQLException e) {}
            if(rs != null) try {rs.close();}
            catch (SQLException e) {}
        }
        
        return proteinIds;
    }
}
