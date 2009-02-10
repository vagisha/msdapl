
<%@page import="edu.uwpr.protinfer.ProteinInferenceProgram"%>
<%@page import="org.yeastrc.ms.domain.search.Program"%><%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<div  align="center" style="padding:5px;font-size: 10pt;width: 90%; color: black;" >
<b><bean:write name="protein" property="accession"/></b>
<span style="text-decoration: underline; cursor: pointer; font-size: 8pt; color: red;"
      onclick="toggleProteinSequence(<bean:write name="protein" property="protein.id" />)"
      id="protseqbutton_<bean:write name="protein" property="protein.id" />">[View Sequence]</span>
<br>
<span style="color: #888888; font-size: 9pt"><bean:write name="protein" property="description" /></span>


<table align="center" cellpadding="2" style="border: 1px solid gray; border-spacing: 2px">
<tr class="pinfer_A">
	<td>Coverage(%)</td>
	<td># Peptides</td>
	<td># Uniq.Peptides</td>
	<td># Spectra </td>
	<td>NSAF** </td>
	<td>Parsimonious</td>
	<td>Other Proteins in Group</td>
</tr>
<tr>
	<td style="border: 1px #CCCCCC dotted;" align="center"><bean:write name="protein" property="protein.coverage" /></td>
	<td style="border: 1px #CCCCCC dotted;" align="center"><bean:write name="protein" property="protein.peptideCount" /></td>
	<td style="border: 1px #CCCCCC dotted;" align="center"><bean:write name="protein" property="protein.uniquePeptideCount" /></td>
	<td style="border: 1px #CCCCCC dotted;" align="center"><bean:write name="protein" property="protein.spectrumCount" /></td>
	<td style="border: 1px #CCCCCC dotted;" align="center"><bean:write name="protein" property="protein.nsafFormatted" /></td>
	<td style="border: 1px #CCCCCC dotted;" align="center">
		<logic:equal name="protein" property="protein.isParsimonious" value="true">Yes</logic:equal>
		<logic:equal name="protein" property="protein.isParsimonious" value="false">No</logic:equal>
	</td>
	<td style="border: 1px #CCCCCC dotted;" align="center">
	<logic:empty name="groupProteins">--</logic:empty>
	<logic:iterate name="groupProteins" id="prot">
		<span onclick="showProteinDetails(<bean:write name="prot" property="protein.id" />)" 
						style="text-decoration: underline; cursor: pointer">
		<bean:write name="prot" property="accession" />
		</span><br>
	</logic:iterate>
	</td>
</tr>
</table>
</div>
<br><br>

<!--  placeholder for protein sequence -->
<table  align="center" width="90%" id="protseqtbl_<bean:write name="protein" property="protein.id"/>" style="display: none;"">
	<tr><td style="background-color: #D4FECA;padding-left: 130px;" id="protsequence_<bean:write name="protein" property="protein.id"/>" ></td></tr>
</table>

<br><br>

<script type=text/javascript">
	function toggleSpectrumMatches() {
		var button = $("#psmToggle");
	
		if(button.text() == "Hide Spectrum Matches") {
			$(".allPeptideSpectra").hide();
			button.text("Show Spectrum Matches");
		}
		else {
			$(".allPeptideSpectra").show();
			button.text("Hide Spectrum Matches");
		}
	}
</script>
<div 
	id = "psmToggle"
	style="text-decoration: underline; cursor: pointer;font-size: 8pt; color: #3D902A;" 
	onclick="toggleSpectrumMatches()">Hide Spectrum Matches</div>
	
<table width="95%" id="protdetailstbl_<bean:write name="protein" property="protein.id"/>" >
	<thead>
    <tr class="main">
    <th align="left" class="main"><b><font size="2pt">Peptide</font></b></th>
    <th width="10%" align="left" class="main"><b><font size="2pt">Unique</b></th>
    <th width="10%" align="left" class="main"><b><font size="2pt">Charge</font></b></th>
    <th width="10%" align="left" class="main"><b><font size="2pt"># Spectra</font></b></th>
    </tr>
    </thead>
   	<tbody>
       <logic:iterate name="ionList" id="ion">
            <tr class="main">
            <td><bean:write name="ion" property="ionSequence" /></td>
            <td>
            	<logic:equal name="ion" property="isUniqueToProteinGroup" value="true">*</logic:equal>
     			<logic:equal name="ion" property="isUniqueToProteinGroup" value="false">&nbsp</logic:equal>
            </td>
            <td><bean:write name="ion" property="charge" /></td>
            <td><bean:write name="ion" property="spectrumCount" /></td>
            </tr>
           	<tr><td colspan="5">
       			<table align="center" width="70%"
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
		        <logic:iterate name="ion" property="psmList" id="psm">
		        <tr>
		     		<td><bean:write name="psm" property="scanNumber" /></td>
		     		<td><bean:write name="ion" property="charge" /></td>
		     		
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
			</td></tr>
        </logic:iterate>
        </tbody>
        </table>
    