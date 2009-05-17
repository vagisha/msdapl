/**
 * ProteinGroupComparisonDataset.java
 * @author Vagisha Sharma
 * May 16, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.www.compare.graph.ComparisonProteinGroup;
import org.yeastrc.www.misc.Pageable;
import org.yeastrc.www.misc.ResultsPager;
import org.yeastrc.www.misc.TableCell;
import org.yeastrc.www.misc.TableHeader;
import org.yeastrc.www.misc.TableRow;
import org.yeastrc.www.misc.Tabular;

import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerProteinBaseDAO;

/**
 * 
 */
public class ProteinGroupComparisonDataset implements Tabular, Pageable {

    private List<Dataset> datasets;
    private List<Integer> fastaDatabaseIds; // for protein name lookup
    
    // FILTERED proteins
    private List<ComparisonProteinGroup> proteinGroups;
    
    private List<ComparisonProtein> proteins;
    
    // counts BEFORE filtering
    private int[][] proteinCounts;
    private int[][] proteinGroupCounts;
    
    private int rowCount = 50;
    private int currentPage = 1;
    private int pageCount = 1;
    private List<Integer> displayPageNumbers;
    
    private int currentGroupId = -1;  // used in the getRow method
    private Map<Integer, Integer> groupMemberCount;
    
    private String rowCssClass = "tr_even";
    
    private static final Logger log = Logger.getLogger(ProteinComparisonDataset.class.getName());
    
    private int  getOffset() {
        return (this.currentPage - 1)*rowCount;
    }

    public void setRowCount(int count) {
        this.rowCount = count;
    }

    public ProteinGroupComparisonDataset() {
        this.datasets = new ArrayList<Dataset>();
        this.proteinGroups = new ArrayList<ComparisonProteinGroup>();
        this.proteins = new ArrayList<ComparisonProtein>();
        groupMemberCount = new HashMap<Integer, Integer>();
        this.displayPageNumbers = new ArrayList<Integer>();
    }

    public List<Dataset> getDatasets() {
        return datasets;
    }

    public List<ComparisonProteinGroup> getProteinsGroups() {
        return proteinGroups;
    }
    
    public void setDatasets(List<Dataset> datasets) {
        this.datasets = datasets;
    }
    
    public void addProteinGroup(ComparisonProteinGroup proteinGroup) {
        this.proteinGroups.add(proteinGroup);
        for(ComparisonProtein protein: proteinGroup.getProteins())
            proteins.add(protein);
        groupMemberCount.put(proteinGroup.getGroupId(), proteinGroup.getProteins().size());
    }
    
    public int getDatasetCount() {
        return datasets.size();
    }
    
    public int getTotalProteinCount() {
        return proteins.size();
    }
    
    public int getTotalProteinGroupCount() {
        return proteinGroups.size();
    }
    
    public void initSummary() {
        initProteinCounts();
        initProteinGroupCounts();
    }
    
