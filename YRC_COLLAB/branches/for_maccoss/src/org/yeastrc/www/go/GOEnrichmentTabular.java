/**
 * GOEnrichmentTabular.java
 * @author Vagisha Sharma
 * Jun 11, 2009
 * @version 1.0
 */
package org.yeastrc.www.go;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.www.misc.TableCell;
import org.yeastrc.www.misc.TableHeader;
import org.yeastrc.www.misc.TableRow;
import org.yeastrc.www.misc.Tabular;
import org.yeastrc.www.project.SORT_CLASS;

/**
 * 
 */
public class GOEnrichmentTabular implements Tabular {

    private String title;
    private int numProteinsInSet;
    private int numProteinsInUniverse;
    
    private List<EnrichedGOTerm> enrichedTerms;
    
    private List<TableHeader> headers;
    
    public GOEnrichmentTabular() {
        
        headers = new ArrayList<TableHeader>();
        TableHeader header = new TableHeader("GO Term");
        header.setSortable(true);
        header.setSortClass(SORT_CLASS.SORT_ALPHA);
        headers.add(header);
        
        header = new TableHeader("P-Value");
        header.setSortable(true);
        header.setSortClass(SORT_CLASS.SORT_FLOAT);
        headers.add(header);
        
        header = new TableHeader("Num. Annotated (in set)");
        header.setSortable(true);
        header.setSortClass(SORT_CLASS.SORT_INT);
        headers.add(header);
        
        header = new TableHeader("Total (in set)");
        header.setSortable(true);
        header.setSortClass(SORT_CLASS.SORT_INT);
        headers.add(header);
        
        header = new TableHeader("Num. Annotated (All)");
        header.setSortable(true);
        header.setSortClass(SORT_CLASS.SORT_INT);
        headers.add(header);
        
        header = new TableHeader("Total (All)");
        header.setSortable(true);
        header.setSortClass(SORT_CLASS.SORT_INT);
        headers.add(header);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNumProteinsInSet() {
        return numProteinsInSet;
    }

    public void setNumProteinsInSet(int numProteinsInSet) {
        this.numProteinsInSet = numProteinsInSet;
    }

    public int getNumProteinsInUniverse() {
        return numProteinsInUniverse;
    }

    public void setNumProteinsInUniverse(int numProteinsInUniverse) {
        this.numProteinsInUniverse = numProteinsInUniverse;
    }

    public List<EnrichedGOTerm> getEnrichedTerms() {
        return enrichedTerms;
    }

    public void setEnrichedTerms(List<EnrichedGOTerm> enrichedTerms) {
        this.enrichedTerms = enrichedTerms;
    }

    @Override
    public int columnCount() {
        return headers.size();
    }

    @Override
    public TableRow getRow(int index) {
        EnrichedGOTerm term = enrichedTerms.get(index);
        TableRow row = new TableRow();
        
        String pdrUrl = "http://www.yeastrc.org/pdr/viewGONode.do?acc="+term.getGoNode().getAccession();
        TableCell cell = new TableCell(term.getGoNode().getName(), pdrUrl, true, true); // absoulte url; opens in a new window
        row.addCell(cell);
        
        cell = new TableCell(term.getPValueString());
        row.addCell(cell);
        
        cell = new TableCell(""+term.getProteins().size());
        row.addCell(cell);

        cell = new TableCell(""+numProteinsInSet);
        row.addCell(cell);
        
        cell = new TableCell(""+term.totalAnnotatedProteins());
        row.addCell(cell);
        
        cell = new TableCell(""+numProteinsInUniverse);
        row.addCell(cell);
        
        return row;
    }

    @Override
    public int rowCount() {
        return enrichedTerms.size();
    }

    @Override
    public List<TableHeader> tableHeaders() {
        return headers;
    }

    @Override
    public void tabulate() {
        // nothing to do here
    }
    
    
    
}
