
<%@page import="org.yeastrc.www.compare.ComparisonDataset"%>
<%@page import="org.yeastrc.www.compare.DatasetColor"%>
<%@page import="org.yeastrc.www.compare.Dataset"%><%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<script src="<yrcwww:link path='js/jquery.ui-1.6rc2/jquery-1.2.6.js'/>"></script>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>


<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<bean:define name="comparison" id="comparison" type="org.yeastrc.www.compare.ComparisonDataset"></bean:define>

<script>
// ---------------------------------------------------------------------------------------
// SETUP THE TABLE
// ---------------------------------------------------------------------------------------
$(document).ready(function() {

   $("#compare_results_pager").attr('width', "80%").attr('align', 'center');
   
   $("#compare_results").each(function() {
   		var $table = $(this);
   		$table.attr('width', "80%");
   		$table.attr('align', 'center');
   		$('.prot_descr', $table).css("font-size", "8pt");
   		
   		
   		<%for(int i = 0; i < comparison.getDatasetCount(); i++) {
   			String color = DatasetColor.get(i).R+", "+DatasetColor.get(i).G+","+DatasetColor.get(i).B;
   		%>
   			$("td.prot-found[id="+<%=String.valueOf(i)%>+"]", $table).css('background-color', "rgb(<%=color%>)");
   		<%}%>
   		
   		$('td.prot-parsim', $table).css('color', 'white').css('font-weight', '#FFFFFF');
   		
   		//$('td.prot-parsim', $table).css('background-color', 'red');
   		
   		//$('tbody > tr:odd', $table).addClass("tr_odd");
   		//$('tbody > tr:even', $table).addClass("tr_even");
   });
});

// ---------------------------------------------------------------------------------------
// PAGE RESULTS
// ---------------------------------------------------------------------------------------
function pageResults(pageNum) {
  	$("input#pageNum").val(pageNum);
  	//alert("setting to "+pageNum+" value set to: "+$("input#pageNum").val());
  	$("form").submit();
}

</script>

<CENTER>




<yrcwww:contentbox centered="true" width="90" widthRel="true" title="Protein Dataset Comparison">

<table align="center">

<tr>
<td colspan="2" style="background-color:#F2F2F2; font-weight:bold; text-align: center; padding:5 5 5 5;" >
Total Proteins: <bean:write name="comparison" property="totalProteinCount" />
</td>
</tr>

<tr valign="top">
<td>
<table  class="table_basic">
<thead>
<tr>
<th>&nbsp;</th>
<logic:iterate name="comparison" property="datasets" id="dataset" indexId="column">
	<th>Dataset ID<bean:write name="dataset" property="datasetId"/></th>
</logic:iterate>
</tr>
</thead>

<tbody>
<logic:iterate name="comparison" property="datasets" id="dataset" indexId="row">
<tr>
<th>Dataset ID <bean:write name="dataset" property="datasetId"/></th>


<logic:iterate name="comparison" property="datasets" id="dataset" indexId="column">
	
	<logic:equal name="column" value="<%=String.valueOf(row)%>">
		<td style="background-color: rgb(<%=DatasetColor.get(row).R %>,<%=DatasetColor.get(row).G %>,<%=DatasetColor.get(row).B %> );">
		&nbsp;
	</td>
	</logic:equal>
	
	<logic:lessThan name="column" value="<%=String.valueOf(row)%>">
		<td>&nbsp;</td>
	</logic:lessThan>
	
	<logic:greaterThan name="column" value="<%=String.valueOf(row)%>">
		<td>(<%=comparison.getCommonProteinCount(row, column) %>)&nbsp;<%=comparison.getCommonProteinsPerc(row, column) %>%</td>
	</logic:greaterThan>
</logic:iterate>

</tr>
</logic:iterate>
</tbody>
</table>

</td>
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
		<td align="center"
		    style="color:#FFFFFF; background-color: rgb(<%=DatasetColor.get(row).R %>,<%=DatasetColor.get(row).G %>,<%=DatasetColor.get(row).B %> ); padding: 4 5 3 5;">
			<span>ID <bean:write name="dataset" property="datasetId" /></span>
		</td>
		<td><%=comparison.getProteinCount(row)%></td>
	</tr>
</logic:iterate>
</tbody>
</table>
</td>
</tr>
</table>

<br>



<!-- ################## FORM  ########################################### -->
<html:form action="doProteinSetComparison" method="POST">

	<logic:iterate name="proteinSetComparisonForm" property="proteinferRunList" id="proteinferRun">
		<logic:equal name="proteinferRun" property="selected" value="true">
			<html:hidden name="proteinferRun" property="runId" indexed="true" />
			<html:hidden name="proteinferRun" property="selected" indexed="true" />
		</logic:equal>
	</logic:iterate>
	<html:hidden name="proteinSetComparisonForm" property="pageNum" styleId="pageNum" />
</html:form>

<center>
<br>

<div style="background-color:#F0F8FF; padding: 5 0 5 0; border: 1px solid gray; width:80%">
<table>
	<tr>
		<td><b>Limit:</b></td>
		<td><b>AND</b></td>
		<td>
			<table style="border: 1px solid #F2F2F2;">
			<tr>
				<logic:iterate name="comparison" property="datasets" id="dataset" indexId="dsIndex">
					<td style="background-color:rgb(<%=DatasetColor.get(dsIndex).R %>,<%=DatasetColor.get(dsIndex).G %>,<%=DatasetColor.get(dsIndex).B %>)">&nbsp;&nbsp;</td>
				</logic:iterate>
			</tr>
			</table>
		</td>
		<td><b>OR</b></td>
		<td>
			<table style="border: 1px solid #F2F2F2;">
			<tr>
				<logic:iterate name="comparison" property="datasets" id="dataset" indexId="dsIndex">
					<td style="background-color:rgb(<%=DatasetColor.get(dsIndex).R %>,<%=DatasetColor.get(dsIndex).G %>,<%=DatasetColor.get(dsIndex).B %>)">&nbsp;&nbsp;</td>
				</logic:iterate>
			</tr>
			</table>
		</td>
		
		<td><b>NOT</b></td>
		<td>
			<table style="border: 1px solid #F2F2F2;">
			<tr>
				<logic:iterate name="comparison" property="datasets" id="dataset" indexId="dsIndex">
					<td style="background-color:rgb(<%=DatasetColor.get(dsIndex).R %>,<%=DatasetColor.get(dsIndex).G %>,<%=DatasetColor.get(dsIndex).B %>)">&nbsp;&nbsp;</td>
				</logic:iterate>
			</tr>
			</table>
		</td>
		<td>
			<html:submit value="Update"></html:submit>
		</td>
		
	</tr>
</table>
</div>
</center>

<br>

<!-- PAGE RESULTS -->
<bean:define name="comparison" id="pageable" />
<table id="compare_results_pager">
<tr>
<td>
<%@include file="/pages/internal/pager.jsp" %>
</td>
</tr>
</table>
		
<!-- RESULTS TABLE -->
<div > 
<yrcwww:table name="comparison" tableId='compare_results' tableClass="table_basic sortable_table" center="true" />
</div>

<img src="<bean:write name='chart' />" align="top" alt="Comparison" style="padding-right:20px;"></img>

</yrcwww:contentbox>

</CENTER>

<%@ include file="/includes/footer.jsp" %>