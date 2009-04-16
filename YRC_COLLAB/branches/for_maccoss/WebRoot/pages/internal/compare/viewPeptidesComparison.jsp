<%@page import="org.yeastrc.www.compare.DatasetColor"%>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<script src="<yrcwww:link path='js/jquery.ui-1.6rc2/jquery-1.2.6.js'/>"></script>
<script src="<yrcwww:link path='/js/jquery.form.js'/>"></script>
<script src="<yrcwww:link path='/js/jquery.blockUI.js'/>"></script>


<bean:define name="comparison" id="comparison" type="org.yeastrc.www.compare.PeptideComparisonDataset"></bean:define>

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

   $("#compare_peptide_results").each(function() {
   		var $table = $(this);
   		$table.attr('width', "80%");
   		$table.attr('align', 'center');
   		
   		
   		<%for(int i = 0; i < comparison.getDatasetCount(); i++) {
   			String color = DatasetColor.get(i).R+", "+DatasetColor.get(i).G+","+DatasetColor.get(i).B;
   		%>
   			$("td.pept-found[id="+<%=String.valueOf(i)%>+"]", $table).css('background-color', "rgb(<%=color%>)");
   		<%}%>
   		
   		$('td.pept-unique', $table).css('color', 'white').css('font-weight', '#FFFFFF');
   		
   });
});
</script>


<!-- RESULTS TABLE -->
<div > 
<yrcwww:table name="pept_comparison" tableId='compare_peptide_results' tableClass="table_basic sortable_table" center="true" />
</div>