
<%@page import="org.yeastrc.ms.domain.search.SORT_ORDER"%><%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:empty name="filterForm">
  <logic:forward name="viewProject" />
</logic:empty>

<script src="<yrcwww:link path='js/jquery-1.3.2.min.js'/>"></script>
<script>

// ---------------------------------------------------------------------------------------
// SETUP THE TABLE
// ---------------------------------------------------------------------------------------
$(document).ready(function() {
   $(".perc_results").each(function() {
   		var $table = $(this);
   		$table.attr('width', "100%");
   		$table.attr('align', 'center');
   		$('th', $table).attr("align", "left");
   		
   		// #ED9A2E #D74D2D
   		$('th', $table).each(function() {
   			if($(this).is('.sorted-asc') || $(this).is('.sorted-desc')) {
   				$(this).addClass('th_selected');
   			}
   			else
   			$(this).addClass('th_normal');
   		});
   		$('th', $table).hover(
   				function() {$(this).addClass('th_hover');} , 
      			function() {$(this).removeClass('th_hover');}).click(function() {
					
					if($(this).is('.sortable')) {
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
        			}
      			});
   		
   		$("tbody > tr:even", $table).addClass('project_A');
   		//$('tbody > tr:odd', $table).css("background-color", "F0FFF0");
   });
});

// ---------------------------------------------------------------------------------------
// PAGE RESULTS
// ---------------------------------------------------------------------------------------
function pageResults(pageNum) {
  	$("input#pageNum").val(pageNum);
  	//alert("setting to "+pageNum+" value set to: "+$("input#pageNum").val());
  	$("form").submit();
  	// document.location = "<yrcwww:link path='viewPercolatorResults.do?ID=' />"+<bean:write name="runSearchAnalysisId"/>+"&page="+pageNum;
}
// ---------------------------------------------------------------------------------------
// SORT RESULTS
// ---------------------------------------------------------------------------------------
function sortResults(sortBy, sortOrder) {
  	// alert(sortBy+" "+sortOrder);
  	$("input#pageNum").val(1); // reset the page number to 1
  	$("input#sortBy").val(sortBy);
  	$("input#sortOrder").val(sortOrder);
  	//alert($("input#pageNum").val()+"   "+$("input#sortBy").val()+"   "+$("input#sortOrder").val());
  	$("form").submit();
  	// document.location = "<yrcwww:link path='viewPercolatorResults.do?ID=' />"+<bean:write name="runSearchAnalysisId"/>+"&page="+pageNum;
}
// ---------------------------------------------------------------------------------------
// UPDATE RESULTS
// ---------------------------------------------------------------------------------------
function updateResults() {
	$("input#pageNum").val(1); // reset the page number to 1
	$("form").submit();
}

</script>

<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<yrcwww:contentbox title="Percolator Results" centered="true" width="850">
<center>

	<!-- SUMMARY -->
	<div style="padding:0 7 0 7; margin-bottom:5; border: 1px dashed gray;background-color: #FFFAF0;">
		<table width="80%">
			<tr>
				<td><b>File:</b></td><td><bean:write name="filterForm" property="filename" /></td>
				<td><b>Analysis ID:</b></td><td><bean:write name="filterForm" property="runSearchAnalysisId" /></td>
				
			</tr>
			<tr>
				<td><b>Experiment ID:</b></td><td><bean:write name="filterForm" property="experimentId" /></td>
				<td><b>Program: </b></td><td><bean:write name="filterForm" property="program" /></td>
				
			</tr>
		</table>
	</div>
	
	<!-- FILTER FORM -->
	<div style="padding:7 7 0 7; margin-bottom:30; border: 1px dashed gray;">
		<html:form action="viewPercolatorResults" method="POST">
		<html:hidden name="filterForm" property="runSearchAnalysisId"/>
		<html:hidden name="filterForm" property="pageNum" styleId="pageNum"/>
		<html:hidden name="filterForm" property="sortByString" styleId="sortBy"/>
		<html:hidden name="filterForm" property="sortOrderString" styleId="sortOrder"/>
		
			<table cellspacing="0" cellpadding="2" >
				<tr>
					<td>Min. Scan</td> <td> <html:text name="filterForm" property="minScan" size="5"/> </td>
					<td>Max. Scan</td> <td> <html:text name="filterForm" property="maxScan" size="5" /> </td>
					<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
					<td>Min. Charge</td><td> <html:text name="filterForm" property="minCharge" size="5" /> </td>
					<td>Max. Charge</td><td> <html:text name="filterForm" property="maxCharge" size="5" /> </td>
				</tr>
				
				<tr>
					<td>Min. RT</td> <td> <html:text name="filterForm" property="minRT" size="5"/> </td>
					<td>Max. RT</td> <td> <html:text name="filterForm" property="maxRT" size="5" /> </td>
					<td></td>
					<td>Min. Obs. Mass</td><td> <html:text name="filterForm" property="minObsMass" size="5" /> </td>
					<td>Max. Obs. Mass</td><td> <html:text name="filterForm" property="maxObsMass" size="5" /> </td>
				</tr>
				
				<tr>
					<td>Min. q-value</td> <td> <html:text name="filterForm" property="minQValue" size="5"/> </td>
					<td>Max. q-value</td> <td> <html:text name="filterForm" property="maxQValue" size="5" /> </td>
					<td></td>
					<td>Min. PEP</td><td> <html:text name="filterForm" property="minPep" size="5" /> </td>
					<td>Max. PEP</td><td> <html:text name="filterForm" property="maxPep" size="5" /> </td>
				</tr>
				
				<tr>
					<td valign="top">Peptide</td> 
					<td colspan=4 align="left" valign="top" style="font-size:8pt;"> 
						<html:text name="filterForm" property="peptide" size="25" /><br>
						Exact: <html:checkbox name="filterForm" property="exactPeptideMatch"  />
					</td>
					<td valign="top">Modified peptides</td><td valign="top"> <html:checkbox name="filterForm" property="showModified" /> </td>
					<td valign="top">Unmodified peptides</td><td valign="top"> <html:checkbox name="filterForm" property="showUnmodified" /> </td>
				</tr>
				
				<tr>
					<td colspan="9" align="center"><html:submit value="Update" 
									styleClass="plain_button" 
									onclick="javascript:updateResults();return false;"/></td>
				</tr>
				<tr>
					<td colspan="9" align="center">
						<b># Results: </b><bean:write name="filterForm" property="numResults" />
						&nbsp; &nbsp; &nbsp;
						<b># Results (filtered):</b><bean:write name="filterForm" property="numResultsFiltered" />
					</td>
				</tr>
			</table>
		</html:form>
	</div>

	<!-- PAGE RESULTS -->
	<bean:define name="results" id="pageable" />
	<%@include file="/pages/internal/pager.jsp" %>
	
	
				
	<!-- RESULTS TABLE -->
	<div style="background-color: #FFFAF0; margin:5 5 5 5; padding:5; border: 1px dashed gray;" > 
	<yrcwww:table name="results" tableId='perc_results' tableClass="perc_results" center="true" />
	</div>
	
</center>	
</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>