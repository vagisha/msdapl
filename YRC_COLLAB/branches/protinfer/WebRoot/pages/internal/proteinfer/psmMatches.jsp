<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


<center>
<div align="center" style="padding:5px;font-size: 10pt;width: 90%; color: black;" >
<b>Peptide Spectrum Matches for <bean:write name="filename" /></b>
</div>
</center>

<table  cellpadding="4" cellspacing="2"
		align="center" width="90%" id="psmtbl_<bean:write name="runSearchId"/>"
		style="border: 1px dashed gray; border-spacing: 2px; margin-top: 0px; margin-bottom: 6px;"
		class="sortable stripe_table">
<thead>
<tr>
	<th class="sort-alpha" style="font-size: 10pt; font-weight: bold;">Peptide</th>
	<th class="sort-int" style="font-size: 10pt; font-weight: bold;">Scan Number</th>
	<th class="sort-int" style="font-size: 10pt; font-weight: bold;">Charge</th>
	<th class="sort-float" style="font-size: 10pt; font-weight: bold;">XCorr</th>
	<th class="sort-float" style="font-size: 10pt; font-weight: bold;">DeltaCN</th>
	<th class="sort-float" style="font-size: 10pt; font-weight: bold;">FDR</th>
	<th style="font-size: 10pt; font-weight: bold;">View</th>
</tr>
</thead>
<tbody>
<logic:iterate name="psmList" id="psm">
	<tr>
	<td><bean:write name="psm" property="peptideSequence" /></td>
	<td><bean:write name="psm" property="scanNumber" /></td>
	<td><bean:write name="psm" property="charge" /></td>
	<td><bean:write name="psm" property="xcorrRounded" /></td>
	<td><bean:write name="psm" property="deltaCnRounded" /></td>
	<td><bean:write name="psm" property="fdrRounded" /></td>
	<td><span style="text-decoration: underline; cursor: pointer;" 
			  onclick="viewSpectrum(<bean:write name="psm" property="scanId" />, <bean:write name="psm" property="hitId" />)" >
		View
		</span></td>
	</tr>
</logic:iterate>
</tbody>
</table>