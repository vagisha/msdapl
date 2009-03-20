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

<div class="mainContainer"> 


<yrcwww:notauthenticated>
<div id="header" align="center">
	<table BORDER="0" WIDTH="100%" CELLPADDING="0" CELLSPACING="0">
	<tr>
	<td width="33%">&nbsp;</td>
	<td width="34%" align="center"><img src="<yrcwww:link path='images/MSDAPL_logo_big.png'/>" /></td>
	<td width="33%" align="right" valign="bottom">
		<div align="right" style="padding:0 10 5 0;"><html:link forward="authenticate">Login</html:link>
	</div></td>
	</tr>
	</table>
</div>
</yrcwww:notauthenticated>
   
<yrcwww:authenticated>

<div id="header">

<table BORDER="0" WIDTH="100%" CELLPADDING="0" CELLSPACING="0">
 <tr>
  <td WIDTH="478" VALIGN="bottom" COLSPAN="2">
   <nobr>
    <img src="<yrcwww:link path='images/MSDAPL_logo_small.png'/>" /> &nbsp;&nbsp;&nbsp;
    
	<span class="menu_base main_menu_selected"><a href="http://www.13styles.com/css-menus/simple-menu/" title="Home" class="current">Home</a></span>
	<span class="menu_base main_menu"><a href="http://www.13styles.com/css-menus/simple-menu/" title="Account">Account</a></span>
	<span class="menu_base main_menu"><a href="http://www.13styles.com/css-menus/simple-menu/" title="Home">Admin</a></span>
	</nobr>
  </td>

  <td WIDTH="100%" ALIGN="RIGHT" style="padding-right: 10;">
    <jsp:useBean id="user" class="org.yeastrc.www.user.User" scope="session"/>
    <FONT STYLE="font-size:8pt;">Welcome <yrcwww:user attribute="firstname"/> <yrcwww:user attribute="lastname"/>!<BR></FONT>
    <div><html:link action="logout.do">Logout</html:link></div>
  </td>
 </tr>
</table>
</div>


<table BORDER="0" WIDTH="100%" CELLPADDING="0" CELLSPACING="0">
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

<div id="history"><yrcwww:history/></div>
</yrcwww:authenticated>

<br><br>