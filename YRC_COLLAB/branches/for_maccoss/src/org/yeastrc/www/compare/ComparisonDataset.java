/**
 * ComparisonDataset.java
 * @author Vagisha Sharma
 * Apr 11, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.www.misc.Pageable;
import org.yeastrc.www.misc.ResultsPager;
import org.yeastrc.www.misc.TableCell;
import org.yeastrc.www.misc.TableHeader;
import org.yeastrc.www.misc.TableRow;
import org.yeastrc.www.misc.Tabular;

/**
 * 
 */
public class ComparisonDataset implements Tabular, Pageable {

    private List<Dataset> datasets;
    private List<ComparisonProtein> proteins;
    private int[][] proteinCounts;
    
    private int rowCount = 50;
    private int currentPage = 1;
    private int pageCount = 1;
    private List<Integer> displayPageNumbers;
    
    private int  getOffset() {
        return (this.currentPage - 1)*rowCount;
    }

    public void setRowCount(int count) {
        this.rowCount = count;
    }

    public ComparisonDataset() {
        this.datasets = new ArrayList<Dataset>();
        this.proteins = new ArrayList<ComparisonProtein>();
        this.displayPageNumbers = new ArrayList<Integer>();
    }

    public List<Dataset> getDatasets() {
        return datasets;
    }

    public List<ComparisonProtein> getProteins() {
        return proteins;
    }
    
    public void setDatasets(List<Dataset> datasets) {
        this.datasets = datasets;
    }
    
    public void addProtein(ComparisonProtein protein) {
        this.proteins.add(protein);
    }
    
    public int getDatasetCount() {
        return datasets.size();
    }
    
    public int getTotalProteinCount() {
        return proteins.size();
    }
    
    private void initProteinCounts() {
        
        proteinCounts = new int[datasets.size()][datasets.size()];
        for(int i = 0; i < datasets.size(); i++) {
            for(int j = 0; j < datasets.size(); j++)
                proteinCounts[i][j]++;
        }
        
        for(ComparisonProtein protein: proteins) {
            
            for(int i = 0; i < datasets.size(); i++) {
                
                Dataset dsi = datasets.get(i);
                if(protein.isInDataset(dsi)) {
                
                    proteinCounts[i][i]++;
                    
                    for(int j = i+1; j < datasets.size(); j++) {

                        Dataset dsj = datasets.get(j);
                        if(protein.isInDataset(dsj)) {
                            proteinCounts[i][j]++;
                            proteinCounts[j][i]++;
                        }
                    }
                }
            }
        }
    }
    
    public int getProteinCount(int datasetIndex) {
        
        if(proteinCounts == null) {
            initProteinCounts();
        }
        return proteinCounts[datasetIndex][datasetIndex];
    }

    public int getCommonProteinCount(int datasetIndex1, int datasetIndex2) {
        
        if(proteinCounts == null) {
            initProteinCounts();
        }
        return proteinCounts[datasetIndex1][datasetIndex2];
    }
    

    public int getCommonProteinsPerc(int datasetIndex1, int datasetIndex2) {
        
        if(proteinCounts == null) {
            initProteinCounts();
        }
        
        int ds1Count = proteinCounts[datasetIndex1][datasetIndex2];
        int ds2Count = proteinCounts[datasetIndex2][datasetIndex2];
        if(ds1Count > ds2Count && ds1Count > 0) {
            return calculatePercent(proteinCounts[datasetIndex1][datasetIndex2], ds1Count);
        }
        else if(ds2Count > 0) {
            return calculatePercent(proteinCounts[datasetIndex1][datasetIndex2], ds2Count);
        }
        return 0;
    }
    
    private static int calculatePercent(int num1, int num2) {
        return (int)((num1*100.0)/num2);
    }

    @Override
    public int columnCount() {
        return datasets.size() + 2;
    }

    @Override
    public TableRow getRow(int index) {
        
        ComparisonProtein protein = proteins.get(index + this.getOffset());
        
        TableRow row = new TableRow();
        TableCell protName = new TableCell(protein.getName());
        row.addCell(protName);
        
        TableCell protDescr = new TableCell();
        protDescr.setClassName("prot_descr");
        String descr = protein.getDescription();
        
        if(descr != null) {
            
            descr = descr.replaceAll("null", "");
            String[] tokens = descr.split(",");
            if(tokens.length > 0) {
                
                String myDescr = "";
                for(String token: tokens) {
                    if(token.trim().length() > 0) {
                        myDescr += ", "+token;
                    }
                }
                if(myDescr.length() > 0) {
                    myDescr = myDescr.substring(1);
                    if(myDescr.length() > 100) {
                        myDescr = myDescr.substring(0, 100)+"...";
                    }
                    protDescr.setData(myDescr);
                }
            }
        }
        row.addCell(protDescr);
        
        
        int dsIndex = 0;
        for(Dataset dataset: datasets) {
            DatasetProteinInformation dpi = protein.getDatasetProteinInformation(dataset);
            TableCell cell = new TableCell();
            cell.setId(String.valueOf(dsIndex));
            dsIndex++;
            
            if(dpi == null || !dpi.isPresent()) { // dataset does not contain this protein
                cell.setClassName("prot-not-found");
            }
            else {
                if(dpi.isParsimonious()) {
                    cell.setData("P");
                    cell.setClassName("prot-found prot-parsim");
                }
                else {
                    cell.setClassName("prot-found");
                }
            }
            row.addCell(cell);
        }
        
        return row;
    }

    @Override
    public int rowCount() {
        return Math.min(rowCount, this.getTotalProteinCount() - this.getOffset());
    }

    @Override
    public List<TableHeader> tableHeaders() {
        List<TableHeader> headers = new ArrayList<TableHeader>(columnCount());
        headers.add(new TableHeader("Protein"));
        headers.add(new TableHeader("Description"));
        for(Dataset dataset: datasets) {
            headers.add(new TableHeader(String.valueOf(dataset.getDatasetId())));
        }
        return headers;
    }

    @Override
    public void tabulate() {
        
        int max = Math.min((this.getOffset() + rowCount), this.getTotalProteinCount());
        
        for(int i = this.getOffset(); i < max; i++) {
            ComparisonProtein protein = proteins.get(i);
            String[] nameDescr = ProteinDatasetComparer.getProteinAccessionDescription(protein.getNrseqId(), true);
            protein.setName(nameDescr[0]);
            protein.setDescription(nameDescr[1]);
        }
    }

    public void setCurrentPage(int page) {
        this.currentPage = page;
        ResultsPager pager = ResultsPager.instance();
        this.pageCount = pager.getPageCount(this.proteins.size(), rowCount);
        this.displayPageNumbers = pager.getPageList(this.proteins.size(), currentPage, rowCount);
    }
    
    @Override
    public int getCurrentPage() {
        return currentPage;
    }

    @Override
    public List<Integer> getDisplayPageNumbers() {
        return this.displayPageNumbers;
    }

    @Override
    public int getLastPage() {
        return this.pageCount;
    }

    @Override
    public int getPageCount() {
        return this.pageCount;
    }
}
