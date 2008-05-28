
<%@page import="org.yeastrc.grant.FundingSourceType"%>
<%@page import="org.yeastrc.grant.FundingSourceType.SourceName"%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>


<html>
   <head>
    <title></title>
	<link REL="stylesheet" TYPE="text/css" HREF="/yrc/css/global.css">
	
<script type="text/javascript">
	function updateGrant(grantID, grantTitle, piID, PI, sourceType, sourceName, grantNumber, grantAmount) {
		window.opener.updateGrant(grantID, grantTitle, piID, PI, sourceType, sourceName, grantNumber, grantAmount);
		window.close();
	}
</script>

  </head>
  
  <body>
  <%@ include file="/includes/errors.jsp" %>
  
	 <yrcwww:contentbox title="Grant Saved!" width="500">
	 <bean:define name="grant" property="fundingSource" id="fundingSource"></bean:define>
	 	<center>
	 		<table>
	 			<tr>
	 				<td style="padding:5px;" WIDTH="25%" VALIGN="top">Title:</td>
	 				<td style="padding:5px;" WIDTH="75%" VALIGN="top"><bean:write name="grant" property="title" /></td>
	 			</tr>
	 			<tr>
	 				<td style="padding:5px;" WIDTH="25%" VALIGN="top">PI:</td>
	 				<td style="padding:5px;" WIDTH="75%" VALIGN="top"><bean:write name="grant" property="PILastName" /></td>
	 			</tr>
	 			<tr>
	 				<td style="padding:5px;" WIDTH="25%" VALIGN="top">Funding Source:</td>
	 				<td style="padding:5px;" WIDTH="75%" VALIGN="top"><bean:write name="fundingSource" property="typeDisplayName" /></td>
	 				
	 			</tr>
	 			<tr>
	 				<td style="padding:5px;" WIDTH="25%" VALIGN="top">Funding Source Name:</td>
	 				<td style="padding:5px;" WIDTH="75%" VALIGN="top"><bean:write name="fundingSource" property="displayName" /></td>
	 			</tr>
	 			
	 			<tr>
	 				<td style="padding:5px;" WIDTH="25%" VALIGN="top">Grant Number:</td>
	 				<td style="padding:5px;" WIDTH="75%" VALIGN="top"><bean:write name="grant" property="grantNumber" /></td>
	 			</tr>
	 			<tr>
	 				<td style="padding:5px;" WIDTH="25%" VALIGN="top">Annual Funds:</td>
	 				<td style="padding:5px;" WIDTH="75%" VALIGN="top"><bean:write name="grant" property="grantAmount" /></td>
	 			</tr>
	 			
	 			<tr>
	 				<td colspan="2"" align="center">
	 					<button onClick="javascript:updateGrant(<bean:write name="grant" property="ID" />, '<bean:write name="grant" property="title" />', <bean:write name="grant" property="PIID" />, '<bean:write name="grant" property="PILastName" />', '<bean:write name="fundingSource" property="typeDisplayName" />', '<bean:write name="fundingSource" property="displayName" />', '<bean:write name="grant" property="grantNumber" />', '<bean:write name="grant" property="grantAmount" />')">Done</button>
	 				</td>
	 			</tr>
	 		</table>
	 	</center>
	 </yrcwww:contentbox>   
<%@ include file="/includes/footer.jsp" %>

