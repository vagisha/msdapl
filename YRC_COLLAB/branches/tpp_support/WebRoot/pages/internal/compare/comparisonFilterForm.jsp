
<%@page import="org.yeastrc.bio.go.GOUtils"%>
<%@page import="org.yeastrc.www.compare.DisplayColumns"%>
<%@page import="org.yeastrc.www.compare.dataset.DatasetColor"%>
<%@page import="org.yeastrc.www.compare.clustering.ClusteringConstants"%>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<script type="text/javascript" src="<yrcwww:link path='/js/jquery.cookie.js'/>"></script>
<script type="text/javascript" src="<yrcwww:link path='/js/jquery.listreorder.js'/>"></script>
<script type="text/javascript" src="<yrcwww:link path='/js/jquery.disable.text.select.pack.js'/>"></script>
<script type="text/javascript">
// Popup window code
function newPopup(url) {
	popupWindow = window.open(
		url,'popUpWindow','height=600,width=600,left=10,top=10,resizable=yes,scrollbars=yes,toolbar=no,menubar=no,location=yes,directories=no,status=yes')
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

function toggleOrderChanger() {

	var text = $("#orderChanger").text();
	//alert(text);
	if(text == "Order Datasets") {
		$("#orderChanger").text("Hide Dataset Order");
		$("#orderChangerTgt").show();
	}
	else {
		$("#orderChanger").text("Order Datasets");
		$("#orderChangerTgt").hide();
	}
}

function saveDisplayColumnsCookie() {
	//alert("saving cookie");
	var cookieVal = "";
	$(".colChooser").each(function() {
		if($(this).is(":checked")) {}
		else {cookieVal += "_"+$(this).attr('title')};
	});
	
	if(cookieVal.length > 0) {
		cookieVal = cookieVal.substring(1);
		//alert(cookieVal);
		var COOKIE_NAME = 'noDispCols_compare';
		var options = { path: '/', expires: 100 };
    	$.cookie(COOKIE_NAME, cookieVal, options);
    }
	
	return false;
}
$(document).ready(function() {
	
	// reset the form.  When clicking the reload button the form is 
	// not resest, so we reset it manually. 
 	$("form")[0].reset();
 	
	var datasetList = $('ol#datasetList').ListReorder();
	datasetList.bind('listorderchanged', function(evt, jqList, listOrder) {
		var str = "";
		for (var i = 0; i < listOrder.length; i++) {
			// i is current datasetIndex; listOrder[i] is the original datasetIndex
			//var origIndex = $("#AND_index_"+listOrder[i]).val();
			$("#AND_index_"+listOrder[i]).val(i);
			$("#OR_index_"+listOrder[i]).val(i);
			$("#NOT_index_"+listOrder[i]).val(i);
			$("#XOR_index_"+listOrder[i]).val(i);
			//var newIndex = $("#AND_index_"+listOrder[i]).val();
			//str += "orig: "+origIndex+"; new: "+newIndex+"\n";
		}
		//alert(str);
	});
	
	$('#resetDatasetOrder').click(function(){
		datasetList.get(0).restoreOrder();
		return false;
	});
	
});
 	

</script>


<html:form action="updateProteinSetComparison" method="POST">

	<!-- Does the user want to download the results -->
	<html:hidden name="proteinSetComparisonForm" property="download" value="false" styleId="download" />
	
	<!-- Does the user want to cluster results -->
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
					<html:hidden name="andDataset" property="datasetIndex" indexed="true" 
								styleId='<%= "AND_index_"+datasetIndex%>'/>
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
					<html:hidden name="orDataset" property="datasetIndex" indexed="true"
								 styleId='<%= "OR_index_"+datasetIndex%>'/>
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
					<html:hidden name="notDataset" property="datasetIndex" indexed="true" 
								 styleId='<%= "NOT_index_"+datasetIndex%>'/>
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
					<html:hidden name="xorDataset" property="datasetIndex" indexed="true" 
								 styleId='<%= "XOR_index_"+datasetIndex%>'/>
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
		<td style="padding: 0 0 0 5;"><b># Peptides*:</b> </td>
		<td style="padding: 0 5 0 0" align="left">
			Min. <html:text name="proteinSetComparisonForm" property="minPeptides" size="8"></html:text> 
		    Max. <html:text name="proteinSetComparisonForm" property="maxPeptides" size="8"></html:text>
		</td>
		<td style="padding:0 0 0 5;"><b># Uniq. Peptides*:</b></td>
		<td style="padding:0 0 0 0;" align="left">
			Min. <html:text name="proteinSetComparisonForm" property="minUniquePeptides" size="8"></html:text> 
			Max. <html:text name="proteinSetComparisonForm" property="maxUniquePeptides" size="8"></html:text>
			<!--
			<html:checkbox name="proteinSetComparisonForm"  property="peptideUniqueSequence">Unique Sequence</html:checkbox>
			-->
		</td>
	</tr>
	<tr><td colspan="4" style="padding-bottom:10px;"><span style="font-size:8pt;">* Peptide = sequence + modifications + charge</span></td></tr>
	
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
 	
 	<tr><td colspan="4" style="border: 1px solid rgb(170, 170, 170); background-color: white;padding:1"><span></span></td></tr>
	<tr><td colspan="4" style="padding:4"><span></span></td></tr>
	
 	<!-- CLUSTERING OPTIONS -->
 	<logic:notPresent name="goEnrichmentView">
	<tr>
	<td><b>Clustering Options: </b></td>
	<td colspan="3">
	
		Gradient:
		<html:select name="proteinSetComparisonForm" property="heatMapGradientString">
			<html:option value="<%=ClusteringConstants.GRADIENT.BY.getDisplayName() %>"></html:option>
			<html:option value="<%=ClusteringConstants.GRADIENT.GR.getDisplayName() %>"></html:option>
		</html:select>
		&nbsp;
		
		<html:checkbox name="proteinSetComparisonForm" property="scaleRows"><nobr>Scale Rows</nobr></html:checkbox>
		&nbsp;
		
		<html:checkbox name="proteinSetComparisonForm" property="clusterColumns"><nobr>Cluster Columns</nobr></html:checkbox>
		&nbsp;
		
		<html:checkbox name="proteinSetComparisonForm" property="useLogScale">Log Scale</html:checkbox>
		&nbsp;
		
		Base:
		<html:select name="proteinSetComparisonForm" property="logBase">
			<html:option value="10"></html:option>
			<html:option value="2"></html:option>
		</html:select>
		&nbsp;
		
		Replace missing with: 
		<html:text name="proteinSetComparisonForm" property="replaceMissingWithValue" size="3"></html:text>
		&nbsp; 
		
		
	</td>
	</tr>
	<tr><td colspan="4" style="padding:4"><span></span></td></tr>
	<tr><td colspan="4" style="border: 1px solid rgb(170, 170, 170); background-color: white;padding:1"><span></span></td></tr>
	<tr><td colspan="4" style="padding:4"><span></span></td></tr>
	</logic:notPresent>
	
	
	<logic:notPresent name="goEnrichmentView">
	<tr>
		<td valign="top" align="left">
		<span class="clickable underline" id="columnChooser" 
			      onclick="toggleColumnChooser();">Choose Columns</span>
		</td>
		<td valign="top">
			<html:checkbox name="proteinSetComparisonForm" property="cluster" styleId="cluster">Cluster Spectrum Counts</html:checkbox>
			<br/><span class="small_font">Normalized spectrum counts are used</span>
		</td>
		<td valign="top" colspan="2">
			<html:checkbox name="proteinSetComparisonForm" property="groupIndistinguishableProteins">Group Indistinguishable Proteins</html:checkbox>
		</td>
	</tr>
	<tr>
		<td>
			<span class="clickable underline" id="orderChanger" 
 			      onclick="toggleOrderChanger();">Order Datasets</span> 
		</td>
		<td></td>
		<td valign="top" align="left" colspan="2">
			<html:checkbox name="proteinSetComparisonForm" property="keepProteinGroups">Keep Protein Groups</html:checkbox><br>
			<span style="font-size:8pt;">Display ALL protein group members even if some of them do not pass the filtering criteria.</span>
 		</td>
 	</tr>
 	
	<tr><td colspan="4" style="padding:4"><span></span></td></tr>
	
	<tr>
	<td colspan="4" align="center">
	
	<!-- DATASET ORDER CHANGER -->
	<div id="orderChangerTgt" class="small_font" align="left" 
		 style="padding: 5 0 5 0; border: 1px solid gray; width:50%; display:none">
		<ol id="datasetList">
			<logic:iterate name="proteinSetComparisonForm" property="andList" id="andDataset" indexId="row">
				<li style="margin: 0 5 5 5">
				<span style="display:block; float:left; width:10px; height:10px; background: rgb(<%=DatasetColor.get(row).R %>,<%=DatasetColor.get(row).G %>,<%=DatasetColor.get(row).B %> );;"></span>&nbsp;
				<span id="<bean:write name='andDataset' property='datasetIndex' />"><b>ID <bean:write name="andDataset" property="datasetId" /></b>
				<bean:write name="andDataset" property="datasetComments" />
				</span>
				</li>
			</logic:iterate>
		</ol>
		<span class="small_font">Drag the blue-bordered white boxes to reorder the datasets. Click "Update" to display results with the new order</span>
		
		<div align="center" style="padding:3px;"><input type="button" value="Reset"  id="resetDatasetOrder"/></div>
		
	</div>
	
	
	<!-- DISPLAY COLUMN CHOOSER -->
	<div id="columnChooserTgt" class="small_font" align="left" 
		 style="padding: 5 0 5 0; border: 1px solid gray; width:50%; display:none">
	
		<html:checkbox name="proteinSetComparisonForm" property="showPresent" styleClass="colChooser"
					   styleId="showPresent"  title="<%=String.valueOf(DisplayColumns.present) %>">Present / Not-present</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showFastaId" styleClass="colChooser"
		   			   styleId="showFastaId" title="<%=String.valueOf(DisplayColumns.fasta) %>">Fasta ID</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showCommonName" styleClass="colChooser"
					   styleId="showCommonName" title="<%=String.valueOf(DisplayColumns.commonName) %>">Common Name</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showDescription" styleClass="colChooser"
					   styleId="showDescription" title="<%=String.valueOf(DisplayColumns.description) %>">Description</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showMolWt" styleClass="colChooser"
					   styleId="showMolWt" title="<%=String.valueOf(DisplayColumns.molWt) %>">Molecular Wt.</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showPi" styleClass="colChooser"
		 			   styleId="showPi" title="<%=String.valueOf(DisplayColumns.pi) %>">pI</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showTotalSeq" styleClass="colChooser"
					   styleId="showTotalSeq" title="<%=String.valueOf(DisplayColumns.totalSeq) %>">Total # Sequences for a protein</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showNumSeq" styleClass="colChooser"
					   styleId="showNumSeq" title="<%=String.valueOf(DisplayColumns.numSeq) %>"># Sequences (S) for a protein in a dataset</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showNumIons" styleClass="colChooser"
					   styleId="showNumIons" title="<%=String.valueOf(DisplayColumns.numIons) %>"># Ions (I) for a protein in a dataset</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showNumUniqIons" styleClass="colChooser"
					   styleId="showNumUniqIons" title="<%=String.valueOf(DisplayColumns.numUniqueIons) %>"># Unique Ions (U.I) for a protein in a dataset</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showSpectrumCount" styleClass="colChooser"
					   styleId="showSpectrumCount" title="<%=String.valueOf(DisplayColumns.numSpectrumCount) %>">Spectrum Count (SC) for a protein in a dataset</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showNsaf" styleClass="colChooser"
					   styleId="showSpectrumCount" title="<%=String.valueOf(DisplayColumns.nsaf) %>">NSAF* (N) for a protein in a dataset</html:checkbox>
		<br/>
		<span class="small_font">*NSAF is available only for proteins inferred via MSDaPl.</span>
		<br/><br/>
		<input type="button" value="Save Settings"  onclick="saveDisplayColumnsCookie();"/>
		</div>
	</td>
	</tr>
	
 	<tr>
 		<td valign="middle" align="center" colspan="4">	
 			<html:submit value="Update" onclick="javascript:updateResults();" styleClass="plain_button" style="margin-top:0px;"></html:submit>
		</td>
	</tr>
	
 	</logic:notPresent>
 	
	
</table>
</div>


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