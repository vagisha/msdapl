
<%@page import="org.yeastrc.bio.go.GOUtils"%>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


<script type="text/javascript">
// Popup window code
function newPopup(url) {
	popupWindow = window.open(
		url,'popUpWindow','height=600,width=500,left=10,top=10,resizable=yes,scrollbars=yes,toolbar=no,menubar=no,location=no,directories=no,status=yes')
}
function toggleColumnChooser() {
	var text = $("#columnChooser").text();
	//alert(text);
	if(text == "Choose Columns") {
		$("#columnChooser").text("Hide Column Chooser");
		$("#columnChooserTgt").show();
	}
	else {
		$("#columnChooser").text("Choose Columns");
		$("#columnChooserTgt").hide();
	}
}
</script>


<html:form action="updateProteinSetComparison" method="POST">

	<!-- Does the user want to download the results -->
	<html:hidden name="proteinSetComparisonForm" property="download" value="false" styleId="download" />
	
	<!-- Does the user want to cluster results -->
	<html:hidden name="proteinSetComparisonForm" property="cluster" styleId="cluster" />
	<html:hidden name="proteinSetComparisonForm" property="clusteringToken" />
	<html:hidden name="proteinSetComparisonForm" property="newToken" />
	
	<!-- Does the user want to do GO Enrichment analysis-->
	<html:hidden name="proteinSetComparisonForm" property="goEnrichment" value="false" styleId="goEnrichment" />
	
	<!-- Does the user want to do GO Enrichment analysis-->
	<html:hidden name="proteinSetComparisonForm" property="goEnrichmentGraph" value="false" styleId="goEnrichmentGraph" />

	<!-- Sorting criteria for the results -->
	<html:hidden name="proteinSetComparisonForm" property="sortByString"  styleId="sortBy" />
	<html:hidden name="proteinSetComparisonForm" property="sortOrderString"  styleId="sortOrder" />
	
	
	<html:hidden name="proteinSetComparisonForm" property="numPerPage" styleId="numPerPage" />
	<html:hidden name="proteinSetComparisonForm" property="pageNum" styleId="pageNum" />
	<html:hidden name="proteinSetComparisonForm" property="rowIndex" styleId="rowIndex" />
	
<center>
<br>

