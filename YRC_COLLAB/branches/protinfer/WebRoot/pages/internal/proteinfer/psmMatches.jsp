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
	<logic:equal name="searchProgram" value="sequest">
		<th style="text-decoration: underline;font-size: 10pt;font-weight: bold;" class="sort-float" >XCorr</th>
	</logic:equal>
	<logic:equal name="searchProgram" value="prolucid">
		<th style="text-decoration: underline;font-size: 10pt;font-weight: bold;" class="sort-float" >primaryScore</th>
	</logic:equal>
	<th class="sort-float" style="font-size: 10pt; font-weight: bold;">DeltaCN</th>
	<th class="sort-float" style="font-size: 10pt; font-weight: bold;">FDR</th>
	<th style="font-size: 10pt; font-weight: bold;">View</th>
</tr>
</thead>
<tbody>

<logic:iterate name="psmList" id="psm">

<logic:equal name="searchProgram" value="sequest">
     <bean:define name="psm" property="spectrumMatch" type="org.yeastrc.ms.domain.search.sequest.SequestSearchResult" id="sequestPsm"></bean:define>
</logic:equal>
<logic:equal name="searchProgram" value="prolucid">
 	<bean:define name="psm" property="spectrumMatch" type="org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResult" id="prolucidPsm"></bean:define>
</logic:equal>	

	<tr>
	<td><bean:write name="psm" property="modifiedSequence" /></td>
	<td><bean:write name="psm" property="scanNumber" /></td>
	
	<logic:equal name="searchProgram" value="sequest">
		<td><bean:write name="sequestPsm" property="charge" /></td>
		<td><bean:write name="sequestPsm" property="sequestResultData.xCorr" /></td>
		<td><bean:write name="sequestPsm" property="sequestResultData.deltaCN" /></td>
	</logic:equal>
	<logic:equal name="searchProgram" value="prolucid">
		<td><bean:write name="prolucidPsm" property="charge" /></td>
		<td><bean:write name="prolucidPsm" property="prolucidResultData.primaryScore" /></td>
		<td><bean:write name="prolucidPsm" property="prolucidResultData.deltaCN" /></td>
	</logic:equal>
	
	<td><bean:write name="psm" property="fdr" /></td>
	<td><span style="text-decoration: underline; cursor: pointer;" 
			  onclick="viewSpectrum(<bean:write name="psm" property="scanId" />, <bean:write name="psm" property="searchResultId" />)" >
		View
		</span></td>
	</tr>
</logic:iterate>
</tbody>
</table>