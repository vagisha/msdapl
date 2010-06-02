
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<div style="background-color:#ED9A2E;width:100%; margin:40 0 0 0; padding:3 0 3 0; color:white;" align="left">
<span style="margin-left:10;" 
	  class="foldable fold-open" id="goslim_fold" onclick="toggleGoSlimDetails();">&nbsp;&nbsp;&nbsp;&nbsp; </span>
<b>GO Slim Analysis</b>
</div>
	  
<div align="center" style="border:1px dotted gray;" id="goslim_fold_target">
	
	<div style="color:red; font-weight:bold;" align="center">
		# Proteins: <bean:write name="goAnalysis" property="totalProteinCount"/> &nbsp; &nbsp; 
		# Not annotated: <bean:write name="goAnalysis" property="numProteinsNotAnnotated"/>
	</div>
		
	<div align="center">
		<b><bean:write name="goAnalysis" property="goSlimName" /> has <bean:write name="goAnalysis" property="slimTermCount"/> terms. Top 15 terms are displayed</b>
	</div>
		
	<table>
	<tr>
	<td>
		<div style="margin-bottom: 10px; padding: 3px; border:1px dashed #BBBBBB; width:100%;" align="center">
		<img src="<bean:write name='pieChartUrl'/>" alt="Can't see the Google Pie Chart??"/></img>
		</div>
	</td>
	</tr>
	<tr>
	<td>
		<div style="margin-bottom: 10px; padding: 3px; border:1px dashed #BBBBBB; width:100%;" align="center">
		<img src="<bean:write name='barChartUrl'/>" alt="Can't see the Google Bar Chart??"/></img>
		</div>
	</td>
	</tr>
	</table>
	
	<br/>
	<div align="center" style="width:75%; font-weight:bold; margin-bottom:3px;color:#D74D2D"><span class="clickable underline" onclick="toggleGoSlimTable();" id="go_slim_table_link">Hide Table</span></div>
	<table class="table_basic" id="go_slim_table" width="75%">
	<thead>
	<th class="sort-alpha">GO ID</th>
	<th class="sort-alpha"><bean:write name="goAnalysis" property="goAspect"/></th>
	<th class="sort-int"># Proteins</th>
	<th class="sort-float">%</th>
	</thead>
	<tbody>
	<logic:iterate name="goAnalysis" property="termNodes" id="node">
	<tr>
		<td>
		<a target="go_window"
	    href="http://www.godatabase.org/cgi-bin/amigo/go.cgi?action=query&view=query&search_constraint=terms&query=<bean:write name="node" property="accession"/>">
        <bean:write name="node" property="accession"/></a>
		<td><bean:write name="node" property="name"/></td>
		<td><bean:write name="node" property="proteinCountForTerm"/></td>
		<td><bean:write name="node" property="proteinCountForTermPerc"/></td>
	</tr>
	</logic:iterate>
	</tbody>
	</table>
</div>
