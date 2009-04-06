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

import org.yeastrc.www.misc.Pageable;
import org.yeastrc.www.misc.TableCell;
import org.yeastrc.www.misc.TableRow;
import org.yeastrc.www.misc.Tabular;

public class TabularPercolatorResults implements Tabular, Pageable {

    
    private static String[] columns = new String[]{"Scan", "Charge", "Obs.Mass", "RT", "Predit. RT", "q-value", "PEP", "Peptide"}; 
    private String sortColumn;
    
    private List<PercolatorResultPlus> results;
    
    private int currentPage;
    private int lastPage = currentPage;
    private List<Integer> displayPageNumbers;
    
    public TabularPercolatorResults(List<PercolatorResultPlus> results) {
        this.results = results;
        displayPageNumbers = new ArrayList<Integer>();
        displayPageNumbers.add(currentPage);
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
        PercolatorResultPlus result = results.get(index);
        TableRow row = new TableRow();
        
        // row.addCell(new TableCell(String.valueOf(result.getId())));
        row.addCell(new TableCell(String.valueOf(result.getScanNumber())));
        row.addCell(new TableCell(String.valueOf(result.getCharge())));
        row.addCell(new TableCell(String.valueOf(round(result.getObservedMass()))));
        
        // Retention time
        BigDecimal temp = result.getRetentionTime();
        if(temp == null) {
            row.addCell(new TableCell("", null));
        }
        else
            row.addCell(new TableCell(String.valueOf(round(temp)), null));
        
        // Predicted retention time
        temp = result.getPredictedRetentionTime();
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
    public int getLastPage() {
        return this.lastPage;
    }
    
    public void setLastPage(int pageNum) {
        this.lastPage = pageNum;
    }

    @Override
    public int getPageCount() {
        return lastPage;
    }

    @Override
    public String sortedColumn() {
        return sortColumn;
    }
    
    public void setSortedColumn(String column) {
        this.sortColumn = column;
    }
    
}
