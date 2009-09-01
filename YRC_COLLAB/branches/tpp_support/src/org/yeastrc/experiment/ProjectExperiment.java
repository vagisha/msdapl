/**
 * MsExperiment.java
 * @author Vagisha Sharma
 * Apr 2, 2009
 * @version 1.0
 */
package org.yeastrc.experiment;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.domain.general.MsExperiment;
import org.yeastrc.www.misc.TableCell;
import org.yeastrc.www.misc.TableHeader;
import org.yeastrc.www.misc.TableRow;
import org.yeastrc.www.misc.Tabular;
import org.yeastrc.www.project.SORT_CLASS;
import org.yeastrc.yates.YatesRun;


/**
 * 
 */
public class ProjectExperiment implements MsExperiment, Comparable<ProjectExperiment>, Tabular {

    private final MsExperiment experiment;
    private List<MsFile> ms1Files;
    private List<MsFile> ms2Files;
    private List<ExperimentSearch> searches;
    private List<SearchAnalysis> analyses;
    private YatesRun dtaSelect;
    private List<ExperimentProteinProphetRun> prophetRuns;
    private List<ExperimentProteinferRun> protInferRuns;
    
    private List<TableRow> rows;
    
    private boolean uploadSuccess = true;
    private int uploadJobId;
    
    public int getUploadJobId() {
        return uploadJobId;
    }

    public void setUploadJobId(int uploadJobId) {
        this.uploadJobId = uploadJobId;
    }

    public boolean isUploadSuccess() {
        return uploadSuccess;
    }

    public void setUploadSuccess(boolean uploadSuccess) {
        this.uploadSuccess = uploadSuccess;
    }

    public ProjectExperiment(MsExperiment experiment) {
        this.experiment = experiment;
    }

    @Override
    public int getId() {
        return experiment.getId();
    }

    @Override
    public Timestamp getLastUpdateDate() {
        return experiment.getLastUpdateDate();
    }

    @Override
    public String getServerAddress() {
        return experiment.getServerAddress();
    }

    @Override
    public String getServerDirectory() {
        return experiment.getServerDirectory();
    }

    public String getComments() {
        return experiment.getComments();
    }

    public List<MsFile> getMs2Files() {
        return ms2Files;
    }

    public void setMs2Files(List<MsFile> ms2Files) {
        this.ms2Files = ms2Files;
    }
    
    public List<MsFile> getMs1Files() {
        return ms1Files;
    }

    public void setMs1Files(List<MsFile> ms1Files) {
        this.ms1Files = ms1Files;
    }
    
    public List<ExperimentSearch> getSearches() {
        return searches;
    }

    public void setSearches(List<ExperimentSearch> searches) {
        this.searches = searches;
    }

    public List<SearchAnalysis> getAnalyses() {
        return analyses;
    }

    public void setAnalyses(List<SearchAnalysis> analyses) {
        this.analyses = analyses;
    }

    public List<ExperimentProteinProphetRun> getProteinProphetRuns() {
        return prophetRuns;
    }
    
    public void setProteinProphetRun(List<ExperimentProteinProphetRun> runs) {
        this.prophetRuns = runs;
    }
    
    public YatesRun getDtaSelect() {
        return dtaSelect;
    }

    public void setDtaSelect(YatesRun dtaSelect) {
        this.dtaSelect = dtaSelect;
    }

    @Override
    public Date getUploadDate() {
        return experiment.getUploadDate();
    }

    @Override
    public int compareTo(ProjectExperiment o) {
        if(o == null)
            return -1;
        return Integer.valueOf(experiment.getId()).compareTo(o.getId());
    }

