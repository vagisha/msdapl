/**
 * UserExperimentSearcher.java
 * @author Vagisha Sharma
 * Jan 30, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsSearchAnalysisDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.project.Project;

/**
 * 
 */
public class ExperimentSearcher {

    private static final ExperimentSearcher instance = new ExperimentSearcher();
    
    private ExperimentSearcher() {}
    
    public static ExperimentSearcher instance() {
        return instance;
    }
    
    public List<Integer> getSearchIdsForProject(Project project) throws SQLException {
        List<Project> list = new ArrayList<Project>();
        list.add(project);
        return getSearchIdsForProjects(list);
    }
    
    public List<Integer> getSearchIdsForProjects(List<Project> projects) throws SQLException {
        List<Integer> experimentIds = getExperimentIdsForProjects(projects);
        // get all the searches for the experiments
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        Set<Integer> searchIds = new HashSet<Integer>();
        for(int expId: experimentIds) {
            searchIds.addAll(searchDao.getSearchIdsForExperiment(expId));
        }
        return new ArrayList<Integer>(searchIds);
    }
    
    public List<Integer> getSearchAnalysisIdsForProject(Project project) throws SQLException {
        List<Project> list = new ArrayList<Project>();
        list.add(project);
        return getSearchAnalysisIdsForProjects(list);
    }
    
    public List<Integer> getSearchAnalysisIdsForProjects(List<Project> projects) throws SQLException {
        List<Integer> experimentIds = getExperimentIdsForProjects(projects);
        // get all the search analyses for the experiments
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        MsSearchAnalysisDAO saDao = DAOFactory.instance().getMsSearchAnalysisDAO();
        Set<Integer> searchAnalysisIds = new HashSet<Integer>();
        for(int expId: experimentIds) {
            // get all the search ids for this experiment
            List<Integer> searchIds = searchDao.getSearchIdsForExperiment(expId);
            // get all the search analysis ids for each search
            for(int searchId: searchIds) {
                searchAnalysisIds.addAll(saDao.getAnalysisIdsForSearch(searchId));
            }
        }
        return new ArrayList<Integer>(searchAnalysisIds);
    }
    
    private List<Integer> getExperimentIdsForProjects(List<Project> projects) throws SQLException {
        if(projects == null || projects.size() == 0) 
            return new ArrayList<Integer>(0);
        
        String projIdStr = "";
        for(Project proj: projects) {
            projIdStr += ","+proj.getID();
        }
        projIdStr = projIdStr.substring(1); // remove first comma
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            
            String sql = "SELECT experimentID FROM tblProjectExperiment WHERE projectID in ("+projIdStr+")";
                    
            conn = DBConnectionManager.getConnection( "yrc" );
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            List<Integer> experimentIds = new ArrayList<Integer>();
            while (rs.next()) {
                experimentIds.add( rs.getInt("experimentID"));
            }
            return experimentIds;
            
        } finally {
            
            if (rs != null) {
                try { rs.close(); rs = null; } catch (Exception e) { ; }
            }

            if (stmt != null) {
                try { stmt.close(); stmt = null; } catch (Exception e) { ; }
            }
            
            if (conn != null) {
                try { conn.close(); conn = null; } catch (Exception e) { ; }
            }           
        }
    }
}
