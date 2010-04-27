/**
 * ClusteredGroupDataset.java
 * @author Vagisha Sharma
 * Apr 23, 2010
 * @version 1.0
 */
package org.yeastrc.www.compare.clustering;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.www.compare.ComparisonProtein;
import org.yeastrc.www.compare.ProteinComparisonDataset;
import org.yeastrc.www.compare.ProteinGroupComparisonDataset;
import org.yeastrc.www.compare.dataset.Dataset;
import org.yeastrc.www.compare.dataset.DatasetProteinInformation;
import org.yeastrc.www.compare.graph.ComparisonProteinGroup;

/**
 * 
 */
public class HeatMapData {

	private List<String> datasetLabels;
	private List<HeatMapRow> rows;
	
	public HeatMapData (ProteinGroupComparisonDataset grpComparison) {
		
		datasetLabels = new ArrayList<String>(grpComparison.getDatasetCount()+1);
		
		for(Dataset ds: grpComparison.getDatasets()) {
			datasetLabels.add(("ID_"+ds.getDatasetId()));
		}
		
		rows = new ArrayList<HeatMapRow>(grpComparison.getTotalProteinGroupCount() + 1);
		
		int index = 0;
		for(ComparisonProteinGroup grp: grpComparison.getProteinsGroups()) {
			
			HeatMapRow row = new HeatMapRow();
			row.setIndexInList(index);
			index += grp.getProteins().size();
			rows.add(row);
			
			ComparisonProtein protein = grp.getProteins().get(0);
			
			List<HeatMapCell> cells = new ArrayList<HeatMapCell>();
			
			row.setRowName(protein.getProteinListing().getFastaReferences().get(0).getShortAccession());
			
			for(Dataset ds: grpComparison.getDatasets()) {
				
				HeatMapCell cell = new HeatMapCell();
				cells.add(cell);
				cell.setLabel("");
				DatasetProteinInformation dpi = protein.getDatasetProteinInformation(ds);
				if(dpi == null || !dpi.isPresent()) {
					cell.setHexColor("#FFFFFF");
				}
				else {
					float scaledSc = grpComparison.getScaledSpectrumCount(dpi.getNormalizedSpectrumCount());
					cell.setHexColor(grpComparison.getScaledColor(scaledSc));
				}
			}
			row.setCells(cells);
		}
	}
	
	
	public HeatMapData (ProteinComparisonDataset comparison) {
		
		datasetLabels = new ArrayList<String>(comparison.getDatasetCount()+1);
		
		for(Dataset ds: comparison.getDatasets()) {
			datasetLabels.add(("ID_"+ds.getDatasetId()));
		}
		
		rows = new ArrayList<HeatMapRow>(comparison.getTotalProteinCount() + 1);
		
		int index = 0;
		for(ComparisonProtein protein: comparison.getProteins()) {
			
			HeatMapRow row = new HeatMapRow();
			row.setIndexInList(index++);
			rows.add(row);
			
			
			List<HeatMapCell> cells = new ArrayList<HeatMapCell>();
			
			row.setRowName(protein.getProteinListing().getFastaReferences().get(0).getShortAccession());
			
			for(Dataset ds: comparison.getDatasets()) {
				
				HeatMapCell cell = new HeatMapCell();
				cells.add(cell);
				cell.setLabel("");
				DatasetProteinInformation dpi = protein.getDatasetProteinInformation(ds);
				if(dpi == null || !dpi.isPresent()) {
					cell.setHexColor("#FFFFFF");
				}
				else {
					float scaledSc = comparison.getScaledSpectrumCount(dpi.getNormalizedSpectrumCount());
					cell.setHexColor(comparison.getScaledColor(scaledSc));
				}
			}
			row.setCells(cells);
		}
	}

	public List<String> getDatasetLabels() {
		return datasetLabels;
	}
	
	public List<HeatMapRow> getRows() {
		return this.rows;
	}
	
	public static final class HeatMapRow {
		
		private String rowName;
		private List<HeatMapCell> cells;
		private int indexInList;
		
		public String getRowName() {
			return rowName;
		}
		public void setRowName(String rowName) {
			this.rowName = rowName;
		}
		public List<HeatMapCell> getCells() {
			return cells;
		}
		public void setCells(List<HeatMapCell> cells) {
			this.cells = cells;
		}
		public int getIndexInList() {
			return indexInList;
		}
		public void setIndexInList(int indexInList) {
			this.indexInList = indexInList;
		}
	}
	
	public static final class HeatMapCell {
		private String hexColor;
		private String label;
		
		public String getHexColor() {
			return hexColor;
		}
		public void setHexColor(String hexColor) {
			this.hexColor = hexColor;
		}
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
	}
	
}