    private void initProteinCounts() {
        
        proteinCounts = new int[datasets.size()][datasets.size()];
        for(int i = 0; i < datasets.size(); i++) {
            for(int j = 0; j < datasets.size(); j++)
                proteinCounts[i][j] = 0;
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
    
    private void initProteinGroupCounts() {
        
        proteinGroupCounts = new int[datasets.size()][datasets.size()];
        for(int i = 0; i < datasets.size(); i++) {
            for(int j = 0; j < datasets.size(); j++)
                proteinGroupCounts[i][j] = 0;
        }
        
        for(ComparisonProteinGroup proteinGroup: proteinGroups) {
            
            for(int i = 0; i < datasets.size(); i++) {
                
                Dataset dsi = datasets.get(i);
                
                for(ComparisonProtein protein: proteinGroup.getProteins()) {
                    if(protein.isInDataset(dsi)) {

                        proteinGroupCounts[i][i]++;

                        for(int j = i+1; j < datasets.size(); j++) {

                            Dataset dsj = datasets.get(j);
                            if(protein.isInDataset(dsj)) {
                                proteinGroupCounts[i][j]++;
                                proteinGroupCounts[j][i]++;
                            }
                        }
                        break; 
                    }
                }
            }
        }
    }
    
    public int getCommonProteinCount(int datasetIndex1, int datasetIndex2) {
        
        if(proteinCounts == null) {
            initProteinCounts();
        }
        return proteinCounts[datasetIndex1][datasetIndex2];
    }
    
    public int getCommonProteinGroupCount(int datasetIndex1, int datasetIndex2) {
        
        if(proteinGroupCounts == null) {
            initProteinGroupCounts();
        }
        return proteinGroupCounts[datasetIndex1][datasetIndex2];
    }
    

    public int getProteinGroupCount(int datasetIndex) {
        
        if(proteinGroupCounts == null) {
            initProteinGroupCounts();
        }
        return proteinGroupCounts[datasetIndex][datasetIndex];
    }

    public int getProteinCount(int datasetIndex) {
        
        if(proteinCounts == null) {
            initProteinCounts();
        }
        return proteinCounts[datasetIndex][datasetIndex];
    }

    /**
     * Fraction of dataset_1 proteins that were also found in dataset_2
     * @param datasetIndex1
     * @param datasetIndex2
     * @return
     */
    public int getCommonProteinsPerc(int datasetIndex1, int datasetIndex2) {
        
        if(proteinCounts == null) {
            initProteinCounts();
        }
        
        int ds1Count = proteinCounts[datasetIndex1][datasetIndex1];
        int commonCount = proteinCounts[datasetIndex1][datasetIndex2];
        
        if(ds1Count <= 0)
            return 0;
        return calculatePercent(commonCount, ds1Count);
    }
    
    public int getCommonProteinGroupsPerc(int datasetIndex1, int datasetIndex2) {
        
        if(proteinGroupCounts == null) {
            initProteinGroupCounts();
        }
        
        int ds1Count = proteinGroupCounts[datasetIndex1][datasetIndex1];
        int commonCount = proteinGroupCounts[datasetIndex1][datasetIndex2];
        
        if(ds1Count <= 0)
            return 0;
        return calculatePercent(commonCount, ds1Count);
    }

    
    private static int calculatePercent(int num1, int num2) {
        return (int)((num1*100.0)/num2);
    }

    @Override
    public int columnCount() {
        return datasets.size() + 4;
    }

    @Override
    public TableRow getRow(int index) {
        
        ComparisonProtein protein = proteins.get(index + this.getOffset());
        
        TableRow row = new TableRow();
        
        // Protein ID
//        TableCell protId = new TableCell(String.valueOf(protein.getNrseqId()));
//        protId.setHyperlink("viewProtein.do?id="+protein.getNrseqId());
//        row.addCell(protId);
        
        if(currentGroupId == -1 || currentGroupId != protein.getGroupId()) {
            currentGroupId = protein.getGroupId();
            
            TableCell groupId = new TableCell(String.valueOf(currentGroupId));
            groupId.setRowSpan(groupMemberCount.get(currentGroupId));
            row.addCell(groupId);
            
            // Peptide count
            TableCell peptCount = new TableCell(String.valueOf(protein.getMaxPeptideCount()));
            peptCount.setRowSpan(groupMemberCount.get(currentGroupId));
            peptCount.setClassName("pept_count clickable underline");
            peptCount.setId(String.valueOf(protein.getNrseqId()));
            row.addCell(peptCount);
            
            rowCssClass = rowCssClass.equals("tr_even") ? "tr_odd" : "tr_even";
            
            row.setStyleClass(rowCssClass+" top_row ");
        }
        else
            row.setStyleClass(rowCssClass);
        
        // Protein name
        TableCell protName = new TableCell(protein.getFastaName());
        protName.setHyperlink("viewProtein.do?id="+protein.getNrseqId());
        row.addCell(protName);
        
        // Protein common name
        TableCell protCommonName = new TableCell(protein.getCommonName());
        row.addCell(protCommonName);
        
        // Protein description
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
        
        // Present / not present in each dataset
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
                String className = "prot-found";
                if(dpi.isParsimonious())
                    className += "  prot-parsim centered ";
                if(dpi.isGrouped()) {
                    className += " faded prot-group clickable ";
                    cell.setName(String.valueOf(protein.getNrseqId()));
                }
                
                cell.setClassName(className);
                
                if(dpi.isParsimonious()) {
                    cell.setData("*");
                }
            }
            row.addCell(cell);
        }
        
        return row;
    }

    private String getCommaSeparatedDatasetIds() {
        StringBuilder buf = new StringBuilder();
        for(Dataset dataset: datasets) {
            if(dataset.getSource() == DatasetSource.PROT_INFER)
                buf.append(","+dataset.getDatasetId());
        }
        if(buf.length() > 0)
            buf.deleteCharAt(0);
        return buf.toString();
    }
    
