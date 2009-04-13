
<%@page import="edu.uwpr.protinfer.ProteinInferenceProgram"%>
<%@page import="org.yeastrc.ms.domain.search.Program"%>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


<logic:present name="proteinId">
<table align="center" width="98%" 
  			style="margin-top: 6px; margin-bottom: 6px;" 
  			class="sortable peptlist table_pinfer_small"
  			id="peptforprottbl_<bean:write name="proteinId" />_<bean:write name="proteinGroupId" />">
</logic:present>

<logic:notPresent name="proteinId">
<table align="center" width="98%"
  			style="border: 1px dashed gray; border-spacing: 4px; margin-top: 6px; margin-bottom: 6px;" 
  			class="sortable peptlist table_pinfer_small"
  			id="peptforprottbl_<bean:write name="proteinGroupId" />">
</logic:notPresent>

  	 <thead><tr>
  	 <th class="sort-alpha" align="left">Uniq</th>
     <th class="sort-alpha" align="left">Peptide</th>
     <th class="sort-int" align="left">Charge</th>
     <th class="sort-int" align="left"># Spectra</th>
     <logic:equal name="protInferProgram" value="<%= ProteinInferenceProgram.PROTINFER_SEQ.name()%>">
     	<th class="sort-float" align="left">Best FDR</th>
     </logic:equal>
     <logic:equal name="protInferProgram" value="<%= ProteinInferenceProgram.PROTINFER_PLCID.name()%>">
     	<th class="sort-float" align="left">Best FDR</th>
     </logic:equal>
     <logic:equal name="inputGenerator" value="<%=Program.SEQUEST.name() %>">
     	<th class="sort-float" align="left">DeltaCN</th>
     	<th class="sort-float" align="left">XCorr</th>
     </logic:equal>
     
     <logic:equal name="inputGenerator" value="<%=Program.PROLUCID.name() %>">
     	<th class="sort-float" align="left">DeltaCN</th>
     	<th class="sort-float" align="left">Primary Score</th>
     </logic:equal>
     <logic:equal name="inputGenerator" value="<%=Program.PERCOLATOR.name() %>">
     	<th class="sort-float" align="left">qValue</th>
     	<th class="sort-float" align="left">PEP</th>
     </logic:equal>
     
     <th align="left">Spectrum</th>
     </tr></thead>
     
     
     <tbody>
     <logic:iterate name="proteinPeptideIons" id="ion">
     
     	<tr>
     		<td>
     			<logic:equal name="ion" property="isUniqueToProteinGroup" value="true">*</logic:equal>
     			<logic:equal name="ion" property="isUniqueToProteinGroup" value="false"></logic:equal>
     		</td>
     		<td><bean:write name="ion" property="ionSequence" /></td>
     		<td><bean:write name="ion" property="charge" /></td>
     		<td><bean:write name="ion" property="spectrumCount" /></td>
     		
     		<logic:equal name="protInferProgram" value="<%= ProteinInferenceProgram.PROTINFER_SEQ.name()%>">
     			<bean:define name="ion" property="ion.bestSpectrumMatch" id="psm_idp" type="edu.uwpr.protinfer.database.dto.idpicker.IdPickerSpectrumMatch"/>
     			<td><bean:write name="psm_idp" property="fdrRounded" /></td>
     		</logic:equal>
     		
     		<logic:equal name="protInferProgram" value="<%= ProteinInferenceProgram.PROTINFER_PLCID.name()%>">
     			<bean:define name="ion" property="ion.bestSpectrumMatch" id="psm_idp" type="edu.uwpr.protinfer.database.dto.idpicker.IdPickerSpectrumMatch"/>
     			<td><bean:write name="psm_idp" property="fdrRounded" /></td>
     		</logic:equal>
     		
     		<logic:equal name="inputGenerator" value="<%=Program.SEQUEST.name() %>">
     			<bean:define name="ion" property="bestSpectrumMatch" id="psm_seq" type="org.yeastrc.ms.domain.search.sequest.SequestSearchResult"/>
     			<td><bean:write name="psm_seq" property="sequestResultData.deltaCN" /></td>
     			<td><bean:write name="psm_seq" property="sequestResultData.xCorr" /></td>
     		</logic:equal>
     		
     		<logic:equal name="inputGenerator" value="<%=Program.PROLUCID.name() %>">
     		 	<bean:define name="ion" property="bestSpectrumMatch" id="psm_plc" type="org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResult"/>
     		 	<td><bean:write name="psm_plc" property="prolucidResultData.primaryScore" /></td>
				<td><bean:write name="psm_plc" property="prolucidResultData.deltaCN" /></td>
     		</logic:equal>
     		 
     		<logic:equal name="inputGenerator" value="<%=Program.PERCOLATOR.name() %>">
     		 	<bean:define name="ion" property="bestSpectrumMatch" id="psm_perc" type="org.yeastrc.ms.domain.analysis.percolator.PercolatorResult"/>
     		 	<td><bean:write name="psm_perc" property="qvalueRounded" /></td>
     			<td><bean:write name="psm_perc" property="posteriorErrorProbabilityRounded" /></td>
     		</logic:equal>
     		 
     		<td><span style="text-decoration: underline; cursor: pointer;" 
				onclick="viewSpectrum(<bean:write name="ion" property="scanId" />, <bean:write name="ion" property="bestSpectrumMatch.id" />)" >
				View
			</span>
			</td>
     	</tr>
     </logic:iterate>
     </tbody>
</table>
