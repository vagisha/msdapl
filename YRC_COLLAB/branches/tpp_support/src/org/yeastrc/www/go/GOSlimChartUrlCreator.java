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
		
		double maxValue = 0.0;
		for(int i = 0; i < maxBars; i++) {
			if(i >= slimTerms.size())
				break;
			GOSlimTerm term = slimTerms.get(i);
			if(term.getProteinCountForTerm() == 0)
				continue;
			
			data += ","+term.getProteinCountForTermPerc();
			labels = "|"+term.getShortName()+labels;
			maxValue = Math.max(maxValue, term.getProteinCountForTermPerc());
		}
		
		if(data.length() > 0)
			data = data.substring(1); // remove first comma
		data = "&chd=t:"+data;
		
		labels = "&chxl=1:"+labels;
		
		
		buf.append(data);
		buf.append(labels);
		
		int maxV = (int) Math.ceil(maxValue);
		maxV = maxV + (10 - maxV%10);
		
		int step = getStep(maxV);
		
		buf.append("&chxr=0,0,"+maxV+","+step);
		buf.append("&chds=0,"+maxV);
		buf.append("&chbh=12"); // width of bars
		buf.append("&chm=N**%,000000,0,-1,11"); // labels for each bar
		
		return buf.toString();
	}
	
	private static int getStep(int maxValue) {
		int step = Math.min(10, maxValue / 10);
		int[] steps = {10,5,2,1};
		int diff = Integer.MAX_VALUE;
		int bstep = step;
		for(int s: steps) {
			if(Math.abs(s - step) < diff) {
				diff = Math.abs(s - step);
				bstep = s;
			}
		}
		return bstep;
	}
}