    @Override
    public void tabulate() {
        
        rows = new ArrayList<TableRow>(ms2Files.size());
        int colCount = columnCount();
        
        for(MsFile file: ms2Files) {
            TableRow row = new TableRow();
            TableCell cell = new TableCell(file.getFileName(), null);
            cell.setClassName("left_align");
            row.addCell(cell);
            rows.add(row);
        }
        
        FileComparator comparator = new FileComparator();
        
        if(ms1Files != null) {
            Collections.sort(ms1Files,comparator);
            int r = 0;
            for(MsFile file: ms1Files) {
                TableRow row = rows.get(r);
                row.addCell(new TableCell(String.valueOf(file.getScanCount()), null));
                r++;
            }
        }
        
        Collections.sort(ms2Files, comparator);
        int r = 0;
        for(MsFile file: ms2Files) {
            TableRow row = rows.get(r);
            row.addCell(new TableCell(String.valueOf(file.getScanCount()), null));
            r++;
        }
        
        // iterate over the searches
        for(ExperimentSearch search: searches) {
            List<SearchFile> files = search.getFiles();
            Collections.sort(files, comparator);
            
            String action = null;
            //if(search.getSearchProgram() == Program.SEQUEST)
                //action = "viewSequestResults.do";
            
            int j = 0;
            for(r = 0; r < rows.size(); r++) {
                TableRow row = rows.get(r);
                SearchFile file = files.get(j);
                if(file.getFileName().equals(row.getCells().get(0).getData())) {
                    String url = null;
                    if(action != null)
                        url = action+"?ID="+file.getId();
                    TableCell cell = new TableCell(String.valueOf(file.getNumResults()),
                            url);
                    row.addCell(cell);
                    j++;
                }
            }
        }
        
        // iterate over the analyses
        for(SearchAnalysis analysis: analyses) {
            List<AnalysisFile> files = analysis.getFiles();
            Collections.sort(files, comparator);
            
            String action = null;
            //if(analysis.getAnalysisProgram() == Program.PERCOLATOR)
             //   action = "viewPercolatorResults.do";
            
            int j = 0;
            for(r = 0; r < rows.size(); r++) {
                TableRow row = rows.get(r);
                AnalysisFile file = files.get(j);
                if(file.getFileName().equals(row.getCells().get(0).getData())) {
                    String url = null;
                    if(action != null)
                        url  = action + "?ID="+file.getId();
                    TableCell cell = new TableCell(String.valueOf(file.getNumResults()),
                            url);
                    row.addCell(cell);
                    j++;
                }
            }
        }
    }
    
    private static class FileComparator implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            if(o1 == o2)    return 0;
            if(o1 == null)  return 1;
            if(o2 == null)  return -1;
            return o1.getFileName().compareTo(o2.getFileName());
        }
    }
    
    @Override
    public int columnCount() {
        // first column is filename
        // second column is # ms2 spectra
        int count = searches.size()+analyses.size();
        count++;  // filename
        count++;  // ms2 file
        if(ms1Files != null)
            count++;
        return count;
    }

    @Override
    public int rowCount() {
        return ms2Files.size();
    }
    
    @Override
    public TableRow getRow(int row) {
        return rows.get(row);
    }

    @Override
    public List<TableHeader> tableHeaders() {
        List<TableHeader> headers = new ArrayList<TableHeader>(columnCount());
        
        TableHeader header = new TableHeader("File");
        header.setSortClass(SORT_CLASS.SORT_ALPHA);
        headers.add(header);
        
        if(ms1Files != null) {
            header = new TableHeader("# MS1 Scans");
            header.setSortClass(SORT_CLASS.SORT_INT);
            headers.add(header);
        }
        
        header = new TableHeader("# MS2 Scans");
        header.setSortClass(SORT_CLASS.SORT_INT);
        headers.add(header);
        
        for(ExperimentSearch search: searches){
            header = new TableHeader(search.getSearchProgram().displayName());
            header.setSortClass(SORT_CLASS.SORT_INT);
            headers.add(header);
        }
        for(SearchAnalysis analysis: analyses) {
            header = new TableHeader(analysis.getAnalysisProgram().displayName());
            header.setSortClass(SORT_CLASS.SORT_INT);
            headers.add(header);
        }
        return headers;
    }

    public List<ExperimentProteinferRun> getProtInferRuns() {
        return protInferRuns;
    }

    public void setProtInferRuns(List<ExperimentProteinferRun> protInferRuns) {
        this.protInferRuns = protInferRuns;
    }
    
    public boolean getHasProtInferResults() {
        return dtaSelect != null || 
        (protInferRuns != null && protInferRuns.size() > 0) ||
        (prophetRuns != null && prophetRuns.size() > 0);
    }
}
