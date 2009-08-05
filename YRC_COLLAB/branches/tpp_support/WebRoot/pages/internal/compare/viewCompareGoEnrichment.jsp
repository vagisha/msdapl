
<%@page import="org.yeastrc.www.compare.DatasetColor"%>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


 
<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<bean:define name="comparison" id="comparison" type="org.yeastrc.www.compare.ProteinComparisonDataset"></bean:define>

<script src="<yrcwww:link path='/js/dragtable.js'/>"></script>

<script type="text/javascript" src="<yrcwww:link path='/js/jquery.ui-1.6rc2/ui/ui.core.js'/>"></script>
<script type="text/javascript" src="<yrcwww:link path='/js/jquery.ui-1.6rc2/ui/ui.tabs.js'/>"></script>
<script type="text/javascript" src="<yrcwww:link path='/js/jquery.ui-1.6rc2/ui/ui.dialog.js'/>"></script>
<script type="text/javascript" src="<yrcwww:link path='/js/jquery.ui-1.6rc2/ui/ui.draggable.js'/>"></script>
<script type="text/javascript" src="<yrcwww:link path='/js/jquery.ui-1.6rc2/ui/ui.resizable.js'/>"></script>

<script type="text/javascript" src="<yrcwww:link path='/js/jquery.history.js'/>"></script>
<script type="text/javascript" src="<yrcwww:link path='/js/jquery.cookie.js'/>"></script>



<script src="<yrcwww:link path='/js/jquery.form.js'/>"></script>

<script src="<yrcwww:link path='/js/jquery.blockUI.js'/>"></script>


<link rel="stylesheet" href="<yrcwww:link path='/css/proteinfer.css'/>" type="text/css" >

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>


<script>

$(document).ready(function() {
   $(".sortable_table").each(function() {
   		var $table = $(this);
   		makeSortableTable($table);
   });
});

// ---------------------------------------------------------------------------------------
// GENE ONTOLOGY ENRICHMENT
// ---------------------------------------------------------------------------------------
function doGoEnrichmentAnalysis() {
	$("input#goEnrichment").val("true");
	$("input#goEnrichmentGraph").val("false");
	if(!validateGoEnrichmentForm())
    	return false;
	$("form[name='proteinSetComparisonForm']").submit();
}

function validateGoEnrichmentForm() {
	// fieldValue is a Form Plugin method that can be invoked to find the 
    // current value of a field 
    value = $('form input[@name=goEnrichmentPVal]').fieldValue();
    valid = validateFloat(value, "P-Value", 0.0, 1.0);
    if(!valid)	return false;
    
    return true;
}

function validateFloat(value, fieldName, min, max) {
	var floatVal = parseFloat(value);
	var valid = true;
	if(isNaN(floatVal))						valid = false;
	if(valid && floatVal < min)			valid = false;
	if(max && (valid && floatVal > max))	valid = false;
	if(!valid) {
		if(max) alert("Value for "+fieldName+" should be between "+min+" and "+max);
		else	alert("Value for "+fieldName+" should be >= "+min);
	}
	return valid;
}
// ---------------------------------------------------------------------------------------
// GENE ONTOLOGY ENRICHMENT GRAPH
// ---------------------------------------------------------------------------------------
function doGoEnrichmentAnalysisGraph() {
	alert("graph!");
	$("input#goEnrichment").val("true");
	$("input#goEnrichmentGraph").val("true");
	if(!validateGoEnrichmentForm())
    	return false;
	$("form[name='proteinSetComparisonForm']").submit();
}

// ---------------------------------------------------------------------------------------
// TOGGLE AND, OR, NOT FILTERS
// ---------------------------------------------------------------------------------------
var colors = [];
<%
	int datasetCount = comparison.getDatasetCount();
	for(int i = 0; i < datasetCount; i++) {
%>
	colors[<%=i%>] = '<%="rgb("+DatasetColor.get(i).R+","+DatasetColor.get(i).G+","+DatasetColor.get(i).B+")"%>';
<%
}
%>
function toggleAndSelect(dsIndex) {
	var id = "AND_"+dsIndex+"_select";
	var value = $("input#"+id).val();
	
	if(value == "true") {
		$("input#"+id).val("false");	
		$("td#AND_"+dsIndex+"_td").css("background-color", "#FFFFFF");
	}
	else {
		$("input#"+id).val("true");	
		$("td#AND_"+dsIndex+"_td").css("background-color", colors[dsIndex]);
	}
}
function toggleOrSelect(dsIndex) {
	var id = "OR_"+dsIndex+"_select";
	var value = $("input#"+id).val();
	if(value == "true") {
		$("input#"+id).val("false");	
		$("td#OR_"+dsIndex+"_td").css("background-color", "#FFFFFF");
	}
	else {
		$("input#"+id).val("true");	
		$("td#OR_"+dsIndex+"_td").css("background-color", colors[dsIndex]);
	}
}
function toggleNotSelect(dsIndex) {
	var id = "NOT_"+dsIndex+"_select";
	var value = $("input#"+id).val();
	if(value == "true") {
		$("input#"+id).val("false");	
		$("td#NOT_"+dsIndex+"_td").css("background-color", "#FFFFFF");
	}
	else {
		$("input#"+id).val("true");	
		$("td#NOT_"+dsIndex+"_td").css("background-color", colors[dsIndex]);
	}
}
function toggleXorSelect(dsIndex) {
	var id = "XOR_"+dsIndex+"_select";
	var value = $("input#"+id).val();
	if(value == "true") {
		$("input#"+id).val("false");	
		$("td#XOR_"+dsIndex+"_td").css("background-color", "#FFFFFF");
	}
	else {
		$("input#"+id).val("true");	
		$("td#XOR_"+dsIndex+"_td").css("background-color", colors[dsIndex]);
	}
}

