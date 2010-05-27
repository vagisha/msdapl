
<%@page import="org.yeastrc.www.go.GOSlimChartUrlCreator"%>
<%@page import="org.yeastrc.www.go.GOSlimAnalysis"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<div align="center" style="margin-top:20px;">
	<%GOSlimAnalysis goAnalysis = (GOSlimAnalysis)request.getAttribute("goAnalysis"); %>
	<table>
	<tr>
	<td>
		<div align="center">Top 15 terms are displayed</div>
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
	<span style="color:red; font-weight:bold;">
	# Proteins: <bean:write name="goAnalysis" property="totalProteinCount"/> &nbsp; &nbsp; 
	# Not annotated: <bean:write name="goAnalysis" property="numProteinsNotAnnotated"/>
	</span>
	<br/>
	<table class="table_basic" id="go_analysis_table" width="75%">
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
