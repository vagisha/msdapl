/**
 * TabularResults.java
 * @author Vagisha Sharma
 * Apr 5, 2009
 * @version 1.0
 */
package org.yeastrc.experiment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.www.taglib.TableCell;
import org.yeastrc.www.taglib.TableRow;
import org.yeastrc.www.taglib.Tabular;

public class TabularPercolatorResults implements Tabular, Pageable {

    
    private static String[] columns = new String[]{"ID", "Scan", "Charge", "Obs.Mass", "RT", "q-value", "PEP", "Peptide"}; 
    
    private List<PercolatorResult> results;
    
    private int currentPage;
    private int firstPage = 1;
    private int lastPage = firstPage;
    private List<Integer> displayPageNumbers;
    
    public TabularPercolatorResults(List<PercolatorResult> results) {
        this.results = results;
        displayPageNumbers = new ArrayList<Integer>();
        displayPageNumbers.add(firstPage);
    }
    
    @Override
    public int columnCount() {
        return columns.length;
    }

    @Override
    public String[] columnNames() {
        return columns;
    }

    @Override
    public TableRow getRow(int index) {
        if(index >= results.size())
            return null;
        PercolatorResult result = results.get(index);
        TableRow row = new TableRow();
        
        row.addCell(new TableCell(String.valueOf(result.getId()), null));
        row.addCell(new TableCell(String.valueOf(result.getScanId()), null));
        row.addCell(new TableCell(String.valueOf(result.getCharge()), null));
        row.addCell(new TableCell(String.valueOf(round(result.getObservedMass())), null));
        BigDecimal temp = result.getPredictedRetentionTime();
        if(temp == null) {
            row.addCell(new TableCell("", null));
        }
        else
            row.addCell(new TableCell(String.valueOf(round(temp)), null));
        row.addCell(new TableCell(String.valueOf(result.getQvalueRounded()), null));
        row.addCell(new TableCell(String.valueOf(result.getPosteriorErrorProbabilityRounded()), null));
        
        row.addCell(new TableCell(String.valueOf(result.getResultPeptide().getModifiedPeptidePS()), null));
        
        return row;
    }
    
    private static double round(BigDecimal number) {
        return round(number.doubleValue());
    }
    private static double round(double num) {
        return Math.round(num*100.0)/100.0;
    }

    @Override
    public int rowCount() {
        return results.size();
    }

    @Override
    public void tabulate() {
        // nothing to do here?
    }

    @Override
    public int getCurrentPage() {
        return currentPage;
    }
    
    public void setCurrentPage(int pageNum) {
        this.currentPage = pageNum;
    }

    @Override
    public List<Integer> getDisplayPageNumbers() {
        return this.displayPageNumbers;
    }
    
    public void setDisplayPageNumbers(List<Integer> pageNums) {
        this.displayPageNumbers = pageNums;
    }

    @Override
    public int getFirstPage() {
        return this.firstPage;
    }
    
    public void setFirstPage(int pageNum) {
        this.firstPage = pageNum;
    }

    @Override
    public int getLastPage() {
        return this.lastPage;
    }
    
    public void setLastPage(int pageNum) {
        this.lastPage = pageNum;
    }
}
