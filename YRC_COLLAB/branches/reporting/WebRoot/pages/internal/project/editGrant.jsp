
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
	
		window.onload = displaySource;
		
		// when the window loads if one of the funding source types is selected
		// display the appropriate funding source name element.
		function displaySource() {
			var radios = document.forms[0].elements['fundingType'];
			for(var i = 0; i < radios.length; i++) {
				var radio = radios[i];
				if (radio.checked) {
					displaySourceName(radio);
					break;
				}
			}
		}
		
		function displaySourceName(element) {
			var sourceType = element.value;
			var hideRow;
			var showRow;
			if (sourceType == "FEDERAL") {
				showRow = document.getElementById("fedSource");
				hideRow = document.getElementById("nonFedSource");
			}
			else {
				showRow = document.getElementById("nonFedSource");
				hideRow = document.getElementById("fedSource");
			}
			if (showRow.style.display == 'none')
				showRow.style.display = '';
			if (hideRow.style.display == '')
				 hideRow.style.display = 'none';
		}
	</script>
  </head>
  
  <body>
  <%@ include file="/includes/errors.jsp" %>
  
  	<% String title = "Edit Grant"; %>
  	
  	<logic:present name="newGrant">
  		<% title = "New Grant"; %>
  	</logic:present>
	 
	<yrcwww:contentbox title="<%=title%>" width="500">
	 	<center>
	 	<html:form action="saveGrant" method="POST">
	 		<table>
	 			<tr>
	 				<td style="padding:5px;" WIDTH="25%" VALIGN="top">Title:</td>
	 				<td style="padding:5px;" WIDTH="75%" VALIGN="top">
	 					<html:hidden property="grantID" />
	 					<html:text property="grantTitle" size="45"></html:text>
	 				</td>
	 			</tr>
	 			<tr>
	 				<td style="padding:5px;" WIDTH="25%" VALIGN="top">PI:</td>
	 				<td style="padding:5px;" WIDTH="75%" VALIGN="top">
	 					<html:select property="PI">
    						<html:option value="0">None</html:option>
							<html:options collection="researchers" property="ID" labelProperty="listing"/>
    					</html:select>
    				</td>
	 			</tr>
	 			<tr>
	 				<td style="padding:5px;" WIDTH="25%" VALIGN="top">Funding Source:</td>
	 				<td style="padding:5px;" WIDTH="75%" VALIGN="top">
	 				<logic:iterate name="sourceTypes" id="source">
	 					<nobr><html:radio property="fundingType" value="<%=((FundingSourceType)source).getType() %>" onclick="displaySourceName(this)" ><bean:write name="source" property="displayName" /></html:radio></nobr>
	 				</logic:iterate>
	 				</td>
	 			</tr>
	 			<tr id="fedSource" style="display:none;">
	 				<td style="padding:5px;" WIDTH="25%" VALIGN="top">Federal Funding Source:</td>
	 				<td style="padding:5px;" WIDTH="75%" VALIGN="top">
	 				<logic:iterate name="federalSources" id="fedSource">
	 					<nobr><html:radio property="fedFundingAgencyName" value="<%=((SourceName)fedSource).getName() %>"><bean:write name="fedSource" property="displayName" /></html:radio></nobr>
	 				</logic:iterate>
	 				</td>
	 			</tr>
	 			<tr id="nonFedSource" style="display:none;">
	 				<td style="padding:5px;" WIDTH="25%" VALIGN="top">Funding Source Name:</td>
	 				<td style="padding:5px;" WIDTH="75%" VALIGN="top"><html:text property="fundingAgencyName" size="45"></html:text></td>
	 			</tr>
	 			<tr>
	 				<td style="padding:5px;" WIDTH="25%" VALIGN="top">Grant Number:</td>
	 				<td style="padding:5px;" WIDTH="75%" VALIGN="top"><html:text property="grantNumber" size="25"></html:text></td>
	 			</tr>
	 			<tr>
	 				<td style="padding:5px;" WIDTH="25%" VALIGN="top">Annual Funds:</td>
	 				<td style="padding:5px;" WIDTH="75%" VALIGN="top"><html:text property="grantAmount" size="25"></html:text></td>
	 			</tr>
	 			
	 			<tr>
	 				<td colspan="2"" align="center">
	 					<html:submit>Save</html:submit>
	 					&nbsp;&nbsp;
	 					<input type="button" value="Cancel" onClick="window.close()">
	 				</td>
	 			</tr>
	 		</table>
	 	</html:form>
	 	</center>
	 </yrcwww:contentbox>   
<%@ include file="/includes/footer.jsp" %>
