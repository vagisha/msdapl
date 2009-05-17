
<%@page import="org.yeastrc.www.compare.DatasetColor"%>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<html:form action="doProteinSetComparison" method="POST">
	<!-- Does the user want to download the results -->
	<html:hidden name="proteinSetComparisonForm" property="download" value="false" styleId="download" />
	
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
		<td>
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
		</tr>
		</table>
		</td>
		
		<!-- ################## PARSIMONIOUS ONLY CHECKBOX	  ########################### -->
		<td valign="top" style="padding-right:10px;"><html:checkbox name="proteinSetComparisonForm" property="onlyParsimonious">Only Parsimonious</html:checkbox> </td>

		<!-- ################## GROUP PROTEINS CHECKBOX	  ########################### -->
		<td valign="top"><html:checkbox name="proteinSetComparisonForm" property="groupProteins">Group Indistinguishable Proteins</html:checkbox> </td>
	</tr>
	<tr>
		<!-- ################## SEARCH BOX	  ########################################### -->
		<td valign="top">
			Search:
		</td>
		<td style="padding-left:5px;"> 
			<html:text name="proteinSetComparisonForm" property="searchString" size="40"></html:text><br>
 			<span style="font-size:8pt;">Enter a comma-separated list of complete or partial identifiers</span>
 		</td>
 		
 		<td valign="top" align="center" colspan="2">
 			<html:submit value="Update" onclick="javascript:updateResults();"></html:submit> &nbsp;
			<span style="color:red; font-size:8pt; text-decoration:underline;" class="clickable" onclick="javascript:downloadResults(); return false;">[Download Results]</span>
		</td>
	</tr>
</table>
</div>

</center>
</html:form>