    @Override
    public int rowCount() {
        return Math.min(rowCount, this.getTotalProteinCount() - this.getOffset());
    }

    @Override
    public List<TableHeader> tableHeaders() {
        List<TableHeader> headers = new ArrayList<TableHeader>(columnCount());
        TableHeader header = null;
        
//        header = new TableHeader("ID");
//        header.setWidth(5);
//        header.setSortable(false);
//        headers.add(header);
        
        
        header = new TableHeader("GroupID");
        header.setWidth(10);
        header.setSortable(false);
        headers.add(header);
        
        header = new TableHeader("#Pept");
        header.setWidth(5);
        header.setSortable(false);
        headers.add(header);
        
        header = new TableHeader("Name");
        header.setWidth(10);
        header.setSortable(false);
        headers.add(header);
        
        header = new TableHeader("Common Name");
        header.setWidth(10);
        header.setSortable(false);
        headers.add(header);
        
        header = new TableHeader("Description");
        header.setWidth(100 - (15 + datasets.size()*2));
        header.setSortable(false);
        headers.add(header);
        
        for(Dataset dataset: datasets) {
            header = new TableHeader(String.valueOf(dataset.getDatasetId()));
            header.setWidth(2);
            header.setSortable(false);
            headers.add(header);
        }
        return headers;
    }

    @Override
    public void tabulate() {
        
        int max = Math.min((this.getOffset() + rowCount), this.getTotalProteinCount());
        initializeInfo(this.getOffset(), max);
    }
    
    public void initializeInfo(int startIndex, int endIndex) {
        
        for(int i = startIndex; i < endIndex; i++) {
            ComparisonProtein protein = proteins.get(i);
            initializeProteinInfo(protein);
        }
    }

    public void initializeProteinInfo(ComparisonProtein protein) {
        
        IdPickerProteinBaseDAO idpProtDao = ProteinferDAOFactory.instance().getIdPickerProteinBaseDao();
        // Get the common name and description
        String[] nameDescr = getProteinNames(protein.getNrseqId());
        protein.setFastaName(nameDescr[0]);
        protein.setCommonName(nameDescr[2]);
        protein.setDescription(nameDescr[1]);

        // Get the group information for the different datasets
        for(DatasetProteinInformation dpi: protein.getDatasetInfo()) {
            if(dpi.getDatasetSource() == DatasetSource.PROT_INFER) {
                boolean grouped = idpProtDao.isNrseqProteinGrouped(dpi.getDatasetId(), protein.getNrseqId());
                dpi.setGrouped(grouped);
            }
            // TODO for DTASelect
        }
    }
    
    private String[] getProteinNames(int nrseqProteinId) {
        
        List<Integer> dbIds = getFastaDatabaseIds();
        ProteinListing fastaListing = FastaProteinLookupUtil.getInstance().getProteinListing(nrseqProteinId, dbIds);
        String accession = fastaListing.getName();
        
        try {
            
            ProteinListing commonListing = CommonNameLookupUtil.getInstance().getProteinListing(nrseqProteinId);
            String commonName = commonListing.getName();
            String description = commonListing.getDescription(90, ", ");
            
            return new String[] {accession, description, commonName};
        }
        catch (Exception e) {
            log.error("Exception getting accession/description for protein Id: "+nrseqProteinId, e);
        }
        return null;
    }
    
    private List<Integer> getFastaDatabaseIds() {
        if(this.fastaDatabaseIds != null)
            return fastaDatabaseIds;
        else {
            fastaDatabaseIds = new ArrayList<Integer>();
            List<Integer> pinferIds = new ArrayList<Integer>();
            for(Dataset dataset: this.datasets)
                if(dataset.getSource() == DatasetSource.PROT_INFER)
                    pinferIds.add(dataset.getDatasetId());
            fastaDatabaseIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferIds);
            return fastaDatabaseIds;
        }
    }

    public void setCurrentPage(int page) {
        this.currentPage = page;
        ResultsPager pager = ResultsPager.instance();
        this.pageCount = pager.getPageCount(this.proteinGroups.size(), rowCount);
        this.displayPageNumbers = pager.getPageList(this.proteinGroups.size(), currentPage, rowCount);
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
