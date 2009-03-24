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
<div id="top_header" align="center">
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

<div id="top_header">

<div id="main_menu">
	<ul>
		<logic:equal name="dir" scope="request" value="internal">
			<li><a href="<yrcwww:link path='pages/internal/front.jsp'/>" title="Home" class="current"><span>Home</span></a></li>
		</logic:equal>
		<logic:notEqual name="dir" scope="request" value="internal">
		
			<logic:equal name="dir" scope="request" value="project">
				<li><a href="<yrcwww:link path='pages/internal/front.jsp'/>" title="Home" class="current"><span>Home</span></a></li>
			</logic:equal>
			
			<logic:notEqual name="dir" scope="request" value="project">
				<li><a href="<yrcwww:link path='pages/internal/front.jsp'/>" title="Home"><span>Home</span></a></li>
			</logic:notEqual>
		</logic:notEqual>
		
		<logic:equal name="dir" scope="request" value="account">
			<li><html:link  action="editInformation.do" styleClass="current"><span>Account</span></html:link></li>
		</logic:equal>
		<logic:notEqual name="dir" scope="request" value="account">
			<li><html:link  action="editInformation.do"><span>Account</span></html:link></li>
		</logic:notEqual>
		
		<logic:equal name="dir" scope="request" value="admin">
			<li><html:link action="" styleClass="current"><span>Admin</span></html:link></li>
		</logic:equal>
		<logic:notEqual name="dir" scope="request" value="admin">
			<li><html:link action="" ><span>Admin</span></html:link></li>
		</logic:notEqual>
	
	</ul>
	
	<div align="right" style="padding-right:20;color:white; font-size:8pt">
		<jsp:useBean id="user" class="org.yeastrc.www.user.User" scope="session"/>
    	Welcome <yrcwww:user attribute="firstname"/> <yrcwww:user attribute="lastname"/>!&nbsp;&nbsp;
    	<html:link action="logout.do">Logout</html:link>
	</div>
	
</div>

</div>

<div id="sub_header">
<div id="sub_menu">
	<ul>
   <logic:equal name="dir" scope="request" value="account">
    <li><html:link href="/yrc/editInformation.do"><span>My Information</span></html:link>
    <li><html:link href="/yrc/editPassword.do"><span>Password</span></html:link></li>
    <li><html:link href="/yrc/editUsername.do"><span>Username</span></html:link></li>
   </logic:equal>
   <logic:equal name="dir" scope="request" value="internal">
        <li><html:link action="newProject.do"><span>New Project</span></html:link></li>
        <yrcwww:member group="any">
   			<li><html:link action="uploadMacCossFormAction.do"><span>Upload Data</span></html:link></li>
   		</yrcwww:member>
   </logic:equal>
   <logic:equal name="dir" scope="request" value="project">
       <li><html:link action="newProject.do"><span>New Project</span></html:link></li>
        <yrcwww:member group="any">
   			<li><html:link action="uploadMacCossFormAction.do"><span>Upload Data</span></html:link></li>
   		</yrcwww:member>
   </logic:equal>
  </ul>
</div>
</div>

<div id="history"><yrcwww:history/></div>
</yrcwww:authenticated>

<br><br>