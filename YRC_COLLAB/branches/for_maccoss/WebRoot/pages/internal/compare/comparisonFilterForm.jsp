
<%@page import="org.yeastrc.www.compare.DatasetColor"%>
<%@page import="org.yeastrc.bio.go.GOUtils"%>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<html:form action="doProteinSetComparison" method="POST">

	<!-- Does the user want to download the results -->
	<html:hidden name="proteinSetComparisonForm" property="download" value="false" styleId="download" />
	
	<!-- Does the user want to do GO Enrichment analysis-->
	<html:hidden name="proteinSetComparisonForm" property="goEnrichment" value="false" styleId="goEnrichment" />
	
	<!-- Does the user want to do GO Enrichment analysis-->
	<html:hidden name="proteinSetComparisonForm" property="goEnrichmentGraph" value="false" styleId="goEnrichmentGraph" />
	
	
	<logic:iterate name="proteinSetComparisonForm" property="proteinferRunList" id="proteinferRun">
		<logic:equal name="proteinferRun" property="selected" value="true">
			<html:hidden name="proteinferRun" property="runId" indexed="true" />
			<html:hidden name="proteinferRun" property="selected" indexed="true" />
		</logic:equal>
	</logic:iterate>
	
	<logic:iterate name="proteinSetComparisonForm" property="dtaRunList" id="dtaRun">
		<logic:equal name="dtaRun" property="selected" value="true">
			<html:hidden name="dtaRun" property="runId" indexed="true" />
			<html:hidden name="dtaRun" property="selected" indexed="true" />
		</logic:equal>
	</logic:iterate>
	
	<html:hidden name="proteinSetComparisonForm" property="pageNum" styleId="pageNum" />
	
<center>
<br>