<div style="background-color:#F0F8FF; padding: 5 0 5 0; border: 1px solid gray; width:80%">
<table align="center">
	<tr>
		<td valign="middle" style="padding: 0 0 10 5;">Filter: </td>
		<td style="padding-bottom:10px;"  align="left" colspan="3">
		<table>
		<tr>
		<td valign="top"><b>AND</b></td>
		<td style="padding-right:10px">
			<table cellpadding="0" cellspacing="0">
			<tr>
				<logic:iterate name="proteinSetComparisonForm" property="andList" id="andDataset" indexId="dsIndex">
					
					<bean:define name="andDataset" property="datasetIndex" id="datasetIndex"/>
					<logic:equal name="andDataset" property="selected" value="true">
						<td style="background-color:rgb(<bean:write name='andDataset' property='red'/>,<bean:write name='andDataset' property='green'/>,<bean:write name='andDataset' property='blue'/>); border:1px solid #AAAAAA;"
							id="AND_<bean:write name='datasetIndex'/>_td"
						>
					</logic:equal>
					<logic:notEqual name="andDataset" property="selected" value="true">
						<td style="background-color:#FFFFFF; border:1px solid #AAAAAA;" id="AND_<bean:write name='datasetIndex'/>_td" >
					</logic:notEqual>
					<span style="cursor:pointer;" onclick="javascript:toggleAndSelect(<bean:write name='datasetIndex'/>, <bean:write name='andDataset' property='red'/>,<bean:write name='andDataset' property='green'/>,<bean:write name='andDataset' property='blue'/>);">&nbsp;&nbsp;</span>
					<html:hidden name="andDataset" property="datasetId" indexed="true" />
					<html:hidden name="andDataset" property="datasetIndex" indexed="true" />
					<html:hidden name="andDataset" property="sourceString" indexed="true" />
					<html:hidden name="andDataset" property="selected" indexed="true" 
				             styleId='<%= "AND_"+datasetIndex+"_select"%>' />
				</td>
				</logic:iterate>
			</tr>
			</table>
		</td>
		<td valign="top"><b>OR</b></td>
		<td style="padding-right:10px">
			<table  cellpadding="0" cellspacing="0">
			<tr>
				<logic:iterate name="proteinSetComparisonForm" property="orList" id="orDataset" indexId="dsIndex">
					
					<bean:define name="orDataset" property="datasetIndex" id="datasetIndex"/>
					<logic:equal name="orDataset" property="selected" value="true">
						<td style="background-color:rgb(<bean:write name='orDataset' property='red'/>,<bean:write name='orDataset' property='green'/>,<bean:write name='orDataset' property='blue'/>); border:1px solid #AAAAAA;"
							id="OR_<bean:write name='datasetIndex'/>_td"
						>
					</logic:equal>
					<logic:notEqual name="orDataset" property="selected" value="true">
						<td style="background-color:#FFFFFF; border:1px solid #AAAAAA;" id="OR_<bean:write name='datasetIndex'/>_td">
					</logic:notEqual>
					<span style="cursor:pointer;" onclick="javascript:toggleOrSelect(<bean:write name='datasetIndex'/>,<bean:write name='orDataset' property='red'/>,<bean:write name='orDataset' property='green'/>,<bean:write name='orDataset' property='blue'/>);">&nbsp;&nbsp;</span>
					<html:hidden name="orDataset" property="datasetId" indexed="true" />
					<html:hidden name="orDataset" property="datasetIndex" indexed="true" />
					<html:hidden name="orDataset" property="sourceString" indexed="true" />
					<html:hidden name="orDataset" property="selected" indexed="true" 
								styleId='<%= "OR_"+datasetIndex+"_select"%>' />
				</td>

				</logic:iterate>
			</tr>
			</table>
		</td>
		<td valign="top"><b>NOT</b></td>
		<td style="padding-right:10px">
			<table  cellpadding="0" cellspacing="0">
			<tr>
				<logic:iterate name="proteinSetComparisonForm" property="notList" id="notDataset" indexId="dsIndex">
					
					<bean:define name="notDataset" property="datasetIndex" id="datasetIndex"/>
					<logic:equal name="notDataset" property="selected" value="true">
						<td style="background-color:rgb(<bean:write name='notDataset' property='red'/>,<bean:write name='notDataset' property='green'/>,<bean:write name='notDataset' property='blue'/>); border:1px solid #AAAAAA;"
							id="NOT_<bean:write name='datasetIndex'/>_td"
						>
					</logic:equal>
					<logic:notEqual name="notDataset" property="selected" value="true">
						<td style="background-color:#FFFFFF; border:1px solid #AAAAAA;" id="NOT_<bean:write name='datasetIndex'/>_td">
					</logic:notEqual>
					<span style="cursor:pointer;" onclick="javascript:toggleNotSelect(<bean:write name='datasetIndex'/>,<bean:write name='notDataset' property='red'/>,<bean:write name='notDataset' property='green'/>,<bean:write name='notDataset' property='blue'/>);">&nbsp;&nbsp;</span>
					<html:hidden name="notDataset" property="datasetId" indexed="true" />
					<html:hidden name="notDataset" property="datasetIndex" indexed="true" />
					<html:hidden name="notDataset" property="sourceString" indexed="true" />
					<html:hidden name="notDataset" property="selected" indexed="true" 
								styleId='<%= "NOT_"+datasetIndex+"_select"%>' />
				</td>
					
				</logic:iterate>
			</tr>
			</table>
		</td>
		<td valign="top"><b>XOR</b></td>
		<td>
			<table  cellpadding="0" cellspacing="0">
			<tr>
				<logic:iterate name="proteinSetComparisonForm" property="xorList" id="xorDataset" indexId="dsIndex">
					
					<bean:define name="xorDataset" property="datasetIndex" id="datasetIndex"/>
					<logic:equal name="xorDataset" property="selected" value="true">
						<td style="background-color:rgb(<bean:write name='xorDataset' property='red'/>,<bean:write name='xorDataset' property='green'/>,<bean:write name='xorDataset' property='blue'/>); border:1px solid #AAAAAA;"
							id="XOR_<bean:write name='datasetIndex'/>_td"
						>
					</logic:equal>
					<logic:notEqual name="xorDataset" property="selected" value="true">
						<td style="background-color:#FFFFFF; border:1px solid #AAAAAA;" id="XOR_<bean:write name='datasetIndex'/>_td">
					</logic:notEqual>
					<span style="cursor:pointer;" onclick="javascript:toggleXorSelect(<bean:write name='datasetIndex'/>,<bean:write name='xorDataset' property='red'/>,<bean:write name='xorDataset' property='green'/>,<bean:write name='xorDataset' property='blue'/>);">&nbsp;&nbsp;</span>
					<html:hidden name="xorDataset" property="datasetId" indexed="true" />
					<html:hidden name="xorDataset" property="datasetIndex" indexed="true" />
					<html:hidden name="xorDataset" property="sourceString" indexed="true" />
					<html:hidden name="xorDataset" property="selected" indexed="true" 
								styleId='<%= "XOR_"+datasetIndex+"_select"%>' />
					</td>
					
				</logic:iterate>
			</tr>
			</table>
		</td>
		</tr>
		</table>
		</td>
	</tr>
	
	<tr>
		<td valign="top" style="padding-bottom: 10px;">Include Proteins:</td>
		<td valign="top" colspan="3" style="padding-bottom: 10px;">
			<html:radio name="proteinSetComparisonForm" property="parsimoniousParam" value="0"><b>All</b></html:radio>
			<html:radio name="proteinSetComparisonForm" property="parsimoniousParam" value="1"><b>Parsimonious in >= 1 Dataset</b></html:radio>
			<html:radio name="proteinSetComparisonForm" property="parsimoniousParam" value="2"><b>Parsimonious in ALL Datasets</b></html:radio>
			<logic:equal name="proteinSetComparisonForm" property="hasProteinProphetDatasets" value="true">
			<br>
			<span style="font-size:8pt;">
				NOTE: For ProteinProphet datasets "parsimonious" = NOT "subsumed"
			</span>
			</logic:equal>
		</td>
	</tr>
	
	
	<tr><td colspan="4" style="border: 1px solid rgb(170, 170, 170); background-color: white;padding:1"><span></span></td></tr>
	<tr><td colspan="4" style="padding:4"><span></span></td></tr>
	
	<!-- ################## MOLECULAR WT. AND pI FILTERS	  ########################################### -->
	<tr>
		<td style="padding: 0 0 5 5;"><b>Mol. Wt:</b> </td>
		<td style="padding: 0 5 5 0" align="left">
			Min. <html:text name="proteinSetComparisonForm" property="minMolecularWt" size="8"></html:text> 
		    Max. <html:text name="proteinSetComparisonForm" property="maxMolecularWt" size="8"></html:text>
		</td>
		<td style="padding:0 0 5 5;"><b>pI:</b></td>
		<td style="padding:0 0 5 0;" align="left">
			Min. <html:text name="proteinSetComparisonForm" property="minPi" size="8"></html:text> 
			Max. <html:text name="proteinSetComparisonForm" property="maxPi" size="8"></html:text>
		</td>
	</tr>
	
	<!-- ################## MIN / MAX PEPTIDES FILTERS	  ########################################### -->
	<tr>
		<td style="padding: 0 0 5 5;"><b># Peptides*:</b> </td>
		<td style="padding: 0 5 5 0" align="left">
			Min. <html:text name="proteinSetComparisonForm" property="minPeptides" size="8"></html:text> 
		    Max. <html:text name="proteinSetComparisonForm" property="maxPeptides" size="8"></html:text>
		</td>
		<td style="padding:0 0 5 5;"><b># Uniq. Peptides*:</b></td>
		<td style="padding:0 0 5 0;" align="left">
			Min. <html:text name="proteinSetComparisonForm" property="minUniquePeptides" size="8"></html:text> 
			Max. <html:text name="proteinSetComparisonForm" property="maxUniquePeptides" size="8"></html:text>
			<!--
			<html:checkbox name="proteinSetComparisonForm"  property="peptideUniqueSequence">Unique Sequence</html:checkbox>
			-->
		</td>
	</tr>
	<tr><td colspan="4"><span style="font-size:8pt;">* Peptide = sequence + modifications + charge</span></td></tr>
	
	<!-- ################## PEPTIDE PROBABILITY FILTERS	  ########################################### -->
	<logic:equal name="proteinSetComparisonForm" property="hasProteinProphetDatasets" value="true">
	<tr>
		<td><b>Min. Peptide Probability: </b></td>
		<td style="padding-left: 5px;"><html:text name="proteinSetComparisonForm" property="minPeptideProbability" size="8"></html:text></td>
		<td colspan="2">Apply to: 
			<html:checkbox name="proteinSetComparisonForm" property="applyProbToPept"># Peptides</html:checkbox>
			<html:checkbox name="proteinSetComparisonForm" property="applyProbToUniqPept"># Uniq. Peptides</html:checkbox>
		</td>
	</tr>
	</logic:equal>
	
	
	<!-- ################## PROTEIN PROPHET OPTIONS	  ########################### -->
	<logic:equal name="proteinSetComparisonForm" property="hasProteinProphetDatasets" value="true">
	<tr>
		<td valign="top" style="padding-bottom: 10px;"><b>ProteinProphet Error: </b></td>
		<td valign="top" style="padding-bottom: 10px; padding-left: 5px;">
			<html:text name="proteinSetComparisonForm" property="errorRate"></html:text>
			<br>
			<span style="font-size:8pt;">
				The error rate closest to the one entered above<br>will be used to determine the probability cutoff
			</span>
		</td>
		<td colspan="2" style="padding-bottom: 10px;">
			Cutoff on Protein Group Probability<html:checkbox property="useProteinGroupProbability" />
			<br>
			<span style="font-size:8pt;">
				If checked, probability cutoff will be applied to protein group probability.
				<br>Otherwise cutoff will be applied to individual protein probability.
			</span>
		</td>
	</tr>
	</logic:equal>
	
	
	<tr><td colspan="4" style="border: 1px solid rgb(170, 170, 170); background-color: white;padding:1"><span></span></td></tr>
	<tr><td colspan="4" style="padding:4"><span></span></td></tr>
	
	<tr>
		<!-- ################## SEARCH BOX	  ########################################### -->
		<td style="padding-left:5px;" valign="top">Fasta ID(s):</td>
		<td style="padding:0 5 5 0;" colspan="3"> 
			<html:text name="proteinSetComparisonForm" property="accessionLike" size="40"></html:text><br>
 			<span style="font-size:8pt;">Enter a comma-separated list of complete or partial FASTA identifiers.</span>
 		</td>
 		<td></td>
 	</tr>
 	<tr>
 		<td style="padding-left:5px;" valign="top">Description Include: </td>
 		<td style="padding:0 5 5 0;"> 
			<html:text name="proteinSetComparisonForm" property="descriptionLike" size="40"></html:text>
 		</td>
 		<td style="padding-left:5px;" valign="top"> Exclude:</td>
 		<td> 
			<html:text name="proteinSetComparisonForm" property="descriptionNotLike" size="40"></html:text>
			<span style="font-size:8pt;"><nobr>Search All:<html:checkbox property="proteinSetComparisonForm" property="searchAllDescriptions"></html:checkbox></nobr></span>
 		</td>
 	</tr>
 	<tr>
  		<td></td>
  		<td colspan="3" ">
  			<div style="font-size:8pt;" align="left">Enter a comma-separated list of terms.
  			Descriptions will be included from the fasta file(s) associated with the data-sets <br>
  			being compared, as well as species specific databases (e.g. SGD) 
  			for any associated target species.
  			<br>Check "Search All" to include descriptions from Swiss-Prot and NCBI-NR. 
  			<br/><font color="red">NOTE: Description searches can be time consuming, especially when "Search All" is checked.</font></div>
  		</td>
  		</tr>
 	
 	
 	<logic:notPresent name="goEnrichmentView">
 	<tr><td colspan="4" style="border: 1px solid rgb(170, 170, 170); background-color: white;padding:1"><span></span></td></tr>
	<tr><td colspan="4" style="padding:4"><span></span></td></tr>
	<tr>
		<td valign="top" colspan="1"><html:checkbox name="proteinSetComparisonForm" property="groupIndistinguishableProteins">Group Indistinguishable Proteins</html:checkbox> </td>
		<td valign="top" align="left" colspan="2">
			<html:checkbox name="proteinSetComparisonForm" property="keepProteinGroups">Keep Protein Groups</html:checkbox><br>
			<span style="font-size:8pt;">Display ALL protein group members even if some of them do not pass the filtering criteria.</span>
 		</td>
 		<td valign="middle" align="center" colspan="1">	
 			<span class="clickable underline" id="columnChooser" 
 			      onclick="toggleColumnChooser();">Choose Columns</span>&nbsp;
 			<html:submit value="Update" onclick="javascript:updateResults();" styleClass="plain_button" style="margin-top:0px;"></html:submit>
			
		</td>
	</tr>
	<tr>
	<td colspan="4" align="center">
	<div id="columnChooserTgt" class="small_font" align="left" 
		 style="padding: 5 0 5 0; border: 1px solid gray; width:50%; display:none">
	
		<html:checkbox name="proteinSetComparisonForm" property="showPresent">Present / Not-present</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showFastaId">Fasta ID</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showCommonName">Common Name</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showDescription">Description</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showMolWt">Molecular Wt.</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showPi">pI</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showTotalSeq">Total # Sequences</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showNumSeq"># Sequences (S) for a dataset</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showNumIons"># Ions (I) for a dataset</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showNumUniqIons"># Unique Ions (U.I) for a dataset</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showSpectrumCount">Spectrum Count (SC) for a dataset</html:checkbox>
		</div>
	</td>
	</tr>
 	</logic:notPresent>
 	
	
