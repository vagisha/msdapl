/**
 * ComparisonDataset.java
 * @author Vagisha Sharma
 * Apr 11, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.www.misc.Pageable;
import org.yeastrc.www.misc.ResultsPager;
import org.yeastrc.www.misc.TableCell;
import org.yeastrc.www.misc.TableHeader;
import org.yeastrc.www.misc.TableRow;
import org.yeastrc.www.misc.Tabular;

import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.idpicker.ibatis.IdPickerProteinBaseDAO;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerProteinBase;
import edu.uwpr.protinfer.util.ProteinUtils;

/**
 * 
 */
public class ProteinComparisonDataset implements Tabular, Pageable {

    private List<Dataset> datasets;
    private List<Integer> fastaDatabaseIds; // for protein name lookup
    
    // FILTERED proteins
    private List<ComparisonProtein> proteins;
    
    // counts BEFORE filtering
    private int[][] proteinCounts;
    private int totalProteinCount;
    
    private float minNormalizedSpectrumCount;
    private float maxNormalizedSpectrumCount;
    
    private int rowCount = 50;
    private int currentPage = 1;
    private int pageCount = 1;
    private List<Integer> displayPageNumbers;
    
    private boolean showFullDescriptions = false;
    
    private static final Logger log = Logger.getLogger(ProteinComparisonDataset.class.getName());
    


    public ProteinComparisonDataset() {
        this.datasets = new ArrayList<Dataset>();
        this.proteins = new ArrayList<ComparisonProtein>();
        this.displayPageNumbers = new ArrayList<Integer>();
    }
    
    public void setShowFullDescriptions(boolean show) {
        this.showFullDescriptions = show;
    }
    
    private int  getOffset() {
        return (this.currentPage - 1)*rowCount;
    }

    public void setRowCount(int count) {
        this.rowCount = count;
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
        return totalProteinCount;
    }
    
    public int getFilteredProteinCount() {
        return proteins.size();
    }
    
    public void setMinNormalizedSpectrumCount(int minNormalizedSpectrumCount) {
        this.minNormalizedSpectrumCount = minNormalizedSpectrumCount;
    }

    public void setMaxNormalizedSpectrumCount(int maxNormalizedSpectrumCount) {
        this.maxNormalizedSpectrumCount = maxNormalizedSpectrumCount;
    }
    
    public void initSummary() {
        initProteinCounts();
        this.totalProteinCount = proteins.size();
        calculateSpectrumCountNormalization();
        getMinMaxSpectrumCounts();
    }
    
    private void getMinMaxSpectrumCounts() {
        float minCount = Float.MAX_VALUE;
        float maxCount = 1.0f;
        for(Dataset dataset: datasets) {
            minCount = Math.min(minCount, dataset.getNormMinProteinSpectrumCount());
            maxCount = Math.max(maxCount, dataset.getNormMaxProteinSpectrumCount());
        }
        this.minNormalizedSpectrumCount = minCount;
        this.maxNormalizedSpectrumCount = maxCount;
    }

