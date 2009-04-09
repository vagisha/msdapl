<%@ page language="java" pageEncoding="ISO-8859-1"%>
<%@page import="java.net.URL"%>

<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>SpectrumViewer</title>
	<link REL="stylesheet" TYPE="text/css" HREF="<yrcwww:link path='css/global.css'/>">
  </head>
  
  <body>
 
<script src="<yrcwww:link path='js/jquery-1.3.2.min.js'/>"></script>

<script>
// ---------------------------------------------------------------------------------------
// SETUP THE TABLE
// ---------------------------------------------------------------------------------------
$(document).ready(function() {

   $(".other_results").each(function() {
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
   		
   		$("tbody > tr:even", $table).each(function() {
   			if(!($(this).is('.tr_highlight'))) {
   				$(this).addClass('project_A');
   			}
   		});
   		//$('tbody > tr:odd', $table).css("background-color", "F0FFF0");
   });
});
</script>


<%@ include file="/includes/errors.jsp" %>

<div style="margin:10;">
<yrcwww:contentbox title="Peptide Spectrum" centered="true" width="95" widthRel="true" >

<center>
<table border="0">
 <tr>
  <td><b>Sequence:</b></td>
  <td><b><bean:write name="peptideSeq" filter="false"/></b></td>
 </tr>
</table>

<p><table border=0 ALIGN="CENTER" width="100%">
 <TR><TD><B>Mass: <bean:write  name="firstMass"/></B></TD>
  <TD colspan="2"><B>File: <bean:write  name="filename"/></B></TD>
  <TD colspan="2"><B>Scan number: <bean:write  name="scanNumber"/></B></TD>
  <TD><B>Charge: <bean:write  name="firstCharge"/></B></TD>
  <TD colspan="2"><B>Database: <bean:write  name="database"/></TD>
 </TR>

 <TR>
  <TD colspan="8" ALIGN="center">
  <% String baseUrl = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath()).toString(); %>
   <applet 
   		code="ed.SpectrumViewerApp.SpectrumApplet.class"
   		archive="SpectrumApplet.jar" 
   		CODEBASE="<%=baseUrl %>/applets" 
   		width=100% 
   		height=500>
    <logic:iterate name="params" id="param" scope="request">
     <bean:write name="param" filter="false" />
    </logic:iterate>
   </applet>
  </TD>
 </TR>
</table>

<!-- OTHER RESULTS FOR THIS SCAN -->
	<div style="background-color: #FFFAF0; margin:5 5 5 5; padding:5; border: 1px dashed gray;" > 
	<logic:present name="results">
		<yrcwww:table name="results" tableId='other_results' tableClass="other_results" center="true" />
	</logic:present>
	<logic:notPresent name="results">
		No other results for for this scan.
	</logic:notPresent>
	</div>

</center>
</yrcwww:contentbox>

</div>
</body>
</html>    
