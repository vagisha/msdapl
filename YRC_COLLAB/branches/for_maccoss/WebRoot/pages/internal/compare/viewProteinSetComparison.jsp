
<%@page import="org.yeastrc.www.compare.ProteinComparisonDataset"%>
<%@page import="org.yeastrc.www.compare.DatasetColor"%>
<%@page import="org.yeastrc.www.compare.Dataset"%>
<%@page import="edu.uwpr.protinfer.database.dto.ProteinFilterCriteria.SORT_ORDER"%><%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:notPresent name="comparison">
	<logic:forward name="newProteinSetComparison" />
</logic:notPresent>

<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<bean:define name="comparison" id="comparison" type="org.yeastrc.www.compare.ProteinComparisonDataset"></bean:define>

<script src="<yrcwww:link path='js/jquery.ui-1.6rc2/jquery-1.2.6.js'/>"></script>
<script src="<yrcwww:link path='js/jquery.form.js'/>"></script>
<script src="<yrcwww:link path='js/jquery.blockUI.js'/>"></script>

<script>
// ---------------------------------------------------------------------------------------
// AJAX DEFAULTS
// ---------------------------------------------------------------------------------------
  $.ajaxSetup({
  	type: 'POST',
  	//timeout: 5000,
  	dataType: 'html',
  	error: function(xhr) {
  			
  				var statusCode = xhr.status;
		  		// status code returned if user is not logged in
		  		// reloading this page will redirect to the login page
		  		if(statusCode == 303)
 					window.location.reload();
 				
 				// otherwise just display an alert
 				else {
 					alert("Request Failed: "+statusCode+"\n"+xhr.statusText);
 				}
  			}
  });
  
  $.blockUI.defaults.message = '<b>Loading...</b>'; 
  $.blockUI.defaults.css.padding = 20;
  $.blockUI.defaults.fadeIn = 0;
  $.blockUI.defaults.fadeOut = 0;
  $().ajaxStop($.unblockUI);
  
// ---------------------------------------------------------------------------------------
// SETUP THE TABLE
// ---------------------------------------------------------------------------------------
$(document).ready(function() {
	
   
   // make the table sortable
   makeSortable();
   
   $("#compare_results_pager1").attr('width', "80%").attr('align', 'center');
   $("#compare_results_pager2").attr('width', "80%").attr('align', 'center');
      
   var colCount = <%=comparison.tableHeaders().size()%>
   $("#compare_results").each(function() {
   		var $table = $(this);
   		$table.attr('width', "80%");
   		$table.attr('align', 'center');
   		$('.prot_descr', $table).css("font-size", "8pt");
   		
   		
   		// ---------------------------------------------------------------
   		// LINK TO GROUP PROTEINS
   		$('.prot-group', $table).each(function() {
   		
   			var nrseqId = $(this).attr('name');
   			var row = $(this).parent();
   			$(row).addClass('prot_closed');
   			
   			$(this).click(function() {
   				
   				var cell = $(this);
   				if($(row).is('.prot_closed')) {
   					$(row).removeClass('prot_closed');
   					$(row).addClass('prot_open');
   					
   					if($(row).is('.has_proteins')) {
   						$(row).next().show();
   					}
   					else {
   						// append a row for the protein groups to go into
   						var newRow = "<tr><td colspan='"+colCount+"'>";
   						newRow += "<div align='center' width='90%' id='proteins_"+nrseqId+"'></div></td></tr>"
   						$(row).after(newRow);
   						
   						// send a request for the peptides
   						$.blockUI();
  						$("#proteins_"+nrseqId).load("<yrcwww:link path='doProteinGroupComparison.do'/>", 	//url
  											{'piDatasetIds': 	'<bean:write name="piDatasetIds"/>', 	// data
  									 		 'dtaDatasetIds':   '<bean:write name="dtaDatasetIds"/>',
  									 		 'nrseqProteinId': 		nrseqId
  									 },
  									 function(responseText, status, xhr) {			// callback
  									 	$.unblockUI();
  									 	$(row).addClass('has_proteins');
  								   });
   					}
   				}
   				else {
   					$(row).removeClass('prot_open');
   					$(row).addClass('prot_closed');
   					$(row).next().hide();
   				}
   			});
   		});
   		                    //cell.setHyperlink("doProteinGroupComparison.do?piDatasetIds="+getCommaSeparatedDatasetIds()+"&nrseqProteinId="+protein.getNrseqId());
   		
   		
   		// ---------------------------------------------------------------
   		// LINK TO PEPTIDES
   		$('.pept_count', $table).each(function() {
   		
   			var nrseqId = $(this).attr('id');
   			var row = $(this).parent();
   			$(row).addClass('pept_closed');
   			
   			$(this).click(function() {
   				
   				var cell = $(this);
   				if($(row).is('.pept_closed')) {
   					$(row).removeClass('pept_closed');
   					$(row).addClass('pept_open');
   					
   					if($(row).is('.has_peptides')) {
   						if($(row).is('.has_proteins')) {
   							$(row).next().next().show();
   						}
   						else {
   							$(row).next().show();
   						}
   					}
   					else {
   						// append a row for the peptide list to go into
   						var newRow = "<tr><td colspan='"+colCount+"'>";
   						newRow += "<div align='center' width='90%' id='peptides_"+nrseqId+"'></div></td></tr>"
   						
   						if($(row).is('.has_proteins')) {
   							$(row).next().after(newRow);
   						}
   						else {
   							$(row).after(newRow);
   						}
   						
   						
   						// send a request for the peptides
   						$.blockUI();
  						$("#peptides_"+nrseqId).load("<yrcwww:link path='doPeptidesComparison.do'/>", 	//url
  											{'piDatasetIds': 	'<bean:write name="piDatasetIds"/>', 	// data
  									 		 'dtaDatasetIds':   '<bean:write name="dtaDatasetIds"/>',
  									 		 'nrseqProteinId': 		nrseqId
  									 },
  									 function(responseText, status, xhr) {			// callback
  									 	$.unblockUI();
  									 	$(row).addClass('has_peptides');
  									 	// make the table sortable
  									 	setupPeptidesTable($('#peptides_table_'+nrseqId));
  								   });
   					}
   				}
   				else {
   					$(row).removeClass('pept_open');
   					$(row).addClass('pept_closed');
   					if($(row).is('.has_proteins')) {
						$(row).next().next().hide();
					}
					else {
						$(row).next().hide();
					}
   				}
   			});
   			
   		});
   		
   		
   		<%for(int i = 0; i < comparison.getDatasetCount(); i++) {
   			String color = DatasetColor.get(i).R+", "+DatasetColor.get(i).G+","+DatasetColor.get(i).B;
   		%>
   			$("td.prot-found[id="+<%=String.valueOf(i)%>+"]", $table).css('background-color', "rgb(<%=color%>)");
   		<%}%>
   		
   		$('td.prot-parsim', $table).css('color', '#FFFFFF').css('font-weight', 'bold');
   		
   });
});