</table>
</div>

<!-- CLUSTER RSULTS -->
<logic:notPresent name="goEnrichmentView">
<div align="center" style="background-color:#F0F8FF; padding: 5 0 5 0; border: 1px solid gray; width:80%; margin-top:10px;">
	<b>Clustering Options: </b>
	<html:checkbox name="proteinSetComparisonForm" property="useLogScale">Log(2) Scale</html:checkbox>
		&nbsp;
	Replace missing with: 
	<html:text name="proteinSetComparisonForm" property="replaceMissingWithValue"></html:text>
	&nbsp;
	<html:submit value="Cluster" onclick="javascript:clusterResults();" styleClass="plain_button" style="margin-top:0px;"></html:submit>
	
	<logic:present name="clusteredImgUrl">
		&nbsp;&nbsp;
		<nobr>
		<b>Heatmap:</b>
		<span style="background-color:yellow;"><a href="JavaScript:newPopup('<bean:write name='clusteredImgUrl'/>');"><b>PDF</b></a></span>
		<span style="background-color:yellow;"><a href="JavaScript:newPopup('<yrcwww:link path='heatmap.do?token='/><bean:write name="proteinSetComparisonForm" property='clusteringToken' />');"><b>HTML</b></a></span>
		</nobr>
	</logic:present>