    private void calculateSpectrumCountNormalization() {
        Dataset maxDataset = null;
        for(Dataset dataset: datasets) {
            if(maxDataset == null || maxDataset.getSpectrumCount() < dataset.getSpectrumCount())
                maxDataset = dataset;
        }
        for(Dataset dataset: datasets) {
            float normFactor = (float)maxDataset.getSpectrumCount() / (float)dataset.getSpectrumCount();
            dataset.setSpectrumCountNormalizationFactor(normFactor);
        }
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
    

    /**
     * Fraction of dataset_1 proteins that were also found in dataset_2
     * @param datasetIndex1
     * @param datasetIndex2
     * @return
     */
    public float getCommonProteinsPerc(int datasetIndex1, int datasetIndex2) {
        
        if(proteinCounts == null) {
            initProteinCounts();
        }
        
        int ds1Count = proteinCounts[datasetIndex1][datasetIndex1];
        int commonCount = proteinCounts[datasetIndex1][datasetIndex2];
        
        if(ds1Count <= 0)
            return 0;
        return calculatePercent(commonCount, ds1Count);
    }
    
    private static float calculatePercent(int num1, int num2) {
        return (float) (Math.round(((float)(num1*100.0)/(float)num2) * 10.0)/10.0);
    }

    @Override
    public int columnCount() {
        return datasets.size() + 4;
    }

    @Override
    public TableRow getRow(int index) {
        
        ComparisonProtein protein = proteins.get(index + this.getOffset());
        
        TableRow row = new TableRow();
        
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
        
        // Peptide count
        TableCell peptCount = new TableCell(String.valueOf(protein.getMaxPeptideCount()));
        peptCount.setClassName("pept_count clickable underline");
        peptCount.setId(String.valueOf(protein.getNrseqId()));
        row.addCell(peptCount);
        
        // Protein 
        TableCell protName = new TableCell(protein.getShortFastaName());
        protName.setHyperlink("viewProtein.do?id="+protein.getNrseqId());
        row.addCell(protName);
        
        // Protein common name
        TableCell protCommonName = new TableCell(protein.getShortCommonName());
        row.addCell(protCommonName);
        
        // Protein molecular wt.
        TableCell molWt = new TableCell();
        molWt.setClassName("prot_descr");
        molWt.setData(protein.getMolecularWeight()+"");
        row.addCell(molWt);
        
        // Protein pI
        TableCell pi = new TableCell();
        pi.setClassName("prot_descr");
        pi.setData(protein.getPi()+"");
        row.addCell(pi);
        
        // Protein description
        TableCell protDescr = new TableCell();
        protDescr.setClassName("prot_descr");
        if(!showFullDescriptions)
            protDescr.setData(protein.getShortDescription());
        else
            protDescr.setData(protein.getDescription());
        row.addCell(protDescr);
        
        // Spectrum counts in each dataset
        for(Dataset dataset: datasets) {
            DatasetProteinInformation dpi = protein.getDatasetProteinInformation(dataset);
            TableCell cell = new TableCell();
            
            if(dpi == null || !dpi.isPresent()) { // dataset does not contain this protein
                cell.setClassName("prot-not-found");
            }
            else {
                String className = "prot-found";
                cell.setClassName(className);
                cell.setData(dpi.getSpectrumCount()+"("+dpi.getNormalizedSpectrumCountRounded()+")");
                cell.setTextColor("#FFFFFF");
                float scaledCount = getScaledSpectrumCount(dpi.getNormalizedSpectrumCount());
                cell.setBackgroundColor(getScaledColor(scaledCount));
            }
            row.addCell(cell);
        }
        
        return row;
    }
    
    private float getScaledSpectrumCount(float count) {
        return ((count - minNormalizedSpectrumCount + 1)/maxNormalizedSpectrumCount)*100.0f;
    }
    
    private String getScaledColor(float scaledSpectrumCount) {
        int rounded = (int)Math.ceil(scaledSpectrumCount);
        int green = 255;
        green -= 255.0/100.0 * rounded;
        int red = 0;
        red  += 255.0/100.0 * rounded;
        return "#"+hexValue(red, green, 0);
    }
    
    private String hexValue(int r, int g, int b) {
        String red = Integer.toHexString(r);
        if(red.length() == 1)
            red = "0"+red;
        String green = Integer.toHexString(g);
        if(green.length() == 1)
            green = "0"+green;
        String blue = Integer.toHexString(b);
        if(blue.length() == 1)
            blue = "0"+blue;
        return red+green+blue;
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
        return Math.min(rowCount, this.getFilteredProteinCount() - this.getOffset());
    }

    @Override
    public List<TableHeader> tableHeaders() {
        List<TableHeader> headers = new ArrayList<TableHeader>(columnCount());
        TableHeader header = null;
        
//        header = new TableHeader("ID");
//        header.setWidth(5);
//        header.setSortable(false);
//        headers.add(header);
        
        for(Dataset dataset: datasets) {
            header = new TableHeader(String.valueOf(dataset.getDatasetId()));
            header.setWidth(2);
            header.setSortable(false);
            headers.add(header);
        }
        
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
        
        header = new TableHeader("Mol. Wt.");
        header.setWidth(8);
        header.setSortable(false);
        headers.add(header);
        
        header = new TableHeader("pI");
        header.setWidth(5);
        header.setSortable(false);
        headers.add(header);
        
        header = new TableHeader("Description");
        header.setWidth(100 - (15 + datasets.size()*2));
        header.setSortable(false);
        headers.add(header);
        
        
        // spectrum counts
        for(Dataset dataset: datasets) {
            header = new TableHeader("SC("+dataset.getDatasetId()+")");
            header.setWidth(2);
            header.setSortable(false);
            headers.add(header);
        }
        
        return headers;
    }

    @Override
    public void tabulate() {
        
        int max = Math.min((this.getOffset() + rowCount), this.getFilteredProteinCount());
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
        
        // Get the spectrum count information for the protein in the different datasets
        ArrayList<Integer> nrseqIds = new ArrayList<Integer>(1);
        nrseqIds.add(protein.getNrseqId());
        for(DatasetProteinInformation dpi: protein.getDatasetInfo()) {
            if(dpi.getDatasetSource() == DatasetSource.PROT_INFER) {
                List<Integer> piProteinIds = idpProtDao.getProteinIdsForNrseqIds(dpi.getDatasetId(), nrseqIds);
                if(piProteinIds.size() == 1) {
                    IdPickerProteinBase prot = idpProtDao.loadProtein(piProteinIds.get(0));
                    dpi.setSpectrumCount(prot.getSpectrumCount());
                }
            }
        }
        
        // Get the NSAF information for the protein in the different datasets
        // NSAF is available only for ProteinInference proteins
        for(DatasetProteinInformation dpi: protein.getDatasetInfo()) {
            if(dpi.getDatasetSource() == DatasetSource.PROT_INFER) {
                List<Integer> piProteinIds = idpProtDao.getProteinIdsForNrseqIds(dpi.getDatasetId(), nrseqIds);
                if(piProteinIds.size() == 1) {
                    IdPickerProteinBase prot = idpProtDao.loadProtein(piProteinIds.get(0));
                    dpi.setNsaf(prot.getNsaf());
                }
            }
        }
        
//            // get the (max)number of peptides identified for this protein
//            protein.setMaxPeptideCount(DatasetPeptideComparer.instance().getMaxPeptidesForProtein(protein));
        
        // get the protein properties
        String sequence = NrSeqLookupUtil.getProteinSequence(protein.getNrseqId());
        protein.setMolecularWeight( (float) (Math.round(ProteinUtils.calculateMolWt(sequence)*100) / 100.0));
        protein.setPi((float) (Math.round(ProteinUtils.calculatePi(sequence)*100) / 100.0));
    }
    
    private String[] getProteinNames(int nrseqProteinId) {
        
        List<Integer> dbIds = getFastaDatabaseIds();
        ProteinListing fastaListing = FastaProteinLookupUtil.getInstance().getProteinListing(nrseqProteinId, dbIds);
        String accession = fastaListing.getAllNames();
        
        String description = null;
        // Look for a description for this protein from the given fasta database IDs.
        if(fastaListing.getAllDescriptions() != null && fastaListing.getAllDescriptions().length() > 0)
            description = fastaListing.getAllDescriptions();
        
        
        try {
            
            ProteinListing commonListing = CommonNameLookupUtil.getInstance().getProteinListing(nrseqProteinId);
            String commonName = commonListing.getAllNames();
            
            // If we haven't already found a description from the given fasta IDs, use the description
            // gathered from all descriptions available for this protein.
            if(description == null)
                description = commonListing.getAllDescriptions();
            
            return new String[] {accession, description, commonName};
        }
        catch (Exception e) {
            log.error("Exception getting accession/description for protein Id: "+nrseqProteinId, e);
        }
        return null;
    }
    
    List<Integer> getFastaDatabaseIds() {
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
