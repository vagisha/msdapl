
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

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
   		$('th', $table).css("background-color", "#D74D2D").css('color', '#FFFFFF');
   		$("tbody > tr:even", $table).addClass("project_A");
   });
});

// ---------------------------------------------------------------------------------------
// PAGE RESULTS
// ---------------------------------------------------------------------------------------
function pageResults(pageNum) {
	// alert(pageNum);
  	document.location = "<yrcwww:link path='viewPercolatorResults.do?ID=' />"+<bean:write name="runSearchAnalysisId"/>+"&page="+pageNum;
}

</script>

<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<yrcwww:contentbox title="Percolator Results" centered="true" width="850">
<center>

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