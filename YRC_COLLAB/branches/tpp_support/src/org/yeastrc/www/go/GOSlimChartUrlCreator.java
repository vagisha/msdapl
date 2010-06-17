/**
 * 
 */
package org.yeastrc.www.go;

import java.util.List;

/**
 * GoogleChartUrlCreator.java
 * @author Vagisha Sharma
 * May 26, 2010
 * 
 */
public class GOSlimChartUrlCreator {

	private GOSlimChartUrlCreator() {}
	
	public static String getPieChartUrl(GOSlimAnalysis analysis, int maxSlices) {
		
		StringBuilder buf = new StringBuilder();
		buf.append("http://chart.apis.google.com/chart?cht=p&chs=800x300&chco=003388,BBBB00");
		
		String data = "";
		String labels = "";
		String legend= "";
		List<GOSlimTerm> slimTerms = analysis.getTermNodesMinusRootNodes();
		
		double totalPerc = 0.0;
		for(int i = 0; i < maxSlices; i++) {
			if(i >= slimTerms.size())
				break;
			GOSlimTerm term = slimTerms.get(i);
			totalPerc += term.getProteinCountForTermPerc();
		}
		
		for(int i = 0; i < maxSlices; i++) {
			if(i >= slimTerms.size())
				break;
			GOSlimTerm term = slimTerms.get(i);
			if(term.getProteinCountForTerm() == 0)
				continue;
			
			int frac = (int)Math.round((term.getProteinCountForTermPerc() * 100.0) / totalPerc);
			data += ","+frac;
			labels += "|"+term.getProteinCountForTerm()+" ("+term.getProteinCountForTermPerc()+"%)";
			legend += "|"+term.getProteinCountForTerm()+" ("+term.getShortName()+")";
		}
		
		if(data.length() > 0)
			data = data.substring(1); // remove first comma
		data = "&chd=t:"+data;
		
		if(labels.length() > 0)
			labels = labels.substring(1); // remove the first comma
		labels = "&chl="+labels;
		
		if(legend.length() > 0)
			legend = legend.substring(1); // remove the first comma
		legend = "&chdl="+legend;
		
		buf.append(data);
		buf.append(labels);
		buf.append(legend);
		
		return buf.toString();
	}
	
	public static String getBarChartUrl(GOSlimAnalysis analysis, int maxBars) {
		
		StringBuilder buf = new StringBuilder();
		buf.append("http://chart.apis.google.com/chart?cht=bhs&chxt=x,y&chs=450x260&chco=008888");
		
		String data = "";
		String labels = "";
		List<GOSlimTerm> slimTerms = analysis.getTermNodesMinusRootNodes();
		
		
		for(int i = 0; i < maxBars; i++) {
			if(i >= slimTerms.size())
				break;
			GOSlimTerm term = slimTerms.get(i);
			if(term.getProteinCountForTerm() == 0)
				continue;
			
			data += ","+term.getProteinCountForTermPerc();
			labels = "|"+term.getShortName()+labels;
		}
		
		if(data.length() > 0)
			data = data.substring(1); // remove first comma
		data = "&chd=t:"+data;
		
		labels = "&chxl=1:"+labels;
		
		
		buf.append(data);
		buf.append(labels);
		
		buf.append("&chxr=1:0,100,10");
		buf.append("&chbh=12"); // width of bars
		buf.append("&chm=N**%,000000,0,-1,11"); // labels for each bar
		
		return buf.toString();
	}
}
