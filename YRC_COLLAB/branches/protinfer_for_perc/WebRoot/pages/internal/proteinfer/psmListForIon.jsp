<%@page import="edu.uwpr.protinfer.ProteinInferenceProgram"%>
<%@page import="org.yeastrc.ms.domain.search.Program"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<table align="center" width="70%"
		id="allpsms_<bean:write name="pinferIonId" />"
     	style="border: 1px dashed gray; border-spacing: 4px; margin-top: 6px; margin-bottom: 6px;" 
     	class="sortable allpsms allPeptideSpectra">
       			
     <thead><tr>
	     <th style="text-decoration: underline;font-size: 10pt;" class="sort-alpha" align="left">Scan Number</th>
	     <th style="text-decoration: underline;font-size: 10pt;" class="sort-int" align="left">Charge</th>
	     <logic:equal name="protInferProgram" value="<%= ProteinInferenceProgram.PROTINFER_SEQ.name()%>">
	     	<th style="text-decoration: underline;font-size: 10pt;" class="sort-float" align="left">FDR</th>
	     </logic:equal>
	     <logic:equal name="protInferProgram" value="<%= ProteinInferenceProgram.PROTINFER_PLCID.name()%>">
	     	<th style="text-decoration: underline;font-size: 10pt;" class="sort-float" align="left">FDR</th>
	     </logic:equal>
	     <logic:equal name="inputGenerator" value="<%=Program.SEQUEST.name() %>">
	     	<th style="text-decoration: underline;font-size: 10pt;" class="sort-float" align="left">DeltaCN</th>
	     	<th style="text-decoration: underline;font-size: 10pt;" class="sort-float" align="left">XCorr</th>
	     </logic:equal>
	     <logic:equal name="inputGenerator" value="<%=Program.EE_NORM_SEQUEST.name() %>">
	     	<th style="text-decoration: underline;font-size: 10pt;" class="sort-float" align="left">DeltaCN</th>
	     	<th style="text-decoration: underline;font-size: 10pt;" class="sort-float" align="left">XCorr</th>
	     </logic:equal>
	     <logic:equal name="inputGenerator" value="<%=Program.PROLUCID.name() %>">
	     	<th style="text-decoration: underline;font-size: 10pt;" class="sort-float" align="left">DeltaCN</th>
	     	<th style="text-decoration: underline;font-size: 10pt;" class="sort-float" align="left">Primary Score</th>
	     </logic:equal>
	     <logic:equal name="inputGenerator" value="<%=Program.PERCOLATOR.name() %>">
	     	<th style="text-decoration: underline;font-size: 10pt;" class="sort-float" align="left">qValue</th>
	     	<th style="text-decoration: underline;font-size: 10pt;" class="sort-float" align="left">PEP</th>
	     </logic:equal>
	     
	     <th style="text-decoration: underline;font-size: 10pt;" align="left">Spectrum</th>
	</tr></thead>
       			
	<tbody>
        <logic:iterate name="psmList" id="psm">
        <tr>
     		<td><bean:write name="psm" property="scanNumber" /></td>
     		<td><bean:write name="psm" property="spectrumMatch.charge" /></td>
     		
     		<logic:equal name="protInferProgram" value="<%= ProteinInferenceProgram.PROTINFER_SEQ.name()%>">
     			<bean:define name="psm" property="proteinferSpectrumMatch" id="psm_idp" type="edu.uwpr.protinfer.database.dto.idpicker.IdPickerSpectrumMatch"/>
     			<td><bean:write name="psm_idp" property="fdrRounded" /></td>
     		</logic:equal>
     		
     		<logic:equal name="protInferProgram" value="<%= ProteinInferenceProgram.PROTINFER_PLCID.name()%>">
     			<bean:define name="psm" property="proteinferSpectrumMatch" id="psm_idp" type="edu.uwpr.protinfer.database.dto.idpicker.IdPickerSpectrumMatch"/>
     			<td><bean:write name="psm_idp" property="fdrRounded" /></td>
     		</logic:equal>
     		
     		<logic:equal name="inputGenerator" value="<%=Program.SEQUEST.name() %>">
     			<bean:define name="psm" property="spectrumMatch" id="psm_seq" type="org.yeastrc.ms.domain.search.sequest.SequestSearchResult"/>
     			<td><bean:write name="psm_seq" property="sequestResultData.deltaCN" /></td>
     			<td><bean:write name="psm_seq" property="sequestResultData.xCorr" /></td>
     		</logic:equal>
     		
     		<logic:equal name="inputGenerator" value="<%=Program.EE_NORM_SEQUEST.name() %>">
     			<bean:define name="psm" property="spectrumMatch" id="psm_seq" type="org.yeastrc.ms.domain.search.sequest.SequestSearchResult"/>
     			<td><bean:write name="psm_seq" property="sequestResultData.deltaCN" /></td>
     			<td><bean:write name="psm_seq" property="sequestResultData.xCorr" /></td>
     		</logic:equal>
     
     		<logic:equal name="inputGenerator" value="<%=Program.PROLUCID.name() %>">
     		 	<bean:define name="psm" property="spectrumMatch" id="psm_plc" type="org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResult"/>
     		 	<td><bean:write name="psm_plc" property="prolucidResultData.primaryScore" /></td>
				<td><bean:write name="psm_plc" property="prolucidResultData.deltaCN" /></td>
     		</logic:equal>
     		 
     		<logic:equal name="inputGenerator" value="<%=Program.PERCOLATOR.name() %>">
     		 	<bean:define name="psm" property="spectrumMatch" id="psm_perc" type="org.yeastrc.ms.domain.analysis.percolator.PercolatorResult"/>
     		 	<td><bean:write name="psm_perc" property="qvalueRounded" /></td>
     			<td><bean:write name="psm_perc" property="posteriorErrorProbabilityRounded" /></td>
     		</logic:equal>
     		 
     		<td><span style="text-decoration: underline; cursor: pointer;"
				onclick="viewSpectrum(<bean:write name="psm" property="scanId" />, <bean:write name="psm" property="runSearchResultId" />)" >
				View
			</span>
			</td>
   			</tr>
        </logic:iterate>
	</tbody>
</table>