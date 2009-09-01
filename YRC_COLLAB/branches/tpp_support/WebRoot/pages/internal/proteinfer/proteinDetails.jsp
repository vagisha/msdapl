
<%@page import="org.yeastrc.ms.domain.search.Program"%>
<%@page import="org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>

<center>
<div align="center" style="padding:5px;font-size: 10pt;width: 90%; color: black;">

<div style="background-color:#F8F8FF; border: 1px solid #F5F5F5; width:100%" align="center">
<b><bean:write name="protein" property="accession"/></b>
<br>
<span style="color: #888888; font-size: 9pt;"><bean:write name="protein" property="description" /></span>

<br>
<span style="text-decoration: underline; cursor: pointer; font-size: 8pt; color: red;"
      onclick="toggleProteinSequence(<bean:write name="protein" property="protein.id" />)"
      id="protseqbutton_<bean:write name="protein" property="protein.id" />">[View Sequence]</span>
&nbsp; &nbsp;
<span style="font-size: 8pt; color: red;">
<a  style="color:red;"   href="<yrcwww:link path='viewProtein.do?id'/>=<bean:write name='protein' property='protein.nrseqProteinId'/>">[Protein Details]</a>
</span>
<br>
</div>
<br>

<table align="center" cellpadding="2" style="border: 1px solid gray; border-spacing: 2px">
<tr class="pinfer_A">
	<td>Coverage(%)</td>
	<td># Peptides</td>
	<td># Uniq.Peptides</td>
	<td># Spectra </td>
	<logic:equal name="isIdPicker" value="true">
		<td>NSAF** </td>
	</logic:equal>
	<td>Parsimonious</td>
	<td>Other Proteins in Group</td>
	<logic:equal name="isIdPicker" value="true">
		<td>Protein Cluster</td>
	</logic:equal>
</tr>
<tr>
	<td style="border: 1px #CCCCCC dotted;" align="center"><bean:write name="protein" property="protein.coverage" /></td>
	<td style="border: 1px #CCCCCC dotted;" align="center"><bean:write name="protein" property="protein.peptideCount" /></td>
	<td style="border: 1px #CCCCCC dotted;" align="center"><bean:write name="protein" property="protein.uniquePeptideCount" /></td>
	<td style="border: 1px #CCCCCC dotted;" align="center"><bean:write name="protein" property="protein.spectrumCount" /></td>
	<logic:equal name="isIdPicker" value="true">
		<td style="border: 1px #CCCCCC dotted;" align="center"><bean:write name="protein" property="protein.nsafFormatted" /></td>
	</logic:equal>
	<td style="border: 1px #CCCCCC dotted;" align="center">
		<logic:equal name="protein" property="protein.isParsimonious" value="true">Yes</logic:equal>
		<logic:equal name="protein" property="protein.isParsimonious" value="false">No</logic:equal>
	</td>
	<td style="border: 1px #CCCCCC dotted; text-align: left;" align="center">
	<logic:empty name="groupProteins">--</logic:empty>
	<logic:iterate name="groupProteins" id="prot">
		<span onclick="showProteinDetails(<bean:write name="prot" property="protein.id" />)" 
						style="text-decoration: underline; cursor: pointer">
		<bean:write name="prot" property="accession" />
		</span><br>
	</logic:iterate>
	</td>
	<logic:equal name="isIdPicker" value="true">
		<td style="border: 1px #CCCCCC dotted;" align="center">
			<span style="cursor:pointer;text-decoration:underline" 
			  onclick="showProteinCluster(<bean:write name="protein" property="protein.clusterId"/>)">
			<bean:write name="protein" property="protein.clusterId"/>
			</span>
		</td>
	</logic:equal>
	
</tr>
</table>
</div>

<br><br>

<!--  placeholder for protein sequence -->
<div align="center">
<table  align="center" width="60%" id="protseqtbl_<bean:write name='protein' property='protein.id'/>" style="display: none;border:1px solid gray;">
	<tr><td align="left" id="protsequence_<bean:write name="protein" property="protein.id"/>" ></td></tr>