// ---------------------------------------------------------------------------------------
// SETUP THE PEPTIDES TABLE
// ---------------------------------------------------------------------------------------
function  setupPeptidesTable(table){
		var $table = $(table);
   		$table.attr('width', "60%");
   		$table.attr('align', 'center');
   		$table.css("margin", "5 5 5 5");
   		
   		<%for(int i = 0; i < comparison.getDatasetCount(); i++) {
   			String color = DatasetColor.get(i).R+", "+DatasetColor.get(i).G+","+DatasetColor.get(i).B;
   		%>
   			$("td.pept-found[id="+<%=String.valueOf(i)%>+"]", $table).css('background-color', "rgb(<%=color%>)").css('color', "rgb(<%=color%>)");
   		<%}%>
   		
   		$('td.pept-unique', $table).css('color', '#FFFFFF').css('font-weight', 'bold');
   		makeSortableTable($table);
   		
   		
}

// ---------------------------------------------------------------------------------------
// MAKE TABLE SORTABLE
// ---------------------------------------------------------------------------------------
function makeSortable() {
   $(".sortable_table").each(function() {
   		var $table = $(this);
   		$table.attr('width', "100%");
   		$table.attr('align', 'center');
   		//$('tbody > tr:odd', $table).addClass("tr_odd");
   		//$('tbody > tr:even', $table).addClass("tr_even");
   		
   		$('th', $table).each(function() {
   		
   				if($(this).is('.sortable')) {
      					
      				$(this).click(function() {
						var sortBy = $(this).attr('id');
						// sorting direction
						var sortOrder = "<%=SORT_ORDER.ASC.name()%>";
						if ($(this).is('.sorted-asc')) {
		          			sortOrder = "<%=SORT_ORDER.DESC.name()%>";
		        		}
		        		else if ($(this).is('.sorted-desc')) {
		          			sortOrder = "<%=SORT_ORDER.ASC.name()%>";
		        		}
	        			sortResults(sortBy, sortOrder);
      			});
      		}
      	});
   });
}


