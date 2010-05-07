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
			
			// add cell for molecular wt.
			String molWtCellColor = getColorForMolWt(protein.getMolecularWeight());
			HeatMapCell cell1 = new HeatMapCell();
			cell1.setLabel("");
			cell1.setHexColor(molWtCellColor);
			cells.add(cell1);
			
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
				
			row.setPlotUrl(plotUrl);
			
			row.setCells(cells);
		}
	}


	private String getPlotUrl(ComparisonProtein protein, List<? extends Dataset> datasets) {
		
		String plotUrl = "http://chart.apis.google.com/chart?cht=bvg&chxt=x,y,x";
		String data1 = "";
		String data2 = "";
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
			
			xlabel += "|"+ds.getDatasetId();
			//colors += "|"+DatasetColor.get(idx).hexValue();
			idx++;
			
			maxLabel = (int)Math.max(maxLabel, (""+ds.getDatasetId()).length());
			
			if(dpi == null || !dpi.isPresent()) {
				data1+=",0";
				data2 += ",0";
			}
			else {
				data1 += ","+dpi.getNormalizedSpectrumCountRounded();
				data2 += ","+dpi.getSpectrumCount();
				maxSc = (int) Math.max(maxSc, dpi.getNormalizedSpectrumCount());
			}
			
		}
		int barWidth = (maxLabel*6)/2;
		if(barWidth < 12)	barWidth = 12;
		String barSpacing = barWidth+",1,10";
		int chartWidth = ((barWidth*2 + 1 + 10)*datasets.size())+50;
		chartSize += chartWidth+"x250";
		plotUrl += "&"+chartSize;
		plotUrl += "&chbh="+barSpacing;
		
		//colors = colors.substring(1);
		plotUrl += "&chco=C6D9FD,4D89F9";
		
		data1 = data1.substring(1);
		data1 = "t:"+data1;
		data2 = data2.substring(1);
		data2 = "|"+data2;
		plotUrl += "&chd="+data1+data2;
		
		
		int div = (int)Math.ceil((double)maxSc / 10.0);
		if(div > 5) {
			div = Math.round((float)div / 5.0f) * 5;
		}
		xrange = "1,0,"+maxSc+","+div;
		plotUrl += "&chxr="+xrange;
		
		scale = "0,"+maxSc;
		plotUrl += "&chds="+scale;
		
		plotUrl += "&chxl=0:"+xlabel;
		
		// x-axis label
		plotUrl += "&chxl=2:|Dataset+ID&chxp=2,50";
		
		// bar labels
		plotUrl += "&chm=N,000000,0,,10|N,000000,1,,10";
		
		// legend
		plotUrl += "&chdl=Spectrum+Count|Norm.+Spectrum+Count&chdlp=b";
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
			
			// add cell for molecular wt.
			String molWtCellColor = getColorForMolWt(protein.getMolecularWeight());
			HeatMapCell cell1 = new HeatMapCell();
			cell1.setLabel("");
			cell1.setHexColor(molWtCellColor);
			cells.add(cell1);
			
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
					
				row.setPlotUrl(plotUrl);
			}
			row.setCells(cells);
		}
	}

	private String getColorForMolWt(float molecularWeight) {
		
		int r = 255;
		int g = 255;
		int b = 0;
		
		int bin = (int) (molecularWeight / 10000);
		//r += 25 * bin;
		g -= 25 * bin;
		//b -= 25 * bin;
		r = Math.min(255, r);
		g = Math.max(0, g);
		//b = Math.max(0, bin);
		return ProteinGroupComparisonDataset.hexValue(r, g, b);
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
		private String plotUrl = "NULL";
		
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
		public String getPlotUrl() {
			return plotUrl;
		}
		public void setPlotUrl(String url) {
			this.plotUrl = url;
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