</table>
</div>
<br><br>

	
<table align="center" width="95%" id="protdetailstbl_<bean:write name="protein" property="protein.id"/>" class="table_pinfer">
	<thead>
    <tr class="main">
    <th class="main" style="font-size:10pt;"><b>Uniq</b></th>
    <th class="main" style="font-size:10pt;"><b>Peptide</b></th>
    <th width="10%" align="left" class="main" style="font-size:10pt;"><b>Charge</b></th>
    <th width="10%" align="left" class="main" style="font-size:10pt;"><b># Spectra</b></th>
    <logic:equal name="protInferProgram" value="<%= ProteinInferenceProgram.PROTINFER_SEQ.name()%>">
     	<th class="main" style="font-size:10pt;">Best FDR</th>
     </logic:equal>
     <logic:equal name="protInferProgram" value="<%= ProteinInferenceProgram.PROTINFER_PLCID.name()%>">
     	<th class="main" style="font-size:10pt;">Best FDR</th>
     </logic:equal>
     <logic:equal name="inputGenerator" value="<%=Program.SEQUEST.name() %>">
     	<th class="main" style="font-size:10pt;">DeltaCN</th>
     	<th class="main" style="font-size:10pt;">XCorr</th>
     </logic:equal>
     
     <logic:equal name="inputGenerator" value="<%=Program.PROLUCID.name() %>">
     	<th class="main" style="font-size:10pt;">DeltaCN</th>
     	<th class="main" style="font-size:10pt;">Primary Score</th>
     </logic:equal>
     <logic:equal name="inputGenerator" value="<%=Program.PERCOLATOR.name() %>">
     	<th class="main" style="font-size:10pt;">qValue</th>
     	<th class="main" style="font-size:10pt;">PEP</th>
     </logic:equal>
     <logic:equal name="protInferProgram" value="<%= ProteinInferenceProgram.PROTEIN_PROPHET.name()%>">
     	<th class="sort-float" align="left">nsp</th>
     	<th class="sort-float" align="left">Wt.</th>
     	<th class="sort-float" align="left">Init.Prob.</th>
     	<th class="sort-float" align="left">nsp adj. Prob.</th>
     </logic:equal>
     <logic:equal name="inputGenerator" value="<%=Program.PEPTIDE_PROPHET.name() %>">
     	<!-- <th class="sort-float" align="left">Probability</th> -->
     	<th class="sort-float" align="left">NET</th>
     	<th class="sort-float" align="left">NMC</th>
     	<th class="sort-float" align="left">Mass Diff.</th>
     	<th class="sort-float" align="left">FVal</th>
     </logic:equal>
     <th class="main" style="font-size:10pt;">Spectrum</th>
    </tr>
    </thead>
    
   	<tbody>
       <logic:iterate name="ionList" id="ion">
            <tr class="main">
            <td>
     			<logic:equal name="ion" property="isUniqueToProteinGroup" value="true">*</logic:equal>
     			<logic:equal name="ion" property="isUniqueToProteinGroup" value="false"></logic:equal>
     		</td>
     		<td class="left_align"><bean:write name="ion" property="ionSequence" /></td>
     		<td><bean:write name="ion" property="charge" /></td>
     		<td>
     			<bean:write name="ion" property="spectrumCount" />
     			<span class="showAllIonHits" style="text-decoration: underline; cursor: pointer;font-size: 7pt; color: #000000;" 
				  id="showhitsforion_<bean:write name="ion" property="ion.id" />"
				  onclick="toggleHitsForIon(<bean:write name="ion" property="ion.id" />)"
				  >[Show]</span>
     		</td>
     		
     		<logic:equal name="protInferProgram" value="<%= ProteinInferenceProgram.PROTINFER_SEQ.name()%>">
     			<bean:define name="ion" property="ion.bestSpectrumMatch" id="psm_idp" type="org.yeastrc.ms.domain.protinfer.idpicker.IdPickerSpectrumMatch"/>
     			<td><bean:write name="psm_idp" property="fdrRounded" /></td>
     		</logic:equal>
     		
     		<logic:equal name="protInferProgram" value="<%= ProteinInferenceProgram.PROTINFER_PLCID.name()%>">
     			<bean:define name="ion" property="ion.bestSpectrumMatch" id="psm_idp" type="org.yeastrc.ms.domain.protinfer.idpicker.IdPickerSpectrumMatch"/>
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
     		 
     		 <logic:equal name="protInferProgram" value="<%= ProteinInferenceProgram.PROTEIN_PROPHET.name()%>">
     			<bean:define name="ion" property="ion" id="prophetIon" type="org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetProteinPeptideIon"/>
     		 	<td><bean:write name="prophetIon" property="numSiblingPeptides" /></td>
     		 	<td><bean:write name="prophetIon" property="weight" /></td>
	     		<td><bean:write name="prophetIon" property="initialProbability" /></td>
	     		<td><bean:write name="prophetIon" property="nspAdjProbability" /></td>
     		</logic:equal>
     		 <logic:equal name="inputGenerator" value="<%=Program.PEPTIDE_PROPHET.name() %>">
     		 	<bean:define name="ion" property="bestSpectrumMatch" id="psm_peptProphet" type="org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResult"/>
     		 	<!--  <td><bean:write name="psm_peptProphet" property="probabilityRounded" /></td> -->
	     		<td><bean:write name="psm_peptProphet" property="numEnzymaticTermini" /></td>
	     		<td><bean:write name="psm_peptProphet" property="numMissedCleavages" /></td>
	     		<td><bean:write name="psm_peptProphet" property="massDifferenceRounded" /></td>
	     		<td><bean:write name="psm_peptProphet" property="fValRounded" /></td>
     		</logic:equal>
     		
     		<td><span style="text-decoration: underline; cursor: pointer;" 
				onclick="viewSpectrum(<bean:write name="ion" property="scanId" />, <bean:write name="ion" property="bestSpectrumMatch.id" />)" >
				View
			</span>
			</td>
            </tr>
            
           	<tr>
           		<td colspan="8" align="center">
				  <!--  peptide ion hits table will go here: psmListForIon.jsp -->
				<div align="center" id="hitsforion_<bean:write name="ion" property="ion.id" />"></div>
				</td>
			</tr>
        </logic:iterate>
        </tbody>
        </table>
</center>