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
import org.yeastrc.www.compare.dataset.DatasetColor;
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
					float heatMapSpectrumCount = dpi.getHeatMapSpectrumCount();
					cell.setHexColor(grpComparison.getHeatMapColor(heatMapSpectrumCount));
				}
			}
			
			// http://chart.apis.google.com/chart?chs=320x200&cht=bvs&chd=t:1,19,27,53,61&chds=0,61&chco=FFCC33&chxt=x,y&chxr=1,0,61,10&chxl=0:|Jan|Feb|Mar|Apr|May
			String plotUrl = getPlotUrl(protein, grpComparison.getDatasets());
				
			row.setRowGraph(plotUrl);
			
			row.setCells(cells);
		}
	}


	private String getPlotUrl(ComparisonProtein protein, List<? extends Dataset> datasets) {
		
		String plotUrl = "http://chart.apis.google.com/chart?cht=bvs&chxt=x,y";
		String data = "";
		//String colors = "";
		String xrange = "";
		String scale = "";
		String xlabel = "";
		String chartSize = "chs=";
		
		// chart title
		//String title = "&chtt="+protein.getProteinListing().getFastaReferences().get(0).getShortAccession();
		//plotUrl += title;
		
		int idx = 0;
		int maxSc = 0;
		int maxLabel = 0;
		for(Dataset ds: datasets) {
			
			DatasetProteinInformation dpi = protein.getDatasetProteinInformation(ds);
			
			xlabel += "|ID_"+ds.getDatasetId();
			//colors += "|"+DatasetColor.get(idx).hexValue();
			idx++;
			
			maxLabel = (int)Math.max(maxLabel, ("ID_"+ds.getDatasetId()).length());
			
			if(dpi == null || !dpi.isPresent()) {
				data+=",0";
			}
			else {
				data += ","+dpi.getNormalizedSpectrumCountRounded();
				maxSc = (int) Math.max(0, dpi.getNormalizedSpectrumCount());
			}
			
		}
		int barWidth = maxLabel*6;
		String barSpacing = barWidth+",10,10";
		int chartWidth = ((barWidth + 10)*datasets.size())+50;
		chartSize += chartWidth+"x200";
		plotUrl += "&"+chartSize;
		plotUrl += "&chbh="+barSpacing;
		
		//colors = colors.substring(1);
		plotUrl += "&chco="+DatasetColor.ORANGE.hexValue();
		
		data = data.substring(1);
		data = "t:"+data;
		plotUrl += "&chd="+data;
		
		
		int div = (int)Math.ceil((double)maxSc / 10.0);
		xrange = "1,0,"+maxSc+","+div;
		plotUrl += "&chxr="+xrange;
		
		scale = "0,"+maxSc;
		plotUrl += "&chds="+scale;
		
		plotUrl += "&chxl=0:"+xlabel;
		return plotUrl;
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
					float heatMapSpectrumCount = dpi.getHeatMapSpectrumCount();
					cell.setHexColor(comparison.getHeatMapColor(heatMapSpectrumCount));
				}
				
				// http://chart.apis.google.com/chart?chs=320x200&cht=bvs&chd=t:1,19,27,53,61&chds=0,61&chco=FFCC33&chxt=x,y&chxr=1,0,61,10&chxl=0:|Jan|Feb|Mar|Apr|May
				String plotUrl = getPlotUrl(protein, comparison.getDatasets());
					
				row.setRowGraph(plotUrl);
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
		private String rowGraph = "NULL";
		
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
		public String getRowGraph() {
			return rowGraph;
		}
		public void setRowGraph(String rowGraph) {
			this.rowGraph = rowGraph;
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
