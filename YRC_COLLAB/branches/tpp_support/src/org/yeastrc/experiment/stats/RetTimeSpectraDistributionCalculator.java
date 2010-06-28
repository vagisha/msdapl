/**
 * RetTimeSpectraDistributionCalculator.java
 * @author Vagisha Sharma
 * Dec 31, 2009
 * @version 1.0
 */
package org.yeastrc.experiment.stats;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.Program;

import edu.uwpr.protinfer.util.TimeUtils;

/**
 * 
 */
public class RetTimeSpectraDistributionCalculator {

    private int analysisId;
    private double scoreCutoff;
    
    private int[] allSpectraCounts;
    private int[] filteredSpectraCounts;
    private double binSize;
    private static final int MAX_BINS = 50;
    private int numBins = MAX_BINS;
    private double maxRT;
    private int maxSpectraCount = 0;
    
    private List<FileStats> fileStats;
    
    private static final Logger log = Logger.getLogger(RetTimeSpectraDistributionCalculator.class.getName());
    
    public RetTimeSpectraDistributionCalculator(int analysisId, double scoreCutoff) {
        this.analysisId = analysisId;
        this.scoreCutoff = scoreCutoff;
        fileStats = new ArrayList<FileStats>();
    }
    
    public List<FileStats> getFileStats() {
        return fileStats;
    }
    
    public double getScoreCutoff() {
        return this.scoreCutoff;
    }
    
    
    public void calculate() {
        
        Program analysisProgram = DAOFactory.instance().getMsSearchAnalysisDAO().load(analysisId).getAnalysisProgram();
        
        initBins();
        
        if(analysisProgram == Program.PERCOLATOR) {
            
            // we will calculate two things: 
            // 1. RT distribution of all acquired MS/MS spectra for the analysis
            // 2. RT distribution of spectra with IDs >= given qvalue cutoff
            
            MsRunSearchDAO rsDao = DAOFactory.instance().getMsRunSearchDAO();
            
            List<Integer> searchIds = DAOFactory.instance().getMsSearchAnalysisDAO().getSearchIdsForAnalysis(analysisId);
            
            long s = System.currentTimeMillis();
            log.info("Binning data..");
            
            for(int searchId: searchIds) {
                
                List<Integer> runSearchIds = rsDao.loadRunSearchIdsForSearch(searchId);
                log.info("SearchID: "+searchId+" has "+runSearchIds.size()+" runSearches");
                
                Collections.sort(runSearchIds);
                for(int runSearchId: runSearchIds) {
                    // binUsingMsLib(scoreCutoff, runSearchId);
                    binUsingJDBC(scoreCutoff, runSearchId, analysisId);
                }
            }
            
            long e = System.currentTimeMillis();
            log.info("Binned data in: "+TimeUtils.timeElapsedSeconds(s, e)+"seconds");
        }
        else {
            log.error("Don't know how to build RT distribution for analysis program: "+analysisProgram);
        }
    }

