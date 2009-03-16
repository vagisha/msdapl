<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%
response.setHeader("Pragma", "No-cache");
response.setHeader("Cache-Control","no-cache");
response.setDateHeader("Expires", 0);
response.addHeader("Cache-control", "no-store"); // tell proxy not to cache
response.addHeader("Cache-control", "max-age=0"); // stale right away
%>

<html>
<head>
 <yrcwww:title />
 <link REL="stylesheet" TYPE="text/css" HREF="<yrcwww:link path='/css/global.css' />">
</head>

<body>


<table BORDER="0" WIDTH="100%" CELLPADDING="0" CELLSPACING="0">

 <tr>

  <td WIDTH="478" VALIGN="BOTTOM" COLSPAN="2">
   <nobr>
   <span style="font-size:14px; font-weight:bold; color:red;">repoman@maccosslab</span>&nbsp;&nbsp;&nbsp;
   <yrcwww:authenticated>
   	<a href="<yrcwww:link path='pages/internal/front.jsp' />">HOME</a> &nbsp
   	
   	<html:link action="editInformation.do">ACCOUNT</html:link>
   	
   	<yrcwww:member group="any">
   		<html:link forward="adminSearch">SEARCH</html:link>
   	</yrcwww:member>
   	
   	<yrcwww:member group="administrators">
   		<html:link action="manageGroups.do">GROUPS</html:link>
   	</yrcwww:member>
   	   
   	</yrcwww:authenticated>
   	
   	</nobr>
   	</td>

  <td WIDTH="100%" ALIGN="RIGHT">
   <yrcwww:authenticated>
    <jsp:useBean id="user" class="org.yeastrc.www.user.User" scope="session"/>
    <FONT STYLE="font-size:8pt;">Welcome <yrcwww:user attribute="firstname"/> <yrcwww:user attribute="lastname"/>!<BR></FONT>
    <div><html:link action="logout.do">Logout</html:link></div>
   </yrcwww:authenticated>
   <yrcwww:notauthenticated>
    Not logged in.&nbsp;&nbsp;
   </yrcwww:notauthenticated>
  </td>
 </tr>

 <tr>
  <td VALIGN="CENTER" WIDTH="236" COLSPAN="3"><hr></td>
 </tr>

 <tr BGCOLOR="#FFFFFF">
  <td BGCOLOR="#FFFFFF" COLSPAN="3" ALIGN="LEFT" VALIGN="top"><NOBR>&nbsp;&nbsp;&nbsp;
   <logic:equal name="dir" scope="request" value="account">
    <html:link href="/yrc/editInformation.do">information</html:link>
    <html:link href="/yrc/editPassword.do">password</html:link>
    <html:link href="/yrc/editUsername.do">username</html:link>
   </logic:equal>
   <logic:equal name="dir" scope="request" value="internal">
        <html:link action="newCollaboration.do">New Project</html:link>
        <yrcwww:member group="any">
   			<html:link action="uploadMacCossFormAction.do">Upload Data</html:link>
   		</yrcwww:member>
   </logic:equal>
   <logic:equal name="dir" scope="request" value="project">
        <html:link action="newCollaboration.do">New Project</html:link>
        <yrcwww:member group="any">
   			<html:link action="uploadMacCossFormAction.do">Upload Data</html:link>
   		</yrcwww:member>
   </logic:equal>
  </NOBR></td>


 </tr>

</table>

   <yrcwww:authenticated><div style="background:#F0F8FF"><yrcwww:history/></div></yrcwww:authenticated>

<br>