</script>

<yrcwww:contentbox title="GO Enrichment" centered="true" width="90" widthRel="true">

<table align="center">

<tr>
<td colspan="2" style="background-color:#F2F2F2; font-weight:bold; text-align: center; padding:5 5 5 5;" >
Total Proteins: <bean:write name="comparison" property="totalProteinCount" />
</td>

<logic:present name="chart">
<td rowspan="5">
	<img src="<bean:write name='chart' />" align="top" alt="Comparison"></img>
</td>
</logic:present>

</tr>

<tr valign="top">

<td>
<table class="table_basic">
<thead>
	<tr>
		<th>Dataset</th>
		<th># Proteins</th>
	</tr>
</thead>
<tbody>
<logic:iterate name="comparison" property="datasets" id="dataset" indexId="row">
	<tr>
		<th align="center">
			<span><html:link action="viewProteinInferenceResult.do" paramId="pinferId" paramName="dataset" paramProperty="datasetId">ID <bean:write name="dataset" property="datasetId" /></html:link></span>
		</th>
		<td align="center" style="color:#FFFFFF; background-color: rgb(<%=DatasetColor.get(row).R %>,<%=DatasetColor.get(row).G %>,<%=DatasetColor.get(row).B %> ); padding: 3 5 3 5;">
			<%=comparison.getProteinCount(row)%>
		</td>
	</tr>
</logic:iterate>
</tbody>
</table>
</td>

<td>
<table  class="table_basic">
<thead>
<tr>
<logic:iterate name="comparison" property="datasets" id="dataset" indexId="column">
	<th>ID<bean:write name="dataset" property="datasetId"/></th>
</logic:iterate>
</tr>
</thead>

<tbody>
<logic:iterate name="comparison" property="datasets" id="dataset" indexId="row">
<tr>

<logic:iterate name="comparison" property="datasets" id="dataset" indexId="column">
	
	<logic:equal name="column" value="<%=String.valueOf(row)%>">
		<td style="background-color: rgb(<%=DatasetColor.get(row).R %>,<%=DatasetColor.get(row).G %>,<%=DatasetColor.get(row).B %> );">
		&nbsp;
	</td>
	</logic:equal>
	
	<logic:notEqual name="column" value="<%=String.valueOf(row)%>">
		<td><%=comparison.getCommonProteinCount(row, column) %>&nbsp;(<%=comparison.getCommonProteinsPerc(row, column) %>%)</td>
	</logic:notEqual>
</logic:iterate>

</tr>
</logic:iterate>
</tbody>
</table>

</td>

</tr>


</table>

<br>


<!-- ################## FILTER FORM  ########################################### -->
<%@include file="comparisonFilterForm.jsp" %>







<!-- BIOLOGICAL PROCESS -->
<logic:present name="bioProcessTerms">
<yrcwww:contentbox title="Biological Process" centered="true" width="80" widthRel="true">
	<div align="center">
	<b># Enriched Terms (Biological Process):<bean:write name="bioProcessTerms" property="enrichedTermCount" /></b>
	</div>
	<yrcwww:table name="bioProcessTerms" tableId='bioProc_terms' tableClass="table_basic sortable_table stripe_table" center="true" />
</yrcwww:contentbox>
<br>
</logic:present>

<!-- CELLULAR COMPONENT -->
<logic:present name="cellComponentTerms">
<yrcwww:contentbox title="Cellular Component" centered="true" width="80" widthRel="true">
	<div align="center">
	<b># Enriched Terms (Cellular Component):<bean:write name="cellComponentTerms" property="enrichedTermCount" /></b>
	</div>
	<yrcwww:table name="cellComponentTerms" tableId='cellComp_terms' tableClass="table_basic sortable_table stripe_table" center="true" />
</yrcwww:contentbox>
<br>
</logic:present>

<!-- MOLECULAR FUNCTION -->
<logic:present name="molFunctionTerms" >
<yrcwww:contentbox title="Molecular Function" centered="true" width="80" widthRel="true">
	<div align="center">
	<b># Enriched Terms (Molecular Function):<bean:write name="molFunctionTerms" property="enrichedTermCount" /></b>
	</div>
	<yrcwww:table name="molFunctionTerms" tableId='molFunc_terms' tableClass="table_basic sortable_table stripe_table" center="true" />
</yrcwww:contentbox>
<br>
</logic:present>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>