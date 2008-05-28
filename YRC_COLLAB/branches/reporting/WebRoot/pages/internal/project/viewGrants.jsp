<%@ page language="java" pageEncoding="ISO-8859-1"%>

<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Grants</title>
	<link REL="stylesheet" TYPE="text/css" HREF="/yrc/css/global.css">
  </head>
  
  <body>
  
<%@ include file="/includes/errors.jsp" %>

<script type="text/javascript">
	function addGrant(grantID, grantTitle, piID, PI, sourceType, sourceName, grantNumber, grantAmount) {
		window.opener.addGrant(grantID, grantTitle, piID, PI, sourceType, sourceName, grantNumber, grantAmount);
		//window.close();
	}
</script>

<center>
<div class="project_header" style="width:90%">
	<center>Grants</center>
</div>
<div class="project" style="width:90%">
	<logic:notEmpty name="grants">
		<% Integer PI = (Integer)request.getAttribute("PI"); %>
		<table style="margin:5px">
		<yrcwww:colorrow>
			<td style="display:none;"></td>
			<td style="font-size:8pt; padding:5px;"><b><nobr><a href='/yrc/viewGrants.do?PI=<%=PI%>&sortby=title'>Grant Title</a></nobr></b></td>
			<td style="font-size:8pt; padding:5px;"><b><nobr><a href='/yrc/viewGrants.do?PI=<%=PI%>&sortby=pi'>PI</a></nobr></b></td>
			<td style="font-size:8pt; padding:5px;"><b><nobr><a href='/yrc/viewGrants.do?PI=<%=PI%>&sortby=sourceType'>Source Type</a></nobr></b></td>
			<td style="font-size:8pt; padding:5px;"><b><nobr><a href='/yrc/viewGrants.do?PI=<%=PI%>&sortby=sourceName'>Source Name</a></nobr></b></td>
			<td style="font-size:8pt; padding:5px;"><b><nobr><a href='/yrc/viewGrants.do?PI=<%=PI%>&sortby=grantNum'>Grant #</a></nobr></b></td>
			<td style="font-size:8pt; padding:5px;"><b><nobr><a href='/yrc/viewGrants.do?PI=<%=PI%>&sortby=grantAmount'>Annual Funds</a></nobr></b></td>
			<td></td>
		</yrcwww:colorrow>
		<logic:iterate name="grants" id="grant">
		<yrcwww:colorrow>
			<bean:define name="grant" property="fundingSource" id="fundingSource"></bean:define>
			<td style="display:none;"><bean:write name="grant" property="ID" /></td>
			<td style="font-size:8pt; padding:5px;"><bean:write name="grant" property="title" /></td>
			<td style="font-size:8pt; padding:5px;"><bean:write name="grant" property="PILastName" /></td>
			<td style="font-size:8pt; padding:5px;"><bean:write name="fundingSource" property="typeDisplayName" /></td>
			<td style="font-size:8pt; padding:5px;"><bean:write name="fundingSource" property="displayName" /></td>
			<td style="font-size:8pt; padding:5px;"><bean:write name="grant" property="grantNumber" /></td>
			<td style="font-size:8pt; padding:5px;"><bean:write name="grant" property="grantAmount" /></td>
			<td style="font-size:8pt; padding:5px;">
			<a href="javascript:addGrant(<bean:write name="grant" property="ID" />, '<bean:write name="grant" property="title" />', <bean:write name="grant" property="PIID" />, '<bean:write name="grant" property="PILastName" />', '<bean:write name="fundingSource" property="typeDisplayName" />', '<bean:write name="fundingSource" property="displayName" />', '<bean:write name="grant" property="grantNumber" />', '<bean:write name="grant" property="grantAmount" />');">[Select]</a></td>
		</yrcwww:colorrow>
		</logic:iterate>
		</table>
		<div align="center"><button onclick="window.close();">Done</button></div>
	</logic:notEmpty>
	
	
	<logic:empty name="grants">
		No grants found!!
	</logic:empty>
	
	<br><br><br>
	<% Integer PI  = (Integer)request.getAttribute("PI"); %>
	<a href="/yrc/editGrant.do?PI=<%=PI%>"><font color="red">Add New Grant</font></a>
</div>

</center> 	
    
<%@ include file="/includes/footer.jsp" %>