<div style="background-color:#F0F8FF; padding: 5 0 5 0; border: 1px solid gray; width:80%">
<table align="center">
	<tr>
		<td valign="middle" style="padding-bottom:10px;">Filter: </td>
		<td style="padding-bottom:10px;"  align="left">
		<table>
		<tr>
		<td valign="top"><b>AND</b></td>
		<td style="padding-right:10px">
			<table cellpadding="0" cellspacing="0">
			<tr>
				<logic:iterate name="proteinSetComparisonForm" property="andList" id="andDataset" indexId="dsIndex">
					
					<logic:equal name="andDataset" property="selected" value="true">
						<td style="background-color:rgb(<%=DatasetColor.get(dsIndex).R %>,<%=DatasetColor.get(dsIndex).G %>,<%=DatasetColor.get(dsIndex).B %>); border:1px solid #AAAAAA;"
							id='<%="AND_"+dsIndex+"_td"%>'
						>
					</logic:equal>
					<logic:notEqual name="andDataset" property="selected" value="true">
						<td style="background-color:#FFFFFF; border:1px solid #AAAAAA;" id='<%="AND_"+dsIndex+"_td"%>' >
					</logic:notEqual>
					<span style="cursor:pointer;" onclick="javascript:toggleAndSelect(<%=dsIndex %>);">&nbsp;&nbsp;</span>
					<html:hidden name="andDataset" property="datasetId" indexed="true" />
					<html:hidden name="andDataset" property="sourceString" indexed="true" />
					<html:hidden name="andDataset" property="selected" indexed="true" styleId='<%="AND_"+dsIndex+"_select"%>' />
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
					
					<logic:equal name="orDataset" property="selected" value="true">
						<td style="background-color:rgb(<%=DatasetColor.get(dsIndex).R %>,<%=DatasetColor.get(dsIndex).G %>,<%=DatasetColor.get(dsIndex).B %>); border:1px solid #AAAAAA;"
							id='<%="OR_"+dsIndex+"_td"%>'
						>
					</logic:equal>
					<logic:notEqual name="orDataset" property="selected" value="true">
						<td style="background-color:#FFFFFF; border:1px solid #AAAAAA;" id='<%="OR_"+dsIndex+"_td"%>'>
					</logic:notEqual>
					<span style="cursor:pointer;" onclick="javascript:toggleOrSelect(<%=dsIndex %>);">&nbsp;&nbsp;</span>
					<html:hidden name="orDataset" property="datasetId" indexed="true" />
					<html:hidden name="orDataset" property="sourceString" indexed="true" />
					<html:hidden name="orDataset" property="selected" indexed="true" styleId='<%="OR_"+dsIndex+"_select"%>' />
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
					
					<logic:equal name="notDataset" property="selected" value="true">
						<td style="background-color:rgb(<%=DatasetColor.get(dsIndex).R %>,<%=DatasetColor.get(dsIndex).G %>,<%=DatasetColor.get(dsIndex).B %>); border:1px solid #AAAAAA;"
							id='<%="NOT_"+dsIndex+"_td"%>'
						>
					</logic:equal>
					<logic:notEqual name="notDataset" property="selected" value="true">
						<td style="background-color:#FFFFFF; border:1px solid #AAAAAA;" id='<%="NOT_"+dsIndex+"_td"%>'>
					</logic:notEqual>
					<span style="cursor:pointer;" onclick="javascript:toggleNotSelect(<%=dsIndex %>);">&nbsp;&nbsp;</span>
					<html:hidden name="notDataset" property="datasetId" indexed="true" />
					<html:hidden name="notDataset" property="sourceString" indexed="true" />
					<html:hidden name="notDataset" property="selected" indexed="true" styleId='<%="NOT_"+dsIndex+"_select"%>' />
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
					
					<logic:equal name="xorDataset" property="selected" value="true">
						<td style="background-color:rgb(<%=DatasetColor.get(dsIndex).R %>,<%=DatasetColor.get(dsIndex).G %>,<%=DatasetColor.get(dsIndex).B %>); border:1px solid #AAAAAA;"
							id='<%="XOR_"+dsIndex+"_td"%>'
						>
					</logic:equal>
					<logic:notEqual name="xorDataset" property="selected" value="true">
						<td style="background-color:#FFFFFF; border:1px solid #AAAAAA;" id='<%="XOR_"+dsIndex+"_td"%>'>
					</logic:notEqual>
					<span style="cursor:pointer;" onclick="javascript:toggleXorSelect(<%=dsIndex %>);">&nbsp;&nbsp;</span>
					<html:hidden name="xorDataset" property="datasetId" indexed="true" />
					<html:hidden name="xorDataset" property="sourceString" indexed="true" />
					<html:hidden name="xorDataset" property="selected" indexed="true" styleId='<%="XOR_"+dsIndex+"_select"%>' />
					</td>
					
				</logic:iterate>
			</tr>
			</table>
		</td>
		</tr>
		</table>
		</td>
		
		<!-- ################## PARSIMONIOUS ONLY CHECKBOX	  ########################### -->
		<td valign="top" style="padding-right:10px;"><html:checkbox name="proteinSetComparisonForm" property="onlyParsimonious">Only Parsimonious</html:checkbox> </td>

		<!-- ################## GROUP PROTEINS CHECKBOX	  ########################### -->
		<logic:notPresent name="goEnrichmentView">
			<td valign="top"><html:checkbox name="proteinSetComparisonForm" property="groupProteins">Group Indistinguishable Proteins</html:checkbox> </td>
		</logic:notPresent>
	</tr>
	
	<tr>
		<!-- ################## SEARCH BOX	  ########################################### -->
		<td valign="top">
			Search:
		</td>
		<td style="padding-left:5px;"> 
			Fasta ID(s): <html:text name="proteinSetComparisonForm" property="nameSearchString" size="40"></html:text><br>
 			<span style="font-size:8pt;">Enter a comma-separated list of complete or partial FASTA identifiers.</span>
 		</td>
 		<td style="padding-left:5px;" colspan="2"> 
			Description: <html:text name="proteinSetComparisonForm" property="descriptionSearchString" size="40"></html:text><br>
 			<span style="font-size:8pt;">Enter a comma-separated list of terms.</span>
 		</td>
	</tr>
	
	<logic:notPresent name="goEnrichmentView">
	<tr>
		<td valign="top" align="center" colspan="4" style="padding-top:5px;">
 			<html:submit value="Update" onclick="javascript:updateResults();" styleClass="plain_button"></html:submit> &nbsp;
		</td>
	</tr>
	<tr>
		<td valign="bottom" align="center" colspan="4">
			<span style="color:red; font-size:9pt; text-decoration:underline;" class="clickable" onclick="javascript:downloadResults(); return false;">[Download Results]</span>
			&nbsp;
			<html:checkbox name="proteinSetComparisonForm" property="collapseProteinGroups">Collapse Protein Groups</html:checkbox>
			&nbsp;
			<html:checkbox name="proteinSetComparisonForm" property="includeDescriptions">Include Description</html:checkbox>
		</td>
	</tr>
	</logic:notPresent>
	
</table>
</div>

<logic:equal name="showGOForm" value="true">

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