// ---------------------------------------------------------------------------------------
// PAGE RESULTS
// ---------------------------------------------------------------------------------------
function pageResults(pageNum) {
  	$("input#pageNum").val(pageNum);
  	$("input#download").val("false");
  	$("input#goEnrichment").val("false");
  	//alert("setting to "+pageNum+" value set to: "+$("input#pageNum").val());
  	$("form[name='proteinSetComparisonForm']").submit();
}

// ---------------------------------------------------------------------------------------
// SORT RESULTS
// ---------------------------------------------------------------------------------------
function sortResults(sortBy, sortOrder) {
  	$("input#pageNum").val(1);
  	$("input#download").val("false");
  	$("input#goEnrichment").val("false");
  	$("input#sortBy").val(sortBy);
  	$("input#sortOrder").val(sortOrder);
  	$("form[name='proteinSetComparisonForm']").attr('target', '');
  	$("form[name='proteinSetComparisonForm']").submit();
}

// ---------------------------------------------------------------------------------------
// UPDATE RESULTS
// ---------------------------------------------------------------------------------------
function updateResults() {
  	$("input#pageNum").val(1);
  	$("input#download").val("false");
  	$("input#goEnrichment").val("false");
  	$("form[name='proteinSetComparisonForm']").attr('target', '');
  	$("form[name='proteinSetComparisonForm']").submit();
}

// ---------------------------------------------------------------------------------------
// DOWNLOAD RESULTS
// ---------------------------------------------------------------------------------------
function downloadResults() {
  	$("input#download").val("true");
  	$("input#goEnrichment").val("false");
  	$("form[name='proteinSetComparisonForm']").attr('target', '_blank');
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

<CENTER>




<yrcwww:contentbox centered="true" width="90" widthRel="true" title="Protein Dataset Comparison">

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
		<th>Spectrum Count (Max)</th>
		<th># Proteins</th>
	</tr>
</thead>
<tbody>
<logic:iterate name="comparison" property="datasets" id="dataset" indexId="row">
	<tr>
		<th align="center">
			<span><html:link action="viewProteinInferenceResult.do" paramId="pinferId" paramName="dataset" paramProperty="datasetId">ID <bean:write name="dataset" property="datasetId" /></html:link></span>
		</th>
		<td align="center">
			<bean:write name="dataset" property="spectrumCount" />(<bean:write name="dataset" property="maxProteinSpectrumCount" />)
		</td>
		<td style="color:#FFFFFF; background-color: rgb(<%=DatasetColor.get(row).R %>,<%=DatasetColor.get(row).G %>,<%=DatasetColor.get(row).B %> ); padding: 3 5 3 5;">
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
		<td>(<%=comparison.getCommonProteinCount(row, column) %>)&nbsp;<%=comparison.getCommonProteinsPerc(row, column) %>%</td>
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

<table  align="center" style="border: 1px dashed gray;" width="80%">
<tbody>
<logic:iterate name="comparison" property="datasets" id="dataset" indexId="row">
	<bean:define id="mod" value="<%=String.valueOf(row%2)%>"></bean:define>
	<logic:equal name="mod" value="0"><tr></logic:equal>
	<td width="2%"style="background-color: rgb(<%=DatasetColor.get(row).R %>,<%=DatasetColor.get(row).G %>,<%=DatasetColor.get(row).B %> );">
		&nbsp;&nbsp;
	</td>
	<td style="font-size:8pt;text-align:left;"><html:link action="viewProteinInferenceResult.do" paramId="pinferId" paramName="dataset" paramProperty="datasetId">ID <bean:write name="dataset" property="datasetId" /></html:link></td>
	<td width="42%" style="font-size:8pt;" ><bean:write name="dataset" property="datasetComments" /></td>
	<logic:equal name="mod" value="1"></tr></logic:equal>
</logic:iterate>
</tbody>
</table>

<logic:present name="dtasWarning">
<p style="color:red; font-weight: bold;" align="center">
WARNING:  Comparison with DTASelect results is not yet fully supported. 
</p>
</logic:present>

<!-- ################## FILTER FORM  ########################################### -->
<%@include file="comparisonFilterForm.jsp" %>


<!-- PAGE RESULTS -->
<bean:define name="comparison" id="pageable" />
<table id="compare_results_pager1">
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

<table id="compare_results_pager2">
<tr>
<td>
<%@include file="/pages/internal/pager.jsp" %>
</td>
</tr>
</table>

</yrcwww:contentbox>

</CENTER>

<%@ include file="/includes/footer.jsp" %>