</div>
</logic:notPresent>


<!-- DOWNLOAD RESULTS -->
<logic:notPresent name="goEnrichmentView">
<div align="center" style="background-color:#F0F8FF; padding: 5 0 5 0; border: 1px solid gray; width:80%; margin-top:10px;">
		<b>Download:</b>
		<html:checkbox name="proteinSetComparisonForm" property="collapseProteinGroups">Collapse Protein Groups</html:checkbox>
		&nbsp;
		<html:checkbox name="proteinSetComparisonForm" property="includeDescriptions">Include Description</html:checkbox>
		<html:submit value="Download" onclick="javascript:downloadResults(); return false;" styleClass="plain_button" style="margin-top:0px;"></html:submit>
		&nbsp;
</div>
</logic:notPresent>

<!-- GO ENRICHMENT -->
<logic:equal name="speciesIsYeast" value="true">
<br>
<div align="center"
	style="background-color:#F0F8FF; padding: 5 0 5 0; border: 1px solid gray; width:80%">
	<b>GO Enrichment:</b>
	<html:select name="proteinSetComparisonForm" property="goAspect">
		<html:option
			value="<%=String.valueOf(GOUtils.BIOLOGICAL_PROCESS) %>">Biological Process</html:option>
		<html:option
			value="<%=String.valueOf(GOUtils.CELLULAR_COMPONENT) %>">Cellular Component</html:option>
		<html:option
			value="<%=String.valueOf(GOUtils.MOLECULAR_FUNCTION) %>">Molecular Function</html:option>
	</html:select>
	&nbsp; &nbsp; Species:
	<html:select name="proteinSetComparisonForm" property="speciesId">
		<html:option value="4932">Saccharomyces cerevisiae </html:option>
	</html:select>
	&nbsp; &nbsp; P-Value:
	<html:text name="proteinSetComparisonForm" property="goEnrichmentPVal"></html:text>
	&nbsp; &nbsp;
	<html:submit value="Calculate" onclick="javascript:doGoEnrichmentAnalysis();"></html:submit>
	<logic:present name="goEnrichmentView">
		<html:submit value="Create Graph" onclick="javascript:doGoEnrichmentAnalysisGraph();"></html:submit>
	</logic:present>
</div>
</logic:equal>
</center>
</html:form>