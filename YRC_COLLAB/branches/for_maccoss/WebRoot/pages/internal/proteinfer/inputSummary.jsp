
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

	<bean:define name="idpickerRun" property="program" id="program" type="edu.uwpr.protinfer.ProteinInferenceProgram"/>
	
	<table align="center" class="table_pinfer_small">
  		<tr>
  			<th colspan="2" style="font-size: 10pt;">Parameters</th>
  		</tr>
  		<logic:iterate name="idpickerRun" property="sortedParams" id="param" type="edu.uwpr.protinfer.database.dto.idpicker.IdPickerParam">
  		<tr>
    	<td VALIGN="top" align="center" style="border: 1px #F2F2F2 solid;">
    		<%=program.getDisplayNameForParam(param.getName()) %>
    	</td>
    	<td VALIGN="top" align="center" style="border: 1px #F2F2F2 solid;">
    		<bean:write name="param" property="value" />
    	</td>
   		</tr>
   		</logic:iterate>
	</table>
	    <br><br>
	<table cellpadding="4" cellspacing="2" align="center" width="90%" style="border:1px dashed #7F7F7F;">
	<tr><td style="background-color:#F2F2F2; font-weight:bold;">
		# Unique peptide sequences: <bean:write name="filteredUniquePeptideCount"/>
	</td></tr>
	</table>
	
	<table cellpadding="4" cellspacing="2" align="center" width="90%" class="sortable stripe_table table_basic">
	<logic:notEmpty name="inputSummary" >
	<thead>
	<tr>
	<th class="sort-alpha" align="left"><b><font size="2pt">File Name</font></b></th>
	<!-- <th class="sort-int" align="left"><b><font size="2pt">Decoy Hits</font></b></th> -->
	<th class="sort-int" align="left"><b><font size="2pt"># Hits</font></b></th>
	<th class="sort-int" align="left"><b><font size="2pt"># Filtered Hits</font></b></th>
	</tr>
	</thead>
	</logic:notEmpty>
	<tbody>
	 	<logic:iterate name="inputSummary"  id="input">
 			<tr>
 				<td class="left_align">
 					<!--  <span style="text-decoration: underline; cursor: pointer;"
 								onclick="showSpectrumMatches(<bean:write name="input" property="input.inputId" />, '<bean:write name="input" property="fileName" />')">
 					-->
 					<bean:write name="input" property="fileName" />
 					<!-- </span> -->
 				</td>
 				<!--  <td><bean:write name="input" property="input.numDecoyHits" /></td> -->
 				<td><bean:write name="input" property="input.numTargetHits" /></td>
 				<td><bean:write name="input" property="input.numFilteredTargetHits" /></td>
 			</tr>
	 	</logic:iterate>
	 	<tr>
	 		<td class="left_align"><b>TOTAL</b></td>
	 		<!-- <td><b><bean:write name="totalDecoyHits" /></b></td> -->
	 		<td><b><bean:write name="totalTargetHits" /></b></td>
	 		<td><b><bean:write name="filteredTargetHits"  /></b></td>
	 	</tr>
	 	</tbody>
		</table>
	<br><br>
	<logic:iterate name="inputSummary" id="input">
		<div id="psm_<bean:write name="input" property="input.inputId" />" style="display: none;" class="input_psm"></div>
	</logic:iterate>