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

<div id="dhtmltooltip"></div>

<script type="text/javascript">

/***********************************************
* Cool DHTML tooltip script- © Dynamic Drive DHTML code library (www.dynamicdrive.com)
* This notice MUST stay intact for legal use
* Visit Dynamic Drive at http://www.dynamicdrive.com/ for full source code
***********************************************/

var offsetxpoint=-60 //Customize x offset of tooltip
var offsetypoint=20 //Customize y offset of tooltip
var ie=document.all
var ns6=document.getElementById && !document.all
var enabletip=false
if (ie||ns6)
var tipobj=document.all? document.all["dhtmltooltip"] : document.getElementById? document.getElementById("dhtmltooltip") : ""

function ietruebody(){
return (document.compatMode && document.compatMode!="BackCompat")? document.documentElement : document.body
}

function ddrivetip(thetext, thecolor, thewidth){
if (ns6||ie){
if (typeof thewidth!="undefined") tipobj.style.width=thewidth+"px"
if (typeof thecolor!="undefined" && thecolor!="") tipobj.style.backgroundColor=thecolor
tipobj.innerHTML=thetext
enabletip=true
return false
}
}

function positiontip(e){
if (enabletip){
var curX=(ns6)?e.pageX : event.clientX+ietruebody().scrollLeft;
var curY=(ns6)?e.pageY : event.clientY+ietruebody().scrollTop;
//Find out how close the mouse is to the corner of the window
var rightedge=ie&&!window.opera? ietruebody().clientWidth-event.clientX-offsetxpoint : window.innerWidth-e.clientX-offsetxpoint-20
var bottomedge=ie&&!window.opera? ietruebody().clientHeight-event.clientY-offsetypoint : window.innerHeight-e.clientY-offsetypoint-20

var leftedge=(offsetxpoint<0)? offsetxpoint*(-1) : -1000

//if the horizontal distance isn't enough to accomodate the width of the context menu
if (rightedge<tipobj.offsetWidth)
//move the horizontal position of the menu to the left by it's width
tipobj.style.left=ie? ietruebody().scrollLeft+event.clientX-tipobj.offsetWidth+"px" : window.pageXOffset+e.clientX-tipobj.offsetWidth+"px"
else if (curX<leftedge)
tipobj.style.left="5px"
else
//position the horizontal position of the menu where the mouse is positioned
tipobj.style.left=curX+offsetxpoint+"px"

//same concept with the vertical position
if (bottomedge<tipobj.offsetHeight)
tipobj.style.top=ie? ietruebody().scrollTop+event.clientY-tipobj.offsetHeight-offsetypoint+"px" : window.pageYOffset+e.clientY-tipobj.offsetHeight-offsetypoint+"px"
else
tipobj.style.top=curY+offsetypoint+"px"
tipobj.style.visibility="visible"
}
}

function hideddrivetip(){
if (ns6||ie){
enabletip=false
tipobj.style.visibility="hidden"
tipobj.style.left="-1000px"
tipobj.style.backgroundColor=''
tipobj.style.width=''
}
}

document.onmousemove=positiontip

</script>


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
