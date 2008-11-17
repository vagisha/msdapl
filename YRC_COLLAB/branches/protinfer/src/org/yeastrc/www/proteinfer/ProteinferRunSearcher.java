package org.yeastrc.www.proteinfer;

import java.util.List;

import org.yeastrc.ms.dao.search.MsRunSearchDAO;

import edu.uwpr.protinfer.database.dao.ProteinferRunDAO;

public class ProteinferRunSearcher {

    private ProteinferRunSearcher() {}
    
    public static List<Integer> getProteinferRunIdsForMsSearch(int msSearchId) {
        List<Integer> msRunSearchIds = getRunSearchIdsForMsSearch(msSearchId);
        return getProteinferRunIdsForRunSearches(msRunSearchIds);
    }
    
    private static List<Integer> getProteinferRunIdsForRunSearches(List<Integer> msRunSearchIds) {
        edu.uwpr.protinfer.database.dao.DAOFactory factory = edu.uwpr.protinfer.database.dao.DAOFactory.instance();
        ProteinferRunDAO runDao = factory.getProteinferRunDao();
        return runDao.getProteinferIdsForRunSearches(msRunSearchIds);
    }

    private static List<Integer> getRunSearchIdsForMsSearch(int msSearchId) {
        org.yeastrc.ms.dao.DAOFactory factory = org.yeastrc.ms.dao.DAOFactory.instance();
        MsRunSearchDAO runSearchDao = factory.getMsRunSearchDAO();
        return runSearchDao.loadRunSearchIdsForSearch(msSearchId);
    }
}
