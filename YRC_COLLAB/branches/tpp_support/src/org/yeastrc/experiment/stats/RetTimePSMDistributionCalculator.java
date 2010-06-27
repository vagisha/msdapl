/**
 * RetTimeDistributionGenerator.java
 * @author Vagisha Sharma
 * Nov 5, 2009
 * @version 1.0
 */
package org.yeastrc.experiment.stats;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.domain.search.Program;

import edu.uwpr.protinfer.util.TimeUtils;

/**
 * 
 */
public class RetTimePSMDistributionCalculator {

    
    private int analysisId;
    private double scoreCutoff;
    
    private int[] allPsmCounts;
    private int[] filteredPsmCounts;
    private double binSize;
    private static final int MAX_BINS = 50;
    private int numBins = MAX_BINS;
    private double maxRT;
    private int maxPsmCount = 0;
    
    private List<FileStats> fileStats;
    
    private static final Logger log = Logger.getLogger(RetTimePSMDistributionCalculator.class.getName());
    
    public RetTimePSMDistributionCalculator(int analysisId, double scoreCutoff) {
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
            // 1. RT distribution of all Percolator PSMs
            // 2. RT distribution of filtered Percolator PSM's (with given qvalue cutoff)
            
            // binUsingMsLib(scoreCutoff);
            binUsingJDBC(analysisId, scoreCutoff);
        }
        else {
            log.error("Don't know how to build RT distribution for analysis program: "+analysisProgram);
        }
    }

    private void binUsingJDBC(int analysisId, double scoreCutoff) {
        
        long s = System.currentTimeMillis();
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        MsRunSearchDAO runSearchDao = DAOFactory.instance().getMsRunSearchDAO();
        
        try {
            conn = DAOFactory.instance().getConnection();
            String sql = "SELECT scan.retentionTime, res.runSearchID, pres.qValue "+
                         "FROM msScan AS scan, msRunSearchAnalysis AS sa, PercolatorResult AS pres, msRunSearchResult AS res "+
                         "WHERE sa.searchAnalysisID="+analysisId+
                         " AND sa.id = pres.runSearchAnalysisID "+
                         "AND pres.resultID = res.id "+
                         "AND res.scanID = scan.id";
            
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            int lastRunSearchId = -1;
            int psmCnt = 0;
            int goodPsmCnt = 0;
            while(rs.next()) {
                
                double rt = rs.getBigDecimal("retentionTime").doubleValue();
                double qvalue = rs.getDouble("qvalue");
                int runSearchId = rs.getInt("runSearchID");
                
                if(lastRunSearchId != -1 && (lastRunSearchId != runSearchId)) {
                    String filename = runSearchDao.loadFilenameForRunSearch(lastRunSearchId);
                    FileStats stat = new FileStats(lastRunSearchId, filename);
                    stat.setTotalCount(psmCnt);
                    stat.setGoodCount(goodPsmCnt);
                    fileStats.add(stat);
                    
                    psmCnt = 0;
                    goodPsmCnt = 0;
                }
                psmCnt++;
                if(qvalue <= scoreCutoff)
                    goodPsmCnt++;
                
                lastRunSearchId = runSearchId;
                putInBin(rt, qvalue, scoreCutoff);
            }
            
            // last one
            String filename = runSearchDao.loadFilenameForRunSearch(lastRunSearchId);
            FileStats stat = new FileStats(lastRunSearchId, filename);
            stat.setTotalCount(psmCnt);
            stat.setGoodCount(goodPsmCnt);
            fileStats.add(stat);
        }
        catch(SQLException ex) {
            log.error("Error binning data",ex);
        }
        finally {
            if(conn != null) try {conn.close();} catch(SQLException e){}
            if(stmt != null) try {stmt.close();} catch(SQLException e){}
            if(rs != null)   try {rs.close();}   catch(SQLException e){}
        }
        
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
    
    public int getMaxPsmCount() {
        return this.maxPsmCount;
    }
    
    public int[] getAllPsmDistribution() {
        return allPsmCounts;
    }
    
    public int[] getFilteredPsmDistribution() {
        return filteredPsmCounts;
    }
    
    private void putInBin(double rt, double qvalue, double scoreCutoff) {
        
        int bin = (int)Math.round(rt / binSize);
        bin = bin == 0 ? bin : bin - 1;
        allPsmCounts[bin]++;
        
        maxPsmCount = Math.max(allPsmCounts[bin], maxPsmCount);
        
        if(qvalue <= scoreCutoff) {
            filteredPsmCounts[bin]++;
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
        
        allPsmCounts = new int[numBins];
        filteredPsmCounts = new int[numBins];
        
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