    private void binUsingJDBC(double scoreCutoff, int runSearchId, int analysisId) {
        
        long s = System.currentTimeMillis();
        
        MsRunSearchDAO rsDao = DAOFactory.instance().getMsRunSearchDAO();
        MsRunSearch rsearch = rsDao.loadRunSearch(runSearchId);
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        int scanCnt = 0;
        int goodScanCnt = 0;
        
        try {
            conn = DAOFactory.instance().getConnection();
            String sql = "SELECT scan.id, scan.retentionTime, pres.qvalue "+
                         "FROM msScan AS scan "+
                         "LEFT JOIN (msRunSearchResult AS res, PercolatorResult AS pres, msRunSearchAnalysis AS sa) "+
                         "ON scan.id = res.scanID "+
                         "WHERE scan.runID="+rsearch.getRunId()+" "+
                         "AND res.runSearchID="+rsearch.getId()+" "+
                         "AND pres.resultID = res.id "+
                         "AND pres.runSearchAnalysisID = sa.id "+
                         "AND sa.searchAnalysisID="+analysisId+" "+
                         "ORDER BY id,qvalue ASC";
            
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            int lastScan = -1;
            
            while(rs.next()) {
                int scanId = rs.getInt("id");
                if(scanId == lastScan)
                    continue;
                lastScan = scanId;
                
                double rt = rs.getBigDecimal("retentionTime").doubleValue();
                double qvalue = rs.getDouble("qvalue");
                putScanInBin(rt, (qvalue <= scoreCutoff));
                
                scanCnt++;
                if(qvalue <= scoreCutoff)
                    goodScanCnt++;
            }
        }
        catch(SQLException ex) {
            log.error("Error binning data",ex);
        }
        finally {
            if(conn != null) try {conn.close();} catch(SQLException e){}
            if(stmt != null) try {stmt.close();} catch(SQLException e){}
            if(rs != null)   try {rs.close();}   catch(SQLException e){}
        }
        
        String filename = DAOFactory.instance().getMsRunSearchDAO().loadFilenameForRunSearch(runSearchId);
        FileStats file = new FileStats(runSearchId, filename);
        fileStats.add(file);
        
        file.setTotalCount(scanCnt);
        file.setGoodCount(goodScanCnt);
        
        long e = System.currentTimeMillis();
        log.info("Binned data in: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
    }


    public int getNumBins() {
        return numBins;
    }
    
    public double getBinSize() {
        return binSize;
    }
    
    public double getMaxRT() {
        return this.maxRT;
    }
    
    public int getMaxSpectraCount() {
        return this.maxSpectraCount;
    }
    
    public int[] getAllSpectraDistribution() {
        return allSpectraCounts;
    }
    
    public int[] getFilteredSpectraDistribution() {
        return filteredSpectraCounts;
    }
    
    private void putScanInBin(double rt, boolean isFiltered) {
        int bin = (int)Math.round(rt / binSize);
        bin = bin == 0 ? bin : bin - 1;
        allSpectraCounts[bin]++;
        
        maxSpectraCount = Math.max(allSpectraCounts[bin], maxSpectraCount);
        
        if(isFiltered) {
            filteredSpectraCounts[bin]++;
        }
    }
    
    private void initBins() {
        
        long s = System.currentTimeMillis();
        log.info("Initializing bins...");
        // get the runIDs for this analysis
        List<Integer> searchIds = DAOFactory.instance().getMsSearchAnalysisDAO().getSearchIdsForAnalysis(analysisId);
        List<Integer> runIds = new ArrayList<Integer>();
        for(int searchId: searchIds) {
            int experimentId = DAOFactory.instance().getMsSearchDAO().loadSearch(searchId).getExperimentId();
            List<Integer> rIds = DAOFactory.instance().getMsExperimentDAO().getRunIdsForExperiment(experimentId);
            runIds.addAll(rIds);
        }
        
        // get max RT and create bins
        this.maxRT = DAOFactory.instance().getMsRunDAO().getMaxRetentionTimeForRuns(runIds);
        if(maxRT <= MAX_BINS) {
            binSize = 1.0;
            numBins = (int)Math.ceil(maxRT);
        }
        else {
            binSize = maxRT / (double)MAX_BINS;
            numBins = MAX_BINS;
        }
        
        allSpectraCounts = new int[numBins];
        filteredSpectraCounts = new int[numBins];
        
        long e = System.currentTimeMillis();
        log.info("Initialized bins in "+TimeUtils.timeElapsedSeconds(s, e)+"seconds");
    }
    
    public static void main(String[] args) {
        int analysisId = 91;
        
        RetTimePSMDistributionCalculator dg = new RetTimePSMDistributionCalculator(analysisId, 0.01);
        dg.calculate();
        System.out.println("# Bins: "+dg.getNumBins());
        System.out.println("Bin size: "+dg.getBinSize());
        System.out.println("Distribution: ");
        int[] distr = dg.getFilteredPsmDistribution();
        int[] allDistr = dg.getAllPsmDistribution();
        
        double halfBin = dg.getBinSize() / 2.0;
        
        for(int i = 0; i < distr.length; i++) {
            double binMid = (i * dg.getBinSize()) + halfBin;
            System.out.println(i+"\t"+binMid+"\t"+distr[i]+"\t"+allDistr[i]);
        }
    }
}
