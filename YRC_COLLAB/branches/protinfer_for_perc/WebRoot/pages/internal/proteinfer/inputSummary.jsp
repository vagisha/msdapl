<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

	
	<table cellpadding="2" align="center" style="font-family: Trebuchet MS,Trebuchet,Verdana,Helvetica,Arial,sans-serif;font-size:12px; border: 1px solid gray; border-spacing: 3px">
  		<tr>
  			<th colspan="2" class="ms_A" style="font-size: 10pt;">IDPicker Parameters</th>
  		</tr>
  		<logic:iterate name="idpickerRun" property="sortedParams" id="param">
  		<tr>
    	<td VALIGN="top" align="center" style="border: 1px #CCCCCC dotted;">
    		<bean:write name="param" property="name" />
    	</td>
    	<td VALIGN="top" align="center" style="border: 1px #CCCCCC dotted;">
    		<bean:write name="param" property="value" />
    	</td>
   		</tr>
   		</logic:iterate>
	</table>
	    <br><br>
	<table cellpadding="4" cellspacing="2" align="center" width="90%" class="sortable stripe_table">
	<logic:notEmpty name="inputSummary" >
	<thead>
	<tr>
	<th class="sort-alpha" align="left"><b><font size="2pt">File Name</font></b></th>
	<th class="sort-int" align="left"><b><font size="2pt">Decoy Hits</font></b></th>
	<th class="sort-int" align="left"><b><font size="2pt">Target Hits</font></b></th>
	<th class="sort-int" align="left"><b><font size="2pt">Filtered Target Hits</font></b></th>
	</tr>
	</thead>
	</logic:notEmpty>
	<tbody>
	 	<logic:iterate name="inputSummary"  id="input">
 			<tr>
 				<td>
 					<span style="text-decoration: underline; cursor: pointer;"
 								onclick="showSpectrumMatches(<bean:write name="input" property="input.inputId" />, '<bean:write name="input" property="fileName" />')">
 					<bean:write name="input" property="fileName" />
 					</span>
 				</td>
 				<td><bean:write name="input" property="input.numDecoyHits" /></td>
 				<td><bean:write name="input" property="input.numTargetHits" /></td>
 				<td><bean:write name="input" property="input.numFilteredTargetHits" /></td>
 			</tr>
	 	</logic:iterate>
	 	<tr>
	 		<td><b>TOTAL</b></td>
	 		<td><b><bean:write name="totalDecoyHits" /></b></td>
	 		<td><b><bean:write name="totalTargetHits" /></b></td>
	 		<td><b><bean:write name="filteredTargetHits"  /></b></td>
	 	</tr>
	 	</tbody>
		</table>
	<br><br>
	<logic:iterate name="inputSummary" id="input">
		<div id="psm_<bean:write name="input" property="input.inputId" />" style="display: none;" class="input_psm"></div>
	</logic:iterate>