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
	<link REL="stylesheet" TYPE="text/css" HREF="/yrc/css/global.css">
  </head>
  
  <body>
  
<%@ include file="/includes/errors.jsp" %>

<center>
<yrcwww:contentbox title="View Peptide Spectra" centered="true" width="1000" scheme="ms">

<center>
<table border="0">
 <yrcwww:colorrow scheme="ms">
  <td><b>Sequence:</b></td>
  <td><b><bean:write name="peptideSeq" filter="false"/></b></td>
 </yrcwww:colorrow>
</table>

<p><table border=0 ALIGN="CENTER">
 <TR><TD><B>Mass: <FONT COLOR="green"><bean:write  name="firstMass"/></B></TD>
  <TD colspan="2"><B>File: <FONT COLOR="green"><bean:write  name="filename"/></B></TD>
  <TD colspan="2"><B>Scan number: <FONT COLOR="green"><bean:write  name="scanNumber"/></B></TD>
  <TD><B>Charge: <FONT COLOR="green"><bean:write  name="firstCharge"/></B></TD>
  <TD colspan="2"><B>Database: <FONT COLOR="green"><bean:write  name="database"/></TD>
 </TR>

 <TR>
  <TD colspan="8" ALIGN="center">
  <% String baseUrl = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath()).toString(); %>
   <applet 
   		code="ed.SpectrumViewerApp.SpectrumApplet.class"
   		archive="SpectrumApplet.jar" 
   		CODEBASE="<%=baseUrl %>/applets" 
   		width=970 
   		height=500>
    <logic:iterate name="params" id="param" scope="request">
     <bean:write name="param" filter="false" />
    </logic:iterate>
   </applet>
  </TD>
 </TR>
</table>

</yrcwww:contentbox>


</center> 	
    
<%@ include file="/includes/footer.jsp" %>
