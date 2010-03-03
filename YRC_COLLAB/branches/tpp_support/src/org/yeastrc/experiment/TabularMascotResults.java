/**
 * TabularMascotResults.java
 * @author Vagisha Sharma
 * Oct 9, 2009
 * @version 1.0
 */
package org.yeastrc.experiment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.SORT_BY;
import org.yeastrc.ms.domain.search.SORT_ORDER;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;
import org.yeastrc.www.misc.Pageable;
import org.yeastrc.www.misc.TableCell;
import org.yeastrc.www.misc.TableHeader;
import org.yeastrc.www.misc.TableRow;
import org.yeastrc.www.misc.Tabular;
import org.yeastrc.www.util.RoundingUtils;

/**
 * 
 */
public class TabularMascotResults implements Tabular, Pageable {

    private SORT_BY[] columns = new SORT_BY[] {
            SORT_BY.FILE_SEARCH,
            SORT_BY.SCAN, 
            SORT_BY.CHARGE, 
            SORT_BY.MASS, 
            SORT_BY.RT, 
            SORT_BY.MASCOT_RANK,
            SORT_BY.ION_SCORE, 
            SORT_BY.IDENTITY_SCORE, 
            SORT_BY.HOMOLOGY_SCORE, 
            SORT_BY.MASCOT_EXPECT, 
            SORT_BY.PEPTIDE,
            SORT_BY.PROTEIN
        };
        
        private SORT_BY sortColumn;
        private SORT_ORDER sortOrder = SORT_ORDER.ASC;
        
        
        private List<MascotResultPlus> results;
        
        private int currentPage;
        private int lastPage = currentPage;
        private List<Integer> displayPageNumbers;
        
        private RoundingUtils rounder = RoundingUtils.getInstance();
        
        public TabularMascotResults(List<MascotResultPlus> results, boolean useEvalue) {
            this.results = results;
            displayPageNumbers = new ArrayList<Integer>();
            displayPageNumbers.add(currentPage);
            
            rounder = RoundingUtils.getInstance();
        }
        
        
        @Override
        public int columnCount() {
            return columns.length;
        }

        public SORT_BY getSortedColumn() {
            return sortColumn;
        }
        
        public void setSortedColumn(SORT_BY column) {
            this.sortColumn = column;
        }
        
        public SORT_ORDER getSortOrder() {
            return sortOrder;
        }

        public void setSortOrder(SORT_ORDER sortOrder) {
            this.sortOrder = sortOrder;
        }
        
        @Override
        public List<TableHeader> tableHeaders() {
            List<TableHeader> headers = new ArrayList<TableHeader>(columns.length);
            for(SORT_BY col: columns) {
                TableHeader header = new TableHeader(col.getDisplayName(), col.name());
                if(col == sortColumn) {
                    header.setSorted(true);
                    header.setSortOrder(sortOrder);
                }
                if(col == SORT_BY.PROTEIN)
                    header.setSortable(false);
                headers.add(header);
            }
            return headers;
        }

        @Override
        public TableRow getRow(int index) {
            if(index >= results.size())
                return null;
            MascotResultPlus result = results.get(index);
            TableRow row = new TableRow();
            
            // row.addCell(new TableCell(String.valueOf(result.getId())));
            TableCell cell = new TableCell(result.getFilename());
            cell.setClassName("left_align");
            row.addCell(cell);
            row.addCell(new TableCell(String.valueOf(result.getScanNumber())));
            row.addCell(new TableCell(String.valueOf(result.getCharge())));
            row.addCell(new TableCell(String.valueOf(rounder.roundFour(result.getObservedMass()))));
            
            // Retention time
            BigDecimal temp = result.getRetentionTime();
            if(temp == null) {
                row.addCell(new TableCell(""));
            }
            else
                row.addCell(new TableCell(String.valueOf(rounder.roundFour(temp))));
            
            
            row.addCell(new TableCell(String.valueOf(result.getMascotResultData().getRank())));
            row.addCell(new TableCell(String.valueOf(rounder.roundFour(result.getMascotResultData().getIonScore()))));
            row.addCell(new TableCell(String.valueOf(result.getMascotResultData().getIdentityScore())));
            row.addCell(new TableCell(String.valueOf(result.getMascotResultData().getHomologyScore())));
            row.addCell(new TableCell(String.valueOf(result.getMascotResultData().getExpect())));
            
            String url = "viewSpectrum.do?scanID="+result.getScanId()+"&runSearchResultID="+result.getId();
            try {
                cell = new TableCell(String.valueOf(result.getResultPeptide().getFullModifiedPeptide()), url, true);
            }
            catch (ModifiedSequenceBuilderException e) {
                cell = new TableCell("Error building peptide sequence");
            }
            cell.setClassName("left_align");
            row.addCell(cell);
            
            String cellContents = result.getOneProtein();
            if(result.getProteinCount() > 1) {
                cellContents += " <span class=\"underline clickable\" "+
                "onClick=javascript:toggleProteins("+result.getId()+") "+
                ">("+result.getProteinCount()+")</span>";
                cellContents += " \n<div style=\"display: none;\" id=\"proteins_for_"+result.getId()+"\">"+result.getOtherProteinsHtml()+"</div>";
            }
            cell = new TableCell(cellContents);
            cell.setClassName("left_align");
            row.addCell(cell);
            return row;
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
}
