/**
 * PeptideComparisonDataset.java
 * @author Vagisha Sharma
 * Apr 15, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.www.misc.TableCell;
import org.yeastrc.www.misc.TableHeader;
import org.yeastrc.www.misc.TableRow;
import org.yeastrc.www.misc.Tabular;

/**
 * 
 */
public class PeptideComparisonDataset implements Tabular {

    
    private List<Dataset> datasets;
    private List<ComparisonPeptide> peptides = new ArrayList<ComparisonPeptide>();
    
    public PeptideComparisonDataset() {
        datasets = new ArrayList<Dataset>(0);
        peptides = new ArrayList<ComparisonPeptide>(0);
    }
    
    public List<ComparisonPeptide> getPeptides() {
        return peptides;
    }

    public void setPeptides(List<ComparisonPeptide> peptides) {
        this.peptides = peptides;
    }
    
    public List<Dataset> getDatasets() {
        return datasets;
    }

    public void setDatasets(List<Dataset> datasets) {
        this.datasets = datasets;
    }
    
    public int getDatasetCount() {
        return datasets.size();
    }

    @Override
    public int columnCount() {
        return datasets.size() + 1;
    }

    @Override
    public TableRow getRow(int index) {
        
        ComparisonPeptide peptide = peptides.get(index);
        TableRow row = new TableRow();
        
        row.addCell(new TableCell(peptide.getSequence()));
        for(Dataset ds: datasets) {
            TableCell cell = new TableCell();
            
            DatasetPeptideInformation dpi = peptide.getDatasetPeptideInformation(ds);
            if(dpi == null || !dpi.isPresent()) { // dataset does not contain this protein
                cell.setClassName("pept-not-found");
            }
            else {
                cell.setClassName("pept-found");
                if(dpi.isUnique()) {
                    cell.setData("U");
                    cell.setClassName("pept-found pept-parsim");
                }
            }
            row.addCell(cell);
        }
        return row;
    }

    @Override
    public int rowCount() {
        return peptides.size();
    }

    @Override
    public List<TableHeader> tableHeaders() {
        List<TableHeader> headers = new ArrayList<TableHeader>(columnCount());
        headers.add(new TableHeader("Sequence"));
        for(Dataset ds: datasets) {
            headers.add(new TableHeader(String.valueOf(ds.getDatasetId())));
        }
        return headers;
    }

    @Override
    public void tabulate() {
        // nothing to do here
    }

}
